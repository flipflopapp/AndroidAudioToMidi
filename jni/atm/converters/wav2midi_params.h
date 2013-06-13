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

#ifndef _AUDIO_PARAMS_H
#define	_AUDIO_PARAMS_H

#ifdef	__cplusplus
extern "C" {
#endif


#ifdef	__cplusplus
}
#endif

#include "../utils/utils.h"

extern int frames;
extern int usejack;
extern int usedoubled;
extern unsigned int median;
extern const char * output_filename;
extern const char * input_filename;

extern const char * output_filename;
extern const char * input_filename;
extern const char * onset_filename;
extern int verbose;
extern int usejack;
extern int usedoubled;


/* energy,specdiff,hfc,complexdomain,phase */
extern aubio_onsetdetection_type type_onset;
extern aubio_onsetdetection_type type_onset2;
extern smpl_t threshold;
extern smpl_t silence;
extern uint_t buffer_size;
extern uint_t overlap_size;
extern uint_t channels;
extern uint_t samplerate;


extern aubio_sndfile_t * file;
extern aubio_sndfile_t * fileout;

extern aubio_pvoc_t * pv;
extern fvec_t * ibuf;
extern fvec_t * obuf;
extern cvec_t * fftgrain;
extern fvec_t * woodblock;
extern aubio_onsetdetection_t *o;
extern aubio_onsetdetection_t *o2;
extern fvec_t *onset;
extern fvec_t *onset2;
extern int isonset;
extern aubio_pickpeak_t * parms;


/* pitch objects */
extern smpl_t pitch;
extern aubio_pitchdetection_t * pitchdet;
extern aubio_pitchdetection_type mode;
extern uint_t median;

extern fvec_t * note_buffer;
extern fvec_t * note_buffer2;
extern smpl_t curlevel;
extern smpl_t maxonset;

/* midi objects */
extern aubio_midi_player_t * mplay;
extern aubio_midi_driver_t * mdriver;
extern aubio_midi_event_t * event;

extern smpl_t curnote;
extern smpl_t newnote;
extern uint_t isready;
extern smpl_t averaging;

/* per example param */
extern uint_t usepitch;

extern aubio_pitchdetection_type type_pitch;
extern aubio_pitchdetection_mode mode_pitch;

void set_onset_type(const char * onset_type);
void set_pitch_type(const char * pitch_type);
void set_onset_threshold(float threshold);
void set_onset_threshold(float averaging_float);
void set_overlap_size(int averaging_int);
void set_onset_silence(float onset_silence);
void set_output_filename(const char * filename);
void set_usejack();
void set_verbose();
void set_input_filename(const char * filename);

#endif	/* _AUDIO_PARAMS_H */

