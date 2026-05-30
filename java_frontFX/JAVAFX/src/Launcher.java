public class Launcher {
    public static void main(String[] args) {
        // 繞過 JavaFX 的 runtime 檢查，直接從這裡啟動 Main
        Main.main(args);
    }
}