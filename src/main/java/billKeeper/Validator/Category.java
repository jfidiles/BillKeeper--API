package Validator;

import billKeeper.*;
import java.util.*;

public class Category {

    public static boolean isValid(String category) {
        if (category == null)
            return false;
        if (Parameter.isEmpty(category))
            return false;
        if (!Parameter.isLengthValid(category))
            return false;
        return true;
    }

    public static List<String> getErrors(String category) {
        List<String> errors = new ArrayList<>();
        if (category == null)
            errors.add(AppConfig.CATEGORY_IS_NULL);
        if (Parameter.isEmpty(category))
            errors.add(AppConfig.CATEGORY_IS_EMPTY);
        if (!Parameter.isLengthValid(category))
            errors.add(AppConfig.CATEGORY_IS_TO_LONG);
        return errors;
    }
}
