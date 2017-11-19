package gonext.smsapp.callrecorder;

import java.io.File;
import java.io.IOException;
import java.lang.Exception;
import java.util.Date;
import java.text.SimpleDateFormat;

import android.os.Environment;
import android.os.IBinder;
import android.app.Service;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.preference.PreferenceManager;
import android.content.SharedPreferences;
import android.content.Context;
import android.content.Intent;
import android.media.MediaRecorder;
import android.util.Log;

//import java.security.KeyPairGenerator;
//import java.security.KeyPair;
//import java.security.Key;

import java.io.InputStream;
import java.io.FileInputStream;
import java.util.Iterator;

import gonext.smsapp.utils.Utils;

public class RecordService 
    extends Service
    implements MediaRecorder.OnInfoListener, MediaRecorder.OnErrorListener
{
    private static final String TAG = "CallRecorder";

    private MediaRecorder recorder = null;
    private boolean isRecording = false;
    private File recording = null;;

    private File makeOutputFile ()
    {
        File dir = new File(getCacheDir().getAbsolutePath()+"/calls/");

        // test dir for existence and writeability
        if (!dir.exists()) {
            try {
                dir.mkdirs();
            } catch (Exception e) {
                Log.e("CallRecorder", "RecordService::makeOutputFile unable to create directory " + dir + ": " + e);
                return null;
            }
        } else {
            if (!dir.canWrite()) {
                Log.e(TAG, "RecordService::makeOutputFile does not have write permission for directory: " + dir);
                return null;
            }
        }

        // test size

        // create filename based on call data
        //String prefix = "call";
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd_HH-mm-ss-SS");
        String prefix = sdf.format(new Date());
        String suffix = "call";
        suffix = suffix + ".3gpp";
        try {
            return File.createTempFile(prefix, suffix, dir);
        } catch (IOException e) {
            Log.e("CallRecorder", "RecordService::makeOutputFile unable to create temp file in " + dir + ": " + e);
            return null;
        }
    }

    public void onCreate()
    {
        super.onCreate();
        Utils.changeRecordingState(true,this);
        recorder = new MediaRecorder();
        Log.i("CallRecorder", "onCreate created MediaRecorder object");
    }

    public void onStart(Intent intent, int startId) {

        recording = makeOutputFile();
        if (recording == null) {
            recorder = null;
            return; //return 0;
        }

        try {
            // These calls will throw exceptions unless you set the 
            // android.permission.RECORD_AUDIO permission for your app
            recorder.reset();
            recorder.setAudioSource(MediaRecorder.AudioSource.VOICE_CALL);
            recorder.setOutputFormat(MediaRecorder.OutputFormat.DEFAULT);
            recorder.setAudioEncoder(MediaRecorder.AudioEncoder.DEFAULT);
            Log.d("CallRecorder", "set encoder default");
            recorder.setOutputFile(recording.getAbsolutePath());
            Log.d("CallRecorder", "set file: " + recording);
            //recorder.setMaxDuration(msDuration); //1000); // 1 seconds
            //recorder.setMaxFileSize(bytesMax); //1024*1024); // 1KB

            recorder.setOnInfoListener(this);
            recorder.setOnErrorListener(this);
            
            try {
                recorder.prepare();
            } catch (java.io.IOException e) {
                Log.e("CallRecorder", "RecordService::onStart() IOException attempting recorder.prepare()\n");
                recorder = null;
                return; //return 0; //START_STICKY;
            }
            Log.d("CallRecorder", "recorder.prepare() returned");

            recorder.start();
            isRecording = true;
            Log.i("CallRecorder", "recorder.start() returned");
        } catch (java.lang.Exception e) {
            recorder.setAudioSource(MediaRecorder.AudioSource.MIC);
            try {
                recorder.prepare();
                recorder.start();
            }catch (Exception e2){
                Log.e("CallRecorder", "RecordService::onStart caught unexpected exception", e);
                recorder = null;
                e2.printStackTrace();
                return;
            }
        }

        return; //return 0; //return START_STICKY;
    }

    public void onDestroy()
    {
        super.onDestroy();

        if (null != recorder) {
            Log.i("CallRecorder", "RecordService::onDestroy calling recorder.release()");
            isRecording = false;
            recorder.release();
            if(recording != null && recording.exists()){
                String fileName = recording.getAbsolutePath();
                fileName = fileName.replace(".3gpp","end.3gpp");
                File dest = new File(fileName);
                recording.renameTo(dest);
            }
        }
        Utils.changeRecordingState(false,this);

    }


    // methods to handle binding the service

    public IBinder onBind(Intent intent)
    {
        return null;
    }

    public boolean onUnbind(Intent intent)
    {
        return false;
    }

    public void onRebind(Intent intent)
    {
    }

    // MediaRecorder.OnInfoListener
    public void onInfo(MediaRecorder mr, int what, int extra)
    {
        Log.i("CallRecorder", "RecordService got MediaRecorder onInfo callback with what: " + what + " extra: " + extra);
        isRecording = false;
    }

    // MediaRecorder.OnErrorListener
    public void onError(MediaRecorder mr, int what, int extra) 
    {
        Log.e("CallRecorder", "RecordService got MediaRecorder onError callback with what: " + what + " extra: " + extra);
        isRecording = false;
        mr.release();
    }
}