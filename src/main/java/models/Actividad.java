package models;

import java.time.LocalDate;

public class Actividad implements ElementoAgricola {
    public enum Tipo { RIEGO, FERTILIZACION, COSECHA, FUMIGACION; }

    private Tipo tipo;
    private LocalDate fecha;
    private boolean completada;

    public Actividad(Tipo tipo, LocalDate fecha) {
        this.tipo = tipo;
        this.fecha = fecha;
        this.completada = false;
    }

    public Tipo getTipo() { return tipo; }
    public LocalDate getFecha() { return fecha; }
    public boolean isCompletada() { return completada; }
    public void setCompletada(boolean completada) { this.completada = completada; }

    @Override
    public String getNombre() { return tipo.name(); }

    @Override
    public String getEstado() { return completada ? "COMPLETADA" : "PENDIENTE"; }
}
