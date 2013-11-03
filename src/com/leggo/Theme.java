package com.leggo;

import android.app.Activity;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;

public class Theme {
	
	public static void setPrefTheme(Activity activity)
	{
		SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(activity);

        if (prefs.getBoolean("nightMode", false) == true)
        {
        	activity.setTheme(R.style.NightMode); 
		}
	}
		
}
