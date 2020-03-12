/* ----------------------------------------------------------------------------
 * BeepingCore java class
 * ----------------------------------------------------------------------------- */

package com.beeping.AndroidBeepingCore;

import android.Manifest;
import android.content.pm.PackageManager;
import android.media.AudioAttributes;
import android.media.AudioFocusRequest;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.content.Context;
import android.os.Handler;
import android.media.AudioManager;

public class BeepingCore extends AppCompatActivity {

    private Context bContext;
    private Thread mThread = null;
    private BeepHandler beeps;
    private long mBeepingObject = 0; //pointer to exchange with JNI class
    private BeepingCoreJNI mBeepingCoreJNI = new BeepingCoreJNI(this);
    private char[] fullCode = new char[10];
    private boolean mDecoding = false;
    final String TAG = "BEEPING:SDK";

    private final int RECORD_AUDIO_PERMISSIONS = 1;

    //Class constructor
    public BeepingCore(Context context) {

        bContext = context;

        Log.d(TAG, "CORE");

        beeps = new BeepHandler();
        beeps.addListener((BeepingCoreEvent) context);

        Log.d(TAG, "AUDIO-MANAGER");
        AudioManager am = (AudioManager) bContext.getSystemService(Context.AUDIO_SERVICE);
        am.requestAudioFocus(new AudioFocusRequest.Builder(AudioManager.AUDIOFOCUS_GAIN)
                .setAudioAttributes(
                        new AudioAttributes.Builder()
                                .setUsage(AudioAttributes.USAGE_GAME)
                                .setContentType(AudioAttributes.CONTENT_TYPE_SPEECH)
                                .build()
                )
                .setAcceptsDelayedFocusGain(true)
                .setOnAudioFocusChangeListener(new AudioManager.OnAudioFocusChangeListener() {
                    @Override
                    public void onAudioFocusChange(int focusChange) {
                        switch (focusChange) {
                            case AudioManager.AUDIOFOCUS_GAIN:
                                Log.d(TAG, "AUDIOFOCUS_GAIN");
                                break;
                            case AudioManager.AUDIOFOCUS_GAIN_TRANSIENT:
                                Log.d(TAG, "AUDIOFOCUS_GAIN_TRANSIENT");
                                break;
                            case AudioManager.AUDIOFOCUS_GAIN_TRANSIENT_MAY_DUCK:
                                Log.d("SDK:BEEPING:CORE", "AUDIOFOCUS_GAIN_TRANSIENT_MAY_DUCK");
                                break;
                            case AudioManager.AUDIOFOCUS_LOSS:
                                Log.d(TAG, "AUDIOFOCUS_LOSS");
                                break;
                            case AudioManager.AUDIOFOCUS_LOSS_TRANSIENT:
                                Log.d(TAG, "AUDIOFOCUS_LOSS_TRANSIENT");
                                break;
                            case AudioManager.AUDIOFOCUS_LOSS_TRANSIENT_CAN_DUCK:
                                Log.d(TAG, "AUDIOFOCUS_LOSS_TRANSIENT_CAN_DUCK");
                                break;
                            case AudioManager.AUDIOFOCUS_REQUEST_FAILED:
                                Log.d(TAG, "AUDIOFOCUS_REQUEST_FAILED");
                                break;

                        }
                    }
                }).build()
        );

        am.requestAudioFocus(new AudioFocusRequest.Builder(AudioManager.STREAM_MUSIC)
                .setAudioAttributes(
                        new AudioAttributes.Builder()
                                .setUsage(AudioAttributes.USAGE_GAME)
                                .setContentType(AudioAttributes.CONTENT_TYPE_SPEECH)
                                .build()
                )
                .setAcceptsDelayedFocusGain(true)
                .setOnAudioFocusChangeListener(new AudioManager.OnAudioFocusChangeListener() {
                    @Override
                    public void onAudioFocusChange(int focusChange) {
                        switch (focusChange) {
                            case AudioManager.AUDIOFOCUS_GAIN:
                                Log.d(TAG, "AUDIOFOCUS_GAIN");
                                break;
                            case AudioManager.AUDIOFOCUS_GAIN_TRANSIENT:
                                Log.d(TAG, "AUDIOFOCUS_GAIN_TRANSIENT");
                                break;
                            case AudioManager.AUDIOFOCUS_GAIN_TRANSIENT_MAY_DUCK:
                                Log.d("SDK:BEEPING:CORE", "AUDIOFOCUS_GAIN_TRANSIENT_MAY_DUCK");
                                break;
                            case AudioManager.AUDIOFOCUS_LOSS:
                                Log.d(TAG, "AUDIOFOCUS_LOSS");
                                break;
                            case AudioManager.AUDIOFOCUS_LOSS_TRANSIENT:
                                Log.d(TAG, "AUDIOFOCUS_LOSS_TRANSIENT");
                                break;
                            case AudioManager.AUDIOFOCUS_LOSS_TRANSIENT_CAN_DUCK:
                                Log.d(TAG, "AUDIOFOCUS_LOSS_TRANSIENT_CAN_DUCK");
                                break;
                            case AudioManager.AUDIOFOCUS_REQUEST_FAILED:
                                Log.d(TAG, "AUDIOFOCUS_REQUEST_FAILED");
                                break;

                        }
                    }
                }).build()
        );

    }

    private void alloc(){

        Log.d(TAG, "ALLOC");

        mBeepingObject = mBeepingCoreJNI.init();

        //bContext
        mThread = new Thread()
        {
            public void run()
            {
                setPriority(Thread.MAX_PRIORITY);
                mBeepingCoreJNI.start(mBeepingObject);
            }
        };
        mThread.start();
        configure(EnumBeepingMode.MODE_NONAUDIBLE);
    }

    private int dealloc()
    {

        Log.d(TAG, "DEALLOC");
        int ret = mBeepingCoreJNI.dealloc(mBeepingObject);
        try {
            mThread.join();
        } catch (InterruptedException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        mThread = null;

        return ret;
    }

    private void configure(final EnumBeepingMode mode) {

        Log.d(TAG, "CONFIGURE");

        int ret = 0;
        if (mode==EnumBeepingMode.MODE_AUDIBLE) {
            ret = mBeepingCoreJNI.configure(2, mBeepingObject);
        } else if (mode==EnumBeepingMode.MODE_NONAUDIBLE) {
            ret = mBeepingCoreJNI.configure(3, mBeepingObject);
        } else if (mode==EnumBeepingMode.MODE_HIDDEN) {
            ret = mBeepingCoreJNI.configure(4, mBeepingObject);
        } else if (mode==EnumBeepingMode.MODE_ALL) {
            ret = mBeepingCoreJNI.configure(5, mBeepingObject);
        } else if (mode==EnumBeepingMode.MODE_CUSTOM) {
            ret = mBeepingCoreJNI.configure(6, mBeepingObject);
        } else {
            ret = mBeepingCoreJNI.configure(5, mBeepingObject);
        }
    }
    public void startBeepingListen()
    {
        this.checkMicrophone();
    }

    private String getBeepId()
    {
        mBeepingCoreJNI.getDecodedString(fullCode, mBeepingObject);

        return this.getBeepKey();
    }

    public void stopBeepingListen()
    {

        if(mDecoding == true){
            dealloc();
        }

        mDecoding = false;
        mBeepingCoreJNI.stopBeepingListen(mBeepingObject);
        Log.d(TAG, "stopBeepingListen");
    }

    private String getBeepKey() {
        char[] beepKey = new char[5];

        for (int i=0;i<5;i++)
            beepKey[i] = fullCode[i];

        return String.valueOf(beepKey);
    }

    //Permissions
    private void checkMicrophone () {

        if (ContextCompat.checkSelfPermission(bContext,
                Manifest.permission.RECORD_AUDIO)
                != PackageManager.PERMISSION_GRANTED) {
            System.out.println("BEEPING:SDK: Sin permisos de microfono de inicio");

            //Give user option to still opt-in the permissions
            ActivityCompat.requestPermissions(this,
                    new String[]{Manifest.permission.RECORD_AUDIO},
                    RECORD_AUDIO_PERMISSIONS);
        }
        else {

            if(mThread == null && mDecoding == false){
                alloc();
            }

            mDecoding = true;

            mBeepingCoreJNI.startBeepingListen(mBeepingObject);

            Log.d(TAG, "LISTENING");
        }
    }

    //Mic callback
    public void onRequestPermissionsResult(int requestCode,
                                           String permissions[], int[] grantResults) {
        switch (requestCode) {
            case RECORD_AUDIO_PERMISSIONS: {
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    // permission was granted, yay!
                    Log.d(TAG, "MICROPHONE PERMISSIONS ACCEPTED");

                    if(mThread == null && mDecoding == false){
                        alloc();
                    }

                    mDecoding = true;

                    mBeepingCoreJNI.startBeepingListen(mBeepingObject);

                    Log.d(TAG, "LISTENING");

                } else {
                    // permission denied, boo! Disable the
                    // functionality that depends on this permission.
                    Log.d(TAG, "MICROPHONE PERMISSIONS CANCELED");
                }
                return;
            }
        }
    }
    //User callback
    protected void BeepingCallback(int value) {

        final String TAG = "BEEPING:SDK";

        Log.d(TAG, "BeepingCallback");

        if (value == BeepingCoreJNI.BC_TOKEN_END_OK) {

            Log.d(TAG, "BC_TOKEN_END");

            new Handler(bContext.getMainLooper()).post(new Runnable() {
                @Override
                public void run() {
                    beeps.sendListener(getBeepId());
                }

            });

        }
    }

}


