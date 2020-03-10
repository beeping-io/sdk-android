/* ----------------------------------------------------------------------------
 * BeepingCoreLJNI java class
 * ----------------------------------------------------------------------------- */

package com.beeping.AndroidBeepingCore;

public class BeepingCoreJNI {


  public static final int BC_TOKEN_START = 0;
  public static final int BC_TOKEN_END_OK = 1;
  public static final int BC_TOKEN_END_BAD = 2;
  public static final int BC_END_PLAY = 3;

  public BeepingCore mBeepingCore;

  static
  {
    try {
        System.loadLibrary("beepingcore");
    } catch (UnsatisfiedLinkError e) {
        System.err.println("native code library failed to load.\n" + e);
        System.exit(1);
    }
  }


  public BeepingCoreJNI(BeepingCore beepingCore)
  {
    mBeepingCore = beepingCore;
  }

  public void BeepingCallback(int value)
  {
    final String TAG = "BEEPING:JNI:TOKEN";

    if (value == BC_TOKEN_START) {
        mBeepingCore.BeepingCallback(value);
    }
    else if (value == BC_TOKEN_END_OK) {
        mBeepingCore.BeepingCallback(value);
    }
    else if (value == BC_TOKEN_END_BAD) {
        mBeepingCore.BeepingCallback(value);
    }
    else if (value == BC_END_PLAY) {
        mBeepingCore.BeepingCallback(value);
    }

  }

    public final native void start(long beepingObject);

    public final native long init();

    public final native int dealloc(long beepingObject);
    public final native int configure(int mode, long beepingObject);
    public final native int startBeepingListen(long beepingObject);
    public final native int stopBeepingListen(long beepingObject);
    public final native int getDecodedString(char[] code, long beepingObject);

}