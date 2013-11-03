package com.leggo;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;

public class Theme {

	final static public int THEME_DEFAULT = 0;
	final static public int THEME_NIGHT = 1;

	public static void setPrefTheme(Activity activity) {
		SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(activity);

		if (prefs.getBoolean("nightMode", false) == true) {
			activity.setTheme(R.style.NightMode);
		}
	}

	public static void changeTheme(Activity activity, int theme_id) {

		switch (theme_id) {
		default:
		case THEME_DEFAULT:
			activity.setTheme(R.style.AppTheme);
			break;

		case THEME_NIGHT:
			activity.setTheme(R.style.NightMode);
			break;
		}

		Intent intent = activity.getIntent();

		intent.addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);

		activity.finish();
		activity.overridePendingTransition(0, 0);

		activity.startActivity(intent);
		activity.overridePendingTransition(0, 0);

	}

}
