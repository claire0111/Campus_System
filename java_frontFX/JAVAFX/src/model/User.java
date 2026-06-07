package model;

public class User {

    // 紀錄目前登入的狀態與身分 (預設未登入)
    private boolean isLoggedIn = false;
    private String currentUserRole = "";

    private String id;
    private String name;
    private String role;// 學生 / 教職員

    public User(String id, String role, String name) {
        this.name = name;
        this.id = id;
        this.role = role;
    }

    public String getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public String getRole() {
        return role;
    }

    public boolean isOrganizer() {
        return role.equals("ORGANIZER");
    }
}
