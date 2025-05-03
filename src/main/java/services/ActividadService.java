package services;

import models.*;
import java.util.List;

public class ActividadService {
    private Cultivo cultivo;

    public ActividadService(Cultivo cultivo) { this.cultivo = cultivo; }

    public void registrarActividad(Actividad a) { cultivo.addActividad(a); }
    public List<Actividad> listarActividades() { return cultivo.getActividades(); }
    public boolean eliminarActividad(Actividad a) { return cultivo.removeActividad(a); }
    public void marcarCompletada(Actividad a) { a.setCompletada(true); }
}
