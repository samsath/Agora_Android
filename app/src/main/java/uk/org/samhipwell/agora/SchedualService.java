package uk.org.samhipwell.agora;
//Help form http://it-ride.blogspot.co.uk/2010/10/android-implementing-notification.html

import android.app.Service;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.os.IBinder;
import android.os.PowerManager;

public class SchedualService extends Service {
    /**
     * This is a service to help with the alarmmanager and schedual the async task.
     */
    private static final String TAG = "Agora";
    private PowerManager.WakeLock wakeLock;

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    private void handleIntent(Intent intent){
        PowerManager pm = (PowerManager)getSystemService(POWER_SERVICE);
        wakeLock = pm.newWakeLock(PowerManager.PARTIAL_WAKE_LOCK,TAG);
        wakeLock.acquire();

        ConnectivityManager con = (ConnectivityManager) getSystemService(CONNECTIVITY_SERVICE);
        if(!con.getBackgroundDataSetting()){
            stopSelf();
            return;
        }
        new serverSync(this).execute();
    }
    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        handleIntent(intent);
        return START_NOT_STICKY;
    }

    public void onDestroy() {
        super.onDestroy();
        wakeLock.release();
    }


}
