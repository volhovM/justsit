// Copyright 2008 Brock M. Tice
/*  This file is part of JustSit.

    JustSit is free software: you can redistribute it and/or modify
    it under the terms of the GNU General Public License as published by
    the Free Software Foundation, either version 3 of the License, or
    (at your option) any later version.

    JustSit is distributed in the hope that it will be useful,
    but WITHOUT ANY WARRANTY; without even the implied warranty of
    MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
    GNU General Public License for more details.

    You should have received a copy of the GNU General Public License
    along with JustSit.  If not, see <http://www.gnu.org/licenses/>.
*/

package com.brocktice.JustSit;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.os.PowerManager;
import android.provider.Settings;
import android.provider.Settings.SettingNotFoundException;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

public class JustSit extends Activity {
	public static final String PREFS_NAME = "JustSitPreferences";
	public static final String TIMER_LABEL = "timer_label";
	public static final String TIMER_DURATION = "timer_duration";
    public static final String PREP_SECONDS = "prepSeconds";
    public static final String MEDITATION_MINUTES = "meditationMinutes";
    public static final String AIRPLANE_MODE = "airplaneMode";
    public static final String SCREEN_ON = "screenOn";
	public static final String ORIG_AIRPLANE_MODE = "originalAirplaneMode";
	public static final String SILENT_MODE = "silentMode";
	public static final String ORIG_RINGER_MODE= "originalRingerMode";
	
	public static final int TRUE=1;
	public static final int FALSE=0;
    private static final int ACTIVITY_PREP=0;
    private static final int ACTIVITY_MEDITATE=1;
    
    private static final String TAG = "JustSit";

    private EditText mPrepText;
    private EditText mMeditateText;
    private ImageView mPrepUp;
    private ImageView mPrepDown;
    private ImageView mMeditateUp;
    private ImageView mMeditateDown;
	private MediaPlayer mMediaPlayer;
	private Boolean mScreenOn;
	private Boolean mAirplaneMode;
	private Boolean mSilentMode;
	private PowerManager.WakeLock mWakeLock;
	private AudioManager mAudioManager;
	
    /** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);
        
        // Restore preferences
        mPrepText = (EditText) findViewById(R.id.preparation_text);
        mMeditateText = (EditText) findViewById(R.id.meditation_text);
        
        SharedPreferences settings = getSharedPreferences(PREFS_NAME, MODE_PRIVATE);
        setPrepTime(settings.getInt(PREP_SECONDS, 30));
        setMeditateTime(settings.getInt(MEDITATION_MINUTES, 30));
        mAirplaneMode = settings.getBoolean(AIRPLANE_MODE, false);
        mScreenOn = settings.getBoolean(SCREEN_ON, false);
        mSilentMode = settings.getBoolean(SILENT_MODE, false);
        
        mPrepUp = (ImageView) findViewById(R.id.prep_up_button);
        mPrepDown = (ImageView) findViewById(R.id.prep_down_button);
        mMeditateUp = (ImageView) findViewById(R.id.meditate_up_button);
        mMeditateDown = (ImageView) findViewById(R.id.meditate_down_button);
        mMediaPlayer = new MediaPlayer();
        mAudioManager = (AudioManager) this.getSystemService(Context.AUDIO_SERVICE);
        
        Button sitButton = (Button) findViewById(R.id.sit);
        sitButton.setOnClickListener(launchRunTimer);
        
        mPrepUp.setOnClickListener(new OnClickListener() {
        	  public void onClick(View v) {
	        	    incrementPrepTime();
        	  }
        	});
        
        mPrepDown.setOnClickListener(new OnClickListener() {
        	public void onClick(View v){
        		decrementPrepTime();
        	}
        });
        
        mMeditateUp.setOnClickListener(new OnClickListener(){
        	public void onClick(View v){
        		incrementMeditateTime();
        	}
        });
        
        mMeditateDown.setOnClickListener(new OnClickListener(){
        	public void onClick(View v){
        		decrementMeditateTime();
        	}
        });
    }
    
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        super.onCreateOptionsMenu(menu);
        MenuInflater inflater = new MenuInflater(this);
        inflater.inflate(R.menu.justsit, menu);
        return true;
    }
    
    @Override
    public boolean onMenuItemSelected(int featureId, MenuItem item) {
        switch(item.getItemId()) {
        case R.id.settings:
            launchSettings();
            return true;
        case R.id.about:
            launchAbout();
            return true;
        }
       
        return super.onMenuItemSelected(featureId, item);
    }
    
    private void launchSettings(){
    	Intent i = new Intent(this, JsSettings.class);
    	startActivity(i);
    }
    
    private void launchAbout(){
    	Intent i = new Intent(this, JsAbout.class);
    	startActivity(i);
    }
    
    private int getPrepTime(){
    	int prepTime;
    	try{
    		prepTime = Integer.parseInt(mPrepText.getText().toString());
    	}catch(NumberFormatException e){
    		Toast badFormatToast = Toast.makeText(this, R.string.invalid_prep_time, Toast.LENGTH_LONG);
    		badFormatToast.show();
    		prepTime = Integer.parseInt(getString(R.string.default_prep_time));
    		mPrepText.setText(getString(R.string.default_prep_time));
    		Log.w(TAG, e.getMessage() + " Using default prep time");
    	}
    	return prepTime;
    }
    
    private int getMeditateTime(){
    	int meditateTime;
    	try{
    		meditateTime = Integer.parseInt(mMeditateText.getText().toString());
    	}catch(NumberFormatException e){
    		Toast badFormatToast = Toast.makeText(this, R.string.invalid_meditation_time, Toast.LENGTH_LONG);
    		badFormatToast.show();
    		meditateTime = Integer.parseInt(getString(R.string.default_meditation_time));
    		mMeditateText.setText(getString(R.string.default_meditation_time));
    		Log.w(TAG, e.getMessage() + " Using default meditation time");
    	}
    	return meditateTime;
    }
    
    private void setPrepTime(int time){
    	mPrepText.setText(Integer.toString(time));	
    }
    
    private void setMeditateTime(int time){
    	mMeditateText.setText(Integer.toString(time));
    }
    
    private void modifyPrepTime(int time){
    	setPrepTime(getPrepTime() + time);
    }
    
    private void modifyMeditateTime(int time){
    	setMeditateTime(getMeditateTime() + time);
    }
    
    private void incrementPrepTime(){
    	modifyPrepTime(1);
    }
    
    private void decrementPrepTime(){
    	modifyPrepTime(-1);
    }
    
    private void incrementMeditateTime(){
    	modifyMeditateTime(1);
    }
    
    private void decrementMeditateTime(){
    	modifyMeditateTime(-1);
    }
    
    private OnClickListener launchRunTimer = new OnClickListener(){
    	public void onClick(View v){
    		meditationSettings(true);
    		Intent i = new Intent(JustSit.this, RunTimer.class);
    		i.putExtra(TIMER_LABEL, R.string.prep_label);
    		i.putExtra(TIMER_DURATION, getPrepTime()*1000);
    		startActivityForResult(i, ACTIVITY_PREP);
    	}
    };
    
	@Override
	protected void onPause() {
		super.onPause();
		mMediaPlayer = null;
		SharedPreferences settings = getSharedPreferences(PREFS_NAME, 0);
	    SharedPreferences.Editor editor = settings.edit();
	    editor.putInt(PREP_SECONDS, getPrepTime());
	    editor.putInt(MEDITATION_MINUTES, getMeditateTime());
	    editor.commit();

	}
	
	@Override
    protected void onActivityResult(int requestCode, int resultCode, Intent intent) {
        super.onActivityResult(requestCode, resultCode, intent);
        if(resultCode == RESULT_OK){
        	switch(requestCode) {
        	case ACTIVITY_PREP:
        		mMediaPlayer = MediaPlayer.create(this, R.raw.prep);
        		mMediaPlayer.start();
        		Intent i = new Intent(JustSit.this, RunTimer.class);
        		i.putExtra(TIMER_LABEL, R.string.meditate_label);
        		i.putExtra(TIMER_DURATION, getMeditateTime()*60000);
        		startActivityForResult(i, ACTIVITY_MEDITATE);
        		break;
        	case ACTIVITY_MEDITATE:
        		meditationSettings(false);
        		mMediaPlayer = null;
        		mMediaPlayer = MediaPlayer.create(this,R.raw.meditate);
        		mMediaPlayer.start();
        		break;
        	}        
        }else{
        	if(mMediaPlayer != null){
        		mMediaPlayer.stop();
        	}
        	meditationSettings(false);
        }
        
	}
	
	protected void setAirplaneMode(boolean on){
		SharedPreferences settings = getSharedPreferences(JustSit.PREFS_NAME, 0);
	    SharedPreferences.Editor editor = settings.edit();
	    if(on){
	    	// Save the existing setting for when we revert
	    	try{
	    		editor.putInt(ORIG_AIRPLANE_MODE, 
	    				Settings.System.getInt(this.getContentResolver(), 
							Settings.System.AIRPLANE_MODE_ON));
	    		editor.commit();
    			}catch (SettingNotFoundException e){
    				Log.e(TAG, e.getMessage());
    			}
    		// Enable airplane mode
    		Settings.System.putInt(this.getContentResolver(), Settings.System.AIRPLANE_MODE_ON, TRUE);
	    }else{
	    	// Revert to original setting
	    	Settings.System.putInt(this.getContentResolver(), Settings.System.AIRPLANE_MODE_ON, settings.getInt(ORIG_AIRPLANE_MODE, FALSE));
	    	if(settings.getInt(ORIG_AIRPLANE_MODE, FALSE) == FALSE){
	    		Toast airplaneToast = Toast.makeText(this, R.string.airplane_mode_off, Toast.LENGTH_LONG);
	    		airplaneToast.show();
	    	}
	    }
	    Intent intent = new Intent(Intent.ACTION_AIRPLANE_MODE_CHANGED);
        intent.putExtra("state", on);
        this.sendBroadcast(intent);
	    
	}
	
	protected void setSilentMode(boolean on){
		SharedPreferences settings = getSharedPreferences(JustSit.PREFS_NAME, 0);
	    SharedPreferences.Editor editor = settings.edit();
		if(on){
			// Save existing setting
			editor.putInt(ORIG_RINGER_MODE, mAudioManager.getRingerMode());
			editor.commit();
			mAudioManager.setRingerMode(AudioManager.RINGER_MODE_VIBRATE);
		}else{
			mAudioManager.setRingerMode(settings.getInt(ORIG_RINGER_MODE, AudioManager.RINGER_MODE_NORMAL));
		}
	}
	
	protected void setScreenLock(boolean on){
		if(mWakeLock == null){
			PowerManager pm = (PowerManager) getSystemService(Context.POWER_SERVICE);
			mWakeLock = pm.newWakeLock(PowerManager.SCREEN_DIM_WAKE_LOCK |
										PowerManager.ON_AFTER_RELEASE, TAG);
		}
		if(on){
		 mWakeLock.acquire();
		}else{
		 mWakeLock.release();
		 mWakeLock = null;
		}

	}
	
	protected void meditationSettings(boolean on){
	    if(mAirplaneMode){
	    	setAirplaneMode(on);
	    }
	    
	    if(mScreenOn){
	    	setScreenLock(on);
	    }
	    
	    if(mSilentMode){
	    	setSilentMode(on);
	    }
	}
}