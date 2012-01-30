package me.cosmodro.tapsy;

import android.app.Activity;
import android.content.Context;
import android.graphics.Canvas;
import android.support.v4.view.ViewPager;
import android.text.format.Time;
import android.util.AttributeSet;
import android.util.Log;
import android.widget.TextView;

import com.wimm.framework.watches.BaseWatchView;


public class TapsyWatchView extends BaseWatchView {
	private static String TAG = "TapsyWatchView";


	private ViewPager pager;
	private TapsyData tapsy;
	private TapsyPagerAdapter pagerAdapter;
	
    //private TapsyActivity activity;
    
	public TapsyWatchView(Context context, AttributeSet attrs) {
		super(context, attrs);
		//activity = (TapsyActivity) context;
		//Log.d(TAG, "TapsyWatchView constructor");
		tapsy = new TapsyData(context);
		pagerAdapter = new TapsyPagerAdapter(tapsy, (Activity)context);
		tapsy.loadPrefs();
	}
	
	@Override
	protected void onFinishInflate(){
		super.onFinishInflate();
		pager = (ViewPager) this.findViewById(R.id.pager);
		//Log.d(TAG, "pager is: "+pager);
		pager.setAdapter(pagerAdapter);
	}

	@Override
	public void onDraw(Canvas canvas){
		super.onDraw(canvas);
		Time time = getTime();
		TextView timeView = (TextView) this.findViewById(R.id.timeText);
		int hour = time.hour;
		if (!this.use24HourTime()){
			if (hour > 12){
				hour -= 12;
			}
			if (hour == 0){
				hour = 12;
			}
		}
		String minute = ""+time.minute;
		if (time.minute < 10){
			minute = "0"+minute;
		}
		timeView.setText(hour +":"+minute);
	  
		/*
		if (!isDisplayActive()){
			postInvalidateDelayed(15000);
		}
		*/
		//activity.updateUI();
		tapsy.updateUI();
	}

}
