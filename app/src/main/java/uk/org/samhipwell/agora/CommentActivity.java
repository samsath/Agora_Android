package uk.org.samhipwell.agora;

import android.app.ListActivity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.TextView;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;


public class CommentActivity extends ListActivity {

    /**
     * This activity is to add comments and view comments to the specific note.
     */
    // set up for th list inflation to show the different comments.
    public HashMap<Integer,List<String>> Comments = new HashMap<Integer, List<String>>();
    private ArrayList<CommentData> commentList = new ArrayList<CommentData>();
    private ArrayAdapter<CommentData> commentAdapter;
    public String url;

    // database access is needed to get hold of the user logged.
    Database db;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        // The comment list is sent from the notes to here in a hashmap and then send back.
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_comment);
        db = new Database(this);
        Bundle bundle = getIntent().getExtras();
        url = bundle.getString("path");
        Comments =  (HashMap<Integer,List<String>>) bundle.getSerializable("HashMap");
        convertComments();
    }

    private void convertComments() {
        // turns the hashmap of comments into a easier form the inflater to read.
        for(int i =0; i<Comments.size(); i++){
            CommentData c = new CommentData(Comments.get(i).get(0),Comments.get(i).get(1),Comments.get(i).get(2));
            commentList.add(c);
        }
        initComments();
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.comment, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        switch (item.getItemId())
        {
            case R.id.action_settings:
                return  true;
            case android.R.id.home:
                this.finish();
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onBackPressed(){
        /*
            as the way the note activty is started it doesn't keep it's note location so each time
            it is started it need to be sent the note location.
        */
        Bundle bundle = new Bundle();
        bundle.putString("path",url);
        Intent intent = new Intent();
        intent.putExtras(bundle);
        setResult(RESULT_OK,intent);
        super.onBackPressed();
    }

    private void initComments(){
        commentAdapter = new CommentInflate(this,R.layout.activity_comment,commentList);
        setListAdapter(commentAdapter);
    }

    public void btnCommentSave(View view) {
        /*
            Takes the input and add the new comment to the file then sends it back to the note activity.
         */
        EditText content = (EditText)findViewById(R.id.ed_comment);
        long time = System.currentTimeMillis();
        String user = db.getUsername();

        List<String> newComment = new ArrayList<String>();
        newComment.add(user);
        newComment.add(String.valueOf(time));
        newComment.add(String.valueOf(content.getText()));
        int Size = Comments.size();
        Comments.put(Size,newComment);

        Intent intent = new Intent();
        Bundle bundle = new Bundle();
        bundle.putString("path",url);
        bundle.putSerializable("HashMap",Comments);
        intent.putExtras(bundle);

        setResult(NoteActivity.RESULT_OK,intent);
        finish();

    }


    private class CommentInflate extends ArrayAdapter<CommentData>{
        private ArrayList<CommentData> comments;
        private LayoutInflater commentView = (LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE);

        public CommentInflate(Context context, int position, ArrayList<CommentData> events){
            super(context,position,events);
            this.comments = events;

        }

        @Override
        public View getView(int position, View inflatedView, ViewGroup parent){
            if(inflatedView == null){
                inflatedView = commentView.inflate(R.layout.comment_inflate,null);
            }

            final CommentData comment = comments.get(position);

            if(comment != null){
                TextView uname = (TextView) inflatedView.findViewById(R.id.tx_username);
                TextView body = (TextView) inflatedView.findViewById(R.id.tx_comment_body);
                uname.setText(comment.username);
                body.setText(comment.body);
            }
            return inflatedView;
        }


    }
    public class CommentData {
        public String username;
        public String Datetime;
        public String body;

        public CommentData(String user, String Datetime,String body){
            this.body = body;
            this.Datetime = Datetime;
            this.username = user;
        }

    }
}



