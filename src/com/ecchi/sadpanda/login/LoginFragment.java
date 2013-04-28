package com.ecchi.sadpanda.login;

import android.app.AlertDialog;
import android.app.AlertDialog.Builder;
import android.app.Dialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;

import com.actionbarsherlock.app.SherlockDialogFragment;
import com.ecchi.sadpanda.R;

public class LoginFragment extends SherlockDialogFragment {
	
	private OnLoginRequestListener mRequestListener;
	
	@Override
	public Dialog onCreateDialog(Bundle savedInstanceState) {
		AlertDialog.Builder loginBuilder = new Builder(getActivity());
				
		loginBuilder.setTitle("Login");
		View v = View.inflate(getActivity(), R.layout.login_layout, null);
		loginBuilder.setView(v);
				
		final EditText username = (EditText)v.findViewById(R.id.username);
		final EditText password = (EditText)v.findViewById(R.id.password);
		
		loginBuilder.setPositiveButton("OK", new DialogInterface.OnClickListener() {			
			@Override
			public void onClick(DialogInterface dialog, int which) {				
				if(mRequestListener != null)
					mRequestListener.onLoginRequest(username.getText().toString(), password.getText().toString());				
			}
		});
		
		loginBuilder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {			
			@Override
			public void onClick(DialogInterface dialog, int which) {
				dialog.cancel();
			}
		});
		
		return loginBuilder.create();
	}
	
	public void setLoginRequestListener(OnLoginRequestListener listener)
	{
		mRequestListener = listener;
	}
}
