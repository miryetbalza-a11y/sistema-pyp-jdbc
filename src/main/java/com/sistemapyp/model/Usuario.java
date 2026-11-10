package com.sistemapyp.model;

/**
 * Clase de Entidad / Modelo: Usuario
 * 
 * Representa la tabla 'usuario' de la base de datos 'sistema_pyp'.
 * Cumple con el estándar de Java Beans (atributos privados, constructor por defecto,
 * getters, setters y representación textual con toString).
 * 
 * Cumple los estándares de nomenclatura de Java:
 * - Paquete en minúsculas: com.sistemapyp.model
 * - Clase en PascalCase: Usuario
 * - Atributos y métodos en camelCase: idUsuario, obtenerNombre(), etc.
 * 
 * @author SENA ADSO - Sistema PyP
 * @version 1.0
 */
public class Usuario {

    // ========================================================================
    // ATRIBUTOS PRIVADOS (Encapsulamiento)
    // ========================================================================
    private String idUsuario;      // Documento de identidad (ej: CC, TI)
    private String nombre;         // Nombre completo del usuario / paciente
    private String sexo;           // Masculino, Femenino, Otro
    private Integer edad;          // Edad (Restricción CHECK entre 0 y 120)
    private String telefono;       // Número de contacto telefónico
    private String pais;           // País de residencia
    private String tipoUsuario;    // 'PACIENTE' o 'PERSONAL_SALUD' (Restricción CHECK)

    // ========================================================================
    // CONSTRUCTORES
    // ========================================================================

    /**
     * Constructor vacío requerido por frameworks y buenas prácticas en POJOs.
     */
    public Usuario() {
    }

    /**
     * Constructor con todos los atributos para inicializar rápidamente instancias.
     * 
     * @param idUsuario Documento o identificador único
     * @param nombre Nombre completo
     * @param sexo Género/Sexo
     * @param edad Edad en años (0-120)
     * @param telefono Teléfono de contacto
     * @param pais País de origen o residencia
     * @param tipoUsuario Tipo de rol ('PACIENTE' o 'PERSONAL_SALUD')
     */
    public Usuario(String idUsuario, String nombre, String sexo, Integer edad, 
                   String telefono, String pais, String tipoUsuario) {
        this.idUsuario = idUsuario;
        this.nombre = nombre;
        this.sexo = sexo;
        this.edad = edad;
        this.telefono = telefono;
        this.pais = pais;
        this.tipoUsuario = tipoUsuario;
    }

    // ========================================================================
    // MÉTODOS GETTERS Y SETTERS (Acceso y mutación controlada)
    // ========================================================================

    public String getIdUsuario() {
        return idUsuario;
    }

    public void setIdUsuario(String idUsuario) {
        this.idUsuario = idUsuario;
    }

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public String getSexo() {
        return sexo;
    }

    public void setSexo(String sexo) {
        this.sexo = sexo;
    }

    public Integer getEdad() {
        return edad;
    }

    public void setEdad(Integer edad) {
        this.edad = edad;
    }

    public String getTelefono() {
        return telefono;
    }

    public void setTelefono(String telefono) {
        this.telefono = telefono;
    }

    public String getPais() {
        return pais;
    }

    public void setPais(String pais) {
        this.pais = pais;
    }

    public String getTipoUsuario() {
        return tipoUsuario;
    }

    public void setTipoUsuario(String tipoUsuario) {
        this.tipoUsuario = tipoUsuario;
    }

    // ========================================================================
    // MÉTODO TOSTRING (Visualización didáctica y depuración)
    // ========================================================================

    @Override
    public String toString() {
        return String.format(
            "| %-15s | %-25s | %-10s | %-4s | %-12s | %-12s | %-15s |",
            idUsuario, 
            (nombre != null ? (nombre.length() > 25 ? nombre.substring(0, 22) + "..." : nombre) : ""),
            (sexo != null ? sexo : "N/A"),
            (edad != null ? edad : "N/A"),
            (telefono != null ? telefono : "N/A"),
            (pais != null ? pais : "N/A"),
            (tipoUsuario != null ? tipoUsuario : "")
        );
    }
}
