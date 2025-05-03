package utils;

import models.Cultivo;
import models.Parcela;
import models.Actividad;
import models.EstadoCultivo;

import java.io.*;
import java.time.LocalDate;
import java.util.*;

public class CSVHandler {

    /**
     * Lee el CSV de cultivos y construye los objetos Cultivo, Parcela y Actividad.
     */
    public static List<Cultivo> leerCultivos(String filePath) throws IOException {
        List<Cultivo> cultivos = new ArrayList<>();
        Map<String, Parcela> parcelasMap = new HashMap<>();

        try (BufferedReader br = new BufferedReader(new FileReader(filePath))) {
            String line;
            while ((line = br.readLine()) != null) {
                String[] parts = splitCSV(line);
                String nombre      = trimQuotes(parts[1]);
                String variedad    = trimQuotes(parts[2]);
                double superficie  = Double.parseDouble(parts[3]);
                String codParcela  = trimQuotes(parts[4]);
                LocalDate fecha    = LocalDate.parse(trimQuotes(parts[5]));
                EstadoCultivo est  = EstadoCultivo.valueOf(trimQuotes(parts[6]));
                String rawActs     = parts[7];
                // rawActs = ["TIPO:fecha",...]
                rawActs = rawActs.substring(1, rawActs.length() - 1);

                List<Actividad> actividades = new ArrayList<>();
                if (!rawActs.isEmpty()) {
                    // Separa por "," pero sin romper comillas internas
                    String[] arr = rawActs.split("\",\"");
                    for (String s : arr) {
                        String clean = trimQuotes(s);
                        String[] pa  = clean.split(":", 2);
                        Actividad.Tipo tipo = Actividad.Tipo.valueOf(pa[0]);
                        LocalDate   f    = LocalDate.parse(pa[1]);
                        actividades.add(new Actividad(tipo, f));
                    }
                }

                // Obtener o crear la parcela
                Parcela parcela = parcelasMap.computeIfAbsent(
                    codParcela,
                    k -> new Parcela(k, 0.0, "")
                );

                // Construir el cultivo y asociar actividades
                Cultivo c = new Cultivo(nombre, variedad, superficie, parcela, fecha, est);
                actividades.forEach(c::addActividad);
                parcela.addCultivo(c);

                cultivos.add(c);
            }
        }

        return cultivos;
    }

    /**
     * Guarda la lista de cultivos en el CSV, respetando el formato original.
     */
    public static void guardarCultivos(List<Cultivo> cultivos, String filePath) throws IOException {
        try (BufferedWriter bw = new BufferedWriter(new FileWriter(filePath))) {
            for (Cultivo c : cultivos) {
                StringBuilder sb = new StringBuilder();
                sb.append("Cultivo,")
                  .append("\"").append(c.getNombre()).append("\",")
                  .append("\"").append(c.getVariedad()).append("\",")
                  .append(c.getSuperficie()).append(",")
                  .append("\"").append(c.getParcela().getCodigo()).append("\",")
                  .append("\"").append(c.getFechaSiembra()).append("\",")
                  .append("\"").append(c.getEstadoEnum()).append("\",")
                  .append("[");

                List<Actividad> acts = c.getActividades();
                for (int i = 0; i < acts.size(); i++) {
                    Actividad a = acts.get(i);
                    sb.append("\"")
                      .append(a.getTipo()).append(":").append(a.getFecha())
                      .append("\"");
                    if (i < acts.size() - 1) sb.append(",");
                }
                sb.append("]");

                bw.write(sb.toString());
                bw.newLine();
            }
        }
    }

    /**
     * Quita comillas dobles al inicio o al final de la cadena, si existen.
     */
    private static String trimQuotes(String s) {
        if (s == null || s.isEmpty()) return s;
        int start = 0, end = s.length();
        if (s.charAt(0) == '\"')         start++;
        if (s.charAt(s.length() - 1) == '\"') end--;
        return s.substring(start, end);
    }

    /**
     * Parte la línea CSV en sus campos, respetando comillas.
     */
    private static String[] splitCSV(String line) {
        List<String> res = new ArrayList<>();
        StringBuilder cur = new StringBuilder();
        boolean inQuotes = false;
        for (int i = 0; i < line.length(); i++) {
            char c = line.charAt(i);
            if (c == '\"') {
                inQuotes = !inQuotes;
                cur.append(c);
            } else if (c == ',' && !inQuotes) {
                res.add(cur.toString());
                cur.setLength(0);
            } else {
                cur.append(c);
            }
        }
        res.add(cur.toString());
        return res.toArray(new String[0]);
    }
}


