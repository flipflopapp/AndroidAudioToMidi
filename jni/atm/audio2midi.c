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

#define HAVE_PTHREADS 1
#define LOG_NDEBUG 0

#include <jni.h>
#include <stdio.h>
#include <stdlib.h>
#include <unistd.h>
#include <sys/types.h>
#include <sys/stat.h>
#include "converters/pcm2wav_process.h"
#include "converters/wav2midi_process.h"

jobjectArray Java_com_atm_android_atm_converters_Wav2MidiConverter_wav2midi(JNIEnv* env,
        jobject thiz) {

    debug("Wav to Midi Conversion Started.\n");
    strcpy(notes, "");

    //Init the params
    wav2midi_init(env, thiz);

    //Start processing
    exec(audio2midi_process, process_print);

    //Cleanup
    free_atm();    

    //Return Notes array
    return notesArray;


/*
    fflush(stderr);
    return (*env)->NewStringUTF(env, notes);
*/

}

jstring Java_com_atm_android_atm_converters_Pcm2WavConverter_pcm2wav(JNIEnv* env,
        jobject thiz) {

    debug("Starting PCM2WAV standalone");

    //Init the params
    int res = pcm2wav_init(env, thiz);
    if (res != ATMSUCCESS) {
        return (*env)->NewStringUTF(env, "FAILURE");
    }

    pcm2wav();

    return (*env)->NewStringUTF(env, "SUCCESS");

}



