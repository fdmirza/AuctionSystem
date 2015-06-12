package com.faizan.auctionsystem;

import java.sql.SQLException;
import java.util.List;

import android.content.Context;
import android.view.View.OnClickListener;
import android.widget.AdapterView.OnItemClickListener;

import com.datamodels.Item;
import com.datamodels.User;

/**
 * displays all loggedIn user's items
 * 
 * @author Faizan
 * 
 */
public class MyItemsFragment extends ItemsFragment implements OnClickListener, OnItemClickListener {

	public static MyItemsFragment newInstance() {
		return new MyItemsFragment();
	}

	@Override
	public List<Item> callForDB(Context context, User user) throws SQLException {
		return MainActivity.getDbHelper(context).getMyItems(user.id);
	}

}
