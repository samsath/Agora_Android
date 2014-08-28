package uk.org.samhipwell.agora;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.util.Log;

import org.apache.http.NameValuePair;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.List;


public class shareSync extends AsyncTask<String,Integer,JSONObject> {
    /**
     * This is the shareSync async task which works with the share activity. This is sent a list of
     * emails to relay to the server.
     */
    private Context context;
    private HttpClient httpclient = new DefaultHttpClient();
    public String SendBack;

    public String project;
    public String note;

    ArrayList<String> emailsList = new ArrayList<String>();

    protected String aurl;
    String cookie;

    private Database db;
    fileSurport fs;
    Repo repo;

    JSONObject sendcontent = new JSONObject();

    public shareSync(Context contect, String filepat, ArrayList<String> emails){
        /*
            produces the contact list into a json.
         */
        context = contect;
        emailsList = emails;

        File file = new File(filepat);
        if(file.isDirectory()){
            project = file.getName();
        }else{
            note = file.getName();
            project = file.getParent();
            Log.i("Agora ===", "Note = " + note + " Project = " + project);
        }

        try {

            JSONArray Jcont = new JSONArray();
            for(int i = 0; i < emails.size();i++){
                Jcont.put(emails.get(i));
            }
            sendcontent.put("email",Jcont);
        } catch (JSONException e) {
            e.printStackTrace();
        }

    }


    @Override
    protected void onPreExecute() {
        super.onPreExecute();
        SharedPreferences settings = context.getSharedPreferences("Agora", 1);

        String url = settings.getString("agoraURL", " ");
        String port = settings.getString("agoraPort", " ");
        aurl ="http://"+ url +":"+ port +"/app/";

        fs = new fileSurport(context);
        db = new Database(context);
        repo = db.getRepo(project);

    }

    @Override
    protected JSONObject doInBackground(String... strings) {
        /*
            Sends the emails to the server in the form of a json.
         */
        List<Login> det = db.getLogin();
        cookie = det.get(0).getCookie();
        long user_id = det.get(0).getUserid();


        String urlused;
        if(note!=null){
            urlused = aurl+"share/note/"+repo.hash+"/"+note.replace(".note","")+"/";
        }else{
            urlused = aurl+"share/project/"+repo.hash+"/";
        }
        try {
            HttpPost httppet = new HttpPost(urlused);
            List<NameValuePair> sendvalues = new ArrayList<NameValuePair>(2);
            sendvalues.add(new BasicNameValuePair("email",sendcontent.toString()));
            sendvalues.add(new BasicNameValuePair("session_key",cookie));
            httppet.setEntity(new UrlEncodedFormEntity(sendvalues, "UTF-8"));
            httpclient.execute(httppet);
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
