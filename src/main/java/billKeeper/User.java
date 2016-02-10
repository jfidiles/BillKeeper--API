package billKeeper;

import java.sql.*;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.beans.factory.annotation.Autowired;

public class User{
	 @Autowired
	private  String username, password, email;

	public User() {}

	public User(String username, String password, String email) {
		this.username = username;
		this.password = password;
		this.email = email;
	}

	public String getUsername() {
		return username;
	}

	public String getPassword() {
		return password;
	}

	public String getEmail() {
		return email;
	}

	public void setUsername(String username) {
		this.username = username;
	}

	public void setPassword(String password) {
		this.password = password;
	}

	public void setEmail(String email) {
		this.email = email;
	}
}
