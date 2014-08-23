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
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;



public class NoteActivity extends Activity {

    public static final int COLOUR_PICKER = 5555;
    public static final int ACTIVITY_REQUEST_CODE = 6666;
    public static final int COMMENT_BACK = 4444;
    public static final int RESULT_OK = 4;
    public static final int D_TEXT = 10;
    public static final int D_BACK = 15;

    private Boolean typeNote;

    public String FilePath;
    EditText content;
    String textColour;
    String bgColour;
    public int datetime;
    public String uname;
    public String type;
    public String ocontent;
    public HashMap<Integer,List<String>> Comments = new HashMap<Integer, List<String>>();

    fileSurport fs;
    JsonGet js;
    Database db;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_note);

        Bundle bundle = getIntent().getExtras();
        FilePath = bundle.getString("path");

        fs = new fileSurport(this);
        js = new JsonGet();
        db = new Database(this);

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

                        String user = commentArray.getJSONObject(j).getString("User");
                        String datetime = String.valueOf((commentArray.getJSONObject(j).getInt("DateTime")));
                        String body = commentArray.getJSONObject(j).getString("Body");

                        List<String> comment = new ArrayList<String>();
                        comment.add(user);
                        comment.add(datetime);
                        comment.add(body);

                        Comments.put(j,comment);
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
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        switch (item.getItemId()){
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
                noteSaveClick();
                return true;
            case R.id.comment:
                // Comments of the note
                commentClick();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }


    public void colourClick(int type){
        Intent intent = new Intent(NoteActivity.this,ColourPicker.class);

        String active = String.valueOf(COLOUR_PICKER);
        Bundle bundle = new Bundle();
        bundle.putString("code",active);
        bundle.putInt("type", type);
        intent.putExtras(bundle);

        startActivityForResult(intent,COLOUR_PICKER);
    }

    public void commentClick(){
        Intent intent = new Intent(NoteActivity.this,CommentActivity.class);
        Bundle bundle = new Bundle();
        bundle.putSerializable("HashMap",Comments);
        intent.putExtras(bundle);

        startActivityForResult(intent,COMMENT_BACK);

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data){
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

                Comments = (HashMap<Integer,List<String>>) bundle.getSerializable("HashMap");

            }
        }
    }

    public void noteSaveClick() {
        //TODO this will save the note to the file
        String outputString = "";
        try {
            // Josn main
            JSONObject json = new JSONObject();
            //Note
            JSONObject noteJson = new JSONObject();
            noteJson.put("user", uname);
            noteJson.put("bg",bgColour);
            noteJson.put("tx",textColour);
            noteJson.put("datetime",System.currentTimeMillis());
            noteJson.put("type",type);
            noteJson.put("content",content.getText());
            json.put("note",noteJson);
            // Comment
            JSONArray commentJson = new JSONArray();
            for(int i =0 ; i < Comments.size();i++){
                JSONObject comment = new JSONObject();
                comment.put("Uer",Comments.get(i).get(0));
                comment.put("DateTime",Comments.get(i).get(1));
                comment.put("Body",Comments.get(i).get(2));
                commentJson.put(comment);
            }
            json.put("comment",commentJson);
            outputString = json.toString();

        } catch (JSONException e) {
            e.printStackTrace();
            Log.e("Agora", "Could not create JSON object Crash");
            finish();
        }


        String url;
        if(typeNote){
            // this is an old note so re-write it
           url = FilePath;
        }else{
            String time = String.valueOf(System.currentTimeMillis());
            String uname = db.getUsername();
            url = FilePath+"/"+time+uname+"android.note";
        }
        try{
            Log.e("Agora","The Note String === " + outputString);

            File file = new File(url);

            FileWriter output = new FileWriter(file);

            output.write(outputString);
            output.flush();
            output.close();
            finish();

        } catch (IOException e) {
            e.printStackTrace();
        }

    }


}
