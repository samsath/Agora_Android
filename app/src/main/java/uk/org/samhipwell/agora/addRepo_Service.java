package uk.org.samhipwell.agora;

import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.os.Binder;
import android.os.Environment;
import android.os.IBinder;
import android.util.Log;

import org.eclipse.jgit.api.Git;
import org.eclipse.jgit.api.errors.GitAPIException;
import org.eclipse.jgit.lib.Repository;
import org.eclipse.jgit.storage.file.FileRepositoryBuilder;

import java.io.File;
import java.io.IOException;


public class addRepo_Service extends Service {

    private final IBinder binder = new addRepoBinder();

    public addRepo_Service() {
    }

    @Override
    public IBinder onBind(Intent intent) {
        // TODO: Return the communication channel to the service.

        return binder;
    }

    public boolean isExternalStorageWritable(){
        String state = Environment.getExternalStorageState();
        if (Environment.MEDIA_MOUNTED.equals(state)){
            return true;
        } else {
            return false;
        }
    }

    protected class addRepoBinder extends Binder {

        public Boolean create_repo(Context context, String name) throws GitAPIException, IOException {
            /**
             * The idea here is to create a new repository in the app data file on external storage.
             * Once created it will be populated with a .git repo
             */
            if (isExternalStorageWritable()){
                // if the external storage is attached. It creates the location
                File file = new File(context.getExternalFilesDir(Environment.DIRECTORY_DOCUMENTS),name.replaceAll(" ","_"));

                if(!file.mkdirs()){
                    // if the directory can't be made then this error will be logged
                    Log.d("GitTest","Directory not created");
                    return false;
                }

                // This part of the function then creates the git repo in the folder.
                Git.init().setDirectory(file).call();

                Repository repository = FileRepositoryBuilder.create(new File(file.getAbsolutePath(),".git"));

                Log.d("GitTest","Repository Created");

                repository.close();
            }
            return true;
        }

    }
}