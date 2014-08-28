package uk.org.samhipwell.agora;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import java.io.IOException;


public class CreateRepo extends Activity {

    fileSurport fs;
    EditText repoName;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_repo);

        repoName = (EditText) findViewById(R.id.et_reponame);
        fs = new fileSurport(getApplicationContext());
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.create_repo, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();
        if (id == R.id.action_settings) {
            Intent intent = new Intent(CreateRepo.this,SettingActivity.class);
            startActivity(intent);
        }
        return super.onOptionsItemSelected(item);
    }

    public void createProject_onClick(View view) throws IOException {
        /**
         * When clicked it will start the service and create the repo of the name.
         * If it works then takes the user to the project screen, else display message.
         */
        String repo = repoName.getText().toString();
        String item = fs.create_repo(this,repo);

        if(!item.isEmpty()){
            Intent intent = new Intent(CreateRepo.this,ProjectList.class);
            startActivity(intent);
        }else{
            // directory and repo wasn't created.
            Context context = getApplicationContext();
            CharSequence Toasttext = "Couldn't create the project";
            Toast toast = Toast.makeText(context,Toasttext,Toast.LENGTH_LONG);
            toast.show();

        }
    }
}
