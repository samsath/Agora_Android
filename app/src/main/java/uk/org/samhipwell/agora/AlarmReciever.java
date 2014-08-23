package uk.org.samhipwell.agora;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.support.v4.content.WakefulBroadcastReceiver;

import java.util.Calendar;


public class AlarmReciever extends WakefulBroadcastReceiver {

    private AlarmManager alarmMan;
    private PendingIntent alarmIntent;

    @Override
    public void onReceive(Context context, Intent intent) {

        Intent service = new Intent(context,SchedualService.class);

        startWakefulService(context,service);

    }

    public void setAlarm(Context context){
        alarmMan = (AlarmManager)context.getSystemService(Context.ALARM_SERVICE);
        Intent intent = new Intent(context, AlarmReciever.class);
        alarmIntent = PendingIntent.getBroadcast(context, 0, intent, 0);

        alarmMan.setInexactRepeating(AlarmManager.ELAPSED_REALTIME_WAKEUP,
                AlarmManager.INTERVAL_HALF_HOUR,
                AlarmManager.INTERVAL_HALF_HOUR, alarmIntent);

        ComponentName receiver = new ComponentName(context, BootReceiver.class);
        PackageManager pm = context.getPackageManager();

        pm.setComponentEnabledSetting(receiver,
                PackageManager.COMPONENT_ENABLED_STATE_ENABLED,
                PackageManager.DONT_KILL_APP);
    }

    public void cancelAlarm(Context context){
        if(alarmMan!=null){
            alarmMan.cancel(alarmIntent);
        }

        ComponentName reciever = new ComponentName(context,BootReceiver.class);
        PackageManager pack = context.getPackageManager();

        pack.setComponentEnabledSetting(reciever,PackageManager.COMPONENT_ENABLED_STATE_DISABLED,
                PackageManager.DONT_KILL_APP);
    }

}
