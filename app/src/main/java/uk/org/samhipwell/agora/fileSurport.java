package uk.org.samhipwell.agora;

import android.content.Context;
import android.os.Environment;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by sam on 20/08/14.
 */
public class fileSurport {

    public Context context;
    private final static int GET = 5;
    private final static int SEND = 7;
    private final static int UPDATE = 9;

    public fileSurport(Context c){
        context = c;
    }

    public String readFile(String projectname, String filename){

        if(isExternalStorageWritableReader()){
            try{

                String uri = context.getExternalFilesDir(Environment.DIRECTORY_DOCUMENTS)+"/"+ projectname.replaceAll(" ", "_")+"/"+filename;
                File file = new File(uri);
                FileInputStream input = new FileInputStream(file);

                BufferedReader reader = new BufferedReader(new InputStreamReader(input));
                StringBuilder sb = new StringBuilder();
                String line;
                while((line = reader.readLine())!=null) {
                    sb.append(line);
                }
                reader.close();
                return sb.toString();

            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        return null;
    }

    public String readFile(String uri){
        if(isExternalStorageWritableReader()){
            try{
                File file = new File(uri);
                FileInputStream input = new FileInputStream(file);

                BufferedReader reader = new BufferedReader(new InputStreamReader(input));
                StringBuilder sb = new StringBuilder();
                String line;
                while((line = reader.readLine())!=null) {
                    sb.append(line);
                }
                reader.close();
                return sb.toString();

            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return null;
    }

    public void writeFile(String projectname, String filename, String content) {
        if(isExternalStorageWritableReader()){
            try {
                String uri = context.getExternalFilesDir(Environment.DIRECTORY_DOCUMENTS)+"/"+ projectname.replaceAll(" ", "_")+"/"+filename;
                //Log.d("Agora file uri",uri);
                File file = new File(uri);

                FileWriter output = new FileWriter(file);
                output.write(content);
                output.flush();
                output.close();


            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }


    public Map<String,Integer> compareList(Map<String, Long> serverList, String projectName) {
        /***
         * This it to go through the server list and file list and see what notes need to be recieved.
         */
        Map<String,Integer> results = new HashMap<String,Integer>();
        if(isExternalStorageWritableReader()){
            //Log.d("Agora Compare",serverList.toString());
            String ur = Environment.DIRECTORY_DOCUMENTS +"/"+projectName;
            // Log.d("Agroa compare dir",ur);
            File file = new File(ur);
            File[] listofFiles = file.listFiles();
            if(listofFiles != null) {
                for (File f : listofFiles) {
                    if (serverList.containsKey(f.toString())) {
                        if (serverList.get(f.toString()) != f.lastModified()) {
                            // If file exsists but different modified times.
                            results.put(f.toString(), UPDATE);
                        }
                    }else{
                        // No file on device so get from server
                        results.put(f.toString(),SEND);
                    }
                }
            }else{
                // Project Dir is empty
                for(String key : serverList.keySet()){
                    results.put(key,GET);
                }
            }

        }
        //Log.e("Agora List result",results.toString());
        return results;
    }

    public boolean isExternalStorageWritableReader(){
        String state = Environment.getExternalStorageState();
        if (Environment.MEDIA_MOUNTED.equals(state) || Environment.MEDIA_MOUNTED_READ_ONLY.equals(state)){
            return true;
        } else {
            return false;
        }
    }





    public String create_repo(Context context, String name) {
        /**
         * The idea here is to create a new repository in the app data file on external storage.
         * Once created it will be populated with a .git repo
         */
        File file = null;
        if (isExternalStorageWritableReader()) {
            // if the external storage is attached. It creates the location
            file = new File(context.getExternalFilesDir(Environment.DIRECTORY_DOCUMENTS), name.replaceAll(" ", "_"));

            if (!file.mkdirs()) {
                // if the directory can't be made then this error will be logged
                // Log.d("GitTest", "Directory not created");
                return null;
            }

            // This part of the function then creates the git repo in the folder.


            //Log.d("GitTest", "Repository Created");


        }
        return String.valueOf(file.getAbsolutePath());
    }
}
