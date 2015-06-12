package com.datamodels;

import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.table.DatabaseTable;

/**
 * 
 * @author Faizan
 * 
 */

@DatabaseTable(tableName = "bids")
public class Bid {
	@DatabaseField(generatedId = true)
	public int id;
	@DatabaseField
	public double amount;
	@DatabaseField
	public int itemId;
	@DatabaseField
	public int userId;
	@DatabaseField
	public String username;

	public Bid() {
	}

	public Bid(double amount, int itemId, int userId, String username) {
		super();
		this.amount = amount;
		this.itemId = itemId;
		this.userId = userId;
		this.username = username;
	}

}
