package uk.org.samhipwell.agora;

import android.app.NotificationManager;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Environment;
import android.support.v4.app.NotificationCompat;
import android.util.Log;
import org.apache.http.Header;
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.cookie.Cookie;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.eclipse.jgit.api.Git;
import org.eclipse.jgit.api.errors.GitAPIException;
import org.eclipse.jgit.lib.Repository;
import org.eclipse.jgit.storage.file.FileRepositoryBuilder;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import java.io.File;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


public class serverSync extends AsyncTask<String, Integer, JSONObject> {


    Context context;
    NotificationCompat.Builder mBuilder = new NotificationCompat.Builder(context);
    private JSONObject jsonResult;


    Database db;
    JsonGet js;
    HttpClient httpclient = new DefaultHttpClient();

    public serverSync(Context contextin){ context = contextin;}
    public String SendBack;
    protected String url;
    protected String port;

    protected String aurl;
    String cookie;
    String projectName;
    String hashname = null;


    @Override
    protected void onPreExecute() {
        super.onPreExecute();
        SharedPreferences settings = context.getSharedPreferences("Agora", 1);

        url = settings.getString("agoraURL", " ");
        port = settings.getString("agoraPort", " ");
        aurl ="http://"+ url +":"+ port +"/app/";


        js = new JsonGet();
        db = new Database(context);

    }

    @Override
    protected JSONObject doInBackground(String... parameter) {

        List<NameValuePair> postValues = new ArrayList<NameValuePair>(2);
        List<Login> det = db.getLogin();
        cookie = det.get(0).getCookie();
        long user_id = det.get(0).getUserid();
        Log.d("Agora cookie",cookie);
        postValues.add(new BasicNameValuePair("session_key",cookie));

        try {
            HttpPost httppost = new HttpPost(aurl+"data/user/");
            httppost.setEntity(new UrlEncodedFormEntity(postValues, "UTF-8"));
            HttpResponse reponse = httpclient.execute(httppost);

            HttpEntity entity = reponse.getEntity();


            String result = js.convert(entity.getContent());


            JSONArray array = new JSONArray(result);
            for (int i = 0; i < array.length();i++){
                if(i==0){
                    JSONObject row = array.getJSONObject(i);
                    if(row.getString("reply").contains("error")){
                        // problem so
                        SendBack = "ERROR";
                    }
                }else{
                    JSONObject row = array.getJSONObject(i);

                    if(db.getRepo(row.getString("name"))==null) {
                        projectName = row.getString("name");
                        hashname = row.getString("url");
                        Repo rep = new Repo();
                        rep.setRname(projectName);
                        rep.setHash(hashname);
                        rep.setUrl(create_repo(context, row.getString("name")));
                        db.createRepo(rep, user_id);
                    }

                    HttpPost Repopost = new HttpPost(aurl+"repo/"+row.getString("url")+"/");
                    Repopost.setEntity(new UrlEncodedFormEntity(postValues, "UTF-8"));
                    HttpResponse RepoResp = httpclient.execute(Repopost);
                    HttpEntity Repoen = RepoResp.getEntity();


                    String Reporesult = js.convert(Repoen.getContent());

                    //Log.e("Agora","Repo List ="+Reporesult);
                    JSONArray RepArray = new JSONArray(Reporesult);
                    Map<String,Long> serverList = new HashMap<String,Long>();
                    for (int r = 0; r < RepArray.length();r++) {
                        JSONObject reprow = RepArray.getJSONObject(r);
                        serverList.put(reprow.getString("name"),Long.valueOf(reprow.getString("time")));
                    }
                    String getList = compareList(serverList,projectName);

                    // send the getList to the server and get those files.
                }
            }

        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } catch (JSONException e) {
            e.printStackTrace();
        } catch (GitAPIException e) {
            e.printStackTrace();
        }




        return null;

    }

    private String compareList(Map<String, Long> serverList, String projectName) {
       String results = null;
        if(isExternalStorageWritable()){
            String ur = Environment.DIRECTORY_DOCUMENTS +"/"+projectName;
            File file = new File(ur);
            File[] listofFiles = file.listFiles();
            for(File f : listofFiles){
                if(serverList.containsKey(f.toString())){
                    if(serverList.get(f.toString()) != f.lastModified()){
                        results += f.toString()+",";
                    }
                    results += f.toString()+",";
                }
            }

        }
        Log.e("Agora List result",results);
        return results;
    }

    public boolean isExternalStorageWritable(){
        String state = Environment.getExternalStorageState();
        if (Environment.MEDIA_MOUNTED.equals(state)){
            return true;
        } else {
            return false;
        }
    }



    public String create_repo(Context context, String name) throws GitAPIException, IOException {
        /**
         * The idea here is to create a new repository in the app data file on external storage.
         * Once created it will be populated with a .git repo
         */
        File file = null;
        if (isExternalStorageWritable()) {
            // if the external storage is attached. It creates the location
            file = new File(context.getExternalFilesDir(Environment.DIRECTORY_DOCUMENTS), name.replaceAll(" ", "_"));

            if (!file.mkdirs()) {
                // if the directory can't be made then this error will be logged
                Log.d("GitTest", "Directory not created");
                return null;
            }

            // This part of the function then creates the git repo in the folder.
            Git.init().setDirectory(file).call();

            Repository repository = FileRepositoryBuilder.create(new File(file.getAbsolutePath(), ".git"));

            Log.d("GitTest", "Repository Created");

            repository.close();
        }
        return String.valueOf(file.getAbsolutePath());
    }





    protected String onPostExecute(String result) {

        if(SendBack.equals("ERROR")){
            return SendBack;

        }else {
            int mNotificationId = 1;
            mBuilder.setSmallIcon(R.drawable.ic_action_refresh);
            mBuilder.setContentTitle("Agora Has Updated all your notes");
            mBuilder.setContentText("You have now all your notes on backed up on the system.");

            NotificationManager mNotificationManager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
            mNotificationManager.notify(mNotificationId, mBuilder.build());

        }
        return null;
    }


}
