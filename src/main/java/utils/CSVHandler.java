package utils;

import models.Cultivo;
import models.Parcela;
import models.Actividad;
import models.EstadoCultivo;

import java.io.*;
import java.time.LocalDate;
import java.util.*;
import java.util.stream.Collectors;

public class CSVHandler {

    // ——————————————————————————————————————————————————————————————————————
    //    Métodos auxiliares para partir y limpiar líneas CSV con comillas
    // ——————————————————————————————————————————————————————————————————————
    private static String[] splitCSV(String line) {
        List<String> parts = new ArrayList<>();
        StringBuilder cur = new StringBuilder();
        boolean inQuotes = false;
        for (char c : line.toCharArray()) {
            if (c == '"') {
                inQuotes = !inQuotes;
                cur.append(c);
            } else if (c == ',' && !inQuotes) {
                parts.add(cur.toString());
                cur.setLength(0);
            } else {
                cur.append(c);
            }
        }
        parts.add(cur.toString());
        return parts.toArray(new String[0]);
    }

    private static String trimQuotes(String s) {
        if (s == null) return null;
        s = s.trim();
        if (s.startsWith("\"") && s.endsWith("\"") && s.length() >= 2) {
            return s.substring(1, s.length() - 1);
        }
        return s;
    }

    // ——————————————————————————————————————————————————————————————————————
    //    LECTURA/ESCRITURA DE PARCELAS
    // ——————————————————————————————————————————————————————————————————————
    public static List<Parcela> leerParcelas(String filePath) {
        List<Parcela> lista = new ArrayList<>();
        File f = new File(filePath);
        if (!f.exists()) return lista;
        try (BufferedReader br = new BufferedReader(new FileReader(f))) {
            String line;
            while ((line = br.readLine()) != null) {
                String[] p = line.split(",", 3);
                String cod = p[0].trim();
                double area = Double.parseDouble(p[1].trim().replace(',', '.'));
                String ubic = (p.length > 2 ? p[2].trim() : "");
                lista.add(new Parcela(cod, area, ubic));
            }
        } catch (IOException ex) {
            ex.printStackTrace();
        }
        return lista;
    }

    public static void guardarParcelas(Collection<Parcela> parcelas, String filePath) {
        try (BufferedWriter bw = new BufferedWriter(new FileWriter(filePath))) {
            for (Parcela p : parcelas) {
                bw.write(String.format(Locale.US, "%s,%.2f,%s",
                    p.getCodigo(),
                    p.getArea(),
                    p.getUbicacion() == null ? "" : p.getUbicacion()
                ));
                bw.newLine();
            }
        } catch (IOException ex) {
            ex.printStackTrace();
        }
    }

    // ——————————————————————————————————————————————————————————————————————
    //    LECTURA/ESCRITURA DE CULTIVOS (incluye actividades embebidas)
    // ——————————————————————————————————————————————————————————————————————
    public static List<Cultivo> leerCultivos(String filePath) throws IOException {
        List<Cultivo> cultivos = new ArrayList<>();
        Map<String, Parcela> parcelasMap = new HashMap<>();

        try (BufferedReader br = new BufferedReader(new FileReader(filePath))) {
            String line;
            while ((line = br.readLine()) != null) {
                String[] parts = splitCSV(line);
                if (parts.length < 7 || !parts[0].equalsIgnoreCase("Cultivo")) continue;

                String nombre     = trimQuotes(parts[1]);
                String variedad   = trimQuotes(parts[2]);
                double superficie = Double.parseDouble(parts[3]);
                String codPar     = trimQuotes(parts[4]);
                LocalDate fecha   = LocalDate.parse(trimQuotes(parts[5]));
                EstadoCultivo est = EstadoCultivo.valueOf(trimQuotes(parts[6]));

                Parcela parc = parcelasMap.computeIfAbsent(
                    codPar,
                    k -> new Parcela(k, 0.0, "")
                );

                Cultivo c = new Cultivo(nombre, variedad, superficie, parc, fecha, est);
                parc.addCultivo(c);

                // actividades embebidas en el CSV
                if (parts.length > 7) {
                    String raw = parts[7].trim();
                    if (raw.startsWith("[") && raw.endsWith("]")) {
                        raw = raw.substring(1, raw.length() - 1);
                        if (!raw.isEmpty()) {
                            String[] items = raw.split("\",\"");
                            for (String it : items) {
                                String seg = trimQuotes(it);
                                String[] f = seg.split(":", 3);
                                Actividad.Tipo t = Actividad.Tipo.valueOf(f[0]);
                                LocalDate d    = LocalDate.parse(f[1]);
                                Actividad a    = new Actividad(t, d);
                                if (f.length == 3 && f[2].equalsIgnoreCase("COMPLETADA")) {
                                    a.setCompletada(true);
                                }
                                c.addActividad(a);
                            }
                        }
                    }
                }

                cultivos.add(c);
            }
        }

        return cultivos;
    }

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
                  .append("\"").append(c.getEstadoEnum().name()).append("\",");

                // embebe actividades
                sb.append("[");
                List<Actividad> acts = c.getActividades();
                for (int i = 0; i < acts.size(); i++) {
                    Actividad a = acts.get(i);
                    sb.append("\"")
                      .append(a.getTipo().name()).append(":")
                      .append(a.getFecha()).append(":")
                      .append(a.isCompletada() ? "COMPLETADA" : "PENDIENTE")
                      .append("\"");
                    if (i < acts.size() - 1) sb.append(",");
                }
                sb.append("]");

                bw.write(sb.toString());
                bw.newLine();
            }
        }
    }

    // ——————————————————————————————————————————————————————————————————————
    //    LECTURA/ESCRITURA DE ACTIVIDADES (CSV aparte)
    // ——————————————————————————————————————————————————————————————————————
    public static Map<String, List<Actividad>> leerActividades(
            String actPath, List<Cultivo> memoria) throws IOException {
        File f = new File(actPath);
        Map<String, List<Actividad>> map = new HashMap<>();

        if (f.exists()) {
            try (BufferedReader br = new BufferedReader(new FileReader(f))) {
                String line;
                while ((line = br.readLine()) != null) {
                    String[] p = line.split(",", 4);
                    String nom = p[0].trim();
                    Actividad.Tipo t = Actividad.Tipo.valueOf(p[1].trim());
                    LocalDate d     = LocalDate.parse(p[2].trim());
                    boolean done    = p[3].trim().equalsIgnoreCase("COMPLETADA");
                    Actividad a     = new Actividad(t, d);
                    if (done) a.setCompletada(true);
                    map.computeIfAbsent(nom, k -> new ArrayList<>()).add(a);
                }
            }
        } else {
            for (Cultivo c : memoria) {
                for (Actividad a : c.getActividades()) {
                    map.computeIfAbsent(c.getNombre(), k -> new ArrayList<>()).add(a);
                }
            }
            guardarActividades(actPath, memoria);
        }

        return map;
    }

    public static void guardarActividades(String actPath, List<Cultivo> cultos) throws IOException {
        try (BufferedWriter bw = new BufferedWriter(new FileWriter(actPath))) {
            for (Cultivo c : cultos) {
                for (Actividad a : c.getActividades()) {
                    bw.write(String.join(",",
                        c.getNombre(),
                        a.getTipo().name(),
                        a.getFecha().toString(),
                        a.isCompletada() ? "COMPLETADA" : "PENDIENTE"
                    ));
                    bw.newLine();
                }
            }
        }
    }
}
