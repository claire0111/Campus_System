package service;

import java.io.FileWriter;
import java.io.PrintWriter;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.time.LocalDateTime;
import java.util.List;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import model.Registration;

public class RegistrationService {
    // 報名、取消報名、防重複
    private final String FILE_PATH = "data/registration list.csv";

    private ObservableList<Registration> registrations = FXCollections.observableArrayList();

    public RegistrationService() {
        loadFromCSV();
 
    }

    // 讀取 CSV
    public void loadFromCSV() {
         registrations.clear();

        List<String[]> data = FileService.readCSV(FILE_PATH);

        for (String[] row : data) {

            if (row.length < 4) continue;

            registrations.add(new Registration(
                    row[0],
                    row[1],
                    row[2],
                    row[3]
            ));
        }
    }

    // 報名
    public boolean register(String studentID, String activityID) {

        // ❌ 防重複報名
        for (Registration r : registrations) {
            if (r.getParticipantID().equals(studentID)
                    && r.getActivityID().equals(activityID)) {
                return false;
            }
        }

        String newSerial = generateSerial();
        String time = LocalDateTime.now().toString();

        Registration newReg = new Registration(
                newSerial,
                studentID,
                activityID,
                time
        );

        registrations.add(newReg);

        FileService.appendCSV(FILE_PATH, new String[]{
                newSerial,
                studentID,
                activityID,
                time
        });

        return true;
    }

    // 取消報名
    public void cancel(String studentID, String activityID) {

        registrations.removeIf(r ->
                r.getParticipantID().equals(studentID)
                        && r.getActivityID().equals(activityID)
        );

        saveToCSV();
    }

    // 查全部
    public ObservableList<Registration> getAll() {
        return registrations;
    }

    // 查某學生報名
    public ObservableList<Registration> getByStudent(String studentID) {

        ObservableList<Registration> result =
                FXCollections.observableArrayList();

        for (Registration r : registrations) {
            if (r.getParticipantID().equals(studentID)) {
                result.add(r);
            }
        }

        return result;
    }

    // 產生唯一序號
    private String generateSerial() {

        int max = 1000;

        for (Registration r : registrations) {
            try {
                int num = Integer.parseInt(r.getSerialNumber());
                max = Math.max(max, num);
            } catch (Exception ignored) {}
        }

        return String.valueOf(max + 1);
    }
    
    // 存 CSV
    private void saveToCSV() {

        java.util.List<String[]> data = new java.util.ArrayList<>();

        for (Registration r : registrations) {
            data.add(new String[]{
                    r.getSerialNumber(),
                    r.getParticipantID(),
                    r.getActivityID(),
                    r.getRegistrationTime()
            });
        }

        FileService.writeCSV(
                FILE_PATH,
                data,
                "serialNumber,participantID,activityID,registrationTime"
        );
    }

}
