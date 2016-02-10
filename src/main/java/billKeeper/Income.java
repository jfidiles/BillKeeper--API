package billKeeper;

import java.sql.*;

public class Income {
    private String source, date;
	private double amount;
	private String userId;

	public Income() {}

	public Income(String source, double amount, String date, String userId) {
		this.source = source;
		this.amount = amount;
		this.date = date;
		this.userId = userId;
	}

	public String getSource() {
		return source;
	}

	public double getAmount() {
		return amount;
	}

	public String getDate() {
		return date;
	}

	public String getUserId() {
		return userId;
	}

	public void setSource(String source) {
		this.source = source;
	}

	public void setAmount(double amount) {
		this.amount = amount;
	}

	public void setDate(String date) {
		this.date = date;
	}

	public void setUserId(String userId) {
		this.userId = userId;
	}

}
