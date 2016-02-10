package Validator;

import billKeeper.*;
import java.util.*;

public class Source {

    public static boolean isValid(String source) {
        if (!Parameter.isSourceValid(source))
            return false;;
        return true;
    }

    public static List<String> getErrors(String source) {
        List<String> error = new ArrayList<>();
        if (!Parameter.isSourceValid(source))
            error.add(AppConfig.SOURCE_EXCEPTION);
        return error;
    }
}
