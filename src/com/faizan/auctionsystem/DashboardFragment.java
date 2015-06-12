package com.faizan.auctionsystem;

import java.sql.SQLException;
import java.util.List;

import android.content.Context;
import android.view.View.OnClickListener;
import android.widget.AdapterView.OnItemClickListener;

import com.datamodels.Item;
import com.datamodels.User;

/**
 * Displays All items
 * 
 * @author Faizan
 * 
 */
public class DashboardFragment extends ItemsFragment implements OnClickListener, OnItemClickListener {

	public static DashboardFragment newInstance() {
		return new DashboardFragment();
	}

	@Override
	public List<Item> callForDB(Context context, User user) throws SQLException {
		return MainActivity.getDbHelper(context).getAllItems();
	}

}
