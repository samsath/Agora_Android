package uk.org.samhipwell.agora;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;



public class StarScreen extends Activity {

    AlarmReciever alarm = new AlarmReciever();

    Database db;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_star_screen);
        Log.d("Agora","Start screen started");
        db = new Database(getApplicationContext());
        if(db.islogged()){
            // if someone has logged into the program before then load program down and make an intent

            alarm.setAlarm(this);
            new serverSync(this).execute();
            Intent intent = new Intent(StarScreen.this, MainActivity.class);
            Bundle bundle = new Bundle();
            bundle.putString("Project","all");
            intent.putExtras(bundle);
            startActivity(intent);
        }else{
            Intent intent = new Intent(StarScreen.this, loginScrenn.class);
            startActivity(intent);
        }
    }




    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        return super.onOptionsItemSelected(item);
    }
}
