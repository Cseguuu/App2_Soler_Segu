package ui;

import javafx.beans.property.ReadOnlyStringWrapper;
import javafx.collections.FXCollections;
import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.ChoiceBox;
import javafx.scene.control.SplitPane;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.stage.Stage;
import models.Cultivo;
import models.EstadoCultivo;
import services.CultivoService;

import java.util.List;
import java.util.stream.Collectors;

public class ReporteWindow extends Stage {
    private final CultivoService cs;
    private TableView<Cultivo> table;
    private TextField tfBuscar;
    private ChoiceBox<EstadoCultivo> cbEstado;
    private Button btnFiltrar, btnCerrar;

    public ReporteWindow(CultivoService cs) {
        this.cs = cs;
        setTitle("Búsqueda / Reporte");
        initUI();
        cargarInicial();
    }

    private void initUI() {
        BorderPane root = new BorderPane();

        // Barra de filtros
        tfBuscar = new TextField();
        tfBuscar.setPromptText("Nombre o variedad");
        tfBuscar.setOnKeyReleased(e -> filtrar());

        cbEstado = new ChoiceBox<>();
        cbEstado.getItems().add(null);                      // null = todos los estados
        cbEstado.getItems().addAll(EstadoCultivo.values());
        cbEstado.setValue(null);

        btnFiltrar = new Button("Filtrar");
        btnFiltrar.setOnAction(e -> filtrar());

        btnCerrar = new Button("Cerrar");
        btnCerrar.setOnAction(e -> close());

        HBox top = new HBox(8, tfBuscar, cbEstado, btnFiltrar, btnCerrar);
        top.setPadding(new Insets(10));
        root.setTop(top);

        // Tabla única
        table = new TableView<>();

        TableColumn<Cultivo, String> colNom = new TableColumn<>("Nombre");
        colNom.setCellValueFactory(new PropertyValueFactory<>("nombre"));

        TableColumn<Cultivo, String> colVar = new TableColumn<>("Variedad");
        colVar.setCellValueFactory(new PropertyValueFactory<>("variedad"));

        TableColumn<Cultivo, Double> colSup = new TableColumn<>("Superficie");
        colSup.setCellValueFactory(new PropertyValueFactory<>("superficie"));

        TableColumn<Cultivo, ?> colFec = new TableColumn<>("Fecha Siembra");
        colFec.setCellValueFactory(new PropertyValueFactory<>("fechaSiembra"));

        TableColumn<Cultivo, String> colEst = new TableColumn<>("Estado");
        colEst.setCellValueFactory(c ->
            new ReadOnlyStringWrapper(c.getValue().getEstado())
        );

        table.getColumns().addAll(colNom, colVar, colSup, colFec, colEst);
        root.setCenter(table);

        setScene(new Scene(root, 800, 500));
    }

    private void cargarInicial() {
        // Muestra todos al abrir
        table.setItems(FXCollections.observableArrayList(cs.getCultivos()));
    }

    private void filtrar() {
        String term = tfBuscar.getText().trim().toLowerCase();
        EstadoCultivo sel = cbEstado.getValue();

        List<Cultivo> filtrados = cs.getCultivos().stream()
            .filter(c -> term.isEmpty()
                     || c.getNombre().toLowerCase().contains(term)
                     || c.getVariedad().toLowerCase().contains(term))
            .filter(c -> sel == null || c.getEstadoEnum() == sel)
            .collect(Collectors.toList());

        table.setItems(FXCollections.observableArrayList(filtrados));
    }
}
