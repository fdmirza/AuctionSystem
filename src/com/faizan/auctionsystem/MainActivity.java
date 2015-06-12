package com.faizan.auctionsystem;

import java.io.File;
import java.sql.SQLException;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentTransaction;
import android.widget.Toast;

import com.crop.Crop;
import com.datamodels.Item;
import com.datamodels.User;
import com.helper.Constants;
import com.helper.DatabaseHelper;
import com.receivers.MyReceiver;

/**
 * 
 * @author Faizan
 * 
 */
public class MainActivity extends FragmentActivity {

	private static final int ALARM_REQUEST_CODE = 212;
	private static final long INTERVAL = 5 * 60 * 1000;
	static DatabaseHelper dbHelper;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);

		SharedPreferences preferences = getSharedPreferences(Constants.PREFERNCE, Context.MODE_PRIVATE);
		boolean isFirstLaunch = preferences.getBoolean(Constants.IS_FIRST_LAUNCH, true);
		if (isFirstLaunch) {
			try {
				populateDataInDb();

			} catch (SQLException e) {
				e.printStackTrace();
			}
			Editor editor = preferences.edit();
			editor.putBoolean(Constants.IS_FIRST_LAUNCH, false);
			editor.commit();
		}

		if (savedInstanceState == null) {
			initFragment();
		}
		startBot();
	}

	/**
	 * Dummy data
	 * 
	 * @throws SQLException
	 */
	private void populateDataInDb() throws SQLException {
		User user = new User("Faizan", "faizan@faizan.com", "123456", "one to six");
		getDbHelper(getApplicationContext()).getUserDao().create(user);
		user = new User("Phillip Morris", "kk@kk.com", "abcdef", "a to f");
		getDbHelper(getApplicationContext()).getUserDao().create(user);
		Item item = new Item("Nexus 5", "Lollipop OS", System.currentTimeMillis(), System.currentTimeMillis() + 2 * 60 * 60 * 1000, 150, "Faizan", 1,
				false, null, 1);
		getDbHelper(getApplicationContext()).getItemDao().create(item);
		item = new Item("Nexus 6", "Highest Resolution phone", System.currentTimeMillis(), System.currentTimeMillis() + 5 * 45 * 60 * 1000, 180,
				"Phillip Morris", 2, false, null, 2);
		getDbHelper(getApplicationContext()).getItemDao().create(item);
		item = new Item("Nexus 5 New", "Lollipop OS, Brand new", System.currentTimeMillis(), System.currentTimeMillis() + 2 * 60 * 60 * 1000, 150,
				"Phillip Morris", 2, false, null, 2);
		getDbHelper(getApplicationContext()).getItemDao().create(item);
		item = new Item("Nexus 6 New", "Best product", System.currentTimeMillis(), System.currentTimeMillis() + 5 * 45 * 60 * 1000, 180,
				"Phillip Morris", 2, false, null, 2);
		getDbHelper(getApplicationContext()).getItemDao().create(item);
	}

	/**
	 * Checks for user login. If login go to {@link HomeFragment} else go to
	 * {@link LoginFragment}
	 */
	private void initFragment() {

		SharedPreferences preferences = getSharedPreferences(Constants.PREFERNCE, Context.MODE_PRIVATE);
		boolean loginStatus = preferences.getBoolean(Constants.LOGIN_STATUS, false);
		if (loginStatus) {
			replaceFragment(HomeFragment.newInstance());
		} else {
			replaceFragment(LoginFragment.newInstance());
		}

	}

	/**
	 * perform a fragment transaction using replace
	 * 
	 * @param fragment
	 */
	private void replaceFragment(Fragment fragment) {
		FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
		transaction.replace(R.id.fragment_container, fragment);
		transaction.commit();

	}

	/**
	 * Gives the singleton database helper for db work
	 * 
	 * @param context
	 * @return
	 */
	public static DatabaseHelper getDbHelper(Context context) {
		if (dbHelper == null) {
			dbHelper = new DatabaseHelper(context);
		}
		return dbHelper;
	}

	@Override
	public void onActivityResult(int requestCode, int resultCode, Intent result) {
		if (requestCode == Crop.REQUEST_PICK && resultCode == FragmentActivity.RESULT_OK) {
			beginCrop(result.getData());
		} else if (requestCode == Crop.REQUEST_CROP) {
			handleCrop(resultCode, result);
		}
		super.onActivityResult(requestCode, resultCode, result);
	}

	private void beginCrop(Uri source) {

		Uri outputUri = Uri.fromFile(new File(getCacheDir(), "cropped_" + System.currentTimeMillis() + ".jpg"));
		new Crop(source).output(outputUri).withAspect(2, 1).start(this);

	}

	private void handleCrop(int resultCode, Intent result) {
		if (resultCode == FragmentActivity.RESULT_OK) {

			AddNewItemFragment.imageURI = Crop.getOutput(result).toString();

		} else if (resultCode == Crop.RESULT_ERROR) {

			Toast.makeText(getApplicationContext(), "Error in cropping", Toast.LENGTH_SHORT).show();

		}
	}

	/**
	 * Starts AlarmManager in repeating mode, after every INTERVAL.
	 * {@link MyReceiver} will be trigerred.
	 */
	private void startBot() {
		Intent myIntent = new Intent(MainActivity.this, MyReceiver.class);
		PendingIntent pendingIntent = PendingIntent.getBroadcast(MainActivity.this, ALARM_REQUEST_CODE, myIntent, PendingIntent.FLAG_UPDATE_CURRENT);

		AlarmManager alarmManager = (AlarmManager) getSystemService(ALARM_SERVICE);
		alarmManager.setRepeating(AlarmManager.RTC, System.currentTimeMillis() + INTERVAL, INTERVAL, pendingIntent);

	}
}
