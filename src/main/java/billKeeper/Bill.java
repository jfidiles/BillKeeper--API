package billKeeper;

import java.sql.*;

public class Bill {
	private String title, date, notes, category, paymentType;
	private String userId;
	private double amount;

	public Bill() {}

	public Bill(String paymentType, String title, double amount, String date, String category,
        String notes, String userId) {

		this.paymentType = paymentType;
		this.title = title;
		this.amount = amount;
		this.date = date;
		this.category = category;
		this.notes = notes;
		this.userId = userId;
	}

	public String getPaymentType() {
		return paymentType;
	}

	public String getTitle() {
		return title;
	}

	public String getDate() {
		return date;
	}

	public String getNotes() {
		return notes;
	}

	public String getCategory() {
		return category;
	}

	public double getAmount() {
		return amount;
	}

	public String getUserId() {
		return userId;
	}

	public void setPaymentType(String paymentType) {
		this.paymentType = paymentType;
	}

	public void setTitle(String title) {
		this.title = title;
	}

	public void setDate(String date) {
		this.date = date;
	}

	public void setNotes(String notes) {
		this.notes = notes;
	}

	public void setCategory(String category) {
		this.category = category;
	}

	public void setAmount(double amount) {
		this.amount = amount;
	}

	public void setUserId(String userId) {
		this.userId = userId;
	}

}
