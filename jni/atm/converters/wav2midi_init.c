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

#include "wav2midi_init.h"

jclass Note;
jmethodID midCtor;
JNIEnv* JNIenv;
jobjectArray notesArray;
int counter = 0;

void wav2midi_init(JNIEnv* env, jobject thiz) {
    LOGV("Aubio init");
    JNIenv = env;
    counter = 0;
    const char * type_pitch_str;
    const char * type_onset_str;
    /* Read the member values from the Java Object that called sendSPAPacket() method
     */
    jclass c = (*env)->GetObjectClass(env, thiz);
    jfieldID fid = (*env)->GetFieldID(env, c, "srcfilename", "Ljava/lang/String;");
    jstring jaccess = (*env)->GetObjectField(env, thiz, fid);
    input_filename = (*env)->GetStringUTFChars(env, jaccess, 0);
    LOGD("Setting Input File: %s", input_filename);


    c = (*env)->GetObjectClass(env, thiz);
    fid = (*env)->GetFieldID(env, c, "type_pitch", "Ljava/lang/String;");
    jaccess = (*env)->GetObjectField(env, thiz, fid);
    type_pitch_str = (*env)->GetStringUTFChars(env, jaccess, 0);
    LOGD("Setting Aubio Pitch Algorithm: %s", type_pitch_str);
    set_pitch_type(type_pitch_str);

    c = (*env)->GetObjectClass(env, thiz);
    fid = (*env)->GetFieldID(env, c, "type_onset", "Ljava/lang/String;");
    jaccess = (*env)->GetObjectField(env, thiz, fid);
    type_onset_str = (*env)->GetStringUTFChars(env, jaccess, 0);
    LOGD("Setting Aubio Onset Algorithm: %s", type_onset_str);
    set_onset_type(type_onset_str);

    c = (*env)->GetObjectClass(env, thiz);
    fid = (*env)->GetFieldID(env, c, "type_onset2", "Ljava/lang/String;");
    jaccess = (*env)->GetObjectField(env, thiz, fid);
    type_onset_str = (*env)->GetStringUTFChars(env, jaccess, 0);
    LOGD("Setting Aubio Onset 2nd Algorithm: %s", type_onset_str);
    set_onset_type2(type_onset_str);



    c = (*env)->GetObjectClass(env, thiz);
    fid = (*env)->GetFieldID(env, c, "samplerate", "I");
    samplerate = (*env)->GetIntField(env, thiz, fid);
    LOGV("Setting Aubio SampleRate: %d", samplerate);

    c = (*env)->GetObjectClass(env, thiz);
    fid = (*env)->GetFieldID(env, c, "buffer_size", "I");
    buffer_size = (*env)->GetIntField(env, thiz, fid);
    LOGV("Setting Aubio buffer_size: %d", buffer_size);

    c = (*env)->GetObjectClass(env, thiz);
    fid = (*env)->GetFieldID(env, c, "overlap_size", "I");
    overlap_size = (*env)->GetIntField(env, thiz, fid);
    LOGV("Setting Aubio overlap_size: %d", overlap_size);

    /*
        c = (*env)->GetObjectClass(env, thiz);
        fid = (*env)->GetFieldID(env, c, "bitspersample", "I");
        bitspersample = (*env)->GetIntField(env, thiz, fid);
        LOGV("PCM Bits per sample: %d", bitspersample);
     */

    c = (*env)->GetObjectClass(env, thiz);
    fid = (*env)->GetFieldID(env, c, "channels", "I");
    channels = (*env)->GetIntField(env, thiz, fid);
    LOGV("Setting Aubio Channels: %d", channels);

    c = (*env)->GetObjectClass(env, thiz);
    fid = (*env)->GetFieldID(env, c, "threshold", "F");
    if (fid != NULL) {
        threshold = (*env)->GetFloatField(env, thiz, fid);
    }
    LOGD("Setting Aubio threshold: %f", threshold);

    c = (*env)->GetObjectClass(env, thiz);
    fid = (*env)->GetFieldID(env, c, "silence", "F");
    if (fid != NULL) {
        silence = (*env)->GetFloatField(env, thiz, fid);
    }
    LOGD("Setting Aubio silence: %f", silence);

    c = (*env)->GetObjectClass(env, thiz);
    fid = (*env)->GetFieldID(env, c, "averaging", "F");
    if (fid != NULL) {
        averaging = (*env)->GetFloatField(env, thiz, fid);
    }
    LOGD("Setting Aubio averaging: %f", averaging);


    debug("Read Java object Notes.\n");
    Note = (*env)->FindClass(env, "com.atm.android.atm.midi.Note");
    if (Note == NULL) {
        debug("Could not find Note class.\n");
        return ;
    }
    midCtor = (*env)->GetMethodID(env, Note, "<init>",
            "(IIF)V");

    debug("After init \n");
    if (midCtor == NULL){
        debug("Could not find Contructor method.\n");
        return ;
    }
    notesArray = (*env)->NewObjectArray(env, 1000, Note, NULL);




    /* Sanity checks
     */
    if (input_filename == NULL) {
        LOGV(LOG_TAG, "Error: Need a file");
        return;
    }

    debug("Opening input file: %s\n", input_filename);

    file = new_aubio_sndfile_ro(input_filename);
    debug("Opening input file finished: %s\n", input_filename);
    if (file == NULL) {
        outmsg("Could not open input file %s.\n", input_filename);
        debug("Could not open input file %s.\n", input_filename);
        exit(15);
    }

    verbose = 1;
    woodblock = new_fvec(buffer_size, 1);

    //Init Aubio with params
    if (verbose) aubio_sndfile_info(file);
    channels = aubio_sndfile_channels(file);
    samplerate = aubio_sndfile_samplerate(file);

    ibuf = new_fvec(overlap_size, channels);
    obuf = new_fvec(overlap_size, channels);
    fftgrain = new_cvec(buffer_size, channels);

    if (usepitch) {
        pitchdet = new_aubio_pitchdetection(buffer_size * 4,
                overlap_size, channels, samplerate, type_pitch, mode_pitch);
        aubio_pitchdetection_set_yinthresh(pitchdet, 0.7);

        if (median) {
            note_buffer = new_fvec(median, 1);
            note_buffer2 = new_fvec(median, 1);
        }
    }
    /* phase vocoder */
    pv = new_aubio_pvoc(buffer_size, overlap_size, channels);
    /* onsets */
    parms = new_aubio_peakpicker(threshold);
    o = new_aubio_onsetdetection(type_onset, buffer_size, channels);
    onset = new_fvec(1, channels);
    if (usedoubled) {
        o2 = new_aubio_onsetdetection(type_onset2, buffer_size, channels);
        onset2 = new_fvec(1, channels);
    }

}


