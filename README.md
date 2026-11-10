# Evidencia GA7-220501096-AA2-EV01: Codificación de módulos del software
## Programa de Formación: Análisis y Desarrollo de Software (ADSO) - SENA
### Dominio: Sistema de Promoción y Prevención en Salud (Sistema PyP)

---

## 1. Descripción del Proyecto

Este proyecto implementa la codificación del módulo central de persistencia y gestión de usuarios/pacientes para el **Sistema de Promoción y Prevención en Salud (PyP)**, utilizando **Java** y conectividad nativa **JDBC (Java Database Connectivity)** contra una base de datos relacional **MySQL**.

El desarrollo se fundamenta en las directrices técnicas del componente formativo **CF20 (Tecnologías y frameworks en el ecosistema Java)**, demostrando explícitamente el flujo de conexión de 8 pasos y las operaciones fundamentales de un CRUD (**C**reate, **R**ead, **U**pdate, **D**elete).

---

## 2. Arquitectura de Software y Estándares de Codificación

El sistema adopta el **Patrón por Capas** y el patrón de diseño **DAO (Data Access Object)**, garantizando una alta cohesión y bajo acoplamiento entre la lógica de negocio y la base de datos:

```
src/main/java/com/sistemapyp/
├── config/
│   └── ConexionBD.java            # Gestión de conexión JDBC (Pasos 3, 4 y 5)
├── model/
│   └── Usuario.java               # Entidad / POJO (Encapsulamiento, Java Beans)
├── dao/
│   ├── IUsuarioDAO.java           # Contrato de persistencia con operaciones CRUD
│   └── UsuarioDAOImpl.java        # Implementación JDBC con PreparedStatement y ResultSet
├── app/
│   └── MenuPyP.java               # Menú interactivo en consola y demostración guiada
└── SistemaPypJdbcApplication.java # Clase principal de arranque Spring Boot
```

### Estándares de Codificación Aplicados:
- **Paquetes (Lowercase):** En minúsculas y organizados semánticamente (`com.sistemapyp.config`, `com.sistemapyp.model`, `com.sistemapyp.dao`, `com.sistemapyp.app`).
- **Clases e Interfaces (PascalCase):** `ConexionBD`, `Usuario`, `IUsuarioDAO`, `UsuarioDAOImpl`, `MenuPyP`.
- **Métodos y Variables (camelCase):** `obtenerConexion()`, `idUsuario`, `listarTodos()`, `filasAfectadas`.

---

## 3. Demostración Formal del Flujo JDBC de 8 Pasos (SENA CF20)

| Paso | Requerimiento CF20 | Implementación en el Proyecto |
| :---: | :--- | :--- |
| **Paso 1** | Confirmar presencia del driver JDBC (`mysql-connector-j`) | Verificación en el repositorio Maven (`com.mysql:mysql-connector-j:9.1.0+`). |
| **Paso 2** | Incluir la librería en el gestor de dependencias | Declaración formal en el archivo `pom.xml`. |
| **Paso 3** | Importar el paquete `java.sql.*` | Incluido en `ConexionBD.java` y `UsuarioDAOImpl.java` (`Connection`, `PreparedStatement`, `ResultSet`, `SQLException`). |
| **Paso 4** | Inicializar el driver mediante `Class.forName` | `Class.forName("com.mysql.cj.jdbc.Driver");` en el bloque estático de `ConexionBD.java`. |
| **Paso 5** | Crear el objeto `Connection` con `DriverManager` | `DriverManager.getConnection(url, user, password)` en el método `obtenerConexion()`. |
| **Paso 6** | Crear el objeto `Statement` / `PreparedStatement` | `con.prepareStatement(SQL_...)` en cada método de `UsuarioDAOImpl.java`. |
| **Paso 7** | Ejecutar sentencias SQL (`executeUpdate` / `executeQuery`) | `ps.executeUpdate()` para INSERT, UPDATE, DELETE; `ps.executeQuery()` para SELECT. |
| **Paso 8** | Procesar los resultados obtenidos con `ResultSet` | Iteración con `rs.next()`, extracción con `rs.getString()`, `rs.getInt()`, y validación `rs.wasNull()`. |

---

## 4. Estructura de la Base de Datos (MySQL)

Basado en el script DDL `Sistema_PyP_EV03.sql`, la base de datos se denomina `sistema_pyp`. La tabla central implementada en este módulo es `usuario`:

```sql
CREATE TABLE `usuario` (
  `id_usuario` varchar(20) NOT NULL,
  `nombre` varchar(100) NOT NULL,
  `sexo` varchar(20) DEFAULT NULL,
  `edad` smallint DEFAULT NULL,
  `telefono` varchar(20) DEFAULT NULL,
  `pais` varchar(50) DEFAULT NULL,
  `tipo_usuario` varchar(20) NOT NULL,
  PRIMARY KEY (`id_usuario`),
  CONSTRAINT `chk_edad` CHECK (((`edad` is null) or (`edad` between 0 and 120))),
  CONSTRAINT `chk_tipo_usuario` CHECK ((`tipo_usuario` in ('PACIENTE','PERSONAL_SALUD')))
);
```

---

## 5. Instrucciones de Compilación y Ejecución

### Prerrequisitos:
- **Java JDK 21 o superior** instalado.
- **MySQL Server** en ejecución en el puerto 3306 con la base de datos `sistema_pyp`.

### 1. Compilación del Proyecto
```bash
./mvnw clean compile
```

### 2. Ejecutar Pruebas Automatizadas (Unitarias e Integración)
```bash
./mvnw test
```

### 3. Ejecutar la Demostración Automática del CRUD
Ejecuta en secuencia: inserción (Create), consulta individual (Read), actualización (Update), listado general (Read All) y eliminación (Delete):
```bash
java -cp target/classes:~/.m2/repository/com/mysql/mysql-connector-j/9.1.0/mysql-connector-j-9.1.0.jar com.sistemapyp.app.MenuPyP --auto
```
*(O utilizando la ruta local correspondiente a la versión instalada de `mysql-connector-j`)*.

### 4. Iniciar el Menú Interactivo por Consola
Permite operar el sistema en tiempo real con datos ingresados por el usuario:
```bash
java -cp target/classes:/Users/linyerbalza/.m2/repository/com/mysql/mysql-connector-j/9.7.0/mysql-connector-j-9.7.0.jar com.sistemapyp.app.MenuPyP
```

---

## 6. Historial de Control de Versiones (Git)

El proyecto cuenta con control de versiones Git estructurado mediante mensajes semánticos de commit:
- `feat: Inicialización del repositorio y estructura base del proyecto`
- `feat: Módulo de conexión JDBC con flujo de 8 pasos (SENA CF20)`
- `feat: Modelo de entidad Usuario y contrato de persistencia IUsuarioDAO`
- `feat: Implementación de persistencia UsuarioDAOImpl con operaciones CRUD`
- `feat: Menú interactivo por consola y suite de pruebas unitarias`
