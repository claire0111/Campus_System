package service;

import java.io.FileWriter;
import java.io.PrintWriter;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;

import javafx.collections.ObservableList;

public class FileService {
    // 檔案讀取、寫入
    // 讀 CSV
    public static List<String[]> readCSV(String filePath) {

        List<String[]> data = new ArrayList<>();

        try {
            List<String> lines = Files.readAllLines(Paths.get(filePath));

            for (int i = 1; i < lines.size(); i++) { // skip header
                String line = lines.get(i).trim();

                if (line.isEmpty()) continue;

                String[] parts = line.split(",");

                data.add(parts);
            }

        } catch (Exception e) {
            System.out.println("❌ 讀取 CSV 失敗: " + filePath);
            System.out.println(e.getMessage());
        }

        return data;
    }

    // 寫 CSV（覆蓋）
    public static void writeCSV(String filePath, List<String[]> data, String header) {

        try (PrintWriter pw = new PrintWriter(new FileWriter(filePath))) {

            // header
            if (header != null && !header.isEmpty()) {
                pw.println(header);
            }

            // data
            for (String[] row : data) {
                pw.println(String.join(",", row));
            }

        } catch (Exception e) {
            System.out.println("❌ 寫入 CSV 失敗: " + filePath);
            System.out.println(e.getMessage());
        }
    }

    // 單行 append（新增資料）
    public static void appendCSV(String filePath, String[] row) {

        try (FileWriter fw = new FileWriter(filePath, true);
             PrintWriter pw = new PrintWriter(fw)) {

            pw.println(String.join(",", row));

        } catch (Exception e) {
            System.out.println("❌ append CSV 失敗: " + filePath);
            System.out.println(e.getMessage());
        }
    }

}
