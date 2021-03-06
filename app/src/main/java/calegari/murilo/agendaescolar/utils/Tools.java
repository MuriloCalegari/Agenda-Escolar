package calegari.murilo.agendaescolar.utils;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.res.Resources;
import android.graphics.Color;


import androidx.preference.PreferenceManager;
import calegari.murilo.agendaescolar.R;

public abstract class Tools {

	public static int getGradeColor(float obtainedGrade, float maximumGrade, Context context) {
		int dangerColor = context.getResources().getColor(R.color.slimchart_danger_color);
		int warningColor = context.getResources().getColor(R.color.slimchart_warning_color);
		int okColor = context.getResources().getColor(R.color.slimchart_ok_color);

		SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(context);
		int dangerGradePercentage = sharedPreferences.getInt("minimumPercentage",60);
		int DANGER_WARNING_THRESHOLD = 10;
		int warningGradePercentage = dangerGradePercentage + DANGER_WARNING_THRESHOLD;

		float averageGradePercentage = obtainedGrade / maximumGrade * 100;

		if (averageGradePercentage >= 100 || averageGradePercentage >= warningGradePercentage) {
			return okColor;
		} else if (averageGradePercentage >= dangerGradePercentage) {
			return warningColor;
		} else {
			return dangerColor;
		}
	}

	public static int getRandomColorFromArray(int resId, Context context) {
		final String[] colorArray = context.getResources().getStringArray(resId);
		return Color.parseColor(colorArray[(int) Math.round(Math.random() * (colorArray.length - 1))]);
	}

}
