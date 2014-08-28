package uk.org.samhipwell.agora;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;



public class StarScreen extends Activity {
    /**
     * This activity is for the start screen so when it loads the app it shows a splash screen.
     * It also works out if the user is logged in if not they get sent to the login screen.
     */

    AlarmReciever alarm = new AlarmReciever();

    Database db;
    private final int DURATION = 3000;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_star_screen);
        Log.d("Agora","Start screen started");
        db = new Database(getApplicationContext());
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                if (db.islogged()) {
                    // if someone has logged into the program before then load program down and make an intent

                    alarm.setAlarm(getApplicationContext());
                    new serverSync(getApplicationContext()).execute();
                    Intent intent = new Intent(StarScreen.this, SyncActiity.class);
                    Bundle bundle = new Bundle();
                    bundle.putString("Project", "all");
                    intent.putExtras(bundle);
                    startActivity(intent);
                } else {
                    Intent intent = new Intent(StarScreen.this, loginScrenn.class);
                    startActivity(intent);
                }
            }
        }, DURATION);
    }

}
