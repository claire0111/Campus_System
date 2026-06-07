package model;

/**
 * 使用者抽象基底類別 — 學生與主辦單位共用身分資訊。
 */
public abstract class User {
    protected String id;
    protected String name;

    protected User(String id, String name) {
        this.id = id;
        this.name = name;
    }

    public String getId() { return id; }
    public String getName() { return name; }

    public abstract String getRole();
    public abstract void displayMenu();

    public boolean isOrganizer() { return false; }
    public boolean isStudent() { return false; }
}
