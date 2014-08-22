package uk.org.samhipwell.agora;

import android.content.Context;
import android.graphics.Color;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.GridView;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by sam on 22/08/14.
 */
public class NoteAdaptor extends BaseAdapter {

    private Context context;
    private String url;
    private List<noteInfo> noteFiles = new ArrayList<noteInfo>();
    /*
    private List<noteInfo> noteFiles = new ArrayList<noteInfo>(){{
        add(new noteInfo("///","Helllo there how are you Im greate thank you ust making sure it all works great and no problems", Color.parseColor("#ff0099"),Color.parseColor("#ffff66"),0));
        add(new noteInfo("//","HI you ok",Color.parseColor("#332fff"),Color.parseColor("#ffffff"),0));
        add(new noteInfo("///","This is the third attempt of the layout and see if the thing will pop up and see how it will go through it",Color.parseColor("#ffffff"),Color.parseColor("#000000"),0));
        add(new noteInfo("////","The fourth note to see how it will go down now.",Color.parseColor("#123987"),Color.parseColor("#765098"),0));
    }}
     */

    public NoteAdaptor(Context contexts,String urls) {
        context = contexts;
        url = urls;
        Log.e("Note",url);
        noteFiles.addAll(getFiles(url));
    }


    @Override
    public int getCount() {
        //TODO to fit with the file input
        return noteFiles.size();
    }

    @Override
    public Object getItem(int position) {
        return noteFiles.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View view, ViewGroup parent) {

        NoteView note;

        if (view == null) {
            note = new NoteView(context);
            note.setLayoutParams(new GridView.LayoutParams(480, 410));
            note.setPadding(16, 16,16, 16);
        } else {
            note = (NoteView) view;
        }
        // add file stuff
        note.setNoteText(noteFiles.get(position).body);
        note.setTextColour(noteFiles.get(position).txColor);
        note.setBgColour(noteFiles.get(position).bgColor);
        note.setCommentNum(noteFiles.get(position).commentNum);
        note.setId(position);

        note.setOnClickListener(new MyOnClickListener(position));

        return note;
    }

    public List<noteInfo> getFiles(String url){
        // Get the list of files here
        List<noteInfo> noteList = new ArrayList<noteInfo>();
        List<File> files;
        String ur;
        if(url.endsWith("/all")){
            // go through everything
            ur = url.substring(0,url.length()-3);
        }else{
            // just the project
            ur = url;
        }
        files = fileList(new File(ur));
        for (File f : files){

            StringBuilder text = new StringBuilder();
            try{
                BufferedReader br = new BufferedReader(new FileReader(new File(f.getAbsolutePath())));
                String Line;
                while((Line = br.readLine())!=null){
                    text.append(Line);
                }
            } catch (FileNotFoundException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }

            try{
                JSONObject mainObj = new JSONObject(String.valueOf(text));
                JSONObject noteObject = mainObj.getJSONObject("note");
                JSONObject commentObject = mainObj.getJSONObject("comment");
                String note = noteObject.getString("content");
                int txcol = Color.parseColor(noteObject.getString("tx"));
                int bgCol = Color.parseColor(noteObject.getString("bg"));
                int comNum = commentObject.length();

                noteList.add(new noteInfo(f.getAbsolutePath(),note,txcol,bgCol,comNum));

            } catch (JSONException e) {
                e.printStackTrace();
            }
        }


        return noteList;
    }

    private List<File> fileList(File path){
        ArrayList<File> file =  new ArrayList<File>();
        File[] files = path.listFiles();
        for(File f : files){
            if(f.isDirectory()){
                file.addAll(fileList(f));
            }else{
                if(f.getName().endsWith(".note")){
                    file.add(f);
                }
            }
        }
        return file;
    }


    public class noteInfo {
        public String fileLoc;
        public String body;
        public Integer txColor;
        public Integer bgColor;
        public Integer commentNum;

        public noteInfo(){}

        public noteInfo(String FileLoc,String Body,Integer txColor,Integer bgColor, Integer Comment){
            this.fileLoc = FileLoc;
            this.body = Body;
            this.txColor = txColor;
            this.bgColor = bgColor;
            this.commentNum = Comment;
        }
    }

    class MyOnClickListener implements View.OnClickListener {
        private final int position;

        public MyOnClickListener(int position) {
            this.position = position;
        }

        public void onClick(View v){
            Log.e("Note", "Note clicked = " + noteFiles.get(position).body);
            // TODO use the click somehow
        }
    }
}