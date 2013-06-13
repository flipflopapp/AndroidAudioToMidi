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
package com.atm.android.atm.midi;

import android.media.AudioManager;
import android.media.MediaPlayer;
import android.media.MediaPlayer.OnCompletionListener;
import android.util.Log;
import java.io.IOException;
import java.util.ArrayList;
import java.util.ListIterator;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Simple MIDI playback class
 * @author Max Kastanas
 */
public class MidiPlayer implements OnCompletionListener {

    private boolean quit = false;
    private String baseFilename = "";

    /**
     * Constructor
     * @param A base MIDI file note, see Assets folder for sample MIDI note file
     */
    public MidiPlayer(String basefilename) {
        this.baseFilename = basefilename;
    }

    /**
     * Playback of MidiNotes
     * @param ArrayList of Notes
     * @see Note
     */
    public synchronized void playMidi(ArrayList<Note> notes) {
        if (notes == null) {
            return;
        }
        quit = false;
        ListIterator e = notes.listIterator();
        Note mynote = null;
        float totTime = 0;
        int previousNote = 0;
        System.gc();
        synchronized (this) {
            while (e.hasNext() && !quit) {
                mynote = (Note) e.next();
                if (mynote == null) {
                    return;
                }
                try {
                    Log.v("Player", "Playing note: " + mynote.note + ", time: " + mynote.time);
                    this.wait((int) ((mynote.time - totTime) * 1000));
                    totTime = mynote.time;
                } catch (InterruptedException ex) {
                    Logger.getLogger(MidiPlayer.class.getName()).log(Level.SEVERE, null, ex);
                }
                if (previousNote != mynote.note) {
                    try {
                        playNote(mynote.note);
                    } catch (IOException ex) {
                        Logger.getLogger(MidiPlayer.class.getName()).log(Level.SEVERE, null, ex);
                    }

                }
                previousNote = mynote.note;
            }
        }

    }

    /**
     * Play Individual Midi Note 
     * @param note (ie 1-127)
     */
    public void playNote(int note) throws IOException {
        MediaPlayer MidiPlayer = new MediaPlayer();
        MidiPlayer.setAudioStreamType(AudioManager.STREAM_MUSIC);
//        Log.v("Midi Player", "Playing note: " + baseFilename + note + ".mid");
        MidiPlayer.setDataSource(baseFilename + note + ".mid");
        MidiPlayer.prepare();
        MidiPlayer.setVolume(1, 1);
        MidiPlayer.start();
    }

    /**
     * On Completion of MIDI playback
     * @see MediaPlayer
     */
    public void onCompletion(MediaPlayer midiPlayer) {

        Log.v("Player", "Closing Midi");
        if (midiPlayer != null) {
            if (midiPlayer.isPlaying() || midiPlayer.isLooping()) {
                midiPlayer.stop();
            }
            midiPlayer.release();
        }
        midiPlayer = null;


    }
}
