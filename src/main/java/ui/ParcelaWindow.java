package ui;

import javafx.beans.property.ReadOnlyDoubleWrapper;
import javafx.beans.property.ReadOnlyStringWrapper;
import javafx.collections.FXCollections;
import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.stage.Stage;
import models.Parcela;
import services.ParcelaService;
import utils.CSVHandler;

import java.util.*;

public class ParcelaWindow extends Stage {
    private final ParcelaService ps;
    private final String parcCsv;
    private final TableView<Parcela> table = new TableView<>();

    public ParcelaWindow(ParcelaService ps, String parcCsv) {
        this.ps = ps;
        this.parcCsv = parcCsv;
        setTitle("Gestión de Parcelas");
        initUI();
        listar();
    }

    private void initUI() {
        BorderPane root = new BorderPane();
        HBox bar = new HBox(8);
        bar.setPadding(new Insets(10));

        Button list = new Button("Listar");
        Button add  = new Button("Agregar");
        Button edit = new Button("Editar");
        Button del  = new Button("Eliminar");
        Button close= new Button("Cerrar");

        list .setOnAction(e -> listar());
        add  .setOnAction(e -> agregar());
        edit .setOnAction(e -> editar());
        del  .setOnAction(e -> eliminar());
        close.setOnAction(e -> close());

        bar.getChildren().addAll(list, add, edit, del, close);
        root.setTop(bar);

        TableColumn<Parcela, String> c1 = new TableColumn<>("Código");
        c1.setCellValueFactory(c -> 
            new ReadOnlyStringWrapper(c.getValue().getCodigo()));
        TableColumn<Parcela, Number> c2 = new TableColumn<>("Área");
        c2.setCellValueFactory(c -> 
            new ReadOnlyDoubleWrapper(c.getValue().getArea()));
        TableColumn<Parcela, String> c3 = new TableColumn<>("Ubicación");
        c3.setCellValueFactory(c -> 
            new ReadOnlyStringWrapper(c.getValue().getUbicacion()));

        table.getColumns().addAll(c1, c2, c3);
        root.setCenter(table);

        setScene(new Scene(root, 600, 400));
    }

    private void listar() {
        table.setItems(FXCollections.observableArrayList(
            new ArrayList<>(ps.getParcelas().values())
        ));
    }

    private void agregar() {
        TextInputDialog d1 = new TextInputDialog();
        d1.setHeaderText(null); d1.setContentText("Código:");
        Optional<String> co = d1.showAndWait(); if (co.isEmpty()) return;

        TextInputDialog d2 = new TextInputDialog();
        d2.setHeaderText(null); d2.setContentText("Área:");
        Optional<String> ao = d2.showAndWait(); if (ao.isEmpty()) return;

        double area;
        try {
            area = Double.parseDouble(ao.get());
        } catch (NumberFormatException ex) {
            new Alert(Alert.AlertType.ERROR, "Área inválida").showAndWait();
            return;
        }

        TextInputDialog d3 = new TextInputDialog();
        d3.setHeaderText(null); d3.setContentText("Ubicación:");
        Optional<String> uo = d3.showAndWait(); if (uo.isEmpty()) return;

        if (!ps.agregarParcela(co.get(), area, uo.get())) {
            new Alert(Alert.AlertType.ERROR, "Ya existe esa parcela").showAndWait();
        }
        guardarYListar();
    }

    private void editar() {
        Parcela sel = table.getSelectionModel().getSelectedItem();
        if (sel == null) {
            new Alert(Alert.AlertType.WARNING, "Seleccione una parcela").showAndWait();
            return;
        }

        TextInputDialog d2 = new TextInputDialog(String.valueOf(sel.getArea()));
        d2.setHeaderText(null); d2.setContentText("Nueva Área:");
        Optional<String> ao = d2.showAndWait(); if (ao.isEmpty()) return;

        double area;
        try {
            area = Double.parseDouble(ao.get());
        } catch (NumberFormatException ex) {
            new Alert(Alert.AlertType.ERROR, "Área inválida").showAndWait();
            return;
        }

        TextInputDialog d3 = new TextInputDialog(sel.getUbicacion());
        d3.setHeaderText(null); d3.setContentText("Nueva Ubicación:");
        Optional<String> uo = d3.showAndWait(); if (uo.isEmpty()) return;

        if (!ps.editarParcela(sel.getCodigo(), area, uo.get())) {
            new Alert(Alert.AlertType.ERROR, "Error al editar").showAndWait();
        }
        guardarYListar();
    }

    private void eliminar() {
        Parcela sel = table.getSelectionModel().getSelectedItem();
        if (sel == null) {
            new Alert(Alert.AlertType.WARNING, "Seleccione una parcela").showAndWait();
            return;
        }
        if (ps.eliminarParcela(sel.getCodigo())) {
            guardarYListar();
        } else {
            new Alert(Alert.AlertType.ERROR,
                "No se puede eliminar: tiene cultivos asignados").showAndWait();
        }
    }

    // Ya no usa try/catch de IOException porque guardarParcelas no lanza excepción
    private void guardarYListar() {
        CSVHandler.guardarParcelas(ps.getParcelas().values(), parcCsv);
        listar();
    }
}
