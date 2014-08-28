package uk.org.samhipwell.agora;

import android.content.Context;
import android.os.Environment;
import android.util.Log;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.HashMap;
import java.util.Map;

public class fileSurport {
    /**
     * This class is to be a set of methods to work on the filesystem.
     */

    public Context context;
    private final static int GET = 5;
    private final static int SEND = 7;
    private final static int UPDATE = 9;

    public fileSurport(Context c){
        context = c;
    }

    public boolean isExternalStorageWritableReader(){
        /*
            Check if the file system is able to be written to.
         */
        String state = Environment.getExternalStorageState();
        if (Environment.MEDIA_MOUNTED.equals(state) || Environment.MEDIA_MOUNTED_READ_ONLY.equals(state)){
            return true;
        } else {
            return false;
        }
    }

    /**
     *  The readFile is has two options of input to either deal with url or finding the note itself.
     */

    public String readFile(String projectname, String filename)throws IOException{

        if(isExternalStorageWritableReader()){

            String uri = context.getExternalFilesDir(Environment.DIRECTORY_DOCUMENTS)+"/"+ projectname.replaceAll(" ", "_")+"/"+filename;
            File file = new File(uri);

            String output = readEntry(new FileInputStream(file));
            return output;

        }
        return null;
    }

    public String readFile(String uri) throws IOException {
        if(isExternalStorageWritableReader()) {

            File file = new File(uri);

            String output = readEntry(new FileInputStream(file));
            return output;
        }

        return null;
    }

    public String readEntry (InputStream input) throws IOException {
        BufferedReader reader = new BufferedReader(new InputStreamReader(input), 8);
        StringBuilder buidler = new StringBuilder();

        String line;

        while((line =  reader.readLine()) != null){
            buidler.append(line + "\n");
        }
        return buidler.toString();
    }


    public void writeFile(String projectname, String filename, String content) {
        /**
         * This writes a string on to a selected file.
         */

        String uri = context.getExternalFilesDir(Environment.DIRECTORY_DOCUMENTS)+"/"+ projectname.replaceAll(" ", "_")+"/"+filename;
        //Log.d("Agora file uri",uri);
        writeEntiry(uri,content);

    }

    public void writeFile(String url, String content){
        writeEntiry(url,content);
    }

    public void writeEntiry(String url, String content){
        if(isExternalStorageWritableReader()){
            try{
                File file = new File(url);

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

    public String create_repo(Context context, String name) {
        /**
         * The idea here is to create a new repository in the app data file on external storage.
         *
         */
        File file = null;
        if (isExternalStorageWritableReader()) {
            // if the external storage is attached. It creates the location
            file = new File(context.getExternalFilesDir(Environment.DIRECTORY_DOCUMENTS), name.replaceAll(" ", "_"));

            if (!file.mkdirs()) {
                // if the directory can't be made then this error will be logged
                Log.e("Agora", "Directory not created");
                return null;
            }

        }
        return String.valueOf(file.getAbsolutePath());
    }
}
