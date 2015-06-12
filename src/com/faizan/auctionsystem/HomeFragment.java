package com.faizan.auctionsystem;

import android.app.AlertDialog;
import android.app.AlertDialog.Builder;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.TextView;

import com.datamodels.User;
import com.helper.Constants;

/**
 * Displays 4 options All Items -> {@link DashboardFragment} My items ->
 * {@link MyItemsFragment} Won Items -> {@link WonItemsFragment} Logout -> just
 * update preferences
 * 
 * @author Faizan
 * 
 */
public class HomeFragment extends BaseFragment implements OnClickListener {

	public static HomeFragment newInstance() {
		return new HomeFragment();
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		super.onCreateView(inflater, container, savedInstanceState);
		View view = inflater.inflate(R.layout.fragment_home, null);
		return view;
	}

	@Override
	public void onViewCreated(View view, Bundle savedInstanceState) {
		super.onViewCreated(view, savedInstanceState);
		User user = getLoggedInUser(view.getContext());
		getMainActivity().setTitle("Welcome " + user.name);
		TextView dashboard = (TextView) view.findViewById(R.id.tv_dashboard);
		dashboard.setOnClickListener(this);

		TextView myItems = (TextView) view.findViewById(R.id.tv_my_items);
		myItems.setOnClickListener(this);

		TextView won = (TextView) view.findViewById(R.id.tv_my_won);
		won.setOnClickListener(this);

		TextView logout = (TextView) view.findViewById(R.id.tv_logout);
		logout.setOnClickListener(this);

	}

	@Override
	public void onClick(View v) {

		int id = v.getId();
		switch (id) {
		case R.id.tv_dashboard:

			replaceFragmentWithBackstack(DashboardFragment.newInstance());
			break;
		case R.id.tv_my_items:
			replaceFragmentWithBackstack(MyItemsFragment.newInstance());
			break;

		case R.id.tv_logout:
			logout(v.getContext());

			break;
		case R.id.tv_my_won:
			replaceFragmentWithBackstack(WonItemsFragment.newInstance());
			break;

		default:
			break;
		}

	}

	/**
	 * for logging out, just Update preference LOGIN_STATUS. see
	 * {@link Constants}
	 * 
	 * @param context
	 */
	private void logout(final Context context) {
		AlertDialog.Builder alert = new Builder(context);
		alert.setTitle("Logout").setMessage("Are you sure you want to logout?").setPositiveButton("Logout", new DialogInterface.OnClickListener() {

			@Override
			public void onClick(DialogInterface dialog, int which) {
				dialog.dismiss();
				SharedPreferences preferences = context.getSharedPreferences(Constants.PREFERNCE, Context.MODE_PRIVATE);
				Editor editor = preferences.edit();
				editor.putBoolean(Constants.LOGIN_STATUS, false);
				editor.commit();
				replaceFragmentWithoutBackstack(LoginFragment.newInstance());

			}
		}).setNegativeButton("Cancel", new DialogInterface.OnClickListener() {

			@Override
			public void onClick(DialogInterface dialog, int which) {
				dialog.dismiss();

			}
		}).create();
		alert.show();

	}

	@Override
	public void onReceive(Context context, Intent intent) {

	}

}
