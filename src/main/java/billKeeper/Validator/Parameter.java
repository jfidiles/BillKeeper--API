package Validator;

import billKeeper.*;
import java.util.*;
import java.text.*;

public class Parameter {

    public static boolean isEmpty(String parameter) {
        return parameter.trim().length() == 0;
    }

    public static boolean isLengthValid(String parameter) {
        return parameter.length() <= AppConfig.PARAM_MAX_LENGTH;
    }

    public static boolean isDateFormat(String parameter) {
        String expectedPattern = AppConfig.DATE_PATTERN;
        SimpleDateFormat dateFormat = new SimpleDateFormat(expectedPattern);
        try {
            Date date = dateFormat.parse(parameter);
        } catch (ParseException e) {
            return false;
        }
        return true;
    }

    public static boolean isPayment(String paymentType) {
        switch(paymentType) {
            case AppConfig.PAID : return true;
            case AppConfig.PAYABLE : return true;
            case AppConfig.OVERDUE : return true;
        }
        return false;
    }

    public static boolean isSourceValid(String source) {
        switch(source) {
            case AppConfig.SALARY : return true;
            case AppConfig.BONUS : return true;
            case AppConfig.SAVINGS : return true;
            case AppConfig.LOAN : return true;
            case AppConfig.PENSION : return true;
            case AppConfig.OTHER_SOURCE : return true;
        }
        return false;
    }

    public static boolean isUserIdValid(String userId) {
        try {
            double id = Double.parseDouble(userId);
        } catch (NumberFormatException e) {
            return false;
        }
        return true;
    }
}
