package com.faizan.auctionsystem;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ListView;
import android.widget.TextView;

import com.datamodels.Item;
import com.datamodels.User;
import com.helper.AnimationType;
import com.helper.Constants;

/**
 * Contains a list, populate data from db, calls pure virtual method callForDB
 * on child fragments.
 * 
 * @author Faizan
 * 
 */
public abstract class ItemsFragment extends BaseFragment implements OnClickListener, OnItemClickListener {

	private ListView listView;
	private ItemsListAdapter mAdapter;
	private Runnable mRunnable = new Runnable() {

		@Override
		public void run() {
			if (mAdapter != null) {
				mAdapter.notifyDataSetChanged();
			}
			listView.postDelayed(this, 1000);

		}
	};
	private ArrayList<Item> items;

	@Override
	public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
		super.onCreateView(inflater, container, savedInstanceState);
		View view = inflater.inflate(R.layout.fragment_dashboard, null);

		return view;
	}

	/**
	 * Every fragment that extends from it MUST define it and return the result
	 * after querying from database depending on requirment
	 * 
	 * @param context
	 * @param user
	 * @return
	 * @throws SQLException
	 */
	public abstract List<Item> callForDB(Context context, User user) throws SQLException;

	@Override
	public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
		super.onViewCreated(view, savedInstanceState);

		listView = (ListView) view.findViewById(R.id.listView);
		User user = getLoggedInUser(view.getContext());
		items = new ArrayList<Item>();
		try {
			items.addAll(callForDB(view.getContext(), user));
		} catch (SQLException e) {
			e.printStackTrace();
		}

		mAdapter = new ItemsListAdapter(items, view.getContext());
		listView.setAdapter(mAdapter);
		listView.setOnItemClickListener(this);
		listView.postDelayed(mRunnable, 1000);
		TextView tv_add = (TextView) view.findViewById(R.id.tv_add_new_item);
		tv_add.setOnClickListener(this);
	}

	@Override
	public void onClick(View v) {
		int id = v.getId();
		switch (id) {
		case R.id.tv_add_new_item:
			addFragmentWithBackstack(AddNewItemFragment.newInstance(), AnimationType.VERTICLE);
			break;

		default:
			break;
		}

	}

	@Override
	public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
		replaceFragmentWithBackstack(ItemDetailsFragment.newInstance(mAdapter.getItem(position).id));
	}

	@Override
	public void onReceive(Context context, Intent intent) {
		int itemId = intent.getIntExtra(Constants.BROADCASTED_ITEM_ID, -1);
		if (itemId != -1) {
			try {
				Item temp = MainActivity.getDbHelper(context).getItem(itemId);
				for (int i = 0; i < items.size(); i++) {
					if (items.get(i).id == itemId) {
						items.set(i, temp);
						break;
					}

				}

			} catch (SQLException e) {
				e.printStackTrace();
			}

		}

	}

}
