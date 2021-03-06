// Copyright (C) 2014.  Finley Smith
//
// This program is free software; you can redistribute it and/or
// modify it under the terms of the GNU General Public License
// as published by the Free Software Foundation; either version 2
// of the License, or (at your option) any later version.
//
// This program is distributed in the hope that it will be useful,
// but WITHOUT ANY WARRANTY; without even the implied warranty of
// MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
// GNU General Public License for more details.
//
// You should have received a copy of the GNU General Public License
// along with this program; if not, write to the Free Software
// Foundation, Inc., 51 Franklin Street, Fifth Floor, Boston, MA  02110-1301, USA.
// Also add information on how to contact you by electronic and paper mail.

package com.bordengrammar.bordengrammarapp;


import android.animation.ValueAnimator;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.preference.PreferenceManager;
import android.util.Log;
import android.widget.ProgressBar;

import com.bordengrammar.bordengrammarapp.utils.ut;
import com.crashlytics.android.Crashlytics;
import com.readystatesoftware.systembartint.SystemBarTintManager;

import java.text.Format;
import java.text.SimpleDateFormat;
import java.util.List;

import twitter4j.Twitter;
import twitter4j.TwitterException;
import twitter4j.TwitterFactory;
import twitter4j.conf.ConfigurationBuilder;

public class SplashActivity extends Activity {


	public String LOG_TAG = "SplashActivity";



	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		Crashlytics.start(this);

		setContentView(R.layout.activity_splash);
		SystemBarTintManager tintManager = new SystemBarTintManager(this);
		tintManager.setStatusBarTintEnabled(true);
		tintManager.setStatusBarTintResource(R.color.bordenyellow);
		tintManager.setNavigationBarTintEnabled(true);
		tintManager.setNavigationBarTintResource(R.color.bordenyellow);
		//final TextView t = (TextView) findViewById(R.id.splashprog);
		final ProgressBar p = (ProgressBar) findViewById(R.id.progressBar);
		//t.setText("Loading");
		ValueAnimator animator = ValueAnimator.ofInt(0, 100);
		animator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener(){
			@Override
			public void onAnimationUpdate(ValueAnimator animation) {
				int currentValue = (Integer) animation.getAnimatedValue();
				//t.setText("Loading " + currentValue + "%");
				p.setProgress(currentValue);
			}
		});
		animator.setDuration(2500);
		animator.start();


		/**
		 * Showing splashscreen while making network calls to download necessary
		 * data before launching the app Will use AsyncTask to make http call
		 */
		final PrefetchData prefetchData = new PrefetchData();
		prefetchData.execute();
		Handler handler = new Handler();
		handler.postDelayed(new Runnable()
		{
			@Override
			public void run() {
				if ( prefetchData.getStatus() == AsyncTask.Status.RUNNING )
					prefetchData.cancel(true);
					if(readPrefs("twitter").isEmpty()){
					savePrefs("twitter", "error");

					}

					Intent i = new Intent(SplashActivity.this, MainActivity.class);
					startActivity(i);
					finish();
			}
		}, 3000 );


	}

	/**
	 * Async Task to make https call
	 */
	private class PrefetchData extends AsyncTask<Void, Void, Void> {

		@Override
		protected void onPreExecute() {
			super.onPreExecute();
			// before making http calls

		}

		@Override
		protected Void doInBackground(Void... arg0) {
            /*
             * Will make http call here This call will download required data
             * before launching the app
             * example:
             */
			if(isNetworkAvailable()) {

				ConfigurationBuilder cb = new ConfigurationBuilder();
				cb.setDebugEnabled(true)
						.setOAuthConsumerKey("Toqp03fcUErG5P8e9nhfsw")
						.setOAuthConsumerSecret(
								"SEqktstO9h7SqSm7zmcuWlH3bOtElJm1Ds2TFSwFBc")
						.setOAuthAccessToken(
								"2245935685-U5LMfl4oEcOv6Khw58JZqRdcH2PlABEeUP2JeXj")
						.setOAuthAccessTokenSecret(
								"uFcGRpCx8aGXdv3AiAkfVImnoLrlNNCUnZ2UtE76Zbnpa");

				TwitterFactory tf = new TwitterFactory(cb.build());
				Twitter twitter = tf.getInstance();
				List<twitter4j.Status> statuses = null;

				String user;
				user = "bordengrammar";
				try {
					statuses = twitter.getUserTimeline(user);
				} catch (TwitterException e) {
					e.printStackTrace();
					if(readPrefs("twitter")==null){
						savePrefs("twitter", "error");
					}
				}

				assert statuses != null;
				twitter4j.Status status = statuses.get(0);
				Format formatter = new SimpleDateFormat("MMM d, K:mm");
				String link = status.getText().toLowerCase();
				String linky = "https://twitter.com/Bordengrammar/status/" + String.valueOf(status.getId());
				StringBuilder reallink = new StringBuilder();
				String returny;
				//if there is no link, we call reallink later and work whether it is null or not
				for(int x=0; x < 160; x=x+1){
					String h = "h";
					String t = "t";
					String p = "p";
					if(link.charAt(x)==h.charAt(0)){
						int x1 = x + 1;
						Integer hy = x;
						if(link.charAt(x1)==t.charAt(0)){
							int x2 = x1 + 1;
							if(link.charAt(x2)==t.charAt(0)){
								int x3 = x2 + 1;
								if(link.charAt(x3)==p.charAt(0)){
									//it is a link
									if(hy==0){
										//we know that the whole entire tweet is a link
										reallink.append(link);
										break;
									}

									//if not, find the index where we started which is x
									//a twitter photo link is 26 chars
									int q = x + 26;

									for(int xy=x; xy < q; xy = xy + 1){
										try {
											reallink.append(link.charAt(xy));
										} catch (StringIndexOutOfBoundsException e){
											e.printStackTrace();
											break;
										}

									}
									break;
								}
							}
						} else {
							hy = null;
						}
					}
				}
				if(reallink.toString().isEmpty()){
					returny = null;
				} else {
					returny = reallink.toString();
					if(returny.contains("t.co")){
						returny = linky;
					}


					ut.logIt(returny);
				}
				savePrefs("link", returny);
				savePrefs("twitter", status.getText());
				savePrefs("twittertime", formatter.format(status.getCreatedAt()));


			} else {
				Log.e(LOG_TAG, "No internet");
				if(readPrefs("twitter")==null){
					savePrefs("twitter", "error");
				}

			}

			return null;
		}

		@Override
		protected void onPostExecute(Void result) {
			super.onPostExecute(result);
			// After completing http call
			// will close this activity and lauch main activity


			if(readPrefs("twitter").isEmpty()){
				savePrefs("twitter", "error");
			}
			if(readPrefs("link").isEmpty()){
				savePrefs("link", "error");
			}

			Intent i = new Intent(SplashActivity.this, MainActivity.class);
			startActivity(i);

			// close this activity
			finish();
		}

	}
	private void savePrefs(String key, String value) {
		SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(this);
		SharedPreferences.Editor edit = sp.edit();
		edit.putString(key, value);
		edit.commit();
	}
	public String readPrefs(String key) {
		SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(this);
		return sp.getString(key, "An error occured whilst fetching twitter feed");
	}
	private boolean isNetworkAvailable() {
		ConnectivityManager connectivityManager
				= (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
		NetworkInfo activeNetworkInfo = connectivityManager.getActiveNetworkInfo();
		return activeNetworkInfo != null;
	}

}