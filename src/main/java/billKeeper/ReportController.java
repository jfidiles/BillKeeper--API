package billKeeper;

import java.util.concurrent.atomic.AtomicLong;
import org.springframework.web.bind.annotation.*;
import org.springframework.http.*;
import java.util.*;

@RestController
public class ReportController {

    //Get list of months where payments were made
   @RequestMapping(value =  "/report/payments", params = {"groupBy"}, method = RequestMethod.GET)
     public ResponseEntity<?> periodOfActivity(
       @RequestHeader (value = "Authorization") String authorisation,
       @RequestParam (value = "groupBy", required = true) String group) {

            if (!Authorisation.isValid(authorisation))
                return new ResponseEntity (HttpStatus.UNAUTHORIZED);

            if (!Validator.UserId.isValid(Authorisation.userId))
                return new ResponseEntity<Error>(new Error(Validator.UserId.getErrors(Authorisation.userId)),
                    HttpStatus.BAD_REQUEST);

            Database db = Database.getInstance();
            List<HashMap> items = db.periodActivity(Authorisation.userId);
            if (items == null || items.isEmpty())
                return new ResponseEntity(HttpStatus.NO_CONTENT);

            return new ResponseEntity<List<HashMap>>(items, HttpStatus.OK);
    }

    /*
        GetBudgetController keeps track of the total sum(amount) of paid, payable, overdue bills and incomes
        or for certain periods of time (today, 1 month ago, 3 months ago, etc.)
    */
        @RequestMapping(value =  "/report/budget", method=RequestMethod.GET)
        public ResponseEntity<?> getBudget(@RequestHeader (value = "Authorization") String authorisation) {

            if (!Authorisation.isValid(authorisation))
                return new ResponseEntity (HttpStatus.UNAUTHORIZED);

            if (!Validator.UserId.isValid(Authorisation.userId))
                return new ResponseEntity<Error>(new Error(Validator.UserId.getErrors(Authorisation.userId)),
                    HttpStatus.BAD_REQUEST);

            Database db = Database.getInstance();
            HashMap<String, Double> budget = new HashMap<>();
            budget.put("paid", db.getTotalAmount(Authorisation.userId, "paid"));
            budget.put("payable", db.getTotalAmount(Authorisation.userId, "payable"));
            budget.put("overdue", db.getTotalAmount(Authorisation.userId, "overdue"));
            budget.put("income", db.getTotalAmount(Authorisation.userId, "income"));

            return new ResponseEntity<HashMap>(budget, HttpStatus.OK);
        }

        //Get sum(amount) for different periods of time: today, 1,3,6 months ago and 1 year
        @RequestMapping(value = "/report/summary", method = RequestMethod.GET)
        public ResponseEntity<?> getSummary(@RequestHeader (value = "Authorization") String authorisation) {

            int period[]={0, 30, 90, 180, 360};

            if (!Authorisation.isValid(authorisation))
                return new ResponseEntity (HttpStatus.UNAUTHORIZED);

            if (!Validator.UserId.isValid(Authorisation.userId))
                return new ResponseEntity<Error>(new Error(Validator.UserId.getErrors(Authorisation.userId)),
                    HttpStatus.BAD_REQUEST);

            Database db = Database.getInstance();
            List<HashMap> summaryItems = new ArrayList<>();
            int length = period.length;
       		for (int i = 0; i < length; i++) {
                HashMap<String, Double> items = new HashMap<>();
    			items.put("paid", db.getSummary(Authorisation.userId, "paid", period[i]));
                items.put("payable", db.getSummary(Authorisation.userId, "payable", period[i]));
                items.put("overdue", db.getSummary(Authorisation.userId, "overdue", period[i]));
                items.put("income", db.getSummary(Authorisation.userId, "income", period[i]));

	   			summaryItems.add(items);
            }
       		return new ResponseEntity<List<HashMap>>(summaryItems, HttpStatus.OK);
       	}

        //Get total amount paid  for every category
        @RequestMapping(value = "/report/amount", method = RequestMethod.GET)
        public ResponseEntity<?> getAmountbyCategories(@RequestHeader (value = "Authorization") String authorisation) {

            if (!Authorisation.isValid(authorisation))
                return new ResponseEntity (HttpStatus.UNAUTHORIZED);

            if (!Validator.UserId.isValid(Authorisation.userId))
                return new ResponseEntity<Error>(new Error(Validator.UserId.getErrors(Authorisation.userId)),
                    HttpStatus.BAD_REQUEST);

            Database db = Database.getInstance();
            List<HashMap> amount = db.getAmountByCategories(Authorisation.userId);
            if (amount == null || amount.isEmpty())
                return new ResponseEntity(HttpStatus.NO_CONTENT);

       		return new ResponseEntity<List<HashMap>>(amount, HttpStatus.OK);
       	}

        //Get First Page elements
        ////Get total amount paid  for every category
        @RequestMapping(value = "/report/firstPage", method = RequestMethod.GET)
        public ResponseEntity<?> getFirstPage(@RequestHeader (value = "Authorization") String authorisation) {

            if (!Authorisation.isValid(authorisation))
                return new ResponseEntity (HttpStatus.UNAUTHORIZED);

            if (!Validator.UserId.isValid(Authorisation.userId))
                return new ResponseEntity<Error>(new Error(Validator.UserId.getErrors(Authorisation.userId)),
                    HttpStatus.BAD_REQUEST);

            Database db = Database.getInstance();
            HashMap amount = db.getFirstPageDetails(Authorisation.userId);
            if (amount == null || amount.isEmpty())
                return new ResponseEntity(HttpStatus.NO_CONTENT);

       		return new ResponseEntity<HashMap>(amount, HttpStatus.OK);
       	}
}
