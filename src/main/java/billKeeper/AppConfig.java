package billKeeper;

import java.security.SecureRandom;

public final class AppConfig {

    public static final String DB_LOCATION = "database/database.sqlite";
    public static final String TABLE_BILL = "bill";
    public static final String TABLE_BUDGET = "budget";
    public static final String TABLE_INCOME = "income";
    public static final String TABLE_USER = "user";
    public static final String LOGIN_ERROR = "Wrong password or username!";
    public static final long TOKEN_EXPIRATION_TIME = 30 * 60 * 1000;

    /*
        * Sanitize constants
    */
    //PaymentType
    public static final String PAID = "paid";
    public static final String PAYABLE = "payable";
    public static final String OVERDUE = "overdue";

    public static final int PARAM_MAX_LENGTH = 30;

    //Date
    public static final String DATE_PATTERN = "dd-MM-yyyy";

    //Source
    public static final String SALARY = "Salary";
    public static final String BONUS = "Bonus";
    public static final String SAVINGS = "Savings";
    public static final String LOAN = "Loan";
    public static final String PENSION = "Pension";
    public static final String OTHER_SOURCE = "Other source";



    /*
        * Exceptions
    */
    //payment_type
    public static final String PAYMENT_TYPE_EXCEPTION = "Wrong payment type. Make sure it`s in the right format.";

    //title
    public static final String TITLE_IS_NULL = "title is null";
    public static final String TITLE_IS_EMPTY = "title is empty";
    public static final String TITLE_IS_TO_LONG = "title has to many characters";

    //Date
    public static final String DATE_IS_NULL = "date is null.";
    public static final String DATE_IS_EMPTY = "date is empty";
    public static final String DATE_WRONG_FORMAT = "date has a wrong format";
    public static final String DATE_IS_NOT_NUMERIC = "date must contain only numbers";

    //Category
    public static final String CATEGORY_IS_NULL = "category is null.";
    public static final String CATEGORY_IS_EMPTY = "category is empty";
    public static final String CATEGORY_IS_TO_LONG = "category has to many characters";

    //UserId
    public static final String USERID_IS_NOT_NUMERIC = "userId is in a wrong format";

    //Source
    public static final String SOURCE_EXCEPTION = "Wrong source. Make sure it`s in the right format";
}
