# Diseño y Evaluación de Pipeline de Feature Store en Tiempo Real

Estás encargado de diseñar y evaluar un pipeline de feature store en tiempo real que alimenta un modelo de recomendación de e-commerce. El pipeline debe soportar ingesta desde CDC de PostgreSQL a Kafka, computación de features en Flink con ventanas deslizantes de 5min/1h/24h, y servir features vía Redis con TTLs por tipo. El modelo se despliega con un router A/B que enruta el 10% del tráfico a un challenger. Deberás justificar la elección de Flink vs Spark Structured Streaming, diseñar el esquema de versionado de features, la política de invalidación de cache, y cómo medir el drift entre el training set y las features online. Además, deberás diseñar el rollback si el challenger degrada el CTR.

## Informacion General

| Campo | Valor |
|-------|-------|
| **Tema** | pipeline de feature store en tiempo real para modelo de recomendación con A/B testing |
| **Nivel** | master-l1 |
| **Tipo** | mixed |
| **Tiempo estimado** | 20 horas |

## Fases del Reto

### Fase 0: Configuración del Proyecto

**Objetivo:** Obtener el proyecto base funcional enviando el Código Base a un asistente de IA, que lo analizará, corregirá errores y generará un ZIP listo para usar.

**Tiempo estimado:** 15-30 minutos

**Instrucciones:**

- Asegúrate de tener instalado para ejecutar el proyecto: JDK 17+, Maven 3.9+, IDE con soporte Java.
- Copia todo el contenido del campo **Código Base** de este reto — incluyendo el texto de instrucciones que aparece al inicio.
- Abre un asistente de IA (Claude en claude.ai, ChatGPT o Gemini — se recomienda Claude), pega el contenido copiado en el chat y envíalo.
- El asistente analizará los archivos, corregirá errores y generará un archivo ZIP descargable. Descárgalo y extráelo en la carpeta donde quieras trabajar.
- Ejecuta `mvn compile` en la raíz. Si no hay errores, estás listo.

**Entregable:** El proyecto compila/arranca sin errores.

<details>
<summary>Pistas de conocimiento</summary>

- Copia el Código Base completo incluyendo el texto de instrucciones al inicio — esas instrucciones le indican al asistente exactamente qué hacer con los archivos.
- Si el asistente no genera el ZIP automáticamente al terminar el análisis, escríbele: "genera el ZIP ahora".
- Si el proyecto tiene errores al arrancar, comparte el mensaje de error con el mismo asistente para que lo corrija.

</details>

### Fase 1: Ingesta y Computación de Features

**Objetivo:** Diseñar la ingesta desde CDC de PostgreSQL a Kafka y la computación de features en Flink.

**Tiempo estimado:** 6 horas

**Instrucciones:**

- Identificar las fuentes de datos y los eventos que deben ser capturados.
- Diseñar la ingesta de datos desde PostgreSQL a Kafka utilizando CDC.
- Implementar la computación de features en Flink con ventanas deslizantes de 5min/1h/24h.

**Entregable:** Descripción del diseño de la ingesta y computación de features, incluyendo la elección de Flink vs Spark Structured Streaming.

<details>
<summary>Pistas de conocimiento</summary>

- Considera la latencia y la consistencia en la elección de la tecnología de streaming.
- Evalúa los pros y contras de Flink y Spark Structured Streaming en términos de ventanas deslizantes y procesamiento en tiempo real.

</details>

### Fase 2: Servir Features y A/B Testing

**Objetivo:** Diseñar el servicio de features vía Redis y el router A/B para el modelo de recomendación.

**Tiempo estimado:** 6 horas

**Instrucciones:**

- Diseñar el servicio de features vía Redis con TTLs por tipo.
- Implementar el router A/B que enruta el 10% del tráfico a un challenger.
- Diseñar el esquema de versionado de features para no romper el modelo en producción.

**Entregable:** Descripción del diseño del servicio de features y el router A/B, incluyendo el esquema de versionado de features.

<details>
<summary>Pistas de conocimiento</summary>

- Considera la política de invalidación de cache y los TTLs para los diferentes tipos de features.
- Evalúa la importancia del versionado de features para mantener la estabilidad del modelo en producción.

</details>

### Fase 3: Medición de Drift y Rollback

**Objetivo:** Diseñar la medición de drift entre el training set y las features online y el rollback si el challenger degrada el CTR.

**Tiempo estimado:** 8 horas

**Instrucciones:**

- Diseñar la medición de drift entre el training set y las features online.
- Implementar el rollback si el challenger degrada el CTR.
- Evaluar los posibles impactos del drift y del rollback en el modelo de recomendación.

**Entregable:** Descripción del diseño de la medición de drift y el rollback, incluyendo los posibles impactos en el modelo de recomendación.

<details>
<summary>Pistas de conocimiento</summary>

- Considera los diferentes métodos para medir el drift entre el training set y las features online.
- Evalúa los posibles impactos del rollback en el modelo de recomendación y cómo mitigarlos.

</details>

## Dimensiones Evaluadas

- **queEs**: ¿Qué es un feature store y por qué es importante en un modelo de recomendación?
- **paraQueSirve**: ¿Para qué sirve el versionado de features en un modelo de recomendación en producción?
- **comoSeUsa**: ¿Cómo se utiliza un router A/B en un modelo de recomendación con challenger?
- **erroresComunes**: ¿Cuáles son los errores comunes en la ingesta de datos desde PostgreSQL a Kafka y cómo se pueden manejar?
- **queDecisionesImplica**: ¿Qué decisiones implica la elección de Flink vs Spark Structured Streaming para la computación de features?

## Criterios de Evaluacion

- Diseño de la ingesta y computación de features en Flink.
- Diseño del servicio de features vía Redis y el router A/B.
- Diseño de la medición de drift y el rollback.
- Justificación de la elección de Flink vs Spark Structured Streaming.
- Diseño del esquema de versionado de features.
- Diseño de la política de invalidación de cache.
- Evaluación de los posibles impactos del drift y del rollback en el modelo de recomendación.

## Como trabajar con un asistente de IA

Hay dos caminos, elegi uno:

- **AGENTS.md** (recomendado) — instrucciones nativas del repo. Abri esta carpeta con tu agente local (Claude Code, Cursor, Codex, Copilot, Gemini) y las carga solo. Sabe que archivos faltan y con que comando se verifica, y completa el scaffold escribiendo en disco.
- **PROMPT_MEJORA.md** — para copiar y pegar en un chat (claude.ai, ChatGPT). Devuelve un ZIP con el proyecto. Sirve si no tenes un agente en el IDE.

Ninguno de los dos resuelve las fases del reto: eso es tu trabajo.

## Verificacion

El proyecto esta listo para trabajar cuando este comando corre sin errores:

```bash
el comando de build o arranque canonico del stack elegido
```

---

*Reto generado automaticamente por Challenge Generator - Pragma*
