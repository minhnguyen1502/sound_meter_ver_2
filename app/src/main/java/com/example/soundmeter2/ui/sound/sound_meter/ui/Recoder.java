package com.example.soundmeter2.ui.sound.sound_meter.ui;

import android.media.MediaRecorder;
import android.util.Log;

import java.io.File;
import java.io.IOException;

public class Recoder {
    private File mFile;
    private MediaRecorder mediaRecorder;
    private boolean isRecording = false;

    public float getMax() {
        if (mediaRecorder != null) {
            try {
                return mediaRecorder.getMaxAmplitude();
            } catch (IllegalStateException e) {
                e.getStackTrace();
                return 0;
            }
        } else {
            return 5;
        }
    }


    public void setmFile(File mFile) {
        this.mFile = mFile;
    }

    public boolean startRecoding() {
        if (mFile == null) {
            return false;
        }
        try {
            mediaRecorder = new MediaRecorder();

            mediaRecorder.setAudioSource(MediaRecorder.AudioSource.MIC);
            mediaRecorder.setOutputFormat(MediaRecorder.OutputFormat.THREE_GPP);
            mediaRecorder.setAudioEncoder(MediaRecorder.AudioEncoder.AMR_NB);
            mediaRecorder.setOutputFile(mFile.getAbsolutePath());

            mediaRecorder.prepare();
            mediaRecorder.start();
            isRecording = true;

            return true;
        } catch (IOException exception) {
            Log.e("Sound_Meter",exception.toString());
            mediaRecorder.reset();
            mediaRecorder.release();
            mFile = null;
            isRecording = false;
            exception.getStackTrace();
            return false;
        } catch (IllegalStateException e) {
            Log.e("Sound_Mete1",e.toString());
            stopRecoding();
            e.getStackTrace();
            return false;
        }
    }

    public void stopRecoding() {
        if (mediaRecorder != null){
            if (isRecording){
                try {
                    mediaRecorder.stop();
                    mediaRecorder.release();
                }catch (Exception e){
                    e.getStackTrace();
                }

            }
            mediaRecorder = null;
            isRecording = false;
        }
    }

    public void deleteRecoding(){
        stopRecoding();
        if (mFile != null){
            mFile.delete();
            mFile = null;
        }

    }
}
