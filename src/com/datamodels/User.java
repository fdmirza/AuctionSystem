package com.datamodels;

import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.table.DatabaseTable;

/**
 * 
 * @author Faizan
 * 
 */
@DatabaseTable(tableName = "users")
public class User {
	@DatabaseField(generatedId = true)
	public int id;
	@DatabaseField
	public String name;
	@DatabaseField(unique = true)
	public String email;
	@DatabaseField
	public String password;
	@DatabaseField
	public String hint;

	public User() {
	}

	public User(String name, String email, String password, String hint) {
		super();
		this.name = name;
		this.email = email;
		this.password = password;
		this.hint = hint;
	}

}
