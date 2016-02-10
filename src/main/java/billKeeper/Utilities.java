package billKeeper;

import java.text.DecimalFormat;

public final class Utilities {

    public static final void reportException(Exception e) {
        System.err.println(e.getClass().getName() + ": " + e.getMessage());
        System.exit(0);
    }
    public static double getTwoDecimal (Double number) {
        DecimalFormat twoDForm = new DecimalFormat("#.##");
        Double temp = Double.valueOf(twoDForm.format(number));
        return temp;
    }
}
