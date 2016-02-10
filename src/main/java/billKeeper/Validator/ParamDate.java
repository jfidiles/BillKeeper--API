package Validator;

import billKeeper.*;
import java.util.*;

public class ParamDate {

    public static boolean isValid(String date) {
        if (date == null)
            return false;
        if (Parameter.isEmpty(date))
            return false;
        if (!Parameter.isDateFormat(date))
            return false;
        return true;
    }

    public static List<String> getErrors(String date) {
        List<String> error = new ArrayList<>();
        if (date == null)
            error.add(AppConfig.DATE_IS_NULL);
        if (Parameter.isEmpty(date))
            error.add(AppConfig.DATE_IS_EMPTY);
        if (!Parameter.isDateFormat(date))
            error.add(AppConfig.DATE_WRONG_FORMAT);
        return error;
    }
}
