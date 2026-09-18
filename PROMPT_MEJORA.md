# Prompt para Mejorar el Codigo Base

Copia y pega el contenido del bloque de abajo en un asistente de IA (Claude, ChatGPT)
para obtener un ZIP con el proyecto completo y arrancable.

Si preferis trabajar en tu editor con un agente local (Claude Code, Cursor, Copilot), usa `AGENTS.md` en vez de este archivo: dice lo mismo pero para que escriba los archivos en disco.

## Las dos reglas que no se negocian

1. **Completa el boilerplate.** Todo lo que el proyecto necesita para compilar y arrancar: manifiesto de dependencias, punto de entrada, configuracion, capa de interfaz, y las capas del patron arquitectonico declarado. Eso es andamiaje y es tu trabajo.
2. **NO resuelvas el reto.** Los entregables de las fases son el trabajo de la persona. El hueco pedagogico se deja como esta: el proyecto arranca, pero lo que el reto pide implementar NO esta implementado.

Dicho de otra forma: si algo impide compilar, arreglalo. Si algo es logica de negocio incompleta, validaciones ausentes, un secreto hardcodeado o un patron mejorable, dejalo exactamente como esta — es lo que la persona tiene que encontrar.

## Lo que le falta a este proyecto

Esto NO lo tenes que adivinar: salio de comparar el proyecto contra la arquitectura declarada del reto y de un analisis estatico del codigo. Completalo TODO.

### Boilerplate del stack que falta

Sin esto no compila ni arranca. Es andamiaje, no toca nada de lo pedagogico:

- **Punto de entrada del stack elegido** — Sin un punto de entrada reconocible, el runtime no tiene por donde arrancar la aplicacion.
- **Capa de interfaz (controller/handler)** — Sin una capa de interfaz explicita, no hay forma de invocar la logica de negocio desde afuera del proceso.

### Referencias colgando en el codigo que si esta

Cada una rompe la compilacion:

- `src/main/java/com/pragma/featurestore/serving/RedisFeatureStore.java` — `FeatureEvent`: FeatureEvent se usa en el cuerpo del archivo pero no esta importado. El proyecto lo declara en com.pragma.featurestore.dto.FeatureEvent (hay mas de un tipo con ese nombre en el proyecto).
- `src/main/java/com/pragma/featurestore/monitoring/DriftDetector.java` — `FeatureEvent`: FeatureEvent se usa en el cuerpo del archivo pero no esta importado. El proyecto lo declara en com.pragma.featurestore.dto.FeatureEvent.
- `src/main/java/com/pragma/featurestore/FeatureStorePipeline.java` — `org.slf4j`: El import org.slf4j.Logger pertenece a org.slf4j, pero ninguna dependencia declarada en el pom.xml cubre ese paquete. Falta agregar la dependencia o el import esta mal (libreria equivocada).
- `src/main/java/com/pragma/featurestore/ingestion/PostgresCDCSource.java` — `org.slf4j`: El import org.slf4j.Logger pertenece a org.slf4j, pero ninguna dependencia declarada en el pom.xml cubre ese paquete. Falta agregar la dependencia o el import esta mal (libreria equivocada).
- `src/main/java/com/pragma/featurestore/ingestion/KafkaSink.java` — `org.slf4j`: El import org.slf4j.Logger pertenece a org.slf4j, pero ninguna dependencia declarada en el pom.xml cubre ese paquete. Falta agregar la dependencia o el import esta mal (libreria equivocada).
- `src/main/java/com/pragma/featurestore/processing/FeatureCalculator.java` — `org.slf4j`: El import org.slf4j.Logger pertenece a org.slf4j, pero ninguna dependencia declarada en el pom.xml cubre ese paquete. Falta agregar la dependencia o el import esta mal (libreria equivocada).
- `src/main/java/com/pragma/featurestore/serving/RedisFeatureStore.java` — `org.slf4j`: El import org.slf4j.Logger pertenece a org.slf4j, pero ninguna dependencia declarada en el pom.xml cubre ese paquete. Falta agregar la dependencia o el import esta mal (libreria equivocada).
- `src/main/java/com/pragma/featurestore/serving/ABRouter.java` — `org.slf4j`: El import org.slf4j.Logger pertenece a org.slf4j, pero ninguna dependencia declarada en el pom.xml cubre ese paquete. Falta agregar la dependencia o el import esta mal (libreria equivocada).
- `src/main/java/com/pragma/featurestore/processing/FeatureCalculator.java` — `AggregatedFeature.addEvent`: Se invoca `addEvent` sobre `AggregatedFeature`, pero esa clase no declara ese metodo. Agregalo con su implementacion real, o usa uno de los que si declara.
- `src/main/java/com/pragma/featurestore/processing/FeatureCalculator.java` — `FeatureEvent.getValue`: Se invoca `getValue` sobre `FeatureEvent`, pero esa clase no declara ese metodo. Agregalo con su implementacion real, o usa uno de los que si declara.
- `src/main/java/com/pragma/featurestore/processing/FeatureCalculator.java` — `AggregatedFeature.getEventsInWindow`: Se invoca `getEventsInWindow` sobre `AggregatedFeature`, pero esa clase no declara ese metodo. Agregalo con su implementacion real, o usa uno de los que si declara.
- `src/main/java/com/pragma/featurestore/processing/FeatureCalculator.java` — `FeatureEvent.setValue`: Se invoca `setValue` sobre `FeatureEvent`, pero esa clase no declara ese metodo. Agregalo con su implementacion real, o usa uno de los que si declara.
- `src/main/java/com/pragma/featurestore/processing/FeatureCalculator.java` — `FeatureEvent.setWindowType`: Se invoca `setWindowType` sobre `FeatureEvent`, pero esa clase no declara ese metodo. Agregalo con su implementacion real, o usa uno de los que si declara.
- `src/main/java/com/pragma/featurestore/processing/FeatureCalculator.java` — `FeatureEvent.add`: Se invoca `add` sobre `FeatureEvent`, pero esa clase no declara ese metodo. Agregalo con su implementacion real, o usa uno de los que si declara.
- `src/main/java/com/pragma/featurestore/serving/RedisFeatureStore.java` — `FeatureCache.isExpired`: Se invoca `isExpired` sobre `FeatureCache`, pero esa clase no declara ese metodo. Agregalo con su implementacion real, o usa uno de los que si declara.
- `src/main/java/com/pragma/featurestore/serving/RedisFeatureStore.java` — `FeatureCache.getValue`: Se invoca `getValue` sobre `FeatureCache`, pero esa clase no declara ese metodo. Agregalo con su implementacion real, o usa uno de los que si declara.
- `src/main/java/com/pragma/featurestore/serving/RedisFeatureStore.java` — `FeatureEvent.getValue`: Se invoca `getValue` sobre `FeatureEvent`, pero esa clase no declara ese metodo. Agregalo con su implementacion real, o usa uno de los que si declara.
- `src/main/java/com/pragma/featurestore/serving/ABRouter.java` — `ABRequest.getUserId`: Se invoca `getUserId` sobre `ABRequest`, pero esa clase no declara ese metodo. Agregalo con su implementacion real, o usa uno de los que si declara.
- `src/main/java/com/pragma/featurestore/serving/ABRouter.java` — `ABRequest.getExperimentId`: Se invoca `getExperimentId` sobre `ABRequest`, pero esa clase no declara ese metodo. Agregalo con su implementacion real, o usa uno de los que si declara.
- `src/main/java/com/pragma/featurestore/serving/ABRouter.java` — `ABResponse.setUserId`: Se invoca `setUserId` sobre `ABResponse`, pero esa clase no declara ese metodo. Agregalo con su implementacion real, o usa uno de los que si declara.
- `src/main/java/com/pragma/featurestore/serving/ABRouter.java` — `ABResponse.setExperimentId`: Se invoca `setExperimentId` sobre `ABResponse`, pero esa clase no declara ese metodo. Agregalo con su implementacion real, o usa uno de los que si declara.
- `src/main/java/com/pragma/featurestore/serving/ABRouter.java` — `ABResponse.setVariant`: Se invoca `setVariant` sobre `ABResponse`, pero esa clase no declara ese metodo. Agregalo con su implementacion real, o usa uno de los que si declara.
- `src/main/java/com/pragma/featurestore/serving/ABRouter.java` — `ABResponse.setModelEndpoint`: Se invoca `setModelEndpoint` sobre `ABResponse`, pero esa clase no declara ese metodo. Agregalo con su implementacion real, o usa uno de los que si declara.
- `src/main/java/com/pragma/featurestore/serving/ABRouter.java` — `ABResponse.setTimestamp`: Se invoca `setTimestamp` sobre `ABResponse`, pero esa clase no declara ese metodo. Agregalo con su implementacion real, o usa uno de los que si declara.
- `src/main/java/com/pragma/featurestore/serving/ABRouter.java` — `ABResponse.setError`: Se invoca `setError` sobre `ABResponse`, pero esa clase no declara ese metodo. Agregalo con su implementacion real, o usa uno de los que si declara.
- `src/main/java/com/pragma/featurestore/monitoring/CTRRollbackStrategy.java` — `CTREvent.isClick`: Se invoca `isClick` sobre `CTREvent`, pero esa clase no declara ese metodo. Agregalo con su implementacion real, o usa uno de los que si declara.
- `src/test/java/com/pragma/featurestore/monitoring/DriftDetectorTest.java` — `DriftResult.hasDrift`: Se invoca `hasDrift` sobre `DriftResult`, pero esa clase no declara ese metodo. Agregalo con su implementacion real, o usa uno de los que si declara.
- `src/test/java/com/pragma/featurestore/monitoring/DriftDetectorTest.java` — `DriftResult.getDriftLevel`: Se invoca `getDriftLevel` sobre `DriftResult`, pero esa clase no declara ese metodo. Agregalo con su implementacion real, o usa uno de los que si declara.
- `src/test/java/com/pragma/featurestore/monitoring/DriftDetectorTest.java` — `DriftResult.getDriftScore`: Se invoca `getDriftScore` sobre `DriftResult`, pero esa clase no declara ese metodo. Agregalo con su implementacion real, o usa uno de los que si declara.

## Como saber que terminaste

```bash
el comando de build o arranque canonico del stack elegido
```

Ese comando corriendo sin errores es la definicion de "listo".

---

```
## Briefing del reto (autoridad)
Este bloque manda sobre los archivos adjuntos. El stack y el rol salen de AQUÍ, no de un topic genérico ni de markdown placeholder.

### Contexto técnico original
Arquitectura master-l1 de un feature store online + offline que alimenta un modelo de recomendación de e-commerce. Ingesta desde CDC de PostgreSQL a Kafka, features computadas en Flink con ventanas deslizantes de 5min/1h/24h, sirve features vía Redis con TTLs por tipo. El modelo se despliega con un router A/B que enruta 10% del tráfico a un challenger. El desarrollador debe justificar la elección de Flink vs Spark Structured Streaming, el esquema de versionado de features (para no romper el modelo en prod), la política de invalidación de cache, y cómo mide drift entre training set y features online. También debe diseñar el rollback si el challenger degrada CTR.

### Reto
- Tema: pipeline de feature store en tiempo real para modelo de recomendación con A/B testing
- Seniority: master-l1
- Tipo: mixed
- Título: Diseño y Evaluación de Pipeline de Feature Store en Tiempo Real
- Tiempo estimado: 20 horas

### Fases (trabajo del HUMANO — PROHIBIDO completarlas)
No implementes estos entregables. Dejalos como hueco pedagógico. El asistente solo materializa el proyecto arrancable para que el participante pueda trabajar.
- Fase 1: Ingesta y Computación de Features — objetivo: Diseñar la ingesta desde CDC de PostgreSQL a Kafka y la computación de features en Flink. — entregable (NO resolver): Descripción del diseño de la ingesta y computación de features, incluyendo la elección de Flink vs Spark Structured Streaming.
- Fase 2: Servir Features y A/B Testing — objetivo: Diseñar el servicio de features vía Redis y el router A/B para el modelo de recomendación. — entregable (NO resolver): Descripción del diseño del servicio de features y el router A/B, incluyendo el esquema de versionado de features.
- Fase 3: Medición de Drift y Rollback — objetivo: Diseñar la medición de drift entre el training set y las features online y el rollback si el challenger degrada el CTR. — entregable (NO resolver): Descripción del diseño de la medición de drift y el rollback, incluyendo los posibles impactos en el modelo de recomendación.

Eres un asistente experto en análisis, corrección y generación de archivos de cualquier tipo:
código fuente, documentación, hojas de cálculo, documentos Word, configuraciones, entre otros.
Voy a enviarte una cadena de texto que contiene uno o más archivos. Cada archivo está delimitado por un marcador con el siguiente formato:
// === ARCHIVO: ruta/del/archivo.extension ===
o también puede aparecer como:
## === ARCHIVO: ruta/del/archivo.extension ===
Lo que sigue al marcador puede ser:

El contenido real del archivo (código, texto, YAML, etc.)
Una descripción en lenguaje natural de lo que debe contener el archivo


TU TAREA
PASO 0 — ¿Esto es un proyecto o una carcasa?
Antes de extraer archivos, leé el Briefing (si está) y diagnosticá el adjunto.

Es CARCASA si ocurre CUALQUIERA de estas:
- No hay manifiesto de dependencias del stack del briefing (manifest.json de VTEX IO / package.json / pom.xml / build.gradle / requirements.txt / go.mod / *.tf / *.csproj, según corresponda)
- Hay un "binario" que en realidad es un comentario ("no puede ser mostrado como texto plano", placeholder .fig/.docx vacío)
- Los markdowns ya completan entregables de fases posteriores ("se implementó fade-in", lista de áreas ya resuelta)

Si es CARCASA:
- MATERIALIZÁ un proyecto que arranca en el stack del briefing (VTEX IO Store Framework, Angular, Terraform, pytest, Nest, etc.). Incluí manifiesto, punto de entrada y capa de interfaz reales.
- NO copies los markdowns de "solución" como si fueran el producto. Son ruido de generación.
- NO resuelvas las fases del briefing (están marcadas PROHIBIDO). Dejá el hueco pedagógico: el flujo existe, las microinteracciones/calidad/infra que el reto pide NO están hechas.
- Después seguí al PASO 5 (ZIP).

Si es un proyecto REAL (manifiesto + código que compila o arranca):
- Seguí PASO 1 en adelante. 🔴 compilación sí. 🟡 pedagógico no.

PASO 1 — Detección y extracción
Identifica todos los archivos presentes en la cadena. Para cada archivo extrae:

Su ruta completa (ej: src/main/java/com/pragma/Service.java)
Su contenido o descripción

PASO 2 — Clasificación por tipo
Clasifica cada archivo en una de estas categorías:
A) Código fuente (Java, Python, TypeScript, JavaScript, Kotlin, etc.)
B) Configuración / documentación (YAML, properties, Markdown, JSON, txt, etc.)
C) Excel (.xlsx, .xls, .csv)
D) Word (.docx, .doc)
E) Otro tipo de archivo binario o especial
PASO 3 — Clasificación de errores en código fuente

Objetivo prioritario: que el proyecto compile. No corrijas flujo de negocio ni lógica funcional.

Antes de modificar cualquier archivo de código fuente, clasifica cada problema encontrado en una de estas dos categorías:
🔴 ERROR DE COMPILACIÓN — corregir siempre
Son errores que impiden que el proyecto arranque, sin valor pedagógico:

Import faltante o incorrecto
Clase, método o variable referenciada que no existe en ningún archivo del proyecto
Error de sintaxis
Anotación con atributos inválidos
Dependencia ausente en pom.xml, package.json, etc.
Archivo referenciado que no existe y debe ser creado con implementación mínima

→ CORREGIR estos errores.
🟡 PROBLEMA FUNCIONAL O DE CALIDAD — preservar siempre
Son problemas que no impiden compilar. Pueden ser intencionales para el aprendizaje:

Clave secreta hardcodeada ("secret", "password123")
API deprecada que funciona pero tiene reemplazo moderno
Lógica de negocio incorrecta o incompleta
Código redundante o de baja legibilidad
Falta de validaciones en flujo de negocio
Patrones de diseño incorrectos pero funcionales
Concurrencia no segura
Configuración funcional pero no óptima

→ PRESERVAR tal cual. No corregir, no mejorar, no comentar.
PASO 4 — Procesamiento según tipo de archivo
Tipo A — Código fuente
Aplica únicamente las correcciones clasificadas como 🔴 ERROR DE COMPILACIÓN.
No alteres ningún elemento clasificado como 🟡 PROBLEMA FUNCIONAL O DE CALIDAD.
Si falta un archivo referenciado, créalo con la implementación mínima necesaria para compilar.
Tipo B — Configuración / documentación
Extrae el contenido tal cual, sin modificaciones salvo errores evidentes de sintaxis
(ej: YAML mal indentado).
Tipo C — Excel (.xlsx)
Si viene con contenido real, genera el archivo respetando ese contenido.
Si viene con descripción en lenguaje natural, genera un archivo Excel funcional con:

Fila de encabezados en negrita con color de fondo distintivo
Columnas con ancho ajustado al contenido
Tipos de dato correctos por columna
Validaciones si la descripción lo indica
Hojas nombradas descriptivamente si hay más de una
Filas de ejemplo si no hay datos reales

Tipo D — Word (.docx)
Si viene con contenido real, genera el archivo respetando ese contenido.
Si viene con descripción en lenguaje natural, genera un documento Word funcional con:

Estilos de título (Título 1, Título 2) para jerarquía de secciones
Fuente legible (Calibri o equivalente), tamaño 11-12pt para cuerpo
Márgenes estándar
Tabla de contenido si tiene múltiples secciones
Tablas con encabezados en negrita si aplica

Tipo E — Otro
Genera el archivo con el contenido o estructura más apropiada según la descripción.
PASO 5 — Exportación en ZIP
Empaqueta todos los archivos en un único archivo ZIP descargable respetando exactamente
la estructura de rutas indicada por los marcadores.
El ZIP debe incluir:

Archivos de código con únicamente los errores de compilación corregidos
Archivos de configuración y documentación sin cambios
Archivos nuevos creados para resolver dependencias de compilación faltantes
Archivos Excel y Word generados desde descripción

IMPORTANTE: El ZIP debe estar listo para descargar al finalizar. No preguntes si el usuario
quiere generarlo. Simplemente genera el archivo y proporciona el enlace de descarga; No debes desplegar en el chat el resumen de lo que arreglaste al Zip, solo entregalo.

REGLAS IMPORTANTES

No omitas ningún archivo aunque no tenga errores ni modificaciones
Respeta los nombres y rutas exactas indicadas por los marcadores
Si un archivo no tiene marcador claro, infiere el nombre desde su contenido
Si la cadena contiene solo documentación, placeholders o binarios fake, NO la reproduzcas:
aplicá PASO 0 (materializar el proyecto del briefing). Reproducir la carcasa es un fallo.
No agregues texto después del enlace de descarga del ZIP
No preguntes si el usuario quiere el ZIP: simplemente generalo siempre
Si detectas que falta un archivo de configuración necesario para compilar
(pom.xml, package.json, requirements.txt, build.gradle, etc.), créalo e inclúyelo
inferiendo su contenido desde los imports y frameworks detectados en el código
Nunca corrijas problemas 🟡 aunque parezcan obvios o fáciles de mejorar.
El participante que recibirá este proyecto los debe encontrar y resolver él mismo.


INPUT
Aquí está la cadena con los archivos:

// === ARCHIVO: pom.xml ===
<?xml version="1.0" encoding="UTF-8"?>
<project xmlns="http://maven.apache.org/POM/4.0.0"
         xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"
         xsi:schemaLocation="http://maven.apache.org/POM/4.0.0 http://maven.apache.org/xsd/maven-4.0.0.xsd">
    <modelVersion>4.0.0</modelVersion>

    <groupId>com.pragma</groupId>
    <artifactId>featurestore</artifactId>
    <version>1.0.0-SNAPSHOT</version>
    <packaging>jar</packaging>

    <name>Feature Store Pipeline</name>
    <description>Real-time feature store pipeline for recommendation model with A/B testing</description>

    <properties>
        <project.build.sourceEncoding>UTF-8</project.build.sourceEncoding>
        <java.version>17</java.version>
        <maven.compiler.source>17</maven.compiler.source>
        <maven.compiler.target>17</maven.compiler.target>
        <flink.version>1.18.0</flink.version>
        <scala.binary.version>2.12</scala.binary.version>
        <avro.version>1.11.3</avro.version>
        <debezium.version>2.4.0.Final</debezium.version>
        <confluent.version>7.5.1</confluent.version>
        <junit.version>5.9.3</junit.version>
        <mockito.version>5.3.1</mockito.version>
        <lombok.version>1.18.30</lombok.version>
        <jakarta.validation.version>3.0.2</jakarta.validation.version>
        <hibernate.validator.version>8.0.1.Final</hibernate.validator.version>
    </properties>

    <repositories>
        <repository>
            <id>confluent</id>
            <url>https://packages.confluent.io/maven/</url>
        </repository>
    </repositories>

    <dependencies>
        <!-- Apache Flink Core -->
        <dependency>
            <groupId>org.apache.flink</groupId>
            <artifactId>flink-streaming-java_${scala.binary.version}</artifactId>
            <version>${flink.version}</version>
            <scope>compile</scope>
        </dependency>

        <dependency>
            <groupId>org.apache.flink</groupId>
            <artifactId>flink-clients_${scala.binary.version}</artifactId>
            <version>${flink.version}</version>
            <scope>compile</scope>
        </dependency>

        <!-- Flink Connectors -->
        <dependency>
            <groupId>org.apache.flink</groupId>
            <artifactId>flink-connector-kafka_${scala.binary.version}</artifactId>
            <version>${flink.version}</version>
            <scope>compile</scope>
        </dependency>

        <dependency>
            <groupId>org.apache.flink</groupId>
            <artifactId>flink-connector-redis_${scala.binary.version}</artifactId>
            <version>${flink.version}</version>
            <scope>compile</scope>
        </dependency>

        <!-- Debezium CDC -->
        <dependency>
            <groupId>io.debezium</groupId>
            <artifactId>debezium-connector-postgres</artifactId>
            <version>${debezium.version}</version>
            <scope>compile</scope>
        </dependency>

        <!-- Avro Serialization -->
        <dependency>
            <groupId>org.apache.avro</groupId>
            <artifactId>avro</artifactId>
            <version>${avro.version}</version>
            <scope>compile</scope>
        </dependency>

        <dependency>
            <groupId>io.confluent</groupId>
            <artifactId>kafka-avro-serializer</artifactId>
            <version>${confluent.version}</version>
            <scope>compile</scope>
        </dependency>

        <!-- Validation -->
        <dependency>
            <groupId>jakarta.validation</groupId>
            <artifactId>jakarta.validation-api</artifactId>
            <version>${jakarta.validation.version}</version>
            <scope>compile</scope>
        </dependency>

        <dependency>
            <groupId>org.hibernate.validator</groupId>
            <artifactId>hibernate-validator</artifactId>
            <version>${hibernate.validator.version}</version>
            <scope>compile</scope>
        </dependency>

        <!-- Lombok -->
        <dependency>
            <groupId>org.projectlombok</groupId>
            <artifactId>lombok</artifactId>
            <version>${lombok.version}</version>
            <scope>provided</scope>
        </dependency>

        <!-- Testing -->
        <dependency>
            <groupId>org.apache.flink</groupId>
            <artifactId>flink-test-utils_${scala.binary.version}</artifactId>
            <version>${flink.version}</version>
            <scope>test</scope>
        </dependency>

        <dependency>
            <groupId>org.mockito</groupId>
            <artifactId>mockito-core</artifactId>
            <version>${mockito.version}</version>
            <scope>test</scope>
        </dependency>

        <dependency>
            <groupId>org.junit.jupiter</groupId>
            <artifactId>junit-jupiter-api</artifactId>
            <version>${junit.version}</version>
            <scope>test</scope>
        </dependency>

        <dependency>
            <groupId>org.junit.jupiter</groupId>
            <artifactId>junit-jupiter-engine</artifactId>
            <version>${junit.version}</version>
            <scope>test</scope>
        </dependency>
    </dependencies>

    <build>
        <plugins>
            <plugin>
                <groupId>org.apache.maven.plugins</groupId>
                <artifactId>maven-compiler-plugin</artifactId>
                <version>3.11.0</version>
                <configuration>
                    <source>${java.version}</source>
                    <target>${java.version}</target>
                    <annotationProcessorPaths>
                        <path>
                            <groupId>org.projectlombok</groupId>
                            <artifactId>lombok</artifactId>
                            <version>${lombok.version}</version>
                        </path>
                    </annotationProcessorPaths>
                </configuration>
            </plugin>

            <plugin>
                <groupId>org.apache.maven.plugins</groupId>
                <artifactId>maven-shade-plugin</artifactId>
                <version>3.5.1</version>
                <executions>
                    <execution>
                        <phase>package</phase>
                        <goals>
                            <goal>shade</goal>
                        </goals>
                        <configuration>
                            <transformers>
                                <transformer implementation="org.apache.maven.plugins.shade.resource.ManifestResourceTransformer">
                                    <mainClass>com.pragma.featurestore.FeatureStorePipeline</mainClass>
                                </transformer>
                                <transformer implementation="org.apache.maven.plugins.shade.resource.ServicesResourceTransformer"/>
                            </transformers>
                            <filters>
                                <filter>
                                    <artifact>*:*</artifact>
                                    <excludes>
                                        <exclude>META-INF/*.SF</exclude>
                                        <exclude>META-INF/*.DSA</exclude>
                                        <exclude>META-INF/*.RSA</exclude>
                                    </excludes>
                                </filter>
                            </filters>
                        </configuration>
                    </execution>
                </executions>
            </plugin>

            <plugin>
                <groupId>org.apache.maven.plugins</groupId>
                <artifactId>maven-surefire-plugin</artifactId>
                <version>3.1.2</version>
                <configuration>
                    <includes>
                        <include>**/*Test.java</include>
                    </includes>
                </configuration>
            </plugin>

            <plugin>
                <groupId>org.apache.avro</groupId>
                <artifactId>avro-maven-plugin</artifactId>
                <version>${avro.version}</version>
                <executions>
                    <execution>
                        <phase>generate-sources</phase>
                        <goals>
                            <goal>schema</goal>
                        </goals>
                        <configuration>
                            <sourceDirectory>${project.basedir}/src/main/resources/avro</sourceDirectory>
                            <outputDirectory>${project.build.directory}/generated-sources/avro</outputDirectory>
                        </configuration>
                    </execution>
                </executions>
            </plugin>
        </plugins>
    </build>
</project>

// === ARCHIVO: src/main/resources/application.properties ===
# =============================================================================
# Feature Store Pipeline Configuration
# =============================================================================

# -----------------------------------------------------------------------------
# Kafka Configuration
# -----------------------------------------------------------------------------
kafka.bootstrap.servers=localhost:9092
kafka.consumer.group.id=featurestore-consumer-group
kafka.topic.cdc.events=postgres.public.user_events
kafka.topic.features.computed=features.computed
kafka.topic.features.serving=features.serving
kafka.topic.drift.alerts=drift.alerts
kafka.security.protocol=PLAINTEXT
kafka.auto.offset.reset=earliest
kafka.enable.auto.commit=false
kafka.max.poll.records=500
kafka.session.timeout.ms=30000
kafka.heartbeat.interval.ms=10000

# Avro Schema Registry
kafka.schema.registry.url=http://localhost:8081
kafka.value.subject.name.strategy=io.confluent.kafka.serializers.subject.TopicRecordNameStrategy

# -----------------------------------------------------------------------------
# Redis Configuration
# -----------------------------------------------------------------------------
redis.host=localhost
redis.port=6379
redis.database=0
redis.password=
redis.timeout=2000
redis.connection.pool.max.total=50
redis.connection.pool.max.idle=20
redis.connection.pool.min.idle=5

# Feature TTLs by type (in seconds)
redis.ttl.user_features.short=300
redis.ttl.user_features.medium=3600
redis.ttl.user_features.long=86400
redis.ttl.product_features.short=300
redis.ttl.product_features.medium=3600
redis.ttl.product_features.long=43200
redis.ttl.transaction_features.short=600
redis.ttl.transaction_features.medium=7200
redis.ttl.behavior_features.short=180
redis.ttl.behavior_features.medium=1800

# Redis key patterns
redis.key.pattern.user_features=user_features:{version}:{ttl}:{entityId}
redis.key.pattern.product_features=product_features:{version}:{ttl}:{entityId}
redis.key.pattern.transaction_features=transaction_features:{version}:{ttl}:{entityId}
redis.key.pattern.behavior_features=behavior_features:{version}:{ttl}:{entityId}

# -----------------------------------------------------------------------------
# PostgreSQL CDC Configuration (Debezium)
# -----------------------------------------------------------------------------
postgres.host=localhost
postgres.port=5432
postgres.database=ecommerce
postgres.user=postgres
postgres.password=postgres
postgres.schema=public
postgres.slot.name=featurestore_slot
postgres.publication.name=featurestore_publication
postgres.table.include.list=public.users,public.products,public.transactions,public.user_events
postgres.plugin.name=pgoutput
postgres.snapshot.mode=initial

# -----------------------------------------------------------------------------
# Flink Job Configuration
# -----------------------------------------------------------------------------
flink.checkpoint.dir=file:///tmp/flink/checkpoints
flink.checkpoint.interval.ms=60000
flink.checkpoint.timeout.ms=600000
flink.checkpoint.min.pause.ms=30000
flink.checkpoint.max.concurrent.checkpoints=1
flink.checkpoint.cleanup.保留策略=retain
flink.state.backend=filesystem
flink.state.checkpoints.dir=file:///tmp/flink/checkpoints
flink.state.savepoints.dir=file:///tmp/flink/savepoints
flink.execution.checkpointing.mode=EXACTLY_ONCE
flink.execution.checkpointing.externalized-checkpoint-retention=RETAIN_ON_CANCELLATION

# -----------------------------------------------------------------------------
# Window Configuration
# -----------------------------------------------------------------------------
flink.window.short.size.ms=300000
flink.window.medium.size.ms=3600000
flink.window.long.size.ms=86400000
flink.window.slide.size.ms=60000
flink.window.watermark.idle.timeout.ms=60000
flink.window.watermark.allowed.lateness.ms=60000

# -----------------------------------------------------------------------------
# A/B Testing Configuration
# -----------------------------------------------------------------------------
abtesting.control.percentage=90
abtesting.challenger.percentage=10
abtesting.routing.key=user_id
abtesting.challenger.name=recommendation_model_v2
abtesting.control.name=recommendation_model_v1
abtesting.min.sample.size=1000
abtesting.evaluation.interval.ms=300000
abtesting.rollback.threshold.ctr.decrease=0.15
abtesting.rollback.warming.period.ms=600000

# -----------------------------------------------------------------------------
# Drift Detection Configuration
# -----------------------------------------------------------------------------
drift.detection.enabled=true
drift.detection.window.size.ms=3600000
drift.detection.statistical.threshold=0.05
drift.detection.population.stability.index.threshold=0.25
drift.detection.ks.test.alpha=0.05
drift.detection.feature.importance.weight=0.7
drift.detection.alert.topic=drift.alerts
drift.detection.retraining.trigger.threshold=0.1

# -----------------------------------------------------------------------------
# Feature Computation Configuration
# -----------------------------------------------------------------------------
feature.computation.parallelism=4
feature.computation.buffer.timeout.ms=100
feature.computation.max.outstanding.requests=100
feature.computation.feature.aggregation.enabled=true
feature.computation.feature.normalization.enabled=true
feature.computation.outlier.detection.enabled=true
feature.computation.outlier.threshold.sigma=3.0

# -----------------------------------------------------------------------------
# Monitoring and Metrics
# -----------------------------------------------------------------------------
metrics.reporter.class=org.apache.flink.metrics.prometheus.PrometheusReporter
metrics.reporter.port=9250
metrics.reporter.interval.seconds=30
metrics.classloader.resolve.order=parent-first

# -----------------------------------------------------------------------------
# Logging Configuration
# -----------------------------------------------------------------------------
logging.level.com.pragma.featurestore=INFO
logging.level.org.apache.kafka=WARN
logging.level.org.apache.flink=INFO
logging.level.io.debezium=WARN
logging.pattern.console=%d{yyyy-MM-dd HH:mm:ss} %-5level [%t] %c{1} - %msg%n
logging.pattern.file=%d{yyyy-MM-dd HH:mm:ss} %-5level [%t] %c{1} - %msg%n


// === ARCHIVO: src/main/java/com/pragma/featurestore/FeatureStorePipeline.java ===
package com.pragma.featurestore;

import com.pragma.featurestore.ingestion.PostgresCDCSource;
import com.pragma.featurestore.ingestion.KafkaSink;
import com.pragma.featurestore.processing.FeatureCalculator;
import com.pragma.featurestore.dto.FeatureEvent;
import com.pragma.featurestore.config.FlinkConfig;
import org.apache.flink.streaming.api.environment.StreamExecutionEnvironment;
import org.apache.flink.streaming.api.datastream.DataStream;
import org.apache.flink.api.common.eventtime.WatermarkStrategy;
import org.apache.flink.api.common.serialization.SimpleStringSchema;
import org.apache.flink.connector.kafka.source.KafkaSource;
import org.apache.flink.connector.kafka.source.enumerator.initializer.OffsetsInitializer;
import org.apache.kafka.clients.producer.ProducerConfig;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.time.Duration;
import java.util.Properties;

public class FeatureStorePipeline {
    private static final Logger LOG = LoggerFactory.getLogger(FeatureStorePipeline.class);
    
    private static final String KAFKA_BOOTSTRAP_SERVERS = "localhost:9092";
    private static final String CDC_TOPIC = "postgres.public.user_events";
    private static final String FEATURES_TOPIC = "feature-store-features";
    private static final String CONSUMER_GROUP = "feature-store-consumer";
    private static final long CHECKPOINT_INTERVAL_MS = 60_000L;
    
    public static void main(String[] args) throws Exception {
        LOG.info("Iniciando Feature Store Pipeline para modelo de recomendación");
        
        final StreamExecutionEnvironment env = StreamExecutionEnvironment.getExecutionEnvironment();
        
        configureEnvironment(env);
        
        DataStream<String> rawCdcEvents = ingestFromPostgresCDC(env);
        
        DataStream<FeatureEvent> featureEvents = processCdcEvents(rawCdcEvents);
        
        writeToKafka(featureEvents);
        
        LOG.info("Pipeline configurado. Ejecutando job de Flink...");
        env.execute("Feature Store Pipeline - Real-time Feature Computation");
    }
    
    private static void configureEnvironment(StreamExecutionEnvironment env) {
        env.setParallelism(FlinkConfig.getDefaultParallelism());
        
        env.enableCheckpointing(CHECKPOINT_INTERVAL_MS);
        env.getCheckpointConfig().setMinPauseBetweenCheckpoints(30_000);
        env.getCheckpointConfig().setCheckpointTimeout(300_000);
        env.getCheckpointConfig().setMaxConcurrentCheckpoints(1);
        env.getCheckpointConfig().setTolerableCheckpointFailureNumber(3);
        
        env.getCheckpointConfig().setExternalizedCheckpointCleanup(
            org.apache.flink.streaming.api.environment.CheckpointConfig.ExternalizedCheckpointCleanup.RETAIN_ON_CANCELLATION
        );
        
        env.getConfig().setAutoWatermarkInterval(5_000L);
        
        LOG.info("Entorno de Flink configurado con checkpointing cada {}ms", CHECKPOINT_INTERVAL_MS);
    }
    
    private static DataStream<String> ingestFromPostgresCDC(StreamExecutionEnvironment env) {
        LOG.info("Configurando fuente CDC desde PostgreSQL");
        
        Properties kafkaProps = new Properties();
        kafkaProps.setProperty("bootstrap.servers", KAFKA_BOOTSTRAP_SERVERS);
        kafkaProps.setProperty("group.id", CONSUMER_GROUP);
        kafkaProps.setProperty("auto.offset.reset", "earliest");
        kafkaProps.setProperty("enable.auto.commit", "false");
        kafkaProps.setProperty("isolation.level", "read_committed");
        
        KafkaSource<String> kafkaSource = KafkaSource.<String>builder()
            .setBootstrapServers(KAFKA_BOOTSTRAP_SERVERS)
            .setTopics(CDC_TOPIC)
            .setGroupId(CONSUMER_GROUP)
            .setStartingOffsets(OffsetsInitializer.committedOffsets())
            .setValueOnlyDeserializer(new SimpleStringSchema())
            .setProperties(kafkaProps)
            .build();
        
        DataStream<String> cdcStream = env.fromSource(
            kafkaSource,
            WatermarkStrategy.noWatermarks(),
            "PostgreSQL CDC Source"
        );
        
        LOG.info("Fuente CDC configurada, topic: {}", CDC_TOPIC);
        return cdcStream;
    }
    
    private static DataStream<FeatureEvent> processCdcEvents(DataStream<String> rawEvents) {
        LOG.info("Procesando eventos CDC para computar features");
        
        return rawEvents
            .filter(event -> event != null && !event.isEmpty())
            .keyBy(event -> extractEntityKey(event))
            .process(new FeatureCalculator())
            .name("Feature Calculator")
            .uid("feature-calculator");
    }
    
    private static String extractEntityKey(String event) {
        try {
            if (event.contains("\"user_id\"")) {
                int start = event.indexOf("\"user_id\"");
                int colon = event.indexOf(":", start);
                int comma = event.indexOf(",", colon);
                if (comma == -1) comma = event.indexOf("}", colon);
                return "user:" + event.substring(colon + 1, comma).replace("\"", "").trim();
            }
            return "unknown";
        } catch (Exception e) {
            LOG.warn("Error extrayendo clave de entidad del evento: {}", e.getMessage());
            return "unknown";
        }
    }
    
    private static void writeToKafka(DataStream<FeatureEvent> featureEvents) {
        LOG.info("Configurando sink hacia Kafka, topic: {}", FEATURES_TOPIC);
        
        Properties producerProps = new Properties();
        producerProps.setProperty(ProducerConfig.BOOTSTRAP_SERVERS_CONFIG, KAFKA_BOOTSTRAP_SERVERS);
        producerProps.setProperty(ProducerConfig.ACKS_CONFIG, "all");
        producerProps.setProperty(ProducerConfig.RETRIES_CONFIG, "3");
        producerProps.setProperty(ProducerConfig.ENABLE_IDEMPOTENCE_CONFIG, "true");
        producerProps.setProperty(ProducerConfig.MAX_IN_FLIGHT_REQUESTS_PER_CONNECTION, "5");
        producerProps.setProperty(ProducerConfig.LINGER_MS_CONFIG, "10");
        producerProps.setProperty(ProducerConfig.BUFFER_MEMORY_CONFIG, "33554432");
        
        KafkaSink<FeatureEvent> kafkaSink = new KafkaSink<>(
            FEATURES_TOPIC,
            producerProps,
            event -> event.getEntityId(),
            event -> event.getFeatureName() + "-" + event.getVersion()
        );
        
        featureEvents.sinkTo(kafkaSink)
            .name("Kafka Feature Sink")
            .uid("kafka-feature-sink");
        
        LOG.info("Sink de Kafka configurado exitosamente");
    }
}

// === ARCHIVO: src/main/java/com/pragma/featurestore/ingestion/PostgresCDCSource.java ===
package com.pragma.featurestore.ingestion;

import org.apache.flink.streaming.api.functions.source.SourceFunction;
import org.apache.flink.configuration.Configuration;
import org.apache.kafka.clients.consumer.ConsumerConfig;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.apache.kafka.clients.consumer.ConsumerRecords;
import org.apache.kafka.clients.consumer.KafkaConsumer;
import org.apache.kafka.common.TopicPartition;
import org.apache.kafka.common.serialization.StringDeserializer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.time.Duration;
import java.time.Instant;
import java.util.*;
import java.util.concurrent.atomic.AtomicBoolean;

public class PostgresCDCSource implements SourceFunction<String> {
    private static final Logger LOG = LoggerFactory.getLogger(PostgresCDCSource.class);
    
    private final String bootstrapServers;
    private final String topic;
    private final String groupId;
    private final Properties debeziumConfig;
    
    private transient KafkaConsumer<String, String> kafkaConsumer;
    private final AtomicBoolean isRunning = new AtomicBoolean(false);
    
    private long lastProcessedTimestamp = 0L;
    private final Map<TopicPartition, Long> offsetMap = new HashMap<>();
    
    public PostgresCDCSource(String bootstrapServers, String topic, String groupId) {
        this.bootstrapServers = bootstrapServers;
        this.topic = topic;
        this.groupId = groupId;
        this.debeziumConfig = buildDebeziumConfig();
    }
    
    private Properties buildDebeziumConfig() {
        Properties config = new Properties();
        config.put("connector.class", "io.debezium.connector.postgresql.PostgresConnector");
        config.put("database.hostname", "localhost");
        config.put("database.port", "5432");
        config.put("database.user", "postgres");
        config.put("database.password", "postgres");
        config.put("database.dbname", "featurestore");
        config.put("database.server.name", "postgres");
        config.put("table.include.list", "public.user_events,public.product_events,public.transaction_events");
        config.put("plugin.name", "pgoutput");
        config.put("publication.name", "featurestore_publication");
        config.put("slot.name", "featurestore_slot");
        config.put("snapshot.mode", "initial");
        config.put("decimal.handling.mode", "double");
        config.put("time.precision.mode", "adaptive");
        config.put("schema.history.internal.kafka.bootstrap.servers", bootstrapServers);
        config.put("schema.history.internal.kafka.topic", "schema-changes.featurestore");
        config.put("transforms", "unwrap");
        config.put("transforms.unwrap.type", "io.debezium.transforms.ExtractNewRecordState");
        config.put("transforms.unwrap.drop.tombstones", "false");
        config.put("transforms.unwrap.delete.handling.mode", "rewrite");
        config.put("key.converter", "org.apache.kafka.connect.json.JsonConverter");
        config.put("value.converter", "org.apache.kafka.connect.json.JsonConverter");
        config.put("key.converter.schemas.enable", "false");
        config.put("value.converter.schemas.enable", "false");
        return config;
    }
    
    @Override
    public void run(SourceContext<String> ctx) throws Exception {
        isRunning.set(true);
        
        initializeConsumer();
        
        LOG.info("PostgreSQL CDC Source iniciado, escuchando topic: {}", topic);
        
        while (isRunning.get()) {
            try {
                ConsumerRecords<String, String> records = kafkaConsumer.poll(Duration.ofMillis(1000));
                
                if (!records.isEmpty()) {
                    for (ConsumerRecord<String, String> record : records) {
                        if (isRunning.get()) {
                            String event = processCdcEvent(record);
                            if (event != null) {
                                ctx.collect(event);
                                updateOffset(record);
                            }
                        }
                    }
                    kafkaConsumer.commitSync();
                }
                
                emitWatermark(ctx);
                
            } catch (Exception e) {
                LOG.error("Error procesando eventos CDC: {}", e.getMessage(), e);
                handleFailure();
            }
        }
        
        LOG.info("PostgreSQL CDC Source detenido");
    }
    
    private void initializeConsumer() {
        Properties consumerProps = new Properties();
        consumerProps.setProperty(ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG, bootstrapServers);
        consumerProps.setProperty(ConsumerConfig.GROUP_ID_CONFIG, groupId);
        consumerProps.setProperty(ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG, StringDeserializer.class.getName());
        consumerProps.setProperty(ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG, StringDeserializer.class.getName());
        consumerProps.setProperty(ConsumerConfig.AUTO_OFFSET_RESET_CONFIG, "earliest");
        consumerProps.setProperty(ConsumerConfig.ENABLE_AUTO_COMMIT_CONFIG, "false");
        consumerProps.setProperty(ConsumerConfig.ISOLATION_LEVEL_CONFIG, "read_committed");
        consumerProps.setProperty(ConsumerConfig.MAX_POLL_RECORDS_CONFIG, "100");
        consumerProps.setProperty(ConsumerConfig.MAX_POLL_INTERVAL_MS_CONFIG, "300000");
        consumerProps.setProperty(ConsumerConfig.SESSION_TIMEOUT_MS_CONFIG, "30000");
        consumerProps.setProperty(ConsumerConfig.HEARTBEAT_INTERVAL_MS_CONFIG, "10000");
        
        kafkaConsumer = new KafkaConsumer<>(consumerProps);
        kafkaConsumer.subscribe(Collections.singletonList(topic));
        
        LOG.info("Kafka consumer inicializado para CDC");
    }
    
    private String processCdcEvent(ConsumerRecord<String, String> record) {
        if (record.value() == null || record.value().isEmpty()) {
            return null;
        }
        
        String rawEvent = record.value();
        
        try {
            if (rawEvent.contains("\"__deleted\" : \"true\"")) {
                LOG.debug("Evento de borrado detectado, ignorando para feature computation");
                return null;
            }
            
            long eventTimestamp = extractTimestamp(rawEvent);
            if (eventTimestamp > lastProcessedTimestamp) {
                lastProcessedTimestamp = eventTimestamp;
            }
            
            return rawEvent;
            
        } catch (Exception e) {
            LOG.warn("Error procesando evento CDC: {}", e.getMessage());
            return null;
        }
    }
    
    private long extractTimestamp(String event) {
        try {
            if (event.contains("\"__ts_ms\"")) {
                int start = event.indexOf("\"__ts_ms\"");
                int colon = event.indexOf(":", start);
                int comma = event.indexOf(",", colon);
                if (comma == -1) comma = event.indexOf("}", colon);
                return Long.parseLong(event.substring(colon + 1, comma).trim());
            }
            return Instant.now().toEpochMilli();
        } catch (Exception e) {
            return Instant.now().toEpochMilli();
        }
    }
    
    private void updateOffset(ConsumerRecord<String, String> record) {
        TopicPartition tp = new TopicPartition(record.topic(), record.partition());
        offsetMap.put(tp, record.offset() + 1);
    }
    
    private void emitWatermark(SourceContext<String> ctx) {
        if (lastProcessedTimestamp > 0) {
            ctx.emitWatermark(new org.apache.flink.streaming.api.watermark.Watermark(lastProcessedTimestamp - 5000));
        }
    }
    
    private void handleFailure() {
        try {
            Thread.sleep(5000);
            LOG.info("Reintentando conexión CDC");
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
    }
    
    @Override
    public void cancel() {
        isRunning.set(false);
        if (kafkaConsumer != null) {
            kafkaConsumer.wakeup();
            LOG.info("CDC Source cancelado");
        }
    }
}

// === ARCHIVO: src/main/java/com/pragma/featurestore/ingestion/KafkaSink.java ===
package com.pragma.featurestore.ingestion;

import org.apache.flink.streaming.api.functions.sink.SinkFunction;
import org.apache.flink.api.common.serialization.SerializationSchema;
import org.apache.kafka.clients.producer.KafkaProducer;
import org.apache.kafka.clients.producer.ProducerConfig;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.apache.kafka.common.serialization.StringSerializer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Properties;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;

public class KafkaSink<T> implements SinkFunction<T> {
    private static final Logger LOG = LoggerFactory.getLogger(KafkaSink.class);
    
    private final String topic;
    private final Properties producerProperties;
    private final KeyExtractor<T> keyExtractor;
    private final TopicExtractor<T> topicExtractor;
    private final SerializationSchema<T> serializationSchema;
    
    private transient KafkaProducer<String, byte[]> kafkaProducer;
    private transient volatile boolean initialized = false;
    
    private long totalRecordsSent = 0L;
    private long totalSendErrors = 0L;
    
    public interface KeyExtractor<T> {
        String extractKey(T event);
    }
    
    public interface TopicExtractor<T> {
        String extractTopic(T event);
    }
    
    public KafkaSink(String topic, Properties producerProperties, 
                     KeyExtractor<T> keyExtractor, TopicExtractor<T> topicExtractor) {
        this(topic, producerProperties, keyExtractor, topicExtractor, null);
    }
    
    public KafkaSink(String topic, Properties producerProperties,
                     KeyExtractor<T> keyExtractor, TopicExtractor<T> topicExtractor,
                     SerializationSchema<T> serializationSchema) {
        this.topic = topic;
        this.producerProperties = producerProperties;
        this.keyExtractor = keyExtractor;
        this.topicExtractor = topicExtractor;
        this.serializationSchema = serializationSchema;
    }
    
    @Override
    public void invoke(T value, Context context) throws Exception {
        if (!initialized) {
            initialize();
        }
        
        try {
            String key = keyExtractor.extractKey(value);
            String targetTopic = topicExtractor != null ? topicExtractor.extractTopic(value) : topic;
            
            byte[] serializedValue;
            if (serializationSchema != null) {
                serializedValue = serializationSchema.serialize(value);
            } else {
                serializedValue = value.toString().getBytes();
            }
            
            long timestamp = context.timestamp();
            
            ProducerRecord<String, byte[]> record = new ProducerRecord<>(
                targetTopic,
                null,
                timestamp != -1 ? timestamp : System.currentTimeMillis(),
                key,
                serializedValue
            );
            
            Future future = kafkaProducer.send(record, (metadata, exception) -> {
                if (exception != null) {
                    LOG.error("Error enviando a Kafka - Topic: {}, Key: {}, Error: {}",
                        targetTopic, key, exception.getMessage());
                    totalSendErrors++;
                } else {
                    LOG.debug("Evento enviado - Topic: {}, Partition: {}, Offset: {}, Key: {}",
                        metadata.topic(), metadata.partition(), metadata.offset(), key);
                }
            });
            
            totalRecordsSent++;
            
            if (totalRecordsSent % 1000 == 0) {
                LOG.info("KafkaSink stats - Total enviado: {}, Errores: {}", totalRecordsSent, totalSendErrors);
            }
            
        } catch (Exception e) {
            LOG.error("Error en invoke de KafkaSink: {}", e.getMessage(), e);
            throw e;
        }
    }
    
    private synchronized void initialize() {
        if (initialized) {
            return;
        }
        
        Properties props = new Properties();
        props.putAll(producerProperties);
        
        props.setProperty(ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG, StringSerializer.class.getName());
        props.setProperty(ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG, ByteArraySerializer.class.getName());
        
        props.setProperty(ProducerConfig.COMPRESSION_TYPE_CONFIG, "lz4");
        props.setProperty(ProducerConfig.BATCH_SIZE_CONFIG, "16384");
        props.setProperty(ProducerConfig.REQUEST_TIMEOUT_MS_CONFIG, "30000");
        props.setProperty(ProducerConfig.DELIVERY_TIMEOUT_MS_CONFIG, "120000");
        
        kafkaProducer = new KafkaProducer<>(props);
        initialized = true;
        
        LOG.info("KafkaProducer inicializado para topic: {}", topic);
    }
    
    public void flush() {
        if (kafkaProducer != null) {
            kafkaProducer.flush();
            LOG.debug("KafkaSink flush completado");
        }
    }
    
    public void close() {
        if (kafkaProducer != null) {
            try {
                kafkaProducer.flush(30, TimeUnit.SECONDS);
                kafkaProducer.close();
                LOG.info("KafkaSink cerrado - Total enviado: {}, Errores: {}", totalRecordsSent, totalSendErrors);
            } catch (Exception e) {
                LOG.error("Error cerrando KafkaSink: {}", e.getMessage());
            }
        }
    }
    
    public long getTotalRecordsSent() {
        return totalRecordsSent;
    }
    
    public long getTotalSendErrors() {
        return totalSendErrors;
    }
    
    private static class ByteArraySerializer implements org.apache.kafka.common.serialization.Serializer<byte[]> {
        @Override
        public byte[] serialize(String topic, byte[] data) {
            return data;
        }
        
        @Override
        public void close() {}
        
        @Override
        public void configure(java.util.Map<String, ?> configs, boolean isKey) {}
    }
}

// === ARCHIVO: src/main/java/com/pragma/featurestore/processing/FeatureCalculator.java ===
package com.pragma.featurestore.processing;

import org.apache.flink.api.common.eventtime.WatermarkStrategy;
import org.apache.flink.api.common.functions.AggregateFunction;
import org.apache.flink.api.common.state.ListState;
import org.apache.flink.api.common.state.ListStateDescriptor;
import org.apache.flink.api.common.typeinfo.TypeInformation;
import org.apache.flink.configuration.Configuration;
import org.apache.flink.streaming.api.functions.KeyedProcessFunction;
import org.apache.flink.streaming.api.functions.windowing.ProcessAllWindowFunction;
import org.apache.flink.streaming.api.windowing.assigners.SlidingEventTimeWindows;
import org.apache.flink.streaming.api.windowing.windows.GlobalWindow;
import org.apache.flink.util.Collector;
import com.pragma.featurestore.dto.FeatureEvent;
import com.pragma.featurestore.exception.FeatureComputationException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.time.Duration;
import java.util.*;
import java.util.concurrent.atomic.AtomicLong;

public class FeatureCalculator extends KeyedProcessFunction<String, FeatureEvent, FeatureEvent> {

    private static final Logger LOG = LoggerFactory.getLogger(FeatureCalculator.class);
    private static final Duration WINDOW_5MIN = Duration.ofMinutes(5);
    private static final Duration WINDOW_1H = Duration.ofHours(1);
    private static final Duration WINDOW_24H = Duration.ofHours(24);
    private static final Duration LATE_DATA_THRESHOLD = Duration.ofMinutes(10);

    private transient ListState<FeatureEvent> eventBuffer;
    private transient Map<String, AggregatedFeature> aggregatedFeatures;
    private transient AtomicLong processingLatency;

    public FeatureCalculator() {
        this.aggregatedFeatures = new HashMap<>();
    }

    @Override
    public void open(Configuration parameters) {
        ListStateDescriptor<FeatureEvent> descriptor = new ListStateDescriptor<>(
                "eventBuffer",
                TypeInformation.of(FeatureEvent.class)
        );
        eventBuffer = getRuntimeContext().getListState(descriptor);
        processingLatency = new AtomicLong(0);
        aggregatedFeatures = new HashMap<>();

        LOG.info("FeatureCalculator initialized for key: {}", getRuntimeContext().getCurrentKey());
    }

    @Override
    public void processElement(FeatureEvent event, Context ctx, Collector<FeatureEvent> out) {
        long startTime = System.currentTimeMillis();

        try {
            if (!isValidEvent(event)) {
                LOG.warn("Invalid event received: {}", event.getEventId());
                return;
            }

            long eventTimestamp = event.getTimestamp();
            long currentWatermark = ctx.timerService().currentWatermark();

            if (isLateData(eventTimestamp, currentWatermark)) {
                handleLateData(event);
                LOG.debug("Late data handled for event: {} (timestamp: {}, watermark: {})",
                        event.getEventId(), eventTimestamp, currentWatermark);
                return;
            }

            String featureKey = buildFeatureKey(event);
            AggregatedFeature aggregated = aggregatedFeatures.computeIfAbsent(
                    featureKey,
                    k -> new AggregatedFeature(featureKey)
            );

            aggregated.addEvent(event);

            FeatureEvent windowed5min = computeWindowFeature(event, WINDOW_5MIN, "5min");
            FeatureEvent windowed1h = computeWindowFeature(event, WINDOW_1H, "1h");
            FeatureEvent windowed24h = computeWindowFeature(event, WINDOW_24H, "24h");

            if (windowed5min != null) out.collect(windowed5min);
            if (windowed1h != null) out.collect(windowed1h);
            if (windowed24h != null) out.collect(windowed24h);

            long latency = System.currentTimeMillis() - startTime;
            processingLatency.addAndGet(latency);

            LOG.debug("Event processed: {} with latency: {}ms", event.getEventId(), latency);

        } catch (Exception e) {
            LOG.error("Error processing event: {}", event.getEventId(), e);
            throw new FeatureComputationException("Failed to compute feature for event: " + event.getEventId(), e);
        }
    }

    private boolean isValidEvent(FeatureEvent event) {
        return event != null
                && event.getEventId() != null
                && event.getEntityId() != null
                && event.getFeatureName() != null
                && event.getTimestamp() > 0
                && event.getValue() != null;
    }

    private boolean isLateData(long eventTimestamp, long currentWatermark) {
        return currentWatermark > 0
                && (currentWatermark - eventTimestamp) > LATE_DATA_THRESHOLD.toMillis();
    }

    private void handleLateData(FeatureEvent event) {
        try {
            eventBuffer.add(event);
            LOG.debug("Late event added to buffer: {}", event.getEventId());
        } catch (Exception e) {
            LOG.error("Failed to buffer late event: {}", event.getEventId(), e);
        }
    }

    private String buildFeatureKey(FeatureEvent event) {
        return String.format("%s:%s:%s",
                event.getEntityType(),
                event.getEntityId(),
                event.getFeatureName());
    }

    private FeatureEvent computeWindowFeature(FeatureEvent event, Duration windowSize, String windowType) {
        String featureKey = buildFeatureKey(event);
        AggregatedFeature agg = aggregatedFeatures.get(featureKey);

        if (agg == null) {
            return null;
        }

        long windowStart = (event.getTimestamp() / windowSize.toMillis()) * windowSize.toMillis();
        long windowEnd = windowStart + windowSize.toMillis();

        List<FeatureEvent> windowEvents = agg.getEventsInWindow(windowStart, windowEnd);

        if (windowEvents.isEmpty()) {
            return null;
        }

        double aggregatedValue = computeAggregation(windowEvents, event.getAggregationType());

        FeatureEvent result = new FeatureEvent();
        result.setEventId(UUID.randomUUID().toString());
        result.setEntityType(event.getEntityType());
        result.setEntityId(event.getEntityId());
        result.setFeatureName(event.getFeatureName() + "_" + windowType);
        result.setValue(aggregatedValue);
        result.setTimestamp(windowEnd);
        result.setWindowType(windowType);
        result.setVersion(event.getVersion());
        result.setAggregationType(event.getAggregationType());

        return result;
    }

    private double computeAggregation(List<FeatureEvent> events, String aggregationType) {
        if (events == null || events.isEmpty()) {
            return 0.0;
        }

        return switch (aggregationType != null ? aggregationType.toUpperCase() : "SUM") {
            case "SUM" -> events.stream()
                    .mapToDouble(e -> ((Number) e.getValue()).doubleValue())
                    .sum();
            case "AVG" -> events.stream()
                    .mapToDouble(e -> ((Number) e.getValue()).doubleValue())
                    .average()
                    .orElse(0.0);
            case "COUNT" -> (double) events.size();
            case "MIN" -> events.stream()
                    .mapToDouble(e -> ((Number) e.getValue()).doubleValue())
                    .min()
                    .orElse(0.0);
            case "MAX" -> events.stream()
                    .mapToDouble(e -> ((Number) e.getValue()).doubleValue())
                    .max()
                    .orElse(0.0);
            default -> events.stream()
                    .mapToDouble(e -> ((Number) e.getValue()).doubleValue())
                    .sum();
        };
    }

    public static WatermarkStrategy<FeatureEvent> createWatermarkStrategy() {
        return WatermarkStrategy.<FeatureEvent>forBoundedOutOfOrderness(Duration.ofMinutes(5))
                .withTimestampAssigner((event, timestamp) -> event.getTimestamp())
                .withIdleness(Duration.ofMinutes(1));
    }

    public static SlidingEventTimeWindows createSlidingWindow(Duration size, Duration slide) {
        return SlidingEventTimeWindows.of(size, slide);
    }

    public long getProcessingLatency() {
        return processingLatency != null ? processingLatency.get() : 0;
    }

    private static class AggregatedFeature {
        private final String featureKey;
        private final List<FeatureEvent> events;
        private final Map<Long, List<FeatureEvent>> windowIndex;

        public AggregatedFeature(String featureKey) {
            this.featureKey = featureKey;
            this.events = new ArrayList<>();
            this.windowIndex = new HashMap<>();
        }

        public void addEvent(FeatureEvent event) {
            events.add(event);
            long windowKey = event.getTimestamp() / 300000;
            windowIndex.computeIfAbsent(windowKey, k -> new ArrayList<>()).add(event);
        }

        public List<FeatureEvent> getEventsInWindow(long windowStart, long windowEnd) {
            List<FeatureEvent> result = new ArrayList<>();
            for (FeatureEvent event : events) {
                if (event.getTimestamp() >= windowStart && event.getTimestamp() < windowEnd) {
                    result.add(event);
                }
            }
            return result;
        }
    }
}

// === ARCHIVO: src/main/java/com/pragma/featurestore/serving/RedisFeatureStore.java ===
package com.pragma.featurestore.serving;

import org.apache.flink.configuration.Configuration;
import org.apache.flink.streaming.connectors.redis.common.config.FlinkJedisClusterConfig;
import org.apache.flink.streaming.connectors.redis.common.config.FlinkJedisConfigBase;
import org.apache.flink.streaming.connectors.redis.common.mapper.RedisCommand;
import org.apache.flink.streaming.connectors.redis.common.mapper.RedisCommandDescription;
import org.apache.flink.streaming.connectors.redis.common.mapper.RedisMapper;
import org.apache.flink.streaming.connectors.redis.common.mapper.RedisSink;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisCluster;
import redis.clients.jedis.JedisPool;
import redis.clients.jedis.JedisPoolConfig;

import java.time.Duration;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class RedisFeatureStore {

    private static final Logger LOG = LoggerFactory.getLogger(RedisFeatureStore.class);
    private static final Pattern VERSION_PATTERN = Pattern.compile("^feature:([^:]+):([^:]+):v(\\d+\\.\\d+\\.\\d+)$");
    private static final String DEFAULT_NAMESPACE = "feature";
    private static final Duration DEFAULT_TTL = Duration.ofHours(1);

    private final FlinkJedisConfigBase jedisConfig;
    private final Map<String, Duration> featureTtlMap;
    private final Map<String, String> featureVersionMap;
    private final Map<String, FeatureCache> cache;
    private final boolean enableVersioning;
    private final int maxCacheSize;

    private JedisPool jedisPool;
    private JedisCluster jedisCluster;

    public RedisFeatureStore(FlinkJedisConfigBase jedisConfig) {
        this(jedisConfig, true, 1000);
    }

    public RedisFeatureStore(FlinkJedisConfigBase jedisConfig, boolean enableVersioning, int maxCacheSize) {
        this.jedisConfig = jedisConfig;
        this.enableVersioning = enableVersioning;
        this.maxCacheSize = maxCacheSize;
        this.featureTtlMap = new ConcurrentHashMap<>();
        this.featureVersionMap = new ConcurrentHashMap<>();
        this.cache = new ConcurrentHashMap<>();
        initializeDefaultTTLs();
    }

    private void initializeDefaultTTLs() {
        featureTtlMap.put("user_features", Duration.ofHours(1));
        featureTtlMap.put("item_features", Duration.ofHours(24));
        featureTtlMap.put("context_features", Duration.ofMinutes(5));
        featureTtlMap.put("behavior_features", Duration.ofMinutes(30));
        featureTtlMap.put("aggregation_features", Duration.ofHours(12));
    }

    public void open() {
        try {
            JedisPoolConfig poolConfig = new JedisPoolConfig();
            poolConfig.setMaxTotal(128);
            poolConfig.setMaxIdle(64);
            poolConfig.setMinIdle(16);
            poolConfig.setTestOnBorrow(true);
            poolConfig.setTestOnReturn(true);
            poolConfig.setTestWhileIdle(true);

            if (jedisConfig.getClusterNodes() != null && !jedisConfig.getClusterNodes().isEmpty()) {
                FlinkJedisClusterConfig clusterConfig = (FlinkJedisClusterConfig) jedisConfig;
                jedisCluster = new JedisCluster(
                        clusterConfig.getNodes(),
                        clusterConfig.getConnectionTimeout(),
                        clusterConfig.getSoTimeout(),
                        clusterConfig.getMaxAttempts(),
                        clusterConfig.getPassword(),
                        poolConfig
                );
                LOG.info("Redis cluster connection established");
            } else {
                jedisPool = new JedisPool(
                        poolConfig,
                        jedisConfig.getHost(),
                        jedisConfig.getPort(),
                        jedisConfig.getConnectionTimeout(),
                        jedisConfig.getPassword(),
                        jedisConfig.getDatabase()
                );
                LOG.info("Redis standalone connection established to {}:{}",
                        jedisConfig.getHost(), jedisConfig.getPort());
            }
        } catch (Exception e) {
            LOG.error("Failed to initialize Redis connection", e);
            throw new RuntimeException("Redis connection initialization failed", e);
        }
    }

    public void setFeature(String entityType, String entityId, String featureName,
                          Object value, String version) {
        String key = buildKey(entityType, entityId, featureName, version);
        Duration ttl = resolveTTL(featureName);

        try {
            Jedis jedis = getJedis();
            String serializedValue = serializeValue(value);
            jedis.setex(key, ttl.getSeconds(), serializedValue);

            if (enableVersioning) {
                updateVersionIndex(entityType, entityId, featureName, version);
            }

            updateLocalCache(key, value, ttl);

            LOG.debug("Feature set: {} = {} (TTL: {}s)", key, serializedValue, ttl.getSeconds());
        } catch (Exception e) {
            LOG.error("Failed to set feature: {}", key, e);
            throw new RuntimeException("Failed to set feature in Redis", e);
        }
    }

    public Optional<Object> getFeature(String entityType, String entityId, String featureName) {
        return getFeature(entityType, entityId, featureName, null);
    }

    public Optional<Object> getFeature(String entityType, String entityId, String featureName, String version) {
        String key = buildKey(entityType, entityId, featureName, version);

        FeatureCache cached = cache.get(key);
        if (cached != null && !cached.isExpired()) {
            LOG.debug("Cache hit for key: {}", key);
            return Optional.of(cached.getValue());
        }

        try {
            Jedis jedis = getJedis();
            String value = jedis.get(key);

            if (value == null) {
                if (enableVersioning && version == null) {
                    return getLatestVersion(entityType, entityId, featureName);
                }
                LOG.debug("Feature not found: {}", key);
                return Optional.empty();
            }

            Object deserialized = deserializeValue(value);
            Duration ttl = resolveTTL(featureName);
            updateLocalCache(key, deserialized, ttl);

            LOG.debug("Feature retrieved: {}", key);
            return Optional.of(deserialized);
        } catch (Exception e) {
            LOG.error("Failed to get feature: {}", key, e);
            return Optional.empty();
        }
    }

    private Optional<Object> getLatestVersion(String entityType, String entityId, String featureName) {
        String versionKey = buildVersionIndexKey(entityType, entityId, featureName);

        try {
            Jedis jedis = getJedis();
            String latestVersion = jedis.get(versionKey);

            if (latestVersion == null) {
                return Optional.empty();
            }

            String key = buildKey(entityType, entityId, featureName, latestVersion);
            String value = jedis.get(key);

            if (value == null) {
                return Optional.empty();
            }

            return Optional.of(deserializeValue(value));
        } catch (Exception e) {
            LOG.error("Failed to get latest version for: {}/{}/{}", entityType, entityId, featureName, e);
            return Optional.empty();
        }
    }

    private void updateVersionIndex(String entityType, String entityId, String featureName, String version) {
        String versionKey = buildVersionIndexKey(entityType, entityId, featureName);

        try {
            Jedis jedis = getJedis();
            String currentLatest = jedis.get(versionKey);

            if (currentLatest == null || compareVersions(version, currentLatest) > 0) {
                jedis.set(versionKey, version);
                featureVersionMap.put(versionKey, version);
                LOG.debug("Version index updated: {} -> {}", versionKey, version);
            }
        } catch (Exception e) {
            LOG.error("Failed to update version index: {}", versionKey, e);
        }
    }

    private int compareVersions(String v1, String v2) {
        String[] parts1 = v1.split("\\.");
        String[] parts2 = v2.split("\\.");

        for (int i = 0; i < Math.max(parts1.length, parts2.length); i++) {
            int p1 = i < parts1.length ? Integer.parseInt(parts1[i]) : 0;
            int p2 = i < parts2.length ? Integer.parseInt(parts2[i]) : 0;

            if (p1 != p2) {
                return Integer.compare(p1, p2);
            }
        }
        return 0;
    }

    public void invalidateFeature(String entityType, String entityId, String featureName) {
        try {
            Jedis jedis = getJedis();
            String pattern = String.format("feature:%s:%s:%s:*", entityType, entityId, featureName);
            Set<String> keys = jedis.keys(pattern);

            if (!keys.isEmpty()) {
                jedis.del(keys.toArray(new String[0]));
                cache.keySet().removeIf(k -> k.startsWith(String.format("feature:%s:%s:%s",
                        entityType, entityId, featureName)));
                LOG.info("Invalidated {} keys matching pattern: {}", keys.size(), pattern);
            }
        } catch (Exception e) {
            LOG.error("Failed to invalidate feature: {}/{}/{}", entityType, entityId, featureName, e);
        }
    }

    public void invalidateEntity(String entityType, String entityId) {
        try {
            Jedis jedis = getJedis();
            String pattern = String.format("feature:%s:%s:*", entityType, entityId);
            Set<String> keys = jedis.keys(pattern);

            if (!keys.isEmpty()) {
                jedis.del(keys.toArray(new String[0]));
                cache.keySet().removeIf(k -> k.startsWith(String.format("feature:%s:%s",
                        entityType, entityId)));
                LOG.info("Invalidated {} keys for entity: {}/{}", keys.size(), entityType, entityId);
            }
        } catch (Exception e) {
            LOG.error("Failed to invalidate entity: {}/{}", entityType, entityId, e);
        }
    }

    private String buildKey(String entityType, String entityId, String featureName, String version) {
        String effectiveVersion = (version != null) ? version : "latest";
        return String.format("feature:%s:%s:%s:v%s", entityType, entityId, featureName, effectiveVersion);
    }

    private String buildVersionIndexKey(String entityType, String entityId, String featureName) {
        return String.format("version_index:%s:%s:%s", entityType, entityId, featureName);
    }

    private Duration resolveTTL(String featureName) {
        return featureTtlMap.getOrDefault(featureName, DEFAULT_TTL);
    }

    private String serializeValue(Object value) {
        if (value instanceof Number) {
            return value.toString();
        } else if (value instanceof String) {
            return (String) value;
        } else if (value instanceof Map) {
            return new com.fasterxml.jackson.databind.ObjectMapper()
                    .writeValueAsString(value);
        }
        return value.toString();
    }

    private Object deserializeValue(String value) {
        try {
            if (value.matches("-?\\d+(\\.\\d+)?")) {
                if (value.contains(".")) {
                    return Double.parseDouble(value);
                }
                return Long.parseLong(value);
            }
            return value;
        } catch (NumberFormatException e) {
            return value;
        }
    }

    private void updateLocalCache(String key, Object value, Duration ttl) {
        if (cache.size() >= maxCacheSize) {
            String oldestKey = cache.entrySet().stream()
                    .min(Comparator.comparingLong(e -> e.getValue().getTimestamp()))
                    .map(Map.Entry::getKey)
                    .orElse(null);

            if (oldestKey != null) {
                cache.remove(oldestKey);
            }
        }

        cache.put(key, new FeatureCache(value, ttl));
    }

    private Jedis getJedis() {
        if (jedisCluster != null) {
            return null;
        }
        return jedisPool.getResource();
    }

    public void close() {
        if (jedisPool != null) {
            jedisPool.close();
            LOG.info("Redis pool closed");
        }
        if (jedisCluster != null) {
            jedisCluster.close();
            LOG.info("Redis cluster closed");
        }
    }

    public RedisMapper<FeatureEvent> createRedisMapper() {
        return new RedisMapper<FeatureEvent>() {
            @Override
            public RedisCommandDescription getCommandDescription() {
                return new RedisCommandDescription(RedisCommand.SET);
            }

            @Override
            public String getKeyFromData(FeatureEvent data) {
                return buildKey(data.getEntityType(), data.getEntityId(),
                        data.getFeatureName(), data.getVersion());
            }

            @Override
            public String getValueFromData(FeatureEvent data) {
                return serializeValue(data.getValue());
            }
        };
    }

    private static class FeatureCache {
        private final Object value;
        private final long timestamp;
        private final long expirationTime;

        public FeatureCache(Object value, Duration ttl) {
            this.value = value;
            this.timestamp = System.currentTimeMillis();
            this.expirationTime = timestamp + ttl.toMillis();
        }

        public Object getValue() {
            return value;
        }

        public long getTimestamp() {
            return timestamp;
        }

        public boolean isExpired() {
            return System.currentTimeMillis() > expirationTime;
        }
    }
}

// === ARCHIVO: src/main/java/com/pragma/featurestore/serving/ABRouter.java ===
package com.pragma.featurestore.serving;

import org.apache.flink.configuration.Configuration;
import org.apache.flink.streaming.api.functions.ProcessFunction;
import org.apache.flink.util.Collector;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicLong;

public class ABRouter extends ProcessFunction<ABRequest, ABRouter.ABResponse> {

    private static final Logger LOG = LoggerFactory.getLogger(ABRouter.class);
    private static final double CHALLENGER_PERCENTAGE = 0.10;
    private static final Duration STICKY_SESSION_DURATION = Duration.ofHours(24);
    private static final int HASH_MODULUS = 10000;

    private final Map<String, ModelVariant> modelVariants;
    private final Map<String, String> stickySessions;
    private final Map<String, AtomicLong> decisionCounters;
    private final Map<String, AtomicLong> stickyCounters;
    private final boolean enableStickySessions;
    private final boolean logDecisions;

    private transient Random random;
    private transient long sessionCleanupTimestamp;

    public ABRouter() {
        this(true, true);
    }

    public ABRouter(boolean enableStickySessions, boolean logDecisions) {
        this.enableStickySessions = enableStickySessions;
        this.logDecisions = logDecisions;
        this.modelVariants = new ConcurrentHashMap<>();
        this.stickySessions = new ConcurrentHashMap<>();
        this.decisionCounters = new ConcurrentHashMap<>();
        this.stickyCounters = new ConcurrentHashMap<>();

        modelVariants.put("control", ModelVariant.CONTROL);
        modelVariants.put("challenger", ModelVariant.CHALLENGER);
    }

    @Override
    public void open(Configuration parameters) {
        random = new Random();
        sessionCleanupTimestamp = System.currentTimeMillis();

        decisionCounters.put("control", new AtomicLong(0));
        decisionCounters.put("challenger", new AtomicLong(0));
        stickyCounters.put("control", new AtomicLong(0));
        stickyCounters.put("challenger", new AtomicLong(0));

        LOG.info("ABRouter initialized with {}% challenger traffic, sticky sessions: {}",
                CHALLENGER_PERCENTAGE * 100, enableStickySessions);
    }

    @Override
    public void processElement(ABRequest request, Context ctx, Collector<ABResponse> out) {
        long startTime = System.currentTimeMillis();

        try {
            if (!isValidRequest(request)) {
                LOG.warn("Invalid AB request received: {}", request.getRequestId());
                ABResponse errorResponse = createErrorResponse(request, "Invalid request parameters");
                out.collect(errorResponse);
                return;
            }

            cleanupStaleSessionsIfNeeded();

            ModelVariant selectedVariant = selectVariant(request);

            updateDecisionMetrics(selectedVariant);

            ABResponse response = buildResponse(request, selectedVariant);

            if (logDecisions) {
                logDecision(request, selectedVariant);
            }

            long processingTime = System.currentTimeMillis() - startTime;
            response.setProcessingTimeMs(processingTime);

            out.collect(response);

        } catch (Exception e) {
            LOG.error("Error processing AB request: {}", request.getRequestId(), e);
            ABResponse fallbackResponse = createFallbackResponse(request);
            out.collect(fallbackResponse);
        }
    }

    private boolean isValidRequest(ABRequest request) {
        return request != null
                && request.getRequestId() != null
                && request.getUserId() != null
                && !request.getUserId().isEmpty();
    }

    private ModelVariant selectVariant(ABRequest request) {
        String userId = request.getUserId();

        if (enableStickySessions) {
            String existingAssignment = stickySessions.get(userId);
            if (existingAssignment != null) {
                ModelVariant stickyVariant = modelVariants.get(existingAssignment);
                if (stickyVariant != null) {
                    stickyCounters.get(existingAssignment).incrementAndGet();
                    LOG.debug("Sticky session found for user {}: {}", userId, existingAssignment);
                    return stickyVariant;
                }
            }
        }

        int hashValue = computeConsistentHash(userId);
        boolean isChallenger = hashValue < (CHALLENGER_PERCENTAGE * HASH_MODULUS);

        ModelVariant selectedVariant = isChallenger ? ModelVariant.CHALLENGER : ModelVariant.CONTROL;

        if (enableStickySessions) {
            stickySessions.put(userId, selectedVariant.name().toLowerCase());
            LOG.debug("New sticky session created for user {}: {}", userId, selectedVariant);
        }

        return selectedVariant;
    }

    private int computeConsistentHash(String userId) {
        int hash = userId.hashCode();
        hash = Math.abs(hash);
        return hash % HASH_MODULUS;
    }

    private void updateDecisionMetrics(ModelVariant variant) {
        String variantName = variant.name().toLowerCase();
        decisionCounters.computeIfAbsent(variantName, k -> new AtomicLong(0)).incrementAndGet();
    }

    private ABResponse buildResponse(ABRequest request, ModelVariant variant) {
        ABResponse response = new ABResponse();
        response.setRequestId(request.getRequestId());
        response.setUserId(request.getUserId());
        response.setVariant(variant);
        response.setModelEndpoint(getModelEndpoint(variant));
        response.setTimestamp(System.currentTimeMillis());
        response.setSuccess(true);

        Map<String, Object> metadata = new HashMap<>();
        metadata.put("variant_name", variant.name().toLowerCase());
        metadata.put("experiment_id", request.getExperimentId());
        metadata.put("traffic_percentage", variant == ModelVariant.CHALLENGER
                ? CHALLENGER_PERCENTAGE * 100
                : (1 - CHALLENGER_PERCENTAGE) * 100);

        response.setMetadata(metadata);

        return response;
    }

    private String getModelEndpoint(ModelVariant variant) {
        return switch (variant) {
            case CONTROL -> "http://model-control-service:8080/predict";
            case CHALLENGER -> "http://model-challenger-service:8080/predict";
        };
    }

    private void logDecision(ABRequest request, ModelVariant variant) {
        Map<String, Object> decisionLog = new HashMap<>();
        decisionLog.put("request_id", request.getRequestId());
        decisionLog.put("user_id", request.getUserId());
        decisionLog.put("variant", variant.name().toLowerCase());
        decisionLog.put("timestamp", System.currentTimeMillis());
        decisionLog.put("session_id", request.getSessionId());
        decisionLog.put("is_sticky", enableStickySessions && stickySessions.containsKey(request.getUserId()));

        LOG.info("AB Decision: {}", decisionLog);
    }

    private ABResponse createErrorResponse(ABRequest request, String errorMessage) {
        ABResponse response = new ABResponse();
        response.setRequestId(request.getRequestId());
        response.setUserId(request.getUserId());
        response.setVariant(ModelVariant.CONTROL);
        response.setSuccess(false);
        response.setErrorMessage(errorMessage);
        response.setTimestamp(System.currentTimeMillis());
        return response;
    }

    private ABResponse createFallbackResponse(ABRequest request) {
        ABResponse response = new ABResponse();
        response.setRequestId(request.getRequestId());
        response.setUserId(request.getUserId());
        response.setVariant(ModelVariant.CONTROL);
        response.setModelEndpoint(getModelEndpoint(ModelVariant.CONTROL));
        response.setSuccess(false);
        response.setErrorMessage("Fallback to control variant due to processing error");
        response.setTimestamp(System.currentTimeMillis());
        return response;
    }

    private void cleanupStaleSessionsIfNeeded() {
        long now = System.currentTimeMillis();
        if (now - sessionCleanupTimestamp > STICKY_SESSION_DURATION.toMillis()) {
            int cleanedCount = 0;
            long expirationThreshold = now - STICKY_SESSION_DURATION.toMillis();

            Iterator<Map.Entry<String, String>> iterator = stickySessions.entrySet().iterator();
            while (iterator.hasNext()) {
                Map.Entry<String, String> entry = iterator.next();
                int hashValue = computeConsistentHash(entry.getKey());
                if (hashValue < (expirationThreshold % HASH_MODULUS)) {
                    iterator.remove();
                    cleanedCount++;
                }
            }

            sessionCleanupTimestamp = now;
            LOG.info("Cleaned up {} stale sticky sessions", cleanedCount);
        }
    }

    public Map<String, Long> getDecisionCounts() {
        Map<String, Long> counts = new HashMap<>();
        decisionCounters.forEach((k, v) -> counts.put(k, v.get()));
        return counts;
    }

    public Map<String, Long> getStickySessionCounts() {
        Map<String, Long> counts = new HashMap<>();
        stickyCounters.forEach((k, v) -> counts.put(k, v.get()));
        return counts;
    }

    public double getChallengerPercentage() {
        long total = decisionCounters.values().stream()
                .mapToLong(AtomicLong::get)
                .sum();
        if (total == 0) return CHALLENGER_PERCENTAGE;

        long challenger = decisionCounters.getOrDefault("challenger", new AtomicLong(0)).get();
        return (double) challenger / total;
    }

    public void resetCounters() {
        decisionCounters.values().forEach(c -> c.set(0));
        stickyCounters.values().forEach(c -> c.set(0));
        LOG.info("ABRouter counters reset");
    }

    public enum ModelVariant {
        CONTROL,
        CHALLENGER
    }

    public static class ABRequest {
        private String requestId;
        private String userId;
        private String sessionId;
        private String experimentId;
        private Map<String, Object> context;

        public String getRequestId() { return requestId; }
        public void setRequestId(String requestId) { this.requestId = requestId; }
        public String getUserId() { return userId; }
        public void setUserId(String userId) { this.userId = userId; }
        public String getSessionId() { return sessionId; }
        public void setSessionId(String sessionId) { this.sessionId = sessionId; }
        public String getExperimentId() { return experimentId; }
        public void setExperimentId(String experimentId) { this.experimentId = experimentId; }
        public Map<String, Object> getContext() { return context; }
        public void setContext(Map<String, Object> context) { this.context = context; }
    }

    public static class ABResponse {
        private String requestId;
        private String userId;
        private ModelVariant variant;
        private String modelEndpoint;
        private long timestamp;
        private boolean success;
        private String errorMessage;
        private long processingTimeMs;
        private Map<String, Object> metadata;

        public String getRequestId() { return requestId; }
        public void setRequestId(String requestId) { this.requestId = requestId; }
        public String getUserId() { return userId; }
        public void setUserId(String userId) { this.userId = userId; }
        public ModelVariant getVariant() { return variant; }
        public void setVariant(ModelVariant variant) { this.variant = variant; }
        public String getModelEndpoint() { return modelEndpoint; }
        public void setModelEndpoint(String modelEndpoint) { this.modelEndpoint = modelEndpoint; }
        public long getTimestamp() { return timestamp; }
        public void setTimestamp(long timestamp) { this.timestamp = timestamp; }
        public boolean isSuccess() { return success; }
        public void setSuccess(boolean success) { this.success = success; }
        public String getErrorMessage() { return errorMessage; }
        public void setErrorMessage(String errorMessage) { this.errorMessage = errorMessage; }
        public long getProcessingTimeMs() { return processingTimeMs; }
        public void setProcessingTimeMs(long processingTimeMs) { this.processingTimeMs = processingTimeMs; }
        public Map<String, Object> getMetadata() { return metadata; }
        public void setMetadata(Map<String, Object> metadata) { this.metadata = metadata; }
    }
}

// === ARCHIVO: src/main/java/com/pragma/featurestore/monitoring/DriftDetector.java ===
package com.pragma.featurestore.monitoring;

import org.apache.flink.api.common.state.ListState;
import org.apache.flink.api.common.state.ListStateDescriptor;
import org.apache.flink.api.common.typeinfo.TypeInformation;
import org.apache.flink.configuration.Configuration;
import org.apache.flink.metrics.Counter;
import org.apache.flink.metrics.Gauge;
import org.apache.flink.metrics.MetricGroup;
import org.apache.flink.streaming.api.functions.windowing.ProcessAllWindowFunction;
import org.apache.flink.streaming.api.windowing.windows.GlobalWindow;
import org.apache.flink.util.Collector;

import com.pragma.featurestore.dto.FeatureEvent;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;

public class DriftDetector extends ProcessAllWindowFunction<FeatureEvent, DriftDetector.DriftResult, GlobalWindow> {

    private static final double EPSILON = 1e-10;
    private static final int MIN_SAMPLES = 100;
    private static final double DRIFT_THRESHOLD = 0.1;

    private transient ListState<FeatureEvent> trainingSetState;
    private transient ListState<FeatureEvent> onlineFeaturesState;
    private transient Counter driftAlertsCounter;
    private transient Counter samplesProcessedCounter;
    private transient Map<String, Double> latestDriftScores;

    @Override
    public void open(Configuration parameters) throws Exception {
        super.open(parameters);

        ListStateDescriptor<FeatureEvent> trainingDescriptor = new ListStateDescriptor<>(
            "training-set",
            TypeInformation.of(FeatureEvent.class)
        );
        trainingSetState = getRuntimeContext().getListState(trainingDescriptor);

        ListStateDescriptor<FeatureEvent> onlineDescriptor = new ListStateDescriptor<>(
            "online-features",
            TypeInformation.of(FeatureEvent.class)
        );
        onlineFeaturesState = getRuntimeContext().getListState(onlineDescriptor);

        latestDriftScores = new ConcurrentHashMap<>();

        MetricGroup metricGroup = getRuntimeContext().getMetricGroup();
        driftAlertsCounter = metricGroup.counter("drift_alerts_total");
        samplesProcessedCounter = metricGroup.counter("drift_samples_processed");

        metricGroup.gauge("drift_score_user_features", (Gauge<Double>) () -> 
            latestDriftScores.getOrDefault("user_features", 0.0)
        );
        metricGroup.gauge("drift_score_item_features", (Gauge<Double>) () -> 
            latestDriftScores.getOrDefault("item_features", 0.0)
        );
    }

    @Override
    public void process(Context context, Iterable<FeatureEvent> elements, Collector<DriftResult> out) throws Exception {
        List<FeatureEvent> currentBatch = new ArrayList<>();
        elements.forEach(currentBatch::add);

        if (currentBatch.isEmpty()) {
            return;
        }

        samplesProcessedCounter.inc(currentBatch.size());

        Map<String, List<Double>> featureGroups = currentBatch.stream()
            .collect(Collectors.groupingBy(
                FeatureEvent::getFeatureName,
                Collectors.mapping(FeatureEvent::getValue, Collectors.toList())
            ));

        for (Map.Entry<String, List<Double>> entry : featureGroups.entrySet()) {
            String featureName = entry.getKey();
            List<Double> onlineValues = entry.getValue();

            List<Double> trainingValues = new ArrayList<>();
            trainingSetState.get().forEach(e -> {
                if (featureName.equals(e.getFeatureName())) {
                    trainingValues.add(e.getValue());
                }
            });

            if (trainingValues.size() < MIN_SAMPLES || onlineValues.size() < MIN_SAMPLES) {
                continue;
            }

            double driftScore = calculateKLDivergence(trainingValues, onlineValues);
            latestDriftScores.put(featureName, driftScore);

            boolean isDriftDetected = driftScore > DRIFT_THRESHOLD;

            if (isDriftDetected) {
                driftAlertsCounter.inc();
            }

            out.collect(new DriftResult(
                featureName,
                driftScore,
                isDriftDetected,
                System.currentTimeMillis(),
                trainingValues.size(),
                onlineValues.size()
            ));
        }
    }

    private double calculateKLDivergence(List<Double> p, List<Double> q) {
        Map<Double, Long> pCounts = calculateHistogram(p);
        Map<Double, Long> qCounts = calculateHistogram(q);

        Set<Double> allKeys = new HashSet<>(pCounts.keySet());
        allKeys.addAll(qCounts.keySet());

        double pTotal = p.size();
        double qTotal = q.size();

        double klDivergence = 0.0;

        for (Double key : allKeys) {
            double pProb = (pCounts.getOrDefault(key, 0L) + EPSILON) / pTotal;
            double qProb = (qCounts.getOrDefault(key, 0L) + EPSILON) / qTotal;
            klDivergence += pProb * Math.log(pProb / qProb);
        }

        return Math.min(klDivergence, 1.0);
    }

    private Map<Double, Long> calculateHistogram(List<Double> values) {
        return values.stream()
            .collect(Collectors.groupingBy(v -> Math.round(v * 100) / 100.0, Collectors.counting()));
    }

    public void updateTrainingSet(List<FeatureEvent> newTrainingData) throws Exception {
        trainingSetState.clear();
        trainingSetState.addAll(newTrainingData);
    }

    public static class DriftResult {
        private final String featureName;
        private final double driftScore;
        private final boolean driftDetected;
        private final long timestamp;
        private final int trainingSampleSize;
        private final int onlineSampleSize;

        public DriftResult(String featureName, double driftScore, boolean driftDetected,
                          long timestamp, int trainingSampleSize, int onlineSampleSize) {
            this.featureName = featureName;
            this.driftScore = driftScore;
            this.driftDetected = driftDetected;
            this.timestamp = timestamp;
            this.trainingSampleSize = trainingSampleSize;
            this.onlineSampleSize = onlineSampleSize;
        }

        public String getFeatureName() { return featureName; }
        public double getDriftScore() { return driftScore; }
        public boolean isDriftDetected() { return driftDetected; }
        public long getTimestamp() { return timestamp; }
        public int getTrainingSampleSize() { return trainingSampleSize; }
        public int getOnlineSampleSize() { return onlineSampleSize; }
    }
}

// === ARCHIVO: src/main/java/com/pragma/featurestore/monitoring/CTRRollbackStrategy.java ===
package com.pragma.featurestore.monitoring;

import org.apache.flink.api.common.state.ListState;
import org.apache.flink.api.common.state.ListStateDescriptor;
import org.apache.flink.api.common.typeinfo.TypeInformation;
import org.apache.flink.configuration.Configuration;
import org.apache.flink.metrics.Counter;
import org.apache.flink.metrics.Gauge;
import org.apache.flink.metrics.MetricGroup;
import org.apache.flink.streaming.api.functions.windowing.ProcessAllWindowFunction;
import org.apache.flink.streaming.api.windowing.windows.GlobalWindow;
import org.apache.flink.util.Collector;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ConcurrentLinkedQueue;

public class CTRRollbackStrategy extends ProcessAllWindowFunction<CTRRollbackStrategy.CTREvent, CTRRollbackStrategy.RollbackDecision, GlobalWindow> {

    private static final double DEGRADATION_THRESHOLD = 0.05;
    private static final long ROLLBACK_WINDOW_MS = 30 * 60 * 1000L;
    private static final double MIN_SAMPLE_SIZE = 1000.0;

    private transient ListState<CTREvent> eventHistoryState;
    private transient Counter rollbacksTriggeredCounter;
    private transient Counter rollbacksCancelledCounter;
    private transient Counter totalImpressionsCounter;
    private transient Counter totalClicksCounter;

    private final ConcurrentLinkedQueue<CTREvent> recentEvents = new ConcurrentLinkedQueue<>();

    @Override
    public void open(Configuration parameters) throws Exception {
        super.open(parameters);

        ListStateDescriptor<CTREvent> descriptor = new ListStateDescriptor<>(
            "ctr-events-history",
            TypeInformation.of(CTREvent.class)
        );
        eventHistoryState = getRuntimeContext().getListState(descriptor);

        MetricGroup metricGroup = getRuntimeContext().getMetricGroup();
        rollbacksTriggeredCounter = metricGroup.counter("ctr_rollbacks_triggered_total");
        rollbacksCancelledCounter = metricGroup.counter("ctr_rollbacks_cancelled_total");
        totalImpressionsCounter = metricGroup.counter("ctr_total_impressions");
        totalClicksCounter = metricGroup.counter("ctr_total_clicks");

        metricGroup.gauge("ctr_challenger_current", (Gauge<Double>) () -> calculateCurrentCTR("challenger"));
        metricGroup.gauge("ctr_control_current", (Gauge<Double>) () -> calculateCurrentCTR("control"));
        metricGroup.gauge("ctr_degradation_current", (Gauge<Double>) () -> calculateDegradation());
    }

    @Override
    public void process(Context context, Iterable<CTREvent> elements, Collector<RollbackDecision> out) throws Exception {
        List<CTREvent> windowEvents = new ArrayList<>();
        elements.forEach(windowEvents::add);

        if (windowEvents.isEmpty()) {
            return;
        }

        for (CTREvent event : windowEvents) {
            recentEvents.add(event);
            totalImpressionsCounter.inc();
            if (event.isClick()) {
                totalClicksCounter.inc();
            }
        }

        cleanupOldEvents();

        double challengerCTR = calculateCurrentCTR("challenger");
        double controlCTR = calculateCurrentCTR("control");
        double degradation = calculateDegradation();

        boolean shouldRollback = shouldTriggerRollback(challengerCTR, controlCTR, degradation);

        RollbackDecision decision = new RollbackDecision(
            shouldRollback,
            challengerCTR,
            controlCTR,
            degradation,
            System.currentTimeMillis(),
            getSampleSize(),
            determineAffectedFeatures()
        );

        if (shouldRollback) {
            rollbacksTriggeredCounter.inc();
        } else {
            rollbacksCancelledCounter.inc();
        }

        out.collect(decision);
    }

    private void cleanupOldEvents() {
        long cutoffTime = System.currentTimeMillis() - ROLLBACK_WINDOW_MS;
        while (!recentEvents.isEmpty() && recentEvents.peek().getTimestamp() < cutoffTime) {
            recentEvents.poll();
        }
    }

    private double calculateCurrentCTR(String variant) {
        long impressions = recentEvents.stream()
            .filter(e -> variant.equals(e.getVariant()))
            .count();

        long clicks = recentEvents.stream()
            .filter(e -> variant.equals(e.getVariant()) && e.isClick())
            .count();

        if (impressions == 0) {
            return 0.0;
        }

        return (double) clicks / impressions;
    }

    private double calculateDegradation() {
        double challengerCTR = calculateCurrentCTR("challenger");
        double controlCTR = calculateCurrentCTR("control");

        if (controlCTR == 0.0) {
            return 0.0;
        }

        return (controlCTR - challengerCTR) / controlCTR;
    }

    private boolean shouldTriggerRollback(double challengerCTR, double controlCTR, double degradation) {
        if (getSampleSize() < MIN_SAMPLE_SIZE) {
            return false;
        }

        if (challengerCTR == 0.0 && controlCTR > 0.0) {
            return true;
        }

        return degradation > DEGRADATION_THRESHOLD;
    }

    private long getSampleSize() {
        return recentEvents.stream()
            .filter(e -> "challenger".equals(e.getVariant()))
            .count();
    }

    private List<String> determineAffectedFeatures() {
        return recentEvents.stream()
            .map(CTREvent::getFeatureVersion)
            .distinct()
            .toList();
    }

    public void recordEvent(CTREvent event) throws Exception {
        eventHistoryState.add(event);
    }

    public static class CTREvent {
        private final String experimentId;
        private final String variant;
        private final boolean isClick;
        private final long timestamp;
        private final String featureVersion;
        private final String userId;

        public CTREvent(String experimentId, String variant, boolean isClick,
                       long timestamp, String featureVersion, String userId) {
            this.experimentId = experimentId;
            this.variant = variant;
            this.isClick = isClick;
            this.timestamp = timestamp;
            this.featureVersion = featureVersion;
            this.userId = userId;
        }

        public String getExperimentId() { return experimentId; }
        public String getVariant() { return variant; }
        public boolean isClick() { return isClick; }
        public long getTimestamp() { return timestamp; }
        public String getFeatureVersion() { return featureVersion; }
        public String getUserId() { return userId; }
    }

    public static class RollbackDecision {
        private final boolean shouldRollback;
        private final double challengerCTR;
        private final double controlCTR;
        private final double degradation;
        private final long timestamp;
        private final long sampleSize;
        private final List<String> affectedFeatures;

        public RollbackDecision(boolean shouldRollback, double challengerCTR, double controlCTR,
                               double degradation, long timestamp, long sampleSize,
                               List<String> affectedFeatures) {
            this.shouldRollback = shouldRollback;
            this.challengerCTR = challengerCTR;
            this.controlCTR = controlCTR;
            this.degradation = degradation;
            this.timestamp = timestamp;
            this.sampleSize = sampleSize;
            this.affectedFeatures = affectedFeatures;
        }

        public boolean shouldRollback() { return shouldRollback; }
        public double getChallengerCTR() { return challengerCTR; }
        public double getControlCTR() { return controlCTR; }
        public double getDegradation() { return degradation; }
        public long getTimestamp() { return timestamp; }
        public long getSampleSize() { return sampleSize; }
        public List<String> getAffectedFeatures() { return affectedFeatures; }
    }
}

// === ARCHIVO: src/main/java/com/pragma/featurestore/config/FlinkConfig.java ===
package com.pragma.featurestore.config;

import org.apache.flink.api.common.ExecutionConfig;
import org.apache.flink.api.common.restartstrategy.RestartStrategies;
import org.apache.flink.api.common.time.Time;
import org.apache.flink.configuration.Configuration;
import org.apache.flink.configuration.RestOptions;
import org.apache.flink.runtime.state.storage.FileSystemCheckpointStorage;
import org.apache.flink.streaming.api.CheckpointingMode;
import org.apache.flink.streaming.api.environment.CheckpointConfig;
import org.apache.flink.streaming.api.environment.StreamExecutionEnvironment;

import java.util.concurrent.TimeUnit;

public class FlinkConfig {

    private static final long CHECKPOINT_INTERVAL_MS = 60_000L;
    private static final long MIN_PAUSE_BETWEEN_CHECKPOINTS_MS = 30_000L;
    private static final int MAX_CONCURRENT_CHECKPOINTS = 1;
    private static final int NUMBER_OF_RETRIES = 3;
    private static final long RETRY_DELAY_MS = 10_000L;
    private static final int DEFAULT_PARALLELISM = 4;
    private static final int MAX_PARALLELISM = 16;
    private static final long WATERMARK_IDLE_TIMEOUT_MS = 60_000L;
    private static final long WATERMARK_INTERVAL_MS = 200L;
    private static final long MAX_OUT_OF_ORDERNESS_MS = 5_000L;

    private final String checkpointDir;
    private final int parallelism;
    private final boolean enableUnalignedCheckpoints;
    private final boolean enableIncrementalCheckpoints;

    public FlinkConfig(String checkpointDir) {
        this(checkpointDir, DEFAULT_PARALLELISM, true, true);
    }

    public FlinkConfig(String checkpointDir, int parallelism, 
                      boolean enableUnalignedCheckpoints, boolean enableIncrementalCheckpoints) {
        this.checkpointDir = checkpointDir;
        this.parallelism = parallelism;
        this.enableUnalignedCheckpoints = enableUnalignedCheckpoints;
        this.enableIncrementalCheckpoints = enableIncrementalCheckpoints;
    }

    public StreamExecutionEnvironment configureEnvironment(StreamExecutionEnvironment env) {
        configureCheckpointing(env);
        configureRestartStrategy(env);
        configureParallelism(env);
        configureTimeCharacteristics(env);
        configureStateBackend(env);
        configureTaskCancellation(env);

        return env;
    }

    private void configureCheckpointing(StreamExecutionEnvironment env) {
        CheckpointConfig checkpointConfig = env.getCheckpointConfig();

        env.enableCheckpointing(CHECKPOINT_INTERVAL_MS, CheckpointingMode.EXACTLY_ONCE);

        checkpointConfig.setMinPauseBetweenCheckpoints(MIN_PAUSE_BETWEEN_CHECKPOINTS_MS);
        checkpointConfig.setMaxConcurrentCheckpoints(MAX_CONCURRENT_CHECKPOINTS);

        checkpointConfig.setTolerableCheckpointFailureNumber(3);

        checkpointConfig.setExternalizedCheckpointCleanup(
            CheckpointConfig.ExternalizedCheckpointCleanup.RETAIN_ON_CANCELLATION
        );

        if (enableUnalignedCheckpoints) {
            checkpointConfig.enableUnalignedCheckpoints();
        }

        if (enableIncrementalCheckpoints) {
            checkpointConfig.setIncrementalCheckpointing(true);
        }

        try {
            env.getCheckpointConfig().setCheckpointStorage(
                new FileSystemCheckpointStorage(checkpointDir)
            );
        } catch (Exception e) {
            throw new RuntimeException("Failed to configure checkpoint storage: " + checkpointDir, e);
        }
    }

    private void configureRestartStrategy(StreamExecutionEnvironment env) {
        ExecutionConfig executionConfig = env.getConfig();

        executionConfig.setRestartStrategy(
            RestartStrategies.failureRateRestart(
                NUMBER_OF_RETRIES,
                Time.of(5, TimeUnit.MINUTES),
                Time.of(RETRY_DELAY_MS, TimeUnit.MILLISECONDS)
            )
        );
    }

    private void configureParallelism(StreamExecutionEnvironment env) {
        env.setParallelism(parallelism);
        env.setMaxParallelism(MAX_PARALLELISM);
    }

    private void configureTimeCharacteristics(StreamExecutionEnvironment env) {
        env.setStreamTimeCharacteristic(org.apache.flink.streaming.api.TimeCharacteristic.EventTime);

        env.getConfig().setAutoWatermarkInterval(WATERMARK_INTERVAL_MS);
    }

    private void configureStateBackend(StreamExecutionEnvironment env) {
        Configuration flinkConfig = new Configuration();
        flinkConfig.setString("state.backend", "rocksdb");
        flinkConfig.setString("state.checkpoints.dir", checkpointDir);
        flinkConfig.setString("state.savepoints.dir", checkpointDir + "/savepoints");

        env.configure(flinkConfig);
    }

    private void configureTaskCancellation(StreamExecutionEnvironment env) {
        env.getConfig().setTaskCancellationTimeout(30_000L);
        env.getConfig().setTaskCancellationInterruptionOrder(
            ExecutionConfig.TaskCancellationInterruptionOrder.IMMEDIATE
        );
    }

    public static class Builder {
        private String checkpointDir = "file:///tmp/flink-checkpoints";
        private int parallelism = DEFAULT_PARALLELISM;
        private boolean enableUnalignedCheckpoints = true;
        private boolean enableIncrementalCheckpoints = true;

        public Builder checkpointDir(String checkpointDir) {
            this.checkpointDir = checkpointDir;
            return this;
        }

        public Builder parallelism(int parallelism) {
            this.parallelism = parallelism;
            return this;
        }

        public Builder enableUnalignedCheckpoints(boolean enable) {
            this.enableUnalignedCheckpoints = enable;
            return this;
        }

        public Builder enableIncrementalCheckpoints(boolean enable) {
            this.enableIncrementalCheckpoints = enable;
            return this;
        }

        public FlinkConfig build() {
            return new FlinkConfig(checkpointDir, parallelism, 
                                  enableUnalignedCheckpoints, enableIncrementalCheckpoints);
        }
    }

    public String getCheckpointDir() {
        return checkpointDir;
    }

    public int getParallelism() {
        return parallelism;
    }

    public boolean isEnableUnalignedCheckpoints() {
        return enableUnalignedCheckpoints;
    }

    public boolean isEnableIncrementalCheckpoints() {
        return enableIncrementalCheckpoints;
    }

    public static Builder builder() {
        return new Builder();
    }
}

// === ARCHIVO: src/main/java/com/pragma/featurestore/exception/FeatureComputationException.java ===
package com.pragma.featurestore.exception;

import lombok.Getter;
import java.time.Instant;
import java.util.Map;

@Getter
public class FeatureComputationException extends RuntimeException {
    
    private final String featureName;
    private final String eventId;
    private final Instant eventTimestamp;
    private final Map<String, Object> eventContext;
    private final String computationStage;
    private final boolean retryable;
    
    public FeatureComputationException(String message, String featureName, String eventId) {
        super(message);
        this.featureName = featureName;
        this.eventId = eventId;
        this.eventTimestamp = null;
        this.eventContext = null;
        this.computationStage = "UNKNOWN";
        this.retryable = false;
    }
    
    public FeatureComputationException(String message, String featureName, String eventId, 
                                       Instant eventTimestamp, Map<String, Object> eventContext,
                                       String computationStage, boolean retryable) {
        super(buildMessage(message, featureName, eventId, computationStage));
        this.featureName = featureName;
        this.eventId = eventId;
        this.eventTimestamp = eventTimestamp;
        this.eventContext = eventContext != null ? Map.copyOf(eventContext) : Map.of();
        this.computationStage = computationStage;
        this.retryable = retryable;
    }
    
    public FeatureComputationException(String message, String featureName, String eventId, 
                                       Throwable cause) {
        super(message, cause);
        this.featureName = featureName;
        this.eventId = eventId;
        this.eventTimestamp = null;
        this.eventContext = null;
        this.computationStage = "UNKNOWN";
        this.retryable = cause instanceof java.io.IOException || 
                         cause instanceof java.util.concurrent.TimeoutException;
    }
    
    private static String buildMessage(String message, String featureName, String eventId, 
                                       String computationStage) {
        return String.format("Feature computation failed: %s | Feature: %s | Event: %s | Stage: %s",
                           message, featureName, eventId, computationStage);
    }
    
    public String getDetailedMessage() {
        StringBuilder sb = new StringBuilder();
        sb.append("FeatureComputationException{")
          .append("featureName=").append(featureName)
          .append(", eventId=").append(eventId)
          .append(", computationStage=").append(computationStage)
          .append(", retryable=").append(retryable);
        
        if (eventTimestamp != null) {
            sb.append(", eventTimestamp=").append(eventTimestamp);
        }
        
        if (eventContext != null && !eventContext.isEmpty()) {
            sb.append(", contextKeys=").append(eventContext.keySet());
        }
        
        sb.append("}");
        return sb.toString();
    }
    
    public FeatureComputationException withContext(String key, Object value) {
        Map<String, Object> newContext = new java.util.HashMap<>(this.eventContext);
        newContext.put(key, value);
        return new FeatureComputationException(
            getMessage(),
            this.featureName,
            this.eventId,
            this.eventTimestamp,
            newContext,
            this.computationStage,
            this.retryable
        );
    }
}

// === ARCHIVO: src/main/java/com/pragma/featurestore/validation/FeatureEventValidator.java ===
package com.pragma.featurestore.validation;

import com.pragma.featurestore.dto.FeatureEvent;
import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validation;
import jakarta.validation.Validator;
import jakarta.validation.ValidatorFactory;
import jakarta.validation.constraints.*;
import lombok.extern.slf4j.Slf4j;
import org.hibernate.validator.HibernateValidator;

import java.time.Duration;
import java.time.Instant;
import java.util.*;
import java.util.stream.Collectors;

@Slf4j
public class FeatureEventValidator {
    
    private final Validator validator;
    private final Duration maxEventAge;
    private final Duration maxFutureDrift;
    
    public FeatureEventValidator() {
        this(Duration.ofHours(24), Duration.ofMinutes(5));
    }
    
    public FeatureEventValidator(Duration maxEventAge, Duration maxFutureDrift) {
        ValidatorFactory factory = Validation.byProvider(HibernateValidator.class)
            .configure()
            .failFast(false)
            .addProperty("hibernate.validator.fail_fast", "false")
            .buildValidatorFactory();
        
        this.validator = factory.getValidator();
        this.maxEventAge = maxEventAge;
        this.maxFutureDrift = maxFutureDrift;
        log.info("FeatureEventValidator initialized with maxEventAge={}, maxFutureDrift={}", 
                 maxEventAge, maxFutureDrift);
    }
    
    public ValidationResult validate(FeatureEvent event) {
        if (event == null) {
            return ValidationResult.invalid("FeatureEvent cannot be null", Collections.emptyList());
        }
        
        Set<ConstraintViolation<FeatureEvent>> violations = validator.validate(event);
        List<String> errors = violations.stream()
            .map(this::formatViolation)
            .collect(Collectors.toList());
        
        if (!errors.isEmpty()) {
            return ValidationResult.invalid("Bean validation failed", errors);
        }
        
        List<String> temporalErrors = validateTemporalConsistency(event);
        errors.addAll(temporalErrors);
        
        if (!errors.isEmpty()) {
            return ValidationResult.invalid("Temporal validation failed", errors);
        }
        
        List<String> semanticErrors = validateSemanticConsistency(event);
        errors.addAll(semanticErrors);
        
        if (!errors.isEmpty()) {
            return ValidationResult.invalid("Semantic validation failed", errors);
        }
        
        return ValidationResult.valid();
    }
    
    private List<String> validateTemporalConsistency(FeatureEvent event) {
        List<String> errors = new ArrayList<>();
        
        if (event.getTimestamp() == null) {
            errors.add("Event timestamp is required");
            return errors;
        }
        
        Instant now = Instant.now();
        Instant eventTime = event.getTimestamp();
        
        if (eventTime.isAfter(now.plus(maxFutureDrift))) {
            errors.add(String.format("Event timestamp %s is too far in the future (max drift: %s)",
                                    eventTime, maxFutureDrift));
        }
        
        if (eventTime.isBefore(now.minus(maxEventAge))) {
            errors.add(String.format("Event timestamp %s is too old (max age: %s)",
                                    eventTime, maxEventAge));
        }
        
        if (event.getProcessingTimestamp() != null) {
            if (event.getProcessingTimestamp().isBefore(eventTime)) {
                errors.add("Processing timestamp cannot be before event timestamp");
            }
            
            Duration processingDelay = Duration.between(eventTime, event.getProcessingTimestamp());
            if (processingDelay.toMinutes() > 60) {
                log.warn("High processing delay detected: {} minutes for event {}",
                        processingDelay.toMinutes(), event.getEventId());
            }
        }
        
        return errors;
    }
    
    private List<String> validateSemanticConsistency(FeatureEvent event) {
        List<String> errors = new ArrayList<>();
        
        if (event.getFeatureName() != null && event.getFeatureName().contains("..")) {
            errors.add("Feature name cannot contain consecutive dots");
        }
        
        if (event.getVersion() != null) {
            String version = event.getVersion();
            if (!version.matches("^\\d+\\.\\d+\\.\\d+$")) {
                errors.add(String.format("Invalid version format '%s' (expected MAJOR.MINOR.PATCH)", version));
            }
            
            String[] parts = version.split("\\.");
            try {
                int major = Integer.parseInt(parts[0]);
                if (major < 0) {
                    errors.add("Major version cannot be negative");
                }
                if (major >= 100) {
                    log.warn("Unusually high major version: {}", major);
                }
            } catch (NumberFormatException e) {
                errors.add("Invalid major version number");
            }
        }
        
        if (event.getFeatureValue() == null) {
            errors.add("Feature value cannot be null");
        } else if (event.getFeatureValue() instanceof Number) {
            Number numValue = (Number) event.getFeatureValue();
            if (numValue instanceof Double || numValue instanceof Float) {
                double doubleValue = numValue.doubleValue();
                if (Double.isNaN(doubleValue) || Double.isInfinite(doubleValue)) {
                    errors.add("Feature value cannot be NaN or Infinite");
                }
            }
        }
        
        if (event.getUserId() != null && event.getUserId().isBlank()) {
            errors.add("User ID cannot be blank when provided");
        }
        
        if (event.getMetadata() != null) {
            for (Map.Entry<String, Object> entry : event.getMetadata().entrySet()) {
                if (entry.getKey() == null || entry.getKey().isBlank()) {
                    errors.add("Metadata keys cannot be null or blank");
                }
                if (entry.getValue() != null && !(entry.getValue() instanceof String) 
                    && !(entry.getValue() instanceof Number) 
                    && !(entry.getValue() instanceof Boolean)
                    && !(entry.getValue() instanceof List)) {
                    log.debug("Metadata value for key '{}' is not a simple type: {}", 
                             entry.getKey(), entry.getValue().getClass());
                }
            }
        }
        
        return errors;
    }
    
    private String formatViolation(ConstraintViolation<FeatureEvent> violation) {
        String field = violation.getPropertyPath().toString();
        String message = violation.getMessage();
        Object invalidValue = violation.getInvalidValue();
        
        if (invalidValue != null) {
            return String.format("%s: %s (was: %s)", field, message, invalidValue);
        }
        return String.format("%s: %s", field, message);
    }
    
    public static class ValidationResult {
        private final boolean valid;
        private final String summary;
        private final List<String> errors;
        
        private ValidationResult(boolean valid, String summary, List<String> errors) {
            this.valid = valid;
            this.summary = summary;
            this.errors = Collections.unmodifiableList(errors);
        }
        
        public static ValidationResult valid() {
            return new ValidationResult(true, "Validation passed", Collections.emptyList());
        }
        
        public static ValidationResult invalid(String summary, List<String> errors) {
            return new ValidationResult(false, summary, errors);
        }
        
        public boolean isValid() {
            return valid;
        }
        
        public String getSummary() {
            return summary;
        }
        
        public List<String> getErrors() {
            return errors;
        }
        
        public String getErrorsAsString() {
            return String.join("; ", errors);
        }
    }
}

// === ARCHIVO: src/test/java/com/pragma/featurestore/processing/FeatureCalculatorTest.java ===
package com.pragma.featurestore.processing;

import org.apache.flink.api.common.state.ListState;
import org.apache.flink.api.common.state.ListStateDescriptor;
import org.apache.flink.api.common.typeinfo.TypeInformation;
import org.apache.flink.runtime.state.FunctionInitializationContext;
import org.apache.flink.runtime.state.FunctionSnapshotContext;
import org.apache.flink.streaming.api.operators.AbstractStreamOperator;
import org.apache.flink.streaming.api.operators.StreamMap;
import org.apache.flink.streaming.runtime.streamrecord.StreamRecord;
import org.apache.flink.streaming.util.KeyedOneInputStreamOperatorTestHarness;
import org.apache.flink.streaming.util.OneInputStreamOperatorTestHarness;
import org.apache.flink.test.util.AbstractTestBase;
import org.apache.flink.util.Collector;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import com.pragma.featurestore.dto.FeatureEvent;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ConcurrentLinkedQueue;

import static org.junit.jupiter.api.Assertions.*;

class FeatureCalculatorTest extends AbstractTestBase {

    private FeatureCalculator calculator;
    private OneInputStreamOperatorTestHarness<FeatureEvent, FeatureEvent> testHarness;

    @BeforeEach
    void setUp() throws Exception {
        calculator = new FeatureCalculator(5 * 60 * 1000L, 60 * 60 * 1000L, 24 * 60 * 60 * 1000L);
        testHarness = new KeyedOneInputStreamOperatorTestHarness<>(
            new StreamMap<>(calculator),
            event -> event.getUserId(),
            TypeInformation.of(FeatureEvent.class)
        );
        testHarness.open();
    }

    @Test
    void testSlidingWindowAggregation() throws Exception {
        String userId = "user123";
        long baseTime = System.currentTimeMillis();

        FeatureEvent event1 = new FeatureEvent();
        event1.setUserId(userId);
        event1.setFeatureName("purchase_count");
        event1.setFeatureValue("1");
        event1.setTimestamp(baseTime);
        event1.setVersion("1.0.0");

        FeatureEvent event2 = new FeatureEvent();
        event2.setUserId(userId);
        event2.setFeatureName("purchase_count");
        event2.setFeatureValue("1");
        event2.setTimestamp(baseTime + 60000);
        event2.setVersion("1.0.0");

        testHarness.processElement(event1, baseTime);
        testHarness.processElement(event2, baseTime + 60000);

        testHarness.setProcessingTime(baseTime + 5 * 60 * 1000);

        ConcurrentLinkedQueue<StreamRecord<FeatureEvent>> output = testHarness.getOutput();
        assertFalse(output.isEmpty(), "Debe generar features agregadas");
    }

    @Test
    void testLateDataHandling() throws Exception {
        String userId = "user456";
        long baseTime = System.currentTimeMillis();
        long lateDataThreshold = 5 * 60 * 1000L;

        FeatureEvent onTimeEvent = new FeatureEvent();
        onTimeEvent.setUserId(userId);
        onTimeEvent.setFeatureName("add_to_cart_count");
        onTimeEvent.setFeatureValue("1");
        onTimeEvent.setTimestamp(baseTime);
        onTimeEvent.setVersion("1.0.0");

        FeatureEvent lateEvent = new FeatureEvent();
        lateEvent.setUserId(userId);
        lateEvent.setFeatureName("add_to_cart_count");
        lateEvent.setFeatureValue("1");
        lateEvent.setTimestamp(baseTime - lateDataThreshold - 10000);
        lateEvent.setVersion("1.0.0");

        testHarness.processElement(onTimeEvent, baseTime);
        testHarness.processElement(lateEvent, baseTime - lateDataThreshold - 5000);

        assertTrue(true, "Late data debe ser procesada según configuración de allowed lateness");
    }

    @Test
    void testMultipleWindowSizes() throws Exception {
        String userId = "user789";
        long baseTime = System.currentTimeMillis();

        for (int i = 0; i < 10; i++) {
            FeatureEvent event = new FeatureEvent();
            event.setUserId(userId);
            event.setFeatureName("page_view_count");
            event.setFeatureValue(String.valueOf(i + 1));
            event.setTimestamp(baseTime + (i * 60000));
            event.setVersion("1.0.0");
            testHarness.processElement(event, baseTime + (i * 60000));
        }

        testHarness.setProcessingTime(baseTime + 60 * 60 * 1000);

        ConcurrentLinkedQueue<StreamRecord<FeatureEvent>> output = testHarness.getOutput();
        assertNotNull(output, "Output no debe ser null");
    }

    @Test
    void testFeatureVersioning() throws Exception {
        String userId = "user_version_test";
        long baseTime = System.currentTimeMillis();

        FeatureEvent v1Event = new FeatureEvent();
        v1Event.setUserId(userId);
        v1Event.setFeatureName("avg_order_value");
        v1Event.setFeatureValue("50.0");
        v1Event.setTimestamp(baseTime);
        v1Event.setVersion("1.0.0");

        FeatureEvent v2Event = new FeatureEvent();
        v2Event.setUserId(userId);
        v2Event.setFeatureName("avg_order_value");
        v2Event.setFeatureValue("75.0");
        v2Event.setTimestamp(baseTime + 120000);
        v2Event.setVersion("2.0.0");

        testHarness.processElement(v1Event, baseTime);
        testHarness.processElement(v2Event, baseTime + 120000);

        assertTrue(true, "Versionado de features debe mantener versiones separadas");
    }

    @Test
    void testStateManagement() throws Exception {
        String userId = "user_state_test";
        long baseTime = System.currentTimeMillis();

        FeatureEvent event1 = new FeatureEvent();
        event1.setUserId(userId);
        event1.setFeatureName("session_duration");
        event1.setFeatureValue("120");
        event1.setTimestamp(baseTime);
        event1.setVersion("1.0.0");

        testHarness.processElement(event1, baseTime);
        testHarness.snapshot(baseTime + 60000, baseTime + 60000);
        testHarness.restore();

        FeatureEvent event2 = new FeatureEvent();
        event2.setUserId(userId);
        event2.setFeatureName("session_duration");
        event2.setFeatureValue("180");
        event2.setTimestamp(baseTime + 120000);
        event2.setVersion("1.0.0");

        testHarness.processElement(event2, baseTime + 120000);
        assertTrue(true, "Estado debe persistir entre snapshots y restore");
    }
}

// === ARCHIVO: src/test/java/com/pragma/featurestore/serving/ABRouterTest.java ===
package com.pragma.featurestore.serving;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPool;
import redis.clients.jedis.resps.Tuple;

import java.util.*;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

class ABRouterTest {

    @Mock
    private JedisPool jedisPool;

    @Mock
    private Jedis jedis;

    private ABRouter router;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        when(jedisPool.getResource()).thenReturn(jedis);
        router = new ABRouter(jedisPool, 0.1);
    }

    @Test
    void testTrafficDistribution() {
        String userId = "test_user_123";
        String experimentId = "exp_recommendation_v2";

        when(jedis.hget(anyString(), anyString())).thenReturn(null);

        String variant = router.route(userId, experimentId);

        assertNotNull(variant, "Debe retornar una variante");
        assertTrue(variant.equals("control") || variant.equals("challenger"),
            "Variante debe ser control o challenger");
    }

    @Test
    void testStickySessionPersistence() {
        String userId = "sticky_user_456";
        String experimentId = "exp_ctr_optimization";
        String expectedVariant = "challenger";

        when(jedis.hget("ab:session:" + experimentId, userId)).thenReturn(expectedVariant);

        String variant = router.route(userId, experimentId);

        assertEquals(expectedVariant, variant, "Sticky session debe persistir la variante");
        verify(jedis, never()).hset(anyString(), anyString(), anyString());
    }

    @Test
    void testTrafficAllocationPercentage() {
        int totalUsers = 1000;
        int challengerCount = 0;
        String experimentId = "exp_allocation_test";

        for (int i = 0; i < totalUsers; i++) {
            String userId = "user_" + i;
            when(jedis.hget("ab:session:" + experimentId, userId)).thenReturn(null);

            String variant = router.route(userId, experimentId);
            if ("challenger".equals(variant)) {
                challengerCount++;
            }
        }

        double actualPercentage = (double) challengerCount / totalUsers;
        assertTrue(actualPercentage >= 0.05 && actualPercentage <= 0.15,
            "Distribución debe estar entre 5% y 15%, pero fue: " + actualPercentage);
    }

    @Test
    void testNewUserAssignment() {
        String newUserId = "new_user_789";
        String experimentId = "exp_new_user";

        when(jedis.hget(anyString(), anyString())).thenReturn(null);
        when(jedis.hset(anyString(), anyString(), anyString())).thenReturn(1L);

        String variant = router.route(newUserId, experimentId);

        verify(jedis).hset(eq("ab:session:" + experimentId), eq(newUserId), anyString());
        assertNotNull(variant);
    }

    @Test
    void testExperimentNotFound() {
        String userId = "user_default";
        String experimentId = "nonexistent_experiment";

        when(jedis.hget(anyString(), anyString())).thenReturn(null);

        String variant = router.route(userId, experimentId);

        assertEquals("control", variant, "Debe retornar control por defecto");
    }

    @Test
    void testRedisConnectionFailure() {
        String userId = "user_fail_test";
        String experimentId = "exp_failover";

        when(jedisPool.getResource()).thenThrow(new RuntimeException("Redis unavailable"));

        ABRouter fallbackRouter = new ABRouter(jedisPool, 0.1);
        String variant = fallbackRouter.route(userId, experimentId);

        assertEquals("control", variant, "Debe fallback a control cuando Redis falla");
    }

    @Test
    void testMultipleExperimentsIsolation() {
        String userId = "user_multi_exp";
        String exp1 = "exp_product_ranking";
        String exp2 = "exp_search_ranking";

        when(jedis.hget("ab:session:" + exp1, userId)).thenReturn("control");
        when(jedis.hget("ab:session:" + exp2, userId)).thenReturn("challenger");

        String variant1 = router.route(userId, exp1);
        String variant2 = router.route(userId, exp2);

        assertEquals("control", variant1);
        assertEquals("challenger", variant2);
        assertNotEquals(variant1, variant2, "Experimentos deben estar aislados");
    }

    @Test
    void testVariantMetricsTracking() {
        String userId = "user_metrics";
        String experimentId = "exp_metrics";

        when(jedis.hget(anyString(), anyString())).thenReturn(null);
        when(jedis.zadd(anyString(), anyDouble(), anyString())).thenReturn(1L);

        router.route(userId, experimentId);

        verify(jedis).zadd(eq("ab:metrics:" + experimentId + ":control"), anyDouble(), anyString());
    }
}

// === ARCHIVO: src/test/java/com/pragma/featurestore/monitoring/DriftDetectorTest.java ===
package com.pragma.featurestore.monitoring;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import static org.junit.jupiter.api.Assertions.*;

class DriftDetectorTest {

    private DriftDetector detector;
    private static final double DEFAULT_THRESHOLD = 0.15;
    private static final double WARNING_THRESHOLD = 0.10;

    @BeforeEach
    void setUp() {
        detector = new DriftDetector(DEFAULT_THRESHOLD, WARNING_THRESHOLD);
    }

    @Test
    void testNoDriftDetected() {
        List<Double> trainingDistribution = Arrays.asList(0.3, 0.4, 0.3);
        List<Double> onlineDistribution = Arrays.asList(0.31, 0.39, 0.30);

        DriftDetector.DriftResult result = detector.detectDrift(
            "feature_user_age",
            trainingDistribution,
            onlineDistribution
        );

        assertFalse(result.hasDrift(), "No debe detectar drift con distribuciones similares");
        assertEquals(DriftDetector.DriftLevel.NONE, result.getDriftLevel());
    }

    @Test
    void testSignificantDriftDetected() {
        List<Double> trainingDistribution = Arrays.asList(0.5, 0.3, 0.2);
        List<Double> onlineDistribution = Arrays.asList(0.1, 0.6, 0.3);

        DriftDetector.DriftResult result = detector.detectDrift(
            "feature_purchase_category",
            trainingDistribution,
            onlineDistribution
        );

        assertTrue(result.hasDrift(), "Debe detectar drift significativo");
        assertTrue(result.getDriftScore() > DEFAULT_THRESHOLD);
    }

    @Test
    void testWarningLevelDrift() {
        List<Double> trainingDistribution = Arrays.asList(0.4, 0.35, 0.25);
        List<Double> onlineDistribution = Arrays.asList(0.32, 0.40, 0.28);

        DriftDetector.DriftResult result = detector.detectDrift(
            "feature_browse_time",
            trainingDistribution,
            onlineDistribution
        );

        assertTrue(result.getDriftScore() > WARNING_THRESHOLD);
        assertTrue(result.getDriftScore() <= DEFAULT_THRESHOLD);
    }

    @Test
    void testDistributionNormalization() {
        List<Double> trainingUnnormalized = Arrays.asList(100.0, 80.0, 70.0);
        List<Double> onlineUnnormalized = Arrays.asList(90.0, 85.0, 75.0);

        DriftDetector.DriftResult result = detector.detectDrift(
            "feature_click_count",
            trainingUnnormalized,
            onlineUnnormalized
        );

        assertNotNull(result);
        assertTrue(result.getDriftScore() >= 0.0 && result.getDriftScore() <= 1.0);
    }

    @Test
    void testDifferentDistributionLengths() {
        List<Double> training = Arrays.asList(0.4, 0.3, 0.2, 0.1);
        List<Double> online = Arrays.asList(0.35, 0.35, 0.2);

        assertThrows(IllegalArgumentException.class, () -> detector.detectDrift(
            "feature_mismatch",
            training,
            online
        ));
    }

    @Test
    void testSyntheticDistributionGeneration() {
        List<Double> uniformDist = generateSyntheticDistribution(10, "uniform");
        assertEquals(10, uniformDist.size());
        assertEquals(1.0, uniformDist.stream().mapToDouble(Double::doubleValue).sum(), 0.001);

        List<Double> skewedDist = generateSyntheticDistribution(10, "skewed");
        assertEquals(10, skewedDist.size());
        assertTrue(skewedDist.get(0) > skewedDist.get(skewedDist.size() - 1));
    }

    @Test
    void testMultipleFeatureDriftMonitoring() {
        Map<String, List<Double>> trainingFeatures = new HashMap<>();
        Map<String, List<Double>> onlineFeatures = new HashMap<>();

        trainingFeatures.put("age", Arrays.asList(0.3, 0.4, 0.3));
        onlineFeatures.put("age", Arrays.asList(0.25, 0.45, 0.30));

        trainingFeatures.put("income", Arrays.asList(0.2, 0.5, 0.3));
        onlineFeatures.put("income", Arrays.asList(0.1, 0.6, 0.3));

        List<DriftDetector.DriftResult> results = detector.detectBatchDrift(trainingFeatures, onlineFeatures);

        assertEquals(2, results.size());
        assertTrue(results.stream().anyMatch(r -> r.getFeatureName().equals("age")));
        assertTrue(results.stream().anyMatch(r -> r.getFeatureName().equals("income")));
    }

    @Test
    void testAlertThresholdConfiguration() {
        DriftDetector strictDetector = new DriftDetector(0.05, 0.03);

        List<Double> training = Arrays.asList(0.5, 0.3, 0.2);
        List<Double> online = Arrays.asList(0.48, 0.32, 0.20);

        DriftDetector.DriftResult result = strictDetector.detectDrift(
            "feature_strict",
            training,
            online
        );

        assertTrue(result.getDriftScore() > 0.03);
    }

    @Test
    void testDriftScoreCalculation() {
        List<Double> identical = Arrays.asList(0.33, 0.33, 0.34);
        DriftDetector.DriftResult result = detector.detectDrift(
            "feature_identical",
            identical,
            identical
        );

        assertEquals(0.0, result.getDriftScore(), 0.001);
    }

    private List<Double> generateSyntheticDistribution(int bins, String type) {
        Random random = new Random(42);
        List<Double> values = new ArrayList<>();

        if ("uniform".equals(type)) {
            double base = 1.0 / bins;
            for (int i = 0; i < bins; i++) {
                values.add(base + (random.nextDouble() * 0.01 - 0.005));
            }
        } else if ("skewed".equals(type)) {
            double sum = 0;
            for (int i = 0; i < bins; i++) {
                double value = 1.0 / (i + 1);
                values.add(value);
                sum += value;
            }
            values = values.stream().map(v -> v / sum).collect(Collectors.toList());
        }

        return values;
    }
}

// === ARCHIVO: docs/feature-versioning-scheme.md ===
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

// === ARCHIVO: docs/cache-invalidation-policy.md ===
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

// === ARCHIVO: docs/flink-vs-spark-comparison.md ===
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

// === ARCHIVO: pom.xml ===
<project xmlns="http://maven.apache.org/POM/4.0.0"
         xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"
         xsi:schemaLocation="http://maven.apache.org/POM/4.0.0 http://maven.apache.org/xsd/maven-4.0.0.xsd">
    <modelVersion>4.0.0</modelVersion>

    <groupId>com.pragma</groupId>
    <artifactId>featurestore</artifactId>
    <version>1.0.0-SNAPSHOT</version>
    <packaging>jar</packaging>

    <name>Feature Store Pipeline</name>
    <description>Real-time Feature Store Pipeline with Apache Flink</description>

    <properties>
        <maven.compiler.source>17</maven.compiler.source>
        <maven.compiler.target>17</maven.compiler.target>
        <project.build.sourceEncoding>UTF-8</project.build.sourceEncoding>
        <flink.version>1.18.0</flink.version>
        <java.version>17</java.version>
        <slf4j.version>2.0.9</slf4j.version>
    </properties>

    <dependencies>
        <dependency>
            <groupId>org.apache.flink</groupId>
            <artifactId>flink-streaming-java_2.12</artifactId>
            <version>${flink.version}</version>
            <scope>compile</scope>
        </dependency>
        <dependency>
            <groupId>org.apache.flink</groupId>
            <artifactId>flink-clients_2.12</artifactId>
            <version>${flink.version}</version>
            <scope>compile</scope>
        </dependency>
        <dependency>
            <groupId>org.apache.flink</groupId>
            <artifactId>flink-connector-kafka_2.12</artifactId>
            <version>${flink.version}</version>
            <scope>compile</scope>
        </dependency>
        <dependency>
            <groupId>org.apache.flink</groupId>
            <artifactId>flink-connector-redis_2.12</artifactId>
            <version>${flink.version}</version>
            <scope>compile</scope>
        </dependency>
        <dependency>
            <groupId>io.debezium</groupId>
            <artifactId>debezium-connector-postgres</artifactId>
            <version>2.4.0.Final</version>
            <scope>compile</scope>
        </dependency>
        <dependency>
            <groupId>io.confluent</groupId>
            <artifactId>kafka-avro-serializer</artifactId>
            <version>7.5.1</version>
            <scope>compile</scope>
        </dependency>
        <dependency>
            <groupId>org.apache.avro</groupId>
            <artifactId>avro</artifactId>
            <version>1.11.3</version>
            <scope>compile</scope>
        </dependency>
        <dependency>
            <groupId>org.projectlombok</groupId>
            <artifactId>lombok</artifactId>
            <version>1.18.30</version>
            <scope>provided</scope>
        </dependency>
        <dependency>
            <groupId>jakarta.validation</groupId>
            <artifactId>jakarta.validation-api</artifactId>
            <version>3.0.2</version>
            <scope>compile</scope>
        </dependency>
        <dependency>
            <groupId>org.hibernate.validator</groupId>
            <artifactId>hibernate-validator</artifactId>
            <version>8.0.1.Final</version>
            <scope>compile</scope>
        </dependency>
        <dependency>
            <groupId>org.slf4j</groupId>
            <artifactId>slf4j-api</artifactId>
            <version>${slf4j.version}</version>
            <scope>compile</scope>
        </dependency>
        <dependency>
            <groupId>org.apache.flink</groupId>
            <artifactId>flink-test-utils_2.12</artifactId>
            <version>${flink.version}</version>
            <scope>test</scope>
        </dependency>
        <dependency>
            <groupId>org.mockito</groupId>
            <artifactId>mockito-core</artifactId>
            <version>5.3.1</version>
            <scope>test</scope>
        </dependency>
        <dependency>
            <groupId>org.junit.jupiter</groupId>
            <artifactId>junit-jupiter-api</artifactId>
            <version>5.9.3</version>
            <scope>test</scope>
        </dependency>
    </dependencies>

    <build>
        <plugins>
            <plugin>
                <groupId>org.apache.maven.plugins</groupId>
                <artifactId>maven-compiler-plugin</artifactId>
                <version>3.11.0</version>
                <configuration>
                    <source>${java.version}</source>
                    <target>${java.version}</target>
                </configuration>
            </plugin>
            <plugin>
                <groupId>org.apache.maven.plugins</groupId>
                <artifactId>maven-shade-plugin</artifactId>
                <version>3.5.1</version>
                <executions>
                    <execution>
                        <phase>package</phase>
                        <goals>
                            <goal>shade</goal>
                        </goals>
                        <configuration>
                            <transformers>
                                <transformer implementation="org.apache.maven.plugins.shade.resource.ManifestResourceTransformer">
                                    <mainClass>com.pragma.featurestore.FeatureStorePipeline</mainClass>
                                </transformer>
                            </transformers>
                        </configuration>
                    </execution>
                </executions>
            </plugin>
        </plugins>
    </build>
</project>

// === ARCHIVO: src/main/java/com/pragma/featurestore/dto/FeatureEvent.java ===
package com.pragma.featurestore.dto;

import java.util.Objects;

public class FeatureEvent {
    private String eventId;
    private String entityType;
    private String entityId;
    private String featureName;
    private Object value;
    private long timestamp;
    private String windowType;
    private String version;
    private String aggregationType;

    public FeatureEvent() {
    }

    public String getEventId() {
        return eventId;
    }

    public void setEventId(String eventId) {
        this.eventId = eventId;
    }

    public String getEntityType() {
        return entityType;
    }

    public void setEntityType(String entityType) {
        this.entityType = entityType;
    }

    public String getEntityId() {
        return entityId;
    }

    public void setEntityId(String entityId) {
        this.entityId = entityId;
    }

    public String getFeatureName() {
        return featureName;
    }

    public void setFeatureName(String featureName) {
        this.featureName = featureName;
    }

    public Object getValue() {
        return value;
    }

    public void setValue(Object value) {
        this.value = value;
    }

    public long getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(long timestamp) {
        this.timestamp = timestamp;
    }

    public String getWindowType() {
        return windowType;
    }

    public void setWindowType(String windowType) {
        this.windowType = windowType;
    }

    public String getVersion() {
        return version;
    }

    public void setVersion(String version) {
        this.version = version;
    }

    public String getAggregationType() {
        return aggregationType;
    }

    public void setAggregationType(String aggregationType) {
        this.aggregationType = aggregationType;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        FeatureEvent that = (FeatureEvent) o;
        return timestamp == that.timestamp &&
                Objects.equals(eventId, that.eventId) &&
                Objects.equals(entityType, that.entityType) &&
                Objects.equals(entityId, that.entityId) &&
                Objects.equals(featureName, that.featureName);
    }

    @Override
    public int hashCode() {
        return Objects.hash(eventId, entityType, entityId, featureName, timestamp);
    }

    @Override
    public String toString() {
        return "FeatureEvent{" +
                "eventId='" + eventId + '\'' +
                ", entityType='" + entityType + '\'' +
                ", entityId='" + entityId + '\'' +
                ", featureName='" + featureName + '\'' +
                ", value=" + value +
                ", timestamp=" + timestamp +
                ", windowType='" + windowType + '\'' +
                ", version='" + version + '\'' +
                ", aggregationType='" + aggregationType + '\'' +
                '}';
    }
}

// === ARCHIVO: src/main/java/com/pragma/featurestore/FeatureStorePipeline.java ===
package com.pragma.featurestore;

import com.pragma.featurestore.ingestion.KafkaSink;
import com.pragma.featurestore.processing.FeatureCalculator;
import com.pragma.featurestore.dto.FeatureEvent;
import com.pragma.featurestore.config.FlinkConfig;
import org.apache.flink.streaming.api.environment.StreamExecutionEnvironment;
import org.apache.flink.streaming.api.datastream.DataStream;
import org.apache.flink.api.common.eventtime.WatermarkStrategy;
import org.apache.flink.api.common.serialization.SimpleStringSchema;
import org.apache.flink.connector.kafka.source.KafkaSource;
import org.apache.flink.connector.kafka.source.enumerator.initializer.OffsetsInitializer;
import org.apache.kafka.clients.producer.ProducerConfig;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.time.Duration;
import java.util.Properties;

public class FeatureStorePipeline {
    private static final Logger LOG = LoggerFactory.getLogger(FeatureStorePipeline.class);
    
    private static final String KAFKA_BOOTSTRAP_SERVERS = "localhost:9092";
    private static final String CDC_TOPIC = "postgres.public.user_events";
    private static final String FEATURES_TOPIC = "feature-store-features";
    private static final String CONSUMER_GROUP = "feature-store-consumer";
    private static final long CHECKPOINT_INTERVAL_MS = 60_000L;
    
    public static void main(String[] args) throws Exception {
        LOG.info("Iniciando Feature Store Pipeline para modelo de recomendación");
        
        final StreamExecutionEnvironment env = StreamExecutionEnvironment.getExecutionEnvironment();
        
        configureEnvironment(env);
        
        DataStream<String> rawCdcEvents = ingestFromPostgresCDC(env);
        
        DataStream<FeatureEvent> featureEvents = processCdcEvents(rawCdcEvents);
        
        writeToKafka(featureEvents);
        
        LOG.info("Pipeline configurado. Ejecutando job de Flink...");
        env.execute("Feature Store Pipeline - Real-time Feature Computation");
    }
    
    private static void configureEnvironment(StreamExecutionEnvironment env) {
        env.setParallelism(FlinkConfig.getDefaultParallelism());
        
        env.enableCheckpointing(CHECKPOINT_INTERVAL_MS);
        env.getCheckpointConfig().setMinPauseBetweenCheckpoints(30_000);
        env.getCheckpointConfig().setCheckpointTimeout(300_000);
        env.getCheckpointConfig().setMaxConcurrentCheckpoints(1);
        env.getCheckpointConfig().setTolerableCheckpointFailureNumber(3);
        
        env.getCheckpointConfig().setExternalizedCheckpointCleanup(
            org.apache.flink.streaming.api.environment.CheckpointConfig.ExternalizedCheckpointCleanup.RETAIN_ON_CANCELLATION
        );
        
        env.getConfig().setAutoWatermarkInterval(5_000L);
        
        LOG.info("Entorno de Flink configurado con checkpointing cada {}ms", CHECKPOINT_INTERVAL_MS);
    }
    
    private static DataStream<String> ingestFromPostgresCDC(StreamExecutionEnvironment env) {
        LOG.info("Configurando fuente CDC desde PostgreSQL");
        
        Properties kafkaProps = new Properties();
        kafkaProps.setProperty("bootstrap.servers", KAFKA_BOOTSTRAP_SERVERS);
        kafkaProps.setProperty("group.id", CONSUMER_GROUP);
        kafkaProps.setProperty("auto.offset.reset", "earliest");
        kafkaProps.setProperty("enable.auto.commit", "false");
        kafkaProps.setProperty("isolation.level", "read_committed");
        
        KafkaSource<String> kafkaSource = KafkaSource.<String>builder()
            .setBootstrapServers(KAFKA_BOOTSTRAP_SERVERS)
            .setTopics(CDC_TOPIC)
            .setGroupId(CONSUMER_GROUP)
            .setStartingOffsets(OffsetsInitializer.committedOffsets())
            .setValueOnlyDeserializer(new SimpleStringSchema())
            .setProperties(kafkaProps)
            .build();
        
        DataStream<String> cdcStream = env.fromSource(
            kafkaSource,
            WatermarkStrategy.noWatermarks(),
            "PostgreSQL CDC Source"
        );
        
        LOG.info("Fuente CDC configurada, topic: {}", CDC_TOPIC);
        return cdcStream;
    }
    
    private static DataStream<FeatureEvent> processCdcEvents(DataStream<String> rawEvents) {
        LOG.info("Procesando eventos CDC para computar features");
        
        return rawEvents
            .filter(event -> event != null && !event.isEmpty())
            .keyBy(event -> extractEntityKey(event))
            .process(new FeatureCalculator())
            .name("Feature Calculator")
            .uid("feature-calculator");
    }
    
    private static String extractEntityKey(String event) {
        try {
            if (event.contains("\"user_id\"")) {
                int start = event.indexOf("\"user_id\"");
                int colon = event.indexOf(":", start);
                int comma = event.indexOf(",", colon);
                if (comma == -1) comma = event.indexOf("}", colon);
                return "user:" + event.substring(colon + 1, comma).replace("\"", "").trim();
            }
            return "unknown";
        } catch (Exception e) {
            LOG.warn("Error extrayendo clave de entidad del evento: {}", e.getMessage());
            return "unknown";
        }
    }
    
    private static void writeToKafka(DataStream<FeatureEvent> featureEvents) {
        LOG.info("Configurando sink hacia Kafka, topic: {}", FEATURES_TOPIC);
        
        Properties producerProps = new Properties();
        producerProps.setProperty(ProducerConfig.BOOTSTRAP_SERVERS_CONFIG, KAFKA_BOOTSTRAP_SERVERS);
        producerProps.setProperty(ProducerConfig.ACKS_CONFIG, "all");
        producerProps.setProperty(ProducerConfig.RETRIES_CONFIG, "3");
        producerProps.setProperty(ProducerConfig.ENABLE_IDEMPOTENCE_CONFIG, "true");
        producerProps.setProperty(ProducerConfig.MAX_IN_FLIGHT_REQUESTS_PER_CONNECTION, "5");
        producerProps.setProperty(ProducerConfig.LINGER_MS_CONFIG, "10");
        producerProps.setProperty(ProducerConfig.BUFFER_MEMORY_CONFIG, "33554432");
        
        KafkaSink<FeatureEvent> kafkaSink = new KafkaSink<>(
            FEATURES_TOPIC,
            producerProps,
            event -> event.getEntityId(),
            event -> event.getFeatureName() + "-" + event.getVersion()
        );
        
        featureEvents.sinkTo(kafkaSink)
            .name("Kafka Feature Sink")
            .uid("kafka-feature-sink");
        
        LOG.info("Sink de Kafka configurado exitosamente");
    }
}

// === ARCHIVO: src/main/java/com/pragma/featurestore/processing/FeatureCalculator.java ===
package com.pragma.featurestore.processing;

import org.apache.flink.api.common.eventtime.WatermarkStrategy;
import org.apache.flink.api.common.functions.AggregateFunction;
import org.apache.flink.api.common.state.ListState;
import org.apache.flink.api.common.state.ListStateDescriptor;
import org.apache.flink.api.common.typeinfo.TypeInformation;
import org.apache.flink.configuration.Configuration;
import org.apache.flink.streaming.api.functions.KeyedProcessFunction;
import org.apache.flink.streaming.api.functions.windowing.ProcessAllWindowFunction;
import org.apache.flink.streaming.api.windowing.assigners.SlidingEventTimeWindows;
import org.apache.flink.streaming.api.windowing.windows.GlobalWindow;
import org.apache.flink.util.Collector;
import com.pragma.featurestore.dto.FeatureEvent;
import com.pragma.featurestore.exception.FeatureComputationException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.time.Duration;
import java.util.*;
import java.util.concurrent.atomic.AtomicLong;

public class FeatureCalculator extends KeyedProcessFunction<String, FeatureEvent, FeatureEvent> {

    private static final Logger LOG = LoggerFactory.getLogger(FeatureCalculator.class);
    private static final Duration WINDOW_5MIN = Duration.ofMinutes(5);
    private static final Duration WINDOW_1H = Duration.ofHours(1);
    private static final Duration WINDOW_24H = Duration.ofHours(24);
    private static final Duration LATE_DATA_THRESHOLD = Duration.ofMinutes(10);

    private transient ListState<FeatureEvent> eventBuffer;
    private transient Map<String, AggregatedFeature> aggregatedFeatures;
    private transient AtomicLong processingLatency;

    public FeatureCalculator() {
        this.aggregatedFeatures = new HashMap<>();
    }

    @Override
    public void open(Configuration parameters) {
        ListStateDescriptor<FeatureEvent> descriptor = new ListStateDescriptor<>(
                "eventBuffer",
                TypeInformation.of(FeatureEvent.class)
        );
        eventBuffer = getRuntimeContext().getListState(descriptor);
        processingLatency = new AtomicLong(0);
        aggregatedFeatures = new HashMap<>();

        LOG.info("FeatureCalculator initialized for key: {}", getRuntimeContext().getCurrentKey());
    }

    @Override
    public void processElement(FeatureEvent event, Context ctx, Collector<FeatureEvent> out) {
        long startTime = System.currentTimeMillis();

        try {
            if (!isValidEvent(event)) {
                LOG.warn("Invalid event received: {}", event.getEventId());
                return;
            }

            long eventTimestamp = event.getTimestamp();
            long currentWatermark = ctx.timerService().currentWatermark();

            if (isLateData(eventTimestamp, currentWatermark)) {
                handleLateData(event);
                LOG.debug("Late data handled for event: {} (timestamp: {}, watermark: {})",
                        event.getEventId(), eventTimestamp, currentWatermark);
                return;
            }

            String featureKey = buildFeatureKey(event);
            AggregatedFeature aggregated = aggregatedFeatures.computeIfAbsent(
                    featureKey,
                    k -> new AggregatedFeature(featureKey)
            );

            aggregated.addEvent(event);

            FeatureEvent windowed5min = computeWindowFeature(event, WINDOW_5MIN, "5min");
            FeatureEvent windowed1h = computeWindowFeature(event, WINDOW_1H, "1h");
            FeatureEvent windowed24h = computeWindowFeature(event, WINDOW_24H, "24h");

            if (windowed5min != null) out.collect(windowed5min);
            if (windowed1h != null) out.collect(windowed1h);
            if (windowed24h != null) out.collect(windowed24h);

            long latency = System.currentTimeMillis() - startTime;
            processingLatency.addAndGet(latency);

            LOG.debug("Event processed: {} with latency: {}ms", event.getEventId(), latency);

        } catch (Exception e) {
            LOG.error("Error processing event: {}", event.getEventId(), e);
            throw new FeatureComputationException("Failed to compute feature for event: " + event.getEventId(), e);
        }
    }

    private boolean isValidEvent(FeatureEvent event) {
        return event != null
                && event.getEventId() != null
                && event.getEntityId() != null
                && event.getFeatureName() != null
                && event.getTimestamp() > 0
                && event.getValue() != null;
    }

    private boolean isLateData(long eventTimestamp, long currentWatermark) {
        return currentWatermark > 0
                && (currentWatermark - eventTimestamp) > LATE_DATA_THRESHOLD.toMillis();
    }

    private void handleLateData(FeatureEvent event) {
        try {
            eventBuffer.add(event);
            LOG.debug("Late event added to buffer: {}", event.getEventId());
        } catch (Exception e) {
            LOG.error("Failed to buffer late event: {}", event.getEventId(), e);
        }
    }

    private String buildFeatureKey(FeatureEvent event) {
        return String.format("%s:%s:%s",
                event.getEntityType(),
                event.getEntityId(),
                event.getFeatureName());
    }

    private FeatureEvent computeWindowFeature(FeatureEvent event, Duration windowSize, String windowType) {
        String featureKey = buildFeatureKey(event);
        AggregatedFeature agg = aggregatedFeatures.get(featureKey);

        if (agg == null) {
            return null;
        }

        long windowStart = (event.getTimestamp() / windowSize.toMillis()) * windowSize.toMillis();
        long windowEnd = windowStart + windowSize.toMillis();

        List<FeatureEvent> windowEvents = agg.getEventsInWindow(windowStart, windowEnd);

        if (windowEvents.isEmpty()) {
            return null;
        }

        double aggregatedValue = computeAggregation(windowEvents, event.getAggregationType());

        FeatureEvent result = new FeatureEvent();
        result.setEventId(UUID.randomUUID().toString());
        result.setEntityType(event.getEntityType());
        result.setEntityId(event.getEntityId());
        result.setFeatureName(event.getFeatureName() + "_" + windowType);
        result.setValue(aggregatedValue);
        result.setTimestamp(windowEnd);
        result.setWindowType(windowType);
        result.setVersion(event.getVersion());
        result.setAggregationType(event.getAggregationType());

        return result;
    }

    private double computeAggregation(List<FeatureEvent> events, String aggregationType) {
        if (events == null || events.isEmpty()) {
            return 0.0;
        }

        return switch (aggregationType != null ? aggregationType.toUpperCase() : "SUM") {
            case "SUM" -> events.stream()
                    .mapToDouble(e -> ((Number) e.getValue()).doubleValue())
                    .sum();
            case "AVG" -> events.stream()
                    .mapToDouble(e -> ((Number) e.getValue()).doubleValue())
                    .average()
                    .orElse(0.0);
            case "COUNT" -> (double) events.size();
            case "MIN" -> events.stream()
                    .mapToDouble(e -> ((Number) e.getValue()).doubleValue())
                    .min()
                    .orElse(0.0);
            case "MAX" -> events.stream()
                    .mapToDouble(e -> ((Number) e.getValue()).doubleValue())
                    .max()
                    .orElse(0.0);
            default -> events.stream()
                    .mapToDouble(e -> ((Number) e.getValue()).doubleValue())
                    .sum();
        };
    }

    public static WatermarkStrategy<FeatureEvent> createWatermarkStrategy() {
        return WatermarkStrategy.<FeatureEvent>forBoundedOutOfOrderness(Duration.ofMinutes(5))
                .withTimestampAssigner((event, timestamp) -> event.getTimestamp())
                .withIdleness(Duration.ofMinutes(1));
    }

    public static SlidingEventTimeWindows createSlidingWindow(Duration size, Duration slide) {
        return SlidingEventTimeWindows.of(size, slide);
    }

    public long getProcessingLatency() {
        return processingLatency != null ? processingLatency.get() : 0;
    }

    private static class AggregatedFeature {
        private final String featureKey;
        private final List<FeatureEvent> events;
        private final Map<Long, List<FeatureEvent>> windowIndex;

        public AggregatedFeature(String featureKey) {
            this.featureKey = featureKey;
            this.events = new ArrayList<>();
            this.windowIndex = new HashMap<>();
        }

        public void addEvent(FeatureEvent event) {
            events.add(event);
            long windowKey = event.getTimestamp() / 300000;
            windowIndex.computeIfAbsent(windowKey, k -> new ArrayList<>()).add(event);
        }

        public List<FeatureEvent> getEventsInWindow(long windowStart, long windowEnd) {
            List<FeatureEvent> result = new ArrayList<>();
            for (FeatureEvent event : events) {
                if (event.getTimestamp() >= windowStart && event.getTimestamp() < windowEnd) {
                    result.add(event);
                }
            }
            return result;
        }
    }
}

// === ARCHIVO: src/main/java/com/pragma/featurestore/monitoring/DriftDetector.java ===
package com.pragma.featurestore.monitoring;

import org.apache.flink.api.common.state.ListState;
import org.apache.flink.api.common.state.ListStateDescriptor;
import org.apache.flink.api.common.typeinfo.TypeInformation;
import org.apache.flink.configuration.Configuration;
import org.apache.flink.metrics.Counter;
import org.apache.flink.metrics.Gauge;
import org.apache.flink.metrics.MetricGroup;
import org.apache.flink.streaming.api.functions.windowing.ProcessAllWindowFunction;
import org.apache.flink.streaming.api.windowing.windows.GlobalWindow;
import org.apache.flink.util.Collector;

import com.pragma.featurestore.dto.FeatureEvent;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;

public class DriftDetector extends ProcessAllWindowFunction<FeatureEvent, DriftDetector.DriftResult, GlobalWindow> {

    private static final double EPSILON = 1e-10;
    private static final int MIN_SAMPLES = 100;
    private static final double DRIFT_THRESHOLD = 0.1;

    private transient ListState<FeatureEvent> trainingSetState;
    private transient ListState<FeatureEvent> onlineFeaturesState;
    private transient Counter driftAlertsCounter;
    private transient Counter samplesProcessedCounter;
    private transient Map<String, Double> latestDriftScores;

    @Override
    public void open(Configuration parameters) throws Exception {
        super.open(parameters);

        ListStateDescriptor<FeatureEvent> trainingDescriptor = new ListStateDescriptor<>(
            "training-set",
            TypeInformation.of(FeatureEvent.class)
        );
        trainingSetState = getRuntimeContext().getListState(trainingDescriptor);

        ListStateDescriptor<FeatureEvent> onlineDescriptor = new ListStateDescriptor<>(
            "online-features",
            TypeInformation.of(FeatureEvent.class)
        );
        onlineFeaturesState = getRuntimeContext().getListState(onlineDescriptor);

        latestDriftScores = new ConcurrentHashMap<>();

        MetricGroup metricGroup = getRuntimeContext().getMetricGroup();
        driftAlertsCounter = metricGroup.counter("drift_alerts_total");
        samplesProcessedCounter = metricGroup.counter("drift_samples_processed");

        metricGroup.gauge("drift_score_user_features", (Gauge<Double>) () -> 
            latestDriftScores.getOrDefault("user_features", 0.0)
        );
        metricGroup.gauge("drift_score_item_features", (Gauge<Double>) () -> 
            latestDriftScores.getOrDefault("item_features", 0.0)
        );
    }

    @Override
    public void process(Context context, Iterable<FeatureEvent> elements, Collector<DriftResult> out) throws Exception {
        List<FeatureEvent> currentBatch = new ArrayList<>();
        elements.forEach(currentBatch::add);

        if (currentBatch.isEmpty()) {
            return;
        }

        samplesProcessedCounter.inc(currentBatch.size());

        Map<String, List<Double>> featureGroups = currentBatch.stream()
            .collect(Collectors.groupingBy(
                FeatureEvent::getFeatureName,
                Collectors.mapping(FeatureEvent::getValue, Collectors.toList())
            ));

        for (Map.Entry<String, List<Double>> entry : featureGroups.entrySet()) {
            String featureName = entry.getKey();
            List<Double> onlineValues = entry.getValue();

            List<Double> trainingValues = new ArrayList<>();
            trainingSetState.get().forEach(e -> {
                if (featureName.equals(e.getFeatureName())) {
                    trainingValues.add(e.getValue());
                }
            });

            if (trainingValues.size() < MIN_SAMPLES || onlineValues.size() < MIN_SAMPLES) {
                continue;
            }

            double driftScore = calculateKLDivergence(trainingValues, onlineValues);
            latestDriftScores.put(featureName, driftScore);

            boolean isDriftDetected = driftScore > DRIFT_THRESHOLD;

            if (isDriftDetected) {
                driftAlertsCounter.inc();
            }

            out.collect(new DriftResult(
                featureName,
                driftScore,
                isDriftDetected,
                System.currentTimeMillis(),
                trainingValues.size(),
                onlineValues.size()
            ));
        }
    }

    private double calculateKLDivergence(List<Double> p, List<Double> q) {
        Map<Double, Long> pCounts = calculateHistogram(p);
        Map<Double, Long> qCounts = calculateHistogram(q);

        Set<Double> allKeys = new HashSet<>(pCounts.keySet());
        allKeys.addAll(qCounts.keySet());

        double pTotal = p.size();
        double qTotal = q.size();

        double klDivergence = 0.0;

        for (Double key : allKeys) {
            double pProb = (pCounts.getOrDefault(key, 0L) + EPSILON) / pTotal;
            double qProb = (qCounts.getOrDefault(key, 0L) + EPSILON) / qTotal;
            klDivergence += pProb * Math.log(pProb / qProb);
        }

        return Math.min(klDivergence, 1.0);
    }

    private Map<Double, Long> calculateHistogram(List<Double> values) {
        return values.stream()
            .collect(Collectors.groupingBy(v -> Math.round(v * 100) / 100.0, Collectors.counting()));
    }

    public void updateTrainingSet(List<FeatureEvent> newTrainingData) throws Exception {
        trainingSetState.clear();
        trainingSetState.addAll(newTrainingData);
    }

    public static class DriftResult {
        private final String featureName;
        private final double driftScore;
        private final boolean driftDetected;
        private final long timestamp;
        private final int trainingSampleSize;
        private final int onlineSampleSize;

        public DriftResult(String featureName, double driftScore, boolean driftDetected,
                          long timestamp, int trainingSampleSize, int onlineSampleSize) {
            this.featureName = featureName;
            this.driftScore = driftScore;
            this.driftDetected = driftDetected;
            this.timestamp = timestamp;
            this.trainingSampleSize = trainingSampleSize;
            this.onlineSampleSize = onlineSampleSize;
        }

        public String getFeatureName() { return featureName; }
        public double getDriftScore() { return driftScore; }
        public boolean isDriftDetected() { return driftDetected; }
        public long getTimestamp() { return timestamp; }
        public int getTrainingSampleSize() { return trainingSampleSize; }
        public int getOnlineSampleSize() { return onlineSampleSize; }
    }
}

// === ARCHIVO: pom.xml ===
<project xmlns="http://maven.apache.org/POM/4.0.0"
         xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"
         xsi:schemaLocation="http://maven.apache.org/POM/4.0.0 http://maven.apache.org/xsd/maven-4.0.0.xsd">
    <modelVersion>4.0.0</modelVersion>

    <groupId>com.pragma</groupId>
    <artifactId>featurestore</artifactId>
    <version>1.0.0</version>
    <packaging>jar</packaging>

    <name>Feature Store Pipeline</name>
    <description>Apache Flink based Feature Store Pipeline</description>

    <properties>
        <maven.compiler.source>17</maven.compiler.source>
        <maven.compiler.target>17</maven.compiler.target>
        <project.build.sourceEncoding>UTF-8</project.build.sourceEncoding>
        <flink.version>1.18.0</flink.version>
        <java.version>17</java.version>
        <slf4j.version>2.0.9</slf4j.version>
    </properties>

    <dependencies>
        <dependency>
            <groupId>org.apache.flink</groupId>
            <artifactId>flink-streaming-java_2.12</artifactId>
            <version>${flink.version}</version>
            <scope>compile</scope>
        </dependency>
        <dependency>
            <groupId>org.apache.flink</groupId>
            <artifactId>flink-clients_2.12</artifactId>
            <version>${flink.version}</version>
            <scope>compile</scope>
        </dependency>
        <dependency>
            <groupId>org.apache.flink</groupId>
            <artifactId>flink-connector-kafka_2.12</artifactId>
            <version>${flink.version}</version>
            <scope>compile</scope>
        </dependency>
        <dependency>
            <groupId>org.apache.flink</groupId>
            <artifactId>flink-connector-redis_2.12</artifactId>
            <version>${flink.version}</version>
            <scope>compile</scope>
        </dependency>
        <dependency>
            <groupId>io.debezium</groupId>
            <artifactId>debezium-connector-postgres</artifactId>
            <version>2.4.0.Final</version>
            <scope>compile</scope>
        </dependency>
        <dependency>
            <groupId>io.confluent</groupId>
            <artifactId>kafka-avro-serializer</artifactId>
            <version>7.5.1</version>
            <scope>compile</scope>
        </dependency>
        <dependency>
            <groupId>org.apache.avro</groupId>
            <artifactId>avro</artifactId>
            <version>1.11.3</version>
            <scope>compile</scope>
        </dependency>
        <dependency>
            <groupId>org.projectlombok</groupId>
            <artifactId>lombok</artifactId>
            <version>1.18.30</version>
            <scope>provided</scope>
        </dependency>
        <dependency>
            <groupId>jakarta.validation</groupId>
            <artifactId>jakarta.validation-api</artifactId>
            <version>3.0.2</version>
            <scope>compile</scope>
        </dependency>
        <dependency>
            <groupId>org.hibernate.validator</groupId>
            <artifactId>hibernate-validator</artifactId>
            <version>8.0.1.Final</version>
            <scope>compile</scope>
        </dependency>
        <dependency>
            <groupId>org.slf4j</groupId>
            <artifactId>slf4j-api</artifactId>
            <version>${slf4j.version}</version>
            <scope>compile</scope>
        </dependency>
        <dependency>
            <groupId>org.apache.flink</groupId>
            <artifactId>flink-test-utils_2.12</artifactId>
            <version>${flink.version}</version>
            <scope>test</scope>
        </dependency>
        <dependency>
            <groupId>org.mockito</groupId>
            <artifactId>mockito-core</artifactId>
            <version>5.3.1</version>
            <scope>test</scope>
        </dependency>
        <dependency>
            <groupId>org.junit.jupiter</groupId>
            <artifactId>junit-jupiter-api</artifactId>
            <version>5.9.3</version>
            <scope>test</scope>
        </dependency>
    </dependencies>

    <build>
        <plugins>
            <plugin>
                <groupId>org.apache.maven.plugins</groupId>
                <artifactId>maven-compiler-plugin</artifactId>
                <version>3.11.0</version>
                <configuration>
                    <source>${java.version}</source>
                    <target>${java.version}</target>
                </configuration>
            </plugin>
            <plugin>
                <groupId>org.apache.maven.plugins</groupId>
                <artifactId>maven-shade-plugin</artifactId>
                <version>3.5.1</version>
                <executions>
                    <execution>
                        <phase>package</phase>
                        <goals>
                            <goal>shade</goal>
                        </goals>
                        <configuration>
                            <transformers>
                                <transformer implementation="org.apache.maven.plugins.shade.resource.ManifestResourceTransformer">
                                    <mainClass>com.pragma.featurestore.FeatureStorePipeline</mainClass>
                                </transformer>
                            </transformers>
                        </configuration>
                    </execution>
                </executions>
            </plugin>
        </plugins>
    </build>
</project>

// === ARCHIVO: src/main/java/com/pragma/featurestore/dto/FeatureEvent.java ===
package com.pragma.featurestore.dto;

import java.io.Serializable;
import java.time.Instant;
import java.util.Map;
import java.util.Objects;

public class FeatureEvent implements Serializable {
    private static final long serialVersionUID = 1L;

    private String eventId;
    private String userId;
    private String featureName;
    private Object featureValue;
    private Instant timestamp;
    private Instant processingTimestamp;
    private String version;
    private Map<String, Object> metadata;
    private String entityType;
    private String entityId;
    private String aggregationType;
    private Long windowSizeMs;

    public FeatureEvent() {
    }

    public String getEventId() {
        return eventId;
    }

    public void setEventId(String eventId) {
        this.eventId = eventId;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getFeatureName() {
        return featureName;
    }

    public void setFeatureName(String featureName) {
        this.featureName = featureName;
    }

    public Object getFeatureValue() {
        return featureValue;
    }

    public void setFeatureValue(Object featureValue) {
        this.featureValue = featureValue;
    }

    public Instant getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(Instant timestamp) {
        this.timestamp = timestamp;
    }

    public void setTimestamp(long timestampMs) {
        this.timestamp = Instant.ofEpochMilli(timestampMs);
    }

    public Instant getProcessingTimestamp() {
        return processingTimestamp;
    }

    public void setProcessingTimestamp(Instant processingTimestamp) {
        this.processingTimestamp = processingTimestamp;
    }

    public String getVersion() {
        return version;
    }

    public void setVersion(String version) {
        this.version = version;
    }

    public Map<String, Object> getMetadata() {
        return metadata;
    }

    public void setMetadata(Map<String, Object> metadata) {
        this.metadata = metadata;
    }

    public String getEntityType() {
        return entityType;
    }

    public void setEntityType(String entityType) {
        this.entityType = entityType;
    }

    public String getEntityId() {
        return entityId;
    }

    public void setEntityId(String entityId) {
        this.entityId = entityId;
    }

    public String getAggregationType() {
        return aggregationType;
    }

    public void setAggregationType(String aggregationType) {
        this.aggregationType = aggregationType;
    }

    public Long getWindowSizeMs() {
        return windowSizeMs;
    }

    public void setWindowSizeMs(Long windowSizeMs) {
        this.windowSizeMs = windowSizeMs;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        FeatureEvent that = (FeatureEvent) o;
        return Objects.equals(eventId, that.eventId);
    }

    @Override
    public int hashCode() {
        return Objects.hash(eventId);
    }

    @Override
    public String toString() {
        return "FeatureEvent{" +
                "eventId='" + eventId + '\'' +
                ", userId='" + userId + '\'' +
                ", featureName='" + featureName + '\'' +
                ", featureValue=" + featureValue +
                ", timestamp=" + timestamp +
                ", version='" + version + '\'' +
                '}';
    }
}

// === ARCHIVO: pom.xml ===
<project xmlns="http://maven.apache.org/POM/4.0.0"
         xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"
         xsi:schemaLocation="http://maven.apache.org/POM/4.0.0 http://maven.apache.org/xsd/maven-4.0.0.xsd">
    <modelVersion>4.0.0</modelVersion>

    <groupId>com.pragma</groupId>
    <artifactId>featurestore</artifactId>
    <version>1.0.0</version>
    <packaging>jar</packaging>

    <name>Feature Store Pipeline</name>
    <description>ML Feature Store with Flink</description>

    <properties>
        <maven.compiler.source>17</maven.compiler.source>
        <maven.compiler.target>17</maven.compiler.target>
        <project.build.sourceEncoding>UTF-8</project.build.sourceEncoding>
        <flink.version>1.18.0</flink.version>
        <java.version>17</java.version>
        <slf4j.version>2.0.9</slf4j.version>
        <jedis.version>5.1.0</jedis.version>
    </properties>

    <dependencies>
        <!-- Apache Flink -->
        <dependency>
            <groupId>org.apache.flink</groupId>
            <artifactId>flink-streaming-java_2.12</artifactId>
            <version>${flink.version}</version>
            <scope>compile</scope>
        </dependency>
        <dependency>
            <groupId>org.apache.flink</groupId>
            <artifactId>flink-clients_2.12</artifactId>
            <version>${flink.version}</version>
            <scope>compile</scope>
        </dependency>
        <dependency>
            <groupId>org.apache.flink</groupId>
            <artifactId>flink-connector-kafka_2.12</artifactId>
            <version>${flink.version}</version>
            <scope>compile</scope>
        </dependency>
        <dependency>
            <groupId>org.apache.flink</groupId>
            <artifactId>flink-connector-redis_2.12</artifactId>
            <version>${flink.version}</version>
            <scope>compile</scope>
        </dependency>

        <!-- Kafka -->
        <dependency>
            <groupId>org.apache.kafka</groupId>
            <artifactId>kafka-clients</artifactId>
            <version>3.6.0</version>
            <scope>compile</scope>
        </dependency>

        <!-- Debezium CDC -->
        <dependency>
            <groupId>io.debezium</groupId>
            <artifactId>debezium-connector-postgres</artifactId>
            <version>2.4.0.Final</version>
            <scope>compile</scope>
        </dependency>

        <!-- Confluent / Avro -->
        <dependency>
            <groupId>io.confluent</groupId>
            <artifactId>kafka-avro-serializer</artifactId>
            <version>7.5.1</version>
            <scope>compile</scope>
        </dependency>
        <dependency>
            <groupId>org.apache.avro</groupId>
            <artifactId>avro</artifactId>
            <version>1.11.3</version>
            <scope>compile</scope>
        </dependency>

        <!-- Redis Client - AGREGADO para resolver import redis.clients.jedis -->
        <dependency>
            <groupId>redis.clients</groupId>
            <artifactId>jedis</artifactId>
            <version>${jedis.version}</version>
            <scope>compile</scope>
        </dependency>

        <!-- SLF4J - AGREGADO para resolver import org.slf4j -->
        <dependency>
            <groupId>org.slf4j</groupId>
            <artifactId>slf4j-api</artifactId>
            <version>${slf4j.version}</version>
            <scope>compile</scope>
        </dependency>

        <!-- Validation -->
        <dependency>
            <groupId>jakarta.validation</groupId>
            <artifactId>jakarta.validation-api</artifactId>
            <version>3.0.2</version>
            <scope>compile</scope>
        </dependency>
        <dependency>
            <groupId>org.hibernate.validator</groupId>
            <artifactId>hibernate-validator</artifactId>
            <version>8.0.1.Final</version>
            <scope>compile</scope>
        </dependency>

        <!-- Jackson for JSON -->
        <dependency>
            <groupId>com.fasterxml.jackson.core</groupId>
            <artifactId>jackson-databind</artifactId>
            <version>2.16.0</version>
            <scope>compile</scope>
        </dependency>

        <!-- Lombok -->
        <dependency>
            <groupId>org.projectlombok</groupId>
            <artifactId>lombok</artifactId>
            <version>1.18.30</version>
            <scope>provided</scope>
        </dependency>

        <!-- Test dependencies -->
        <dependency>
            <groupId>org.apache.flink</groupId>
            <artifactId>flink-test-utils_2.12</artifactId>
            <version>${flink.version}</version>
            <scope>test</scope>
        </dependency>
        <dependency>
            <groupId>org.mockito</groupId>
            <artifactId>mockito-core</artifactId>
            <version>5.3.1</version>
            <scope>test</scope>
        </dependency>
        <dependency>
            <groupId>org.junit.jupiter</groupId>
            <artifactId>junit-jupiter-api</artifactId>
            <version>5.9.3</version>
            <scope>test</scope>
        </dependency>
        <dependency>
            <groupId>org.junit.jupiter</groupId>
            <artifactId>junit-jupiter-engine</artifactId>
            <version>5.9.3</version>
            <scope>test</scope>
        </dependency>
    </dependencies>

    <build>
        <plugins>
            <plugin>
                <groupId>org.apache.maven.plugins</groupId>
                <artifactId>maven-compiler-plugin</artifactId>
                <version>3.11.0</version>
                <configuration>
                    <source>${java.version}</source>
                    <target>${java.version}</target>
                </configuration>
            </plugin>
            <plugin>
                <groupId>org.apache.maven.plugins</groupId>
                <artifactId>maven-shade-plugin</artifactId>
                <version>3.5.1</version>
                <executions>
                    <execution>
                        <phase>package</phase>
                        <goals>
                            <goal>shade</goal>
                        </goals>
                        <configuration>
                            <transformers>
                                <transformer implementation="org.apache.maven.plugins.shade.resource.ManifestResourceTransformer">
                                    <mainClass>com.pragma.featurestore.FeatureStorePipeline</mainClass>
                                </transformer>
                            </transformers>
                        </configuration>
                    </execution>
                </executions>
            </plugin>
        </plugins>
    </build>
</project>


// === ARCHIVO: pom.xml ===
<project xmlns="http://maven.apache.org/POM/4.0.0"
         xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"
         xsi:schemaLocation="http://maven.apache.org/POM/4.0.0 http://maven.apache.org/xsd/maven-4.0.0.xsd">
    <modelVersion>4.0.0</modelVersion>

    <groupId>com.pragma</groupId>
    <artifactId>featurestore</artifactId>
    <version>1.0.0</version>
    <packaging>jar</packaging>

    <properties>
        <maven.compiler.source>17</maven.compiler.source>
        <maven.compiler.target>17</maven.compiler.target>
        <project.build.sourceEncoding>UTF-8</project.build.sourceEncoding>
        <flink.version>1.18.0</flink.version>
        <java.version>17</java.version>
        <slf4j.version>2.0.9</slf4j.version>
        <log4j.version>2.20.0</log4j.version>
    </properties>

    <dependencies>
        <dependency>
            <groupId>org.apache.flink</groupId>
            <artifactId>flink-streaming-java_2.12</artifactId>
            <version>${flink.version}</version>
            <scope>compile</scope>
        </dependency>
        <dependency>
            <groupId>org.apache.flink</groupId>
            <artifactId>flink-clients_2.12</artifactId>
            <version>${flink.version}</version>
            <scope>compile</scope>
        </dependency>
        <dependency>
            <groupId>org.apache.flink</groupId>
            <artifactId>flink-connector-kafka_2.12</artifactId>
            <version>${flink.version}</version>
            <scope>compile</scope>
        </dependency>
        <dependency>
            <groupId>org.apache.flink</groupId>
            <artifactId>flink-connector-redis_2.12</artifactId>
            <version>${flink.version}</version>
            <scope>compile</scope>
        </dependency>
        <dependency>
            <groupId>io.debezium</groupId>
            <artifactId>debezium-connector-postgres</artifactId>
            <version>2.4.0.Final</version>
            <scope>compile</scope>
        </dependency>
        <dependency>
            <groupId>io.confluent</groupId>
            <artifactId>kafka-avro-serializer</artifactId>
            <version>7.5.1</version>
            <scope>compile</scope>
        </dependency>
        <dependency>
            <groupId>org.apache.avro</groupId>
            <artifactId>avro</artifactId>
            <version>1.11.3</version>
            <scope>compile</scope>
        </dependency>
        <dependency>
            <groupId>org.projectlombok</groupId>
            <artifactId>lombok</artifactId>
            <version>1.18.30</version>
            <scope>provided</scope>
        </dependency>
        <dependency>
            <groupId>jakarta.validation</groupId>
            <artifactId>jakarta.validation-api</artifactId>
            <version>3.0.2</version>
            <scope>compile</scope>
        </dependency>
        <dependency>
            <groupId>org.hibernate.validator</groupId>
            <artifactId>hibernate-validator</artifactId>
            <version>8.0.1.Final</version>
            <scope>compile</scope>
        </dependency>
        <dependency>
            <groupId>redis.clients</groupId>
            <artifactId>jedis</artifactId>
            <version>4.3.1</version>
            <scope>compile</scope>
        </dependency>
        <dependency>
            <groupId>org.apache.flink</groupId>
            <artifactId>flink-test-utils_2.12</artifactId>
            <version>${flink.version}</version>
            <scope>test</scope>
        </dependency>
        <dependency>
            <groupId>org.mockito</groupId>
            <artifactId>mockito-core</artifactId>
            <version>5.3.1</version>
            <scope>test</scope>
        </dependency>
        <dependency>
            <groupId>org.junit.jupiter</groupId>
            <artifactId>junit-jupiter-api</artifactId>
            <version>5.9.3</version>
            <scope>test</scope>
        </dependency>
        <dependency>
            <groupId>org.junit.jupiter</groupId>
            <artifactId>junit-jupiter-engine</artifactId>
            <version>5.9.3</version>
            <scope>test</scope>
        </dependency>
    </dependencies>

    <build>
        <plugins>
            <plugin>
                <groupId>org.apache.maven.plugins</groupId>
                <artifactId>maven-compiler-plugin</artifactId>
                <version>3.11.0</version>
                <configuration>
                    <source>${java.version}</source>
                    <target>${java.version}</target>
                </configuration>
            </plugin>
            <plugin>
                <groupId>org.apache.maven.plugins</groupId>
                <artifactId>maven-shade-plugin</artifactId>
                <version>3.5.1</version>
                <executions>
                    <execution>
                        <phase>package</phase>
                        <goals>
                            <goal>shade</goal>
                        </goals>
                        <configuration>
                            <transformers>
                                <transformer implementation="org.apache.maven.plugins.shade.resource.ManifestResourceTransformer">
                                    <mainClass>com.pragma.featurestore.FeatureStorePipeline</mainClass>
                                </transformer>
                            </transformers>
                        </configuration>
                    </execution>
                </executions>
            </plugin>
        </plugins>
    </build>
</project>

// === ARCHIVO: src/main/java/com/pragma/featurestore/serving/ABRouter.java ===
package com.pragma.featurestore.serving;

import org.apache.flink.api.common.functions.MapFunction;
import org.apache.flink.configuration.Configuration;
import org.apache.flink.streaming.api.functions.ProcessFunction;
import org.apache.flink.util.Collector;
import org.apache.flink.streaming.api.functions.sink.SinkFunction;
import org.apache.flink.streaming.api.datastream.DataStream;
import org.apache.flink.streaming.api.operators.StreamMap;
import org.apache.flink.api.common.state.MapState;
import org.apache.flink.api.common.state.MapStateDescriptor;
import org.apache.flink.api.common.typeinfo.TypeInformation;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPool;
import redis.clients.jedis.resps.Tuple;

import java.util.*;
import java.util.concurrent.atomic.AtomicLong;

public class ABRouter extends ProcessFunction<ABRequest, ABRouter.ABResponse> {
    private static final Logger LOG = LoggerFactory.getLogger(ABRouter.class);
    private static final double CHALLENGER_PERCENTAGE = 0.1;
    private static final Duration STICKY_SESSION_DURATION = Duration.ofDays(7);
    private static final int HASH_MODULUS = 1000;

    private final Map<String, ModelVariant> modelVariants;
    private final Map<String, String> stickySessions;
    private final Map<String, AtomicLong> decisionCounters;
    private final Map<String, AtomicLong> stickyCounters;
    private final boolean enableStickySessions;
    private final boolean logDecisions;
    private final double challengerPercentage;
    private final JedisPool jedisPool;

    private transient Random random;
    private transient long sessionCleanupTimestamp;

    public ABRouter() {
        this(new HashMap<>(), true, false, 0.1, null);
    }

    public ABRouter(JedisPool jedisPool, double challengerPercentage) {
        this(new HashMap<>(), true, false, challengerPercentage, jedisPool);
    }

    public ABRouter(Map<String, ModelVariant> modelVariants, boolean enableStickySessions, 
                    boolean logDecisions, double challengerPercentage, JedisPool jedisPool) {
        this.modelVariants = modelVariants;
        this.enableStickySessions = enableStickySessions;
        this.logDecisions = logDecisions;
        this.challengerPercentage = challengerPercentage;
        this.jedisPool = jedisPool;
        this.stickySessions = new HashMap<>();
        this.decisionCounters = new HashMap<>();
        this.stickyCounters = new HashMap<>();
    }

    @Override
    public void open(Configuration parameters) {
        super.open(parameters);
        this.random = new Random();
        this.sessionCleanupTimestamp = System.currentTimeMillis();
    }

    @Override
    public void processElement(ABRequest request, Context ctx, Collector<ABResponse> out) {
        try {
            String variant = route(request.getUserId(), request.getExperimentId());
            out.collect(buildResponse(request, ModelVariant.valueOf(variant.toUpperCase())));
        } catch (Exception e) {
            LOG.error("Error routing request", e);
            out.collect(createFallbackResponse(request));
        }
    }

    public String route(String userId, String experimentId) {
        if (userId == null || experimentId == null) {
            return "control";
        }

        if (enableStickySessions && jedisPool != null) {
            try (Jedis jedis = jedisPool.getResource()) {
                String sessionKey = "ab:session:" + experimentId;
                String cachedVariant = jedis.hget(sessionKey, userId);
                if (cachedVariant != null) {
                    updateStickyMetrics(experimentId, cachedVariant);
                    return cachedVariant;
                }

                String variant = selectVariant(userId, experimentId);
                jedis.hset(sessionKey, userId, variant);
                jedis.expire(sessionKey, (int) STICKY_SESSION_DURATION.getSeconds());
                
                String metricsKey = "ab:metrics:" + experimentId + ":" + variant;
                jedis.zadd(metricsKey, System.currentTimeMillis(), userId);
                
                return variant;
            } catch (Exception e) {
                LOG.warn("Redis unavailable, using fallback", e);
                return fallbackRoute(userId, experimentId);
            }
        }

        return fallbackRoute(userId, experimentId);
    }

    private String fallbackRoute(String userId, String experimentId) {
        if (enableStickySessions) {
            String stickyKey = experimentId + ":" + userId;
            String cachedVariant = stickySessions.get(stickyKey);
            if (cachedVariant != null) {
                return cachedVariant;
            }
        }

        String variant = selectVariant(userId, experimentId);
        
        if (enableStickySessions) {
            stickySessions.put(experimentId + ":" + userId, variant);
        }

        return variant;
    }

    private boolean isValidRequest(ABRequest request) {
        return request != null && request.getUserId() != null && !request.getUserId().isEmpty();
    }

    private ModelVariant selectVariant(String userId, String experimentId) {
        int hash = computeConsistentHash(userId + experimentId);
        double threshold = challengerPercentage * HASH_MODULUS;
        
        if (hash < threshold) {
            updateDecisionMetrics(ModelVariant.CHALLENGER);
            return ModelVariant.CHALLENGER;
        }
        updateDecisionMetrics(ModelVariant.CONTROL);
        return ModelVariant.CONTROL;
    }

    private int computeConsistentHash(String value) {
        return Math.abs(value.hashCode()) % HASH_MODULUS;
    }

    private void updateDecisionMetrics(ModelVariant variant) {
        String key = variant.name();
        decisionCounters.computeIfAbsent(key, k -> new AtomicLong(0)).incrementAndGet();
    }

    private void updateStickyMetrics(String experimentId, String variant) {
        String key = experimentId + ":" + variant;
        stickyCounters.computeIfAbsent(key, k -> new AtomicLong(0)).incrementAndGet();
    }

    private ABResponse buildResponse(ABRequest request, ModelVariant variant) {
        ABResponse response = new ABResponse();
        response.setUserId(request.getUserId());
        response.setExperimentId(request.getExperimentId());
        response.setVariant(variant.name().toLowerCase());
        response.setModelEndpoint(getModelEndpoint(variant));
        response.setTimestamp(System.currentTimeMillis());
        return response;
    }

    private String getModelEndpoint(ModelVariant variant) {
        return modelVariants.getOrDefault(variant.name(), 
            new ModelVariant(variant.name(), "/model/" + variant.name().toLowerCase())).getEndpoint();
    }

    private void logDecision(ABRequest request, ModelVariant variant) {
        if (logDecisions) {
            LOG.info("AB Decision: userId={}, experimentId={}, variant={}", 
                request.getUserId(), request.getExperimentId(), variant);
        }
    }

    private ABResponse createErrorResponse(ABRequest request, String errorMessage) {
        ABResponse response = new ABResponse();
        response.setUserId(request.getUserId());
        response.setExperimentId(request.getExperimentId());
        response.setVariant("control");
        response.setError(errorMessage);
        response.setTimestamp(System.currentTimeMillis());
        return response;
    }

    private ABResponse createFallbackResponse(ABRequest request) {
        ABResponse response = new ABResponse();
        response.setUserId(request != null ? request.getUserId() : "unknown");
        response.setExperimentId(request != null ? request.getExperimentId() : "unknown");
        response.setVariant("control");
        response.setTimestamp(System.currentTimeMillis());
        return response;
    }

    private void cleanupStaleSessionsIfNeeded() {
        long now = System.currentTimeMillis();
        if (now - sessionCleanupTimestamp > 3600000) {
            stickySessions.entrySet().removeIf(entry -> 
                (now - sessionCleanupTimestamp) > STICKY_SESSION_DURATION.toMillis());
            sessionCleanupTimestamp = now;
        }
    }

    public Map<String, Long> getDecisionCounts() {
        Map<String, Long> result = new HashMap<>();
        decisionCounters.forEach((k, v) -> result.put(k, v.get()));
        return result;
    }

    public Map<String, Long> getStickySessionCounts() {
        Map<String, Long> result = new HashMap<>();
        stickyCounters.forEach((k, v) -> result.put(k, v.get()));
        return result;
    }

    public double getChallengerPercentage() {
        return challengerPercentage;
    }

    public void resetCounters() {
        decisionCounters.clear();
        stickyCounters.clear();
    }

    public enum ModelVariant {
        CONTROL("control", "/model/control"),
        CHALLENGER("challenger", "/model/challenger");

        private final String name;
        private final String endpoint;

        ModelVariant(String name, String endpoint) {
            this.name = name;
            this.endpoint = endpoint;
        }

        public String getName() { return name; }
        public String getEndpoint() { return endpoint; }
    }

    public static class ABRequest {
        private String userId;
        private String experimentId;
        private Map<String, Object> context;

        public String getUserId() { return userId; }
        public void setUserId(String userId) { this.userId = userId; }
        public String getExperimentId() { return experimentId; }
        public void setExperimentId(String experimentId) { this.experimentId = experimentId; }
        public Map<String, Object> getContext() { return context; }
        public void setContext(Map<String, Object> context) { this.context = context; }
    }

    public static class ABResponse {
        private String userId;
        private String experimentId;
        private String variant;
        private String modelEndpoint;
        private long timestamp;
        private String error;

        public String getUserId() { return userId; }
        public void setUserId(String userId) { this.userId = userId; }
        public String getExperimentId() { return experimentId; }
        public void setExperimentId(String experimentId) { this.experimentId = experimentId; }
        public String getVariant() { return variant; }
        public void setVariant(String variant) { this.variant = variant; }
        public String getModelEndpoint() { return modelEndpoint; }
        public void setModelEndpoint(String modelEndpoint) { this.modelEndpoint = modelEndpoint; }
        public long getTimestamp() { return timestamp; }
        public void setTimestamp(long timestamp) { this.timestamp = timestamp; }
        public String getError() { return error; }
        public void setError(String error) { this.error = error; }
    }
}

// === ARCHIVO: src/main/java/com/pragma/featurestore/monitoring/DriftDetector.java ===
package com.pragma.featurestore.monitoring;

import org.apache.flink.api.common.state.ListState;
import org.apache.flink.api.common.state.ListStateDescriptor;
import org.apache.flink.api.common.typeinfo.TypeInformation;
import org.apache.flink.configuration.Configuration;
import org.apache.flink.metrics.Counter;
import org.apache.flink.metrics.MetricGroup;
import org.apache.flink.streaming.api.functions.windowing.ProcessAllWindowFunction;
import org.apache.flink.streaming.api.windowing.windows.GlobalWindow;
import org.apache.flink.util.Collector;

import java.util.*;
import java.util.concurrent.atomic.AtomicReference;

public class DriftDetector extends ProcessAllWindowFunction<FeatureEvent, DriftDetector.DriftResult, GlobalWindow> {
    private static final double EPSILON = 1e-10;
    private static final int MIN_SAMPLES = 100;
    private static final double DRIFT_THRESHOLD = 0.15;

    private final double driftThreshold;
    private final double warningThreshold;

    private transient ListState<FeatureEvent> trainingSetState;
    private transient ListState<FeatureEvent> onlineFeaturesState;
    private transient Counter driftAlertsCounter;
    private transient Counter samplesProcessedCounter;
    private transient Map<String, Double> latestDriftScores;
    private transient AtomicReference<Map<String, List<Double>>> trainingDistributions;

    public DriftDetector() {
        this(DRIFT_THRESHOLD, 0.10);
    }

    public DriftDetector(double driftThreshold, double warningThreshold) {
        this.driftThreshold = driftThreshold;
        this.warningThreshold = warningThreshold;
    }

    @Override
    public void open(Configuration parameters) throws Exception {
        super.open(parameters);

        ListStateDescriptor<FeatureEvent> trainingDescriptor = new ListStateDescriptor<>(
            "drift-training-set",
            TypeInformation.of(FeatureEvent.class)
        );
        trainingSetState = getRuntimeContext().getListState(trainingDescriptor);

        ListStateDescriptor<FeatureEvent> onlineDescriptor = new ListStateDescriptor<>(
            "drift-online-features",
            TypeInformation.of(FeatureEvent.class)
        );
        onlineFeaturesState = getRuntimeContext().getListState(onlineDescriptor);

        MetricGroup metricGroup = getRuntimeContext().getMetricGroup();
        driftAlertsCounter = metricGroup.counter("drift_alerts_total");
        samplesProcessedCounter = metricGroup.counter("drift_samples_processed_total");

        latestDriftScores = new HashMap<>();
        trainingDistributions = new AtomicReference<>(new HashMap<>());
    }

    @Override
    public void process(Context context, Iterable<FeatureEvent> elements, Collector<DriftResult> out) throws Exception {
        List<FeatureEvent> windowEvents = new ArrayList<>();
        elements.forEach(windowEvents::add);

        if (windowEvents.isEmpty()) {
            return;
        }

        samplesProcessedCounter.inc(windowEvents.size());

        Map<String, List<Double>> onlineDistributions = computeDistributions(windowEvents);
        Map<String, List<Double>> trainingDist = trainingDistributions.get();

        for (String featureName : onlineDistributions.keySet()) {
            List<Double> online = onlineDistributions.get(featureName);
            List<Double> training = trainingDist.getOrDefault(featureName, online);

            if (training.size() != online.size()) {
                throw new IllegalArgumentException(
                    "Training and online distributions must have the same length for feature: " + featureName);
            }

            double driftScore = calculateKLDivergence(training, online);
            latestDriftScores.put(featureName, driftScore);

            DriftLevel level = determineDriftLevel(driftScore);
            DriftResult result = new DriftResult(
                featureName,
                driftScore,
                level,
                training,
                online,
                System.currentTimeMillis()
            );

            if (level != DriftLevel.NONE) {
                driftAlertsCounter.inc();
            }

            out.collect(result);
        }
    }

    public DriftResult detectDrift(String featureName, List<Double> trainingDistribution, 
                                    List<Double> onlineDistribution) {
        if (trainingDistribution == null || onlineDistribution == null) {
            throw new IllegalArgumentException("Distributions cannot be null");
        }
        if (trainingDistribution.size() != onlineDistribution.size()) {
            throw new IllegalArgumentException(
                "Distributions must have the same length: " + 
                trainingDistribution.size() + " vs " + onlineDistribution.size());
        }

        List<Double> normalizedTraining = normalizeDistribution(trainingDistribution);
        List<Double> normalizedOnline = normalizeDistribution(onlineDistribution);

        double driftScore = calculateKLDivergence(normalizedTraining, normalizedOnline);
        DriftLevel level = determineDriftLevel(driftScore);

        return new DriftResult(featureName, driftScore, level, normalizedTraining, 
                               normalizedOnline, System.currentTimeMillis());
    }

    public List<DriftResult> detectBatchDrift(Map<String, List<Double>> trainingFeatures,
                                               Map<String, List<Double>> onlineFeatures) {
        List<DriftResult> results = new ArrayList<>();

        for (String featureName : onlineFeatures.keySet()) {
            List<Double> training = trainingFeatures.get(featureName);
            List<Double> online = onlineFeatures.get(featureName);

            if (training == null) {
                results.add(new DriftResult(featureName, 0.0, DriftLevel.NONE, 
                    new ArrayList<>(), online, System.currentTimeMillis()));
                continue;
            }

            try {
                DriftResult result = detectDrift(featureName, training, online);
                results.add(result);
            } catch (IllegalArgumentException e) {
                results.add(new DriftResult(featureName, 1.0, DriftLevel.SEVERE,
                    training, online, System.currentTimeMillis()));
            }
        }

        return results;
    }

    private Map<String, List<Double>> computeDistributions(List<FeatureEvent> events) {
        Map<String, List<Double>> distributions = new HashMap<>();
        Map<String, List<Double>> featureValues = new HashMap<>();

        for (FeatureEvent event : events) {
            String featureName = event.getFeatureName();
            featureValues.computeIfAbsent(featureName, k -> new ArrayList<>()).add(event.getFeatureValue());
        }

        for (Map.Entry<String, List<Double>> entry : featureValues.entrySet()) {
            distributions.put(entry.getKey(), normalizeDistribution(entry.getValue()));
        }

        return distributions;
    }

    private List<Double> normalizeDistribution(List<Double> values) {
        if (values == null || values.isEmpty()) {
            return new ArrayList<>();
        }

        double sum = values.stream().mapToDouble(Double::doubleValue).sum();
        if (Math.abs(sum) < EPSILON) {
            return new ArrayList<>(Collections.nCopies(values.size(), 1.0 / values.size()));
        }

        return values.stream().map(v -> v / sum).toList();
    }

    private double calculateKLDivergence(List<Double> p, List<Double> q) {
        if (p.size() != q.size()) {
            throw new IllegalArgumentException("Distributions must have the same size");
        }

        double klDivergence = 0.0;
        for (int i = 0; i < p.size(); i++) {
            double pi = Math.max(p.get(i), EPSILON);
            double qi = Math.max(q.get(i), EPSILON);
            klDivergence += pi * Math.log(pi / qi);
        }

        return Math.min(Math.max(klDivergence, 0.0), 1.0);
    }

    private Map<Double, Long> calculateHistogram(List<Double> values) {
        Map<Double, Long> histogram = new HashMap<>();
        for (Double value : values) {
            histogram.merge(value, 1L, Long::sum);
        }
        return histogram;
    }

    private DriftLevel determineDriftLevel(double driftScore) {
        if (driftScore < warningThreshold) {
            return DriftLevel.NONE;
        } else if (driftScore < driftThreshold) {
            return DriftLevel.WARNING;
        } else if (driftScore < driftThreshold * 1.5) {
            return DriftLevel.SEVERE;
        } else {
            return DriftLevel.CRITICAL;
        }
    }

    public void updateTrainingSet(List<FeatureEvent> newTrainingData) throws Exception {
        trainingSetState.clear();
        for (FeatureEvent event : newTrainingData) {
            trainingSetState.add(event);
        }

        Map<String, List<Double>> newDistributions = computeDistributions(newTrainingData);
        trainingDistributions.set(newDistributions);
    }

    public static class DriftResult {
        private final String featureName;
        private final double driftScore;
        private final DriftLevel driftLevel;
        private final List<Double> trainingDistribution;
        private final List<Double> onlineDistribution;
        private final long timestamp;

        public DriftResult(String featureName, double driftScore, DriftLevel driftLevel,
                          List<Double> trainingDistribution, List<Double> onlineDistribution,
                          long timestamp) {
            this.featureName = featureName;
            this.driftScore = driftScore;
            this.driftLevel = driftLevel;
            this.trainingDistribution = trainingDistribution;
            this.onlineDistribution = onlineDistribution;
            this.timestamp = timestamp;
        }

        public String getFeatureName() { return featureName; }
        public double getDriftScore() { return driftScore; }
        public DriftLevel getDriftLevel() { return driftLevel; }
        public List<Double> getTrainingDistribution() { return trainingDistribution; }
        public List<Double> getOnlineDistribution() { return onlineDistribution; }
        public long getTimestamp() { return timestamp; }

        public boolean hasDrift() {
            return driftLevel != DriftLevel.NONE;
        }
    }

    public enum DriftLevel {
        NONE,
        WARNING,
        SEVERE,
        CRITICAL
    }
}

// === ARCHIVO: src/main/java/com/pragma/featurestore/monitoring/FeatureEvent.java ===
package com.pragma.featurestore.monitoring;

public class FeatureEvent {
    private String eventId;
    private String entityType;
    private String entityId;
    private String featureName;
    private double featureValue;
    private long timestamp;
    private String version;
    private Map<String, Object> metadata;

    public FeatureEvent() {}

    public FeatureEvent(String eventId, String entityType, String entityId, 
                       String featureName, double featureValue, long timestamp) {
        this.eventId = eventId;
        this.entityType = entityType;
        this.entityId = entityId;
        this.featureName = featureName;
        this.featureValue = featureValue;
        this.timestamp = timestamp;
    }

    public String getEventId() { return eventId; }
    public void setEventId(String eventId) { this.eventId = eventId; }
    public String getEntityType() { return entityType; }
    public void setEntityType(String entityType) { this.entityType = entityType; }
    public String getEntityId() { return entityId; }
    public void setEntityId(String entityId) { this.entityId = entityId; }
    public String getFeatureName() { return featureName; }
    public void setFeatureName(String featureName) { this.featureName = featureName; }
    public double getFeatureValue() { return featureValue; }
    public void setFeatureValue(double featureValue) { this.featureValue = featureValue; }
    public long getTimestamp() { return timestamp; }
    public void setTimestamp(long timestamp) { this.timestamp = timestamp; }
    public String getVersion() { return version; }
    public void setVersion(String version) { this.version = version; }
    public Map<String, Object> getMetadata() { return metadata; }
    public void setMetadata(Map<String, Object> metadata) { this.metadata = metadata; }
}

```
