package com.datamodels;

import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.table.DatabaseTable;

/**
 * 
 * @author Faizan
 * 
 */
@DatabaseTable(tableName = "items")
public class Item {

	@DatabaseField(generatedId = true)
	public int id;
	@DatabaseField
	public String name;
	@DatabaseField
	public String description;
	@DatabaseField
	public long endTime;
	@DatabaseField
	public long createdTime;
	@DatabaseField
	public double lastBidPrice;
	@DatabaseField
	public String lastBidUser;
	@DatabaseField
	public int lastBidUserId;
	@DatabaseField
	public boolean isSold;
	@DatabaseField
	public String imageUri;
	@DatabaseField
	int ownerId;

	public Item() {
	}

	public Item(String name, String description, long createdTime, long endTime, double lastBidPrice, String lastBidUser, int lastBidUserId,
			boolean isSold, String imageUri, int ownerId) {
		super();
		this.name = name;
		this.description = description;
		this.endTime = endTime;
		this.createdTime = createdTime;
		this.lastBidPrice = lastBidPrice;
		this.lastBidUser = lastBidUser;
		this.lastBidUserId = lastBidUserId;
		this.isSold = isSold;
		this.imageUri = imageUri;
		this.ownerId = ownerId;
	}

}
