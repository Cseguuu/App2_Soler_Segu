// src/main/java/ui/ActividadRow.java
package ui;

import javafx.beans.property.*;
import models.Actividad;
import models.Cultivo;

import java.time.LocalDate;

public class ActividadRow {
    private final Cultivo cultivo;
    private final Actividad actividad;

    public ActividadRow(Cultivo cultivo, Actividad actividad) {
        this.cultivo   = cultivo;
        this.actividad = actividad;
    }

    public Cultivo getCultivo() { return cultivo; }
    public Actividad getActividad() { return actividad; }
    public LocalDate getFecha() { return actividad.getFecha(); }

    // Exponer propiedades para TableView
    public StringProperty tipoProperty() {
        return new SimpleStringProperty(actividad.getTipo().name());
    }
    public StringProperty estadoProperty() {
        return new SimpleStringProperty(
            actividad.isCompletada() ? "COMPLETADA" : "PENDIENTE"
        );
    }
}
