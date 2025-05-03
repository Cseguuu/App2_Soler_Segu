package ui;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.collections.transformation.FilteredList;
import javafx.beans.property.ReadOnlyObjectWrapper;
import javafx.beans.property.ReadOnlyStringWrapper;
import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.stage.Stage;
import models.Cultivo;
import models.Actividad;
import services.CultivoService;
import utils.CSVHandler;

import java.io.IOException;
import java.time.LocalDate;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class ActividadWindow extends Stage {
    private static final String ACTIVIDADES_FILE = "actividades.csv";

    private final CultivoService cultivoService;
    private final List<Cultivo> cultivos;
    private final ObservableList<ActividadRow> masterData;
    private final FilteredList<ActividadRow> filteredData;
    private final TableView<ActividadRow> table;

    public ActividadWindow(CultivoService cultivoService) {
        this.cultivoService = cultivoService;
        this.cultivos = cultivoService.getCultivos();
        this.masterData = FXCollections.observableArrayList();
        this.filteredData = new FilteredList<>(masterData, p -> true);

        setTitle("Gestión de Actividades");
        BorderPane root = new BorderPane();

        // Filtros arriba
        HBox filterBox = createFilterControls();
        root.setTop(filterBox);

        // Tabla central
        table = new TableView<>(filteredData);
        setupTableColumns(table);
        setupRowEditing(table);
        root.setCenter(table);

        // Botones abajo
        HBox actionBox = createActionButtons();
        root.setBottom(actionBox);

        // Cargar datos
        loadData();

        Scene scene = new Scene(root, 900, 600);
        setScene(scene);
    }

    private HBox createFilterControls() {
        HBox box = new HBox(10);
        box.setPadding(new Insets(10));

        ComboBox<String> cultivoFilter = new ComboBox<>();
        cultivoFilter.getItems().add("Todos");
        cultivos.forEach(c -> cultivoFilter.getItems().add(c.getNombre()));
        cultivoFilter.setValue("Todos");

        ComboBox<String> tipoFilter = new ComboBox<>();
        tipoFilter.getItems().add("Todos");
        for (Actividad.Tipo t : Actividad.Tipo.values()) {
            tipoFilter.getItems().add(t.name());
        }
        tipoFilter.setValue("Todos");

        cultivoFilter.setOnAction(e -> applyFilters(cultivoFilter, tipoFilter));
        tipoFilter.setOnAction(e -> applyFilters(cultivoFilter, tipoFilter));

        box.getChildren().addAll(
            new Label("Cultivo:"), cultivoFilter,
            new Label("Tipo:"), tipoFilter
        );
        return box;
    }

    private void applyFilters(ComboBox<String> cultivoFilter, ComboBox<String> tipoFilter) {
        String selCult = cultivoFilter.getValue();
        String selTipo = tipoFilter.getValue();
        filteredData.setPredicate(row -> {
            boolean matchCult = selCult.equals("Todos") || row.getCultivo().getNombre().equals(selCult);
            String rowTipo = row.tipoProperty().get();
            boolean matchTipo = selTipo.equals("Todos") || rowTipo.equals(selTipo);
            return matchCult && matchTipo;
        });
    }

    private void setupTableColumns(TableView<ActividadRow> table) {
        TableColumn<ActividadRow, String> colCult = new TableColumn<>("Cultivo");
        colCult.setCellValueFactory(c -> new ReadOnlyStringWrapper(c.getValue().getCultivo().getNombre()));
        colCult.setPrefWidth(200);

        TableColumn<ActividadRow, LocalDate> colFecha = new TableColumn<>("Fecha");
        colFecha.setCellValueFactory(c -> new ReadOnlyObjectWrapper<>(c.getValue().getFecha()));
        colFecha.setPrefWidth(120);

        TableColumn<ActividadRow, String> colTipo = new TableColumn<>("Tipo");
        colTipo.setCellValueFactory(c -> c.getValue().tipoProperty());
        colTipo.setPrefWidth(120);

        TableColumn<ActividadRow, String> colEst = new TableColumn<>("Estado");
        colEst.setCellValueFactory(c -> c.getValue().estadoProperty());
        colEst.setPrefWidth(120);

        table.getColumns().setAll(colCult, colFecha, colTipo, colEst);
    }

    private void setupRowEditing(TableView<ActividadRow> table) {
        table.setRowFactory(tv -> {
            TableRow<ActividadRow> row = new TableRow<>();
            row.setOnMouseClicked(ev -> {
                if (ev.getClickCount() == 2 && !row.isEmpty()) {
                    showEditDialog(row.getItem());
                }
            });
            return row;
        });
    }

    private HBox createActionButtons() {
        HBox box = new HBox(10);
        box.setPadding(new Insets(10));
        Button addBtn = new Button("Agregar");
        addBtn.setOnAction(e -> showAddDialog());
        Button completeBtn = new Button("Completar");
        completeBtn.setOnAction(e -> {
            ActividadRow sel = table.getSelectionModel().getSelectedItem();
            if (sel == null) {
                new Alert(Alert.AlertType.WARNING, "Seleccione una actividad").showAndWait();
                return;
            }
            sel.getActividad().setCompletada(true);
            table.refresh();
            saveActivities();
        });
        Button saveBtn = new Button("Guardar");
        saveBtn.setOnAction(e -> saveActivities());
        Button exitBtn = new Button("Salir");
        exitBtn.setOnAction(e -> close());
        box.getChildren().addAll(addBtn, completeBtn, saveBtn, exitBtn);
        return box;
    }

    private void showAddDialog() {
        // Mostrar solo cultivos sin actividades previas
        List<Cultivo> disponibles = cultivos.stream()
            .filter(c -> c.getActividades().isEmpty())
            .collect(Collectors.toList());

        Dialog<ActividadRow> dlg = new Dialog<>();
        dlg.setTitle("Agregar Actividad");
        dlg.getDialogPane().getButtonTypes().addAll(ButtonType.OK, ButtonType.CANCEL);

        ComboBox<Cultivo> cbCult = new ComboBox<>(FXCollections.observableArrayList(disponibles));
        cbCult.setPromptText("Cultivo");
        ComboBox<Actividad.Tipo> cbTipo = new ComboBox<>(FXCollections.observableArrayList(Actividad.Tipo.values()));
        cbTipo.setPromptText("Tipo");
        DatePicker dp = new DatePicker(LocalDate.now());

        GridPane grid = new GridPane();
        grid.setHgap(10); grid.setVgap(10);
        grid.add(new Label("Cultivo:"), 0, 0);
        grid.add(cbCult, 1, 0);
        grid.add(new Label("Tipo:"), 0, 1);
        grid.add(cbTipo, 1, 1);
        grid.add(new Label("Fecha:"), 0, 2);
        grid.add(dp, 1, 2);
        dlg.getDialogPane().setContent(grid);

        dlg.setResultConverter(bt -> {
            if (bt == ButtonType.OK && cbCult.getValue() != null && cbTipo.getValue() != null) {
                Actividad a = new Actividad(cbTipo.getValue(), dp.getValue());
                cbCult.getValue().addActividad(a);
                return new ActividadRow(cbCult.getValue(), a);
            }
            return null;
        });

        dlg.showAndWait().ifPresent(r -> masterData.add(r));
    }

    private void showEditDialog(ActividadRow rowData) {
        Dialog<Boolean> dlg = new Dialog<>();
        dlg.setTitle("Cambiar Estado");
        dlg.getDialogPane().getButtonTypes().addAll(ButtonType.OK, ButtonType.CANCEL);

        ComboBox<String> cbEst = new ComboBox<>(FXCollections.observableArrayList("PENDIENTE", "COMPLETADA"));
        cbEst.setValue(rowData.estadoProperty().get());
        dlg.getDialogPane().setContent(cbEst);

        dlg.setResultConverter(bt -> bt == ButtonType.OK ? cbEst.getValue().equals("COMPLETADA") : null);
        dlg.showAndWait().ifPresent(completed -> {
            rowData.getActividad().setCompletada(completed);
            table.refresh();
        });
    }

    private void saveActivities() {
        try {
            CSVHandler.guardarActividades(ACTIVIDADES_FILE, cultivos);
            new Alert(Alert.AlertType.INFORMATION, "Actividades guardadas").showAndWait();
        } catch (IOException e) {
            new Alert(Alert.AlertType.ERROR, "Error al guardar: " + e.getMessage()).showAndWait();
        }
    }

    private void loadData() {
        masterData.clear();
        try {
            Map<String, List<Actividad>> map = CSVHandler.leerActividades(ACTIVIDADES_FILE, cultivos);
            for (Cultivo c : cultivos) {
                List<Actividad> acts = map.getOrDefault(c.getNombre(), List.of());
                c.getActividades().clear();
                c.getActividades().addAll(acts);
                for (Actividad a : acts) {
                    masterData.add(new ActividadRow(c, a));
                }
            }
        } catch (IOException e) {
            new Alert(Alert.AlertType.ERROR, "Error al cargar: " + e.getMessage()).showAndWait();
        }
    }
}
