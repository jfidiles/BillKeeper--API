package billKeeper;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

public class Budget {
	private String category, date, userId;
	private int wishAmount, paidAmount;

	public Budget() {}

	public Budget(String category, int wishAmount, int paidAmount, String userId, String date) {
		this.category = category;
		this.wishAmount = wishAmount;
		this.paidAmount = paidAmount;
		this.userId = userId;
		this.date = date;
	}

	public String getCategory() {
		return category;
	}

	public int getWishAmount() {
		return wishAmount;
	}

	public int getPaidAmount() {
		return paidAmount;
	}

	public String getUserId() {
		return userId;
	}

	public String getDate() {
		return date;
	}

	public void setCategory(String category) {
		this.category = category;
	}

	public void setWishAmount(int wishAmount) {
		this.wishAmount = wishAmount;
	}

	public void setPaidAmount(int paidAmount) {
		this.paidAmount = paidAmount;
	}

	public void setUserId(String userId) {
		this.userId = userId;
	}

	public void setDate(String date) {
		this.date= date;
	}


}
