package billKeeper;

import java.util.concurrent.atomic.AtomicLong;
import org.springframework.web.bind.annotation.*;
import org.springframework.http.*;

@RestController
public class UserController {

    @RequestMapping(value = "/user", method = RequestMethod.POST)
    public ResponseEntity<User> newUser(@RequestBody User newUser) {
        Database db = Database.getInstance();
        String hashedPassword = (Encrypt.password(newUser.getPassword()));
        newUser.setPassword(hashedPassword);
    	db.createUser(newUser.getUsername(), hashedPassword, newUser.getEmail());
        return new ResponseEntity<User>(newUser, HttpStatus.CREATED);
    }
}
