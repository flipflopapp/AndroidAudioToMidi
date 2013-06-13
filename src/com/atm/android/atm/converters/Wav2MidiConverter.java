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

import com.atm.android.atm.midi.Note;
import java.util.ArrayList;
import java.util.Arrays;

/**
 * Converter for WAV files to MIDI notes
 * @author Max Kastanas
 */
public class Wav2MidiConverter {

    private String srcfilename;
    private String destfilename;
    //ATM Default Settings
    private int samplerate = 11025;
    private int channels = 1;
    private int bitspersample = 16;
    //Aubio Specific Algorithms and settings
    private float threshold = (float) 0.3;
    private float silence = (float) -90.;
    private float averaging = (float) 1.; //boolean 0,1
    private int buffer_size = 128; //1024;
    private int overlap_size = 256; //512;
    private String type_pitch = "yinfft"; // aubio_pitch_mcomb
    private String type_onset = "kl";
    private String type_onset2 = "complex";
    private Note[] Notes;

    /**
     * Constructor
     * Some devices don't support stereo recording
     * @param   wavfilename WAV file to be converted to MIDI notes
     */
    public Wav2MidiConverter(String wavfilename) {
        srcfilename = wavfilename;
    }

    /**
     * Constructor (Conversion to MIDI file is not supported yet)
     * Some devices don't support stereo recording
     * @param   srcfilename WAV file to be converted
     * @param   destfilename MIDI file to be produced
     * @see Note
     */
    public Wav2MidiConverter(String srcfilename, String destfilename) {
        this.srcfilename = srcfilename;
        this.destfilename = destfilename;
    }

    /**
     * Sets bits per sample
     * Some devices don't support stereo recording
     * @param   bitspersample bits per sample for audio conversion (ie 8,16 bits)
     */
    public void setBitspersample(int bitspersample) {
        this.bitspersample = bitspersample;
    }

    /**
     * Sets number of channels for WAV conversion
     * Some devices don't support stereo recording
     * @param   channels sample rate for audio conversion (ie 1,2)
     */
    public void setChannels(int channels) {
        this.channels = channels;
    }

    /**
     * Sets samplerate for WAV conversion
     * Some devices don't support all frequencies!
     * @param   samplerate sample rate for audio conversion (ie 8000,11025,22050,44100 Hz)
     */
    public void setSamplerate(int samplerate) {
        this.samplerate = samplerate;
    }

    /**
     * Sets buffer size for aubio processing
     * @param   buffer_size buffer size in bytes (ie 512)
     */
    public void setBuffer_size(int buffer_size) {
        this.buffer_size = buffer_size;
    }

    /**
     * Sets destination WAV file name to be converted. Operation not supported yet.
     * @param   destfilename A MIDI file
     */
    public void setDestfilename(String destfilename) {
        this.destfilename = destfilename;
    }

    /**
     * Sets silence for aubio processing
     * @param   overlap_size overlap size (ie 256), needs to be less than half of the buffer size
     */
    public void setOverlap_size(int overlap_size) {
        this.overlap_size = overlap_size;
    }

    /**
     * Sets silence for aubio processing
     * @param   silence silence level (ie -90)
     */
    public void setSilence(float silence) {
        this.silence = silence;
    }

    /**
     * Sets source WAV file name to be converted
     * @param   srcfilename A WAV file
     */
    public void setSrcfilename(String srcfilename) {
        this.srcfilename = srcfilename;
    }

    public void setThreshold(float threshold) {
        this.threshold = threshold;
    }

    /**
     * Sets averaging for aubio processing
     * @param   averaging averaging (1 for averaging)
     */
    public void setAveraging(float averaging) {
        this.averaging = averaging;
    }

    /**
     * Sets 1st pass algorithm for onset detection
     * @param   type_onset type can be ("energy","specdiff","hfc","complexdomain","complex","phase","mkl","kl")
     */
    public void setType_onset(String type_onset) {
        this.type_onset = type_onset;
    }

    /**
     * Sets 2nd pass algorithm for onset detection
     * @param   type_onset2 type can be ("None","energy","specdiff","hfc","complexdomain","complex","phase","mkl","kl")
     */
    public void setType_onset2(String type_onset2) {
        this.type_onset2 = type_onset2;
    }

    /**
     * Sets algorithm for pitch detection
     * @param   type_pitch type can be (mcomb,yinfft,yin,schmitt,fcomb)
     */
    public void setType_pitch(String type_pitch) {
        this.type_pitch = type_pitch;
    }

    /**
     * Converts the WAV file to Midi notes, do not use directly
     * @see wav2midiNotes()
     */
    protected native Note[] wav2midi();

    /**
     * Returns the MIDI notes detected
     * @return   ArrayList of MIDI Notes
     * @see Note
     */
    public ArrayList<Note> getNotes() {
        ArrayList<Note> notes = new ArrayList<Note>(Arrays.asList(Notes));
        return notes;
    }

    /**
     * Converts the WAV file to Midi notes
     * @return   ArrayList of MIDI Notes
     * @see Note
     */
    public void wav2midiNotes() {
        Notes = wav2midi();
    }
}
