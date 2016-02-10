package Validator;

import java.util.*;

public class UserAndPayment {

    public static boolean isValid(String userId, String paymentType) {
        if (!Validator.PaymentType.isValid(paymentType))
            return false;
        if (!Validator.UserId.isValid(userId))
            return false;
        return true;
    }

    public static List<String> getErrors(String userId, String paymentType) {
        List<String> errors = new ArrayList<>();
        if (!Validator.PaymentType.isValid(paymentType))
            errors.addAll(Validator.PaymentType.getErrors(paymentType));
        if (!Validator.UserId.isValid(userId))
            errors.addAll(Validator.UserId.getErrors(userId));
        if (errors != null && !errors.isEmpty())
            return errors;
        return null;
    }

}
