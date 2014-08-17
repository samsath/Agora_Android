package uk.org.samhipwell.agora;

import android.app.NotificationManager;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.support.v4.app.NotificationCompat;
import android.util.Log;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.List;


public class serverSync extends AsyncTask<String, Integer, JSONObject> {


    Context context;
    NotificationCompat.Builder mBuilder = new NotificationCompat.Builder(context);
    private static SharedPreferences settings;
    private static String url;
    private static String port;
    private JSONObject jsonResult;
    private String result;

    HttpPost httppost;
    HttpResponse reponse;

    Database db;

    public serverSync(Context contextin){ context = contextin;}

    @Override
    protected void onPreExecute() {
        super.onPreExecute();
        settings = context.getSharedPreferences("Agora", 1);

        url = settings.getString("agoraURL"," ");
        port = settings.getString("agoraPort"," ");

        httppost = new HttpPost("http://"+url+":"+port+"/app/data/user/");

        db = new Database(context);
    }

    @Override
    protected JSONObject doInBackground(String... parameter) {

        List<NameValuePair> postValues = new ArrayList<NameValuePair>(2);
        List<Login> det = db.getLogin();
        String cookie = det.get(0).getCookie();
        postValues.add(new BasicNameValuePair("session",cookie));

        try {
            httppost.setEntity(new UrlEncodedFormEntity(postValues, "UTF-8"));

            HttpEntity entity = reponse.getEntity();

            InputStream inputstream = entity.getContent();

            BufferedReader reader = new BufferedReader(new InputStreamReader(inputstream,"UTF-8"), 8);
            StringBuilder buidler = new StringBuilder();

            String line;

            while((line =  reader.readLine()) != null){
                buidler.append(line + "\n");
            }
            result = buidler.toString();

            Log.e("Agora Result", result);

            jsonResult = new JSONObject(result);

        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        } catch (JSONException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }


        return null;
    }



    protected void onPostExecute(String result) {

        int mNotificationId = 1;
        mBuilder.setSmallIcon(R.drawable.ic_action_refresh);
        mBuilder.setContentTitle("Agora Has Updated all your notes");
        mBuilder.setContentText("You have now all your notes on backed up on the system.");

        NotificationManager mNotificationManager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
        mNotificationManager.notify(mNotificationId, mBuilder.build());
    }


}
