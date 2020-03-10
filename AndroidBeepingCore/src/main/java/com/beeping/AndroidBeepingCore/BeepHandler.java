package com.beeping.AndroidBeepingCore;

public class BeepHandler {

    private BeepingCoreEvent listener;

    public BeepHandler(){
        this.listener = null;
    }

    public void addListener(BeepingCoreEvent listener) {

        this.listener = listener;
    }

    public void sendListener (String beepId) {

        if ( beepId != null )
            this.listener.beepIdWith(beepId) ;
    }

}


