package me.cosmodro.tapsy;

import android.content.Context;
import android.graphics.Canvas;
import android.text.format.Time;
import android.util.AttributeSet;
import android.util.Log;
import android.widget.TextView;

import com.wimm.framework.watches.BaseWatchView;


public class TapsyWatchView extends BaseWatchView {
	private static String TAG = "TapsyWatchView";

    private TapsyActivity activity;
    
	public TapsyWatchView(Context context, AttributeSet attrs) {
		super(context, attrs);
		activity = (TapsyActivity) context;
		Log.d(TAG, "TapsyWatchView constructor");
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
		activity.updateUI();
	}

}
