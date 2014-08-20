package uk.org.samhipwell.agora;

import android.app.NotificationManager;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.support.v4.app.NotificationCompat;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.HttpClient;
import org.apache.http.client.ResponseHandler;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.BasicResponseHandler;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


public class serverSync extends AsyncTask<String, Integer, JSONObject> {

    private final static int GET = 5;
    private final static int SEND = 7;
    private final static int UPDATE = 9;

    private Context context;
    private NotificationCompat.Builder mBuilder = new NotificationCompat.Builder(context);


    private Database db;
    JsonGet js;
    private HttpClient httpclient = new DefaultHttpClient();

    public serverSync(Context contextin){ context = contextin;}
    public String SendBack;
    protected String port;

    private String aurl;
    String cookie;
    private String projectName;
    String hashname = null;

    fileSurport fs;




    @Override
    protected void onPreExecute() {
        super.onPreExecute();
        SharedPreferences settings = context.getSharedPreferences("Agora", 1);

        String url = settings.getString("agoraURL", " ");
        port = settings.getString("agoraPort", " ");
        aurl ="http://"+ url +":"+ port +"/app/";

        fs = new fileSurport(context);
        js = new JsonGet();
        db = new Database(context);

    }

    @Override
    protected JSONObject doInBackground(String... parameter) {

        List<NameValuePair> postValues = new ArrayList<NameValuePair>(2);
        List<Login> det = db.getLogin();
        cookie = det.get(0).getCookie();
        long user_id = det.get(0).getUserid();
        //Log.d("Agora cookie",cookie);
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
                        rep.setUrl(fs.create_repo(context, row.getString("name")));
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
                    //Log.e("Agora Server Reply List",serverList.toString());
                    Map<String,Integer> getList = fs.compareList(serverList, projectName);

                    for(Map.Entry<String,Integer> item : getList.entrySet()){
                        String note = item.getKey().substring(0, item.getKey().length() - 5).toLowerCase();
                        switch (item.getValue()){
                            case GET:
                                // if the file request is just to recieve the file then a GET request is sent
                                HttpGet htpget = new HttpGet(aurl+"repo/"+row.getString("url")+"/note/"+note+"/");
                                ResponseHandler<String> responseHandler = new BasicResponseHandler();
                                String serverRes;
                                serverRes = httpclient.execute(htpget,responseHandler);
                                //Log.d("Agora file res=",serverRes);
                                // project name, filename, content
                                fs.writeFile(row.getString("name"), item.getKey().toLowerCase(), serverRes);
                                break;

                            case SEND:
                                // if the file is on the device but not on the server
                                String sendcontent = fs.readFile(row.getString("name"), item.getKey().toLowerCase());
                                HttpPost sendfile = new HttpPost(aurl+"repo/"+row.getString("url")+"/note/upload/"+note+"/");
                                List<NameValuePair> sendvalues = new ArrayList<NameValuePair>(2);
                                sendvalues.add(new BasicNameValuePair("file",sendcontent));
                                sendvalues.add(new BasicNameValuePair("session_key",cookie));

                                httppost.setEntity(new UrlEncodedFormEntity(sendvalues, "UTF-8"));
                                httpclient.execute(sendfile);


                                break;

                            case UPDATE:
                                // the file is on the device and server but needs to be commbined
                                String updatecontent = fs.readFile(row.getString("name"), item.getKey().toLowerCase());
                                HttpPost updatefile = new HttpPost(aurl+"repo/"+row.getString("url")+"/note/check/"+note+"/");
                                List<NameValuePair> updatevalues = new ArrayList<NameValuePair>(2);
                                updatevalues.add(new BasicNameValuePair("file",updatecontent));
                                updatevalues.add(new BasicNameValuePair("session_key",cookie));

                                httppost.setEntity(new UrlEncodedFormEntity(updatevalues, "UTF-8"));
                                HttpResponse updateResponse = httpclient.execute(updatefile);

                                fs.writeFile(row.getString("name"), note, updateResponse.toString());

                                break;

                        }
                    }


                }
            }

        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } catch (JSONException e) {
            e.printStackTrace();
        }



        return null;

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
