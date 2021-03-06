package uk.org.samhipwell.agora;

import android.app.ListActivity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Environment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;
import android.widget.Toast;

import java.io.File;
import java.util.ArrayList;


public class ProjectList extends ListActivity  {
    /**
     * This is the activity which shows all the current user's projects.
     * By means of list inflation
     */

    private static ArrayList<ProjectsData>projectList = new ArrayList<ProjectsData>();
    private static ArrayAdapter<ProjectsData> projectAdaptor;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_projectlist);
        Log.d("Agora","Project List create");

    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.create_repo, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.

        switch (item.getItemId()){
            case R.id.action_settings:
                Intent intent = new Intent(ProjectList.this,SettingActivity.class);
                startActivity(intent);
                return true;
            case R.id.createrepo:
                Intent cintent = new Intent(ProjectList.this,CreateRepo.class);
                startActivity(cintent);
                return true;
            case R.id.allnotes:
                Intent mintent = new Intent(ProjectList.this,MainActivity.class);
                startActivity(mintent);
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }


    public boolean isExternalStorageReadable() {
        String state = Environment.getExternalStorageState();
        if (Environment.MEDIA_MOUNTED.equals(state) ||
                Environment.MEDIA_MOUNTED_READ_ONLY.equals(state)) {
            return true;
        }
        return false;
    }


    private void queryProjects() {
        /**
         * Gets the list of repos on the system the user has.
         */
        // TODO change this so it works better
        Log.d("Agora", "queryProject called");

        ArrayList<ProjectsData> project = new ArrayList<ProjectsData>();
        if (isExternalStorageReadable()) {

            File[] files = new File[0];
            try {
                String dir = this.getExternalFilesDir(Environment.DIRECTORY_DOCUMENTS).getAbsolutePath() +"/";
                Log.d("Agora","dir="+dir);
                files = new File(dir).listFiles();
            }catch(NullPointerException e) {
                Log.d("Agora",e.toString());
            }
            if (files != null) {
                for (File item : files) {
                    if (item.isDirectory()) {
                        ProjectsData rfolder = new ProjectsData(item.getName());
                        projectList.add(rfolder);
                    }
                }

            } else {
                projectList.add(new ProjectsData("There is currently no project here."));
            }

        }
        initProjectInflate();

    }


    private void initProjectInflate(){
        projectAdaptor = new ProjectListFunction(this,R.layout.activity_projectlist,projectList);
        setListAdapter(projectAdaptor);
    }



    public static class ProjectsData {
        public String name;
        public String loc;

        public ProjectsData(String name){
            this.name = name.replaceAll("_"," ");
            this.loc = name;
        }
    }

    @Override
    protected void onStart() {
        /**
         * When the activity starts the list of projects are generated.
         */
        Log.d("Agora","ProjectList started");
        super.onStart();
        projectList.clear();
        queryProjects();
    }

    @Override
    protected void onRestart(){
        Log.d("Agora","ProjectList Restart");
        super.onRestart();
        projectList.clear();
        queryProjects();
    }


    @Override
    protected void onStop(){
        /**
         * When the activity stops the project list is cleared
         */
        Log.d("Agora","ProjectList Stopped");
        super.onStop();
        projectList.clear();
    }

    private class ProjectListFunction extends ArrayAdapter<ProjectsData> {

        private ArrayList<ProjectsData>objects;

        private LayoutInflater projectview = (LayoutInflater)getSystemService(Context.LAYOUT_INFLATER_SERVICE);

        public ProjectListFunction(Context context, int resource, ArrayList<ProjectsData> projectList) {
            super(context,resource,projectList);
            this.objects = projectList;
        }

        @Override
        public View getView(int postion,View projectView, ViewGroup parent){

            if(projectView == null){
                projectView = projectview.inflate(R.layout.projectlist_fragment,null);

            }

            final ProjectsData data = objects.get(postion);

            if (data != null){
                TextView name = (TextView) projectView.findViewById(R.id.Projectname);
                name.setText(data.name);
            }

            projectView.setOnClickListener(new View.OnClickListener(){
                @Override
                public void onClick(View item){
                    /**
                     * When click it will send the user to the project note page.
                     * if there is one to go to.
                     */
                    if(data.loc.equals("null")){
                        Context context = getApplicationContext();
                        CharSequence Toasttext = "There are no projects, please create a new one.";
                        Toast toast = Toast.makeText(context,Toasttext,Toast.LENGTH_LONG);
                        toast.show();
                    }else{
                        // TODO
                        // Send the project info to the MainActivity to be displayed
                        Log.d("Agora","Project "+data.name+" Clicked at "+data.loc);
                        Intent intent = new Intent(ProjectList.this, MainActivity.class);

                        Bundle bundle = new Bundle();
                        bundle.putString("Project",data.loc);

                        intent.putExtras(bundle);
                        startActivity(intent);
                    }
                }
            });
            return projectView;
        }
    }
}
