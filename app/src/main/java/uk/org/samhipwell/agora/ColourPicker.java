package uk.org.samhipwell.agora;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;


public class ColourPicker extends Activity {
    /**
     * This is a colour picker activity made to work as a dialogue so
     * on the notes it can change there colour. It the same for background
     * and text.
     */

    public String colour;
    private int type;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        // Bundle from note tell what the colour is for
        super.onCreate(savedInstanceState);
        setContentView(R.layout.colour_picker);
        Bundle bundle = getIntent().getExtras();
        String code = bundle.getString("code");
        type = bundle.getInt("type");

    }


    public void closeClick(View view) {
        finish();
    }

    public void imageClick(View view) {
        /*
            All the icons go to the same command, and the use of tags to identify
            the different items clicked. Then it sends an intent back to the note
            saying what colour was clicked and what the colour is for.
         */
        String tag = (String) view.getTag();
        colour = tag;


        Intent intent = new Intent();
        Bundle bundle = new Bundle();
        bundle.putInt("type",type);
        bundle.putString("colour", colour);
        intent.putExtras(bundle);

        setResult(NoteActivity.RESULT_OK,intent);
        finish();

    }
}
