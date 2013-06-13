ATM (Audio2Midi) for Android

Authors:
    Max Kastanas <max2idea@users.sourceforge.net>
    Naval Saini <navalnovel@gmail.com>

================================================================================

1. What is ATM?
    ATM or Audio To Midi is a library for Android devices that recognized and
    converts raw audio to midi notes. ATM is using Android ports of aubio, fftw, libsndfile,
    and libsamplerate, see file THANKS for more info.

===============================================================================

2. Requirements:

    a. Apache Ant (1.7.1)
        http://ant.apache.org/bindownload.cgi

    b. Android NDK (Android r5-linux)
        http://developer.android.com/sdk/ndk/index.html
        Unzip the contents under a directory ie:
        /home/yourname/tools/android-sdk-linux_86

    c. Android SDK (Android r08-linux)
        http://developer.android.com/sdk/index.html
        Unzip the contents under a directory ie:
        /home/yourname/tools/android-ndk-r5

    d. Include the Android SDK tools directory in your user PATH variable:
        export PATH=$PATH:/home/dev/tools/android-sdk-linux_86/tools:/home/yourname/tools/android-ndk-r5

    e. Download an SDK Android platform, in a command shell type: "android &"
       to start up the "Android SDK and AVD Manager".  Download an SDK
       platform (Android 2.1 and above is recommended) under the
       "Available Packages" section.

    f. Java SDK (1.6 and above)
        http://www.oracle.com/technetwork/java/javase/downloads/index.html

    g. Netbeans (6.9 and above) - Optional if you want to use Netbeans as
       the IDE:
        http://netbeans.org/downloads/

    h. Netbeans Android plugin (0.11 and above) - Optional if you are using
       the Netbeans IDE:
        http://kenai.com/projects/nbandroid/pages/Install

===============================================================================
2. Setup

    a. Untar the tar.gz file:
        tar -zxf audio2midi-0.9b.tar.gz

    b. Update the properties files with the path to the sdk and ndk tools:
        .../audio2midi/build.properties
            sdk.dir=/home/yourname/tools/android-sdk-linux_86
            ndk.dir=/home/yourname/tools/android-ndk-r5

    c. Include the Android SDK platform directory in your user PATH variable
        export PATH=$PATH:/home/dev/tools/android-sdk-linux_86/platforms-tools

    d. Create a new Android device, in a command shell type: "android".
        This will start the android SDK and AVD Manager
        Under Virtual Devices create an AVD device and name it as "Android2.1"

    e. Update (if needed) project file with the right virtual device as above:
        .../audio2midi/nbproject/project.properties
            platform.active=Android2.1

    f. The relevant audio2midi android C code as well as Android ports for
        libraries aubio, fftw, libsndfile, and libsamplerate can be found under
        the ".../project/jni" directory.

===============================================================================
3. Build

    a. To build the app make sure you're under the project directory:
        $> cd /home/yourname/.../audio2midi

    b. To build the application type:
        $> ant jar

===============================================================================
4. Run

    a. Now search under the .../audio2midi/dist directory for a .apk
       file.  You can copy the .apk file to your SD card by typing (make
       sure the Android Emulator is on):
            $> adb push Audio2Midi.apk /sdcard/

    Then you can open it and install it from within your Android device
    using a Filemanager like Astro. Make sure that you have turned on the
    "Unknown Sources" Option under Application settings.

===============================================================================
6. Developing with Netbeans

To develop and build with Netbeans make sure you have installed the nbandroid
plugin (see requirements. Once you have that install a new Java platform under
the Tool -> Java Platforms and choose a Google Android platform, navigate
under the SDK platform directory you downloaded under step 1e. Finally, just
open the .../audio2midi directory as a Netbeans project.

===============================================================================
7. Debugging

You can use gdb to debug the app.  Before you do so make sure you have the
following attribute in your AndroidManifest.xml file:
    android:debuggable="true"
Now rebuild the application and start the Android Emulator, then in a command
shell type:
    $> ndk-gdb --project=<path-to>/android/project

This will start gdb in remote debugging, similarly you can follow the same
steps to debug a physical device

===============================================================================
3. Install Demo
    You can install a sample application that makes use of ATM from
    http://code.google.com/p/audio2midi


===============================================================================
5. Android Emulator

    If you don't have a device you can install and run the Audio To Midi Demo in the Android
    emulator.

    Note: Recording will only work on a physical device.

    To do so follow these steps:
        $> android

    Under the virtual devices find your device "Android2.1" and start it up

    In a command line type:
        $> adb install AudioToMidi.apk

    You can now find the Firewall Knock Operator app under the menu tap on
    the icon to get started.

===============================================================================
4. License
This program is free software; you can redistribute it and/or
modify it under the terms of the GNU General Public License
as published by the Free Software Foundation; either version 2
of the License, or (at your option) any later version.

This program is distributed in the hope that it will be useful,
but WITHOUT ANY WARRANTY; without even the implied warranty of
MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
GNU General Public License for more details.

You should have received a copy of the GNU General Public License
along with this program; if not, write to the Free Software
Foundation, Inc., 51 Franklin Street, Fifth Floor, Boston, MA  02110-1301, USA.

===============================================================================
5. Release Notes
    ATM v0.9b (Sep 12, 2011)
    What's new:
    - Support for Wav to Midi Notes conversion (only file as an input, synthesized MIDI output - no midi file)
    - Support for PCM to Wav conversion

===============================================================================
6. TODO:
    - Support for standard MIDI file format output
    - Support for realtime conversion to MIDI (perhaps JACK, ALSA SEQ on rooted phones)
    - Support for native MIDI playback (QSynth)

Copyright � 2011 ATM
http://code.google.com/audio2midi

Endofdoc
