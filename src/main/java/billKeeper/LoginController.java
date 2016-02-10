package billKeeper;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.security.SecureRandom;
import java.util.Date;

import com.nimbusds.jose.*;
import com.nimbusds.jose.crypto.*;
import com.nimbusds.jwt.*;

@RestController
public class LoginController {
    public static long date;
    @RequestMapping(value = "/login", method = RequestMethod.POST)
    public ResponseEntity<?> login(@RequestBody HashMap loginDetails) {
        String token = "", username = "", password = "", hashedPassword = "", userId = "";
        byte[] secret = Authorisation.getSecret();
        HashMap<String, String> response = new HashMap();
        try {
            //get body
            username = loginDetails.get("username").toString();
            password = loginDetails.get("password").toString();

            //check if user exist
            Database db = Database.getInstance();
            hashedPassword = db.getLoginDetails(username);
            if (hashedPassword == null) {
                response.put("error", AppConfig.LOGIN_ERROR);
                return new ResponseEntity<HashMap>(response, HttpStatus.NOT_FOUND);
            } else {
                //check if password matches hashedPassword
                if (!Encrypt.isPasswordMatch(password, hashedPassword)) {
                    response.put("error", AppConfig.LOGIN_ERROR);
                    return new ResponseEntity<HashMap>(response, HttpStatus.NOT_FOUND);
                } else {
                    userId = db.getUserId(username);
                    JWSSigner signer = new MACSigner(secret);

                    // Prepare JWT with claims set
                    date = new Date().getTime();
                    JWTClaimsSet claimsSet = new JWTClaimsSet.Builder()
                            .expirationTime(new Date(date + AppConfig.TOKEN_EXPIRATION_TIME))
                            .claim("iss", userId)
                            .build();
                    SignedJWT signedJWT = new SignedJWT(new JWSHeader(JWSAlgorithm.HS256), claimsSet);

                    // Apply the HMAC protection
                    signedJWT.sign(signer);
                    token = signedJWT.serialize();
                    response.put("token", token);
                }
            }
        } catch(Exception ex) {
            Utilities.reportException(ex);
        }
        return new ResponseEntity<HashMap>(response, HttpStatus.OK);
    }
}
