// src/main/java/ui/ReporteWindow.java
package ui;

import javafx.beans.property.ReadOnlyObjectWrapper;  // <<— add this
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.stage.Stage;
import models.Cultivo;
import models.EstadoCultivo;
import services.CultivoService;

public class ReporteWindow extends Stage {
    private final CultivoService cultivoService;

    public ReporteWindow(CultivoService cs) {
        this.cultivoService = cs;
        setTitle("Búsqueda y Reporte");
        initUI();
    }

    private void initUI() {
        BorderPane root = new BorderPane();
        TabPane tabs = new TabPane();

        // ——— Pestaña “Buscar” ———
        Tab tabBuscar = new Tab("Buscar");
        VBox vbBuscar = new VBox(10);
        vbBuscar.setPadding(new Insets(10));

        TextField tfBuscar = new TextField();
        tfBuscar.setPromptText("Nombre o variedad");
        Button btnBuscar = new Button("Buscar");

        TableView<Cultivo> tblBuscar = new TableView<>();
        configureTable(tblBuscar);

        btnBuscar.setOnAction(e -> {
            String term = tfBuscar.getText().trim();
            ObservableList<Cultivo> resultados =
                FXCollections.observableArrayList(
                    cultivoService.buscarPorNombreOVariedad(term)
                );
            tblBuscar.setItems(resultados);
        });

        vbBuscar.getChildren().addAll(
            new Label("Término de búsqueda:"),
            tfBuscar,
            btnBuscar,
            tblBuscar
        );
        tabBuscar.setContent(vbBuscar);

        // ——— Pestaña “Reporte por Estado” ———
        Tab tabReporte = new Tab("Reporte");
        VBox vbReporte = new VBox(10);
        vbReporte.setPadding(new Insets(10));

        ChoiceBox<EstadoCultivo> cbEstado =
            new ChoiceBox<>(FXCollections.observableArrayList(EstadoCultivo.values()));
        cbEstado.setValue(EstadoCultivo.ACTIVO);

        Button btnGenerar = new Button("Generar reporte");

        TableView<Cultivo> tblReporte = new TableView<>();
        configureTable(tblReporte);

        btnGenerar.setOnAction(e -> {
            EstadoCultivo sel = cbEstado.getValue();
            ObservableList<Cultivo> rpt =
                FXCollections.observableArrayList(cultivoService.reportePorEstado(sel));
            tblReporte.setItems(rpt);
        });

        vbReporte.getChildren().addAll(
            new Label("Seleccione Estado:"),
            cbEstado,
            btnGenerar,
            tblReporte
        );
        tabReporte.setContent(vbReporte);

        tabs.getTabs().addAll(tabBuscar, tabReporte);
        root.setCenter(tabs);

        // ——— Botón Volver ———
        HBox bottom = new HBox();
        bottom.setPadding(new Insets(10));
        Button btnBack = new Button("Volver");
        btnBack.setOnAction(e -> this.close());
        bottom.getChildren().add(btnBack);
        root.setBottom(bottom);

        setScene(new Scene(root, 700, 500));
    }

    /**
     * Añade las columnas comunes de cultivo a una tabla dada.
     */
    private void configureTable(TableView<Cultivo> table) {
        TableColumn<Cultivo, String> colNom = new TableColumn<>("Nombre");
        colNom.setCellValueFactory(c -> 
            new ReadOnlyObjectWrapper<>(c.getValue().getNombre())
        );

        TableColumn<Cultivo, String> colVar = new TableColumn<>("Variedad");
        colVar.setCellValueFactory(c -> 
            new ReadOnlyObjectWrapper<>(c.getValue().getVariedad())
        );

        TableColumn<Cultivo, Double> colSup = new TableColumn<>("Superficie");
        colSup.setCellValueFactory(c -> 
            new ReadOnlyObjectWrapper<>(c.getValue().getSuperficie())
        );

        TableColumn<Cultivo, EstadoCultivo> colEst = new TableColumn<>("Estado");
        colEst.setCellValueFactory(c -> 
            new ReadOnlyObjectWrapper<>(c.getValue().getEstadoEnum())
        );

        TableColumn<Cultivo, String> colPar = new TableColumn<>("Parcela");
        colPar.setCellValueFactory(c -> 
            new ReadOnlyObjectWrapper<>(c.getValue().getParcela().getCodigo())
        );

        table.getColumns().setAll(colNom, colVar, colSup, colEst, colPar);
    }
}
