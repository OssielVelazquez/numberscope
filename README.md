# NumberScope

Panel de análisis de metadatos telefónicos. Analiza números de teléfono y muestra información **pública** sobre su bloque de numeración: país, región, tipo de línea y la LADA (región asignada por el IFT en México).

> ⚠️ **Alcance del proyecto**
> NumberScope **no rastrea teléfonos ni revela la ubicación de personas**. Solo interpreta metadatos públicos del rango de numeración (identificador del operador y región administrativa). La región mostrada corresponde al bloque asignado por el regulador, **no** a la ubicación del dispositivo ni del propietario.

## Demo

Puedes probar el proyecto localmente siguiendo las instrucciones de la sección "Cómo ejecutarlo en local". No se incluyen capturas para mantener el repositorio ligero.

## Stack técnico

| Capa | Tecnología |
|------|-----------|
| Backend | Java 21 + Spring Boot 4.1.1 + Spring Web + Spring Data JPA |
| Base de datos | H2 (archivo local, persistente) |
| Librería telefónica | [libphonenumber](https://github.com/google/libphonenumber) de Google |
| Frontend | React 19 + TypeScript + Vite |
| Estilos | TailwindCSS |
| Estado del servidor | TanStack Query (React Query) |
| HTTP | Axios |
| Mapas | Leaflet + React-Leaflet + OpenStreetMap |

## Funcionalidades

- ✅ Análisis de números telefónicos con formato internacional (`+52...`) o nacional + región.
- ✅ Detección de país, tipo de línea (móvil/fijo/VoIP) y validez mediante `libphonenumber`.
- ✅ Detección de LADA mexicana y visualización de la región en un mapa (OpenStreetMap).
- ✅ Historial persistente en H2 con:
  - Búsqueda por número, país, tipo de línea o LADA.
  - Eliminación individual de entradas.
  - Borrado completo del historial.
  - Clic en cualquier entrada para reanalizarla (sin duplicados).
- ✅ Interfaz oscura tipo panel de investigación.

## Estructura del proyecto

```
numberscope/
├── backend/                    # API Spring Boot
│   ├── src/main/java/com/numberscope/
│   │   ├── BackendApplication.java
│   │   ├── config/             # Configuración CORS
│   │   ├── phone/              # Módulo de análisis telefónico
│   │   │   ├── PhoneController.java
│   │   │   ├── PhoneAnalyzerService.java
│   │   │   ├── PhoneQuery.java
│   │   │   ├── PhoneQueryRepository.java
│   │   │   └── dto/
│   │   └── region/             # Tabla de LADAs mexicanas con coordenadas
│   └── src/main/resources/application.properties
└── frontend/                   # Interfaz React + Vite
    ├── src/
    │   ├── App.tsx
    │   ├── lib/api.ts
    │   └── pages/PhonePage.tsx
    └── package.json
```

## Cómo ejecutarlo en local

### Requisitos previos

- **Java 21** (probado con Amazon Corretto 21)
- **Node.js 20+** y **npm**
- IntelliJ IDEA o VS Code (opcional, para editar)

### Backend

```bash
cd backend
./mvnw spring-boot:run
```

En Windows:

```bat
cd backend
mvnw.cmd spring-boot:run
```

El backend arranca en `http://localhost:8080`. La base de datos H2 se crea automáticamente en `backend/data/numberscope.mv.db`.

### Frontend

En otra terminal:

```bash
cd frontend
npm install
npm run dev
```

El frontend arranca en `http://localhost:5173`. Vite redirige las peticiones `/api/*` al backend automáticamente.

## Endpoints de la API

| Método | Ruta | Descripción |
|--------|------|-------------|
| POST | `/api/phone/analyze` | Analiza un número telefónico |
| GET | `/api/phone/history` | Devuelve el historial de consultas |
| DELETE | `/api/phone/history/{id}` | Elimina una entrada del historial |
| DELETE | `/api/phone/history` | Borra todo el historial |

### Ejemplo: analizar un número

```bash
curl -X POST http://localhost:8080/api/phone/analyze \
  -H "Content-Type: application/json" \
  -d '{"rawInput":"+526871236436","defaultRegion":"MX"}'
```

Respuesta:

```json
{
  "rawInput": "+526871236436",
  "valid": true,
  "e164": "+526871236436",
  "country": "MX",
  "region": "MX",
  "carrier": null,
  "lineType": "Fijo o móvil",
  "timezones": [],
  "regionLat": 25.5733,
  "regionLng": -108.4668,
  "regionName": "Región LADA 687",
  "lada": "687",
  "error": null
}
```

## Alcance y consideraciones éticas

Este proyecto fue diseñado con un enfoque explícito en la **legalidad y la transparencia**:

- **No rastrea teléfonos.** Eso no es técnicamente posible con un número, y hacerlo sin consentimiento es delito en México y en la mayoría de países.
- **No consulta bases de datos filtradas.** Todo lo que muestra proviene de:
  - `libphonenumber`, librería open source de Google.
  - El Plan Nacional de Numeración del IFT (México), información pública.
- **La región mostrada corresponde al bloque de numeración**, no a la ubicación del dispositivo ni del propietario.
- Los datos de operador pueden no estar disponibles para todos los países.

El objetivo del proyecto es demostrar dominio de:
- Diseño de API REST con Spring Boot 4.
- Integración con librerías externas.
- Manejo de persistencia con JPA/H2.
- Frontend moderno con React 19, TypeScript y Tailwind.
- Mapas interactivos con Leaflet.

## Roadmap

- [ ] Soporte de más países con tabla de regiones.
- [ ] Exportar historial a CSV.
- [ ] Autenticación básica en la API.
- [ ] Empaquetado con Docker Compose.
- [ ] Tests unitarios con JUnit y Vitest.

## Autor

**Ossiel Velazquez** — Software Developer & QA Tester

- GitHub: [@OssielVelazquez](https://github.com/OssielVelazquez)
- Stack: Android • Java • Kotlin • Spring Boot • SQL

## Licencia

MIT. Consulta el archivo [LICENSE](LICENSE) para más detalles.