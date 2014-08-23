package uk.org.samhipwell.agora;
//Help form http://it-ride.blogspot.co.uk/2010/10/android-implementing-notification.html

import android.app.Service;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.os.IBinder;
import android.os.PowerManager;

public class SchedualService extends Service {
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

        serverSync sync = new serverSync(this);

        ConnectivityManager con = (ConnectivityManager) getSystemService(CONNECTIVITY_SERVICE);
        if(!con.getBackgroundDataSetting()){
            stopSelf();
            return;
        }
         sync.execute();
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
