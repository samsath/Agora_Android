package uk.org.samhipwell.agora;

import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.os.Binder;
import android.os.Environment;
import android.os.IBinder;
import android.util.Log;

import java.io.File;
import java.util.ArrayList;

public class RepoList_Service extends Service {

    private final IBinder binder = new repoListBinder();
    private ArrayList<ProjectData> projectList = new ArrayList<ProjectData>();


    @Override
    public IBinder onBind(Intent intent) {
        Log.d("Agora", "RepoList server bind");

        projectList = new ArrayList<ProjectData>();
        return binder;
    }

    private class ProjectData {
        public String name;
        public String loc;

        public ProjectData(String name, String loc){
            this.name = name;
            this.loc = loc;
        }
    }

    public class repoListBinder extends Binder {

        public boolean isExternalStorageReadable() {
            String state = Environment.getExternalStorageState();
            if (Environment.MEDIA_MOUNTED.equals(state) ||
                    Environment.MEDIA_MOUNTED_READ_ONLY.equals(state)) {
                return true;
            }
            return false;
        }

        public ArrayList get_completeList(Context context){
            ArrayList<ProjectData> project = new ArrayList<ProjectData>();
            if (isExternalStorageReadable()) {
                File[] files = new File(context.getExternalFilesDir(Environment.DIRECTORY_DOCUMENTS).getAbsolutePath()).listFiles();
                for (File item : files){
                    if(item.isDirectory()){
                        ProjectData rfolder = new ProjectData(item.getName(),item.getAbsolutePath());
                        project.add(rfolder);
                    }
                }

            }
            return project;
        }

    }
}
