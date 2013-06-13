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
package com.atm.android.atm.raw;

import java.io.BufferedOutputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import android.media.AudioFormat;
import android.media.AudioRecord;
import android.media.MediaRecorder;

/**
 * Raw PCM recorder
 * @author Max Kastanas
 */
public class Recorder implements Runnable {

    private volatile boolean isPaused;
    private File fileName;
    private volatile boolean isRecording;
    private final Object mutex = new Object();
// Changing the sample resolution changes sample type. byte vs. short.
    //Default settings
    private int audioEncoding = AudioFormat.ENCODING_PCM_16BIT;
    private int frequency = 11025;
    private int channelConfiguration = AudioFormat.CHANNEL_CONFIGURATION_MONO;

    /**
     * Set Audio Encoding
     * @param audioEncoding (ie ENCODING_PCM_8BIT,ENCODING_PCM_16BIT)
     * @see AudioFormat
     */
    public void setAudioEncoding(int audioEncoding) {
        this.audioEncoding = audioEncoding;
    }

    /**
     * Constructor
     * @param audioEncoding (ie ENCODING_PCM_8BIT,ENCODING_PCM_16BIT)
     * @param frequency (ie 8000,11025,22050,44100 Hz)
     * @param channelConfiguration MONO, STEREO (ie CHANNEL_CONFIGURATION_MONO,CHANNEL_CONFIGURATION_STEREO)
     * @see AudioFormat
     */
    public Recorder(int audioEncoding, int frequency, int channelConfiguration) {
        super();
        this.setFrequency(frequency);
        this.setChannelConfiguration(channelConfiguration);
        this.setAudioEncoding(audioEncoding);
        this.setPaused(false);
    }

    /**
     * Start Recording from microphone in high priority
     */
    public void run() {
// Wait until we’re recording…
        synchronized (mutex) {
            while (!this.isRecording) {
                try {
                    mutex.wait();
                } catch (InterruptedException e) {
                    throw new IllegalStateException("Wait() interrupted!", e);
                }
            }
        }

// Open output stream…
        if (this.fileName == null) {
            throw new IllegalStateException("fileName is            null");
        }
        BufferedOutputStream bufferedStreamInstance = null;
        if (fileName.exists()) {
            fileName.delete();
        }
        try {
            fileName.createNewFile();
        } catch (IOException e) {
            throw new IllegalStateException("Cannot create file: " + fileName.toString());
        }
        try {
            bufferedStreamInstance = new BufferedOutputStream(
                    new FileOutputStream(this.fileName));
        } catch (FileNotFoundException e) {
            throw new IllegalStateException(
                    "Cannot Open File", e);

        }
        DataOutputStream dataOutputStreamInstance =
                new DataOutputStream(bufferedStreamInstance);

// We’re important…
        android.os.Process.setThreadPriority(android.os.Process.THREAD_PRIORITY_URGENT_AUDIO);

// Allocate Recorder and Start Recording…
        int bufferRead = 0;
        int bufferSize = AudioRecord.getMinBufferSize(this.getFrequency(),
                this.getChannelConfiguration(), this.getAudioEncoding());
        AudioRecord recordInstance = new AudioRecord(
                MediaRecorder.AudioSource.MIC, this.getFrequency(), this.getChannelConfiguration(), this.getAudioEncoding(),
                bufferSize);
        short[] tempBuffer = new short[bufferSize];
        recordInstance.startRecording();
        while (this.isRecording) {
// Are we paused?
            synchronized (mutex) {
                if (this.isPaused) {
                    try {
                        mutex.wait(250);
                    } catch (InterruptedException e) {
                        throw new IllegalStateException("Wait() interrupted!", e);
                    }


                    continue;
                }
            }

            bufferRead = recordInstance.read(tempBuffer, 0, bufferSize);
            if (bufferRead == AudioRecord.ERROR_INVALID_OPERATION) {
                throw new IllegalStateException("                read() returned AudioRecord.ERROR_INVALID_OPERATION");
            } else if (bufferRead == AudioRecord.ERROR_BAD_VALUE) {
                throw new IllegalStateException(
                        "read() returned AudioRecord.ERROR_BAD_VALUE");
            } else if (bufferRead == AudioRecord.ERROR_INVALID_OPERATION) {
                throw new IllegalStateException(
                        "read() returned AudioRecord.ERROR_INVALID_OPERATION");
            }
            try {
                for (int idxBuffer = 0; idxBuffer < bufferRead; ++idxBuffer) {
                    dataOutputStreamInstance.writeShort(tempBuffer[idxBuffer]);
                }
            } catch (IOException e) {
                throw new IllegalStateException("dataOutputStreamInstance.writeShort(curVal)");
            }

        }
        recordInstance.stop();

// Close resources…
        recordInstance.stop();
        try {
            bufferedStreamInstance.close();
        } catch (IOException e) {
            throw new IllegalStateException(
                    "Cannot close buffered writer.");
        }
    }

    /**
     * Constructor
     * @param fileName File path to a PCM filename to be produced
     */
    public void setFileName(File fileName) {
        this.fileName = fileName;
    }

    /**
     * Constructor
     * @return Current file path to the PCM filename to be produced
     */
    public File getFileName() {
        return fileName;
    }

    /**
     * @param isRecording if currently recording
     */
    public void setRecording(boolean isRecording) {
        synchronized (mutex) {
            this.isRecording = isRecording;
            if (this.isRecording) {
                mutex.notify();
            }
        }
    }

    /**
     * @return true if currently recording
     */
    public boolean isRecording() {
        synchronized (mutex) {
            return isRecording;
        }
    }

    /**
     * @param frequency Set frequency to record with (ie 8000,11025,22050,44100 Hz)
     */
    public void setFrequency(int frequency) {
        this.frequency = frequency;
    }

    /**
     * @return current frequency to record with (ie 8000,11025,22050,44100 Hz)
     */
    public int getFrequency() {
        return frequency;
    }

    /**
     * @param channelConfiguration (ie CHANNEL_CONFIGURATION_MONO,CHANNEL_CONFIGURATION_STEREO)
     * @see AudioFormat
     */
    public void setChannelConfiguration(int channelConfiguration) {
        this.channelConfiguration = channelConfiguration;
    }

    /**
     * @return Current channel configuration (ie CHANNEL_CONFIGURATION_MONO,CHANNEL_CONFIGURATION_STEREO)
     * @see AudioFormat
     */
    public int getChannelConfiguration() {
        return channelConfiguration;
    }

    /**
     * @param audioEncoding (ie ENCODING_PCM_8BIT,ENCODING_PCM_16BIT)
     */
    public int getAudioEncoding() {
        return audioEncoding;
    }

    /**
     * @param isPaused
     *            the isPaused to set
     */
    public void setPaused(boolean isPaused) {
        synchronized (mutex) {
            this.isPaused = isPaused;
        }
    }

    /**
     * @return true if Recording is paused
     */
    public boolean isPaused() {
        synchronized (mutex) {
            return isPaused;
        }
    }

    /**
     * @return Stop Recording
     */
    public void stop() {
//        throw new UnsupportedOperationException("Not yet implemented");
    }
}
