/* ----------------------------------------------------------------------------
 * BeepingCore java class
 * ----------------------------------------------------------------------------- */

package com.beeping.AndroidBeepingCore;

import android.util.Log;
import android.content.Context;
import android.os.Handler;
import android.media.AudioManager;

public class BeepingCore {

    private Context bContext;
    private Thread mThread = null;
    private BeepHandler beeps;
    private long mBeepingObject = 0; //pointer to exchange with JNI class
    private BeepingCoreJNI mBeepingCoreJNI = new BeepingCoreJNI(this);
    private char[] fullCode = new char[10];
    private boolean mDecoding = false;

    //Class constructor
    public BeepingCore(Context context) {

        final String TAG = "BEEPING:SDK";

        bContext = context;

        Log.d(TAG, "BEEPINGCORE");

        beeps = new BeepHandler();
        beeps.addListener((BeepingCoreEvent) context);

        AudioManager.OnAudioFocusChangeListener mOnAudioFocusChangeListener = new AudioManager.OnAudioFocusChangeListener() {

            @Override
            public void onAudioFocusChange(int focusChange) {

                switch (focusChange) {
                    case AudioManager.AUDIOFOCUS_GAIN:
                        Log.d(TAG, "AUDIOFOCUS_GAIN");
                        //if (isAuto())
                            //startBeepingListen();
                        break;
                    case AudioManager.AUDIOFOCUS_GAIN_TRANSIENT:
                        Log.d(TAG, "AUDIOFOCUS_GAIN_TRANSIENT");
                        break;
                    case AudioManager.AUDIOFOCUS_GAIN_TRANSIENT_MAY_DUCK:
                        Log.d("SDK:BEEPING:CORE", "AUDIOFOCUS_GAIN_TRANSIENT_MAY_DUCK");
                        break;
                    case AudioManager.AUDIOFOCUS_LOSS:
                        Log.d(TAG, "AUDIOFOCUS_LOSS");
                        //if (isAuto())
                            //stopBeepingListen();
                        break;
                    case AudioManager.AUDIOFOCUS_LOSS_TRANSIENT:
                        Log.d(TAG, "AUDIOFOCUS_LOSS_TRANSIENT");
                        //if (isAuto())
                            //stopBeepingListen();
                        break;
                    case AudioManager.AUDIOFOCUS_LOSS_TRANSIENT_CAN_DUCK:
                        Log.d(TAG, "AUDIOFOCUS_LOSS_TRANSIENT_CAN_DUCK");
                        break;
                    case AudioManager.AUDIOFOCUS_REQUEST_FAILED:
                        Log.d(TAG, "AUDIOFOCUS_REQUEST_FAILED");
                        break;

                }
            }
        };

        AudioManager am = (AudioManager) bContext.getSystemService(Context.AUDIO_SERVICE);
        am.requestAudioFocus( mOnAudioFocusChangeListener, AudioManager.STREAM_MUSIC, AudioManager.AUDIOFOCUS_GAIN);

    }

    private void alloc(){

        final String TAG = "BEEPING:SDK:ALLOC";

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

    public int dealloc()
    {
        final String TAG = "BEEPING:SDK";

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

    public void configure(final EnumBeepingMode mode) {

        final String TAG = "BEEPING:SDK";

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
        final String TAG = "BEEPING:SDK";

        Log.d(TAG, "LISTENING");

        if(mThread == null && mDecoding == false){
            alloc();
        }

        mDecoding = true;

        mBeepingCoreJNI.startBeepingListen(mBeepingObject);
    }

    public String getBeepId()
    {
        mBeepingCoreJNI.getDecodedString(fullCode, mBeepingObject);

        return this.getBeepKey();
    }

    public void onlineMode()
    {
        Log.d("SDK:METHOD", "onlineMode");
    }

    public void offlineMode()
    {
        Log.d("SDK:METHOD", "offlineMode");
    }

    public void stopBeepingListen()
    {
      Log.d("SDK:DEBUG", "stopBeepingListen");
      mDecoding = false;
      mBeepingCoreJNI.stopBeepingListen(mBeepingObject);
    }

    /* BEGIN FOR SECOND SCREEN */

    public String getBeepFullCode(){

        mBeepingCoreJNI.getDecodedString(fullCode, mBeepingObject);

        return String.valueOf( fullCode );
    }

    private String getBeepKey() {
        char[] beepKey = new char[5];

        for (int i=0;i<5;i++)
            beepKey[i] = fullCode[i];

        return String.valueOf(beepKey);
    }

    //User callback
    protected void BeepingCallback(int value) {

        final String TAG = "BEEPING:SDK";

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


