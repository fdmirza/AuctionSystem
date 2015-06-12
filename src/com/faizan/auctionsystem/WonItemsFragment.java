package com.faizan.auctionsystem;

import java.sql.SQLException;
import java.util.List;

import android.content.Context;
import android.view.View.OnClickListener;
import android.widget.AdapterView.OnItemClickListener;

import com.datamodels.Item;
import com.datamodels.User;

/**
 * Displays all items WON by loggedIn user.
 * 
 * @author Faizan
 * 
 */
public class WonItemsFragment extends ItemsFragment implements OnClickListener, OnItemClickListener {

	public static WonItemsFragment newInstance() {
		return new WonItemsFragment();
	}

	@Override
	public List<Item> callForDB(Context context, User user) throws SQLException {
		return MainActivity.getDbHelper(context).getWonItems(user.id);
	}

}
