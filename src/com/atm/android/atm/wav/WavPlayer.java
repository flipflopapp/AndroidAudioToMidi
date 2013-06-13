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
package com.atm.android.atm.wav;

import android.media.AudioManager;
import android.media.MediaPlayer;
import android.util.Log;

/**
 * @author Max Kastanas
 * Class is to be used for WAV playback
 */
public class WavPlayer extends MediaPlayer {

    /**
     * Set WAV file for playback
     */
    public WavPlayer(String filepath) {
        try {
            setAudioStreamType(AudioManager.STREAM_MUSIC);
            setDataSource(filepath);
            prepare();
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    /**
     * Start Playback
     */
    public void playWav() {

        start();
    }

    /**
     * Stop player and release resources
     */
    public void stopWav() {

        if (this != null) {
            try {
                stop();
                this.closeWav();
            } catch (Exception ex) {
                Log.v("Player", "Cannot stop...");
            }

        }


    }

    /**
     * Close player and release resources
     */
    public void closeWav() throws Exception {
//        UIUtils.log("Closing Midi");
        if (isPlaying() || isLooping()) {
            stop();
        }
        release();
    }
}
