package ui;

import javafx.beans.property.ReadOnlyStringWrapper;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.stage.Stage;
import models.*;
import services.CultivoService;
import services.ParcelaService;
import utils.CSVHandler;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

/**
 * Ventana JavaFX para la gestión de cultivos
 * (ahora muestra la parcela a la que pertenece cada cultivo).
 */
public class CultivoWindow extends Stage {

    private final CultivoService cs;
    private final ParcelaService ps;
    private final TableView<Cultivo> table = new TableView<>();

    public CultivoWindow(CultivoService cs, ParcelaService ps) {
        this.cs = cs; this.ps = ps;
        setTitle("Gestión de Cultivos");
        initUI();  listar();
    }

    /* ═════════════════════════ UI ═════════════════════════ */

    private void initUI() {
        BorderPane root = new BorderPane();

        /* barra de botones */
        HBox bar = new HBox(6);
        bar.setPadding(new Insets(10));
        Button bList = new Button("Listar");
        Button bNew  = new Button("Nuevo");
        Button bEdit = new Button("Editar");
        Button bDel  = new Button("Eliminar");
        Button bExit = new Button("Cerrar");
        bar.getChildren().addAll(bList, bNew, bEdit, bDel, bExit);
        root.setTop(bar);

        /* tabla */
        TableColumn<Cultivo, String> cNom = new TableColumn<>("Nombre");
        cNom.setCellValueFactory(c -> new ReadOnlyStringWrapper(c.getValue().getNombre()));

        TableColumn<Cultivo, String> cVar = new TableColumn<>("Variedad");
        cVar.setCellValueFactory(c -> new ReadOnlyStringWrapper(c.getValue().getVariedad()));

        TableColumn<Cultivo, Double> cSup = new TableColumn<>("Superficie");
        cSup.setCellValueFactory(new PropertyValueFactory<>("superficie"));

        TableColumn<Cultivo, String> cPar = new TableColumn<>("Parcela");
        cPar.setCellValueFactory(c -> new ReadOnlyStringWrapper(
                c.getValue().getParcela() != null ? c.getValue().getParcela().getCodigo() : "—"));

        TableColumn<Cultivo, LocalDate> cFec = new TableColumn<>("Fecha Siembra");
        cFec.setCellValueFactory(new PropertyValueFactory<>("fechaSiembra"));

        TableColumn<Cultivo, String> cEst = new TableColumn<>("Estado");
        cEst.setCellValueFactory(c -> new ReadOnlyStringWrapper(c.getValue().getEstado()));

        table.getColumns().addAll(cNom, cVar, cSup, cPar, cFec, cEst);
        root.setCenter(table);

        /* wiring */
        bList.setOnAction(e -> listar());
        bNew .setOnAction(e -> crear());
        bEdit.setOnAction(e -> editar());
        bDel .setOnAction(e -> eliminar());
        bExit.setOnAction(e -> close());

        setScene(new Scene(root, 760, 420));
    }

    /* ═════════════════════ operaciones ═════════════════════ */

    private void listar() {
        ObservableList<Cultivo> data = FXCollections.observableArrayList(cs.getCultivos());
        table.setItems(data);
    }

    private void crear() {
        Dialog<Cultivo> dlg = dialogoCultivo(null);
        dlg.showAndWait().ifPresent(c -> {
            cs.agregarCultivo(c);   // usa nuevo método
            listar(); salvar();
        });
    }

    private void editar() {
        Cultivo sel = table.getSelectionModel().getSelectedItem();
        if (sel == null) return;

        Dialog<Cultivo> dlg = dialogoCultivo(sel);
        dlg.showAndWait().ifPresent(c -> {
            cs.reemplazarCultivo(sel, c);   // usa nuevo método
            listar(); salvar();
        });
    }

    private void eliminar() {
        Cultivo sel = table.getSelectionModel().getSelectedItem();
        if (sel == null) return;

        Alert a = new Alert(Alert.AlertType.CONFIRMATION,
                "¿Eliminar cultivo \"" + sel.getNombre() + "\"?",
                ButtonType.YES, ButtonType.NO);
        a.showAndWait().filter(b -> b == ButtonType.YES)
                       .ifPresent(b -> { cs.eliminarCultivo(sel); listar(); salvar(); });
    }

    /* ═════════════ diálogo de alta / edición ═════════════ */

    private Dialog<Cultivo> dialogoCultivo(Cultivo base) {
        boolean edit = base != null;

        Dialog<Cultivo> dlg = new Dialog<>();
        dlg.setTitle(edit ? "Editar Cultivo" : "Nuevo Cultivo");
        ButtonType OK = new ButtonType(edit ? "Guardar" : "Crear", ButtonBar.ButtonData.OK_DONE);
        dlg.getDialogPane().getButtonTypes().addAll(OK, ButtonType.CANCEL);

        GridPane g = new GridPane(); g.setHgap(10); g.setVgap(8); g.setPadding(new Insets(10));

        TextField tfNom = new TextField(edit ? base.getNombre() : "");
        TextField tfVar = new TextField(edit ? base.getVariedad() : "");
        TextField tfSup = new TextField(edit ? String.valueOf(base.getSuperficie()) : "");
        ComboBox<Parcela> cbPar = new ComboBox<>(FXCollections.observableArrayList(ps.getParcelas().values()));
        cbPar.setValue(edit ? base.getParcela() : null);
        DatePicker dpFec = new DatePicker(edit ? base.getFechaSiembra() : LocalDate.now());
        ComboBox<EstadoCultivo> cbEst = new ComboBox<>(FXCollections.observableArrayList(EstadoCultivo.values()));
        cbEst.setValue(edit ? base.getEstadoEnum() : cbEst.getItems().get(0));


        g.addRow(0, new Label("Nombre:"),    tfNom);
        g.addRow(1, new Label("Variedad:"),  tfVar);
        g.addRow(2, new Label("Superficie:"),tfSup);
        g.addRow(3, new Label("Parcela:"),   cbPar);
        g.addRow(4, new Label("Fecha:"),     dpFec);
        g.addRow(5, new Label("Estado:"),    cbEst);

        dlg.getDialogPane().setContent(g);

        dlg.setResultConverter(bt -> {
            if (bt != OK) return null;
            return new Cultivo(
                    tfNom.getText().trim(),
                    tfVar.getText().trim(),
                    Double.parseDouble(tfSup.getText()),
                    cbPar.getValue(),
                    dpFec.getValue(),
                    cbEst.getValue()
            );
        });
        return dlg;
    }

    /* ═════════════════════ persistencia ═══════════════════ */

    private void salvar() {
        try {
            CSVHandler.guardarCultivos(cs.getCultivos(), "cultivos.csv");
        } catch (Exception ex) { ex.printStackTrace(); }
    }
}
