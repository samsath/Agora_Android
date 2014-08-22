package uk.org.samhipwell.agora;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;


public class ColourPicker extends Activity {
    public String colour;
    private int type;
    private String code;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.colour_picker);
        Bundle bundle = getIntent().getExtras();
        code = bundle.getString("code");
        type = bundle.getInt("type");

    }


    public void closeClick(View view) {
        finish();
    }

    public void imageClick(View view) {
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
