package ui;

import javafx.beans.property.ReadOnlyStringWrapper;
import javafx.collections.FXCollections;
import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.stage.Stage;
import models.Cultivo;
import models.EstadoCultivo;
import models.Parcela;
import services.CultivoService;
import services.ParcelaService;
import utils.CSVHandler;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

public class CultivoWindow extends Stage {
    private final CultivoService cs;
    private final ParcelaService ps;
    private TableView<Cultivo> table;

    public CultivoWindow(CultivoService cs, ParcelaService ps) {
        this.cs = cs;
        this.ps = ps;
        setTitle("Gestión de Cultivos");
        initUI();
        listar();  // carga inicial
    }

    private void initUI() {
        BorderPane root = new BorderPane();

        HBox btnBar = new HBox(5);
        btnBar.setPadding(new Insets(10));
        Button btnListar   = new Button("Listar");   btnListar.setOnAction(e -> listar());
        Button btnCrear    = new Button("Crear");    btnCrear.setOnAction(e -> crear());
        Button btnEditar   = new Button("Editar");   btnEditar.setOnAction(e -> editar());
        Button btnEliminar = new Button("Eliminar"); btnEliminar.setOnAction(e -> eliminar());
        Button btnCerrar   = new Button("Cerrar");   btnCerrar.setOnAction(e -> close());
        btnBar.getChildren().addAll(btnListar, btnCrear, btnEditar, btnEliminar, btnCerrar);
        root.setTop(btnBar);

        table = new TableView<>();
        TableColumn<Cultivo, String> colNom = new TableColumn<>("Nombre");
        colNom.setCellValueFactory(new PropertyValueFactory<>("nombre"));
        TableColumn<Cultivo, String> colVar = new TableColumn<>("Variedad");
        colVar.setCellValueFactory(new PropertyValueFactory<>("variedad"));
        TableColumn<Cultivo, Double> colSup = new TableColumn<>("Superficie");
        colSup.setCellValueFactory(new PropertyValueFactory<>("superficie"));
        TableColumn<Cultivo, LocalDate> colFec = new TableColumn<>("Fecha Siembra");
        colFec.setCellValueFactory(new PropertyValueFactory<>("fechaSiembra"));
        TableColumn<Cultivo, String> colEst = new TableColumn<>("Estado");
        colEst.setCellValueFactory(c ->
            new ReadOnlyStringWrapper(c.getValue().getEstado())
        );
        table.getColumns().addAll(colNom, colVar, colSup, colFec, colEst);
        root.setCenter(table);

        setScene(new Scene(root, 600, 400));
    }

    private void listar() {
        List<Cultivo> lista = cs.getCultivos();
        table.setItems(FXCollections.observableArrayList(lista));
    }

    private void crear() {
        Dialog<Cultivo> dlg = new Dialog<>();
        dlg.setTitle("Crear Cultivo");
        ButtonType ok = new ButtonType("Crear", ButtonBar.ButtonData.OK_DONE);
        dlg.getDialogPane().getButtonTypes().addAll(ok, ButtonType.CANCEL);

        GridPane grid = new GridPane();
        grid.setHgap(10); grid.setVgap(10); grid.setPadding(new Insets(20));
        TextField tfNombre = new TextField();   grid.add(new Label("Nombre:"), 0, 0); grid.add(tfNombre, 1, 0);
        TextField tfVar    = new TextField();   grid.add(new Label("Variedad:"),0,1); grid.add(tfVar,1,1);
        TextField tfSup    = new TextField();   grid.add(new Label("Superficie:"),0,2); grid.add(tfSup,1,2);
        ComboBox<Parcela> cbPar = new ComboBox<>(FXCollections.observableArrayList(ps.getParcelas().values()));
        grid.add(new Label("Parcela:"),0,3); grid.add(cbPar,1,3);
        DatePicker dpFecha  = new DatePicker();  grid.add(new Label("Fecha Siembra:"),0,4); grid.add(dpFecha,1,4);
        ComboBox<EstadoCultivo> cbEst = new ComboBox<>(FXCollections.observableArrayList(EstadoCultivo.values()));
        grid.add(new Label("Estado:"),0,5); grid.add(cbEst,1,5);

        dlg.getDialogPane().setContent(grid);
        dlg.setResultConverter(b -> {
            if (b == ok) {
                return new Cultivo(
                  tfNombre.getText(),
                  tfVar.getText(),
                  Double.parseDouble(tfSup.getText()),
                  cbPar.getValue(),
                  dpFecha.getValue(),
                  cbEst.getValue()
                );
            }
            return null;
        });

        Optional<Cultivo> res = dlg.showAndWait();
        res.ifPresent(c -> {
            cs.agregarCultivo(
              c.getNombre(), c.getVariedad(), c.getSuperficie(),
              c.getParcela(), c.getFechaSiembra(), c.getEstadoEnum()
            );
            listar();
            salvar();
        });
    }

    private void editar() {
        Cultivo sel = table.getSelectionModel().getSelectedItem();
        if (sel == null) return;
        Dialog<Void> dlg = new Dialog<>();
        dlg.setTitle("Editar Cultivo");
        ButtonType ok = new ButtonType("Guardar", ButtonBar.ButtonData.OK_DONE);
        dlg.getDialogPane().getButtonTypes().addAll(ok, ButtonType.CANCEL);

        GridPane grid = new GridPane();
        grid.setHgap(10); grid.setVgap(10); grid.setPadding(new Insets(20));
        TextField tfNombre = new TextField(sel.getNombre());    grid.add(new Label("Nombre:"),0,0); grid.add(tfNombre,1,0);
        TextField tfVar    = new TextField(sel.getVariedad());  grid.add(new Label("Variedad:"),0,1); grid.add(tfVar,1,1);
        TextField tfSup    = new TextField(String.valueOf(sel.getSuperficie())); grid.add(new Label("Superficie:"),0,2); grid.add(tfSup,1,2);
        ComboBox<Parcela> cbPar = new ComboBox<>(FXCollections.observableArrayList(ps.getParcelas().values()));
        cbPar.setValue(sel.getParcela()); grid.add(new Label("Parcela:"),0,3); grid.add(cbPar,1,3);
        DatePicker dpFecha = new DatePicker(sel.getFechaSiembra()); grid.add(new Label("Fecha Siembra:"),0,4); grid.add(dpFecha,1,4);
        ComboBox<EstadoCultivo> cbEst = new ComboBox<>(FXCollections.observableArrayList(EstadoCultivo.values()));
        cbEst.setValue(sel.getEstadoEnum()); grid.add(new Label("Estado:"),0,5); grid.add(cbEst,1,5);

        dlg.getDialogPane().setContent(grid);
        dlg.setResultConverter(b -> null);
        Optional<Void> r = dlg.showAndWait();
        if (r.isPresent()) {
            cs.editarCultivo(
              sel,
              tfNombre.getText(), tfVar.getText(),
              Double.parseDouble(tfSup.getText()),
              dpFecha.getValue(), cbEst.getValue()
            );
            listar(); salvar();
        }
    }

    private void eliminar() {
        Cultivo sel = table.getSelectionModel().getSelectedItem();
        if (sel == null) return;
        Alert a = new Alert(Alert.AlertType.CONFIRMATION, "Eliminar cultivo?", ButtonType.YES, ButtonType.NO);
        a.showAndWait().ifPresent(b -> {
            if (b == ButtonType.YES) {
                cs.eliminarCultivo(sel);
                listar(); salvar();
            }
        });
    }

    private void salvar() {
        try {
            CSVHandler.guardarCultivos(cs.getCultivos(), "cultivos.csv");
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }
}
