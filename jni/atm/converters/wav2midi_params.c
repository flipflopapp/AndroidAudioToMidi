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

#include "wav2midi_params.h"


const char * prog_name;

// EXPOSED PARAMS TO JNI
smpl_t threshold = 0.3;
smpl_t silence = -90.;
smpl_t averaging; //boolean 0,1
uint_t buffer_size = 128; //1024;
uint_t overlap_size = 256; //512;
uint_t channels = 1;
uint_t samplerate = 11025; //44100;
aubio_pitchdetection_type type_pitch = aubio_pitch_yinfft; // aubio_pitch_mcomb
aubio_onsetdetection_type type_onset = aubio_onset_kl;

//EXTRA PARAMS
aubio_pitchdetection_mode mode_pitch = aubio_pitchm_freq;
aubio_onsetdetection_type type_onset2 = aubio_onset_complex;


//VARIABLES FOR AUBIO

uint_t median = 6;

/* pitch objects */
smpl_t pitch = 0.;
aubio_pitchdetection_t * pitchdet;

/* settings */
const char * output_filename = NULL;
const char * input_filename = NULL;
const char * onset_filename = "/sdcard/sounds/woodblock.aiff";
/*
const char * onset_filename  = AUBIO_PREFIX "/share/sounds/" PACKAGE "/woodblock.aiff";
 */
int frames = 0;
int usejack = 0;
int usedoubled = 1;

aubio_sndfile_t * file = NULL;
aubio_sndfile_t * fileout = NULL;

aubio_pvoc_t * pv;
fvec_t * ibuf;
fvec_t * obuf;
cvec_t * fftgrain;
fvec_t * woodblock;
aubio_onsetdetection_t *o;
aubio_onsetdetection_t *o2;
fvec_t *onset;
fvec_t *onset2;
int isonset = 0;
aubio_pickpeak_t * parms;



fvec_t * note_buffer = NULL;
fvec_t * note_buffer2 = NULL;
smpl_t curlevel = 0.;
smpl_t maxonset = 0.;

/* midi objects */
aubio_midi_player_t * mplay;
aubio_midi_driver_t * mdriver;
aubio_midi_event_t * event;

smpl_t curnote = 0.;
smpl_t newnote = 0.;
uint_t isready = 0;






void set_onset_type(const char * onset_type) {

    if (strcmp(onset_type, "energy") == 0)
        type_onset = aubio_onset_energy;
    else if (strcmp(onset_type, "specdiff") == 0)
        type_onset = aubio_onset_specdiff;
    else if (strcmp(onset_type, "hfc") == 0)
        type_onset = aubio_onset_hfc;
    else if (strcmp(onset_type, "complexdomain") == 0)
        type_onset = aubio_onset_complex;
    else if (strcmp(onset_type, "complex") == 0)
        type_onset = aubio_onset_complex;
    else if (strcmp(onset_type, "phase") == 0)
        type_onset = aubio_onset_phase;
    else if (strcmp(onset_type, "mkl") == 0)
        type_onset = aubio_onset_mkl;
    else if (strcmp(onset_type, "kl") == 0)
        type_onset = aubio_onset_kl;
    else {
        errmsg("unknown onset type.\n");
        abort();
    }
    usedoubled = 0;

}

void set_onset_type2(const char * onset_type) {

    if (strcmp(onset_type, "energy") == 0)
        type_onset2 = aubio_onset_energy;
    else if (strcmp(onset_type, "specdiff") == 0)
        type_onset2 = aubio_onset_specdiff;
    else if (strcmp(onset_type, "hfc") == 0)
        type_onset2 = aubio_onset_hfc;
    else if (strcmp(onset_type, "complexdomain") == 0)
        type_onset2 = aubio_onset_complex;
    else if (strcmp(onset_type, "complex") == 0)
        type_onset2 = aubio_onset_complex;
    else if (strcmp(onset_type, "phase") == 0)
        type_onset2 = aubio_onset_phase;
    else if (strcmp(onset_type, "mkl") == 0)
        type_onset2 = aubio_onset_mkl;
    else if (strcmp(onset_type, "kl") == 0)
        type_onset2 = aubio_onset_kl;
    else if (strcmp(onset_type, "") == 0)
        usedoubled = 0;
    else {
        errmsg("unknown onset type.\n");
        abort();
    }
    usedoubled = 1;

}

void set_pitch_type(const char * pitch_type) {
    if (strcmp(pitch_type, "mcomb") == 0)
        type_pitch = aubio_pitch_mcomb;
    else if (strcmp(pitch_type, "yinfft") == 0)
        type_pitch = aubio_pitch_yinfft;
    else if (strcmp(pitch_type, "yin") == 0)
        type_pitch = aubio_pitch_yin;
    else if (strcmp(pitch_type, "schmitt") == 0)
        type_pitch = aubio_pitch_schmitt;
    else if (strcmp(pitch_type, "fcomb") == 0)
        type_pitch = aubio_pitch_fcomb;
    else {
        errmsg("unknown pitch type.\n");
        abort();
    }
}

void set_onset_threshold(float threshold) {
    threshold = threshold;
}

void set_averaging(float averaging_float) {
    averaging = averaging_float;
}

void set_overlap_size(int overlap_size_int) {
    overlap_size = overlap_size_int;
}

void set_onset_silence(float onset_silence) {
    silence = onset_silence;
}

void set_output_filename(const char * filename) {
    output_filename = filename;
}

void set_usejack() {
    usejack = 1;
}

void set_verbose() {
    verbose = 1;
}

void set_input_filename(const char * filename) {
    input_filename = filename;
}
