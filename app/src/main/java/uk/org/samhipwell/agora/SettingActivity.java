package uk.org.samhipwell.agora;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.EditText;


public class SettingActivity extends Activity {

    static SharedPreferences settings;
    static SharedPreferences.Editor editor;

    public static final String Aurl = "agoraURL";
    public static final String Aport = "agoraPort";

    EditText url,port;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_setting);

        settings = this.getSharedPreferences("Agora",MODE_WORLD_READABLE);

        url = (EditText) findViewById(R.id.etURL);
        port = (EditText) findViewById(R.id.etPort);
        if(settings.contains(Aurl)){
            url.setText(settings.getString(Aurl,""));
        }
        if(settings.contains(Aport)){
            port.setText(settings.getString(Aport,""));
        }
    }


    public void settingSaveClick(View view) {
        String aurl = url.getText().toString();
        Log.d("Agora",aurl);
        String aport = port.getText().toString();
        Log.d("Agora",aport);

        editor = settings.edit();

        editor.putString(Aurl,aurl);
        editor.putString(Aport,aport);

        editor.apply();

        Intent intent = new Intent(SettingActivity.this, MainActivity.class);
        startActivity(intent);
    }
}
