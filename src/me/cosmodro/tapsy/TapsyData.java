/**
 * Tapsy is a watchface for the WiMM platform designed to keep track of your blood alcohol concentration
 * It needs to know 
 * 1) when you started drinking
 * 2) how many drinks you've had
 * 3) your weight
 * 4) your gender
 * 5) legal limit threshold
 * 
 * It uses widmark's formula, which is:
 * BAC = (Standard Drinks * 0.06 * 100% * 1.055 / Weight * Gender Constant) - (0.015 * Hours)
 * 
 * Time to Sober means solve for hours, given BAC = threshold
 * threshold = (Standard Drinks * 0.06 * 100% * 1.055 / Weight * Gender Constant) - (0.015 * Hours)
 * threshold + (0.015*hours) = (Standard Drinks * 0.06 * 100% * 1.055 / Weight * Gender Constant)
 * 0.015*hours = (Standard Drinks * 0.06 * 100% * 1.055 / Weight * Gender Constant) - threshold
 * hours = ((Standard Drinks * 0.06 * 100% * 1.055 / Weight * Gender Constant) - threshold)/0.015
 * 
 * total Hours= ((drinks*0.06*100*1.055/weight*gender_constant) - threshold)/0.015;
 * hours to go = total hours - time since started drinking
 * so minutes = hours * 60 = ((drinks*0.06*100*1.055/weight*gender_constant) - threshold)/0.015;
 */

package me.cosmodro.tapsy;

import java.text.DecimalFormat;


import android.content.Context;
import android.content.SharedPreferences;
import android.util.Log;
import android.widget.TextView;

public class TapsyData {
	private final static String TAG = "TapsyData";
	private final static String TAPSY = "TAPSY";

	private final static double millisPerHour = 1000 * 60 * 60;

	private Long drinkStartTime = null;
	private int drinksCount;
	private double weight;
	private Gender gender;
	private double legalThreshold;
	
	private TextView drinksCountView;
	private TextView bacView;
	private TextView timeLeftView;
	private Context context;
	
	public TapsyData(Context ctx){
		this.context = ctx;
	}
	
	public SharedPreferences getPrefs(){
		SharedPreferences prefs = context.getSharedPreferences(TAPSY, Context.MODE_PRIVATE);
		return prefs;
	}

	public void savePrefs(){
		//Log.d(TAG, "savePrefs");
		SharedPreferences prefs = getPrefs();
		SharedPreferences.Editor editor = prefs.edit();
		//Log.d(TAG, "saving drinksCount as:"+drinksCount);
		editor.putInt("drinksCount", drinksCount);
		long dst = -1;
		if (drinkStartTime != null){
			dst = drinkStartTime;
		}
		editor.putLong("drinkStartTime", dst);
		editor.commit();
	}
	
	public void loadPrefs(){
		//Log.d(TAG, "loadPrefs");
		SharedPreferences prefs = getPrefs();
		int dc = prefs.getInt("drinksCount", 0);
		//Log.d(TAG, "got drinksCount as: "+dc);
		setDrinksCount(dc);
		setDrinkStartTime(prefs.getLong("drinkStartTime", -1));
		if (getBAC(System.currentTimeMillis()) < 0.01){
			reset();
		}
	}

/*	public boolean dragCanExit() {
		if (pager.getCurrentItem() == 1) {
			prefsRootView = (ScrollView) this.findViewById(R.id.rootPrefView);
			return prefsRootView.getScrollY() == 0;
		} else {
			return true;
		}
	}
*/
	public void addDrink() {
		if (drinksCount == 0) {
			// first drink, start the timer.
			setDrinkStartTime(System.currentTimeMillis());
		}
		setDrinksCount(drinksCount+1);
		savePrefs();
		updateUI();
	}

	public void updateUI() {
		drinksCountView.setText(Integer.valueOf(drinksCount).toString());
		long now = System.currentTimeMillis();
		double bac = getBAC(now);
		DecimalFormat df = new DecimalFormat("#.###");
		bacView.setText(df.format(bac));
		timeLeftView.setText(Integer.valueOf(getMinutesUntilSober(bac)).toString());
	}

	public double getBAC(long now) {
		double bac = 0;
		if (drinksCount > 0){
			double timeSinceStartedDrinking = (now - drinkStartTime)
					/ millisPerHour;
			//bac = (drinksCount * 0.06 * 100 * 1.055 / (weight * gender.eliminationRate))
			//		- (0.015 * timeSinceStartedDrinking);
			//100 = Kg/L -> g/100ml
			//0.8 = density of ethanol
			//0.6 = fl oz of alcohol in avg drink
			//16 = oz/lb
			
			bac = 100 * (0.8*0.6*drinksCount/(weight*16*gender.eliminationRate) - (0.00015 * timeSinceStartedDrinking));
			if (bac < 0){
				bac = 0;
			}
		}
		return bac;
	}

	public int getMinutesUntilSober(double bac) {
		if (drinkStartTime == null){
			return 0;
		}if (bac < legalThreshold){
			return 0;
		}
		//double tothours = ((drinksCount * 0.06 * 100 * 1.055 / (weight * gender.eliminationRate)) - legalThreshold) / 0.015;
		double tothours = ((0.8*0.6*drinksCount/(weight*16*gender.eliminationRate)) - legalThreshold/100) / 0.00015;
		double millisToGo = tothours*millisPerHour - (System.currentTimeMillis() - drinkStartTime);
		int minutesToGo = (int)Math.ceil(millisToGo/(60000));
		return minutesToGo;
	}

	public Long getDrinkStartTime() {
		return drinkStartTime;
	}

	public void setDrinkStartTime(Long drinkStartTime) {
		this.drinkStartTime = drinkStartTime;
	}

	public int getDrinksCount() {
		return drinksCount;
	}

	public void setDrinksCount(int drinksCount) {
		this.drinksCount = drinksCount;
	}

	public double getWeight() {
		return weight;
	}

	public void setWeight(double weight) {
		this.weight = weight;
	}

	public Gender getGender() {
		return gender;
	}

	public void setGender(Gender gender) {
		this.gender = gender;
	}

	public double getLegalThreshold() {
		return legalThreshold;
	}

	public void setLegalThreshold(double legalThreshold) {
		this.legalThreshold = legalThreshold;
	}

	public void setDrinksCountView(TextView drinksCountView) {
		this.drinksCountView = drinksCountView;
	}

	public void setBacView(TextView bacView) {
		this.bacView = bacView;
	}

	public void setTimeLeftView(TextView timeLeftView) {
		this.timeLeftView = timeLeftView;
	}

	public void reset() {
		setDrinksCount(0);
		setDrinkStartTime(null);
		savePrefs();
		updateUI();
	}
	
}