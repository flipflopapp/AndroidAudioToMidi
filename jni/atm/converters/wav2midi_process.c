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
#include "wav2midi_process.h"

unsigned int pos = 0; /*frames%dspblocksize*/
uint_t usepitch = 1;
char notes[10000];

int audio2midi_process(float **input, float **output, int nframes) {
    unsigned int i; /*channels*/
    unsigned int j; /*frames*/
    for (j = 0; j < (unsigned) nframes; j++) {
        if (usejack) {
            for (i = 0; i < channels; i++) {
                /* write input to datanew */
                fvec_write_sample(ibuf, input[i][j], i, pos);
                /* put synthnew in output */
                output[i][j] = fvec_read_sample(obuf, i, pos);
            }
        }
        /*time for fft*/
        if (pos == overlap_size - 1) {
            /* block loop */
            aubio_pvoc_do(pv, ibuf, fftgrain);
            aubio_onsetdetection(o, fftgrain, onset);
            if (usedoubled) {
                aubio_onsetdetection(o2, fftgrain, onset2);
                onset->data[0][0] *= onset2->data[0][0];
            }
            isonset = aubio_peakpick_pimrt(onset, parms);

            pitch = aubio_pitchdetection(pitchdet, ibuf);
            if (median) {
                note_append(note_buffer, pitch);
            }

            /* curlevel is negatif or 1 if silence */
            curlevel = aubio_level_detection(ibuf, silence);
            if (isonset) {
                /* test for silence */
                if (curlevel == 1.) {
                    isonset = 0;
                    if (median) isready = 0;
                    /* send note off */
                    send_noteon(curnote, 0);
                } else {
                    if (median) {
                        isready = 1;
                    } else {
                        /* kill old note */
                        send_noteon(curnote, 0);
                        /* get and send new one */
                        send_noteon(pitch, 127 + (int) floor(curlevel));
                        curnote = pitch;
                    }

                    for (pos = 0; pos < overlap_size; pos++) {
                        obuf->data[0][pos] = woodblock->data[0][pos];
                    }
                }
            } else {
                if (median) {
                    if (isready > 0)
                        isready++;
                    if (isready == median) {
                        /* kill old note */
                        send_noteon(curnote, 0);
                        newnote = get_note(note_buffer, note_buffer2);
                        curnote = newnote;
                        /* get and send new one */
                        if (curnote > 45) {
                            send_noteon(curnote, 127 + (int) floor(curlevel));
                        }
                    }
                } // if median
                for (pos = 0; pos < overlap_size; pos++)
                    obuf->data[0][pos] = 0.;
            }
            /* end of block loop */
            pos = -1; /* so it will be zero next j loop */
        }
        pos++;
    }
    return 1;
}

void exec(aubio_process_func_t process_func, aubio_print_func_t print) {
    LOGV("Aubio start");


    /* phasevoc */
    outmsg("Processing ...\n");
    LOGV("Aubio processing");

    frames = 0;

    while ((signed)overlap_size == aubio_sndfile_read(file, overlap_size, ibuf)) {
        isonset = 0;
        process_func(ibuf->data, obuf->data, overlap_size);
        frames++;
    }

    debug("Processed %d frames of %d samples.\n", frames, buffer_size);
    LOGV("Process Complete\n");
    del_aubio_sndfile(file);

    if (output_filename != NULL)
        del_aubio_sndfile(fileout);


}

void send_noteon(int pitch, int velo) {
    smpl_t mpitch = floor(aubio_freqtomidi(pitch) + .5);

    /* we should check if we use midi here, not jack */
    if (verbose) {
        if (velo == 0) {
            /*
                        outmsg("Time: %f\n",frames*overlap_size/(float)samplerate);
             */
        } else if (mpitch >= 0 && mpitch <= 127) {
            outmsg("Midi Note: %d, Velocity: %d, Time: %f\n", (int) mpitch, (int) velo,
                    frames * overlap_size / (float) samplerate);
            char notes_tmp[100];
            sprintf(notes_tmp, "%d, %d, %f\n", (int) mpitch, (int) velo,
                    frames * overlap_size / (float) samplerate);
            strcat(notes, notes_tmp);

            jobject newNote;
            newNote = (*JNIenv)->NewObject(JNIenv, Note, midCtor, (int) mpitch, (int) velo,
                    frames * overlap_size / (float) samplerate);
            (*JNIenv)->SetObjectArrayElement(JNIenv, notesArray, counter++, newNote);

        }
    }
}

void note_append(fvec_t * note_buffer, smpl_t curnote) {
    uint_t i = 0;
    for (i = 0; i < note_buffer->length - 1; i++) {
        note_buffer->data[0][i] = note_buffer->data[0][i + 1];
    }
    note_buffer->data[0][note_buffer->length - 1] = curnote;
    return;
}

uint_t get_note(fvec_t *note_buffer, fvec_t *note_buffer2) {
    uint_t i = 0;
    for (i = 0; i < note_buffer->length; i++) {
        note_buffer2->data[0][i] = note_buffer->data[0][i];
    }
    return vec_median(note_buffer2);
}

void free_atm(void) {
    if (usepitch) {
        send_noteon(curnote, 0);
        del_aubio_pitchdetection(pitchdet);
        if (median) {
            del_fvec(note_buffer);
            del_fvec(note_buffer2);
        }
    }
    if (usedoubled) {
        del_aubio_onsetdetection(o2);
        del_fvec(onset2);
    }
    del_aubio_onsetdetection(o);
    del_aubio_peakpicker(parms);
    del_aubio_pvoc(pv);
    del_fvec(obuf);
    del_fvec(ibuf);
    del_cvec(fftgrain);
    del_fvec(onset);
    del_fvec(woodblock);
    aubio_cleanup();
}

void process_print(void) {
    if (verbose) outmsg("%f\n", pitch);
}