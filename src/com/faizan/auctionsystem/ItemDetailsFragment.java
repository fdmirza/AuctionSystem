package com.faizan.auctionsystem;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;

import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.content.LocalBroadcastManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.datamodels.Bid;
import com.datamodels.Item;
import com.datamodels.User;
import com.helper.AnimationType;
import com.helper.Constants;
import com.nostra13.universalimageloader.core.ImageLoader;

/**
 * Displays the detials of an item. Also give option to Bid on it.
 * 
 * @author Faizan
 * 
 */
public class ItemDetailsFragment extends BaseFragment implements OnClickListener {

	private static final String ITEM_ID = "_item_id";
	Item item = null;
	private TextView tvCurrentValue;
	private LinearLayout llHistoryContainer;

	public static ItemDetailsFragment newInstance(int id) {
		ItemDetailsFragment fragment = new ItemDetailsFragment();
		Bundle args = new Bundle();
		args.putInt(ITEM_ID, id);
		fragment.setArguments(args);
		return fragment;
	}

	@Override
	public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
		super.onCreateView(inflater, container, savedInstanceState);
		View view = inflater.inflate(R.layout.fragment_item_details, null);

		return view;
	}

	@Override
	public void onCreate(Bundle savedInstanceState) {
		LocalBroadcastManager.getInstance(getActivity()).registerReceiver(broadcastReceiver, new IntentFilter(Constants.ACTION_BOT));
		super.onCreate(savedInstanceState);
	}

	@Override
	public void onDestroy() {
		super.onDestroy();
		LocalBroadcastManager.getInstance(getActivity()).unregisterReceiver(broadcastReceiver);
	}

	@Override
	public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
		super.onViewCreated(view, savedInstanceState);
		int id = getArguments().getInt(ITEM_ID);

		try {
			item = MainActivity.getDbHelper(view.getContext()).getItem(id);
		} catch (SQLException e) {
			e.printStackTrace();
		}

		if (item != null) {
			if (item.endTime < System.currentTimeMillis())
				item.isSold = true;
			TextView tvItemName = (TextView) view.findViewById(R.id.tv_item_name);
			tvItemName.setText(item.name);
			TextView tvItemDesc = (TextView) view.findViewById(R.id.tv_item_description);
			tvItemDesc.setText(item.description);
			tvCurrentValue = (TextView) view.findViewById(R.id.tv_item_value);
			tvCurrentValue.setText("$" + String.format("%.2f", item.lastBidPrice));
			TextView tvMakeBid = (TextView) view.findViewById(R.id.tv_make_bid);
			if (item.isSold) {
				ImageView ivSold = (ImageView) view.findViewById(R.id.iv_sold);
				ivSold.setVisibility(View.VISIBLE);
				tvMakeBid.setVisibility(View.GONE);
			} else {
				tvMakeBid.setOnClickListener(this);
			}
			llHistoryContainer = (LinearLayout) view.findViewById(R.id.ll_history_container);
			populateHistory(view.getContext(), llHistoryContainer, item);
			ImageView ivItem = (ImageView) view.findViewById(R.id.iv_item);
			if (item.imageUri == null) {
				if (id % 2 != 0)
					ivItem.setImageResource(R.drawable.nexus_one);
				else
					ivItem.setImageResource(R.drawable.nexus_two);
			} else {
				ImageLoader.getInstance().displayImage(item.imageUri, ivItem);
			}
		}

	}

	/**
	 * fetch all bids on this item and add in UI row of LinearLayout
	 * 
	 * @param context
	 * @param llHistoryContainer
	 * @param item
	 */
	private void populateHistory(Context context, LinearLayout llHistoryContainer, Item item) {

		LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		llHistoryContainer.removeAllViews();
		ArrayList<Bid> bids = new ArrayList<Bid>();
		try {
			bids.addAll(MainActivity.getDbHelper(context).getAllBidsByItemId(item.id));
		} catch (SQLException e) {
			e.printStackTrace();
		}
		if (bids.size() == 0) {
			View child = inflater.inflate(R.layout.row_no_data, null);
			llHistoryContainer.addView(child);

		} else
			Collections.reverse(bids);
		for (Iterator<Bid> iterator = bids.iterator(); iterator.hasNext();) {
			Bid bid = (Bid) iterator.next();
			View child = inflater.inflate(R.layout.row_history, null);
			((TextView) child.findViewById(R.id.tv_user_name)).setText(bid.username);
			((TextView) child.findViewById(R.id.tv_bid_value)).setText("$" + String.format("%.2f", bid.amount));
			llHistoryContainer.addView(child);

		}

	}

	@Override
	public void onClick(View v) {
		int id = v.getId();
		switch (id) {
		case R.id.tv_make_bid:
			User user = getLoggedInUser(v.getContext());
			if (user.id == item.lastBidUserId) {
				Toast.makeText(v.getContext(), "You are currently winning this item.", Toast.LENGTH_LONG).show();
				return;
			}
			addFragmentWithBackstack(MakeBidFragment.newInstance(item.id), AnimationType.HORIZONTAL);
			break;

		default:
			break;
		}

	}

	@Override
	public void onReceive(Context context, Intent intent) {
		int itemId = intent.getIntExtra(Constants.BROADCASTED_ITEM_ID, -1);
		if (itemId != -1) {
			try {
				Item temp = MainActivity.getDbHelper(context).getItem(itemId);
				if (item.id == itemId) {
					tvCurrentValue.setText("$" + String.format("%.2f", temp.lastBidPrice));
					populateHistory(context, llHistoryContainer, temp);
				}

			} catch (SQLException e) {
				e.printStackTrace();
			}

		}

	}

}
