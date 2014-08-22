package uk.org.samhipwell.agora;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.ClientProtocolException;
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
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutionException;



public class RegisterActivity extends Activity {
    public static final int RESULT_OK = 7;
    EditText username,password,firstname,lastname,email;
    ImageView imageview;
    private String imagePath;
    private static String url = "";
    private static String port = "";


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);
        username = (EditText) findViewById(R.id.reg_username);
        password = (EditText) findViewById(R.id.reg_password);
        firstname = (EditText) findViewById(R.id.reg_firstname);
        lastname = (EditText) findViewById(R.id.reg_lastname);
        email = (EditText) findViewById(R.id.reg_email);

        imageview = (ImageView) findViewById(R.id.imageView1);

        SharedPreferences settings = this.getSharedPreferences("Agora", MODE_WORLD_READABLE);

        url = settings.getString("agoraURL", " ");
        port = settings.getString("agoraPort", " ");

    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.register, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();
        if (id == R.id.action_settings) {
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    public void ImageViewClick(View view) {
        Intent imageintent = new Intent(Intent.ACTION_PICK, android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);

        startActivityForResult(imageintent, RESULT_OK);
    }

    @Override
    protected void onActivityResult(int reqCode, int resCode, Intent idata){
		/*
		 * This is activated once the intent brings back a result, it will set the image box to the image selected and
		 * convert the URI into a bitmap for the image. Also saves the image path as a string which will be added to the
		 * content provide when saved.
		 */
        super.onActivityResult(reqCode, resCode, idata);

        if(reqCode == RESULT_OK){
            if( resCode == RESULT_OK){
                if(idata != null){

                    Uri selectImageLoc = idata.getData();
                    String[] filePathColumn = {MediaStore.Images.Media.DATA};

                    Cursor c = getContentResolver().query(selectImageLoc, filePathColumn, null, null, null);
                    c.moveToFirst();

                    int colIndex = c.getColumnIndex(filePathColumn[0]);
                    imagePath = c.getString(colIndex);
                    c.close();

                    imageview.setImageBitmap(BitmapFactory.decodeFile(imagePath));
                }
            }
        }
    }


    public void btnSave(View view) throws ExecutionException, InterruptedException {

        List<String> data = new ArrayList<String>();

        data.add(username.getText().toString());
        data.add(password.getText().toString());
        data.add(firstname.getText().toString());
        data.add(lastname.getText().toString());
        data.add(email.getText().toString());
        data.add(imagePath);
        data.add("http://"+url+":"+port+"/app/register/");



        RegisterCheck regcheck = new RegisterCheck();
        regcheck.execute(data).get();

        JSONObject jsonResult = regcheck.onPostExecute();

        try {
            if(jsonResult.getString("user").equals("created")){


                User user = new User();
                user.setUsername(username.getText().toString());
                user.setEmail(email.getText().toString());
                user.setFirst_name(firstname.getText().toString());
                user.setLast_name(lastname.getText().toString());

                Database db = new Database(this);
                long id = db.createUser(user);
                Login login = new Login(id,jsonResult.getString("cookie"));
                long result = db.createLogin(login);
                Log.e("Agora",login.toString());
                Log.e("Agora","Created "+ result +"of login details");

                Intent intent = new Intent(RegisterActivity.this,SyncActiity.class);
                startActivity(intent);


            }else{

                username.setText("Different User name here please!");
                password.setText("");
                Context context = getApplicationContext();
                CharSequence Toasttext = "There is already a user of that name, please pick a new one.";
                Toast toast = Toast.makeText(context,Toasttext,Toast.LENGTH_LONG);
                toast.show();
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }

    }

    private class RegisterCheck extends AsyncTask<List<String>,Integer,Boolean>{
        protected JSONObject jsonResult = null;
        HttpClient httpclient = new DefaultHttpClient();
        @Override
        protected Boolean doInBackground(List<String>... request) {

            String username = request[0].get(0);
            String password = request[0].get(1);
            String first = request[0].get(2);
            String last = request[0].get(3);
            String email = request[0].get(4);
            String impath = request[0].get(5);
            String url = request[0].get(6);

            String result;

            Log.e("Agora","Register Async");

            HttpPost httppost = new HttpPost(url);
            HttpResponse httpResponse;

            List<NameValuePair> postValues = new ArrayList<NameValuePair>(2);
            postValues.add(new BasicNameValuePair("username",username));
            postValues.add(new BasicNameValuePair("password",password));
            postValues.add(new BasicNameValuePair("firstname",first));
            postValues.add(new BasicNameValuePair("lastname",last));
            postValues.add(new BasicNameValuePair("email",email));

            try{
                httppost.setEntity(new UrlEncodedFormEntity(postValues, "UTF-8"));
                httpResponse = httpclient.execute(httppost);

                HttpEntity entity = httpResponse.getEntity();

                InputStream inputstream = entity.getContent();

                BufferedReader reader = new BufferedReader(new InputStreamReader(inputstream,"UTF-8"), 8);
                StringBuilder buidler = new StringBuilder();

                String line;

                while((line =  reader.readLine()) != null){
                    buidler.append(line + "\n");
                }
                result = buidler.toString();

                Log.e("Agora Result = ",result);
                jsonResult = new JSONObject(result);

                Log.e("Agora User = ",jsonResult.getString("user"));



            } catch (UnsupportedEncodingException e) {
                e.printStackTrace();
            } catch (ClientProtocolException e) {
                e.printStackTrace();
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

    @Override
    protected void onStop(){
        /**
         * When the activity stops the project list is cleared
         */
        Log.d("Agora","Register Stopped");
        super.onStop();
        username.setText("");
        password.setText("");
        firstname.setText("");
        lastname.setText("");
        email.setText("");
    }

}
