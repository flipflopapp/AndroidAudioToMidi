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

#include <stdio.h>
#include <stdlib.h>
#include <stdarg.h>
#include <getopt.h>
#include <unistd.h>
#include <math.h>
#include <aubio.h>
#include <aubioext.h>
#define LOG_NDEBUG 0
#include "utils/Log.h"

#ifdef HAVE_C99_VARARGS_MACROS
//#define debug(format, args...)  if (verbose) fprintf(stderr, format , ##args)
//#define errmsg(format, args...) fprintf(stderr, format , ##args)
//#define outmsg(format, args...) fprintf(stdout, format , ##args)
#define debug(format, args...)  if(verbose) LOGV(format , ##args)
#define errmsg(format, args...) LOGE(format , ##args)
#define outmsg(format, args...) LOGV(format , ##args)
#else
#define debug(format, args...)  if (verbose) fprintf(stderr, format , ##args)
#define errmsg(format, args...) fprintf(stderr, format , ##args)
#define outmsg(format, args...) fprintf(stdout, format , ##args)
#endif

/* defined in utils.c */
void usage (FILE * stream, int exit_code);
int parse_args (int argc, char **argv);

#ifndef JACK_SUPPORT
typedef int (*aubio_process_func_t)
        (smpl_t **input, smpl_t **output, int nframes);
#endif

#define ATMFAILURE 1
#define ATMSUCCESS 0



