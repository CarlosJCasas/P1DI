package com.example.p1di.core;

import java.text.SimpleDateFormat;
import java.util.Date;

public class Tarea {

    private String titulo;
    private Date fechaLimite;

    public Tarea(String titulo, Date fechaLimite) {
        this.titulo = titulo;
        this.fechaLimite = fechaLimite;
    }

    public String getTitulo() {
        return titulo;
    }

    public void setTitulo(String titulo) {
        this.titulo = titulo;
    }

    public Date getFechaLimite() {
        String pattern = "dd-MM-yyyy";
        SimpleDateFormat format = new SimpleDateFormat(pattern);
        return fechaLimite;
    }

    public void setFechaLimite(Date fechaLimite) {
        this.fechaLimite = fechaLimite;
    }
}
