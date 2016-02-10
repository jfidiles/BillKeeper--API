package Validator;

import billKeeper.*;
import java.util.*;

public class Bill {
    public static boolean isValid(billKeeper.Bill bill) {
        if (!Validator.PaymentType.isValid(bill.getPaymentType()))
            return false;
        if (!Validator.Title.isValid(bill.getTitle()))
            return false;
        if (!Validator.ParamDate.isValid(bill.getDate()))
            return false;
        if (!Validator.Category.isValid(bill.getCategory()))
            return false;
        return true;
    }

    public static List<String> getErrors(billKeeper.Bill bill) {
        List<String> errors = new ArrayList<>();
        if (!Validator.PaymentType.isValid(bill.getPaymentType()))
            errors.addAll(PaymentType.getErrors(bill.getPaymentType()));
        if (!Validator.Title.isValid(bill.getTitle()))
            errors.addAll(Title.getErrors(bill.getTitle()));
        if (!Validator.ParamDate.isValid(bill.getDate()))
            errors.addAll(ParamDate.getErrors(bill.getDate()));
        if (!Validator.Category.isValid(bill.getCategory()))
            errors.addAll(Category.getErrors(bill.getCategory()));
        return errors;
    }
}
