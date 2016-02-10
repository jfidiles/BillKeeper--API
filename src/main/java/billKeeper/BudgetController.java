package billKeeper;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.*;

import org.springframework.http.*;
import org.springframework.web.bind.annotation.*;

@RestController
public class BudgetController {

    //Create new bill
    @RequestMapping(value = "/budget", method = RequestMethod.POST)
    public ResponseEntity<?> createBudget(@RequestHeader(value = "Authorization") String authorisation,
        @RequestBody Budget createBudget) {

            if (!Authorisation.isValid(authorisation))
                return new ResponseEntity(HttpStatus.UNAUTHORIZED);

            if (!Validator.Budget.isValid(createBudget))
                return new ResponseEntity<Error>(new Error(Validator.Budget.getErrors(createBudget)),
                    HttpStatus.BAD_REQUEST);

            Database db = Database.getInstance();
        	db.createBudget(createBudget.getCategory(), createBudget.getWishAmount(),
                createBudget.getPaidAmount(), createBudget.getDate(), Authorisation.userId);

            return new ResponseEntity(HttpStatus.CREATED);
    }

    //Get list of all items from budget table
	@RequestMapping(value =  "/budget", method=RequestMethod.GET)
    public ResponseEntity<?> budgetItems(
        @RequestHeader (value = "Authorization") String authorisation) {

            if (!Authorisation.isValid(authorisation))
                return new ResponseEntity(HttpStatus.UNAUTHORIZED);

            if (!Validator.UserId.isValid(Authorisation.userId))
                return new ResponseEntity<Error>(new Error(Validator.UserId.getErrors(Authorisation.userId)),
                    HttpStatus.BAD_REQUEST);

            Database db = Database.getInstance();
            List<HashMap> items = db.getBudget(Authorisation.userId);
            if (items == null || items.isEmpty())
                return new ResponseEntity(HttpStatus.NO_CONTENT);

    		return new ResponseEntity<List<HashMap>>(items, HttpStatus.OK);
	}

    //get single budget item
	@RequestMapping(value =  "/budget/{budgetId}", method=RequestMethod.GET)
    public ResponseEntity<?> onebudget(@RequestHeader (value = "Authorization") String authorisation,
        @PathVariable ("budgetId") int budgetId) {

            if (!Authorisation.isValid(authorisation))
                return new ResponseEntity(HttpStatus.UNAUTHORIZED);

            if (!Validator.UserId.isValid(Authorisation.userId))
                return new ResponseEntity<Error>(new Error(Validator.UserId.getErrors(Authorisation.userId)),
                    HttpStatus.BAD_REQUEST);

            Database db = Database.getInstance();
            if (!db.isUsersBudget(budgetId, Authorisation.userId))
                return new ResponseEntity(HttpStatus.FORBIDDEN);

            List<HashMap> items = db.getSingleBudget(budgetId);
            return new ResponseEntity<List<HashMap>>(items, HttpStatus.OK);
        }

        //Update bill
        @RequestMapping(value = "/budget/{budgetId}", method = RequestMethod.PUT)
    	public ResponseEntity<?> updateBudget(@RequestHeader (value = "Authorization") String authorisation,
            @RequestBody Budget updatedBudget,
            @PathVariable ("budgetId") int budgetId) {

                if (!Authorisation.isValid(authorisation))
                    return new ResponseEntity(HttpStatus.UNAUTHORIZED);

                if (!Validator.Budget.isValid(updatedBudget))
                    return new ResponseEntity<Error>(new Error(Validator.Budget.getErrors(updatedBudget)),
                        HttpStatus.BAD_REQUEST);

                Database db = Database.getInstance();
                if (!db.isUsersBudget(budgetId, Authorisation.userId))
                    return new ResponseEntity(HttpStatus.FORBIDDEN);

                db.updateBudgetItem(updatedBudget.getCategory(), updatedBudget.getWishAmount(),
                    updatedBudget.getPaidAmount(), updatedBudget.getDate(), budgetId);

                return new ResponseEntity(HttpStatus.OK);
    	}

        //Delete budget
        @RequestMapping(value = "/budget/{budgetId}", method = RequestMethod.DELETE)
        public ResponseEntity<?> deleteBudget(@RequestHeader (value = "Authorization") String authorisation,
            @PathVariable ("budgetId") int budgetId) {

                if (!Authorisation.isValid(authorisation))
                    return new ResponseEntity(HttpStatus.UNAUTHORIZED);

                if (!Validator.UserId.isValid(Authorisation.userId))
                    return new ResponseEntity<Error>(new Error(Validator.UserId.getErrors(Authorisation.userId)),
                        HttpStatus.BAD_REQUEST);

                Database db = Database.getInstance();
                if (!db.isUsersBudget(budgetId, Authorisation.userId))
                    return new ResponseEntity(HttpStatus.FORBIDDEN);

                db.deleteBudget(budgetId);
                return new ResponseEntity(HttpStatus.OK);
        }
}
