package com.sistemapyp.dao;

// ============================================================================
// PASO 3 DEL FLUJO JDBC: Importación del paquete java.sql.*
// Clases fundamentales: Connection, PreparedStatement, ResultSet, SQLException
// ============================================================================
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Types;
import java.util.ArrayList;
import java.util.List;

import com.sistemapyp.config.ConexionBD;
import com.sistemapyp.model.Usuario;

/**
 * Implementación de la interfaz IUsuarioDAO.
 * 
 * Gestiona el acceso y persistencia física en la tabla 'usuario' de MySQL,
 * demostrando formalmente los 8 pasos de la conexión JDBC explicados en el
 * documento formativo SENA CF20.
 * 
 * Se utiliza PreparedStatement para prevenir ataques de Inyección SQL y
 * optimizar el plan de ejecución en la base de datos.
 * 
 * @author SENA ADSO - Sistema PyP
 * @version 1.0
 */
public class UsuarioDAOImpl implements IUsuarioDAO {

    // ========================================================================
    // SENTENCIAS SQL PARAMETRIZADAS (Buenas prácticas de seguridad)
    // ========================================================================
    private static final String SQL_INSERT = 
        "INSERT INTO usuario (id_usuario, nombre, sexo, edad, telefono, pais, tipo_usuario) VALUES (?, ?, ?, ?, ?, ?, ?)";

    private static final String SQL_SELECT_BY_ID = 
        "SELECT id_usuario, nombre, sexo, edad, telefono, pais, tipo_usuario FROM usuario WHERE id_usuario = ?";

    private static final String SQL_SELECT_ALL = 
        "SELECT id_usuario, nombre, sexo, edad, telefono, pais, tipo_usuario FROM usuario ORDER BY nombre ASC";

    private static final String SQL_UPDATE = 
        "UPDATE usuario SET nombre = ?, sexo = ?, edad = ?, telefono = ?, pais = ?, tipo_usuario = ? WHERE id_usuario = ?";

    private static final String SQL_DELETE = 
        "DELETE FROM usuario WHERE id_usuario = ?";

    // ========================================================================
    // OPERACIÓN 1: CREATE (Inserción de registros)
    // ========================================================================
    @Override
    public boolean registrar(Usuario usuario) {
        // Validación previa
        if (usuario == null || usuario.getIdUsuario() == null || usuario.getIdUsuario().trim().isEmpty()) {
            System.err.println("[VALIDACIÓN] El ID/documento del usuario es obligatorio.");
            return false;
        }

        /*
         * PASOS JDBC APLICADOS:
         * Paso 5: Obtener Connection activa desde ConexionBD.
         * Paso 6: Crear el objeto PreparedStatement a partir de la conexión.
         * Paso 7: Ejecutar la sentencia SQL con executeUpdate().
         */
        try (Connection con = ConexionBD.obtenerConexion();
             PreparedStatement ps = con.prepareStatement(SQL_INSERT)) {

            // Asignación de parámetros a los marcadores '?'
            ps.setString(1, usuario.getIdUsuario().trim());
            ps.setString(2, usuario.getNombre());
            ps.setString(3, usuario.getSexo());
            
            // Manejo de valores que pueden ser nulos (edad)
            if (usuario.getEdad() != null) {
                ps.setInt(4, usuario.getEdad());
            } else {
                ps.setNull(4, Types.SMALLINT);
            }
            
            ps.setString(5, usuario.getTelefono());
            ps.setString(6, usuario.getPais());
            ps.setString(7, usuario.getTipoUsuario());

            // PASO 7: Ejecutar sentencia de actualización (INSERT)
            int filasAfectadas = ps.executeUpdate();
            return filasAfectadas > 0;

        } catch (SQLException e) {
            System.err.println("[JDBC ERROR] Error al registrar usuario: " + e.getMessage());
            return false;
        }
    }

    // ========================================================================
    // OPERACIÓN 2A: READ (Consulta individual por ID)
    // ========================================================================
    @Override
    public Usuario buscarPorId(String idUsuario) {
        if (idUsuario == null || idUsuario.trim().isEmpty()) {
            return null;
        }

        /*
         * PASOS JDBC APLICADOS:
         * Paso 5: Conexión mediante DriverManager/ConexionBD.
         * Paso 6: Crear PreparedStatement con la consulta parametrizada.
         * Paso 7: Ejecutar consulta con executeQuery() obteniendo un ResultSet.
         * Paso 8: Procesar el cursor ResultSet con next() y extractores getX().
         */
        try (Connection con = ConexionBD.obtenerConexion();
             PreparedStatement ps = con.prepareStatement(SQL_SELECT_BY_ID)) {

            ps.setString(1, idUsuario.trim());

            // PASO 7: Ejecutar consulta de lectura
            try (ResultSet rs = ps.executeQuery()) {
                // PASO 8: Procesar el resultado
                if (rs.next()) {
                    return mapearUsuarioDesdeResultSet(rs);
                }
            }

        } catch (SQLException e) {
            System.err.println("[JDBC ERROR] Error al buscar usuario por ID: " + e.getMessage());
        }
        return null;
    }

    // ========================================================================
    // OPERACIÓN 2B: READ (Consulta masiva / Listar todos)
    // ========================================================================
    @Override
    public List<Usuario> listarTodos() {
        List<Usuario> lista = new ArrayList<>();

        /*
         * PASOS JDBC APLICADOS:
         * Paso 5: Conexión activa.
         * Paso 6: Objeto Statement / PreparedStatement preparado.
         * Paso 7: executeQuery() retorna el ResultSet completo.
         * Paso 8: Ciclo while con rs.next() para recorrer cada fila de la tabla.
         */
        try (Connection con = ConexionBD.obtenerConexion();
             PreparedStatement ps = con.prepareStatement(SQL_SELECT_ALL);
             ResultSet rs = ps.executeQuery()) {

            // PASO 8: Iterar sobre el cursor del ResultSet fila por fila
            while (rs.next()) {
                Usuario usuario = mapearUsuarioDesdeResultSet(rs);
                lista.add(usuario);
            }

        } catch (SQLException e) {
            System.err.println("[JDBC ERROR] Error al listar usuarios: " + e.getMessage());
        }
        return lista;
    }

    // ========================================================================
    // OPERACIÓN 3: UPDATE (Actualización de registros existentes)
    // ========================================================================
    @Override
    public boolean actualizar(Usuario usuario) {
        if (usuario == null || usuario.getIdUsuario() == null) {
            System.err.println("[VALIDACIÓN] Datos de usuario insuficientes para actualizar.");
            return false;
        }

        /*
         * PASOS JDBC APLICADOS:
         * Paso 5: Conexión.
         * Paso 6: PreparedStatement con sentencia UPDATE y parámetros.
         * Paso 7: executeUpdate() retorna el número de filas modificadas.
         */
        try (Connection con = ConexionBD.obtenerConexion();
             PreparedStatement ps = con.prepareStatement(SQL_UPDATE)) {

            ps.setString(1, usuario.getNombre());
            ps.setString(2, usuario.getSexo());

            if (usuario.getEdad() != null) {
                ps.setInt(3, usuario.getEdad());
            } else {
                ps.setNull(3, Types.SMALLINT);
            }

            ps.setString(4, usuario.getTelefono());
            ps.setString(5, usuario.getPais());
            ps.setString(6, usuario.getTipoUsuario());
            ps.setString(7, usuario.getIdUsuario().trim());

            int filasAfectadas = ps.executeUpdate();
            return filasAfectadas > 0;

        } catch (SQLException e) {
            System.err.println("[JDBC ERROR] Error al actualizar usuario: " + e.getMessage());
            return false;
        }
    }

    // ========================================================================
    // OPERACIÓN 4: DELETE (Eliminación de registros)
    // ========================================================================
    @Override
    public boolean eliminar(String idUsuario) {
        if (idUsuario == null || idUsuario.trim().isEmpty()) {
            System.err.println("[VALIDACIÓN] ID de usuario inválido para eliminar.");
            return false;
        }

        /*
         * PASOS JDBC APLICADOS:
         * Paso 5: Conexión.
         * Paso 6: PreparedStatement con sentencia DELETE.
         * Paso 7: executeUpdate() para impactar la tabla.
         */
        try (Connection con = ConexionBD.obtenerConexion();
             PreparedStatement ps = con.prepareStatement(SQL_DELETE)) {

            ps.setString(1, idUsuario.trim());

            int filasAfectadas = ps.executeUpdate();
            return filasAfectadas > 0;

        } catch (SQLException e) {
            System.err.println("[JDBC ERROR] Error al eliminar usuario: " + e.getMessage());
            return false;
        }
    }

    // ========================================================================
    // MÉTODO AUXILIAR: Mapeo de ResultSet a Objeto Usuario (Paso 8)
    // ========================================================================
    /**
     * Extrae las columnas de la fila actual apuntada por el cursor del ResultSet
     * y construye una instancia del modelo Usuario.
     * 
     * @param rs Cursor posicionado en una fila válida
     * @return Objeto Usuario mapeado
     * @throws SQLException Si ocurre un error al leer del cursor
     */
    private Usuario mapearUsuarioDesdeResultSet(ResultSet rs) throws SQLException {
        Usuario u = new Usuario();
        u.setIdUsuario(rs.getString("id_usuario"));
        u.setNombre(rs.getString("nombre"));
        u.setSexo(rs.getString("sexo"));

        int edad = rs.getInt("edad");
        if (rs.wasNull()) {
            u.setEdad(null);
        } else {
            u.setEdad(edad);
        }

        u.setTelefono(rs.getString("telefono"));
        u.setPais(rs.getString("pais"));
        u.setTipoUsuario(rs.getString("tipo_usuario"));
        return u;
    }
}
