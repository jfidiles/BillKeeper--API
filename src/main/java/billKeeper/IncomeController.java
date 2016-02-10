package billKeeper;

import java.util.concurrent.atomic.AtomicLong;
import org.springframework.web.bind.annotation.*;
import org.springframework.http.*;
import java.util.*;

@RestController
public class IncomeController {

    //Create new income
    @RequestMapping(value = "/income", method = RequestMethod.POST)
    public ResponseEntity<?> createNewIncome(@RequestHeader (value = "Authorization") String authorisation,
        @RequestBody Income createIncome) {

        if (!Authorisation.isValid(authorisation))
                return new ResponseEntity(HttpStatus.UNAUTHORIZED);

        if (!Validator.Income.isValid(createIncome))
            return new ResponseEntity<Error>(new Error(Validator.Income.getErrors(createIncome)),
                HttpStatus.BAD_REQUEST);

        Database db = Database.getInstance();
        db.createNewIncome(createIncome.getSource(), createIncome.getAmount(), createIncome.getDate(),
            Authorisation.userId);

        return new ResponseEntity(HttpStatus.CREATED);
    }

    //Get all incomes
    @RequestMapping(value = "/income", method = RequestMethod.GET)
    public ResponseEntity<?>  getincome(@RequestHeader (value = "Authorization") String authorisation) {

        if (!Authorisation.isValid(authorisation))
            return new ResponseEntity(HttpStatus.UNAUTHORIZED);

        if (!Validator.UserId.isValid(Authorisation.userId))
            return new ResponseEntity<Error>(new Error(Validator.UserId.getErrors(Authorisation.userId)),
                HttpStatus.BAD_REQUEST);

        Database db = Database.getInstance();
        List<HashMap> items = db.getIncome(Authorisation.userId);
        if (items == null || items.isEmpty())
            return new ResponseEntity(HttpStatus.NO_CONTENT);

    	return new ResponseEntity<List<HashMap>>(items, HttpStatus.OK);
    }

    //Get single income
    @RequestMapping(value = "/income/{incomeId}", method = RequestMethod.GET)
    public ResponseEntity<?>  getSingleIncome(
        @RequestHeader (value = "Authorization") String authorisation,
        @PathVariable("incomeId") int incomeId) {

            if (!Authorisation.isValid(authorisation))
                return new ResponseEntity(HttpStatus.UNAUTHORIZED);

            if (!Validator.UserId.isValid(Authorisation.userId))
                return new ResponseEntity<Error>(new Error(Validator.UserId.getErrors(Authorisation.userId)),
                    HttpStatus.BAD_REQUEST);

            Database db = Database.getInstance();
            if (!db.isUsersIncome(incomeId, Authorisation.userId))
                return new ResponseEntity(HttpStatus.FORBIDDEN);

            List<HashMap> items = db.getSingleIncome(incomeId);
            if (items == null || items.isEmpty())
                return new ResponseEntity(HttpStatus.NO_CONTENT);

        	return new ResponseEntity<List<HashMap>>(items, HttpStatus.OK);
    }

    //delete income - not done
     @RequestMapping(value = "/income/{incomeId}", method = RequestMethod.DELETE)
     public ResponseEntity<?> delete(@RequestHeader (value = "Authorization") String authorisation,
        @PathVariable ("incomeId") int incomeId) {

            if (!Authorisation.isValid(authorisation))
                return new ResponseEntity(HttpStatus.UNAUTHORIZED);

            if (!Validator.UserId.isValid(Authorisation.userId))
                return new ResponseEntity<Error>(new Error(Validator.UserId.getErrors(Authorisation.userId)),
                    HttpStatus.BAD_REQUEST);

            Database db = Database.getInstance();
            if (!db.isUsersIncome(incomeId, Authorisation.userId))
                return new ResponseEntity(HttpStatus.FORBIDDEN);

            db.deleteIncome(incomeId);
            return new ResponseEntity(HttpStatus.OK);
     }

    //Update income
    @RequestMapping(value = "/income/{incomeId}", method = RequestMethod.PUT)
    public ResponseEntity<?> updateIncome(@RequestHeader (value = "Authorization") String authorisation,
        @RequestBody Income updatedIncome,
        @PathVariable ("incomeId") int incomeId) {

            if (!Authorisation.isValid(authorisation))
                return new ResponseEntity(HttpStatus.UNAUTHORIZED);

            if (!Validator.Income.isValid(updatedIncome))
                return new ResponseEntity<Error>(new Error(Validator.Income.getErrors(updatedIncome)),
                    HttpStatus.BAD_REQUEST);

            Database db = Database.getInstance();
            if (!db.isUsersIncome(incomeId, Authorisation.userId))
                return new ResponseEntity(HttpStatus.FORBIDDEN);

            db.updateIncome(updatedIncome.getSource(), updatedIncome.getAmount(), updatedIncome.getDate(), incomeId);
    	    return new ResponseEntity(HttpStatus.OK);
        }
}
