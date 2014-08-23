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
    public HashMap<Integer,List<String>> Comments = new HashMap<Integer, List<String>>();
    Database db;

    private ArrayList<CommentData> commentList = new ArrayList<CommentData>();
    private ArrayAdapter<CommentData> commentAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_comment);
        db = new Database(this);
        Bundle bundle = getIntent().getExtras();
        Comments =  (HashMap<Integer,List<String>>) bundle.getSerializable("HashMap");
        convertComments();
    }

    private void convertComments() {
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
        int id = item.getItemId();
        if (id == R.id.action_settings) {
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    private void initComments(){
        commentAdapter = new CommentInflate(this,R.layout.activity_comment,commentList);
        setListAdapter(commentAdapter);
    }

    public void btnCommentSave(View view) {
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



