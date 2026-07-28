package com.mdtm.aliviababa.dto;

public class RegistroRequest {

    private String username;
    private String password;
    private String email;
    private String nombre;
    private String direccion;
    private String telefono;
    private String rol;
    
    public RegistroRequest(String username, String password, String email, String nombre, String direccion, String telefono,
            String rol) {
        this.username = username;
        this.password = password;
        this.email = email;
        this.nombre = nombre;
        this.direccion = direccion;
        this.telefono = telefono;
        this.rol = rol;
    }
    
    public String getUsername() {
        return username;
    }
    public void setUsername(String username) {
        this.username = username;
    }
    public String getPassword() {
        return password;
    }
    public void setPassword(String password) {
        this.password = password;
    }
    public String getEmail() {
        return email;
    }
    public void setEmail(String email) {
        this.email = email;
    }
    public String getNombre() {
        return nombre;
    }
    public void setNombre(String nombre) {
        this.nombre = nombre;
    }
    public String getDireccion() {
        return direccion;
    }
    public void setDireccion(String direccion) {
        this.direccion = direccion;
    }
    public String getTelefono() {
        return telefono;
    }
    public void setTelefono(String telefono) {
        this.telefono = telefono;
    }
    public String getRol() {
        return rol;
    }
    public void setRol(String rol) {
        this.rol = rol;
    }

    @Override
    public String toString() {
        return "RegistroRequest [username=" + username + ", password=" + password + ", nombre=" + nombre
                + ", direccion=" + direccion + ", telefono=" + telefono + ", rol=" + rol + "]";
    }
    
}


