package billKeeper;

import org.springframework.core.ExceptionDepthComparator;

import org.mindrot.jbcrypt.BCrypt;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.HashMap;

import com.nimbusds.jose.*;
import com.nimbusds.jose.crypto.*;
import com.nimbusds.jwt.*;

public class Database {
    private static Database _instance = null;
    Connection connection = null;
    String tableBill = AppConfig.TABLE_BILL;
    String tableIncome = AppConfig.TABLE_INCOME;
    String tableBudget = AppConfig.TABLE_BUDGET;
    String tableUser = AppConfig.TABLE_USER;

    private Database() {
        try  {
            Class.forName("org.sqlite.JDBC");
            connection = DriverManager.getConnection("jdbc:sqlite:" + AppConfig.DB_LOCATION);
            connection.setAutoCommit(true) ;
        }  catch(Exception e)  {
            Utilities.reportException(e);
        }
    }

    public static Database getInstance() {
        if (_instance == null) {
            _instance = new Database();
        }
        return _instance;
    }

//-----------------------------------------Bill---------------------------------------------------------------------

    public void createBill(String paymentType, String title, double amount, String date,
 		 String category, String notes, String userId) {

            try {
                 String query = "insert into " + tableBill +
             	 		" (payment_type, title, amount, date, category, notes, id_user) values (?, ?, ?, ?, ?, ?, ?)";

                 PreparedStatement statement = connection.prepareStatement(query);
                 statement.setString(1, paymentType);
                 statement.setString(2, title);
                 statement.setDouble(3, amount);
                 statement.setString(4, date);
                 statement.setString(5, category);
                 statement.setString(6, notes);
                 statement.setString(7, userId);
                 statement.executeUpdate();
            } catch(Exception e) {
                 Utilities.reportException(e);
            }
    }

    public void modifyBill(String paymentType, String title, double amount, String date,
	 		String category, String notes, int billId) {

            try {
                String query = "update " + tableBill + " set " +
					" payment_type = ?, title = ?, amount = ?, date = ?, category = ?, notes = ? where id_bill = ?";

                PreparedStatement statement = connection.prepareStatement(query);
                statement.setString(1, paymentType);
                statement.setString(2, title);
                statement.setDouble(3, amount);
                statement.setString(4, date);
                statement.setString(5, category);
                statement.setString(6, notes);
                statement.setInt(7, billId);
                statement.executeUpdate();
            } catch(Exception e) {
                System.err.println(e.getClass().getName() + ": " + e.getMessage());
                System.exit(0);
            }
    }

    public List<HashMap>  getBillElements(String userId, String paymentType) {
          List<HashMap> items = new ArrayList<>();
          try {
              String query = "select * from " + tableBill +" where id_user = ? and payment_type = ?";

              PreparedStatement statement = connection.prepareStatement(query);
              statement.setString(1, userId);
              statement.setString(2, paymentType);
              ResultSet resultSet = statement.executeQuery();

              while (resultSet.next()) {
                  HashMap<String, String> element = new HashMap<>();
                  element.put("billId", Integer.toString(resultSet.getInt("id_bill")));
                  element.put("title" , resultSet.getString("title"));
                  element.put("date", resultSet.getString("date"));
                  element.put("notes", resultSet.getString("notes"));
                  element.put("category", resultSet.getString("category"));
                  element.put("amount", Double.toString(resultSet.getDouble("amount")));
                  items.add(element);
              }
          } catch(Exception e) {
              Utilities.reportException(e);
          }
          return items;
    }

    public List<HashMap> getSingleBill(int billId) {
        List<HashMap> item = new ArrayList<>();
        try {
            String query = "select * from " + tableBill + " where id_bill = ?";

            PreparedStatement statement = connection.prepareStatement(query);
            statement.setInt(1, billId);

            ResultSet resultSet = statement.executeQuery();

            HashMap<String, String> element = new HashMap<>();
            if (resultSet.next()) {
                element.put("title" , resultSet.getString("title"));
                element.put("date", resultSet.getString("date"));
                element.put("notes", resultSet.getString("notes"));
                element.put("paymentType", resultSet.getString("payment_type"));
                element.put("category", resultSet.getString("category"));
                element.put("amount", Double.toString(resultSet.getDouble("amount")));
                item.add(element);
            }
        } catch(Exception e) {
            Utilities.reportException(e);
        }
        return item;
    }

    public void deleteBill(int billId) {

        try {
            String query = "delete from " + tableBill + " where id_bill = ?";

            PreparedStatement statement = connection.prepareStatement(query);
            statement.setInt(1, billId);
            statement.executeUpdate();
        } catch(Exception e) {
            Utilities.reportException(e);
        }
    }

    public void payBill(int billId) {
        try {
            String query = "update " + tableBill + " set payment_type = 'paid' where id_bill = ?";

            PreparedStatement statement = connection.prepareStatement(query);
            statement.setInt(1, billId);
            statement.executeUpdate();
        } catch(Exception e) {
            Utilities.reportException(e);
        }
    }

    public List<HashMap>  getAmountForCategory(String userId, String paymentType) {
          List<HashMap> categories = new ArrayList<>();
          try {
              String query = "select sum(amount) as amount, category from " + tableBill +
                  " where id_user = ? and payment_type = ? group by category";

              PreparedStatement statement = connection.prepareStatement(query);
              statement.setString(1, userId);
              statement.setString(2, paymentType);

              ResultSet resultSet = statement.executeQuery();
              while (resultSet.next()) {
                  HashMap<String, String> item = new HashMap<>();
                  item.put("amount", Double.toString(resultSet.getDouble("amount")));
                  item.put("category", resultSet.getString("category"));

                  categories.add(item);
              }
          } catch(Exception e) {
              Utilities.reportException(e);
          }
          return categories;
    }

    public boolean isUsersBill(int billId, String userId) {
        ResultSet resultSet = null;
        try {
            String query = "select * from " + tableBill + " where id_bill = ? and id_user = ?";
            PreparedStatement statement = connection.prepareStatement(query);
            statement.setInt(1, billId);
            statement.setString(2, userId);

            resultSet = statement.executeQuery();
            if (resultSet.next())
                return true;
        } catch(Exception e) {
            Utilities.reportException(e);
        }
        return false;
    }

    public void updatePayableToOverdue (String userId) {
        try {
            String query = "update " + tableBill + " set payment_type = 'overdue' where " +
            "datetime('now', '-1 days') > (substr(date, 7) || \"-\" || substr(date,4,2)  || \"-\" || substr(date, 1,2)) " +
            "  and payment_type = 'payable' and id_user = ?";

            PreparedStatement statement = connection.prepareStatement(query);
            statement.setString(1, userId);
            statement.executeUpdate();
        } catch (Exception e) {
            Utilities.reportException(e);
        }
    }
    //---------------------------------------BUDGET---------------------------------------------------------------

    //Create new budget
    public void createBudget(String category, double wishAmount, double paidAmount,
        String date, String userId) {

        try {
            String query = "insert into " + tableBudget + " (category, wish_amount, paid_amount, date, id_user)" +
                " values (?, ?, ?, ?, ?)";
            PreparedStatement statement = connection.prepareStatement(query);
            statement.setString(1, category);
            statement.setDouble(2, wishAmount);
            statement.setDouble(3, paidAmount);
            statement.setString(4, date);
            statement.setString(5, userId);
            statement.executeUpdate();
        } catch(Exception e) {
            Utilities.reportException(e);
        }
    }

    //Get list of all items in budget table
    public List<HashMap> getBudget(String userId) {
        ResultSet resultSet = null;
        List<HashMap> items = new ArrayList<>();
        try {
            String query = "select * from " + tableBudget + " where id_user = ?" ;
            PreparedStatement statement = connection.prepareStatement(query);
            statement.setString(1, userId);
            resultSet = statement.executeQuery();
            while (resultSet.next()) {
                HashMap<String, String> element = new HashMap<>();
                element.put("category", resultSet.getString("category"));
                element.put("wishAmount", Double.toString(resultSet.getDouble("wish_amount")));
                element.put("paidAmount", Double.toString(resultSet.getDouble("paid_amount")));
                element.put("date", resultSet.getString("date"));
                element.put("budgetId", Integer.toString(resultSet.getInt("id_budget")));

                items.add(element);
            }
        } catch(Exception e) {
            Utilities.reportException(e);
        }
        return items;
    }

    public List<HashMap> getSingleBudget(int budgetId) {
        List<HashMap> items = new ArrayList<>();
        ResultSet resultSet = null;
        try {
            String query = "select * from " + tableBudget + " where id_budget = ?";
            PreparedStatement statement = connection.prepareStatement(query);
            statement.setInt(1, budgetId);
            resultSet = statement.executeQuery();
            if (resultSet.next()) {
                HashMap<String, String> element = new HashMap<>();
                element.put("category", resultSet.getString("category"));
                element.put("wishAmount", Double.toString(resultSet.getDouble("wish_amount")));
                element.put("paidAmount", Double.toString(resultSet.getDouble("paid_amount")));
                element.put("date", resultSet.getString("date"));
                element.put("budgetId", Integer.toString(resultSet.getInt("id_budget")));

                items.add(element);
            } else {
                return null;
            }
        } catch(Exception e) {
            Utilities.reportException(e);
        }
        return items;
    }

    //Modify budget
    public void updateBudgetItem(String category, double wishAmount, double paidAmount,
            String date, int budgetId) {

        try {
            String query = "update " + tableBudget + " set " +
        		" category = ?, wish_amount = ?, paid_amount = ?, date = ? where id_budget = ?";

            PreparedStatement statement = connection.prepareStatement(query);
            statement.setString(1, category);
            statement.setDouble(2, wishAmount);
            statement.setDouble(3, paidAmount);
            statement.setString(4, date);
            statement.setInt(5, budgetId);
            statement.executeUpdate();
        } catch(Exception e) {
            Utilities.reportException(e);
        }
    }

    public void deleteBudget(int budgetId) {
        try {
            String query = "delete from  " + tableBudget + " where id_budget = ?";
            PreparedStatement statement = connection.prepareStatement(query);
            statement.setInt(1, budgetId);
            statement.executeUpdate();
        } catch(Exception e) {
            Utilities.reportException(e);
        }
    }

    public boolean isUsersBudget(int budgetId, String userId) {
        ResultSet resultSet = null;
        try {
            String query = "select * from " + tableBudget + " where id_budget = ? and id_user = ?";
            PreparedStatement statement = connection.prepareStatement(query);
            statement.setInt(1, budgetId);
            statement.setString(2, userId);
            resultSet = statement.executeQuery();
            if (resultSet.next())
                return true;
        } catch(Exception e) {
            Utilities.reportException(e);
        }
        return false;
    }


//-----------------------------------------------INCOME--------------------------------------------------------------

    //Create new income
    public void createNewIncome(String source, double amount, String date, String userId) {
        try {
            String query = "insert into " + tableIncome + " (source, amount, date, id_user) " +
                " values (?, ?, ?, ?)";

            PreparedStatement statement = connection.prepareStatement(query);
            statement.setString(1, source);
            statement.setDouble(2, amount);
            statement.setString(3, date);
            statement.setString(4, userId);
            statement.executeUpdate();
        } catch(Exception e) {
            Utilities.reportException(e);
        }
    }

    //Get all incomes from Database
    public List<HashMap> getIncome(String userId) {
        List<HashMap> items = new ArrayList<>();
        ResultSet resultSet = null;
        try {
            String query = "select * from " + tableIncome + " where id_user = ?";
            PreparedStatement statement = connection.prepareStatement(query);
            statement.setString(1, userId);
            resultSet = statement.executeQuery();
            while (resultSet.next()) {
                HashMap<String, String> element = new HashMap<>();
                element.put("incomeId", Integer.toString(resultSet.getInt("id_income")));
                element.put("source", resultSet.getString("source"));
                element.put("amount", Double.toString(resultSet.getDouble("amount")));
                element.put("date", resultSet.getString("date"));

                items.add(element);
            }
        } catch(Exception e) {
            Utilities.reportException(e);
        }
        return items;
    }

    public List<HashMap> getSingleIncome(int incomeId) {
        List<HashMap> items = new ArrayList<>();
        ResultSet resultSet = null;
        try {
            String query = "select * from " + tableIncome + " where id_income = ?";
            PreparedStatement statement = connection.prepareStatement(query);
            statement.setInt(1, incomeId);
            resultSet = statement.executeQuery();
            if (resultSet.next()) {
                HashMap<String, String> element = new HashMap<>();
                element.put("incomeId", Integer.toString(resultSet.getInt("id_income")));
                element.put("source", resultSet.getString("source"));
                element.put("amount", Double.toString(resultSet.getDouble("amount")));
                element.put("date", resultSet.getString("date"));

                items.add(element);
            } else {
                return null;
            }
        } catch(Exception e) {
            Utilities.reportException(e);
        }
        return items;
    }

    //update income table
    public void updateIncome(String source, double amount, String date, int incomeId) {
        try {
            String query =  "update " + tableIncome + " set " +
                            " source = ?, amount = ?, date = ? where id_income = ?";
            PreparedStatement statement = connection.prepareStatement(query);
            statement.setString(1, source);
            statement.setDouble(2, amount);
            statement.setString(3, date);
            statement.setInt(4, incomeId);
            statement.executeUpdate();
        } catch(Exception e) {
            Utilities.reportException(e);
        }
    }

    public void deleteIncome(int incomeId) {
        try {
            String query = "delete from " + tableIncome + " where id_income = ?";
            PreparedStatement statement = connection.prepareStatement(query);
            statement.setInt(1, incomeId);
            statement.executeUpdate();
        } catch(Exception e) {
            Utilities.reportException(e);
        }
    }

    public boolean isUsersIncome(int incomeId, String userId) {
        ResultSet resultSet = null;
        try {
            String query = "select * from " + tableIncome + " where id_income = ? and id_user = ?";
            PreparedStatement statement = connection.prepareStatement(query);
            statement.setInt(1, incomeId);
            statement.setString(2, userId);
            resultSet = statement.executeQuery();
            if (resultSet.next())
                return true;
        } catch(Exception e) {
            Utilities.reportException(e);
        }
        return false;
    }

//-------------------------------------------------Report---------------------------------------------------

    //Get sum(amount) for income and bill (bills are grouped by "group" - paid, payable or overdue)
    public double getTotalAmount(String userId, String paymentType) {
        double amount = 0;
        try {
            String query;
            PreparedStatement statement;
            ResultSet resultSet = null;
            if (paymentType.equals("income")){
                query = "select sum(amount) as amount from " + tableIncome + " where id_user = ?";
                statement = connection.prepareStatement(query);
                statement.setString(1, userId);
                resultSet = statement.executeQuery();
            } else {
                query = "select sum(amount) as amount from " + tableBill + " where payment_type = ? and id_user = ?" +
                    " group by payment_type";
                statement = connection.prepareStatement(query);
                statement.setString(1, paymentType);
                statement.setString(2, userId);
                resultSet = statement.executeQuery();
            }
            while (resultSet.next()) {
        	       amount = resultSet.getDouble("amount");
        	}
        } catch(Exception e) {
            Utilities.reportException(e);
        }
        return amount;
    }

    //Get amount where days_between <= nr of days
    public double getSummary(String userId, String type, int days) {
        double amount = 0;
        try {
            ResultSet resultSet = null;
            String query;
            PreparedStatement statement;
            switch(type){
                case "paid" :
                            if (days == 0)
                                query = "select sum(amount) as amount from " + tableBill +
                                    " where round(julianday('now')) - " +
                                    " round(julianday((substr(date, 7) || \"-\" || substr(date, 4, 2))  || \"-\" || " +
                                    " substr(date, 1, 2))) = ?" +
                                    " and  id_user = ? and payment_type = ?";
                            else
                                query = "select sum(amount) as amount from " + tableBill +
                                    " where round(julianday('now')) - " +
                                    " round(julianday((substr(date, 7) || \"-\" || substr(date, 4, 2))  || \"-\" || " +
                                    " substr(date, 1, 2))) <= ?" +
                                    " and  id_user = ? and payment_type = ?";

                                statement = connection.prepareStatement(query);
                                statement.setInt(1, days);
                                statement.setString(2, userId);
                                statement.setString(3, type);
                                resultSet = statement.executeQuery();
                                break;

                case "payable" :
                                query = "select sum(amount) as amount from " + tableBill +
                                    " where round(julianday('now')) - " +
                                    " round(julianday((substr(date, 7) || \"-\" || substr(date, 4, 2))  || \"-\" || " +
                                    " substr(date, 1, 2))) >=- ?" +
                                    " and  id_user = ? and payment_type = ?";

                                    statement = connection.prepareStatement(query);
                                    statement.setInt(1, days);
                                    statement.setString(2, userId);
                                    statement.setString(3, type);
                                    resultSet = statement.executeQuery();
                                    break;

                case "overdue" :
                                query = "select sum(amount) as amount from " + tableBill +
                                    " where round(julianday('now')) - " +
                                    " round(julianday((substr(date, 7) || \"-\" || substr(date, 4, 2))  || \"-\" || " +
                                    " substr(date, 1, 2))) <= ?" +
                                    " and  id_user = ? and payment_type = ?";

                                    statement = connection.prepareStatement(query);
                                    statement.setInt(1, days);
                                    statement.setString(2, userId);
                                    statement.setString(3, type);
                                    resultSet = statement.executeQuery();
                                    break;

                case "income" :
                                query = "select sum(amount) as amount from " + tableIncome +
                                    " where round(julianday('now')) - " +
                                    " round(julianday((substr(date, 7) || \"-\" || substr(date, 4, 2))  || \"-\" || " +
                                    " substr(date, 1, 2))) <= ? and  id_user = ?";

                                    statement = connection.prepareStatement(query);
                                    statement.setInt(1, days);
                                    statement.setString(2, userId);
                                    resultSet = statement.executeQuery();
                                    break;
            }
            while (resultSet.next()) {
                 amount = Utilities.getTwoDecimal(resultSet.getDouble("amount"));
             }

         } catch(Exception e) {
             System.err.println(e.getClass().getName() + ": " + e.getMessage());
             System.exit(0);
         }

        return amount;
    }

// Get months where we have payments made
    public List<HashMap> periodActivity(String userId) {
        List<HashMap> activity = new ArrayList<>();
        ResultSet resultSet = null;
        try {
            String query = "select distinct " +
                " strftime('%m', julianday(substr(date, 7) || \"-\" || " +
                " substr(date, 4, 2)  || \"-\" || substr(date, 1, 2))) as month, " +
                " strftime('%Y', julianday(substr(date, 7) || \"-\" || " +
                "substr(date, 4, 2)  || \"-\" || substr(date, 1, 2)))" +
                " as year from " + tableBill + " where id_user = ? order by month";

            PreparedStatement statement = connection.prepareStatement(query);
            statement.setString(1, userId);
            resultSet = statement.executeQuery();
            while (resultSet.next()) {
                HashMap<String, String> element = new HashMap<>();
                element.put("month", resultSet.getString("month"));
                element.put("year", resultSet.getString("year"));

                activity.add(element);
            }
        } catch(Exception e) {
            Utilities.reportException(e);
        }
        return activity;
    }

    public List<HashMap> getAmountByCategories (String userId) {
        List<HashMap> categoryAmount = new ArrayList<>();
        ResultSet resultSet = null;
        try {
            String query = "select category, sum(amount) as amount from " + tableBill +
                " where id_user = ? group by category";
            PreparedStatement statement = connection.prepareStatement(query);
            statement.setString(1, userId);
            resultSet = statement.executeQuery();

            while (resultSet.next()) {
                HashMap<String, String> element = new HashMap<>();
                Double dblAmount = Utilities.getTwoDecimal(resultSet.getDouble("amount"));

                element.put("category", resultSet.getString("category"));
                element.put("amount", Double.toString(dblAmount));
                categoryAmount.add(element);
            }

        } catch (Exception e) {
            Utilities.reportException(e);
        }
        return categoryAmount;
    }

    public HashMap getFirstPageDetails (String userId) {
        HashMap<String, String> element = new HashMap<>();
        String countOverdue = Integer.toString(countBill(AppConfig.OVERDUE, userId));
        String countPayable = Integer.toString(countBill(AppConfig.PAYABLE, userId));
        String amount = Double.toString(getAmountForFuturePayments(userId));
        element.put("countPayable", countPayable);
        element.put("countOverdue", countOverdue);
        element.put("amount", amount);
        return element;
    }

    public int countBill (String paymentType, String userId) {
        ResultSet resultSet = null;
        int number = 0;
        try {
            String query = "select count(payment_type) as count from " + tableBill + " where payment_type = '"
            + paymentType + "' and id_user = ? ";

            PreparedStatement statement = connection.prepareStatement(query);
            statement.setString(1, userId);
            resultSet = statement.executeQuery();

            if (resultSet.next()) {
                number = resultSet.getInt("count");
            }
        } catch (Exception e) {
            Utilities.reportException(e);
        }
        return number;
    }

    public double getAmountForFuturePayments (String userId) {
        ResultSet resultSet = null;
        double amount = 0;
        try {
            String query = "select sum(amount) as amount from " + tableBill + " where id_user = ? and (payment_type = '" +
            AppConfig.OVERDUE + "' or payment_type = '" + AppConfig.PAYABLE + "')";

            PreparedStatement statement = connection.prepareStatement(query);
            statement.setString(1, userId);
            resultSet = statement.executeQuery();

            if (resultSet.next()) {
                amount = resultSet.getDouble("amount");
            }
        } catch (Exception e) {
            Utilities.reportException(e);
        }
        return amount;
    }
//------------------------------------------------USER----------------------------------------------------------

    public void createUser (String username, String password, String email) {

        try {
            String query = "insert into " + tableUser + "(username, password, email) values (?, ?, ?)";
            PreparedStatement statement = connection.prepareStatement(query);
            statement.setString(1, username);
            statement.setString(2, password);
            statement.setString(3, email);
            statement.executeUpdate();
        } catch(Exception e) {
            Utilities.reportException(e);
        }
    }

    public String getUserId(String username) {
        try {
            ResultSet resultSet = null;
            String query = "select id_user from " + tableUser + " where username = ?";
            PreparedStatement statement = connection.prepareStatement(query);
            statement.setString(1, username);
            resultSet = statement.executeQuery();
            if (resultSet.next()) {
                return Integer.toString(resultSet.getInt("id_user"));
            }
        } catch(Exception e) {
            Utilities.reportException(e);
        }
        return null;
    }

    public String getLoginDetails(String username) {
        try {
            ResultSet resultSet = null;
            String query = "select password from " + tableUser + " where username = ?";
            PreparedStatement statement = connection.prepareStatement(query);
            statement.setString(1, username);
            resultSet = statement.executeQuery();
            if (resultSet.next()) {
                return resultSet.getString("password");
            }
        } catch(Exception e) {
            Utilities.reportException(e);
        }
        return null;
    }

}
