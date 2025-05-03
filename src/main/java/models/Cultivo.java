package models;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

public class Cultivo implements ElementoAgricola {
    private String nombre;
    private String variedad;
    private double superficie;
    private Parcela parcela;
    private LocalDate fechaSiembra;
    private EstadoCultivo estado;
    private List<Actividad> actividades;

    public Cultivo(String nombre,
                   String variedad,
                   double superficie,
                   Parcela parcela,
                   LocalDate fechaSiembra,
                   EstadoCultivo estado) {
        this.nombre = nombre;
        this.variedad = variedad;
        this.superficie = superficie;
        this.parcela = parcela;
        this.fechaSiembra = fechaSiembra;
        this.estado = estado;
        this.actividades = new ArrayList<>();
    }

    @Override
    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public String getVariedad() {
        return variedad;
    }

    public void setVariedad(String variedad) {
        this.variedad = variedad;
    }

    public double getSuperficie() {
        return superficie;
    }

    public void setSuperficie(double superficie) {
        this.superficie = superficie;
    }

    public Parcela getParcela() {
        return parcela;
    }

    public void setParcela(Parcela parcela) {
        this.parcela = parcela;
    }

    @Override
    public LocalDate getFecha() {
        return fechaSiembra;
    }

    public LocalDate getFechaSiembra() {
        return fechaSiembra;
    }

    public void setFechaSiembra(LocalDate fechaSiembra) {
        this.fechaSiembra = fechaSiembra;
    }

    /**
     * Implementa ElementoAgricola.getEstado(): devuelve String
     */
    @Override
    public String getEstado() {
        return estado.name();
    }

    /**
     * Getter adicional para acceder directamente al enum
     */
    public EstadoCultivo getEstadoEnum() {
        return estado;
    }

    public void setEstado(EstadoCultivo estado) {
        this.estado = estado;
    }

    public List<Actividad> getActividades() {
        return actividades;
    }

    public void addActividad(Actividad actividad) {
        actividades.add(actividad);
    }

    public boolean removeActividad(Actividad actividad) {
        return actividades.remove(actividad);
    }

    @Override
    public String toString() {
        return String.format(
            "Cultivo[nombre=%s, variedad=%s, superficie=%.2f, parcela=%s, fechaSiembra=%s, estado=%s, actividades=%d]",
            nombre, variedad, superficie, parcela.getCodigo(), fechaSiembra, estado, actividades.size()
        );
    }
}
