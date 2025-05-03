package ui;

import javafx.beans.property.ReadOnlyDoubleWrapper;
import javafx.beans.property.ReadOnlyStringWrapper;
import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.stage.Stage;
import models.Parcela;
import services.ParcelaService;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class ParcelaWindow extends Stage {
    private final ParcelaService ps;
    private final TableView<Parcela> table = new TableView<>();

    public ParcelaWindow(ParcelaService ps) {
        this.ps = ps;
        setTitle("Gestión de Parcelas");
        initUI();
        listar();
    }

    private void initUI() {
        BorderPane root = new BorderPane();
        HBox btnBar = new HBox(10);
        btnBar.setPadding(new Insets(10));

        Button listarBtn   = new Button("Listar");
        Button agregarBtn  = new Button("Agregar");
        Button editarBtn   = new Button("Editar");
        Button eliminarBtn = new Button("Eliminar");
        Button cerrarBtn   = new Button("Cerrar");

        listarBtn  .setOnAction(e -> listar());
        agregarBtn .setOnAction(e -> agregar());
        editarBtn  .setOnAction(e -> editar());
        eliminarBtn.setOnAction(e -> eliminar());
        cerrarBtn  .setOnAction(e -> close());

        btnBar.getChildren().addAll(listarBtn, agregarBtn, editarBtn, eliminarBtn, cerrarBtn);
        root.setTop(btnBar);

        // Columnas: Código, Área y Ubicación
        TableColumn<Parcela, String> colCodigo = new TableColumn<>("Código");
        colCodigo.setCellValueFactory(c -> new ReadOnlyStringWrapper(c.getValue().getCodigo()));

        TableColumn<Parcela, Number> colArea = new TableColumn<>("Área");
        colArea.setCellValueFactory(c -> new ReadOnlyDoubleWrapper(c.getValue().getArea()));

        TableColumn<Parcela, String> colUbic = new TableColumn<>("Ubicación");
        colUbic.setCellValueFactory(c -> new ReadOnlyStringWrapper(c.getValue().getUbicacion()));

        table.getColumns().addAll(colCodigo, colArea, colUbic);
        root.setCenter(table);

        setScene(new Scene(root, 600, 400));
    }

    private void listar() {
        List<Parcela> lista = new ArrayList<>(ps.getParcelas().values());
        table.getItems().setAll(lista);
    }

    private void agregar() {
        // Entrada de código
        TextInputDialog dlgCod = new TextInputDialog();
        dlgCod.setTitle("Nueva Parcela");
        dlgCod.setHeaderText(null);
        dlgCod.setContentText("Código:");
        Optional<String> codOpt = dlgCod.showAndWait();
        if (codOpt.isEmpty()) return;

        // Entrada de área
        TextInputDialog dlgArea = new TextInputDialog();
        dlgArea.setTitle("Nueva Parcela");
        dlgArea.setHeaderText(null);
        dlgArea.setContentText("Área:");
        Optional<String> areaOpt = dlgArea.showAndWait();
        if (areaOpt.isEmpty()) return;

        double area;
        try {
            area = Double.parseDouble(areaOpt.get());
        } catch (NumberFormatException ex) {
            new Alert(Alert.AlertType.ERROR, "Área inválida").showAndWait();
            return;
        }

        // Entrada de ubicación
        TextInputDialog dlgUbic = new TextInputDialog();
        dlgUbic.setTitle("Nueva Parcela");
        dlgUbic.setHeaderText(null);
        dlgUbic.setContentText("Ubicación:");
        Optional<String> ubicOpt = dlgUbic.showAndWait();
        if (ubicOpt.isEmpty()) return;

        boolean creado = ps.agregarParcela(codOpt.get(), area, ubicOpt.get());
        if (!creado) {
            new Alert(Alert.AlertType.ERROR, "Ya existe una parcela con ese código").showAndWait();
            return;
        }
        listar();
    }

    private void editar() {
        Parcela sel = table.getSelectionModel().getSelectedItem();
        if (sel == null) {
            new Alert(Alert.AlertType.WARNING, "Seleccione una parcela").showAndWait();
            return;
        }

        // Editar área
        TextInputDialog dlgArea = new TextInputDialog(String.valueOf(sel.getArea()));
        dlgArea.setTitle("Editar Parcela");
        dlgArea.setHeaderText(null);
        dlgArea.setContentText("Área:");
        Optional<String> areaOpt = dlgArea.showAndWait();
        if (areaOpt.isEmpty()) return;

        double area;
        try {
            area = Double.parseDouble(areaOpt.get());
        } catch (NumberFormatException ex) {
            new Alert(Alert.AlertType.ERROR, "Área inválida").showAndWait();
            return;
        }

        // Editar ubicación
        TextInputDialog dlgUbic = new TextInputDialog(sel.getUbicacion());
        dlgUbic.setTitle("Editar Parcela");
        dlgUbic.setHeaderText(null);
        dlgUbic.setContentText("Ubicación:");
        Optional<String> ubicOpt = dlgUbic.showAndWait();
        if (ubicOpt.isEmpty()) return;

        boolean editado = ps.editarParcela(sel.getCodigo(), area, ubicOpt.get());
        if (!editado) {
            new Alert(Alert.AlertType.ERROR, "Error al editar la parcela").showAndWait();
            return;
        }
        listar();
    }

    private void eliminar() {
        Parcela sel = table.getSelectionModel().getSelectedItem();
        if (sel == null) {
            new Alert(Alert.AlertType.WARNING, "Seleccione una parcela").showAndWait();
            return;
        }
        Alert confirm = new Alert(Alert.AlertType.CONFIRMATION,
            "¿Eliminar parcela " + sel.getCodigo() + "?",
            ButtonType.YES, ButtonType.NO);
        Optional<ButtonType> res = confirm.showAndWait();
        if (res.isPresent() && res.get() == ButtonType.YES) {
            boolean ok = ps.eliminarParcela(sel.getCodigo());
            if (!ok) {
                new Alert(Alert.AlertType.ERROR,
                    "No se puede eliminar: la parcela tiene cultivos asignados").showAndWait();
            }
            listar();
        }
    }
}
