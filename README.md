# Nexus Fashion — Arquitectura de Microservicios

Sistema de comercio electrónico de moda basado en una arquitectura distribuida de microservicios desarrollada con Spring Boot, comunicados a través de un API Gateway centralizado y desplegados mediante Docker.

## Integrantes

- Luisana Rivero
- Vicente Briones

## Repositorio

https://github.com/Vi-Briones/Nexux-Fashion-Final-Examen.git

---

## Microservicios implementados# Nexus Fashion — Arquitectura de Microservicios

Sistema de comercio electrónico de moda basado en una arquitectura distribuida de microservicios desarrollada con Spring Boot, comunicados a través de un API Gateway centralizado y desplegados mediante Docker.

## Integrantes

- Luisana Rivero
- Vicente Briones

## Repositorio

https://github.com/Vi-Briones/Nexux-Fashion-Final-Examen.git

## Tablero Trello

https://trello.com/invite/b/6a53e8e136f7968441b89970/ATTIc43ff26994a9ba4b01c2228b9c5a54e56302BE42/nexus-fashion-microservicios

---

## Microservicios implementados

| Servicio | Puerto | Descripción |
|---|---|---|
| api-gateway | 9090 | Punto de entrada unificado para todos los microservicios |
| cliente-service | 9091 | Gestión de clientes y roles |
| compra-service | 9092 | Registro y control de compras |
| producto-service | 9093 | Catálogo de productos y categorías |
| venta-service | 9094 | Gestión de ventas y métodos de pago |
| envio-service | 9095 | Control de envíos y detalles de entrega |
| inventario-service | 9096 | Gestión de stock e inventario por producto |
| resena-service | 9097 | Reseñas y calificaciones de compras |
| notificacion-service | 9098 | Envío y gestión de notificaciones a usuarios |
| soporte-service | 9099 | Tickets de soporte al cliente |
| recomendaciones-service | 9100 | Recomendaciones de productos por cliente |

---

## Rutas principales del API Gateway

| Ruta | Servicio destino |
|---|---|
| `/clientes/**` | cliente-service:9091 |
| `/compras/**` | compra-service:9092 |
| `/productos/**` | producto-service:9093 |
| `/ventas/**` | venta-service:9094 |
| `/envios/**` | envio-service:9095 |
| `/inventarios/**` | inventario-service:9096 |
| `/resenas/**` | resena-service:9097 |
| `/notificaciones/**` | notificacion-service:9098 |
| `/soportes/**` | soporte-service:9099 |
| `/recomendaciones/**` | recomendaciones-service:9100 |

---

## Documentación Swagger/OpenAPI

Una vez levantado el sistema, cada microservicio expone su documentación en:

| Servicio | URL Swagger |
|---|---|
| cliente-service | http://localhost:9091/doc/swagger-ui.html |
| compra-service | http://localhost:9092/doc/swagger-ui.html |
| producto-service | http://localhost:9093/doc/swagger-ui.html |
| venta-service | http://localhost:9094/doc/swagger-ui.html |
| envio-service | http://localhost:9095/doc/swagger-ui.html |
| inventario-service | http://localhost:9096/doc/swagger-ui.html |
| resena-service | http://localhost:9097/doc/swagger-ui.html |
| notificacion-service | http://localhost:9098/doc/swagger-ui.html |
| soporte-service | http://localhost:9099/doc/swagger-ui.html |
| recomendaciones-service | http://localhost:9100/doc/swagger-ui.html |

---

## Instrucciones de ejecución local con Docker

### Requisitos previos

- Docker Desktop instalado y corriendo
- JDK 21
- Maven

### Pasos

**1. Clonar el repositorio**
```bash
git clone https://github.com/Vi-Briones/Nexux-Fashion-Final-Examen.git
cd Nexux-Fashion-Final-Examen
```

**2. Compilar todos los servicios**

En Windows (PowerShell):
```powershell
.\build-all.bat
```

O manualmente por servicio:
```powershell
cd cliente-service && ./mvnw clean package -DskipTests && cd ..
cd compra-service && ./mvnw clean package -DskipTests && cd ..
# repetir para cada servicio...
```

**3. Levantar todos los contenedores**
```bash
docker compose up --build
```

**4. Verificar que todos los servicios están corriendo**
```bash
docker ps
```

**5. Probar el sistema**

Acceder al API Gateway en `http://localhost:9090` y usar cualquiera de las rutas listadas arriba.

### Detener el sistema
```bash
docker compose down
```

---

## Stack tecnológico

- Java 21
- Spring Boot 4.0.6
- Spring Cloud Gateway
- Spring Data JPA + Hibernate
- MySQL (vía XAMPP / host.docker.internal)
- Liquibase (migraciones de base de datos)
- Docker + Docker Compose
- Swagger / SpringDoc OpenAPI
- JUnit 5 + Mockito (pruebas unitarias)
- Lombok
- SLF4J (logging estructurado)


| Servicio | Puerto | Descripción |
|---|---|---|
| api-gateway | 9090 | Punto de entrada unificado para todos los microservicios |
| cliente-service | 9091 | Gestión de clientes y roles |
| compra-service | 9092 | Registro y control de compras |
| producto-service | 9093 | Catálogo de productos y categorías |
| venta-service | 9094 | Gestión de ventas y métodos de pago |
| envio-service | 9095 | Control de envíos y detalles de entrega |
| inventario-service | 9096 | Gestión de stock e inventario por producto |
| resena-service | 9097 | Reseñas y calificaciones de compras |
| notificacion-service | 9098 | Envío y gestión de notificaciones a usuarios |
| soporte-service | 9099 | Tickets de soporte al cliente |
| recomendaciones-service | 9100 | Recomendaciones de productos por cliente |

---

## Rutas principales del API Gateway

| Ruta | Servicio destino |
|---|---|
| `/clientes/**` | cliente-service:9091 |
| `/compras/**` | compra-service:9092 |
| `/productos/**` | producto-service:9093 |
| `/ventas/**` | venta-service:9094 |
| `/envios/**` | envio-service:9095 |
| `/inventarios/**` | inventario-service:9096 |
| `/resenas/**` | resena-service:9097 |
| `/notificaciones/**` | notificacion-service:9098 |
| `/soportes/**` | soporte-service:9099 |
| `/recomendaciones/**` | recomendaciones-service:9100 |

---

## Documentación Swagger/OpenAPI

Una vez levantado el sistema, cada microservicio expone su documentación en:

| Servicio | URL Swagger |
|---|---|
| cliente-service | http://localhost:9091/doc/swagger-ui.html |
| compra-service | http://localhost:9092/doc/swagger-ui.html |
| producto-service | http://localhost:9093/doc/swagger-ui.html |
| venta-service | http://localhost:9094/doc/swagger-ui.html |
| envio-service | http://localhost:9095/doc/swagger-ui.html |
| inventario-service | http://localhost:9096/doc/swagger-ui.html |
| resena-service | http://localhost:9097/doc/swagger-ui.html |
| notificacion-service | http://localhost:9098/doc/swagger-ui.html |
| soporte-service | http://localhost:9099/doc/swagger-ui.html |
| recomendaciones-service | http://localhost:9100/doc/swagger-ui.html |

---

## Instrucciones de ejecución local con Docker

### Requisitos previos

- Docker Desktop instalado y corriendo
- JDK 21
- Maven

### Pasos

**1. Clonar el repositorio**
```bash
git clone https://github.com/Vi-Briones/Nexux-Fashion-Final-Examen.git
cd Nexux-Fashion-Final-Examen
```

**2. Compilar todos los servicios**

En Windows (PowerShell):
```powershell
.\build-all.bat
```

O manualmente por servicio:
```powershell
cd cliente-service && ./mvnw clean package -DskipTests && cd ..
cd compra-service && ./mvnw clean package -DskipTests && cd ..
# repetir para cada servicio...
```

**3. Levantar todos los contenedores**
```bash
docker compose up --build
```

**4. Verificar que todos los servicios están corriendo**
```bash
docker ps
```

**5. Probar el sistema**

Acceder al API Gateway en `http://localhost:9090` y usar cualquiera de las rutas listadas arriba.

### Detener el sistema
```bash
docker compose down
```

---

## Stack tecnológico

- Java 21
- Spring Boot 4.0.6
- Spring Cloud Gateway
- Spring Data JPA + Hibernate
- MySQL (vía XAMPP / host.docker.internal)
- Liquibase (migraciones de base de datos)
- Docker + Docker Compose
- Swagger / SpringDoc OpenAPI
- JUnit 5 + Mockito (pruebas unitarias)
- Lombok
- SLF4J (logging estructurado)[Uploading README.md…]()
