package com.sistemapyp.app;

import com.sistemapyp.config.ConexionBD;
import com.sistemapyp.dao.IUsuarioDAO;
import com.sistemapyp.dao.UsuarioDAOImpl;
import com.sistemapyp.model.Usuario;

import java.util.List;
import java.util.Scanner;

/**
 * Clase Principal de la Aplicación: MenuPyP
 * 
 * Presenta una interfaz de consola interactiva y pedagógica para demostrar
 * el funcionamiento de las operaciones CRUD sobre la tabla 'usuario' del
 * Sistema de Promoción y Prevención en Salud (PyP), aplicando el estándar JDBC
 * del componente formativo SENA CF20.
 * 
 * @author SENA - Sistema PyP - Linyermar Altuzarra & Angie Garzon
 * @version 1.0
 */
public class MenuPyP {

    private final IUsuarioDAO usuarioDAO;
    private final Scanner scanner;

    public MenuPyP() {
        this.usuarioDAO = new UsuarioDAOImpl();
        this.scanner = new Scanner(System.in);
    }

    /**
     * Punto de entrada principal de la aplicación.
     */
    public static void main(String[] args) {
        MenuPyP app = new MenuPyP();

        // Si se pasa el argumento "--auto", ejecuta la demostración automática sin esperar teclado
        if (args.length > 0 && "--auto".equalsIgnoreCase(args[0])) {
            app.ejecutarDemostracionAutomatica();
        } else {
            app.iniciarMenu();
        }
    }

    /**
     * Despliega el menú interactivo por consola.
     */
    public void iniciarMenu() {
        int opcion = -1;
        do {
            mostrarEncabezado();
            System.out.println(" 1. [CREATE]  Registrar nuevo usuario / paciente");
            System.out.println(" 2. [READ]    Listar todos los usuarios");
            System.out.println(" 3. [READ]    Buscar usuario por documento");
            System.out.println(" 4. [UPDATE]  Actualizar datos de un usuario");
            System.out.println(" 5. [DELETE]  Eliminar un usuario");
            System.out.println(" 6. [JDBC]    Verificar conexión con MySQL (8 pasos)");
            System.out.println(" 7. [DEMO]    Ejecutar demostración automática completa");
            System.out.println(" 0. Salir de la aplicación");
            System.out.println("---------------------------------------------------------------------------------");
            System.out.print("Seleccione una opción: ");

            try {
                String linea = scanner.nextLine();
                opcion = Integer.parseInt(linea.trim());
            } catch (NumberFormatException e) {
                opcion = -1;
            }

            switch (opcion) {
                case 1 -> registrarUsuario();
                case 2 -> listarUsuarios();
                case 3 -> buscarUsuario();
                case 4 -> actualizarUsuario();
                case 5 -> eliminarUsuario();
                case 6 -> ConexionBD.probarConexion();
                case 7 -> ejecutarDemostracionAutomatica();
                case 0 -> System.out.println("\n¡Gracias por utilizar el Sistema PyP! Hasta pronto.");
                default -> System.out.println("\n[AVISO] Opción no válida. Por favor, intente nuevamente.");
            }

            if (opcion != 0) {
                System.out.println("\nPresione ENTER para continuar...");
                scanner.nextLine();
            }

        } while (opcion != 0);
    }

    private void mostrarEncabezado() {
        System.out.println("\n=================================================================================");
        System.out.println("            SISTEMA DE PROMOCIÓN Y PREVENCIÓN EN SALUD (PyP)");
        System.out.println("              EVIDENCIA: GA7-220501096-AA2-EV01 - SENA ADSO");
        System.out.println("=================================================================================");
    }

    /**
     * Operación CREATE: Pide los datos por teclado y los envía al DAO.
     */
    private void registrarUsuario() {
        System.out.println("\n--- [CREATE] REGISTRAR NUEVO USUARIO / PACIENTE ---");

        System.out.print("Documento de identidad (ID): ");
        String id = scanner.nextLine().trim();

        // Validar que no exista previamente
        Usuario existente = usuarioDAO.buscarPorId(id);
        if (existente != null) {
            System.out.println("[ERROR] Ya existe un usuario con el ID " + id + ": " + existente.getNombre());
            return;
        }

        System.out.print("Nombre completo: ");
        String nombre = scanner.nextLine().trim();

        System.out.print("Sexo (Masculino/Femenino/Otro): ");
        String sexo = scanner.nextLine().trim();

        System.out.print("Edad (0 a 120): ");
        Integer edad = null;
        try {
            String edadStr = scanner.nextLine().trim();
            if (!edadStr.isEmpty()) {
                edad = Integer.parseInt(edadStr);
            }
        } catch (NumberFormatException e) {
            System.out.println("[AVISO] Formato de edad no válido, se registrará sin edad.");
        }

        System.out.print("Teléfono de contacto: ");
        String telefono = scanner.nextLine().trim();

        System.out.print("País: ");
        String pais = scanner.nextLine().trim();

        System.out.print("Tipo de usuario (1: PACIENTE / 2: PERSONAL_SALUD): ");
        String tipoOpcion = scanner.nextLine().trim();
        String tipoUsuario = "2".equals(tipoOpcion) ? "PERSONAL_SALUD" : "PACIENTE";

        Usuario nuevo = new Usuario(id, nombre, sexo, edad, telefono, pais, tipoUsuario);
        boolean exito = usuarioDAO.registrar(nuevo);

        if (exito) {
            System.out.println("\n[ÉXITO] Usuario registrado correctamente en MySQL.");
        } else {
            System.out.println("\n[FALLO] No se pudo registrar el usuario en la base de datos.");
        }
    }

    /**
     * Operación READ: Muestra todos los usuarios en formato de tabla.
     */
    private void listarUsuarios() {
        System.out.println("\n--- [READ] LISTADO GENERAL DE USUARIOS ---");
        List<Usuario> usuarios = usuarioDAO.listarTodos();

        if (usuarios.isEmpty()) {
            System.out.println("No hay usuarios registrados actualmente en la base de datos.");
            return;
        }

        imprimirCabeceraTabla();
        for (Usuario u : usuarios) {
            System.out.println(u);
        }
        imprimirPieTabla(usuarios.size());
    }

    /**
     * Operación READ: Consulta un usuario por su documento.
     */
    private void buscarUsuario() {
        System.out.println("\n--- [READ] CONSULTAR USUARIO POR DOCUMENTO ---");
        System.out.print("Ingrese el documento a buscar: ");
        String id = scanner.nextLine().trim();

        Usuario usuario = usuarioDAO.buscarPorId(id);
        if (usuario != null) {
            System.out.println("\n[RESULTADO] Usuario encontrado:");
            imprimirCabeceraTabla();
            System.out.println(usuario);
            imprimirSeparadorTabla();
        } else {
            System.out.println("\n[AVISO] No se encontró ningún usuario con el ID: " + id);
        }
    }

    /**
     * Operación UPDATE: Modifica los datos de un usuario existente.
     */
    private void actualizarUsuario() {
        System.out.println("\n--- [UPDATE] ACTUALIZAR DATOS DE UN USUARIO ---");
        System.out.print("Ingrese el documento del usuario a modificar: ");
        String id = scanner.nextLine().trim();

        Usuario actual = usuarioDAO.buscarPorId(id);
        if (actual == null) {
            System.out.println("\n[ERROR] No existe ningún usuario con el ID: " + id);
            return;
        }

        System.out.println("\nDatos actuales del usuario:");
        imprimirCabeceraTabla();
        System.out.println(actual);
        imprimirSeparadorTabla();

        System.out.println("(Deje el campo en blanco y presione ENTER si desea conservar el valor actual)");

        System.out.print("Nuevo nombre [" + actual.getNombre() + "]: ");
        String nombre = scanner.nextLine().trim();
        if (!nombre.isEmpty()) actual.setNombre(nombre);

        System.out.print("Nuevo sexo [" + actual.getSexo() + "]: ");
        String sexo = scanner.nextLine().trim();
        if (!sexo.isEmpty()) actual.setSexo(sexo);

        System.out.print("Nueva edad [" + actual.getEdad() + "]: ");
        String edadStr = scanner.nextLine().trim();
        if (!edadStr.isEmpty()) {
            try {
                actual.setEdad(Integer.parseInt(edadStr));
            } catch (NumberFormatException e) {
                System.out.println("[AVISO] Edad inválida, se conserva la anterior.");
            }
        }

        System.out.print("Nuevo teléfono [" + actual.getTelefono() + "]: ");
        String telefono = scanner.nextLine().trim();
        if (!telefono.isEmpty()) actual.setTelefono(telefono);

        System.out.print("Nuevo país [" + actual.getPais() + "]: ");
        String pais = scanner.nextLine().trim();
        if (!pais.  isEmpty()) actual.setPais(pais);

        System.out.print("Nuevo tipo de usuario (1: PACIENTE / 2: PERSONAL_SALUD) [" + actual.getTipoUsuario() + "]: ");
        String tipoOpcion = scanner.nextLine().trim();
        if ("1".equals(tipoOpcion)) actual.setTipoUsuario("PACIENTE");
        if ("2".equals(tipoOpcion)) actual.setTipoUsuario("PERSONAL_SALUD");

        boolean exito = usuarioDAO.actualizar(actual);
        if (exito) {
            System.out.println("\n[ÉXITO] Usuario actualizado correctamente en MySQL.");
        } else {
            System.out.println("\n[FALLO] No se pudo actualizar el registro.");
        }
    }

    /**
     * Operación DELETE: Elimina un usuario por su documento.
     */
    private void eliminarUsuario() {
        System.out.println("\n--- [DELETE] ELIMINAR USUARIO DEL SISTEMA ---");
        System.out.print("Ingrese el documento del usuario a eliminar: ");
        String id = scanner.nextLine().trim();

        Usuario actual = usuarioDAO.buscarPorId(id);
        if (actual == null) {
            System.out.println("\n[ERROR] No existe ningún usuario con el ID: " + id);
            return;
        }

        System.out.println("\n¿Está seguro de eliminar a " + actual.getNombre() + " (ID: " + id + ")?");
        System.out.print("Escriba 'S' para confirmar: ");
        String confirmacion = scanner.nextLine().trim();

        if ("S".equalsIgnoreCase(confirmacion)) {
            boolean exito = usuarioDAO.eliminar(id);
            if (exito) {
                System.out.println("\n[ÉXITO] Usuario eliminado correctamente de la base de datos.");
            } else {
                System.out.println("\n[FALLO] No se pudo eliminar el usuario.");
            }
        } else {
            System.out.println("\n[CANCELADO] Operación cancelada por el usuario.");
        }
    }

    /**
     * Demostración automática pedagógica: Ejecuta en secuencia las 4 operaciones
     * CRUD para validar todo el flujo sin requerir interacción manual.
     */
    public void ejecutarDemostracionAutomatica() {
        System.out.println("\n=================================================================================");
        System.out.println("   DEMOSTRACIÓN PEDAGÓGICA AUTOMÁTICA DEL FLUJO CRUD - SISTEMA PyP");
        System.out.println("=================================================================================");

        String testId = "CC10203040";

        // Limpiar registro de prueba si existía
        usuarioDAO.eliminar(testId);

        // 1. CREATE
        System.out.println("\n[PASO 1: CREATE] Insertando usuario de prueba: " + testId);
        Usuario nuevo = new Usuario(
            testId,
            "María Paula Rodríguez",
            "Femenino",
            28,
            "+57 3105559876",
            "Colombia",
            "PACIENTE"
        );
        boolean insertado = usuarioDAO.registrar(nuevo);
        System.out.println("Resultado de inserción: " + (insertado ? "EXITOSO" : "FALLÓ"));

        // 2. READ (Individual)
        System.out.println("\n[PASO 2: READ (Individual)] Consultando usuario con ID " + testId);
        Usuario leido = usuarioDAO.buscarPorId(testId);
        if (leido != null) {
            imprimirCabeceraTabla();
            System.out.println(leido);
            imprimirSeparadorTabla();
        }

        // 3. UPDATE
        System.out.println("\n[PASO 3: UPDATE] Actualizando teléfono y edad de " + testId);
        if (leido != null) {
            leido.setTelefono("+57 3001112233");
            leido.setEdad(29);
            boolean actualizado = usuarioDAO.actualizar(leido);
            System.out.println("Resultado de actualización: " + (actualizado ? "EXITOSO" : "FALLÓ"));

            Usuario verificado = usuarioDAO.buscarPorId(testId);
            System.out.println("Verificación de datos actualizados:");
            imprimirCabeceraTabla();
            System.out.println(verificado);
            imprimirSeparadorTabla();
        }

        // 4. READ (General)
        System.out.println("\n[PASO 4: READ (General)] Listado de todos los usuarios en la base:");
        List<Usuario> lista = usuarioDAO.listarTodos();
        imprimirCabeceraTabla();
        for (Usuario u : lista) {
            System.out.println(u);
        }
        imprimirPieTabla(lista.size());

        // 5. DELETE
        System.out.println("\n[PASO 5: DELETE] Eliminando usuario de prueba: " + testId);
        boolean eliminado = usuarioDAO.eliminar(testId);
        System.out.println("Resultado de eliminación: " + (eliminado ? "EXITOSO" : "FALLÓ"));

        // Verificar que ya no existe
        Usuario postDelete = usuarioDAO.buscarPorId(testId);
        System.out.println("Verificación post-eliminación (debe ser null): " + postDelete);

        System.out.println("\n=================================================================================");
        System.out.println("  ¡DEMOSTRACIÓN CRUD FINALIZADA CON ÉXITO CUMPLIENDO LOS 8 PASOS JDBC!");
        System.out.println("=================================================================================\n");
    }

    private void imprimirCabeceraTabla() {
        imprimirSeparadorTabla();
        System.out.println(String.format(
            "| %-15s | %-25s | %-10s | %-4s | %-12s | %-12s | %-15s |",
            "DOCUMENTO (ID)", "NOMBRE COMPLETO", "SEXO", "EDAD", "TELÉFONO", "PAÍS", "TIPO USUARIO"
        ));
        imprimirSeparadorTabla();
    }

    private void imprimirSeparadorTabla() {
        System.out.println("+-----------------+---------------------------+------------+------+--------------+--------------+-----------------+");
    }

    private void imprimirPieTabla(int total) {
        imprimirSeparadorTabla();
        System.out.println("Total de registros cargados: " + total);
    }
}
