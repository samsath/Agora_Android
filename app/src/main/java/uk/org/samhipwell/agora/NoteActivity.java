package uk.org.samhipwell.agora;

import android.app.Activity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;

import org.json.JSONArray;
import org.json.JSONException;


public class NoteActivity extends Activity {

    public String FilePath;
    EditText content;
    String textColour;
    String bgColour;
    public String datetime;
    public String uname;
    public String type;
    public String ocontent;

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
            try {
                JSONArray array = new JSONArray(fs.readFile(FilePath));
                datetime = array.getJSONObject(0).getString("datetime");
                uname = array.getJSONObject(0).getString("user");
                ocontent = array.getJSONObject(0).getString("content");
                type = array.getJSONObject(0).getString("type");
                textColour = array.getJSONObject(0).getString("tx");
                bgColour = array.getJSONObject(0).getString("bg");
                //TODO add for commets
                } catch (JSONException e) {
                e.printStackTrace();
            }

            content.setText(ocontent);

        }else{
            // This is to create a new note
        }
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.note, menu);
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

    public void textColourClick(View view) {
        // TODO this will set the value for the text colour
    }

    public void bgColourClick(View view) {
        // TODO this will set the value for the Background Colour
    }

    public void noteSaveClick(View view) {
        //TODO this will save the note to the file
    }
}
