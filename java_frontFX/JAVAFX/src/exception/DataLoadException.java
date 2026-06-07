package exception;

/**
 * 資料載入失敗時拋出的自訂例外。
 */
public class DataLoadException extends Exception {
    public DataLoadException(String message) {
        super(message);
    }

    public DataLoadException(String message, Throwable cause) {
        super(message, cause);
    }
}
