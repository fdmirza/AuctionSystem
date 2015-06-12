package com.receivers;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Random;

import com.datamodels.Bid;
import com.datamodels.Item;
import com.faizan.auctionsystem.MainActivity;
import com.helper.Constants;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.support.v4.content.LocalBroadcastManager;
/**
 * Receives triggers from AlarmManager
 * @author Faizan
 *
 */
public class MyReceiver extends BroadcastReceiver {

	@Override
	public void onReceive(Context context, Intent intent) {

		ArrayList<Item> items = new ArrayList<Item>();
		try {
			items.addAll(MainActivity.getDbHelper(context).getAllNonBotItems(1));
		} catch (SQLException e) {
			e.printStackTrace();
		}
		if (items.size() == 0)
			return;
		Random rand = new Random();

		int n = rand.nextInt(items.size() - 1);
		int amount = (rand.nextInt(50) + 5);
		Item temp = items.get(n);
		temp.lastBidPrice = temp.lastBidPrice + amount;
		temp.lastBidUser = "Faizan";
		temp.lastBidUserId = 1;
		try {
			MainActivity.getDbHelper(context).getItemDao().update(temp);
			MainActivity.getDbHelper(context).getBidsDao().create(new Bid(temp.lastBidPrice, temp.id, 1, "Faizan"));
		} catch (SQLException e) {
			e.printStackTrace();
		}

		Intent i = new Intent(Constants.ACTION_BOT);
		i.putExtra(Constants.BROADCASTED_ITEM_ID, temp.id);
		LocalBroadcastManager.getInstance(context).sendBroadcast(i);

	}
}