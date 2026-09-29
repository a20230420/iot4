package com.example.labmodelo.entity;

import java.io.Serializable;

// >>> CAMBIAR AQUÍ <<< entidad/DTO: los nombres de los atributos deben coincidir con las claves del JSON
// (o usar @SerializedName("clave_json") de Gson si quieres otro nombre en Java).
// Serializable es lo que permite pasarla como argumento por Safe Args (Clase6: Persona).
public class Usuario implements Serializable {

    private int id;
    private String name;
    private String username;
    private String email;
    private String phone;
    private String website;
    private Company company;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public String getWebsite() {
        return website;
    }

    public void setWebsite(String website) {
        this.website = website;
    }

    public Company getCompany() {
        return company;
    }

    public void setCompany(Company company) {
        this.company = company;
    }
}
