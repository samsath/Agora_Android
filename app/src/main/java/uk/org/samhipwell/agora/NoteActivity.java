package uk.org.samhipwell.agora;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
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
    int textColour;
    int bgColour;
    public int datetime;
    public String uname;
    public String type;
    public String ocontent;
    public HashMap<Integer,List<String>> Comments = new HashMap<Integer, List<String>>();

    fileSurport fs;
    JsonGet js;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_note);

        Bundle bundle = getIntent().getExtras();
        FilePath = bundle.getString("path");

        fs = new fileSurport(this);
        js = new JsonGet();

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
                        String datatime = String.valueOf((commentArray.getJSONObject(j).getInt("DateTime")));
                        String body = commentArray.getJSONObject(j).getString("Body");

                        List<String> comment = new ArrayList<String>();
                        comment.add(user);
                        comment.add(datatime);
                        comment.add(body);

                        Comments.put(j,comment);
                    }
                }
                datetime = noteObject.getInt("datetime");
                uname = noteObject.getString("user");
                ocontent = noteObject.getString("content");
                textColour = Color.parseColor(noteObject.getString("tx"));
                bgColour = Color.parseColor(noteObject.getString("bg"));
                type = noteObject.getString("type");
            } catch (JSONException e) {
                e.printStackTrace();
            }



            content.setText(ocontent);
            content.setBackgroundColor(bgColour);
            content.setTextColor(textColour);

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
                    textColour = Color.parseColor(bundle.getString("colour"));
                    content.setTextColor(textColour);

                } else if (type == D_BACK) {
                    bgColour = Color.parseColor(bundle.getString("colour"));
                    content.setBackgroundColor(bgColour);
                }

            } else if (requestCode == COMMENT_BACK) {

                Comments = (HashMap<Integer,List<String>>) bundle.getSerializable("HashMap");

            }
        }
    }

    public void noteSaveClick() {
        //TODO this will save the note to the file
        if(typeNote){
            // this is an old note so re-write it
        }else{

        }

    }


}