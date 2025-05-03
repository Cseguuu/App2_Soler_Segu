package ui;

import javafx.application.Application;
import javafx.application.Platform;
import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import javafx.stage.WindowEvent;
import models.Cultivo;
import models.Parcela;
import services.CultivoService;
import services.ParcelaService;
import utils.CSVHandler;

import java.io.IOException;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

public class App2 extends Application {
    private static String cultCsv;
    private static final String parcCsv = "parcelas.csv";

    private List<Cultivo> cultivos;
    private Map<String, Parcela> parcelas;
    private CultivoService cs;
    private ParcelaService ps;

    public static void main(String[] args) {
        if (args.length != 1) {
            System.err.println("Uso: java -jar App2.jar <cultivos.csv>");
            System.exit(1);
        }
        cultCsv = args[0];
        launch(args);
    }

    @Override
    public void init() throws Exception {
        // 1) Leer cultivos
        cultivos = CSVHandler.leerCultivos(cultCsv);

        // 2) Leer o inicializar parcelas.csv
        parcelas = new LinkedHashMap<>();
        for (Parcela p : CSVHandler.leerParcelas(parcCsv)) {
            parcelas.put(p.getCodigo(), p);
        }

        // 3) Garantizar todas las parcelas de cultivos
        for (Cultivo c : cultivos) {
            parcelas.putIfAbsent(c.getParcela().getCodigo(), c.getParcela());
        }

        // 4) Guardar estado actualizado
        CSVHandler.guardarParcelas(parcelas.values(), parcCsv);

        // 5) Crear servicios
        cs = new CultivoService(cultivos);
        ps = new ParcelaService(parcelas.values());
    }

    @Override
    public void start(Stage stage) {
        stage.setTitle("App2 – Menú Principal");

        // Consumir el evento de cerrar con la X para que no haga nada
        stage.setOnCloseRequest(WindowEvent::consume);

        Button btnCult = new Button("Gestión de Cultivos");
        btnCult.setMaxWidth(Double.MAX_VALUE);
        btnCult.setOnAction(e -> new CultivoWindow(cs, ps).show());

        Button btnPar = new Button("Gestión de Parcelas");
        btnPar.setMaxWidth(Double.MAX_VALUE);
        btnPar.setOnAction(e -> new ParcelaWindow(ps, parcCsv).show());

        Button btnAct = new Button("Gestión de Actividades");
        btnAct.setMaxWidth(Double.MAX_VALUE);
        btnAct.setOnAction(e -> new ActividadWindow(cs).show());

        Button btnRep = new Button("Búsqueda / Reporte");
        btnRep.setMaxWidth(Double.MAX_VALUE);
        btnRep.setOnAction(e -> new ReporteWindow(cs).show());

        Button btnSalir = new Button("Salir");
        btnSalir.setMaxWidth(Double.MAX_VALUE);
        btnSalir.setOnAction(e -> {
            // Guardar ambos CSV y salir
            try {
                CSVHandler.guardarCultivos(cultivos, cultCsv);
                CSVHandler.guardarParcelas(ps.getParcelas().values(), parcCsv);
            } catch (IOException ex) {
                ex.printStackTrace();
            }
            Platform.exit();
            System.exit(0);
        });

        VBox root = new VBox(10, btnCult, btnPar, btnAct, btnRep, btnSalir);
        root.setPadding(new Insets(15));
        stage.setScene(new Scene(root, 300, 280));
        stage.show();
    }
}
