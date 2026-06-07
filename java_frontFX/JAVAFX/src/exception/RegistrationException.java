package exception;

/**
 * 報名相關業務錯誤的自訂例外。
 */
public class RegistrationException extends Exception {
    public RegistrationException(String message) {
        super(message);
    }
}
