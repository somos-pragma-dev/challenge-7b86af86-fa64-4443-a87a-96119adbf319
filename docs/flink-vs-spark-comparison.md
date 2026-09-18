# Análisis Comparativo: Apache Flink vs Spark Structured Streaming

## Resumen Ejecutivo

Este documento presenta un análisis comparativo detallado entre Apache Flink y Spark Structured Streaming para determinar la tecnología más adecuada como motor de procesamiento del pipeline de Feature Store en tiempo real. La evaluación considera los requisitos específicos del caso de uso: latencia ultra-baja, procesamiento con estado, ventanas temporales y integración con el ecosistema existente.

## Comparación Técnica Fundamental

### Modelo de Procesamiento

**Apache Flink** implementa un modelo de procesamiento nativo de streaming con tiempo de procesamiento real (processing time) y tiempo de evento (event time). Cada evento se procesa individualmente tan pronto como llega al sistema, manteniendo el ordenamiento basado en timestamps del evento. El estado se mantiene en memoria con checkpoints periódicos a almacenamiento durable, permitiendo exactamente-once semantics sin comprometer latencia.

**Spark Structured Streaming** adopta un modelo de micro-batching donde los eventos se acumulan en mini-lotes (micro-batches) de duración configurable (típicamente 100ms a 1s). Aunque ofrece un modelo de programación más familiar para desarrolladores batch, introduce latencia mínima igual al tamaño del micro-batch. A partir de Spark 3.5, el modo continuous processing ofrece latencia sub-segundo pero con limitaciones en operaciones con estado.

### Latencia y Throughput

Para el caso de uso de Feature Store donde la latencia entre un evento de usuario y la disponibilidad de la feature actualizada es crítica:

**Flink** ofrece latencia de procesamiento de evento único en el orden de milisegundos. El throughput escala linealmente con la cantidad de parallelism configurado, y puede manejar millones de eventos por segundo en clusters bien dimensionados. La arquitectura basada en streams continuos elimina el overhead de coordinación entre batches.

**Spark Structured Streaming** con micro-batching típico ofrece latencia mínima de 100-500ms dependiendo del tamaño del batch. El modo continuous processing puede lograr latencia de ~1ms pero limita las operaciones disponibles. El throughput es alto para workloads batch-oriented pero tiene más overhead para escenarios de streaming puro.

### Procesamiento con Estado y Ventanas

**Flink** proporciona APIs nativas y expresivas para procesamiento con estado: keyed state (por clave de entidad), operator state (compartido entre instancias), y ventanas de todos los tipos (tumbling, sliding, session, global). Las ventanas se disparan exactamente cuando el watermark indica que no llegan más eventos para ese window, permitiendo resultados correctos incluso con datos fuera de orden. El estado se checkpoints automáticamente y es recuperable ante fallos.

**Spark Structured Streaming** soporta stateful processing mediante operaciones como mapGroupsWithState, flatMapGroupsWithState, y window aggregations. Sin embargo, la gestión de estado es menos granular que Flink, y las ventanas con event time requieren configuración cuidadosa de watermarks. El estado se almacena en el checkpoint directory configurado, pero la recuperación puede ser más lenta.

## Análisis de Fit con los Requisitos del Pipeline

### Requisito: Ventanas Deslizantes de 5min/1h/24h

El pipeline requiere ventanas temporales para calcular features agregadas como promedio de compras en las últimas 5 minutos, 1 hora, y 24 horas.

**Flink**: Las ventanas deslizantes (sliding windows) son una construcción de primera clase. La API de window assigners permite definir windows de cualquier tamaño y slide interval. El trigger puede configurarse para emitir resultados intermedios o solo al final del window. La gestión de late data mediante allowed lateness y side outputs es elegante y bien documentada.

**Spark**: Las ventanas temporales se expresan mediante groupBy con la función window(). El comportamiento es correcto pero la semántica de micro-batch puede causar que eventos que llegan ligeramente tarde se incluyan o excluyan de maneras no intuitivas. La configuración de watermark es esencial para evitar memory leaks por estado acumulado.

### Requisito: Exactly-Once Semantics

El pipeline requiere exactly-once semantics para garantizar que cada evento de feature se procese exactamente una vez, sin duplicados ni pérdidas.

**Flink**: Implementa exactly-once nativamente mediante su mecanismo de checkpointing y分布式 snapshotting (Chandy-Lamport). El sink debe ser idempotente o usar transacciones para completar la garantía. La integración con Kafka source y Redis sink soporta este modo de manera robusta.

**Spark**: También ofrece exactly-once mediante checkpointing, pero la semántica depende del sink. Para Kafka sink, usa transacciones idempotentes. Para sinks no transaccionales, la garantía es at-least-once. La recuperación de estado es más lenta que Flink en escenarios de gran estado.

### Requisito: Integración con CDC (Debezium)

El pipeline consume eventos de Debezium desde PostgreSQL.

**Flink**: El Debezium format está soportado nativamente en Flink SQL y DataStream API. Los CDC events se deserializan correctamente incluyendo el schema completo y los cambios before/after. La integración con Flink CDC Connector permite consumir directamente de Debezium sin componente intermedio de Kafka, aunque el diseño actual usa Kafka como buffer.

**Spark**: Spark Structured Streaming puede consumir de Kafka y deserializar CDC events, pero requiere más código boilerplate para manejar el formato Debezium. No existe un connector nativo de Spark CDC equivalente al Flink CDC Connector.

### Requisito: Serving a Redis con TTLs

Las features se sirven desde Redis con diferentes TTLs por tipo.

**Flink**: El Redis connector de Flink soporta escritura atómica con TTL. La integración es madura y bien probada. El modo de escritura puede ser idempotente (overwrite) o redis transactions para operaciones más complejas.

**Spark**: El Redis connector existe pero está menos mantenido que el de Flink. Las opciones de escritura con TTL requieren configuración adicional y pueden tener menor rendimiento en escenarios de alta frecuencia.

## Trade-offs y Consideraciones Operacionales

### Curva de Aprendizaje

**Flink**: La curva de aprendizaje es más pronunciada. Conceptos como watermarks, timestamps, processing time vs event time, y gestión de estado requieren comprensión profunda. Sin embargo, una vez dominados, el modelo de programación es más intuitivo para streaming.

**Spark**: La ventaja de Spark es la familiaridad para desarrolladores que vienen del mundo batch. La misma API sirve para batch y streaming. La transición de batch a streaming es más suave, pero las sutilezas del streaming pueden causar comportamientos inesperados.

### Recursos y Cluster Management

**Flink**: Puede ejecutarse en YARN, Mesos, Kubernetes, o como standalone cluster. El modelo de recursos es más predecible: cada task manager tiene slots fijos, y el scheduler asigna trabajo a slots disponibles. La integración con Kubernetes es excelente mediante el Flink Kubernetes Operator.

**Spark**: Ejecuta en YARN, Mesos, Kubernetes, o standalone. El modelo de recursos es más flexible pero puede causar resource contention en clusters compartidos. La integración con Kubernetes es madura.

### Ecosystem y Comunidad

**Flink**: Comunidad enfocada en streaming real. Apache Flink es el estándar de facto para streaming en tiempo real en producción. La documentación de streaming es excelente. Menos recursos de aprendizaje que Spark.

**Spark**: Comunidad masiva y ecosistema extenso. Spark Streaming es una parte del todo Spark. Miles de tutoriales, cursos, y ejemplos disponibles. La documentación es comprehensiva pero a veces dispersa entre versiones.

## Recomendación para el Feature Store Pipeline

### Selección: Apache Flink

Para este caso de uso específico de Feature Store en tiempo real, **Flink es la elección recomendada** por las siguientes razones:

1. **Latencia nativa de streaming**: El pipeline requiere que las features estén disponibles en Redis lo antes posible después del evento. Flink procesa eventos individualmente sin esperar micro-batches, logrando latencia de milisegundos vs los 100-500ms de Spark.

2. **Ventanas temporales avanzadas**: Las ventanas de 5min/1h/24h con slide son una construcción de primera clase en Flink. La gestión de late data y watermarks es más robusta y mejor documentada que en Spark.

3. **Procesamiento con estado**: El Feature Store mantiene estado significativo (agregaciones, contadores, históricos). Flink maneja estado grande de manera más eficiente con su keyed state y rocksdb backend.

4. **CDC Integration**: El Flink CDC Connector proporciona una experiencia de consumo de Debezium más integrada y mantenida que las alternativas en Spark.

5. **Exactly-once guarantees**: La arquitectura de checkpointing de Flink proporciona exactly-once de manera más confiable para workloads de streaming puro.

### Cuándo Considerar Spark Structured Streaming

Spark sería preferible en escenarios donde:

- El equipo tiene experiencia sólida en Spark y no hay tiempo para curva de aprendizaje de Flink
- El pipeline mezcla procesamiento batch (entrenamiento de modelos) y streaming en el mismo cluster
- Los requisitos de latencia son más flexibles (segundos en lugar de milisegundos)
- Se requiere unificar el código de procesamiento batch y streaming bajo una misma API

### Roadmap de Adopción

La adopción de Flink requiere:

1. **Formación del equipo**: Invertir en capacitación sobre Flink DataStream API, watermarks, y gestión de estado
2. **Setup de cluster**: Configurar Flink cluster en Kubernetes con alta disponibilidad
3. **Migración gradual**: Comenzar con las features más críticas y validar comportamiento
4. **Operaciones**: Establecer procesos de monitoring, alerting, y recovery

## Conclusión

Apache Flink es la tecnología más adecuada para el pipeline de Feature Store en tiempo real descrito en los requisitos. Su modelo de procesamiento nativo de streaming, manejo superior de ventanas temporales y estado, y mejor integración con el ecosistema CDC posicionan a Flink como la elección que maximiza el rendimiento del pipeline mientras mantiene la robustez operacional necesaria para un sistema de producción.

La inversión adicional en curva de aprendizaje se justifica por la naturaleza crítica del caso de uso: un sistema de recomendación en tiempo real donde cada milisegundo de latencia impacta la experiencia del usuario y, en última instancia, las métricas de negocio.