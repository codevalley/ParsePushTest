package com.dexetra.parsepush;
import android.app.Application;

import com.parse.Parse;
import com.parse.ParseACL;

/**
 * 
 */

/**
 * @author dexadmin
 * 
 */
public class ParsePushApplication extends Application {

	/**
	 * 
	 */
	public void onCreate() {

		super.onCreate();
		Parse.initialize(this, "your application id here.",
				"your application key here."); // find more info at https://parse.com/apps/quickstart#android/blank 

		//ParseUser.enableAutomaticUser();
		ParseACL defaultACL = new ParseACL();
		// Optionally enable public read access.
		// defaultACL.setPublicReadAccess(true);
		ParseACL.setDefaultACL(defaultACL, true);
		
	}

}
