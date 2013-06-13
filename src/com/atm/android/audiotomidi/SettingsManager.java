/*
Copyright (C) Max Kastanas 2010
Copyright (C) Naval Saini 2010

 * This program is free software; you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation; either version 2 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program; if not, write to the Free Software
 * Foundation, Inc., 59 Temple Place, Suite 330, Boston, MA  02111-1307  USA
 *
 */
package com.atm.android.audiotomidi;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.media.AudioFormat;
import android.os.Bundle;
import android.preference.PreferenceActivity;
import android.preference.PreferenceManager;

public class SettingsManager extends PreferenceActivity {

    static int getBitsPerSample(Activity activity) {
        int recFormat = AudioFormat.ENCODING_DEFAULT;
        int recFormatIndex = SettingsManager.getRecFormat(activity);
        switch (recFormatIndex) {
            case 0:
                //ENCODING DEFAULT fails for some devices using 16 Bit as default
                recFormat = AudioFormat.ENCODING_PCM_16BIT;
                break;
            case 1:
                recFormat = AudioFormat.ENCODING_PCM_8BIT;
                break;
            case 2:
                recFormat = AudioFormat.ENCODING_PCM_16BIT;
                break;
        }
        return recFormat;
    }

    static boolean getAveraging(Activity activity) {
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(activity);
        return prefs.getBoolean("averagingPref", true);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Intent data = new Intent();
        setResult(Const.SETTINGS_RETURN_CODE, data);
        addPrefs();
    }

    public static int getSynthInstrument(Activity activity) {

        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(activity);
        return Integer.parseInt(prefs.getString("synthInstrumentPref", "65"));

    }

    public static int getRecFreq(Activity activity) {

        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(activity);
        return Integer.parseInt(prefs.getString("recFreqPref", "11025"));

    }

    public static int getRecFormat(Activity activity) {

        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(activity);
        return Integer.parseInt(prefs.getString("recFormatPref", "0"));

    }

    public static int getRecChannel(Activity activity) {

        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(activity);
        return Integer.parseInt(prefs.getString("recChanPref", "1"));

    }

    public static int getOverlapSize(Activity activity) {

        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(activity);
        return Integer.parseInt(prefs.getString("overlapSizePref", "256"));

    }

    public static int getBufferSize(Activity activity) {

        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(activity);
        return Integer.parseInt(prefs.getString("bufferSizePref", "128"));

    }

    public static String getPitchType(Activity activity) {

        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(activity);
        return prefs.getString("pitchTypePref", "yinfft");

    }

    public static String getOnsetType(Activity activity) {

        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(activity);
        return prefs.getString("onsetTypePref", "kl");

    }

    public static String getOnsetType2(Activity activity) {

        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(activity);
        return prefs.getString("onsetType2Pref", "complex");

    }
    
    public static float getThreshold(Activity activity) {

        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(activity);
        return Float.parseFloat(prefs.getString("thresholdPref", "0.30"));

    }

    public static float getSilence(Activity activity) {

        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(activity);
        return Float.parseFloat(prefs.getString("silencePref", "-90.0"));

    }

    public static boolean getShowNoteLetter(Activity activity) {

        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(activity);
        return prefs.getBoolean("showNoteLettersOnPref", true);
    }

    public void addPrefs() {
        addPreferencesFromResource(R.xml.settings);
    }
}
