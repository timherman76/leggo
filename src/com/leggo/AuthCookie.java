package com.leggo;

import java.io.IOException;
import java.util.List;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.params.HttpClientParams;
import org.apache.http.cookie.Cookie;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.params.BasicHttpParams;
import org.apache.http.params.HttpParams;

import android.accounts.Account;
import android.accounts.AccountManager;
import android.accounts.AccountManagerFuture;
import android.accounts.AuthenticatorException;
import android.accounts.OperationCanceledException;
import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.util.Log;

public class AuthCookie {
	public static final String TAG = "AuthCookie";
	
	private static String APP_AUTH_URL = "http://simplecta.appspot.com/_ah/login?continue=http://localhost/&auth=";

	public static void getCookie(final String authToken, final Context context) {

		new Thread(new Runnable() {
			public void run() {
				SharedPreferences prefs = PreferenceManager
						.getDefaultSharedPreferences(context);
				SharedPreferences.Editor editor = prefs.edit();

				String href = APP_AUTH_URL + authToken;
				String cookie;

				DefaultHttpClient httpclient = new DefaultHttpClient();
				final HttpParams params = new BasicHttpParams();

				// Don't follow redirects
				HttpClientParams.setRedirecting(params, false);
				httpclient.setParams(params);
				HttpGet httpget = new HttpGet(href);
				try {
					HttpResponse response = httpclient.execute(httpget);
					HttpEntity entity = response.getEntity();
					if (entity != null) {
						entity.consumeContent();
					}
					// Get all the cookies
					List<Cookie> cookies = httpclient.getCookieStore()
							.getCookies();
					if (cookies.isEmpty()) {
						Log.d(TAG, "No Cookies");
					} else {
						// Search for the SACSID cookie and store it
						for (int i = 0; i < cookies.size(); i++) {
							Cookie c = cookies.get(i);
							if (c.getName().contentEquals("ACSID")) {
								Log.d(TAG, "ACSID Found");
								cookie = c.getValue();
								Log.d(TAG, "Cookie set to: " + cookie);
								editor.putString("cookie", cookie);
								editor.commit();
							}
						}
					}
				} catch (ClientProtocolException e) {
					e.printStackTrace();
				} catch (IOException e) {
					e.printStackTrace();
				}

			}
		}).start();
	}
	
	public static void revalidateCookie(final int accountId, final Activity activity) {
		new Thread(new Runnable() {
			public void run() {
				Context context = activity;
				SharedPreferences prefs = PreferenceManager
						.getDefaultSharedPreferences(context);
				SharedPreferences.Editor editor = prefs.edit();
				
				AccountManager accountManager = AccountManager
						.get(context);
				Account[] accounts = accountManager.getAccountsByType("com.google");
				
				AccountManagerFuture<Bundle> future = accountManager.getAuthToken(accounts[accountId], "ah", null, activity, null, null);
				try {
					Bundle bundle = future.getResult();
					
					String authToken = bundle.getString(AccountManager.KEY_AUTHTOKEN);
					accountManager.invalidateAuthToken("com.google", authToken);
					Log.d(TAG, "oldtoken:"+ authToken);
					
					future = accountManager.getAuthToken(accounts[accountId], "ah", null, activity, null, null);
					bundle = future.getResult();
					authToken = bundle.getString(AccountManager.KEY_AUTHTOKEN);
					Log.d(TAG, "newtoken:"+ authToken);
					editor.putString("token", authToken);
					editor.commit();
					
					getCookie(authToken, context);
				} catch (OperationCanceledException e) {
					e.printStackTrace();
				} catch (AuthenticatorException e) {
					e.printStackTrace();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
	}).start();
	}
	

}
