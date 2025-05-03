package services;

import models.*;
import java.util.*;

public class ParcelaService {
    private Map<String, Parcela> parcelas;

    public ParcelaService(Collection<Parcela> lista) {
        this.parcelas = new HashMap<>();
        for (Parcela p : lista) parcelas.put(p.getCodigo(), p);
    }

    public Map<String, Parcela> getParcelas() { return parcelas; }

    public boolean agregarParcela(String codigo, double area, String ubicacion) {
        if (parcelas.containsKey(codigo)) return false;
        parcelas.put(codigo, new Parcela(codigo, area, ubicacion));
        return true;
    }

    public boolean eliminarParcela(String codigo) {
        Parcela p = parcelas.get(codigo);
        if (p == null || !p.getCultivos().isEmpty()) return false;
        parcelas.remove(codigo);
        return true;
    }

    public boolean editarParcela(String codigo, double area, String ubicacion) {
        Parcela p = parcelas.get(codigo);
        if (p == null) return false;
        p.setArea(area); p.setUbicacion(ubicacion);
        return true;
    }

    public boolean asignarCultivo(String codigo, Cultivo c) {
        Parcela p = parcelas.get(codigo);
        if (p == null) return false;
        c.setParcela(p);
        p.addCultivo(c);
        return true;
    }
}