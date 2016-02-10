package Validator;

import billKeeper.*;
import java.util.*;

public class Income {

    public static boolean isValid(billKeeper.Income income) {
        if (!Validator.Source.isValid(income.getSource()))
            return false;
        if (!Validator.ParamDate.isValid(income.getDate()))
            return false;
        return true;
    }
    public static List<String> getErrors(billKeeper.Income income) {
        List<String> errors = new ArrayList<>();
        if (!Validator.Source.isValid(income.getSource()))
            errors.addAll(Validator.Source.getErrors(income.getSource()));
        if (!Validator.ParamDate.isValid(income.getDate()))
            errors.addAll(Validator.ParamDate.getErrors(income.getDate()));
        if (errors != null && !errors.isEmpty())
            return errors;
        return null;
    }
}
