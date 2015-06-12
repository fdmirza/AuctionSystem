package com.helper;

import java.sql.SQLException;
import java.util.List;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import com.datamodels.Bid;
import com.datamodels.Item;
import com.datamodels.User;
import com.j256.ormlite.android.AndroidConnectionSource;
import com.j256.ormlite.android.AndroidDatabaseConnection;
import com.j256.ormlite.android.DatabaseTableConfigUtil;
import com.j256.ormlite.dao.Dao;
import com.j256.ormlite.dao.DaoManager;
import com.j256.ormlite.stmt.QueryBuilder;
import com.j256.ormlite.support.DatabaseConnection;
import com.j256.ormlite.table.DatabaseTableConfig;
import com.j256.ormlite.table.TableUtils;

/**
 * Database helper class used to manage the creation and upgrading of your
 * database. This class also usually provides the DAOs used by the other
 * classes.
 * 
 * @author Faizan
 * 
 */
public class DatabaseHelper extends SQLiteOpenHelper {

	private AndroidConnectionSource connectionSource = new AndroidConnectionSource(this);

	private static final String DATABASE_NAME = "mauction.sqlite";
	private static final int DATABASE_VERSION = 1;

	private Dao<User, String> userDao = null;
	private Dao<Item, String> itemDao = null;
	private Dao<Bid, String> bidsDao = null;

	public DatabaseHelper(Context context) {
		super(context, DATABASE_NAME, null, DATABASE_VERSION);
	}

	@Override
	public void onCreate(SQLiteDatabase db) {
		DatabaseConnection conn = getConnectionSource().getSpecialConnection();
		boolean clearSpecial = false;
		if (conn == null) {
			conn = new AndroidDatabaseConnection(db, true);
			try {
				getConnectionSource().saveSpecialConnection(conn);
				clearSpecial = true;
			} catch (SQLException e) {
				throw new IllegalStateException("Could not save special connection", e);
			}
		}
		try {
			onCreate();
		} finally {
			if (clearSpecial) {
				getConnectionSource().clearSpecialConnection(conn);
			}
		}
	}

	public AndroidConnectionSource getDatabaseConnection() {
		return getConnectionSource();
	}

	@Override
	public final void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
		DatabaseConnection conn = getConnectionSource().getSpecialConnection();
		boolean clearSpecial = false;
		if (conn == null) {
			conn = new AndroidDatabaseConnection(db, true);
			try {
				getConnectionSource().saveSpecialConnection(conn);
				clearSpecial = true;
			} catch (SQLException e) {
				throw new IllegalStateException("Could not save special connection", e);
			}
		}
		try {
			onUpgrade(oldVersion, newVersion);
		} finally {
			if (clearSpecial) {
				getConnectionSource().clearSpecialConnection(conn);
			}
		}
	}

	/**
	 * Close the database connections and clear any cached DAOs.
	 */
	@Override
	public void close() {
		super.close();
	}

	/**
	 * Returns the Database Access Object (DAO) for our ChatMessage class. It
	 * will create it or just give the cached value.
	 */

	public Dao<User, String> getUserDao() throws SQLException {
		if (userDao == null) {
			userDao = getDao(User.class);
		}
		return userDao;
	}

	public Dao<Item, String> getItemDao() throws SQLException {
		if (itemDao == null) {
			itemDao = getDao(Item.class);
		}
		return itemDao;
	}

	public Dao<Bid, String> getBidsDao() throws SQLException {
		if (bidsDao == null) {
			bidsDao = getDao(Bid.class);
		}
		return bidsDao;
	}

	private void onCreate() {
		try {
			Log.i(DatabaseHelper.class.getName(), "onCreate");

			TableUtils.createTable(getConnectionSource(), User.class);
			TableUtils.createTable(getConnectionSource(), Item.class);
			TableUtils.createTable(getConnectionSource(), Bid.class);

		} catch (Exception e) {
			Log.e(DatabaseHelper.class.getName(), "Can't create database", e);
			throw new RuntimeException(e);
		}
	}

	/**
	 * This is called when your application is upgraded and it has a higher
	 * version number. This allows you to adjust the various response to match
	 * the new version number.
	 */
	private void onUpgrade(int oldVersion, int newVersion) {

		Log.i(DatabaseHelper.class.getName(), "onUpgrade");
		try {

			TableUtils.dropTable(getConnectionSource(), User.class, true);

			// TableUtils.dropTable(getConnectionSource(),
			// ContactSyncData.class, true);

			onCreate();
		} catch (Exception e) {
			Log.e(DatabaseHelper.class.getName(), "Can't drop databases", e);
			throw new RuntimeException(e);
		}
	}

	private <D extends Dao<T, ?>, T> D getDao(Class<T> clazz) throws SQLException {
		// lookup the dao, possibly invoking the cached database config
		Dao<T, ?> dao = DaoManager.lookupDao(getConnectionSource(), clazz);
		if (dao == null) {
			// try to use our new reflection magic
			DatabaseTableConfig<T> tableConfig = DatabaseTableConfigUtil.fromClass(getConnectionSource(), clazz);
			if (tableConfig == null) {
				dao = (Dao<T, ?>) DaoManager.createDao(getConnectionSource(), clazz);
			} else {
				dao = (Dao<T, ?>) DaoManager.createDao(getConnectionSource(), tableConfig);
			}
		}

		@SuppressWarnings("unchecked")
		D castDao = (D) dao;
		return castDao;
	}

	public void clearData() throws SQLException {
		TableUtils.clearTable(getConnectionSource(), User.class);
		TableUtils.clearTable(getConnectionSource(), Item.class);
		TableUtils.clearTable(getConnectionSource(), Bid.class);

	}

	public AndroidConnectionSource getConnectionSource() {
		return connectionSource;
	}

	public void setConnectionSource(AndroidConnectionSource connectionSource) {
		this.connectionSource = connectionSource;
	}

	/**
	 * Check user credentials and return {@link ResultCodes} for cases if user
	 * not exist. If user exists but password not matches, it will return
	 * password hint
	 * 
	 * @param email
	 * @param password
	 * @return
	 * @throws SQLException
	 */
	public String checkUserCredentials(String email, String password) throws SQLException {
		QueryBuilder<User, String> qb = getUserDao().queryBuilder();
		qb.where().eq("email", email);
		User user = qb.queryForFirst();
		if (user == null)
			return ResultCodes.USER_NOT_EXISTS;
		else if (user != null && !user.password.equals(password))
			return user.hint + "";
		else
			return ResultCodes.USER_LOGIN_SUCCESS;

	}

	/**
	 * returns true if email already exists in database
	 * 
	 * @param email
	 * @return
	 * @throws SQLException
	 */
	public boolean hasEmailRegistered(String email) throws SQLException {
		QueryBuilder<User, String> qb = getUserDao().queryBuilder();
		qb.where().eq("email", email);
		User user = qb.queryForFirst();
		if (user == null)
			return false;
		else
			return true;
	}

	/**
	 * Returns all the items added
	 * 
	 * @return
	 * @throws SQLException
	 */
	public List<Item> getAllItems() throws SQLException {
		QueryBuilder<Item, String> qb = getItemDao().queryBuilder();

		return qb.query();

	}

	/**
	 * return items only where the given userId equals ownerId
	 * 
	 * @param userId
	 * @return
	 * @throws SQLException
	 */

	public List<Item> getMyItems(int userId) throws SQLException {
		QueryBuilder<Item, String> qb = getItemDao().queryBuilder();
		qb.where().eq("ownerId", userId);
		qb.orderBy("endTime", true);

		return qb.query();

	}

	/**
	 * returns Won items for particular userId by checking lastBidUserId of item
	 * equals userId and endTime has expired
	 * 
	 * @param userId
	 * @return
	 * @throws SQLException
	 */
	public List<Item> getWonItems(int userId) throws SQLException {
		QueryBuilder<Item, String> qb = getItemDao().queryBuilder();
		qb.where().eq("lastBidUserId", userId).and().le("endTime", System.currentTimeMillis());

		return qb.query();

	}

	/**
	 * To be used by Bot, it will return all non bot items and open for Bidding
	 * 
	 * @param userId
	 * @return
	 * @throws SQLException
	 */
	public List<Item> getAllNonBotItems(int userId) throws SQLException {
		QueryBuilder<Item, String> qb = getItemDao().queryBuilder();
		qb.where().ne("ownerId", userId).and().ge("endTime", System.currentTimeMillis());

		return qb.query();

	}

	/**
	 * get user object for particular email
	 * 
	 * @param email
	 * @return
	 * @throws SQLException
	 */
	public User getUser(String email) throws SQLException {
		QueryBuilder<User, String> qb = getUserDao().queryBuilder();
		qb.where().eq("email", email);
		return qb.queryForFirst();

	}

	/**
	 * adds a row
	 * 
	 * @param item
	 * @throws SQLException
	 */

	public void addItem(Item item) throws SQLException {
		getItemDao().create(item);

	}

	/**
	 * get item for a particular id
	 * 
	 * @param id
	 * @return
	 * @throws SQLException
	 */
	public Item getItem(int id) throws SQLException {
		QueryBuilder<Item, String> qb = getItemDao().queryBuilder();
		qb.where().eq("id", id);

		return qb.queryForFirst();
	}

	/**
	 * returns all the Bids on a particular item by id
	 * 
	 * @param id
	 * @return
	 * @throws SQLException
	 */
	public List<Bid> getAllBidsByItemId(int id) throws SQLException {
		QueryBuilder<Bid, String> qb = getBidsDao().queryBuilder();
		qb.where().eq("itemId", id);
		return qb.query();
	}

}
