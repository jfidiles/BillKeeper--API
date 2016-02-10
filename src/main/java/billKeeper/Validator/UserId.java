package Validator;

import billKeeper.*;
import java.util.*;

public class UserId {

    public static boolean isValid(String userId) {
        if (!Parameter.isUserIdValid(userId))
            return false;
        return true;
    }

    public static List<String> getErrors(String userId) {
        List<String> error = new ArrayList<>();
        if (!Parameter.isUserIdValid(userId))
            error.add(AppConfig.USERID_IS_NOT_NUMERIC);
        return error;
    }
}
