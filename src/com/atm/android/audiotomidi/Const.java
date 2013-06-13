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
package com.atm.android.audiotomidi;

import android.os.Environment;

/**
 *
 * @author dev
 */
public class Const {

    public static String basefiledir = Environment.getExternalStorageDirectory() + "/atm";
    public static String midinotefilename = "midinote";
    public static String wavfilenamefile = "test.wav";
    public static String pcmfilenamefile = "test.pcm";
    public static String midfilenamefile = "test.mid";
    public static int SETTINGS_RETURN_CODE = 1000;
    public static int SETTINGS_REQUEST_CODE = 1001;

    

}
