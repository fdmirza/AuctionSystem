package com.faizan.auctionsystem;

import java.sql.SQLException;
import java.util.Locale;

import com.datamodels.User;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnFocusChangeListener;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

/**
 * Register a user in database
 * 
 * @author Faizan
 * 
 */
public class RegisterFragment extends BaseFragment implements OnClickListener, OnFocusChangeListener {

	public static RegisterFragment newInstance() {
		return new RegisterFragment();
	}

	private EditText et_email;
	private EditText et_name;
	private EditText et_password;
	private EditText et_confirm_password;
	private EditText et_password_hint;

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		super.onCreateView(inflater, container, savedInstanceState);
		View view = inflater.inflate(R.layout.fragment_register, container, false);

		return view;
	}

	@Override
	public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
		super.onViewCreated(view, savedInstanceState);
		TextView tv_register = (TextView) view.findViewById(R.id.tv_register);
		tv_register.setOnClickListener(this);

		et_email = (EditText) view.findViewById(R.id.et_email);
		et_name = (EditText) view.findViewById(R.id.et_name);
		et_password = (EditText) view.findViewById(R.id.et_password);
		et_confirm_password = (EditText) view.findViewById(R.id.et_confirm_password);
		et_password_hint = (EditText) view.findViewById(R.id.et_password_hint);
		et_email.setOnFocusChangeListener(this);
	}

	@Override
	public void onClick(View v) {
		int id = v.getId();
		switch (id) {

		case R.id.tv_register:
			String email = et_email.getText().toString().trim().toLowerCase(Locale.ENGLISH);
			String name = et_name.getText().toString().trim().toLowerCase(Locale.ENGLISH);
			String password = et_password.getText().toString().trim().toLowerCase(Locale.ENGLISH);
			String confirmPassword = et_confirm_password.getText().toString().trim().toLowerCase(Locale.ENGLISH);
			String hint = et_password_hint.getText().toString().trim().toLowerCase(Locale.ENGLISH);
			if (validateInput(v.getContext(), email, name, password, confirmPassword, hint)) {
				boolean result = RegisterUser(v.getContext(), email, name, password, confirmPassword, hint);
				if (result) {
					Toast.makeText(v.getContext(), "Registration Successful.", Toast.LENGTH_SHORT).show();
				}

			}
			getActivity().onBackPressed();
			break;

		default:
			break;
		}

	}

	private boolean RegisterUser(Context context, String email, String name, String password, String confirmPassword, String hint) {

		try {
			User user = new User(name, email, confirmPassword, hint);
			if (MainActivity.getDbHelper(context).hasEmailRegistered(email)) {
				Toast.makeText(context, "Email already exists.", Toast.LENGTH_SHORT).show();
				return false;
			}
			MainActivity.getDbHelper(context).getUserDao().create(user);
			return true;
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return false;
	}

	/**
	 * validates all the data, if something missing shows toast.
	 * 
	 * @param context
	 * @param email
	 * @param name
	 * @param password
	 * @param confirmPassword
	 * @param hint
	 * @return
	 */
	private boolean validateInput(Context context, String email, String name, String password, String confirmPassword, String hint) {
		if (email.length() == 0) {
			Toast.makeText(context, "Please enter your email.", Toast.LENGTH_SHORT).show();
			return false;
		}
		if (!isValidEmail(email)) {
			Toast.makeText(context, "Email is not valid.", Toast.LENGTH_SHORT).show();
			return false;
		}
		if (name.length() == 0) {
			Toast.makeText(context, "Please enter your name.", Toast.LENGTH_SHORT).show();
			return false;
		}
		if (password.length() == 0) {
			Toast.makeText(context, "Please enter password.", Toast.LENGTH_SHORT).show();
			return false;
		}
		if (password.length() < 6) {
			Toast.makeText(context, "Password must be atleast 6 digits or alphabets.", Toast.LENGTH_SHORT).show();
			return false;
		}
		if (!confirmPassword.equals(password)) {
			Toast.makeText(context, "Passwords do not match.", Toast.LENGTH_SHORT).show();
			return false;
		}

		return true;
	}

	private boolean isValidEmail(String email) {
		// TODO: check email regex
		return true;
	}

	@Override
	public void onFocusChange(View v, boolean hasFocus) {
		// if (!hasFocus &&
		// isValidEmail(et_email.getText().toString().trim().toLowerCase())) {
		//
		// }

	}

	@Override
	public void onReceive(Context context, Intent intent) {

	}

}
