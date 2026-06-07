package service;

import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.List;

import model.User;

public class LoginService {
    // 登入驗證、身分管理、登出
    // 目前登入使用者
    private User currentUser;

    // 登入
    public boolean login(String role, String id, String pass) {

        if ("學生".equals(role)) {
            User student = checkStudent(id, pass);
            if (student != null) {
                currentUser = student;
                return true;
            }
            // if ("stu".equals(id) && "111".equals(pass)) {
            // success = true;
            // }
        }

        if ("教職員".equals(role)) {
            User teacher = checkTeacher(id, pass);
            if (teacher != null) {
                currentUser = teacher;
                return true;
            }
            // if ("tea".equals(id) && "222".equals(pass)) {
            // success = true;
            // }
        }

        return false;
    }

    // 登出
    public void logout() {
        currentUser = null;
    }

    // 是否登入
    public boolean isLoggedIn() {
        return currentUser != null;
    }

    // 取得角色
    public String getRole() {
        return currentUser != null ? currentUser.getRole() : "";
    }

    // 取得使用者
    public User getCurrentUser() {
        return currentUser;
    }

    // 取得使用者 ID（便捷方法）
    public String getUserId() {
        return currentUser != null ? currentUser.getId() : "";
    }

    // 取得使用者名稱（便捷方法）
    public String getUserName() {
        return currentUser != null ? currentUser.getName() : "";
    }

    // Student CSV
    private User checkStudent(String id, String pwd) {

        try {
            List<String> lines = Files.readAllLines(
                    Paths.get("data/student list.csv"));

            for (int i = 1; i < lines.size(); i++) {

                String[] data = lines.get(i).split(",");

                if (data.length < 3) continue;

                String sid = data[0];
                String name = data[1];
                String password = data[2];

                if (sid.equals(id) && password.equals(pwd)) {
                    return new User(sid, "student", name);
                }
            }

        } catch (Exception e) {
            System.out.println("student login error: " + e.getMessage());
        }

        return null;
    }

    // Teacher CSV
    private User checkTeacher(String id, String pwd) {

        try {
            List<String> lines = Files.readAllLines(
                    Paths.get("data/teacher list.csv"));

            for (int i = 1; i < lines.size(); i++) {

                String[] data = lines.get(i).split(",");

                if (data.length < 3) continue;

                String tid = data[0];
                String name = data[1];
                String password = data[2];

                if (tid.equals(id) && password.equals(pwd)) {
                    return new User(tid, "teacher", name);
                }
            }

        } catch (Exception e) {
            System.out.println("teacher login error: " + e.getMessage());
        }

        return null;
    }

}
