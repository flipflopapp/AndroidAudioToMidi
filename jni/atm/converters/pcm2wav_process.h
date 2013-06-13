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

#ifndef _PCM_CONVERTER_H
#define	_PCM_CONVERTER_H

#include <stdio.h>
#include "../../utils/Log.h"
#include <fcntl.h>
#include "./pcm2wav_params.h"



#ifdef	__cplusplus
extern "C" {
#endif


#ifdef	__cplusplus
}
#endif


struct WAVE {
    char riff[4];
    int length;
    char wave[4];
    char fmt[4];
    int fmtLen;
    short
    compression_code;
    short numChannels;
    int sampleRate;
    int bytePerSecond;
    short
    bytePerSample;
    short bitPerSample;
    char data[4];
    int dataLen;
} wav;

int pcm2wav();




#endif	/* _PCM_CONVERTER_H */

