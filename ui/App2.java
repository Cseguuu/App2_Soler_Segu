package ui;

import javafx.application.Application;
import javafx.application.Platform;
import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import models.Cultivo;
import models.Parcela;
import services.CultivoService;
import services.ParcelaService;
import utils.CSVHandler;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class App2 extends Application {
    private static String csvFile;
    private List<Cultivo> cultivos;
    private Map<String, Parcela> parcelas;
    private CultivoService cs;
    private ParcelaService ps;

    public static void main(String[] args) {
        if (args.length != 1) {
            System.out.println("Uso: java MainApp <archivo_csv>");
            System.exit(1);
        }
        csvFile = args[0];
        launch(args);
    }

    @Override
    public void init() throws Exception {
        // Carga datos antes de mostrar UI
        cultivos = CSVHandler.leerCultivos(csvFile);
        parcelas = new HashMap<>();
        for (Cultivo c : cultivos) {
            parcelas.put(c.getParcela().getCodigo(), c.getParcela());
        }
        cs = new CultivoService(cultivos);
        ps = new ParcelaService(parcelas.values());
    }

    @Override
    public void start(Stage stage) {
        stage.setTitle("Menú Principal");

        Button btnCultivos = new Button("Gestión de Cultivos");
        btnCultivos.setMaxWidth(Double.MAX_VALUE);
        btnCultivos.setOnAction(e -> new CultivoWindow(cs, ps).show());

        Button btnParcelas = new Button("Gestión de Parcelas");
        btnParcelas.setMaxWidth(Double.MAX_VALUE);
        // Ahora solo pasa el servicio de parcelas
        btnParcelas.setOnAction(e -> new ParcelaWindow(ps).show());

        Button btnActividades = new Button("Gestión de Actividades");
        btnActividades.setMaxWidth(Double.MAX_VALUE);
        btnActividades.setOnAction(e -> new ActividadWindow(cs).show());

        Button btnReportes = new Button("Búsqueda / Reporte");
        btnReportes.setMaxWidth(Double.MAX_VALUE);
        btnReportes.setOnAction(e -> new ReporteWindow(cs).show());

        Button btnSalir = new Button("Salir");
        btnSalir.setMaxWidth(Double.MAX_VALUE);
        btnSalir.setOnAction(e -> {
            try {
                CSVHandler.guardarCultivos(cultivos, csvFile);
            } catch (IOException ex) {
                ex.printStackTrace();
            }
            Platform.exit();
        });

        VBox root = new VBox(10, btnCultivos, btnParcelas, btnActividades, btnReportes, btnSalir);
        root.setPadding(new Insets(15));

        stage.setScene(new Scene(root, 300, 250));
        stage.show();
    }

    @Override
    public void stop() throws Exception {
        // Asegurar guardado al cerrar
        CSVHandler.guardarCultivos(cultivos, csvFile);
    }
}