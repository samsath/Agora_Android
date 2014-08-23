package uk.org.samhipwell.agora;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Point;
import android.os.Bundle;
import android.os.Environment;
import android.preference.PreferenceManager;
import android.util.Log;
import android.view.Display;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ArrayAdapter;
import android.widget.GridView;

import java.io.File;



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
        if(bundle==null){
            project = "all";
            mTitle = "All Notes";
        }else {
            project = bundle.getString("Project");
            mTitle = project.replaceAll("_"," ");
        }
        ur = this.getExternalFilesDir(Environment.DIRECTORY_DOCUMENTS).toString();
        fs = new fileSurport(this);
        Log.e("Agora","URL = "+ur+"/"+project);

        start();

    }

    public void start(){
        Display display = getWindowManager().getDefaultDisplay();
        Point size = new Point();
        display.getSize(size);
        int columNum = size.x/480;

        GridView grdiview = (GridView) findViewById(R.id.gridView);
        grdiview.setNumColumns(columNum);
        grdiview.setAdapter(new NoteAdaptor(this,ur+"/"+project));
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        if(project.equals("all")) {
            getMenuInflater().inflate(R.menu.main, menu);
        }else{
            getMenuInflater().inflate(R.menu.project_main, menu);
        }
        setTitle(mTitle);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        switch (item.getItemId()){
            case R.id.action_settings:
                Intent intent = new Intent(MainActivity.this,SettingActivity.class);
                startActivity(intent);
                return true;
            case R.id.projectbutton:
                Intent pintent = new Intent(MainActivity.this,ProjectList.class);
                startActivity(pintent);
                return true;
            case R.id.createbutton:
                if(project.equals("all")){
                    Intent cintent = new Intent(MainActivity.this,CreateRepo.class);
                    startActivity(cintent);
                }else{
                    Bundle nbundle =new Bundle();
                    nbundle.putString("path",ur+"/"+project);
                    Intent nintent = new Intent(MainActivity.this,NoteActivity.class);
                    nintent.putExtras(nbundle);
                    startActivity(nintent);
                }
                return true;
            case R.id.sharebutton:
                Intent sintent = new Intent(MainActivity.this,ShareActivity.class);
                Bundle sbundle = new Bundle();
                sbundle.putString("path",ur+"/"+project);
                sintent.putExtras(sbundle);
                startActivity(sintent);

                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    @Override
    protected void onStart(){
        super.onStart();
        start();
    }

    @Override
    public void onResume(){
        super.onResume();
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(this);

    }


}
