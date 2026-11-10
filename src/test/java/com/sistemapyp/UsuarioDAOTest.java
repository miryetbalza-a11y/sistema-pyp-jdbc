package com.sistemapyp;

import com.sistemapyp.config.ConexionBD;
import com.sistemapyp.dao.IUsuarioDAO;
import com.sistemapyp.dao.UsuarioDAOImpl;
import com.sistemapyp.model.Usuario;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Pruebas Unitarias e Integración para la capa de persistencia (JDBC)
 * Evidencia: GA7-220501096-AA2-EV01
 */
class UsuarioDAOTest {

    private IUsuarioDAO usuarioDAO;
    private static final String ID_TEST = "CC99887766";

    @BeforeEach
    void setUp() {
        usuarioDAO = new UsuarioDAOImpl();
        // Garantizar limpieza antes de cada prueba
        usuarioDAO.eliminar(ID_TEST);
    }

    @AfterEach
    void tearDown() {
        // Limpieza posterior
        usuarioDAO.eliminar(ID_TEST);
    }

    @Test
    @DisplayName("Paso 5 JDBC: Verificar conexión activa mediante ConexionBD")
    void testConexionExitosa() throws SQLException {
        try (Connection con = ConexionBD.obtenerConexion()) {
            assertNotNull(con, "La conexión no debe ser nula");
            assertFalse(con.isClosed(), "La conexión debe encontrarse abierta");
            assertEquals("sistema_pyp", con.getCatalog(), "El catálogo activo debe ser sistema_pyp");
        }
    }

    @Test
    @DisplayName("CRUD: Flujo completo Create, Read, Update, Delete para Usuario")
    void testFlujoCrudCompleto() {
        // 1. CREATE
        Usuario nuevo = new Usuario(
            ID_TEST,
            "Carlos Eduardo Mendoza",
            "Masculino",
            35,
            "+57 3124445566",
            "Colombia",
            "PACIENTE"
        );
        boolean registrado = usuarioDAO.registrar(nuevo);
        assertTrue(registrado, "El usuario debe registrarse exitosamente en MySQL");

        // 2. READ (Por ID)
        Usuario consultado = usuarioDAO.buscarPorId(ID_TEST);
        assertNotNull(consultado, "El usuario registrado debe ser recuperado");
        assertEquals("Carlos Eduardo Mendoza", consultado.getNombre());
        assertEquals(35, consultado.getEdad());

        // 3. UPDATE
        consultado.setNombre("Carlos E. Mendoza Gómez");
        consultado.setEdad(36);
        boolean actualizado = usuarioDAO.actualizar(consultado);
        assertTrue(actualizado, "El usuario debe actualizarse correctamente");

        Usuario verificado = usuarioDAO.buscarPorId(ID_TEST);
        assertEquals("Carlos E. Mendoza Gómez", verificado.getNombre());
        assertEquals(36, verificado.getEdad());

        // 4. READ (Listar todos)
        List<Usuario> todos = usuarioDAO.listarTodos();
        assertFalse(todos.isEmpty(), "La lista de usuarios no debe estar vacía");
        boolean encontrado = todos.stream().anyMatch(u -> ID_TEST.equals(u.getIdUsuario()));
        assertTrue(encontrado, "El usuario de prueba debe estar en el listado general");

        // 5. DELETE
        boolean eliminado = usuarioDAO.eliminar(ID_TEST);
        assertTrue(eliminado, "El usuario debe eliminarse correctamente");

        Usuario postDelete = usuarioDAO.buscarPorId(ID_TEST);
        assertNull(postDelete, "El usuario ya no debe existir tras ser eliminado");
    }
}
