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

import com.atm.android.audiotomidi.SettingsManager;
import android.app.Activity;
import android.content.Context;
import android.content.res.AssetManager;
import android.util.Log;
import com.atm.android.audiotomidi.Const;
import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.OutputStream;

/**
 *
 * @author dev
 */
public class FileInstaller {

    public static void cloneMidiFileProg(Activity activity, String srcFile, String destFile, byte newProg) {
        FileUtils.modifyByte(activity, srcFile, destFile, 102, newProg);

    }

    public static void cloneMidiFileNotes(Activity activity, String srcFile, String destFile, byte newNote) {

        File srcFileF = new File(srcFile);
        byte[] bytes = {109, 114};
        byte[] byteContents = {newNote, newNote};
        FileUtils.modifyByte(activity, srcFileF, destFile, bytes, byteContents);

    }

    public static void installFiles(Activity activity) {

        Log.v("Installer", "Installing files...");
        File tmpDir = new File(Const.basefiledir);
        if (!tmpDir.exists()) {
            tmpDir.mkdir();
        }
        
        //Install synth notes
        installSynthFiles(activity, (byte) SettingsManager.getSynthInstrument(activity));

        //Install Demo WAV file
        installFile(activity, Const.wavfilenamefile, Const.basefiledir);

    }

    public static void installSynthFiles(Activity activity, byte progNum) {
//        UIUtils.log("Installing custom synth files...");
        String srcFile = Const.midinotefilename + "0.mid";
//        FileUtils.installFile(activity, srcFile, VMidiConst.TMP_DIR);
        cloneMidiFileProg(activity, srcFile, Const.basefiledir + "/" + srcFile, (byte) progNum);
        //Clone Notes for rest of files
        for (int i = 1; i <= 127; i++) {
//            UIUtils.log("Installing custom synth file: " + i);
            String destFile = Const.midinotefilename + (i) + ".mid";
            cloneMidiFileNotes(activity, Const.basefiledir + "/" + srcFile, Const.basefiledir + "/" + destFile, (byte) i);
        }
    }

    public static void installFile(Context activity, String srcFile, String destDir) {
        try {
            AssetManager am = activity.getResources().getAssets(); // get the local asset manager
            InputStream is = am.open(srcFile); // open the input stream for reading
            File destDirF = new File(destDir);
            if (!destDirF.exists()) {
                destDirF.mkdir();
            }
            OutputStream os = new FileOutputStream(destDir + "/" + srcFile);
            byte[] buf = new byte[8092];
            int n;
            while ((n = is.read(buf)) > 0) {
                os.write(buf, 0, n);
            }
            os.close();
            is.close();
        } catch (Exception ex) {
            Log.e("Installer", "failed to install file: " + ex);
        }

    }
}
