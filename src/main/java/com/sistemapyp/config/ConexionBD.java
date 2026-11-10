package com.sistemapyp.config;

// ============================================================================
// PASO 3 DEL FLUJO JDBC: Importar el paquete java.sql.*
// Estas interfaces y clases permiten la comunicación directa con la base de datos.
// ============================================================================
import java.io.InputStream;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.Properties;

/**
 * Clase ConexionBD
 * 
 * Responsable de gestionar el ciclo de vida de la conexión JDBC hacia la base
 * de datos MySQL para el Sistema de Promoción y Prevención (PyP).
 * 
 * Implementa explícitamente los pasos fundamentales del componente formativo CF20:
 * - Paso 3: Importación de librerías java.sql.*
 * - Paso 4: Carga e inicialización del Driver MySQL (Class.forName)
 * - Paso 5: Creación del objeto Connection mediante DriverManager
 * 
 * @author SENA - Sistema PyP - Linyermar Altuzarra & Angie Garzon
 * @version 1.0
 */
public class ConexionBD {

    // Constantes por defecto para la conexión a MySQL
    // se establecen las constantes aqui para poder probar la conexion
    // de manera autonoma ejecutando la clase
    private static final String DRIVER = "com.mysql.cj.jdbc.Driver";
    private static final String URL_DEFAULT = "jdbc:mysql://localhost:3306/sistema_pyp?useSSL=false&serverTimezone=America/Bogota&allowPublicKeyRetrieval=true";
    private static final String USER_DEFAULT = "root";
    private static final String PASSWORD_DEFAULT = "root";

    private static String url;
    private static String user;
    private static String password;
    private static String driver;

    static {
        cargarConfiguracion();
        registrarDriver();
    }

    /**
     * Carga los parámetros desde application.properties o utiliza los valores por defecto.
     */
    private static void cargarConfiguracion() {
        Properties props = new Properties();
        try (InputStream input = ConexionBD.class.getClassLoader().getResourceAsStream("application.properties")) {
            if (input != null) {
                props.load(input);
                url = props.getProperty("db.url", URL_DEFAULT);
                user = props.getProperty("db.user", USER_DEFAULT);
                password = props.getProperty("db.password", PASSWORD_DEFAULT);
                driver = props.getProperty("db.driver", DRIVER);
            } else {
                url = URL_DEFAULT;
                user = USER_DEFAULT;
                password = PASSWORD_DEFAULT;
                driver = DRIVER;
            }
        } catch (Exception e) {
            System.err.println("Advertencia: No se pudo leer application.properties, usando valores por defecto. Detalle: " + e.getMessage());
            url = URL_DEFAULT;
            user = USER_DEFAULT;
            password = PASSWORD_DEFAULT;
            driver = DRIVER;
        }
    }

    /**
     * ========================================================================
     * PASO 4 DEL FLUJO JDBC: Inicializar el Driver con Class.forName
     * ========================================================================
     * Carga dinámicamente en el ClassLoader de Java la clase del Driver MySQL
     * registrada en el conector (mysql-connector-j).
     */
    private static void registrarDriver() {
        try {
            Class.forName(driver);
            System.out.println("[JDBC PASO 4] Driver MySQL registrado exitosamente: " + driver);
        } catch (ClassNotFoundException e) {
            System.err.println("[JDBC ERROR] No se encontró la clase del Driver JDBC: " + driver);
            System.err.println("Verifique que mysql-connector-j esté incluido en pom.xml (Pasos 1 y 2).");
            e.printStackTrace();
        }
    }

    /**
     * ========================================================================
     * PASO 5 DEL FLUJO JDBC: Crear el objeto de tipo Connection
     * ========================================================================
     * Utiliza DriverManager.getConnection pasando la URL, usuario y contraseña
     * para abrir un canal de comunicación TCP hacia el servidor MySQL.
     * 
     * @return Objeto Connection activo
     * @throws SQLException Si ocurre algún fallo de autenticación o conexión de red
     */
    public static Connection obtenerConexion() throws SQLException {
        try {
            Connection conexion = DriverManager.getConnection(url, user, password);
            return conexion;
        } catch (SQLException e) {
            System.err.println("[JDBC ERROR] Falló la conexión con la base de datos MySQL.");
            System.err.println("URL intentada: " + url);
            System.err.println("Usuario: " + user);
            System.err.println("Mensaje de error SQL: " + e.getMessage());
            throw e;
        }
    }

    /**
     * Método utilitario para cerrar de forma segura una conexión activa.
     * 
     * @param conexion Objeto Connection que se desea cerrar
     */
    public static void cerrarConexion(Connection conexion) {
        if (conexion != null) {
            try {
                if (!conexion.isClosed()) {
                    conexion.close();
                    System.out.println("[JDBC] Conexión cerrada de forma exitosa.");
                }
            } catch (SQLException e) {
                System.err.println("[JDBC ERROR] Error al intentar cerrar la conexión: " + e.getMessage());
            }
        }
    }

    /**
     * Método didáctico para verificar y probar la conexión a MySQL e imprimir
     * metadatos útiles de la base de datos.
     * 
     * @return true si la conexión fue exitosa, false en caso contrario
     */
    public static boolean probarConexion() {
        System.out.println("\n------------------------------------------------------------");
        System.out.println("  INICIANDO PRUEBA DE CONEXIÓN JDBC - SISTEMA PyP");
        System.out.println("------------------------------------------------------------");
        System.out.println("URL: " + url);
        System.out.println("Usuario: " + user);
        
        try (Connection con = obtenerConexion()) {
            if (con != null && !con.isClosed()) {
                System.out.println("[JDBC PASO 5] ¡Conexión establecida correctamente con MySQL!");
                System.out.println("Base de Datos Activa: " + con.getCatalog());
                System.out.println("Motor de Base de Datos: " + con.getMetaData().getDatabaseProductName() + " " + con.getMetaData().getDatabaseProductVersion());
                System.out.println("Driver en uso: " + con.getMetaData().getDriverName() + " v" + con.getMetaData().getDriverVersion());
                System.out.println("------------------------------------------------------------\n");
                return true;
            }
        } catch (SQLException e) {
            System.err.println("Error en prueba de conexión: " + e.getMessage());
        }
        return false;
    }

    /**
     * Método ejecutable principal para probar la conexión directamente.
     */
    public static void main(String[] args) {
        probarConexion();
    }
}
