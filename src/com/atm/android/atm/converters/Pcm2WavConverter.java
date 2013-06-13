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
package com.atm.android.atm.converters;

/**
 * Class is to be used for raw PCM files to WAV audio for later processing.
 * @author Max Kastanas
 */
public class Pcm2WavConverter {

    private final String srcfilename;
    private final String destfilename;
    //Default Settings
    private int samplerate = 11025;
    private int channels = 1;
    private int bitspersample = 16;

    /**
     * Constructor
     * @return  A converter object
     * @param   srcfilename A source PCM file
     * @param   destination A destination WAV file.
     */
    public Pcm2WavConverter(String srcfilename, String destfilename) {
        this.srcfilename = srcfilename;
        this.destfilename = destfilename;
    }

    /**
     * Sets bits per sample
     * Some devices don't support stereo recording
     * @param   bitspersample bits per sample for audio conversion (ie 8,16 bits)
     */
    public void setBitPerSample(int bitspersample) {
        this.bitspersample = bitspersample;
    }

    /**
     * Sets number of channels for PCM conversion
     * Some devices don't support stereo recording
     * @param   channels sample rate for audio conversion (ie 1,2)
     */
    public void setChannels(int channels) {
        this.channels = channels;
    }

    /**
     * Sets samplerate for PCM conversion
     * Some devices don't support all frequencies!
     * @param   samplerate sample rate for audio conversion (ie 8000,11025,22050,44100 Hz)
     */
    public void setSamplerate(int samplerate) {
        this.samplerate = samplerate;
    }

    /**
     * JNI interface for converting PCM file to a WAV file
     */
    protected native String pcm2wav();

    /**
     * Converting the PCM file to a WAV file
     */
    public void convertPcm2wav() {
        this.pcm2wav();
    }
}
