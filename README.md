# Proyectos

Sistema Intercmabio Cartas

## 🧱 Requisitos

* Tener instalado [Docker](https://www.docker.com/get-started) y [Docker Compose](https://docs.docker.com/compose/install/)

Verificá que estén funcionando con:

```bash
docker --version
docker-compose --version
```
Los Tests usan Test Containers para poder probar todas las funcionalidades que requieren de la Base de Datos. Por esto, es necesario que Docker este ejecutandose a la hora de ejecutar los Tests

---

## 🚀 Cómo ejecutar la aplicación de forma Local

1. **Cloná el repositorio y situate en el directorio raíz:**

```bash
git clone https://github.com/ManuRodri01/Proyectos.git
cd Sistema_Intercambio_Cartas
```

2. **Levantá todos los servicios con Docker Compose:**

```bash
docker-compose up --build
```

Esto construirá y levantará los tres servicios: backend, BBDD y bot, cada uno en su contenedor correspondiente.

📌 Si querés que los servicios se ejecuten en segundo plano (modo "detached"), podés usar la opción `-d`:

```bash
docker-compose up --build -d
```

Para que el bot funcione, se debe crear un bot en telegram con BOT FATHER y poner su nombre y token en el application properties del proyecto del bot


## 🛑 Cómo detener los contenedores

Desde la misma carpeta donde corriste el `docker-compose up`, ejecutá:

```bash
docker-compose down
```

Esto detendrá y eliminará los contenedores, pero mantendrá las imágenes construidas.

---
