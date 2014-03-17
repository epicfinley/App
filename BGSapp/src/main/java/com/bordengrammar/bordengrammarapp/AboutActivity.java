package com.bordengrammar.bordengrammarapp;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;

import com.suredigit.inappfeedback.FeedbackDialog;


public class AboutActivity extends Activity {
	public String TAG = "MainActivity";
	private FeedbackDialog feedBack;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_about);
	    feedBack = new FeedbackDialog(this, "AF-186C1F794D93-1A");
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main_activity_actions, menu);
        return true;
    }

	@Override
	public boolean onOptionsItemSelected(MenuItem item) { //after the menu has been inflated
		switch (item.getItemId()) { //get the id fors the menus from our menu.xml
			case android.R.id.home: //if it is home
				Intent l = new Intent(AboutActivity.this, MainActivity.class);
				startActivity(l);
				return true; //break it so it does not go onto next case
			case R.id.facebook: //if it is facebook button
				logIt("Facebook button clicked"); //logit
				Intent faceBrowserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse("https://www.facebook.com/BordenGrammarSchool"));  //Create intent variable
				startActivity(faceBrowserIntent); //Start that intent
				return true;
			case R.id.website: //if they clicked they
				Log.i(TAG, "Website Clicked");
				Intent websiteBrowserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse("http://website.bordengrammar.kent.sch.uk/")); //Same as above
				startActivity(websiteBrowserIntent);
				return true;
			case R.id.action_settings: //action settings actually about, can't change it now
				logIt("clicked about");
				Intent i = new Intent(AboutActivity.this, AboutActivity.class);
				startActivity(i);

				return true;
			case R.id.action_feedback: //if they clciked send feedback
				logIt("feedback");
				feedBack.show(); //show the feedback that we declared
				return true;
			default:
				return super.onOptionsItemSelected(item);
		}

	}
	public void logIt(String it) {
		Log.i(TAG, it);
	}

}