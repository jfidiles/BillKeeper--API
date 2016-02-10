package Validator;

import billKeeper.*;
import java.util.*;

public class Budget {

    public static boolean isValid(billKeeper.Budget budget) {
        if (!Validator.Category.isValid(budget.getCategory()))
            return false;
        if (!Validator.ParamDate.isValid(budget.getDate()))
            return false;
        return true;
    }

    public static List<String> getErrors(billKeeper.Budget budget) {
        List<String> errors = new ArrayList<>();
        if (!Validator.Category.isValid(budget.getCategory()))
            errors.addAll(Validator.Category.getErrors(budget.getCategory()));
        if (!Validator.ParamDate.isValid(budget.getDate()))
            errors.addAll(Validator.ParamDate.getErrors(budget.getDate()));
        if (errors != null && !errors.isEmpty())
            return errors;
        return null;
    }
}
