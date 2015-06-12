package com.faizan.auctionsystem;

import android.content.Context;
import android.content.SharedPreferences;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;

import com.datamodels.User;
import com.google.gson.GsonBuilder;
import com.helper.AnimationType;
import com.helper.Constants;
import com.receivers.IBroadcastReceiver;
import com.receivers.InternalBroadcastReceiver;

/**
 * Base for all the Fragments Provide some useful methods of performing fragment
 * transaction and managing backstack
 * 
 * @author Faizan
 * 
 */
public abstract class BaseFragment extends Fragment implements IBroadcastReceiver {

	InternalBroadcastReceiver broadcastReceiver = new InternalBroadcastReceiver(this);

	void replaceFragmentWithBackstack(Fragment fragment) {
		FragmentTransaction transaction = getActivity().getSupportFragmentManager().beginTransaction();
		transaction.replace(R.id.fragment_container, fragment);
		transaction.addToBackStack(null);
		transaction.commit();
	}

	void replaceFragmentWithoutBackstack(Fragment fragment) {
		FragmentTransaction transaction = getActivity().getSupportFragmentManager().beginTransaction();
		transaction.replace(R.id.fragment_container, fragment);
		transaction.commit();
	}

	void addFragmentWithBackstack(Fragment fragment, int animationType) {
		FragmentTransaction transaction = getActivity().getSupportFragmentManager().beginTransaction();
		transaction.replace(R.id.fragment_container, fragment);

		if (animationType == AnimationType.HORIZONTAL) {
			transaction.setCustomAnimations(R.anim.push_right_in, R.anim.push_right_out, R.anim.push_left_in, R.anim.push_left_out);
		} else if (animationType == AnimationType.VERTICLE) {
			transaction.setCustomAnimations(R.anim.slide_in_up, R.anim.scale_down_half, R.anim.scale_up, R.anim.slide_out_down);
		} else if (animationType == AnimationType.FLIP) {

		} else if (animationType == AnimationType.FADE) {
			transaction.setCustomAnimations(android.R.anim.fade_in, android.R.anim.fade_out, R.anim.push_left_in, android.R.anim.fade_out);

		} else if (animationType == AnimationType.NONE) {

		}

		transaction.addToBackStack(null);
		transaction.commit();
	}

	public MainActivity getMainActivity() {
		if (getActivity() != null) {
			return (MainActivity) getActivity();
		} else
			return null;
	}

	User getLoggedInUser(Context context) {
		SharedPreferences preferences = context.getSharedPreferences(Constants.PREFERNCE, Context.MODE_PRIVATE);

		return new GsonBuilder().create().fromJson(preferences.getString(Constants.LOGGED_IN_USER, ""), User.class);

	}

}
