# AGENTS.md

Instrucciones para el agente de IA que abra este repositorio (Claude Code, Cursor, Codex, Copilot, Gemini). Se cargan solas: no hay que pegar nada en ningun chat.

## Que es este repositorio

Es el codigo base de un reto de aprendizaje de Pragma: **Diseño y Evaluación de Pipeline de Feature Store en Tiempo Real**.

| | |
|---|---|
| Tema | pipeline de feature store en tiempo real para modelo de recomendación con A/B testing |
| Nivel | master-l1 |
| Chapter | Generico |
| Especialidad | Inferido del contexto |
| Stack | Java / Apache Flink 1.18 |
| Patron arquitectonico | microservicio reactivo con capas estándar (ingestion, processing, serving, monitoring) |
| Tiempo estimado | 20 horas |

## Tu tarea

Dejar este proyecto en estado **verificable**: que el comando de verificacion corra sin errores. Escribi los archivos en disco, en este repositorio. No generes ZIPs ni archivos adjuntos.

En orden:

1. Corre `el comando de build o arranque canonico del stack elegido` y mira que falla.
2. Completa lo que falte de la lista de abajo: manifiesto de dependencias, punto de entrada, capa de interfaz y las capas del patron declarado.
3. Arregla SOLO los errores que impiden compilar o arrancar.
4. Volve a correr `el comando de build o arranque canonico del stack elegido` hasta que pase.
5. Pará ahí.

## Regla dura: las fases son trabajo del humano

**PROHIBIDO implementar los entregables de las fases.** El valor del reto esta en que la persona los resuelva. Tu trabajo es que tenga un proyecto que arranca; el hueco pedagogico se queda como esta.

No resuelvas nada de esto:

- **Fase 1 — Ingesta y Computación de Features**: Descripción del diseño de la ingesta y computación de features, incluyendo la elección de Flink vs Spark Structured Streaming.
- **Fase 2 — Servir Features y A/B Testing**: Descripción del diseño del servicio de features y el router A/B, incluyendo el esquema de versionado de features.
- **Fase 3 — Medición de Drift y Rollback**: Descripción del diseño de la medición de drift y el rollback, incluyendo los posibles impactos en el modelo de recomendación.

Distincion operativa:

- **Arreglar** (si): import faltante, tipo que no existe, dependencia sin declarar, error de sintaxis, archivo referenciado que no existe.
- **No tocar** (no): logica de negocio incompleta, validaciones ausentes, secretos hardcodeados, APIs deprecadas que funcionan, concurrencia insegura, patrones mejorables. Eso es lo que la persona tiene que encontrar.

## Lo que falta y tenes que completar

### 1. Boilerplate del stack (2)

Sin esto el proyecto no compila ni arranca. **Es tu trabajo crearlo**, y no toca nada de lo pedagogico: es andamiaje del stack.

- [ ] **Punto de entrada del stack elegido** — Sin un punto de entrada reconocible, el runtime no tiene por donde arrancar la aplicacion.
- [ ] **Capa de interfaz (controller/handler)** — Sin una capa de interfaz explicita, no hay forma de invocar la logica de negocio desde afuera del proceso.

### 2. Referencias colgando (29)

Salieron de un analisis estatico del codigo que SI esta en el repo. Cada una rompe la compilacion:

- [ ] `src/main/java/com/pragma/featurestore/serving/RedisFeatureStore.java` — `FeatureEvent`
      FeatureEvent se usa en el cuerpo del archivo pero no esta importado. El proyecto lo declara en com.pragma.featurestore.dto.FeatureEvent (hay mas de un tipo con ese nombre en el proyecto).
- [ ] `src/main/java/com/pragma/featurestore/monitoring/DriftDetector.java` — `FeatureEvent`
      FeatureEvent se usa en el cuerpo del archivo pero no esta importado. El proyecto lo declara en com.pragma.featurestore.dto.FeatureEvent.
- [ ] `src/main/java/com/pragma/featurestore/FeatureStorePipeline.java` — `org.slf4j`
      El import org.slf4j.Logger pertenece a org.slf4j, pero ninguna dependencia declarada en el pom.xml cubre ese paquete. Falta agregar la dependencia o el import esta mal (libreria equivocada).
- [ ] `src/main/java/com/pragma/featurestore/ingestion/PostgresCDCSource.java` — `org.slf4j`
      El import org.slf4j.Logger pertenece a org.slf4j, pero ninguna dependencia declarada en el pom.xml cubre ese paquete. Falta agregar la dependencia o el import esta mal (libreria equivocada).
- [ ] `src/main/java/com/pragma/featurestore/ingestion/KafkaSink.java` — `org.slf4j`
      El import org.slf4j.Logger pertenece a org.slf4j, pero ninguna dependencia declarada en el pom.xml cubre ese paquete. Falta agregar la dependencia o el import esta mal (libreria equivocada).
- [ ] `src/main/java/com/pragma/featurestore/processing/FeatureCalculator.java` — `org.slf4j`
      El import org.slf4j.Logger pertenece a org.slf4j, pero ninguna dependencia declarada en el pom.xml cubre ese paquete. Falta agregar la dependencia o el import esta mal (libreria equivocada).
- [ ] `src/main/java/com/pragma/featurestore/serving/RedisFeatureStore.java` — `org.slf4j`
      El import org.slf4j.Logger pertenece a org.slf4j, pero ninguna dependencia declarada en el pom.xml cubre ese paquete. Falta agregar la dependencia o el import esta mal (libreria equivocada).
- [ ] `src/main/java/com/pragma/featurestore/serving/ABRouter.java` — `org.slf4j`
      El import org.slf4j.Logger pertenece a org.slf4j, pero ninguna dependencia declarada en el pom.xml cubre ese paquete. Falta agregar la dependencia o el import esta mal (libreria equivocada).
- [ ] `src/main/java/com/pragma/featurestore/processing/FeatureCalculator.java` — `AggregatedFeature.addEvent`
      Se invoca `addEvent` sobre `AggregatedFeature`, pero esa clase no declara ese metodo. Agregalo con su implementacion real, o usa uno de los que si declara.
- [ ] `src/main/java/com/pragma/featurestore/processing/FeatureCalculator.java` — `FeatureEvent.getValue`
      Se invoca `getValue` sobre `FeatureEvent`, pero esa clase no declara ese metodo. Agregalo con su implementacion real, o usa uno de los que si declara.
- [ ] `src/main/java/com/pragma/featurestore/processing/FeatureCalculator.java` — `AggregatedFeature.getEventsInWindow`
      Se invoca `getEventsInWindow` sobre `AggregatedFeature`, pero esa clase no declara ese metodo. Agregalo con su implementacion real, o usa uno de los que si declara.
- [ ] `src/main/java/com/pragma/featurestore/processing/FeatureCalculator.java` — `FeatureEvent.setValue`
      Se invoca `setValue` sobre `FeatureEvent`, pero esa clase no declara ese metodo. Agregalo con su implementacion real, o usa uno de los que si declara.
- [ ] `src/main/java/com/pragma/featurestore/processing/FeatureCalculator.java` — `FeatureEvent.setWindowType`
      Se invoca `setWindowType` sobre `FeatureEvent`, pero esa clase no declara ese metodo. Agregalo con su implementacion real, o usa uno de los que si declara.
- [ ] `src/main/java/com/pragma/featurestore/processing/FeatureCalculator.java` — `FeatureEvent.add`
      Se invoca `add` sobre `FeatureEvent`, pero esa clase no declara ese metodo. Agregalo con su implementacion real, o usa uno de los que si declara.
- [ ] `src/main/java/com/pragma/featurestore/serving/RedisFeatureStore.java` — `FeatureCache.isExpired`
      Se invoca `isExpired` sobre `FeatureCache`, pero esa clase no declara ese metodo. Agregalo con su implementacion real, o usa uno de los que si declara.
- [ ] `src/main/java/com/pragma/featurestore/serving/RedisFeatureStore.java` — `FeatureCache.getValue`
      Se invoca `getValue` sobre `FeatureCache`, pero esa clase no declara ese metodo. Agregalo con su implementacion real, o usa uno de los que si declara.
- [ ] `src/main/java/com/pragma/featurestore/serving/RedisFeatureStore.java` — `FeatureEvent.getValue`
      Se invoca `getValue` sobre `FeatureEvent`, pero esa clase no declara ese metodo. Agregalo con su implementacion real, o usa uno de los que si declara.
- [ ] `src/main/java/com/pragma/featurestore/serving/ABRouter.java` — `ABRequest.getUserId`
      Se invoca `getUserId` sobre `ABRequest`, pero esa clase no declara ese metodo. Agregalo con su implementacion real, o usa uno de los que si declara.
- [ ] `src/main/java/com/pragma/featurestore/serving/ABRouter.java` — `ABRequest.getExperimentId`
      Se invoca `getExperimentId` sobre `ABRequest`, pero esa clase no declara ese metodo. Agregalo con su implementacion real, o usa uno de los que si declara.
- [ ] `src/main/java/com/pragma/featurestore/serving/ABRouter.java` — `ABResponse.setUserId`
      Se invoca `setUserId` sobre `ABResponse`, pero esa clase no declara ese metodo. Agregalo con su implementacion real, o usa uno de los que si declara.
- [ ] `src/main/java/com/pragma/featurestore/serving/ABRouter.java` — `ABResponse.setExperimentId`
      Se invoca `setExperimentId` sobre `ABResponse`, pero esa clase no declara ese metodo. Agregalo con su implementacion real, o usa uno de los que si declara.
- [ ] `src/main/java/com/pragma/featurestore/serving/ABRouter.java` — `ABResponse.setVariant`
      Se invoca `setVariant` sobre `ABResponse`, pero esa clase no declara ese metodo. Agregalo con su implementacion real, o usa uno de los que si declara.
- [ ] `src/main/java/com/pragma/featurestore/serving/ABRouter.java` — `ABResponse.setModelEndpoint`
      Se invoca `setModelEndpoint` sobre `ABResponse`, pero esa clase no declara ese metodo. Agregalo con su implementacion real, o usa uno de los que si declara.
- [ ] `src/main/java/com/pragma/featurestore/serving/ABRouter.java` — `ABResponse.setTimestamp`
      Se invoca `setTimestamp` sobre `ABResponse`, pero esa clase no declara ese metodo. Agregalo con su implementacion real, o usa uno de los que si declara.
- [ ] `src/main/java/com/pragma/featurestore/serving/ABRouter.java` — `ABResponse.setError`
      Se invoca `setError` sobre `ABResponse`, pero esa clase no declara ese metodo. Agregalo con su implementacion real, o usa uno de los que si declara.
- [ ] `src/main/java/com/pragma/featurestore/monitoring/CTRRollbackStrategy.java` — `CTREvent.isClick`
      Se invoca `isClick` sobre `CTREvent`, pero esa clase no declara ese metodo. Agregalo con su implementacion real, o usa uno de los que si declara.
- [ ] `src/test/java/com/pragma/featurestore/monitoring/DriftDetectorTest.java` — `DriftResult.hasDrift`
      Se invoca `hasDrift` sobre `DriftResult`, pero esa clase no declara ese metodo. Agregalo con su implementacion real, o usa uno de los que si declara.
- [ ] `src/test/java/com/pragma/featurestore/monitoring/DriftDetectorTest.java` — `DriftResult.getDriftLevel`
      Se invoca `getDriftLevel` sobre `DriftResult`, pero esa clase no declara ese metodo. Agregalo con su implementacion real, o usa uno de los que si declara.
- [ ] `src/test/java/com/pragma/featurestore/monitoring/DriftDetectorTest.java` — `DriftResult.getDriftScore`
      Se invoca `getDriftScore` sobre `DriftResult`, pero esa clase no declara ese metodo. Agregalo con su implementacion real, o usa uno de los que si declara.

### Presentes (21)

- `pom.xml`
- `src/main/resources/application.properties`
- `src/main/java/com/pragma/featurestore/FeatureStorePipeline.java`
- `src/main/java/com/pragma/featurestore/ingestion/PostgresCDCSource.java`
- `src/main/java/com/pragma/featurestore/ingestion/KafkaSink.java`
- `src/main/java/com/pragma/featurestore/processing/FeatureCalculator.java`
- `src/main/java/com/pragma/featurestore/serving/RedisFeatureStore.java`
- `src/main/java/com/pragma/featurestore/serving/ABRouter.java`
- `src/main/java/com/pragma/featurestore/monitoring/DriftDetector.java`
- `src/main/java/com/pragma/featurestore/monitoring/CTRRollbackStrategy.java`
- `src/main/java/com/pragma/featurestore/config/FlinkConfig.java`
- `src/main/java/com/pragma/featurestore/exception/FeatureComputationException.java`
- `src/main/java/com/pragma/featurestore/validation/FeatureEventValidator.java`
- `src/test/java/com/pragma/featurestore/processing/FeatureCalculatorTest.java`
- `src/test/java/com/pragma/featurestore/serving/ABRouterTest.java`
- `src/test/java/com/pragma/featurestore/monitoring/DriftDetectorTest.java`
- `docs/feature-versioning-scheme.md`
- `docs/cache-invalidation-policy.md`
- `docs/flink-vs-spark-comparison.md`
- `src/main/java/com/pragma/featurestore/dto/FeatureEvent.java`
- `src/main/java/com/pragma/featurestore/monitoring/FeatureEvent.java`

### Capas del patron declarado

Cada una tiene que existir como directorio real con al menos un archivo. Codigo plano en la raiz no satisface el patron.

- `src/main/java/com/pragma/featurestore`
- `src/main/java/com/pragma/featurestore/ingestion`
- `src/main/java/com/pragma/featurestore/processing`
- `src/main/java/com/pragma/featurestore/serving`
- `src/main/java/com/pragma/featurestore/monitoring`
- `src/main/java/com/pragma/featurestore/config`
- `src/main/java/com/pragma/featurestore/dto`
- `src/main/java/com/pragma/featurestore/exception`
- `src/main/java/com/pragma/featurestore/validation`
- `src/test/java/com/pragma/featurestore`

## Verificacion

```bash
el comando de build o arranque canonico del stack elegido
```

Ese comando pasando es la definicion de "terminado" para vos.

## Convenciones que tenes que respetar

- Un solo ecosistema: no declares librerias de otro lenguaje ni mezcles gestores de paquetes.
- Toda libreria que uses tiene que estar declarada en el manifiesto de dependencias.
- Todo import declarado tiene que usarse; todo tipo usado tiene que existir o venir de una dependencia declarada.
- El patron es **microservicio reactivo con capas estándar (ingestion, processing, serving, monitoring)**: los contratos (interfaces, puertos) los define la capa interna y los implementa la externa, nunca al revés.
- Los archivos que crees llevan implementacion real, no stubs: sin `TODO`, sin cuerpos vacios, sin `// getters y setters`.

## Contexto del candidato

Sirve para calibrar el nivel del codigo, no para resolver las fases.

- Brecha que el reto ataca: Arquitectura master-l1 de un feature store online + offline que alimenta un modelo de recomendación de e-commerce. Ingesta desde CDC de PostgreSQL a Kafka, features computadas en Flink con ventanas deslizantes de 5min/1h/24h, sirve features vía Redis con TTLs por tipo. El modelo se despliega con un router A/B que enruta 10% del tráfico a un challenger. El desarrollador debe justificar la elección de Flink vs Spark Structured Streaming, el esquema de versionado de features (para no romper el modelo en prod), la política de invalidación de cache, y cómo mide drift entre training set y features online. También debe diseñar el rollback si el challenger degrada CTR.

---

*Generado por Challenge Generator — Pragma. `README.md` tiene el enunciado completo del reto para la persona. `PROMPT_MEJORA.md` es la variante para pegar en un chat, si se prefiere ese flujo.*
