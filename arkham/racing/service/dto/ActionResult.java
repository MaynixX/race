package arkham.racing.service.dto;

import java.io.Serializable;

/**
 * результат действия (успех/ошибка)
 */
public class ActionResult implements Serializable {
    private boolean success;
    private String message;

    public ActionResult(boolean success, String message) {
        this.success = success;
        this.message = message;
    }

    public static ActionResult success(String message) {
        return new ActionResult(true, message);
    }

    public static ActionResult failure(String message) {
        return new ActionResult(false, message);
    }

    public boolean isSuccess() {
        return success;
    }

    public String getMessage() {
        return message;
    }
}
