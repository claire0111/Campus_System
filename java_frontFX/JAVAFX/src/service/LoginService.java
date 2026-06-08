package service;

import exception.DataLoadException;
import model.Organizer;
import model.Student;
import model.User;
import util.CsvUtil;

import java.util.List;

public class LoginService {

    private User currentUser;

    public boolean login(String role, String id, String pass) {
        if ("學生".equals(role)) {
            Student student = checkStudent(id, pass);
            if (student != null) {
                currentUser = student;
                return true;
            }
        }

        if ("教職員".equals(role) || "主辦單位".equals(role)) {
            Organizer organizer = checkOrganizer(id, pass);
            if (organizer != null) {
                currentUser = organizer;
                return true;
            }
        }

        return false;
    }

    public void logout() {
        currentUser = null;
    }

    public boolean isLoggedIn() {
        return currentUser != null;
    }

    public String getRole() {
        return currentUser != null ? currentUser.getRole() : "";
    }

    public User getCurrentUser() {
        return currentUser;
    }

    public String getUserId() {
        return currentUser != null ? currentUser.getId() : "";
    }

    public String getUserName() {
        return currentUser != null ? currentUser.getName() : "";
    }

    public boolean isOrganizer() {
        return currentUser != null && currentUser.isOrganizer();
    }

    public boolean isStudent() {
        return currentUser != null && currentUser.isStudent();
    }

    private Student checkStudent(String id, String pwd) {
        try {
            List<String[]> rows = CsvUtil.readAll("data/student list.csv");
            for (String[] data : rows) {
                if (data.length < 3) continue;
                if (data[0].equals(id) && data[2].equals(pwd)) {
                    return new Student(data[0], data[1]);
                }
            }
        } catch (DataLoadException e) {
            System.out.println("student login error: " + e.getMessage());
        }
        return null;
    }

    private Organizer checkOrganizer(String id, String pwd) {
        try {
            List<String[]> rows = CsvUtil.readAll("data/teacher list.csv");
            for (String[] data : rows) {
                if (data.length < 3) continue;
                if (data[0].equals(id) && data[2].equals(pwd)) {
                    String unit = data.length > 5 ? data[5] : "";
                    return new Organizer(data[0], data[1], unit);
                }
            }
        } catch (DataLoadException e) {
            System.out.println("organizer login error: " + e.getMessage());
        }
        return null;
    }
}
