package util;

import exception.DataLoadException;

import java.io.FileWriter;
import java.io.PrintWriter;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;

/**
 * 統一 CSV 讀寫工具（支援引號與換行欄位）。
 */
public class CsvUtil {

    public static List<String[]> readAll(String filePath) throws DataLoadException {
        List<String[]> rows = new ArrayList<>();
        try {
            List<String> lines = Files.readAllLines(Paths.get(filePath), StandardCharsets.UTF_8);
            if (lines.isEmpty()) return rows;

            List<String> logicalLines = mergeQuotedLines(lines.subList(1, lines.size()));
            for (String line : logicalLines) {
                if (line.trim().isEmpty()) continue;
                rows.add(parseLine(line));
            }
        } catch (Exception e) {
            throw new DataLoadException("無法讀取 CSV：" + filePath, e);
        }
        return rows;
    }

    public static void writeAll(String filePath, String header, List<String[]> rows) throws DataLoadException {
        try (PrintWriter pw = new PrintWriter(new FileWriter(filePath, StandardCharsets.UTF_8))) {
            if (header != null && !header.isEmpty()) pw.println(header);
            for (String[] row : rows) {
                pw.println(joinRow(row));
            }
        } catch (Exception e) {
            throw new DataLoadException("無法寫入 CSV：" + filePath, e);
        }
    }

    public static void appendRow(String filePath, String[] row) throws DataLoadException {
        try (PrintWriter pw = new PrintWriter(new FileWriter(filePath, StandardCharsets.UTF_8, true))) {
            pw.println(joinRow(row));
        } catch (Exception e) {
            throw new DataLoadException("無法附加 CSV：" + filePath, e);
        }
    }

    public static String[] parseLine(String line) {
        boolean inQuote = false;
        StringBuilder sb = new StringBuilder();
        List<String> result = new ArrayList<>();

        for (char c : line.toCharArray()) {
            if (c == '"') {
                inQuote = !inQuote;
            } else if (c == ',' && !inQuote) {
                result.add(sb.toString().trim());
                sb.setLength(0);
            } else {
                sb.append(c);
            }
        }
        result.add(sb.toString().trim());
        return result.toArray(new String[0]);
    }

    public static String escape(String value) {
        if (value == null) return "";
        if (value.contains(",") || value.contains("\"") || value.contains("\n")) {
            return "\"" + value.replace("\"", "\"\"") + "\"";
        }
        return value;
    }

    private static String joinRow(String[] row) {
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < row.length; i++) {
            if (i > 0) sb.append(",");
            sb.append(escape(row[i]));
        }
        return sb.toString();
    }

    private static List<String> mergeQuotedLines(List<String> lines) {
        List<String> logical = new ArrayList<>();
        StringBuilder current = new StringBuilder();
        boolean inQuote = false;

        for (String line : lines) {
            if (current.length() > 0) current.append("\n").append(line);
            else current.append(line);

            for (char c : line.toCharArray()) {
                if (c == '"') inQuote = !inQuote;
            }
            if (!inQuote) {
                logical.add(current.toString());
                current = new StringBuilder();
            }
        }
        if (current.length() > 0) logical.add(current.toString());
        return logical;
    }
}
