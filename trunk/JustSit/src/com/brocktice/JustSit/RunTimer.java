package com.brocktice.JustSit;

import android.app.Activity;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.widget.TextView;

public class RunTimer extends Activity {
	private TextView mTimerView;
	private TextView mTimerLabel;
	public static final String PREFS_NAME = "JustSitPreferences";

	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.run_timer);
		mTimerView = (TextView) findViewById(R.id.timer_view);
		mTimerLabel = (TextView) findViewById(R.id.timer_label);
		
		Bundle extras = getIntent().getExtras();
		if (extras != null) {
			int timer_label = extras.getInt(JustSit.TIMER_LABEL);
			int timer_duration = extras.getInt(JustSit.TIMER_DURATION);
			mTimerLabel.setText(timer_label);
			runCountdown(timer_duration);
		}
	}
	
	
	protected void runCountdown(int start_time){
		new CountDownTimer(start_time, 1000) {

            @Override
			public void onTick(long millisUntilFinished) {
                                  mTimerView.setText(Long.toString(millisUntilFinished / 1000));  
            }

            @Override
			public void onFinish() {
            	setResult(RESULT_OK);
            	finish();
            }
         }.start();
	}
	
	protected int calculateCountdownTime(int hours, int minutes){
		return (hours*3600 + minutes*60) * 1000;
	}
	
	
}
