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

#include "pcm2wav_init.h"

int pcm2wav_init(JNIEnv* env, jobject thiz) {
    debug("PCM2WAV init start");
    /* Read the member values from the Java Object that called sendSPAPacket() method
     */
    jclass c = (*env)->GetObjectClass(env, thiz);
    jfieldID fid = (*env)->GetFieldID(env, c, "srcfilename", "Ljava/lang/String;");
    jstring jaccess = (*env)->GetObjectField(env, thiz, fid);
    pcm2wav_input_filename = (*env)->GetStringUTFChars(env, jaccess, 0);

    c = (*env)->GetObjectClass(env, thiz);
    fid = (*env)->GetFieldID(env, c, "destfilename", "Ljava/lang/String;");
    jaccess = (*env)->GetObjectField(env, thiz, fid);
    pcm2wav_output_filename = (*env)->GetStringUTFChars(env, jaccess, 0);

    c = (*env)->GetObjectClass(env, thiz);
    fid = (*env)->GetFieldID(env, c, "samplerate", "I");
    ATM_PCM_SAMPLERATE = (*env)->GetIntField(env, thiz, fid);
    LOGV("PCM SampleRate: %d", ATM_PCM_SAMPLERATE);

    c = (*env)->GetObjectClass(env, thiz);
    fid = (*env)->GetFieldID(env, c, "bitspersample", "I");
    ATM_PCM_BITSPERSAMPLE = (*env)->GetIntField(env, thiz, fid);
    LOGV("PCM Bits per sample: %d", ATM_PCM_BITSPERSAMPLE);
    
     c = (*env)->GetObjectClass(env, thiz);
    fid = (*env)->GetFieldID(env, c, "channels", "I");
    ATM_PCM_CHANNELS = (*env)->GetIntField(env, thiz, fid);
    LOGV("PCM Channels: %d", ATM_PCM_CHANNELS);

    /* Sanity checks
     */
    if (pcm2wav_input_filename == NULL) {
        LOGV(LOG_TAG, "Error: Need a PCM file");
        return ATMFAILURE;
    }

    if (pcm2wav_output_filename == NULL) {
        LOGV(LOG_TAG, "Error: Need a WAV file");
        return ATMFAILURE;
    }

    debug("PCM2WAV init finish");

    return ATMSUCCESS;

}

