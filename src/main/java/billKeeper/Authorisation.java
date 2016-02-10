package billKeeper;

import java.util.Date;

import com.nimbusds.jose.*;
import com.nimbusds.jose.crypto.*;
import com.nimbusds.jwt.*;

import java.security.SecureRandom;

public class Authorisation {
    private static SecureRandom _random = new SecureRandom();
    private static byte[] _secret = null;
    public static String userId;
    private static SignedJWT signedJWT;
    private static JWSVerifier verifier;
    private static Date tokenDate;

    //check if the token is OK
    public static boolean isValid(String authorisationHeader) {
        String token = authorisationHeader.split(" ")[1];
        try {
            signedJWT = SignedJWT.parse(token);
            verifier = new MACVerifier(getSecret());
            if (!signedJWT.verify(verifier))
                return false;
            getData();
        } catch(Exception e) {
            return false;
        }
        if (!isExpired())
             return false;
        return true;
    }

    //get data after token validation
    private static void getData() {
        try {
            userId = signedJWT.getJWTClaimsSet().getIssuer();
            tokenDate = signedJWT.getJWTClaimsSet().getExpirationTime();
        } catch(Exception e) {
            Utilities.reportException(e);
        }
    }

    //Creates and returns the secret.
    public static final byte[] getSecret() {
        if (_secret == null) {
            _secret = new byte[32];
            _random.nextBytes(_secret);
        }
        return _secret;
    }

    //Check if the token has expired
    private static boolean isExpired() {
        if (new Date().before(tokenDate))
            return true;
        return false;
    }

}
