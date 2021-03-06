package uk.org.samhipwell.agora;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.EditText;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;



public class NoteActivity extends Activity {
    /**
     * This is the activity to show the whole note
     */

    public static final int COLOUR_PICKER = 5555;
    public static final int COMMENT_BACK = 4444;
    public static final int RESULT_OK = 4;
    public static final int D_TEXT = 10;
    public static final int D_BACK = 15;

    private Boolean typeNote;

    public String FilePath;
    EditText content;
    String textColour = "#2d2d2d";
    String bgColour = "#e1e1e1";
    public int datetime;
    public String uname;
    public String type;
    public String ocontent;
    public HashMap<Integer,List<String>> Comments = new HashMap<Integer, List<String>>();
    public HashMap<Integer,List<String>> ArchiveList = new HashMap<Integer, List<String>>();
    fileSurport fs;
    Database db;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_note);

        Bundle bundle = getIntent().getExtras();

        // if there is a bundle then it loads the note if not creates a blank one.
        if(bundle!=null) {
            FilePath = bundle.getString("path");
        }
        fs = new fileSurport(this);
        db = new Database(this);
        noteLoad();
    }

    private void noteLoad(){
        /**
         * If a file is sent to it then it will load the file up if not it creates a blank file.
         */

        content = (EditText) findViewById(R.id.et_NoteBody);

        if(FilePath.endsWith(".note")){
            // load an old note into the project
            typeNote = true;
            StringBuilder text = new StringBuilder();
            try{
                BufferedReader br = new BufferedReader(new FileReader(new File(FilePath)));
                String Line;
                while((Line = br.readLine())!=null){
                    text.append(Line);
                }
            } catch (FileNotFoundException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }


            try {
                JSONObject mainObj = new JSONObject(String.valueOf(text));
                JSONObject noteObject = mainObj.getJSONObject("note");
                try{
                    JSONObject commentObject = mainObj.getJSONObject("comment");
                } catch (JSONException e) {
                    JSONArray commentArray = mainObj.getJSONArray("comment");
                    for(int j = 0; j < commentArray.length(); j++){

                        String user = commentArray.getJSONObject(j).getString("user");
                        String datetime = String.valueOf(commentArray.getJSONObject(j).getInt("datetime"));
                        String body = commentArray.getJSONObject(j).getString("body");

                        List<String> comment = new ArrayList<String>();
                        comment.add(user);
                        comment.add(datetime);
                        comment.add(body);

                        Comments.put(j,comment);
                    }
                }

                try{
                    JSONObject archiveObject = mainObj.getJSONObject("comment");
                } catch (JSONException e) {
                    JSONArray archiveArray = mainObj.getJSONArray("comment");
                    for(int a =0;a<archiveArray.length();a++){
                        String user = archiveArray.getJSONObject(a).getString("user");
                        String datetime = String.valueOf(archiveArray.getJSONObject(a).getInt("datetime"));
                        String body = archiveArray.getJSONObject(a).getString("body");

                        List<String> arch = new ArrayList<String>();
                        arch.add(user);
                        arch.add(datetime);
                        arch.add(body);

                        ArchiveList.put(a,arch);
                    }


                }

                datetime = noteObject.getInt("datetime");
                uname = noteObject.getString("user");
                ocontent = noteObject.getString("content");
                textColour = noteObject.getString("tx");
                bgColour = noteObject.getString("bg");
                type = noteObject.getString("type");
            } catch (JSONException e) {
                e.printStackTrace();
            }

            content.setText(ocontent);
            content.setBackgroundColor(Color.parseColor(bgColour));
            content.setTextColor(Color.parseColor(textColour));

        }else{
            // This is to create a new note
            typeNote = false;
        }
    }



    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.note_menu, menu);
        setTitle("");
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        switch (item.getItemId()){
            case android.R.id.home:
                noteSave();
                return true;
            case R.id.sharebutton:
                Intent sintent = new Intent(NoteActivity.this,ShareActivity.class);
                Bundle sbundle = new Bundle();
                sbundle.putString("path",FilePath);
                sintent.putExtras(sbundle);
                startActivity(sintent);
                return true;
            case R.id.TextColour:
                // set the text colour
                colourClick(D_TEXT);
                return true;
            case R.id.BGColour:
                // set the background colour
                colourClick(D_BACK);
                return true;
            case R.id.save:
                //save the note
                noteSave();
                return true;
            case R.id.comment:
                // Comments of the note
                commentClick();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    /**
     * This here is to save the current note object if it is closed and reopened. Only works on the
     * already saved notes.
     */
    @Override
    public void onSaveInstanceState(Bundle savedInstanceState){
        super.onSaveInstanceState(savedInstanceState);
        savedInstanceState.putString("path",FilePath);
    }

    @Override
    public void onRestoreInstanceState(Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);
        FilePath = savedInstanceState.getString("path");
        noteLoad();
    }


    public void colourClick(int type){
        /**
         * Starts the colour picker activity with what the colour is for.
         */
        Intent intent = new Intent(NoteActivity.this,ColourPicker.class);

        String active = String.valueOf(COLOUR_PICKER);
        Bundle bundle = new Bundle();
        bundle.putString("code",active);
        bundle.putInt("type", type);
        intent.putExtras(bundle);

        startActivityForResult(intent,COLOUR_PICKER);
    }

    public void commentClick(){
        /**
         * Starts the comment activity and send the comments to the activty.
         */
        Intent intent = new Intent(NoteActivity.this,CommentActivity.class);
        Bundle bundle = new Bundle();
        bundle.putString("path",FilePath);
        bundle.putSerializable("HashMap",Comments);
        intent.putExtras(bundle);

        startActivityForResult(intent,COMMENT_BACK);

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data){
        /**
         * This waits for a reply for both the comment and colour activity and depending on
         * what information it sends back it hands it differently.
         */

        if(resultCode == RESULT_OK) {
            Bundle bundle = data.getExtras();
            if (requestCode == COLOUR_PICKER) {

                int type = bundle.getInt("type");
                if (type == D_TEXT) {
                    textColour = bundle.getString("colour");
                    content.setTextColor(Color.parseColor(textColour));

                } else if (type == D_BACK) {
                    bgColour = bundle.getString("colour");
                    content.setBackgroundColor(Color.parseColor(bgColour));
                }

            } else if (requestCode == COMMENT_BACK) {
                FilePath = bundle.getString("path");
                Comments = (HashMap<Integer,List<String>>) bundle.getSerializable("HashMap");

            }
        }
    }


    public void noteSave() {
        /**
         * Saves the note to the file
         */
        String outputString = "";
        try {
            /*
                This is to arrange the file info into the json format
             */

            // Josn main
            JSONObject json = new JSONObject();

            //Note
            JSONObject noteJson = new JSONObject();
            noteJson.put("user", db.getUsername());
            noteJson.put("bg",bgColour);
            noteJson.put("tx",textColour);
            noteJson.put("datetime",System.currentTimeMillis());
            noteJson.put("type","html");
            noteJson.put("content",content.getText());
            json.put("note",noteJson);

            // Comment
            JSONArray commentJson = new JSONArray();
            for(int i =0 ; i < Comments.size();i++){
                JSONObject comment = new JSONObject();
                comment.put("user",Comments.get(i).get(0));
                comment.put("datetime",Comments.get(i).get(1));
                comment.put("body",Comments.get(i).get(2));
                commentJson.put(comment);
            }
            json.put("comment",commentJson);

            //Archive
            JSONArray archiveJson = new JSONArray();

            JSONObject archJson = new JSONObject();
            archJson.put("user", db.getUsername());
            archJson.put("datetime",System.currentTimeMillis());
            archJson.put("body",content.getText());
            archiveJson.put(archJson);

            for(int a =0; a<ArchiveList.size();a++){
                JSONObject arch = new JSONObject();
                arch.put("user",ArchiveList.get(a).get(0));
                arch.put("datatime",ArchiveList.get(a).get(1));
                arch.put("body",ArchiveList.get(a).get(2));
                archiveJson.put(arch);
            }
            json.put("archive",archiveJson);

            //Putting them all together
            outputString = json.toString();

        } catch (JSONException e) {
            e.printStackTrace();
            Log.e("Agora", "Could not create JSON object Crash");
            finish();
        }
        /*
            Sort out if the file is a new one so loads it or creates a new one.
         */
        String url;
        if(typeNote){
            // this is an old note so re-write it
           url = FilePath;
        }else{
            String time = String.valueOf(System.currentTimeMillis());
            String uname = db.getUsername();
            url = FilePath+"/"+time+uname+"android.note";
        }
        /*
            writes the string to the system
         */
        Log.e("Agora","The Note String === " + outputString);
        fs.writeFile(url,outputString);
        finish();
    }
}
