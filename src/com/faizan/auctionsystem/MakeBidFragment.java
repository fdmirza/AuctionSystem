package com.faizan.auctionsystem;

import java.sql.SQLException;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.datamodels.Bid;
import com.datamodels.Item;
import com.datamodels.User;
import com.helper.Constants;
import com.nostra13.universalimageloader.core.ImageLoader;

/**
 * For bidding on an item
 * 
 * @author Faizan
 * 
 */
public class MakeBidFragment extends BaseFragment implements OnClickListener {

	private static final String ITEM_ID = "_item_id";
	private ImageView iv_item;
	private EditText et_item_price;
	Item item = null;
	private TextView tvCurrentVal;

	public static MakeBidFragment newInstance(int id) {
		MakeBidFragment fragment = new MakeBidFragment();
		Bundle args = new Bundle();
		args.putInt(ITEM_ID, id);
		fragment.setArguments(args);
		return fragment;
	}

	@Override
	public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
		super.onCreateView(inflater, container, savedInstanceState);
		View view = inflater.inflate(R.layout.fragment_make_bid, null);

		return view;
	}

	@Override
	public void onResume() {

		super.onResume();

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
		iv_item = (ImageView) view.findViewById(R.id.iv_item);
		if (item.imageUri == null) {
			// For test inputs, monkey inputs from code
			if (id % 2 != 0)
				iv_item.setImageResource(R.drawable.nexus_one);
			else
				iv_item.setImageResource(R.drawable.nexus_two);
		} else {
			// real
			ImageLoader.getInstance().displayImage(item.imageUri, iv_item);
		}
		tvCurrentVal = (TextView) view.findViewById(R.id.tv_item_value);
		tvCurrentVal.setText("$" + String.format("%.2f", item.lastBidPrice));
		et_item_price = (EditText) view.findViewById(R.id.et_item_price);
		TextView tv_submit = (TextView) view.findViewById(R.id.tv_submit);
		tv_submit.setOnClickListener(this);
	}

	@Override
	public void onClick(View v) {
		int id = v.getId();

		switch (id) {
		case R.id.tv_submit:
			String price = et_item_price.getText().toString().trim();
			if (validateInput(v.getContext(), price)) {
				double temp = Double.parseDouble(price);
				User user = getLoggedInUser(v.getContext());
				item.lastBidPrice = temp;
				item.lastBidUser = user.name;
				item.lastBidUserId = user.id;
				try {
					MainActivity.getDbHelper(v.getContext()).getItemDao().update(item);
				} catch (SQLException e) {
					e.printStackTrace();
				}

				Bid bid = new Bid(temp, item.id, user.id, user.name);
				try {
					MainActivity.getDbHelper(v.getContext()).getBidsDao().create(bid);
				} catch (SQLException e) {
					e.printStackTrace();
				}

				getActivity().onBackPressed();

			}
			break;

		default:
			break;
		}

	}

	/**
	 * Validates the data, if error show message in toast.
	 * 
	 * @param context
	 * @param price
	 * @return
	 */
	private boolean validateInput(Context context, String price) {

		if (price.length() == .0) {
			Toast.makeText(context, "Please input bid amount.", Toast.LENGTH_SHORT).show();
			return false;
		}
		double temp = Double.parseDouble(price);
		if (temp <= item.lastBidPrice) {
			Toast.makeText(context, "Your amount is less than current bid.", Toast.LENGTH_SHORT).show();
			return false;
		}
		if (item.endTime <= System.currentTimeMillis()) {
			Toast.makeText(context, "Sorry, bidding time over.", Toast.LENGTH_LONG).show();
			return false;
		}

		return true;
	}

	@Override
	public void onReceive(Context context, Intent intent) {
		int itemId = intent.getIntExtra(Constants.BROADCASTED_ITEM_ID, -1);
		if (itemId != -1) {
			try {

				if (item.id == itemId) {
					item = MainActivity.getDbHelper(context).getItem(itemId);
					tvCurrentVal.setText("$" + String.format("%.2f", item.lastBidPrice));
				}

			} catch (SQLException e) {
				e.printStackTrace();
			}

		}

	}

}
