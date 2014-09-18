package uk.org.samhipwell.agora;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import org.apache.http.NameValuePair;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.List;


public class CreateRepo extends Activity {

    fileSurport fs;
    EditText repoName;
    Database db;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_repo);

        repoName = (EditText) findViewById(R.id.et_reponame);
        fs = new fileSurport(getApplicationContext());
        db = new Database(getApplicationContext());
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.create_repo, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();
        if (id == R.id.action_settings) {
            Intent intent = new Intent(CreateRepo.this,SettingActivity.class);
            startActivity(intent);
        }
        return super.onOptionsItemSelected(item);
    }

    public void createProject_onClick(View view) throws IOException {
        /**
         * When clicked it will start the service and create the repo of the name.
         * If it works then takes the user to the project screen, else display message.
         */
        String repo = repoName.getText().toString();
        String item = fs.create_repo(this,repo);
        new ProjectCreate().execute(repo.replace(" ","_"));
        if(!item.isEmpty()){
            Intent intent = new Intent(CreateRepo.this,ProjectList.class);
            startActivity(intent);
        }else{
            // directory and repo wasn't created.
            Context context = getApplicationContext();
            CharSequence Toasttext = "Couldn't create the project";
            Toast toast = Toast.makeText(context,Toasttext,Toast.LENGTH_LONG);
            toast.show();

        }
    }

    private class ProjectCreate extends AsyncTask<String,Integer,String>{

        @Override
        protected String doInBackground(String... lists) {
            /**
             * This communicates with the server and sends the request to create the project.
             */

            SharedPreferences settings = getApplicationContext().getSharedPreferences("Agora", MODE_WORLD_READABLE);
            String url = settings.getString("agoraURL"," ");
            String port = settings.getString("agoraPort"," ");

            List<Login> det = db.getLogin();
            String cookie = det.get(0).getCookie();

            List<NameValuePair> postValues = new ArrayList<NameValuePair>(2);
            postValues.add(new BasicNameValuePair("session_key",cookie));
            postValues.add(new BasicNameValuePair("project", lists[0]));
            String aurl = "http://"+url+":"+port+"/app/createproject/";
            HttpPost httppost = new HttpPost(aurl);
            HttpClient httpclient = new DefaultHttpClient();
            try{
                httppost.setEntity(new UrlEncodedFormEntity(postValues, "UTF-8"));
                httpclient.execute(httppost);
            } catch (UnsupportedEncodingException e) {
                e.printStackTrace();
            } catch (ClientProtocolException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }

            return null;
        }
    }


}
