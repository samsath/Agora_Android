package uk.org.samhipwell.agora;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.NavUtils;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutionException;


public class loginScrenn extends Activity {

    static SharedPreferences settings;
    private static String url = "";
    private static String port = "";


    HttpClient httpclient = new DefaultHttpClient();
    HttpPost httppost;

    EditText uname,pwd;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_loginscreen);

        Log.e("Agora","LoginScreen Started");

        settings = this.getSharedPreferences("Agora", MODE_WORLD_READABLE);
       // if(settings.contains("agoraURL")){
            url = settings.getString("agoraURL"," ");
            Log.d("Agora","url =" +url);
        //}
       // if(settings.contains("agoraPort")){
            port = settings.getString("agoraPort"," ");
            Log.d("Agora","port =" +port);
       // }
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
            case android.R.id.home:
                NavUtils.navigateUpFromSameTask(this);
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }


    public void signinClick(View view) throws ExecutionException, InterruptedException {

        List<String> data = new ArrayList<String>();
        String username = uname.getText().toString();
        String password = pwd.getText().toString();


        data.add("http://"+url+":"+port+"/app/"+username+"/");
        data.add(username);
        data.add(password);

        Log.d("Agora",data.toString());

        LoginCheck logcheck = new LoginCheck();
        logcheck.execute(data).get();

        /**
         * if the request has a the logged in info then the information gets sent to the
         * db else a toast saying that
         */
        JSONObject jsonResult = logcheck.onPostExecute();

        try {
            if(jsonResult.getString("logged").equals("Welcome")){
                Log.d("Agora UI",jsonResult.getString("first_name"));

                User user = new User();
                user.setUsername(username);
                user.setEmail(jsonResult.getString("email"));
                user.setFirst_name(jsonResult.getString("first_name"));
                user.setLast_name(jsonResult.getString("last_name"));

                Database db = new Database(this);
                long id = db.createUser(user);
                Login login = new Login(id,jsonResult.getString("cookie"));
                long result = db.createLogin(login);

                Intent intent = new Intent(loginScrenn.this,SyncActiity.class);
                startActivity(intent);


            }else{
                uname.setText("User name here please!");
                pwd.setText("");
                Context context = getApplicationContext();
                CharSequence Toasttext = "User login details wrong!";
                Toast toast = Toast.makeText(context,Toasttext,Toast.LENGTH_LONG);
                toast.show();
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }


    }

    public void registerClick(View view) {
        Intent intent = new Intent(loginScrenn.this,RegisterActivity.class);
        startActivity(intent);
    }


    private class LoginCheck extends AsyncTask<List<String>, Integer, JSONObject> {

        protected JSONObject jsonResult = null;

        @Override
        protected JSONObject doInBackground(List<String>... request) {
            //  http://www.androidsnippets.com/executing-a-http-post-request-with-httpclient
            //  http://stackoverflow.com/questions/6343166/android-os-networkonmainthreadexception


            String result;

            String url = request[0].get(0);
            String user = request[0].get(1);
            String password = request[0].get(2);

            Log.e("Agora", url);
            httppost = new HttpPost(url);
            Log.e("Agora", httppost.toString());
            HttpResponse response;

            List<NameValuePair> postValues = new ArrayList<NameValuePair>(2);
            postValues.add(new BasicNameValuePair("username", user));
            postValues.add(new BasicNameValuePair("password", password));

           try {
               Log.e("Agora", "post value -" + postValues.toString());
               httppost.setEntity(new UrlEncodedFormEntity(postValues, "UTF-8"));
               response = httpclient.execute(httppost);

               HttpEntity entity = response.getEntity();

               InputStream inputstream = entity.getContent();

               BufferedReader reader = new BufferedReader(new InputStreamReader(inputstream,"UTF-8"), 8);
               StringBuilder buidler = new StringBuilder();

               String line;

               while((line =  reader.readLine()) != null){
                   buidler.append(line + "\n");
               }
               result = buidler.toString();

               Log.e("Agora Result",result);

               jsonResult = new JSONObject(result);

               Log.d("Agora",jsonResult.getString("first_name"));



            } catch (IOException e) {
                e.printStackTrace();
            } catch (JSONException e) {
               e.printStackTrace();
           }


            return null;
        }

        protected JSONObject onPostExecute(){
            return jsonResult;
        }

    }
}
