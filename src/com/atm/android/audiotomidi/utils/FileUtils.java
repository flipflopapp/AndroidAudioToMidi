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
package com.atm.android.audiotomidi.utils;

import android.app.Activity;
import android.content.res.AssetManager;
import android.util.Log;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.OutputStream;

/**
 *
 * @author dev
 */
public class FileUtils {

    public static void modifyByte(Activity activity, File srcFile, String destFile, byte[] byteNums, byte[] byteContents) {
//        throw new UnsupportedOperationException("Not yet implemented");
//        UIUtils.log("Modifying byte: " + byteNum + " in sdcard file: " + srcFile);
        try {
            FileInputStream is = new FileInputStream(srcFile); // open the input stream for reading
            OutputStream os = new FileOutputStream(destFile);
            byte[] buf = new byte[8092];
            int n;
            int totalBytes = 0;
            while ((n = is.read(buf)) > 0) {
                totalBytes += n;
//                UIUtils.log("Total Bytes read: " + totalBytes);
                for (int i = 0; i < byteNums.length; i++) {
                    if (byteNums[i] <= totalBytes) {
//                    UIUtils.log("Replacing byte: " + (totalBytes - n + byteNum ) + " contents with: " + byteContents);
                        buf[totalBytes - n + byteNums[i]] = byteContents[i];
                    }
                }
                os.write(buf, 0, n);
            }
//            UIUtils.log(ByteUtils.ByteArrayToString(buf));
            os.close();
            is.close();
        } catch (Exception ex) {
            Log.e("Installer", "failed to modify file: " + ex);
        }
    }

    public static void modifyByte(Activity activity, String srcFile, String destFile, int byteNum, byte byteContents) {
//        throw new UnsupportedOperationException("Not yet implemented");
//        UIUtils.log("Modifying byte: " + byteNum + " in Asset file: " + srcFile);
        try {
            AssetManager am = activity.getResources().getAssets(); // get the local asset manager
            InputStream is = am.open(srcFile); // open the input stream for reading
            OutputStream os = new FileOutputStream(destFile);
            byte[] buf = new byte[8092];
            int n;
            int totalBytes = 0;
            while ((n = is.read(buf)) > 0) {
                totalBytes += n;
//                UIUtils.log("Total Bytes read: " + totalBytes);
                if (byteNum <= totalBytes) {
//                    UIUtils.log("Replacing byte: " + (totalBytes - n + byteNum ) + " contents with: " + byteContents);
                    buf[totalBytes - n + byteNum] = byteContents;
                }
                os.write(buf, 0, n);
            }
//            UIUtils.log(ByteUtils.ByteArrayToString(buf));
            os.close();
            is.close();
        } catch (Exception ex) {
            Log.e("Installer", "failed to modify file: " + ex);
        }
    }
}
