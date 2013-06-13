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
#ifndef _AUDIO2MIDI_PROCESS_H
#define	_AUDIO2MIDI_PROCESS_H

#ifdef	__cplusplus
extern "C" {
#endif



#ifdef	__cplusplus
}
#endif

#include <jni.h>
#include "wav2midi_params.h"
#include "wav2midi_init.h"
#include "sample.h"

extern jobjectArray notesArray;


extern char notes[10000];
typedef void (aubio_print_func_t)(void);
int audio2midi_process(float **input, float **output, int nframes);
void exec(aubio_process_func_t process_func, aubio_print_func_t print);
void send_noteon(int pitch, int velo);
/** append new note candidate to the note_buffer and return filtered value. we
 * need to copy the input array as vec_median destroy its input data.*/
void note_append(fvec_t * note_buffer, smpl_t curnote);
uint_t get_note(fvec_t *note_buffer, fvec_t *note_buffer2);
void free_atm(void);
void process_print(void) ;



#endif	/* _AUDIO2MIDI_PROCESS_H */

