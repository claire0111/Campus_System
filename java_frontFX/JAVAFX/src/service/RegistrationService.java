package service;

import java.time.LocalDateTime;
import java.util.List;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import model.Event;
import model.Registration;
import util.EventStatusUtil;

public class RegistrationService {
    public enum RegisterResult {
        SUCCESS, DUPLICATE, FULL, CLOSED
    }

    private final String FILE_PATH = "data/registration list.csv";
    private ObservableList<Registration> registrations = FXCollections.observableArrayList();

    public RegistrationService() {
        loadFromCSV();
    }

    public void loadFromCSV() {
        registrations.clear();
        List<String[]> data = FileService.readCSV(FILE_PATH);

        for (String[] row : data) {
            if (row.length < 4) continue;
            registrations.add(new Registration(row[0], row[1], row[2], row[3]));
        }
    }

    public RegisterResult register(String studentID, Event event) {
        String currentStatus = EventStatusUtil.calculate(
                event.getRegStart(), event.getRegEnd(), LocalDateTime.now());
        event.setStatus(currentStatus);

        if (!EventStatusUtil.isRegistrationOpen(currentStatus)) {
            return RegisterResult.CLOSED;
        }

        String activityID = event.getId();

        for (Registration r : registrations) {
            if (r.getParticipantID().equals(studentID)
                    && matchesActivity(r.getActivityID(), event)) {
                return RegisterResult.DUPLICATE;
            }
        }

        if (event.getLimit() > 0 && countByActivity(event) >= event.getLimit()) {
            return RegisterResult.FULL;
        }

        String newSerial = generateSerial();
        String time = LocalDateTime.now().toString();

        Registration newReg = new Registration(newSerial, studentID, activityID, time);
        registrations.add(newReg);

        FileService.appendCSV(FILE_PATH, new String[]{
                newSerial, studentID, activityID, time
        });

        return RegisterResult.SUCCESS;
    }

    public void cancel(String studentID, String activityID) {
        registrations.removeIf(r ->
                r.getParticipantID().equals(studentID)
                        && r.getActivityID().equals(activityID)
        );
        saveToCSV();
    }

    public void cancelByEvent(Event event) {
        registrations.removeIf(r -> matchesActivity(r.getActivityID(), event));
        saveToCSV();
    }

    public int countByActivity(Event event) {
        int count = 0;
        for (Registration r : registrations) {
            if (matchesActivity(r.getActivityID(), event)) {
                count++;
            }
        }
        return count;
    }

    public boolean isRegistered(String studentID, Event event) {
        for (Registration r : registrations) {
            if (r.getParticipantID().equals(studentID)
                    && matchesActivity(r.getActivityID(), event)) {
                return true;
            }
        }
        return false;
    }

    public boolean isFull(Event event) {
        return event.getLimit() > 0 && countByActivity(event) >= event.getLimit();
    }

    private boolean matchesActivity(String activityID, Event event) {
        return activityID.equals(event.getId()) || activityID.equals(event.getName());
    }

    public ObservableList<Registration> getAll() {
        return registrations;
    }

    public ObservableList<Registration> getByStudent(String studentID) {
        ObservableList<Registration> result = FXCollections.observableArrayList();
        for (Registration r : registrations) {
            if (r.getParticipantID().equals(studentID)) {
                result.add(r);
            }
        }
        return result;
    }

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
        FileService.writeCSV(FILE_PATH, data,
                "serialNumber,participantID,activityID,registrationTime");
    }
}
