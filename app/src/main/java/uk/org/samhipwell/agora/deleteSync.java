package uk.org.samhipwell.agora;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.util.Log;

import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;

import java.io.File;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by sam on 23/08/14.
 */
public class deleteSync extends AsyncTask<String,Integer,String> {

    private Context context;
    private HttpClient httpclient = new DefaultHttpClient();
    private Database db;
    protected String port;

    public String note;
    public String project;
    public String oldfile;

    public Repo repo;

    private String aurl;
    String cookie;
    private String projectName;

    public deleteSync(Context contexts,String filepath){
        context = contexts;
        oldfile = filepath;

        db = new Database(context);

        File file = new File(filepath);
        if(file.isDirectory()){
            project = file.getName();
        }else{
            note = file.getName();
            File p = new File(file.getParent());
            project = p.getName();
            Log.i("Agora ===", "Note = " + note + " Project = " + project);
        }

        repo = db.getRepo(project);

        SharedPreferences settings = context.getSharedPreferences("Agora", 1);

        String url = settings.getString("agoraURL", " ");
        String port = settings.getString("agoraPort", " ");
        aurl ="http://"+ url +":"+ port +"/app/";

    }



    @Override
    protected String doInBackground(String... strings) {
        String res ="";
        File old = new File(oldfile);
        old.renameTo(new File(oldfile+".delete"));

        Log.i("Agora Note Dele","path = "+old.toString());

        List<Login> det = db.getLogin();
        cookie = det.get(0).getCookie();
        long user_id = det.get(0).getUserid();

        String urlused = aurl+"note/delete/"+repo.hash+"/"+note.replace(".note","")+"/";
        Log.i("Agora Note Delete url",urlused);
        try {
            HttpPost httppet = new HttpPost(urlused);
            List<NameValuePair> sendvalues = new ArrayList<NameValuePair>(2);
            sendvalues.add(new BasicNameValuePair("file",old.toString()));
            sendvalues.add(new BasicNameValuePair("session_key",cookie));
            httppet.setEntity(new UrlEncodedFormEntity(sendvalues, "UTF-8"));
            HttpResponse r = httpclient.execute(httppet);
            res = r.toString();
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        } catch (ClientProtocolException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }

        return res;
    }
}
