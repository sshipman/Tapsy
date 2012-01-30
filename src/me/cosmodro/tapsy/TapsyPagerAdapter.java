package me.cosmodro.tapsy;	

import java.util.ArrayList;

import com.wimm.framework.app.FontManager;

import me.cosmodro.wimm.widgets.NumberPicker;
import me.cosmodro.wimm.widgets.OnValueChangedListener;

import android.app.Activity;
import android.content.SharedPreferences;
import android.graphics.Typeface;
import android.os.Parcelable;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.RadioButton;
import android.widget.TextView;

public class TapsyPagerAdapter extends PagerAdapter {
	protected static final String TAG = "TapsyPagerAdapter";
	private ArrayList<View> views;
	private TapsyData tapsy;
	public View watchFace;
	public View prefsView;
	private SharedPreferences prefs;
	
	public TapsyPagerAdapter(TapsyData tapsy, Activity context){
		views = new ArrayList<View>();
		this.tapsy = tapsy;
		//Log.d(TAG, "constructor");
		LayoutInflater lif = context.getLayoutInflater();
		//Log.d(TAG, "inflating watch face");
		watchFace = lif.inflate(R.layout.watch_face, null);
		views.add(watchFace);
		//Log.d(TAG, "inflating prefs");
		prefsView = lif.inflate(R.layout.prefs, null);
		views.add(prefsView);
		//Log.d(TAG, "inflated.  now init prefs");
		initPrefs();
		//Log.d(TAG, "and init watch ui");
		initWatchUI();
	}
	
	/**
	 * initialize prefs with values from shared preferences
	 */
	private void initPrefs(){
		prefs = tapsy.getPrefs();
		final SharedPreferences.Editor editor = prefs.edit();
		Button resetButton = (Button) prefsView.findViewById(R.id.resetButton);
		resetButton.setOnClickListener(new OnClickListener(){
			@Override
			public void onClick(View arg0) {
				tapsy.reset();
			}
		});
		
		NumberPicker weight = (NumberPicker) prefsView.findViewById(R.id.weightPref);
		weight.setValue(prefs.getInt("weight", 150));
		tapsy.setWeight(weight.getValue());
		weight.setOnValueChangedListener(new OnValueChangedListener(){
			@Override
			public void onValueChanged(int value) {
				tapsy.setWeight(value);
				editor.putInt("weight", value);
				editor.commit();
			}
		});
		final RadioButton male = (RadioButton) prefsView.findViewById(R.id.radio_male);
		RadioButton female = (RadioButton) prefsView.findViewById(R.id.radio_female);
		male.setChecked(prefs.getBoolean("maleGender", true));
		if (male.isChecked()){
			tapsy.setGender(Gender.MALE);
		}else{
			female.setChecked(true);
			tapsy.setGender(Gender.FEMALE);
		}
		OnClickListener radioListener = new OnClickListener(){
			@Override
			public void onClick(View v) {
				RadioButton rb = (RadioButton) v;
				boolean isMale = true;
				if (rb.equals(male)){
					tapsy.setGender(Gender.MALE);
				}else{
					tapsy.setGender(Gender.FEMALE);
					isMale = false;
				}
				//Log.d(TAG, "setting gender to isMale:"+isMale);
				editor.putBoolean("maleGender", isMale);
				editor.commit();
			}
		};
		male.setOnClickListener(radioListener);
		female.setOnClickListener(radioListener);
		
		NumberPicker threshold = (NumberPicker) prefsView.findViewById(R.id.thresholdPref);
		threshold.setValue(prefs.getInt("threshold", 6));
		tapsy.setLegalThreshold(threshold.getValue() / 100d);
		threshold.setOnValueChangedListener(new OnValueChangedListener(){
			@Override
			public void onValueChanged(int value) {
				tapsy.setLegalThreshold(value/100d);
				editor.putInt("threshold", value);
				editor.commit();
			}
		});
	}

	private void initWatchUI(){
		Button addDrinkButton = (Button)watchFace.findViewById(R.id.addDrinkButton);
		addDrinkButton.setOnClickListener(new OnClickListener(){
			@Override
			public void onClick(View v) {
				tapsy.addDrink();
			}
		});
		
		TextView drinksCountView = (TextView)watchFace.findViewById(R.id.drinkCountText);
		tapsy.setDrinksCountView(drinksCountView);
		TextView bacView = (TextView)watchFace.findViewById(R.id.bac);
		tapsy.setBacView(bacView);
		TextView timeLeftView = (TextView)watchFace.findViewById(R.id.timeLeft);
		tapsy.setTimeLeftView(timeLeftView);
		
		Typeface digital = FontManager.createTypeface(FontManager.DIGITAL);
		
		ViewGroup vg = (ViewGroup)watchFace;
		for (int i = 0; i < vg.getChildCount(); i++){
			View v = vg.getChildAt(i);
			if (v.getClass() == TextView.class){
				((TextView)v).setTypeface(digital);
			}
		}
		tapsy.updateUI();
	}
	
	@Override
	public int getCount() {
		return views.size();
	}

    /**
     * Create the page for the given position.  The adapter is responsible
     * for adding the view to the container given here, although it only
     * must ensure this is done by the time it returns from
     * {@link #finishUpdate()}.
     *
     * @param container The containing View in which the page will be shown.
     * @param position The page position to be instantiated.
     * @return Returns an Object representing the new page.  This does not
     * need to be a View, but can be some other container of the page.
     */
        @Override
        public Object instantiateItem(View collection, int position) {
        	View view = views.get(position);
            ((ViewPager) collection).addView(view,0);
            return view;
        }

        /**
         * Remove a page for the given position.  The adapter is responsible
         * for removing the view from its container, although it only must ensure
         * this is done by the time it returns from {@link #finishUpdate()}.
         *
         * @param container The containing View from which the page will be removed.
         * @param position The page position to be removed.
         * @param object The same object that was returned by
         * {@link #instantiateItem(View, int)}.
         */
            @Override
            public void destroyItem(View collection, int position, Object view) {
                    ((ViewPager) collection).removeView((View) view);
            }

            
            
            @Override
            public boolean isViewFromObject(View view, Object object) {
                    return view==((View)object);
            }
            
            /**
             * Called when the a change in the shown pages has been completed.  At this
             * point you must ensure that all of the pages have actually been added or
             * removed from the container as appropriate.
             * @param container The containing View which is displaying this adapter's
             * page views.
             */
            @Override
            public void finishUpdate(View arg0) {}
            

            @Override
            public void restoreState(Parcelable arg0, ClassLoader arg1) {}

            @Override
            public Parcelable saveState() {
                    return null;
            }

            @Override
            public void startUpdate(View arg0) {}
        
            


}
