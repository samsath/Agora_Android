package uk.org.samhipwell.agora;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;

import org.apache.http.NameValuePair;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.message.BasicNameValuePair;
import org.eclipse.jgit.diff.Edit;

import java.util.ArrayList;
import java.util.List;


public class loginScrenn extends Activity {

    static SharedPreferences settings;
    private static String url = "";
    private static String port = "";

    HttpClient httpclient;
    HttpPost httppost;

    EditText uname,pwd;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_loginscreen);
        settings = this.getPreferences(MODE_WORLD_WRITEABLE);
        if(settings.contains("agoraURL")){
            url = settings.getString("agoraURL"," ");
        }
        if(settings.contains("agoraPort")){
            port = settings.getString("agoraPort"," ");
        }

        uname = (EditText) findViewById(R.id.etUsername);
        pwd = (EditText) findViewById(R.id.etPassword);
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.

        switch (item.getItemId()){
            case R.id.action_settings:
                Intent intent = new Intent(loginScrenn.this,SettingActivity.class);
                startActivity(intent);

            default:
                return super.onOptionsItemSelected(item);
        }
    }


    public void signinClick(View view) {

        List<NameValuePair> postValues = new ArrayList<NameValuePair>(2);
        postValues.add(new BasicNameValuePair("username",uname.getText().toString()));
        postValues.add(new BasicNameValuePair("password",pwd.getText().toString()));

        // TODO http://www.androidsnippets.com/executing-a-http-post-request-with-httpclient

    }

    public void registerClick(View view) {
    }
}
