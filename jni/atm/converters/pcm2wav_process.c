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
#include "pcm2wav_process.h"

int pcm2wav() {

    char c[ATM_PCM_BUFFER];
    char c1[ATM_PCM_BUFFER];
    int infile, outfile, l, k;
    l = 0;
    LOGV("Converting PCM to WAV");
    infile = open(pcm2wav_input_filename, O_RDONLY);
    if (infile < 3) {
        fprintf(stderr, "open infile error\n");
        return 1;
    }
    while ((k = read(infile, c, ATM_PCM_BUFFER)) > 0) l += k;
    close(infile);
    infile = open(pcm2wav_input_filename, O_RDONLY);
    outfile = open(pcm2wav_output_filename, O_CREAT | O_WRONLY | O_TRUNC);
    if (outfile < 3) {
        fprintf(stderr, "open outfile error\n");
        return 1;
    }
    strcpy(wav.riff, "RIFF");
    wav.length = l + 44 - 8;
    strcpy(wav.wave, "WAVE");
    strcpy(wav.fmt, "fmt ");
    wav.fmtLen = 16;
    wav.compression_code = 1;
    wav.numChannels = ATM_PCM_CHANNELS; // change this
    wav.sampleRate = ATM_PCM_SAMPLERATE; // change this
    wav.bitPerSample = ATM_PCM_BITSPERSAMPLE; // change this

    wav.bytePerSample = wav.bitPerSample / 8 * wav.numChannels; // change this
    wav.bytePerSecond = wav.sampleRate * wav.bytePerSample; // change this

    strcpy(wav.data, "data");
    wav.dataLen = l + 44 - 44;
    write(outfile, wav.riff, 44);
    while ((k = read(infile, c, ATM_PCM_BUFFER)) > 0) {
        int i = 0;
        //Convert to Little Endian
        for (; i < k; i += 2) {
            char temp = c[i + 1];
            c[i + 1] = c[i];
            c[i] = temp;
        }
        write(outfile, c, k);
    }
}
