package billKeeper;

import org.mindrot.jbcrypt.BCrypt;

public class Encrypt {

    //Check if the password that is inserted matches the one in the dabatase
    public static boolean isPasswordMatch (String password, String hashedPassword) {
        if(BCrypt.checkpw(password, hashedPassword)) {
            return true;
        }
        return false;
    }

    //return Bcrypted password
    public static String password(String password) {
        return BCrypt.hashpw(password, BCrypt.gensalt(10));
    }
}
