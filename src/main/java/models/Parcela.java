package models;

import java.util.ArrayList;
import java.util.List;

public class Parcela {
    private String codigo;
    private double area;
    private String ubicacion;
    private List<Cultivo> cultivos;

    public Parcela(String codigo, double area, String ubicacion) {
        this.codigo = codigo;
        this.area = area;
        this.ubicacion = ubicacion;
        this.cultivos = new ArrayList<>();
    }

    public String getCodigo() { return codigo; }
    public double getArea() { return area; }
    public void setArea(double area) { this.area = area; }
    public String getUbicacion() { return ubicacion; }
    public void setUbicacion(String ubicacion) { this.ubicacion = ubicacion; }
    public List<Cultivo> getCultivos() { return cultivos; }

    public void addCultivo(Cultivo cultivo) { cultivos.add(cultivo); }
    public boolean removeCultivo(Cultivo cultivo) { return cultivos.remove(cultivo); }

    @Override
    public String toString() {
        return String.format("Parcela[codigo=%s, area=%.2f, ubicacion=%s, cultivos=%d]",
            codigo, area, ubicacion, cultivos.size());
    }
}