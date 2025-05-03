package services;

import models.*;
import java.time.LocalDate;
import java.util.List;
import java.util.stream.Collectors;

public class CultivoService {
    private List<Cultivo> cultivos;

    public CultivoService(List<Cultivo> cultivos) { this.cultivos = cultivos; }

    public List<Cultivo> getCultivos() { return cultivos; }

    public void agregarCultivo(String nombre, String variedad, double superficie,
                               Parcela parcela, LocalDate fechaSiembra, EstadoCultivo estado) {
        Cultivo c = new Cultivo(nombre, variedad, superficie, parcela, fechaSiembra, estado);
        cultivos.add(c);
        parcela.addCultivo(c);
    }

    public boolean eliminarCultivo(Cultivo c) {
        if (c.getActividades().stream().anyMatch(a -> !a.isCompletada())) return false;
        c.getParcela().removeCultivo(c);
        return cultivos.remove(c);
    }

    public void editarCultivo(Cultivo c, String nombre, String variedad,
                              double sup, LocalDate fecha, EstadoCultivo est) {
        c.setNombre(nombre); c.setVariedad(variedad);
        c.setSuperficie(sup); c.setFechaSiembra(fecha);
        c.setEstado(est);
    }

    public List<Cultivo> buscarPorNombreOVariedad(String term) {
        return cultivos.stream()
            .filter(c -> c.getNombre().equalsIgnoreCase(term)
                      || c.getVariedad().equalsIgnoreCase(term))
            .collect(Collectors.toList());
    }

    public List<Cultivo> reportePorEstado(EstadoCultivo estado) {
        return cultivos.stream()
            // Aquí usamos getEstadoEnum() que devuelve el enum real
            .filter(c -> c.getEstadoEnum() == estado)
            .collect(Collectors.toList());
    }
}