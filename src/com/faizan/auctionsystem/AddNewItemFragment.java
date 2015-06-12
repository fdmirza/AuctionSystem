package com.faizan.auctionsystem;

import java.sql.SQLException;
import java.util.Calendar;

import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.crop.Crop;
import com.datamodels.Item;
import com.datamodels.User;
import com.helper.DateTimePicker;
import com.nostra13.universalimageloader.core.ImageLoader;

/**
 * 
 * @author Faizan
 * 
 */
public class AddNewItemFragment extends BaseFragment implements OnClickListener {

	public static String imageURI;
	private ImageView iv_item;
	private EditText et_item_name;
	private EditText et_item_desc;
	private EditText et_item_price;
	private TextView tv_select_time;
	private long selectedTime;

	/**
	 * 
	 * @return instance
	 */
	public static AddNewItemFragment newInstance() {
		return new AddNewItemFragment();
	}

	@Override
	public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
		super.onCreateView(inflater, container, savedInstanceState);
		imageURI = null;
		View view = inflater.inflate(R.layout.fragment_add_new_item, null);

		return view;
	}

	@Override
	public void onResume() {

		super.onResume();
		if (imageURI != null)
			ImageLoader.getInstance().displayImage(imageURI, iv_item);

	}

	@Override
	public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {

		super.onViewCreated(view, savedInstanceState);
		iv_item = (ImageView) view.findViewById(R.id.iv_item);
		iv_item.setOnClickListener(this);
		et_item_name = (EditText) view.findViewById(R.id.et_item_name);
		et_item_desc = (EditText) view.findViewById(R.id.et_item_desc);
		et_item_price = (EditText) view.findViewById(R.id.et_item_price);
		tv_select_time = (TextView) view.findViewById(R.id.tv_select_time);
		TextView tv_submit = (TextView) view.findViewById(R.id.tv_submit);
		tv_submit.setOnClickListener(this);
		tv_select_time.setOnClickListener(this);
	}

	@Override
	public void onClick(View v) {
		int id = v.getId();

		switch (id) {
		case R.id.iv_item:
			Crop.pickImage(getActivity());
			break;
		case R.id.tv_select_time:
			showDateTimeDialog(v.getContext());
			break;

		case R.id.tv_submit:
			String name = et_item_name.getText().toString().trim();
			String desc = et_item_desc.getText().toString().trim();
			String p = et_item_price.getText().toString().trim();
			double price;
			if (p.length() == 0) {
				price = 0;
			} else {
				price = Double.parseDouble(p);
			}
			if (validateInput(v.getContext(), name, desc, price)) {
				User user = getLoggedInUser(v.getContext());
				Item item = new Item(name, desc, System.currentTimeMillis(), selectedTime, price, user.name, user.id, false, imageURI, user.id);
				try {
					MainActivity.getDbHelper(getActivity()).addItem(item);
					Toast.makeText(getActivity(), "Item Created.", Toast.LENGTH_LONG).show();
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
	 * checks for name desc and price and displays errors in Toast
	 * 
	 * @param context
	 * @param name
	 * @param desc
	 * @param price
	 * @return
	 */
	private boolean validateInput(Context context, String name, String desc, double price) {
		if (imageURI == null) {
			Toast.makeText(context, "Please add image", Toast.LENGTH_SHORT).show();
			return false;
		}
		if (name.length() == 0) {
			Toast.makeText(context, "Please set item name", Toast.LENGTH_SHORT).show();
			return false;
		}
		if (desc.length() == 0) {
			Toast.makeText(context, "Please add description", Toast.LENGTH_SHORT).show();
			return false;
		}
		if (price == 0) {
			Toast.makeText(context, "Please set initial price", Toast.LENGTH_SHORT).show();
			return false;
		}
		if (selectedTime <= System.currentTimeMillis()) {
			Toast.makeText(context, "Time is in past.", Toast.LENGTH_SHORT).show();
			return false;
		}
		return true;
	}

	@Override
	public void onReceive(Context context, Intent intent) {

	}

	/**
	 * Selecting date and time
	 * 
	 * @param context
	 */
	private void showDateTimeDialog(Context context) {
		final Dialog mDateTimeDialog = new Dialog(context);
		final RelativeLayout mDateTimeDialogView = (RelativeLayout) ((LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE))
				.inflate(R.layout.date_time_dialog, null);
		final DateTimePicker mDateTimePicker = (DateTimePicker) mDateTimeDialogView.findViewById(R.id.DateTimePicker);
		final String timeS = android.provider.Settings.System.getString(context.getContentResolver(), android.provider.Settings.System.TIME_12_24);
		final boolean is24h = !(timeS == null || timeS.equals("12"));

		((Button) mDateTimeDialogView.findViewById(R.id.SetDateTime)).setOnClickListener(new OnClickListener() {

			public void onClick(View v) {
				mDateTimePicker.clearFocus();
				long millis = mDateTimePicker.getDateTimeMillis();
				if (millis < System.currentTimeMillis() + 10) {

					Toast.makeText(v.getContext(), "Time is in past", Toast.LENGTH_SHORT).show();
					mDateTimeDialog.dismiss();
					return;
				}

				String dateTime = mDateTimePicker.get(Calendar.YEAR) + "/" + (mDateTimePicker.get(Calendar.MONTH) + 1) + "/"
						+ mDateTimePicker.get(Calendar.DAY_OF_MONTH);

				if (mDateTimePicker.is24HourView()) {
					dateTime = dateTime + " - " + mDateTimePicker.get(Calendar.HOUR_OF_DAY) + ":" + mDateTimePicker.get(Calendar.MINUTE);

				} else {
					dateTime = dateTime + " - " + mDateTimePicker.get(Calendar.HOUR) + ":" + mDateTimePicker.get(Calendar.MINUTE) + " "
							+ (mDateTimePicker.get(Calendar.AM_PM) == Calendar.AM ? "AM" : "PM");
				}
				selectedTime = millis;
				(tv_select_time).setText(dateTime);
				mDateTimeDialog.dismiss();
			}
		});

		((Button) mDateTimeDialogView.findViewById(R.id.CancelDialog)).setOnClickListener(new OnClickListener() {

			public void onClick(View v) {
				mDateTimeDialog.cancel();
			}
		});

		((Button) mDateTimeDialogView.findViewById(R.id.ResetDateTime)).setOnClickListener(new OnClickListener() {

			public void onClick(View v) {
				mDateTimePicker.reset();
			}
		});

		mDateTimePicker.setIs24HourView(is24h);
		mDateTimeDialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
		mDateTimeDialog.setContentView(mDateTimeDialogView);
		mDateTimeDialog.show();
	}

}
