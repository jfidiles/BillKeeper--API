package Validator;

import billKeeper.*;
import java.util.*;

public class Title {

    public static boolean isValid(String title) {
        if (title == null)
            return false;
        if (Parameter.isEmpty(title))
            return false;
        if (!Parameter.isLengthValid(title))
            return false;
        return true;
    }

    public static List<String> getErrors(String title) {
        List<String> errors = new ArrayList<>();
        if (title == null)
            errors.add(AppConfig.TITLE_IS_NULL);
        if (Parameter.isEmpty(title))
            errors.add(AppConfig.TITLE_IS_EMPTY);
        if (!Parameter.isLengthValid(title))
            errors.add(AppConfig.TITLE_IS_TO_LONG);
        return errors;
    }
}
