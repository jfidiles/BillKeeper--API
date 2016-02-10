package billKeeper;

import org.springframework.web.bind.annotation.*;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.*;

import com.nimbusds.jose.*;
import com.nimbusds.jose.crypto.*;
import com.nimbusds.jwt.*;

import org.springframework.http.*;

@RestController
public class BillController {

    //Create new bill
    @RequestMapping(value = "/bill", method = RequestMethod.POST)
    public ResponseEntity<?> createBill(@RequestHeader(value = "Authorization") String authorisation,
        @RequestBody Bill createBill) {

            if (!Authorisation.isValid(authorisation))
                return new ResponseEntity(HttpStatus.UNAUTHORIZED);

            if (!Validator.Bill.isValid(createBill))
                return new ResponseEntity<Error>(new Error(Validator.Bill.getErrors(createBill)),
                    HttpStatus.BAD_REQUEST);

            Database db = Database.getInstance();
            db.createBill(createBill.getPaymentType(), createBill.getTitle(),
                    createBill.getAmount(), createBill.getDate(), createBill.getCategory(), createBill.getNotes(),
                    Authorisation.userId);
           return new ResponseEntity(HttpStatus.CREATED);
    }

     //Get details about a single bill
     @RequestMapping(value = "/bill/{billId}", method = RequestMethod.GET)
        public ResponseEntity<?> getSelectedBill(
            @RequestHeader(value = "Authorization") String authorisation,
            @PathVariable ("billId") int billId) {

                if (!Authorisation.isValid(authorisation))
                    return new ResponseEntity(HttpStatus.UNAUTHORIZED);

                if (!Validator.UserId.isValid(Authorisation.userId))
                    return new ResponseEntity<Error>(new Error(Validator.UserId.getErrors(Authorisation.userId)),
                        HttpStatus.BAD_REQUEST);

                Database db = Database.getInstance();
                if (!db.isUsersBill(billId, Authorisation.userId))
                    return new ResponseEntity(HttpStatus.FORBIDDEN);

                List<HashMap> elements = db.getSingleBill(billId);
                return new ResponseEntity<List<HashMap>>(elements, HttpStatus.OK);
            }

     //Get list elements by paymentType
     @RequestMapping(value =  "/bill", params = {"paymentType"}, method=RequestMethod.GET)
     public ResponseEntity<?> billItems(@RequestHeader(value = "Authorization") String authorisation,
        @RequestParam (value = "paymentType", required = true) String paymentType) {

            if (!Authorisation.isValid(authorisation))
                return new ResponseEntity(HttpStatus.UNAUTHORIZED);

            if (!Validator.UserAndPayment.isValid(Authorisation.userId, paymentType))
                return new ResponseEntity<Error>(new Error(Validator.UserAndPayment.getErrors(Authorisation.userId,
                    paymentType)), HttpStatus.BAD_REQUEST);

            Database db = Database.getInstance();
            List<HashMap> billItems = db.getBillElements(Authorisation.userId, paymentType);
            if (billItems == null || billItems.isEmpty())
                return new ResponseEntity<Error>(new Error("No content"), HttpStatus.NO_CONTENT);

            return new ResponseEntity<List<HashMap>>(billItems, HttpStatus.OK);
     }

     //Delete bill
     @RequestMapping(value = "/bill/{billId}", method = RequestMethod.DELETE)
     public ResponseEntity<?> deleteBill(@RequestHeader (value = "Authorization") String authorisation,
        @PathVariable ("billId") int billId) {

            if (!Authorisation.isValid(authorisation))
                return new ResponseEntity(HttpStatus.UNAUTHORIZED);

            if (!Validator.UserId.isValid(Authorisation.userId))
                return new ResponseEntity<Error>(new Error(Validator.UserId.getErrors(Authorisation.userId)),
                    HttpStatus.BAD_REQUEST);

            Database db = Database.getInstance();
            if (!db.isUsersBill(billId, Authorisation.userId))
                return new ResponseEntity(HttpStatus.FORBIDDEN);

            db.deleteBill(billId);
            return new ResponseEntity(HttpStatus.OK);
     }

     //Mark bill as paid
     @RequestMapping(value = "/bill/pay/{billId}", method = RequestMethod.PUT)
     public ResponseEntity<?> pay(@RequestHeader (value = "Authorization") String authorisation,
        @PathVariable ("billId") int billId) {

            if (!Authorisation.isValid(authorisation))
                return new ResponseEntity(HttpStatus.UNAUTHORIZED);

            if (!Validator.UserId.isValid(Authorisation.userId))
                return new ResponseEntity<Error>(new Error(Validator.UserId.getErrors(Authorisation.userId)),
                    HttpStatus.BAD_REQUEST);

            Database db = Database.getInstance();
            if (!db.isUsersBill(billId, Authorisation.userId))
                return new ResponseEntity(HttpStatus.FORBIDDEN);

            db.payBill(billId);
            return new ResponseEntity(HttpStatus.OK);
     }

     //Update bill
      @RequestMapping(value = "/bill/{billId}", method = RequestMethod.PUT)
      public ResponseEntity<?> updateBill(@RequestHeader (value = "Authorization") String authorisation,
        @RequestBody Bill updatedBill, @PathVariable ("billId") int billId) {

            if (!Authorisation.isValid(authorisation))
                return new ResponseEntity(HttpStatus.UNAUTHORIZED);

            if (!Validator.Bill.isValid(updatedBill))
                return new ResponseEntity<Error>(new Error(Validator.Bill.getErrors(updatedBill)),
                    HttpStatus.BAD_REQUEST);

            Database db = Database.getInstance();
            if (!db.isUsersBill(billId, Authorisation.userId))
                return new ResponseEntity(HttpStatus.FORBIDDEN);


            db.modifyBill(updatedBill.getPaymentType(), updatedBill.getTitle(),
                updatedBill.getAmount(), updatedBill.getDate(), updatedBill.getCategory(),
                updatedBill.getNotes(), billId);

            return new ResponseEntity(HttpStatus.OK);
      }

      //Get sum(amount) and categories from bill
      @RequestMapping(value =  "bill/amount", params = {"groupBy", "paymentType"}, method = RequestMethod.GET)
      public ResponseEntity<?> getAmountByCategories(
        @RequestHeader (value = "Authorization") String authorisation,
        @RequestParam (value = "groupBy", required = true) String categories,
        @RequestParam (value = "paymentType", required = true) String paymentType) {

            if (!Authorisation.isValid(authorisation))
                return new ResponseEntity(HttpStatus.UNAUTHORIZED);

            if (!Validator.UserAndPayment.isValid(Authorisation.userId, paymentType))
                return new ResponseEntity<Error>(new Error(Validator.UserAndPayment.getErrors(Authorisation.userId,
                    paymentType)), HttpStatus.BAD_REQUEST);

            Database db = Database.getInstance();
            List<HashMap> getElements = db.getAmountForCategory(Authorisation.userId, paymentType);
            return new ResponseEntity<List<HashMap>>(getElements, HttpStatus.OK);
      }

      //Update payable bills to overdue
      //Mark bill as paid
      @RequestMapping(value = "/bill/update/payable", method = RequestMethod.PUT)
      public ResponseEntity<?> pay(@RequestHeader (value = "Authorization") String authorisation) {

             if (!Authorisation.isValid(authorisation))
                 return new ResponseEntity(HttpStatus.UNAUTHORIZED);

             if (!Validator.UserId.isValid(Authorisation.userId))
                 return new ResponseEntity<Error>(new Error(Validator.UserId.getErrors(Authorisation.userId)),
                     HttpStatus.BAD_REQUEST);

             Database db = Database.getInstance();
             db.updatePayableToOverdue(Authorisation.userId);
             return new ResponseEntity(HttpStatus.OK);
      }
}
