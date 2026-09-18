# Política de Invalidación de Cache

## Estrategias de Invalidación por Tipo de Feature

El pipeline de Feature Store utiliza múltiples estrategias de invalidación adaptadas a las características de cada tipo de feature. La elección de la estrategia correcta impacta directamente la consistencia de datos, la latencia de servicio y la carga en el backend de origen.

## Clasificación de Features por Características

### Features Estáticas (User Demographics, Product Attributes)

Características que cambian raramente y representan información base de entidades. Ejemplos: edad del usuario, categoría de producto, ubicación de tienda.

**Estrategia de invalidación**: TTL largo con invalidación basada en eventos.

- **TTL**: 24 horas (86400 segundos)
- **Invalidación por evento**: Solo cuando el CDC detecta cambio en la tabla source
- **Consideraciones**: La latencia de actualización no es crítica; priorizamos reducir carga en PostgreSQL

### Features Semiestáticas (User Preferences, Inventory Levels)

Características que cambian con frecuencia moderada y representan estado actual. Ejemplos: preferencias de usuario, nivel de inventario, precio actual.

**Estrategia de invalidación**: TTL medio con actualización proactiva.

- **TTL**: 1 hora (3600 segundos)
- **Invalidación por evento**: Cambio detectado en la tabla source
- **Consideración de fallos**: Si el evento de cambio falla, el TTL garantiza consistencia eventual

### Features Dinámicas (User Behavior, Real-time Aggregations)

Características que cambian constantemente y representan comportamiento reciente. Ejemplos: clicks en últimas 24h, promedio de gasto semanal, tendencia de compras.

**Estrategia de invalidación**: TTL corto con recomputación continua.

- **TTL**: 5 minutos (300 segundos)
- **Recomputación**: Ventanas deslizantes en Flink recalculan continuamente
- **Fallback**: Si Redis no responde, se recalcula on-demand desde el estado de Flink

## Implementación de TTLs en Redis

### Configuración por Namespace

Cada namespace de feature tiene su propia configuración de TTL:

```
user_features:{entity_id}     -> TTL: 1h
product_features:{entity_id}  -> TTL: 24h
behavior_features:{entity_id} -> TTL: 5min
```

### Set con Expiración

El código de escritura en Redis utiliza el comando SET con EX para establecer TTL atómico:

```java
redisTemplate.opsForValue().set(
    key,
    serializedFeature,
    ttlSeconds,
    TimeUnit.SECONDS
);
```

Esta operación es atómica: la feature se escribe y su TTL se establece en una sola operación, evitando race conditions donde un lector podría acceder a datos stale entre el set y la expiración.

## Invalidación Basada en Eventos de Cambio

### Flujo de Invalidación

Cuando Debezium detecta un cambio en PostgreSQL, el pipeline ejecuta:

1. **CDC Event Received**: El connector captura el change event con before/after values
2. **Feature Calculator Process**: Se recalculan las features afectadas por el cambio
3. **Invalidation Signal**: Se envía señal de invalidación al serving layer
4. **Redis Update**: Se escribe la nueva versión con TTLreseteado

### Manejo de Eventos de Invalidación

El sistema utiliza un topic de Kafka dedicado para señales de invalidación:

```
{
  "featureNamespace": "user_features",
  "entityId": "user123",
  "version": "v1.2.0",
  "timestamp": 1699900000000,
  "invalidationType": "UPDATE"
}
```

El consumidor de este topic ejecuta la invalidación correspondiente, asegurándose de que la nueva versión esté disponible antes de marcar la antigua como inválida.

### Consistencia Eventual

El modelo de consistencia es eventual: el tiempo entre el cambio en la fuente y la actualización en Redis incluye:

- Latencia de Debezium (típicamente < 1 segundo)
- Tiempo de procesamiento en Flink (ventanas de 5min para features agregadas)
- Latencia de escritura en Redis (< 10ms)

Para la mayoría de casos de uso de recomendación, esta consistencia eventual es aceptable. Para escenarios que requieren consistencia fuerte, se implementa un patrón de cache-aside con invalidación explícita.

## Políticas de Fallo

### Fallo en Escritura a Redis

Cuando la escritura en Redis falla:

1. **Retry automático**: Se reintenta hasta 3 veces con backoff exponencial
2. **Cola de dead letter**: Los eventos fallidos se encolan para procesamiento posterior
3. **Alerta de monitoring**: Se dispara alerta si la tasa de fallo supera el 1%
4. **Fallback a cálculo on-demand**: Si Redis no responde, el serving recalcula desde estado de Flink

### Fallo en Señal de Invalidación

Cuando la señal de invalidación no se procesa:

1. **TTL como redentor**: La feature expira naturalmente según su TTL
2. **Reintento con idempotencia**: Se procesa el evento de cambio en la próxima ventana
3. **Verificación de consistencia**: Job nocturno que compara valores en PostgreSQL con Redis

### Fallo en Cálculo de Feature

Cuando el cálculo de una feature falla:

1. **Valor por defecto**: Se sirve un valor por defecto predefinido (configurable por feature)
2. **Logging detallado**: Se registra el error con traceId para debugging
3. **Métrica de calidad**: Se incrementa contador de "features unavailable"
4. **Alerta crítica**: Si más del 5% de features fallan, se alerta al equipo

## Estrategias de Invalidación Avanzadas

### Invalidation por Dependencia

Cuando una feature depende de otras, el cambio en una dependencia puede invalidar las dependientes:

```
feature: user_total_spend_30d
dependencias: [user_transactions, product_prices]
```

Si `product_prices` cambia, el sistema detecta la dependencia y programa invalidación de `user_total_spend_30d` para el siguiente ciclo de recomputación.

### Invalidation Geográfica

Para features que varían por ubicación:

- Invalidación selectiva por región cuando cambia el catálogo local
- TTL más corto para regiones con mayor volatilidad de inventario
- Sincronización de cambios de precio en tiempo real (< 1 minuto)

### Warm-up Post-Deploy

Después de un deploy que cambia lógica de cálculo:

1. Se invalidan todas las features del namespace afectado
2. El job de recomputación procesa el backlog de eventos pendientes
3. Se verifica que el cache alcance > 95% de hit rate antes de considerar el deploy exitoso

## Métricas de Monitorización

### Key Metrics

| Métrica | Umbral de Alerta | Descripción |
|---------|------------------|-------------|
| cache_hit_rate | < 80% | Porcentaje de requests que encuentran la feature en cache |
| invalidation_lag | > 5min | Tiempo entre cambio en fuente y actualización en cache |
| ttl_expirations | > 1000/min | Rate de expiraciones naturales de TTL |
| invalidation_failures | > 1% | Tasa de fallos en señales de invalidación |