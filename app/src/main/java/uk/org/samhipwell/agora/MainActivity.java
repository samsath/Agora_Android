package uk.org.samhipwell.agora;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Environment;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.GridView;

import java.io.File;
import java.util.ArrayList;



public class MainActivity extends Activity {
    private CharSequence mTitle;
    private File[] notelist;
    private static ArrayAdapter<File> fileAdaptor;
    private String project;
    private String ur;

    fileSurport fs;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Bundle bundle = getIntent().getExtras();
        project = bundle.getString("Project");
        ur = this.getExternalFilesDir(Environment.DIRECTORY_DOCUMENTS).toString();
        fs = new fileSurport(this);
        Log.e("Agora","URL = "+ur);
        queryNotes();


        final GridView gridView =(GridView)findViewById(R.id.gridView);
        gridView.setAdapter(new GridAdaptor(this,notelist));

        gridView.setOnItemClickListener(new AdapterView.OnItemClickListener() {

            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                Intent intent = new Intent(MainActivity.this,NoteActivity.class);
                File note = (File) adapterView.getAdapter().getItem(i);
                String text = note.getAbsolutePath();
                Bundle bundle = new Bundle();
                bundle.putString("path",text);
                intent.putExtras(bundle);
                startActivity(intent);
            }
        });

    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
        getActionBar().setTitle(mTitle);
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

    private void queryNotes(){
        //TODO file recursion here
        if(fs.isExternalStorageWritableReader()) {
            if (project.equals("all")) {
                // get all the notes
                mTitle = "All Notes";

                ArrayList<File> newlist = new ArrayList<File>();
                File[] check = new File(ur).listFiles();

                for (File f : check) {
                    if (f.isDirectory()) {
                        for (File s : f.listFiles()) {
                            newlist.add(s);
                        }
                    } else {
                        newlist.add(f);
                    }
                }
                File[] test = new File[newlist.size()];
                for (int i = 0; i < newlist.size(); i++) {
                    test[i] = newlist.get(i);
                }
                notelist = test;
            } else {
                // get the notes in the certain file.
                mTitle = project;
                ur += "/" + project;
                // notelist = (List<File>) FileUtils.listFiles(new File(ur),new String[] { "note" },true);
                notelist = new File(ur).listFiles();

            }
        }else{
            Log.e("Agora","Not fiel system readable");
        }
    }


}
