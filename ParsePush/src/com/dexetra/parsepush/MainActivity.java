package com.dexetra.parsepush;

import org.json.JSONException;
import org.json.JSONObject;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.parse.LogInCallback;
import com.parse.ParseException;
import com.parse.ParseInstallation;
import com.parse.ParsePush;
import com.parse.ParseQuery;
import com.parse.ParseUser;
import com.parse.PushService;
import com.parse.SendCallback;
import com.parse.SignUpCallback;

public class MainActivity extends Activity {
	Button btnSignUp;
	Button btnLogIn;
	Button btnPush;

	EditText edtName;
	EditText edtPass;
	EditText edtMail;

	TextView txtMsg;

	ParseUser mCurrentUser = null;
	int mMode = 0;
	LogInCallback mLogInCallBack;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		initObjects();
		initParse();
	}

	private void initParse() {
		PushService.subscribe(this, "", MainActivity.class);
		mCurrentUser = ParseUser.getCurrentUser();

		if (mCurrentUser != null) {
			// you're logged in!!
			btnPush.setEnabled(true);
			txtMsg.setText("Logged in");
			ParseInstallation installation = ParseInstallation
					.getCurrentInstallation();
			installation.put("username", mCurrentUser.getUsername());
			installation.saveEventually();
		}
		
		IntentFilter intentFilter = new IntentFilter("MyAction");
		BroadcastReceiver pushReceiver;
		pushReceiver = new BroadcastReceiver() {
		                            public void onReceive(Context context, Intent intent) {
		                                 Bundle extras = intent.getExtras();
		                                  try {
		                                        txtMsg.setText("Push message received!");
		                                  } catch (Exception e) {
		                                        e.printStackTrace();
		                                  }
		                            }
		                          };
		registerReceiver(pushReceiver, intentFilter);
	}
	public void btnPush_onClick(View view) {
		if(mMode == 0){
			ParsePush push = new ParsePush(); 
			push.setChannel(""); 
			push.setMessage("Simple broadcast Message"); 
			push.sendInBackground();
		}else if(mMode == 1){
			//Broadcast with custom action and intent filter.
			//Sends a more structured JSON object.
			JSONObject object = new JSONObject();
	        try {
	                    object.put("alert", "MyData");
	                    object.put("title", "MyTitle");
	                    object.put("action", "MyAction");                   
	                    ParsePush pushMessageClient1 = new ParsePush();
	                    pushMessageClient1.setData(object);
	                    pushMessageClient1.setChannel("");
	                    pushMessageClient1.sendInBackground(new SendCallback() {
	                                        @Override
	                                        public void done(ParseException e) {
	                                                 if (e == null)
	                                                	 txtMsg.setText("Push complete");
	                                        }
	                                  });
	         } catch (JSONException e){
	        	 e.printStackTrace();
	         }
		}else if(mMode == 2){
			//Push only to a particular user. 
			ParsePush parsePush = new ParsePush();
			ParseQuery pQuery = ParseInstallation.getQuery(); // <-- Installation query
			pQuery.whereEqualTo("username", mCurrentUser.getUsername());
			parsePush.sendMessageInBackground("Only for the person who sent the push", pQuery);
		}
		
		
	}
	public void btnLogin_onClick(View view) {
		logIn();
	}
	public void btnSignUp_onClick(View view) {
		ParseUser user = new ParseUser();
		user.setUsername(edtName.getText().toString());
		user.setPassword(edtPass.getText().toString());
		user.setEmail(edtMail.getText().toString());
		user.put("phone", "650-253-0000");

		user.signUpInBackground(new SignUpCallback() {
			public void done(ParseException e) {
				if (e == null) {
					txtMsg.setText("Sign Up successful!");
					logIn();
				} else {
					txtMsg.setText("Sign Up failed!");
				}
			}
		});
	}

	private void logIn() {
		ParseUser.logInInBackground(edtName.getText().toString(), edtPass
				.getText().toString(), mLogInCallBack);
	}

	private void initObjects() {
		btnSignUp = (Button) findViewById(R.id.btnSignUp);
		btnLogIn = (Button) findViewById(R.id.btnLogin);
		btnPush = (Button) findViewById(R.id.btnPush);

		edtName = (EditText) findViewById(R.id.edtUser);
		edtPass = (EditText) findViewById(R.id.edtPass);
		edtMail = (EditText) findViewById(R.id.edtMail);

		txtMsg = (TextView) findViewById(R.id.txtMsg);
		btnPush.setEnabled(false);
		
		
		mLogInCallBack = new LogInCallback() {
	    	  public void done(ParseUser user, ParseException e) {
		    	    if (user != null) {
		    	    	btnPush.setEnabled(true);
		    	    	mCurrentUser = ParseUser.getCurrentUser();
		    	    	ParseInstallation installation = ParseInstallation.getCurrentInstallation();
		    			installation.put("username", mCurrentUser.getUsername());
		    			installation.saveEventually();
		    			txtMsg.setText("Logged in");
		    	    } else {
		    	    	txtMsg.setText("Failed log in");
		    	    }
		    	  }
		    	};
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		getMenuInflater().inflate(R.menu.activity_main, menu);
		return true;
	}
}
