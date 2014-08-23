package uk.org.samhipwell.agora;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;


public class BootReceiver extends BroadcastReceiver {

    AlarmReciever alarm = new AlarmReciever();

    @Override
    public void onReceive(Context context, Intent intent) {
        if(intent.getAction().equals("android.intent.action.BOOT_COMPLETE")){
            alarm.setAlarm(context);
        }
    }
}
