package ui;

import models.*;
import services.*;
import utils.CSVHandler;

import java.io.IOException;
import java.util.*;

public class App2 {
    public static void main(String[] args) {
        if (args.length != 1) {
            System.out.println("Uso: java App2 <archivo_csv>");
            System.exit(1);
        }
        String csv = args[0];
        List<Cultivo> cultivos;
        try {
            cultivos = CSVHandler.leerCultivos(csv);
        } catch (IOException e) {
            System.err.println("Error leyendo CSV: " + e.getMessage());
            return;
        }
        Map<String, Parcela> parcelas = new HashMap<>();
        for (Cultivo c : cultivos) parcelas.put(c.getParcela().getCodigo(), c.getParcela());

        CultivoService cs = new CultivoService(cultivos);
        ParcelaService ps = new ParcelaService(parcelas.values());

        Menu.mostrarMenuPrincipal(cs, ps);

        try {
            CSVHandler.guardarCultivos(cultivos, csv);
            System.out.println("Cambios guardados en " + csv);
        } catch (IOException e) {
            System.err.println("Error guardando CSV: " + e.getMessage());
        }
    }
}
