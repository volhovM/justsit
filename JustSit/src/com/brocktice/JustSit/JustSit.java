package com.brocktice.JustSit;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;

public class JustSit extends Activity {
	public static final String PREFS_NAME = "JustSitPreferences";
	public static final String TIMER_LABEL = "timer_label";
	public static final String TIMER_DURATION = "timer_duration";
	
    private static final int ACTIVITY_PREP=0;
    private static final int ACTIVITY_MEDITATE=1;
    
    private static final String TAG = "JustSit";
    
	//private TimePicker mPrepTimePicker;
	//private TimePicker mDurationTimePicker;
    private EditText mPrepText;
    private EditText mMeditateText;
    private ImageView mPrepUp;
    private ImageView mPrepDown;
    private ImageView mMeditateUp;
    private ImageView mMeditateDown;
	private MediaPlayer mMediaPlayer;
	
    /** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);
        
        // Restore preferences
        mPrepText = (EditText) findViewById(R.id.preparation_text);
        mMeditateText = (EditText) findViewById(R.id.meditation_text);
        
        SharedPreferences settings = getSharedPreferences(PREFS_NAME, MODE_PRIVATE);
        setPrepTime(settings.getInt("prepSeconds", 30));
        setMeditateTime(settings.getInt("meditationMinutes", 30));

        mPrepUp = (ImageView) findViewById(R.id.prep_up_button);
        mPrepDown = (ImageView) findViewById(R.id.prep_down_button);
        mMeditateUp = (ImageView) findViewById(R.id.meditate_up_button);
        mMeditateDown = (ImageView) findViewById(R.id.meditate_down_button);
        
        mMediaPlayer = new MediaPlayer();
        
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
    
    private int getPrepTime(){
    	return Integer.parseInt(mPrepText.getText().toString());
    }
    
    private int getMeditateTime(){
    	return Integer.parseInt(mMeditateText.getText().toString());
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
	    editor.putInt("prepSeconds", getPrepTime());
	    editor.putInt("meditationMinutes", getMeditateTime());

	    // Don't forget to commit your edits!!!
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
        		mMediaPlayer = null;
        		mMediaPlayer = MediaPlayer.create(this,R.raw.meditate);
        		mMediaPlayer.start();
        		break;
        	}        
        }
	}
}