// src/ui/ActividadWindow.java
package ui;

import javafx.collections.FXCollections;
import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import models.Actividad;
import models.Cultivo;
import services.ActividadService;
import services.CultivoService;

import java.util.List;

public class ActividadWindow extends Stage {
    private final CultivoService cs;
    private ActividadService as;
    private final TableView<Actividad> table = new TableView<>();
    private final ComboBox<Cultivo> cbCultivos = new ComboBox<>();

    public ActividadWindow(CultivoService cs) {
        this.cs = cs;
        setTitle("Gestión de Actividades");
        initUI();
    }

    private void initUI() {
        BorderPane root = new BorderPane();
        VBox topBox = new VBox(10);
        topBox.setPadding(new Insets(10));
        cbCultivos.setItems(FXCollections.observableArrayList(cs.getCultivos()));
        cbCultivos.setPromptText("Seleccione cultivo");
        cbCultivos.setOnAction(e -> {
            Cultivo sel = cbCultivos.getValue();
            if (sel != null) {
                as = new ActividadService(sel);
                listar();
            }
        });

        HBox btnBar = new HBox(10);
        Button reg = new Button("Registrar");
        reg.setOnAction(e -> {/* diálogo para registrar */});
        Button del = new Button("Eliminar");
        del.setOnAction(e -> {/* diálogo para eliminar */});
        Button comp = new Button("Marcar completada");
        comp.setOnAction(e -> {/* diálogo para marcar */});
        Button cerrar = new Button("Cerrar");
        cerrar.setOnAction(e -> close());
        btnBar.getChildren().addAll(reg, del, comp, cerrar);
        topBox.getChildren().addAll(cbCultivos, btnBar);
        root.setTop(topBox);

        TableColumn<Actividad, ?> colTipo = new TableColumn<>("Tipo");
        colTipo.setCellValueFactory(new PropertyValueFactory<>("tipo"));
        TableColumn<Actividad, ?> colFecha = new TableColumn<>("Fecha");
        colFecha.setCellValueFactory(new PropertyValueFactory<>("fecha"));
        TableColumn<Actividad, ?> colEst = new TableColumn<>("Estado");
        colEst.setCellValueFactory(new PropertyValueFactory<>("estado"));
        table.getColumns().addAll(colTipo, colFecha, colEst);
        root.setCenter(table);

        setScene(new Scene(root, 600, 450));
    }

    private void listar() {
        if (as != null) {
            List<Actividad> acts = as.listarActividades();
            table.getItems().setAll(acts);
        }
    }
}
