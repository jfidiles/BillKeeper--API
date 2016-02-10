package billKeeper;

import org.springframework.web.bind.annotation.*;
import org.springframework.http.*;

@RestController
public class HomeController {

     @RequestMapping(value = "/", method = RequestMethod.GET)
     public String Home() {
         return "The API is running.";
     }
}
