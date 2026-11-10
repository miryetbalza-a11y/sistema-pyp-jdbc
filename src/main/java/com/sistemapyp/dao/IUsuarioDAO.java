package com.sistemapyp.dao;

import com.sistemapyp.model.Usuario;
import java.util.List;

/**
 * Interfaz IUsuarioDAO
 * 
 * Define el contrato de persistencia para el módulo de usuarios del Sistema PyP.
 * Establece las cuatro operaciones CRUD fundamentales requeridas por la evidencia:
 * - Create: registrar
 * - Read: buscarPorId, listarTodos
 * - Update: actualizar
 * - Delete: eliminar
 * 
 * @author SENA ADSO - Sistema PyP
 * @version 1.0
 */
public interface IUsuarioDAO {

    /**
     * Operación CREATE: Inserta un nuevo registro de usuario en la base de datos.
     * 
     * @param usuario Objeto con la información a persistir
     * @return true si se insertó con éxito, false en caso contrario
     */
    boolean registrar(Usuario usuario);

    /**
     * Operación READ (Individual): Consulta un usuario a partir de su documento / ID.
     * 
     * @param idUsuario Identificador del usuario a consultar
     * @return Objeto Usuario con sus datos cargados, o null si no existe
     */
    Usuario buscarPorId(String idUsuario);

    /**
     * Operación READ (General): Obtiene el listado completo de usuarios registrados.
     * 
     * @return Lista de objetos Usuario
     */
    List<Usuario> listarTodos();

    /**
     * Operación UPDATE: Actualiza los datos de un usuario existente.
     * 
     * @param usuario Objeto con los datos modificados
     * @return true si se actualizó al menos un registro, false en caso contrario
     */
    boolean actualizar(Usuario usuario);

    /**
     * Operación DELETE: Elimina un usuario a partir de su identificador.
     * 
     * @param idUsuario Identificador del registro a remover
     * @return true si se eliminó con éxito, false en caso contrario
     */
    boolean eliminar(String idUsuario);
}
