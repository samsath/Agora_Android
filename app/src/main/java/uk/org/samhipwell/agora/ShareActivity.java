package uk.org.samhipwell.agora;


import android.app.ListActivity;
import android.content.ContentResolver;
import android.content.Context;
import android.database.Cursor;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.ArrayList;


public class ShareActivity extends ListActivity {
    /**
     * This is the activty which when the user wants to share a project or note they click on the
     * individuals they want to share to then it uses the shareSync to send the information off.
     */

    public String project;
    public String note;
    public String filepath;
    public ArrayList<String> emails = new ArrayList<String>();
    public ArrayList<Contact> contactList = new ArrayList<Contact>();
    private ArrayAdapter<Contact> contactAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_share);
        Bundle bundle = getIntent().getExtras();
        filepath = bundle.getString("path");

        readContacts();
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.share, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();
        if (id == R.id.shareTocontact) {
            // TODO when click send the list of people to the account
            send();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    private void send() {
        // This gets all the contacts emails and sends them to the server to and the invites
        shareSync sync = new shareSync(this,filepath,emails);
        sync.execute();

    }

    public void readContacts(){
        /*
            Reads the contact list saved on the phone and produces the contact details (name and email)
            which is then used to create a list for the user to click on.
        */
        //ContactsContract.CommonDataKinds.Phone.CONTENT_URI
        Uri uri = ContactsContract.CommonDataKinds.Phone.CONTENT_URI;
        ContentResolver contres = getContentResolver();
        Cursor cur = contres.query(ContactsContract.CommonDataKinds.Phone.CONTENT_URI,null,null,null,null);

        String[] projection = new String[] {ContactsContract.CommonDataKinds.Phone.CONTACT_ID,ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME,
            ContactsContract.CommonDataKinds.Phone.PHOTO_THUMBNAIL_URI,ContactsContract.CommonDataKinds.Email.DATA};
        Cursor cnames = contres.query(uri,projection,null,null,null);

        int idIDX = cnames.getColumnIndex(ContactsContract.CommonDataKinds.Phone.CONTACT_ID);
        int nameIDX = cnames.getColumnIndex(ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME);
        int photoIDX = cnames.getColumnIndex(ContactsContract.CommonDataKinds.Phone.PHOTO_THUMBNAIL_URI);

        if(cnames.moveToFirst()) {
            do {
                String photoURL;
                if (cnames.getString(photoIDX) != null) {
                    photoURL = cnames.getString(photoIDX);
                } else {
                    photoURL = "@drawable/contact.png";
                }
                contactList.add(new Contact(cnames.getString(idIDX), cnames.getString(nameIDX), photoURL));
            } while (cnames.moveToNext());
        }else{
            Log.d("AgoraContat","No Contacts");
        }
        cnames.close();

        while(cur.moveToNext()){
            String id = cur.getString(cur.getColumnIndex(ContactsContract.Contacts._ID));
            Cursor email = contres.query(ContactsContract.CommonDataKinds.Email.CONTENT_URI, null,
                    ContactsContract.CommonDataKinds.Email.CONTACT_ID + " = ?",new String[]{id}, null);

            int emailidIDX = email.getColumnIndex(ContactsContract.CommonDataKinds.Email.CONTACT_ID);
            int emailAdresIDX = email.getColumnIndex(ContactsContract.CommonDataKinds.Email.DATA);

            if(email.moveToFirst()) {
                do {
                    String emailID = email.getString(emailidIDX);
                    String emailAdd = email.getString(emailAdresIDX);
                    for (int i = 0; i > contactList.size(); i++) {
                        if (contactList.get(i).id.equals(emailID)) {
                            contactList.get(i).setEmail(emailAdd);
                        }
                    }
                } while (email.moveToNext());
            }else{
                Log.d("AgoraContat","No Email");
            }

            email.close();
        }

        initContactInflate();

    }

    private void initContactInflate() {
        Log.d("AgoraContact","Inflate");
        for(int i=0 ; i > contactList.size(); i++){
            Log.d("AgoreContacts", contactList.get(i).name);
        }
        contactAdapter = new ContactFunctio(this,R.layout.comment_inflate,contactList);
        setListAdapter(contactAdapter);
    }


    class Contact{
        public String id;
        public String name;
        public String email;
        public String thumb;

        public Contact(String id, String names,String photo){
            this.id = id;
            this.name = names;
            this.thumb = photo;
        }

        public void setEmail(String email) {
            this.email = email;
        }

    }

    private class ContactFunctio extends ArrayAdapter<Contact> {

        private LayoutInflater contactview = (LayoutInflater)getSystemService(Context.LAYOUT_INFLATER_SERVICE);

        public ContactFunctio(Context context, int resource, ArrayList<Contact> emails) {
            super(context, resource,emails);
        }

        @Override
        public View getView(int position, View contactView, ViewGroup parent){
            if(contactView == null){
                contactView = contactview.inflate(R.layout.comment_inflate,null);
            }

            final Contact contact = contactList.get(position);

            if(contact != null){
                TextView name = (TextView) contactView.findViewById(R.id.contactName);
                ImageView thumb = (ImageView) contactView.findViewById(R.id.contactThumb);
                name.setText(contact.name);
                thumb.setImageBitmap(BitmapFactory.decodeFile(contact.thumb));
            }

            contactView.setOnClickListener( new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    emails.add(contact.email);
                }
            });

            return contactView;
        }
    }

    @Override
    protected void onStart() {
		/*
		 * Every time  the activity is started it will call content provide function
		 * So that if another application changes the content this information will be up to date.
		 */
        super.onStart();
        readContacts();
    }

    @Override
    protected void onStop() {
		/*
		 * When the activity stops it clears the contact list so that it can be re-populate at activity start
		 * and does cause conflicts of data.
		 */
        super.onStop();
        contactList.clear();
    }
}
