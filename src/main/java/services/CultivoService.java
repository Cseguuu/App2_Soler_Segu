package services;

import models.*;
import java.time.LocalDate;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Servicio de dominio para operaciones sobre la lista de cultivos.
 */
public class CultivoService {

    private final List<Cultivo> cultivos;

    public CultivoService(List<Cultivo> cultivos) { this.cultivos = cultivos; }

    public List<Cultivo> getCultivos() { return cultivos; }

    /* ───────────────────────── métodos YA existentes ───────────────────────── */

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

    /* ───────────────────────── NUEVOS métodos requeridos por la UI ───────────────────────── */

    /** Inserta directamente un objeto Cultivo ya construido. */
    public void agregarCultivo(Cultivo c) {
        cultivos.add(c);
        if (c.getParcela() != null) c.getParcela().addCultivo(c);
    }

    /** Reemplaza en la lista el cultivo `oldC` por `newC`.  */
    public void reemplazarCultivo(Cultivo oldC, Cultivo newC) {
        int idx = cultivos.indexOf(oldC);
        if (idx >= 0) {
            // quitar de la parcela anterior
            if (oldC.getParcela() != null) oldC.getParcela().removeCultivo(oldC);
            cultivos.set(idx, newC);
            // asociar a la (posible) nueva parcela
            if (newC.getParcela() != null) newC.getParcela().addCultivo(newC);
        }
    }

    /* ───────────────────────── búsquedas / reportes ───────────────────────── */

    public List<Cultivo> buscarPorNombreOVariedad(String term) {
        return cultivos.stream()
                .filter(c -> c.getNombre().equalsIgnoreCase(term)
                          || c.getVariedad().equalsIgnoreCase(term))
                .collect(Collectors.toList());
    }

    public List<Cultivo> reportePorEstado(EstadoCultivo estado) {
        return cultivos.stream()
                .filter(c -> c.getEstadoEnum() == estado)
                .collect(Collectors.toList());
    }
}
