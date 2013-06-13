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
import android.os.Bundle;
import android.util.Log;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.widget.TextView;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.PackageManager.NameNotFoundException;
import android.graphics.Color;
import android.media.AudioFormat;
import android.os.AsyncTask;
import android.os.Handler;
import android.os.Message;
import android.util.AttributeSet;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.webkit.WebView;
import android.widget.ImageButton;
import android.widget.RelativeLayout;
import android.widget.ScrollView;
import android.widget.Toast;
import com.atm.android.atm.midi.MidiPlayer;
import com.atm.android.atm.midi.Note;
import com.atm.android.atm.converters.Pcm2WavConverter;
import com.atm.android.atm.raw.Recorder;
import com.atm.android.atm.converters.Wav2MidiConverter;
import com.atm.android.atm.wav.WavPlayer;
import com.atm.android.audiotomidi.utils.FileInstaller;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;

public class AudioToMidiDemo extends Activity {

    public Activity activity = this;
    private static final int SETTINGS = 1;
    private static final int HELP = 2;
    private static final int QUIT = 3;
    private int WIDGET_ID_PARENT = 10;
    private int WIDGET_ID_STOP = 11;
    private int WIDGET_ID_PLAY = 1223;
    private int WIDGET_ID_LYRICS = 13;
    private int WIDGET_ID_LYRICS_SCROLL = 14;
    public View parent;
    public TextView mLyrics;
    public ProgressDialog progDialog;
    public ImageButton mPlay;
    public ImageButton mRecord;
    public RelativeLayout mLayout;
    public ImageButton mPlayMidi;
    private Recorder recorderInstance;
    private WavPlayer wavPlayer;
    private MidiPlayer midiPlayer;
    private boolean quit = false;
    public ArrayList<Note> myNotes;
    public boolean verbose = true;
    public String pcmfilename = Const.basefiledir + "/" + Const.pcmfilenamefile;
    public String wavfilename = Const.basefiledir + "/" + Const.wavfilenamefile;
    public String midinotefilename = Const.basefiledir + "/" + Const.midinotefilename;
    private Wav2MidiConverter wav2midiconverter;
    private Pcm2WavConverter pcm2wavconverter;
    private String notes;

    public static void UIAlert(String title, String body, Activity activity) {
        AlertDialog ad;
        ad = new AlertDialog.Builder(activity).create();
        ad.setTitle(title);
        ad.setMessage(body);
        ad.setButton("OK", new DialogInterface.OnClickListener() {

            public void onClick(DialogInterface dialog, int which) {
                return;
            }
        });
        ad.show();
    }
    private int synthInstrument = 0;

    /** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        this.onSelectMenuAbout();

        progDialog = ProgressDialog.show(this, "Installer", "Installing files...", true);
        Init s = new Init();
        s.execute();


        //Setup UI
        this.setupWidgets();
        this.createUI();

    }

    private class Init extends AsyncTask<Void, Void, Void> {

        @Override
        protected Void doInBackground(Void... arg0) {

            //Install libs
            loadNativeLibs();

            //Install FIles
            FileInstaller.installFiles(activity);

            //Init ATM Converters
            pcm2wavconverter = new Pcm2WavConverter(pcmfilename, wavfilename);

            //TODO: Support MIDI file format output
            //For now notes are retrieved via JNI method
            wav2midiconverter = new Wav2MidiConverter(wavfilename);

            //Convert Audio
            convertTestWavFile();


            return null;
        }

        @Override
        protected void onPostExecute(Void test) {
            sendHandlerMessage(handler, RESULT_OK);
        }
    }

    private class Reloader extends AsyncTask<Void, Void, Void> {

        @Override
        protected Void doInBackground(Void... arg0) {
            convertTestWavFile();
            FileInstaller.installSynthFiles(activity, (byte) synthInstrument);
            return null;
        }

        @Override
        protected void onPostExecute(Void test) {
            sendHandlerMessage(handler, RESULT_OK);
        }
    }

    private void convertTestWavFile() {
        setWav2MidiParams();

        //Convert default Wav to Midi
        wav2midiconverter.wav2midiNotes();

        //Populate notes from default file test.wav
        populateMidiNotes();

        //Init Players
        initWavPlayer(wavfilename);

        //TODO: Support MIDI file format output
        // and reproduction via Android Media Player
        //For now we use a custom player
        initMidiPlayer(midinotefilename);
    }

    private void populateMidiNotes() {

        this.myNotes = this.wav2midiconverter.getNotes();

        Iterator iter = myNotes.iterator();

        while (iter.hasNext()) {
            Note note = (Note) iter.next();
            if (note == null) {
                return;
            }
            notes += (note.note + "," + note.vel + "," + note.time + "\n");
        }

    }

    private class RecordWav extends AsyncTask<Void, Void, Void> {

        @Override
        protected Void doInBackground(Void... arg0) {
            // Record 5 seconds of audio.

            recorderInstance = new Recorder(
                    SettingsManager.getBitsPerSample(activity),
                    SettingsManager.getRecFreq(activity),
                    SettingsManager.getRecChannel(activity));

            Thread th = new Thread(recorderInstance);
            recorderInstance.setFileName(new File(Const.basefiledir + "/" + Const.pcmfilenamefile));
            th.start();
            recorderInstance.setRecording(true);
            synchronized (this) {
                try {
                    this.wait(5000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }

            }
            recorderInstance.setRecording(false);
            try {
                th.join();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            sendHandlerMessage(handler, RESULT_OK);

            return null;
        }

        @Override
        protected void onPostExecute(Void test) {
            convertPCMToMidi();
            initWavPlayer(Const.basefiledir + "/" + Const.wavfilenamefile);
            initMidiPlayer(Const.basefiledir + "/" + Const.midinotefilename);
        }
    }

    public void convertPCMToMidi() {
        progDialog = ProgressDialog.show(this, "Processing Audio file", "Detecting notes from test.wav", true);
        ProcessWav s = new ProcessWav();
        s.execute();
    }

    private class ProcessWav extends AsyncTask<Void, Void, Void> {

        @Override
        protected Void doInBackground(Void... arg0) {
            //Converting PCM to Midi
            Log.v("Process Recording", "COnverting PCM to Midi");
            convertPCM2Midi();

            //Populating Midi Notes
            Log.v("Process Recording", "Populate Midi Notes");
            populateMidiNotes();
            sendHandlerMessage(handler, RESULT_OK);

            return null;
        }

        @Override
        protected void onPostExecute(Void test) {
        }
    }

    public static void sendHandlerMessage(Handler handler, int message_type) {
        Message msg1 = handler.obtainMessage();
        Bundle b = new Bundle();
        b.putInt("message_type", message_type);
        msg1.setData(b);
        handler.sendMessage(msg1);
    }

    private class PlayMidi extends AsyncTask<Void, Void, Void> {

        @Override
        protected Void doInBackground(Void... arg0) {

            //UIUtils.log("Starting event listener thread for track: " + track);
            midiPlayer.playMidi(myNotes);

            sendHandlerMessage(handler, RESULT_OK);
            return null;
        }

        @Override
        protected void onPostExecute(Void test) {
        }
    }

    public class AutoScrollView extends ScrollView {

        public AutoScrollView(Context context, AttributeSet attrs) {
            super(context, attrs);
        }

        public AutoScrollView(Context context) {
            super(context);
        }
    }
    public AutoScrollView mLyricsScroll;

    private void onRecordButton() {
        this.recordWav();
    }

    private void onPlayMidiButton() {

        progDialog = ProgressDialog.show(this, "MIDI Player", "Playing synthesized MIDI...", true);
        PlayMidi s = new PlayMidi();
        s.execute();





    }

    private void onStartButton() {
        wavPlayer.playWav();
        Toast t = Toast.makeText(this, "Playing PCM WAV Audio File", 0);
        t.show();
    }

    public void setupWidgets() {

        mRecord = new ImageButton(this);
        mRecord.setBackgroundColor(Color.BLACK);
        mRecord.setVisibility(View.VISIBLE);

        mPlay = new ImageButton(this);
        mPlay.setBackgroundColor(Color.BLACK);
        mPlay.setVisibility(View.VISIBLE);

        mPlayMidi = new ImageButton(this);
        mPlayMidi.setBackgroundColor(Color.BLACK);
        mPlayMidi.setVisibility(View.VISIBLE);


        // Get our EditText object.
        mLyricsScroll = new AutoScrollView(this);
        mLyricsScroll.setHorizontalFadingEdgeEnabled(false);
        mLyricsScroll.setVerticalFadingEdgeEnabled(false);
        mLyricsScroll.setFocusable(false);
        mLyricsScroll.setFocusableInTouchMode(false);

        mLyrics = new TextView(this);
        mLyrics.setBackgroundColor(Color.BLACK);
        mLyrics.setVisibility(View.VISIBLE);
        mLyrics.setVerticalFadingEdgeEnabled(false);
        mLyrics.setHorizontalFadingEdgeEnabled(false);
        mLyrics.setFocusable(false);
        mLyrics.setFocusableInTouchMode(false);
        mLyrics.setTextSize(20);


        mPlay.setOnClickListener(new OnClickListener() {

            public void onClick(View view) {
                onStartButton();

            }
        });


        mRecord.setOnClickListener(new OnClickListener() {

            public void onClick(View view) {

                onRecordButton();
            }
        });


        mPlayMidi.setOnClickListener(new OnClickListener() {

            public void onClick(View view) {


                onPlayMidiButton();
            }
        });


    }
    // Define the Handler that receives messages from the thread and update the progress
    public Handler handler = new Handler() {

        @Override
        public synchronized void handleMessage(Message msg) {
            Bundle b = msg.getData();
            if (progDialog != null) {
                progDialog.dismiss();
            }
            mLyrics.setText("Note, Volume, Time\n");
            if (notes != null) {
                mLyrics.setText(mLyrics.getText() + notes);
            }
        }
    };

    public void createUI() {
        if (mLayout != null) {
            mLayout.removeAllViews();
        }
        mLayout = new RelativeLayout(this);
        //Parent Screen
        parent = new View(this);
        RelativeLayout.LayoutParams parentViewParams = new RelativeLayout.LayoutParams(
                RelativeLayout.LayoutParams.FILL_PARENT,
                RelativeLayout.LayoutParams.FILL_PARENT);
        mLayout.addView(parent, parentViewParams);
        parent.setId(this.WIDGET_ID_PARENT);

        //Setup Icons
        mPlay.setImageResource(R.drawable.play_32x32);
        mPlayMidi.setImageResource(R.drawable.saxophone_32x32);
        mRecord.setImageResource(R.drawable.mic_32x32);



        //Play Button
        RelativeLayout.LayoutParams mPlayParams = new RelativeLayout.LayoutParams(
                RelativeLayout.LayoutParams.WRAP_CONTENT, RelativeLayout.LayoutParams.WRAP_CONTENT);
        mPlayParams.addRule(RelativeLayout.ALIGN_PARENT_BOTTOM, parent.getId());
        mPlayParams.addRule(RelativeLayout.CENTER_HORIZONTAL, parent.getId());
        mLayout.addView(mPlay, mPlayParams);
        mPlay.setId(WIDGET_ID_PLAY);

////        //Stop Button
//        UIUtils.log("Play width: " + this.mPlay.getWidth());
        RelativeLayout.LayoutParams mPlayMidiParams = new RelativeLayout.LayoutParams(
                RelativeLayout.LayoutParams.WRAP_CONTENT, RelativeLayout.LayoutParams.WRAP_CONTENT);
        mPlayMidiParams.addRule(RelativeLayout.ALIGN_PARENT_BOTTOM, parent.getId());
        mPlayMidiParams.addRule(RelativeLayout.RIGHT_OF, mPlay.getId());
        mLayout.addView(mPlayMidi, mPlayMidiParams);
        mPlayMidi.setId(this.WIDGET_ID_STOP);

        RelativeLayout.LayoutParams mRecordParams = new RelativeLayout.LayoutParams(
                RelativeLayout.LayoutParams.WRAP_CONTENT, RelativeLayout.LayoutParams.WRAP_CONTENT);
        mRecordParams.addRule(RelativeLayout.ALIGN_PARENT_BOTTOM, parent.getId());
        mRecordParams.addRule(RelativeLayout.LEFT_OF, mPlay.getId());
        mLayout.addView(mRecord, mRecordParams);
        mRecord.setId(this.WIDGET_ID_STOP);

        RelativeLayout.LayoutParams mLyricsParams = new RelativeLayout.LayoutParams(
                RelativeLayout.LayoutParams.FILL_PARENT,
                RelativeLayout.LayoutParams.FILL_PARENT);

        mLyricsScroll.removeAllViews();
        mLyricsScroll.addView(mLyrics, mLyricsParams);
        mLyricsScroll.setPadding(15, 0, 5, 0);
        mLyrics.setId(this.WIDGET_ID_LYRICS);

        RelativeLayout.LayoutParams mLyricsScrollParams = new RelativeLayout.LayoutParams(
                RelativeLayout.LayoutParams.FILL_PARENT,
                RelativeLayout.LayoutParams.FILL_PARENT);
        mLyricsScrollParams.addRule(RelativeLayout.ABOVE, mPlay.getId());
        mLayout.addView(mLyricsScroll, mLyricsScrollParams);
        mLyricsScroll.setId(WIDGET_ID_LYRICS_SCROLL);

        setContentView(mLayout);
    }

    @Override
    public void onStop() {
        super.onStop();
        if (recorderInstance != null) {
            recorderInstance.stop();
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        synchronized (this) {
            this.quit = true;
            this.notifyAll();
        }

    }

    private void loadNativeLibs() {
        loadNativeLib("libatm.so", "/data/data/com.atm.android.audiotomidi/lib");

    }

    private void loadNativeLib(String lib, String destDir) {
        if (true) {
            String libLocation = destDir + "/" + lib;
            try {
                System.load(libLocation);
            } catch (Exception ex) {
                Log.e("JNIExample", "failed to load native library: " + ex);
            }
        }

    }

    public synchronized void recordWav() {
        progDialog = ProgressDialog.show(this, "Recording", "Recording from mic", true);
        RecordWav r = new RecordWav();
        r.execute();
    }

    public void convertPCM2Midi() {
        Log.v("Process Recording", "Converting PCM to WAV");
        this.setPcm2WavParams();

        Log.v("Process Recording", "Converting WAV to MIDI");
        this.setWav2MidiParams();
        wav2midiconverter.wav2midiNotes();
    }

    private void setPcm2WavParams() {
        int bitsPerSampleInt = SettingsManager.getBitsPerSample(activity);
        int bitsPerSample = 16;
        switch (bitsPerSampleInt) {
            case AudioFormat.ENCODING_DEFAULT:
                bitsPerSample = 16;
                break;
            case AudioFormat.ENCODING_PCM_8BIT:
                bitsPerSample = 8;
                break;
            case AudioFormat.ENCODING_PCM_16BIT:
                bitsPerSample = 16;
                break;

        }
        pcm2wavconverter.setBitPerSample(bitsPerSample);
        pcm2wavconverter.setChannels(SettingsManager.getRecChannel(activity));
        pcm2wavconverter.setSamplerate(SettingsManager.getRecFreq(activity));
        pcm2wavconverter.convertPcm2wav();

    }

    private void setWav2MidiParams() {
        int bitsPerSampleInt = SettingsManager.getBitsPerSample(activity);
        int bitsPerSample = 16;
        switch (bitsPerSampleInt) {
            case AudioFormat.ENCODING_DEFAULT:
                bitsPerSample = 16;
                break;
            case AudioFormat.ENCODING_PCM_8BIT:
                bitsPerSample = 8;
                break;
            case AudioFormat.ENCODING_PCM_16BIT:
                bitsPerSample = 16;
                break;

        }

        wav2midiconverter.setBitspersample(bitsPerSample);
        wav2midiconverter.setChannels(SettingsManager.getRecChannel(activity));
        wav2midiconverter.setSamplerate(SettingsManager.getRecFreq(activity));

        //Set Engine Params
        wav2midiconverter.setBuffer_size(SettingsManager.getBufferSize(activity));
        wav2midiconverter.setOverlap_size(SettingsManager.getOverlapSize(activity));
        wav2midiconverter.setSilence(SettingsManager.getSilence(activity));
        wav2midiconverter.setThreshold(SettingsManager.getThreshold(activity));
        wav2midiconverter.setType_onset(SettingsManager.getOnsetType(activity));
        wav2midiconverter.setType_onset2(SettingsManager.getOnsetType2(activity));
        wav2midiconverter.setType_pitch(SettingsManager.getPitchType(activity));
        if (SettingsManager.getAveraging(activity)) {
            wav2midiconverter.setAveraging((float) 1.0);
        } else {
            wav2midiconverter.setAveraging((float) 0);
        }



    }

    public void initWavPlayer(String filepath) {
        if (wavPlayer != null) {
            if (wavPlayer.isPlaying() || wavPlayer.isLooping()) {
                wavPlayer.stop();
            }
        } else {
            wavPlayer = new WavPlayer(filepath);
        }
    }

    public void initMidiPlayer(String filepath) {
        if (midiPlayer != null) {
        } else {
            midiPlayer = new MidiPlayer(filepath);
        }

    }

    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {
        menu.clear();
        menu.add(0, SETTINGS, 0, "Settings").setIcon(android.R.drawable.ic_menu_preferences);
        menu.add(0, HELP, 0, "About").setIcon(android.R.drawable.ic_menu_help);
        menu.add(0, QUIT, 0, "Exit").setIcon(android.R.drawable.ic_lock_power_off);
        return true;
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == Const.SETTINGS_RETURN_CODE) {
            //Read from activity
            int progNum = 0;
            progNum = SettingsManager.getSynthInstrument(activity);

            this.synthInstrument = progNum;

            //Async task
            progDialog = ProgressDialog.show(this, "Please Wait", "Reloading", true);
            Reloader s = new Reloader();
            s.execute();

        }
    }

    public void onSelectMenuSettings() {
        Intent SettingsActivity = getIntentSettings();
        startActivityForResult(SettingsActivity, Const.SETTINGS_REQUEST_CODE);
    }

    public void menuListener(int c) {
        if (c == QUIT) {
            this.finish();
        } else if (c == HELP) {
            this.onSelectMenuAbout();
        } else if (c == SETTINGS) {
            this.onSelectMenuSettings();
        }
    }

    @Override
    public boolean onOptionsItemSelected(final MenuItem item) {
        menuListener(item.getItemId());
        return true;
    }

    public void onSelectMenuAbout() {
        PackageInfo pInfo = null;

        try {
            pInfo = activity.getPackageManager().getPackageInfo(activity.getClass().getPackage().getName(), PackageManager.GET_META_DATA);
        } catch (NameNotFoundException e) {
            e.printStackTrace();
        }

        String VERSION_NAME = pInfo.versionName;
        AlertDialog alertDialog;
        alertDialog = new AlertDialog.Builder(this).create();
        alertDialog.setTitle("Audio To Midi Demo v" + VERSION_NAME);
        WebView webview = new WebView(activity);
        webview.setBackgroundColor(Color.BLACK);
        String about = ""
                + "This is a demo of ATM (Audio To Midi) library for Android phones "
                + "based on aubio, fftw, libsndfile, and libsamplerate libraries. <br>"
                + "<br>"
                + "This program comes with ABSOLUTELY NO WARRANTY; for details see license below."
                + " This is free software, and you are welcome to redistribute it "
                + "under certain condititions; see license below."
                + "<br>"
                + "<br><br>This software is released under GPL license: "
                + "<a href=\"http://www.gnu.org/licenses/gpl-2.0.html\"> <font color=\"red\"> http://www.gnu.org/licenses/gpl-2.0.html</font></a>"
                + "<br><br>Source code can be found in: "
                + "<br><a href=\"http://code.google.com/p/audio2midi\"> <font color=\"red\"> code.google.com/audio2midi </font></a>";
        webview.loadData("<font color=\"FFFFFF\">" + about + " </font>", "text/html", "UTF-8");
        alertDialog.setView(webview);
        alertDialog.setButton("OK", new DialogInterface.OnClickListener() {

            public void onClick(DialogInterface di, int i) {
                return;
            }
        });
        alertDialog.show();
    }

    public Intent getIntentSettings() {
        return new Intent(getBaseContext(),
                com.atm.android.audiotomidi.SettingsManager.class);
    }
}
