package uk.org.samhipwell.agora;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.Typeface;
import android.text.Layout;
import android.text.StaticLayout;
import android.text.TextPaint;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;


public class NoteView extends View {
    /**
     * This here is a custom view to be inserted into the grid layout so that the user can interact with it.
     * This is guided by the google development website.
     */

    // Text colour
    private int textColour = Color.WHITE;
    // backgound colour
    private int bgColour = Color.BLACK;
    // NoteText
    private String noteText;
    // comment number
    private int commentNum;
    // show comments
    private boolean showComments;
    //paint for the drawing of the custome view
    private Paint noteBox;

    private int viewWidth = 480;
    private int viewHeight = 410;
    private String Fileloc;


    public NoteView(Context context){
        super(context);
        noteBox = new Paint();

    }

    public NoteView(Context context, String body,Integer txColour,Integer bgColour,Integer CommentNum){
        super(context);
        this.noteText = body;
        this.textColour = txColour;
        this.bgColour = bgColour;
        this.commentNum = CommentNum;
        if (CommentNum > 0){
            showComments = true;
        }

        noteBox = new Paint();

    }

    public NoteView(Context context, AttributeSet attrs) {
        super(context,attrs);

        // paint the object of the box
        noteBox = new Paint();

        TypedArray a = context.getTheme().obtainStyledAttributes(attrs,R.styleable.NoteView,0,0);

        // get the attributes now
        try{
            noteText = a.getString(R.styleable.NoteView_NoteText);
            commentNum = a.getInteger(R.styleable.NoteView_CommentNum,0);
            textColour = a.getInteger(R.styleable.NoteView_TextColour,0);
            bgColour = a.getInteger(R.styleable.NoteView_BackgroundColour,0);
            showComments = a.getBoolean(R.styleable.NoteView_ShowComments,false);

        }finally {
            a.recycle();
        }
    }

    @Override
    protected void onDraw(Canvas canvas){
        // draw the actuale note for the view

        String usedText;

        // Text working out
        int textLenght = noteText.length();
        if(textLenght>=132){
            usedText = noteText.substring(0,132);
            usedText += "  ...";
        }else{
            usedText = noteText;
        }
        TextPaint tcan = new TextPaint();
        tcan.setColor(textColour);
        tcan.setTextSize(60);
        tcan.setTextAlign(Paint.Align.LEFT);
        tcan.setAntiAlias(true);
        tcan.setTypeface(Typeface.DEFAULT);
        StaticLayout slText = new StaticLayout("" + usedText,tcan,viewWidth, Layout.Alignment.ALIGN_NORMAL,1,1,true);

        //Box working out
        // the box properties
        noteBox.setStyle(Paint.Style.FILL);
        noteBox.setAntiAlias(true);

        //set the background colour
        noteBox.setColor(Color.parseColor("#008080"));
        // draw the box
        Rect ra = new Rect(0, 0, viewWidth, viewHeight);
        canvas.drawRect(ra,noteBox);
        //canvas.translate(ra.centerX()-(viewWidth/2)-20, ra.centerY()-(viewHeight/2)-20);
        Rect rb = new Rect(2, 2, viewWidth-10, viewHeight-10);
        noteBox.setColor(bgColour);
        //Log.e("NoteRext", r.centerX()+","+ r.centerY());
        canvas.drawRect(rb,noteBox);
        canvas.translate(rb.centerX()-(viewWidth/2), rb.centerY()-(viewHeight/2));
        if(commentNum>0){
            Log.e("Agora Note","Has "+commentNum+" Comments");

            noteBox.setColor(Color.WHITE);
            noteBox.setTextSize(70);

            canvas.drawText(""+commentNum+" Comments",15,viewHeight-20,noteBox);
        }

        slText.draw(canvas);
    }


    @Override
    public boolean onTouchEvent(MotionEvent event){
        //Log.e("Note Click", noteText);
        return super.onTouchEvent(event);

    }

    public int getTextColour() {
        return textColour;
    }

    public void setTextColour(int textColour) {
        this.textColour = textColour;
        invalidate();
        requestLayout();
    }

    public int getBgColour() {
        return bgColour;
    }

    public void setBgColour(int bgColour) {
        this.bgColour = bgColour;
        invalidate();
        requestLayout();
    }

    public String getNoteText() {
        return noteText;
    }

    public void setNoteText(String noteText) {
        this.noteText = noteText;
    }

    public int getCommentNum() {
        return commentNum;
    }

    public void setCommentNum(int commentNum) {
        this.commentNum = commentNum;
    }

    public boolean isShowComments() {
        return showComments;
    }

    public void setShowComments(boolean showComments) {
        this.showComments = showComments;
    }

    public int getViewWidth() {
        return viewWidth;
    }

    public void setViewWidth(int viewWidth) {
        this.viewWidth = ((viewWidth-100)/2)-10;
    }

    public int getViewHeight() {
        return viewHeight;
    }

    public void setViewHeight(int viewHeight) {
        this.viewHeight = viewHeight;
    }

    public String getFileloc() {return Fileloc; }

    public void setFileloc(String fileloc) { Fileloc = fileloc; }
}
