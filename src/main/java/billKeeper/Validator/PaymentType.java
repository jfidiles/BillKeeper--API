package Validator;

import billKeeper.*;
import java.util.*;

public class PaymentType {

    public static boolean isValid(String paymentType) {
        if (!Parameter.isPayment(paymentType))
            return false;
        return true;
    }

    public static List<String> getErrors(String paymentType) {
        List<String> error = new ArrayList<>();
        if (!Parameter.isPayment(paymentType))
            error.add(AppConfig.PAYMENT_TYPE_EXCEPTION);
        return error;
    }
}
