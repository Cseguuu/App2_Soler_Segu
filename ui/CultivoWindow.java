package ui;

import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.stage.Stage;
import models.Cultivo;
import services.CultivoService;
import services.ParcelaService;

import java.util.List;

public class CultivoWindow extends Stage {
    private final CultivoService cs;
    private final ParcelaService ps;
    private final TableView<Cultivo> table = new TableView<>();

    public CultivoWindow(CultivoService cs, ParcelaService ps) {
        this.cs = cs;
        this.ps = ps;
        setTitle("Gestión de Cultivos");
        initUI();
    }

    private void initUI() {
        BorderPane root = new BorderPane();
        HBox btnBar = new HBox(10);
        btnBar.setPadding(new Insets(10));

        Button listar = new Button("Listar");
        listar.setOnAction(e -> listar());
        Button crear = new Button("Crear");
        crear.setOnAction(e -> {/* diálogo para crear */});
        Button editar = new Button("Editar");
        editar.setOnAction(e -> {/* diálogo para editar */});
        Button eliminar = new Button("Eliminar");
        eliminar.setOnAction(e -> {/* diálogo para eliminar */});
        Button cerrar = new Button("Cerrar");
        cerrar.setOnAction(e -> close());

        btnBar.getChildren().addAll(listar, crear, editar, eliminar, cerrar);
        root.setTop(btnBar);

        TableColumn<Cultivo, String> colNom = new TableColumn<>("Nombre");
        colNom.setCellValueFactory(new PropertyValueFactory<>("nombre"));
        TableColumn<Cultivo, String> colVar = new TableColumn<>("Variedad");
        colVar.setCellValueFactory(new PropertyValueFactory<>("variedad"));
        TableColumn<Cultivo, Double> colSup = new TableColumn<>("Superficie");
        colSup.setCellValueFactory(new PropertyValueFactory<>("superficie"));
        TableColumn<Cultivo, ?> colFec = new TableColumn<>("Fecha Siembra");
        colFec.setCellValueFactory(new PropertyValueFactory<>("fechaSiembra"));
        TableColumn<Cultivo, ?> colEst = new TableColumn<>("Estado");
        colEst.setCellValueFactory(new PropertyValueFactory<>("estadoEnum"));
        table.getColumns().addAll(colNom, colVar, colSup, colFec, colEst);
        root.setCenter(table);

        setScene(new Scene(root, 600, 400));
    }

    private void listar() {
        List<Cultivo> lista = cs.getCultivos();
        table.getItems().setAll(lista);
    }
}