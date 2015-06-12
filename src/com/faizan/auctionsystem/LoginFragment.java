package com.faizan.auctionsystem;

import java.sql.SQLException;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
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

import com.datamodels.User;
import com.google.gson.GsonBuilder;
import com.helper.Constants;
import com.helper.ResultCodes;

/**
 * 
 * @author Faizan
 * 
 */
public class LoginFragment extends BaseFragment implements OnClickListener {

	public static LoginFragment newInstance() {
		return new LoginFragment();
	}

	private EditText et_password;
	private EditText et_email;
	private TextView tv_hint;
	private ImageView ivIcon;

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		super.onCreateView(inflater, container, savedInstanceState);
		View view = inflater.inflate(R.layout.fragment_login, container, false);

		return view;
	}

	@Override
	public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
		super.onViewCreated(view, savedInstanceState);
		TextView tv_login = (TextView) view.findViewById(R.id.tv_login);
		TextView tv_register = (TextView) view.findViewById(R.id.tv_register);
		tv_hint = (TextView) view.findViewById(R.id.tv_pasword_hint);
		et_email = (EditText) view.findViewById(R.id.et_email);
		et_password = (EditText) view.findViewById(R.id.et_password);
		ivIcon = (ImageView) view.findViewById(R.id.ivIcon);
		ivIcon.postDelayed(new Runnable() {

			@Override
			public void run() {
				ivIcon.setVisibility(View.VISIBLE);

			}
		}, 500);
		tv_login.setOnClickListener(this);
		tv_register.setOnClickListener(this);
		if (getMainActivity() != null) {
			getMainActivity().setTitle("Login");
		}
	}

	@Override
	public void onClick(View v) {
		int id = v.getId();
		switch (id) {
		case R.id.tv_login:
			String email = et_email.getText().toString().trim();
			String password = et_password.getText().toString().trim();
			if (validateInput(v.getContext(), email, password)) {
				String result = null;
				try {
					result = MainActivity.getDbHelper(v.getContext()).checkUserCredentials(email, password);
				} catch (SQLException e) {
					e.printStackTrace();
				}
				if (CheckUserCredentials(v.getContext(), result)) {
					saveUser(v.getContext(), email);
					replaceFragmentWithoutBackstack(HomeFragment.newInstance());
				}
			}

			break;
		case R.id.tv_register:
			replaceFragmentWithBackstack(RegisterFragment.newInstance());
			break;

		default:
			break;
		}

	}

	private void saveUser(Context context, String email) {
		try {
			User user = MainActivity.getDbHelper(context).getUser(email);
			SharedPreferences preferences = context.getSharedPreferences(Constants.PREFERNCE, Context.MODE_PRIVATE);
			Editor editor = preferences.edit();
			editor.putBoolean(Constants.LOGIN_STATUS, true);
			editor.putString(Constants.LOGGED_IN_USER, new GsonBuilder().create().toJson(user));
			editor.commit();

		} catch (SQLException e) {
			e.printStackTrace();
		}

	}

	/**
	 * Checks the result came from db query. Possible results can be;
	 * 
	 * ResultCodes.USER_NOT_EXISTS = if email does not exists
	 * ResultCodes.USER_LOGIN_SUCCESS = if email exists and password match hint
	 * hint = if email exists but password do not match
	 * 
	 * @param context
	 * @param result
	 *            {@link ResultCodes}
	 */
	private boolean CheckUserCredentials(Context context, String result) {
		if (result != null) {
			if (result.equals(ResultCodes.USER_NOT_EXISTS)) {
				Toast.makeText(context, result, Toast.LENGTH_LONG).show();
			} else if (result.equals(ResultCodes.USER_LOGIN_SUCCESS)) {
				Toast.makeText(context, result, Toast.LENGTH_LONG).show();
				return true;
			} else {
				Toast.makeText(context, ResultCodes.USER_INVALID_CREDENTIALS, Toast.LENGTH_LONG).show();
				if (result.length() > 0) {
					tv_hint.setText("Hint: " + result);
					tv_hint.setVisibility(View.VISIBLE);
				}
			}

		}

		return false;

	}

	/**
	 * Check if password and email are non empty
	 * 
	 * @param context
	 * @param email
	 * @param password
	 * @return
	 */
	private boolean validateInput(Context context, String email, String password) {

		if (email.length() == 0 || password.length() == 0) {
			Toast.makeText(context, "Input Credentials", Toast.LENGTH_SHORT).show();
			return false;
		}

		if (email.length() > 0 && password.length() > 0) {

			return true;
		}
		return false;
	}

	@Override
	public void onReceive(Context context, Intent intent) {

	}
}
