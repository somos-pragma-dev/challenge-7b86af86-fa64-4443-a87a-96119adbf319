# Esquema de Versionado de Features

## Visión General

El versionado de features es fundamental para mantener la trazabilidad entre el entrenamiento del modelo y las features que se sirven en producción. Este documento describe el esquema de versionado semántico adoptado en el pipeline de Feature Store y las políticas de compatibilidad que rigen la evolución de las features.

## Esquema MAJOR.MINOR.PATCH para Features

### Componentes del Versionado

El versionado de cada feature sigue el formato semántico estándar de tres segmentos:

- **MAJOR (X.0.0)**: Cambios incompatibles en la definición de la feature. Cuando se modifica la lógica de cálculo de manera que los valores históricos no son comparables con los nuevos, se incrementa el major. Ejemplos: cambio en la ventana de agregación de 5min a 10min, modificación del algoritmo de normalización, eliminación de un campo source.

- **MINOR (1.Y.0)**: Nueva funcionalidad compatible hacia atrás. Cuando se añade una nueva feature derivada o se mejora el cálculo sin alterar los valores existentes, se incrementa el minor. Ejemplos: añadir una nueva agregación (percentil 95), extender la ventana de 1h a 2h manteniendo la lógica existente.

- **PATCH (1.1.Z)**: Correcciones de bugs compatibles. Cuando se corrige un error en el cálculo que afectaba la calidad de los datos sin cambiar la semántica, se incrementa el patch. Ejemplos: corrección de un edge case en el cálculo de promedio, fix en el manejo de valores nulos.

### Representación en Redis Keys

Las keys de Redis incorporan el versionado para permitir coexistencia de versiones y rollback controlado:

```
user_features:{version}:{feature_name}:{entity_id}
product_features:{version}:{feature_name}:{entity_id}
```

El placeholder `{version}` sigue el formato `v{major}.{minor}.{patch}`. Por ejemplo: `user_features:v2.1.0:avg_order_value:user123`.

Esta estructura permite que el modelo acceda a una versión específica de features mientras se migra gradualmente a una nueva versión. El router A/B puede dirigir tráfico a diferentes versiones para validar cambios antes de un rollout completo.

## Políticas de Compatibilidad

### Compatibilidad hacia atrás (Backward Compatibility)

Una feature es backward compatible cuando los modelos entrenados con versiones anteriores pueden servir predictions usando features de versiones más nuevas sin reentrenamiento. El pipeline garantiza backward compatibility cuando:

1. Se añaden nuevos campos sin modificar los existentes
2. Se corrigen errores de cálculo que producían valores incorrectos
3. Se extienden ventanas temporales manteniendo la misma lógica de agregación

### Compatibilidad hacia adelante (Forward Compatibility)

Una feature es forward compatible cuando features de versiones nuevas pueden servir a modelos entrenados con versiones anteriores. Esta política es más restrictiva y requiere:

1. Valores por defecto para cualquier campo nuevo
2. Preservación del tipo de dato y rango de valores
3. No eliminación de campos que el modelo anterior esperaba

### Matriz de Compatibilidad

| Cambio de Versión | Tipo de Compatibilidad | Acción Requerida |
|-------------------|------------------------|------------------|
| PATCH increment | Ambas兼容 | Ninguna, deployment automático |
| MINOR increment | Backward only | Validar que modelos existentes funcionan |
| MAJOR increment | Ninguna | Reentrenamiento obligatorio del modelo |

## Gestión de Versiones en el Pipeline

### Registro de Versiones

Cada feature tiene un registro en el Schema Registry que incluye:

- Identificador único de feature
- Versión actual (MAJOR.MINOR.PATCH)
- Historial de versiones anteriores con timestamps
- Dependencias con otras features
- Metadata del propietario y fecha de deprecación

### Proceso de Release de Nueva Versión

El flujo para publicar una nueva versión de feature sigue estos pasos:

1. **Desarrollo**: Se implementa la nueva versión en un namespace separado
2. **Validación**: Se comparan estadísticas de la nueva versión con la anterior
3. **Shadow Mode**: Se sirve la nueva versión en paralelo sin usarla para predictions
4. **Canary**: Se redirige un pequeño porcentaje de tráfico a la nueva versión
5. **Promoción**: Si los métricas son satisfactorias, se promote a versión principal
6. **Deprecación**: La versión anterior se marca como deprecated con ventana de 30 días

### Rollback

El sistema mantiene las últimas tres versiones de cada feature disponibles en Redis. El rollback se ejecuta cambiando la referencia en el modelo a la versión anterior sin necesidad de redeploy. El proceso es:

1. Detectar degradación mediante métricas de drift o CTR
2. Identificar la versión anterior estable
3. Actualizar el mapping de versión en el router A/B
4. Confirmar recuperación de métricas
5. Investigar causa raíz del fallo

## Consideraciones para Entrenamiento

### Versioning de Training Sets

Cada training set se asocia explícitamente a las versiones de features usadas en su creación. Esto permite:

- Reentrenar exactamente con las mismas features que se usaron originalmente
- Comparar rendimiento entre diferentes versiones de features
- Auditar la trazabilidad completa del ciclo de vida del modelo

### Feature Store Metadata

El Feature Store mantiene metadata adicional por versión:

- Timestamp de creación y última actualización
- Usuario o sistema que solicitó el cambio
-理由 del cambio (bug fix, mejora, cambio de negocio)
- Correlación con cambios en el modelo
- Estadísticas descriptivas (media, desviación estándar, percentiles)