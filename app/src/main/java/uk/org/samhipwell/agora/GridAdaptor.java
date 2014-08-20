package uk.org.samhipwell.agora;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.GridView;
import android.widget.TextView;

import java.io.File;
import java.util.List;

/**
 * Created by sam on 20/08/14.
 * http://developer.android.com/guide/topics/ui/layout/gridview.html
 */
public class GridAdaptor extends BaseAdapter {

    private Context gridContext;
    private File[] notes;

    public GridAdaptor(Context context,File[] note) {
        gridContext = context;
        notes = note;
    }

    @Override
    public int getCount() {
        return notes.length;
    }

    @Override
    public Object getItem(int position) {
        return notes[position];
    }

    @Override
    public long getItemId(int position) {
        return 0;
    }

    private LayoutInflater noteview = (LayoutInflater)gridContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);

    @Override
    public View getView(int position, View view, ViewGroup parent) {

        if(view == null){
            view = noteview.inflate(R.layout.grid_single,null);
            view.setLayoutParams(new GridView.LayoutParams(85,85));
            view.setPadding(5,5,5,5);

        }

        final File note = notes[position];

        if(note != null){
            // TODO at the moment just be filepath should be the content
            TextView main = (TextView) view.findViewById(R.id.grid_body);
            TextView comment = (TextView) view.findViewById(R.id.grid_comments);

            main.setText(note.getPath());
            comment.setText(note.getName());

        }


        return view;
    }
}
