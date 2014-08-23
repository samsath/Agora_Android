package uk.org.samhipwell.agora;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import java.util.ArrayList;
import java.util.List;

public class Database extends SQLiteOpenHelper{

    public static final String LOG = "Agora";

    // Database info
    public static final int DATABASE_VERSION = 1;
    public static final String DATABASE_NAME = "AgoraManager";

    //table names
    public static final String TABLE_REPO = "repo";
    public static final String TABLE_USER = "user";
    public static final String TABLE_LOGIN = "login";
    public static final String TABLE_USER_REPO ="userrepo";

    //common columns
    public static final String KEY_ID ="id";
    public static final String KEY_USERID = "userid";

    //repo table
    public static final String KEY_RNAME = "rname";
    public static final String KEY_URL = "url";
    public static final String KEY_HASH = "hash";

    //user table
    public static final String KEY_USERNAME ="username";
    public static final String KEY_FNAME ="first_name";
    public static final String KEY_LNAME = "last_name";
    public static final String KEY_EMAIL = "email";
    public static final String KEY_PHOTO = "photo";

    //login table
    public static final String KEY_COOKIE = "cookie";

    //user_repo table
    public static final String KEY_REPOID = "repoid";

    //Table creators
    // repo table
    private static final String CREATE_TABLE_REPO ="CREATE TABLE " + TABLE_REPO +
                                                    "("+KEY_ID +" INTEGER PRIMARY KEY," +
                                                    KEY_RNAME + " VARCHAR(255)," +
                                                    KEY_URL + " VARCHAR(255)," +
                                                    KEY_HASH + " VARCHAR(255)" +
                                                    ")";

    // user table
    private static final String CREATE_TABLE_USER = "CREATE TABLE " +TABLE_USER +
                                                    "(" + KEY_ID +" INTEGER PRIMARY KEY ," +
                                                    KEY_USERNAME +" VARCHAR(255)," +
                                                    KEY_FNAME + " VARCHAR(255)," +
                                                    KEY_LNAME +" VARCHAR(255), " +
                                                    KEY_EMAIL +" VARCHAR(255)," +
                                                    KEY_PHOTO +" VARCHAR(255)" +
                                                    ")";

    // login table
    private static final String CREATE_TABLE_LOGIN = "CREATE TABLE " + TABLE_LOGIN  +
                                                    "(" + KEY_USERID +" INTEGER," +
                                                    KEY_COOKIE + " VARCHAR(255)" +
                                                    ")";

    // user repo table
    private static final String CREATE_TABLE_USERREPO = "CREATE TABLE "+ TABLE_USER_REPO +
                                                        "("+ KEY_ID +" INTEGER PRIMARY KEY," +
                                                        KEY_USERID + " INTEGER NOT NULL," +
                                                        KEY_REPOID + " INTEGER NOT NULL" +
                                                        ")";

    public Database(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
        Log.d("Agora","Database Started");
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        /**
         * SQL commands to create tables
         */
        Log.d("Agora", "Database Created ");
        db.execSQL(CREATE_TABLE_REPO);
        db.execSQL(CREATE_TABLE_USER);
        db.execSQL(CREATE_TABLE_LOGIN);
        db.execSQL(CREATE_TABLE_USERREPO);

    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldV, int newV) {
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_REPO);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_USER);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_LOGIN);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_USER_REPO);

        onCreate(db);
    }

    /**
     *
     *
     * Repo Database Queries
     *
     */

    public long createRepo(Repo repo, long user_id){
        /**
         * This creates a Repo on the database
         */
        SQLiteDatabase db = this.getWritableDatabase();
        long repo_id;

        Cursor c = db.rawQuery("SELECT * FROM "+TABLE_REPO + " WHERE "+ KEY_RNAME +" = '" + repo.getRname()+"';",null);
        if(c.moveToFirst()){
            // user already exsits on database
            repo_id = c.getInt(c.getColumnIndex(KEY_ID));
        }else {

            ContentValues values = new ContentValues();
            values.put(KEY_RNAME, repo.getRname());
            values.put(KEY_URL, repo.getUrl());
            values.put(KEY_HASH, repo.getHash());

            repo_id = db.insert(TABLE_REPO, null, values);


            createUserRepo(repo_id, user_id);

        }

        return repo_id;
    }

    public Repo getRepo(String reponame){
        /**
         * This retreives the repo info of that named repo
         */
        SQLiteDatabase db = this.getReadableDatabase();

        String selectQuery = "SELECT * FROM " + TABLE_REPO + " WHERE " +KEY_RNAME+ " = '" + reponame +"'";

        Log.e(LOG,selectQuery);

        Cursor c = db.rawQuery(selectQuery,null);

        if(c.getCount()>0){
            c.moveToFirst();
            Repo rep = new Repo();
            rep.setId(c.getInt(c.getColumnIndex(KEY_ID)));
            rep.setRname(c.getString(c.getColumnIndex(KEY_RNAME)));
            rep.setUrl(c.getString(c.getColumnIndex(KEY_URL)));
            rep.setHash(c.getString(c.getColumnIndex(KEY_HASH)));
            return rep;
        }else{
            return null;
        }

    }

    public List<Repo> getAllRepo(){
        /**
         * Get all the repos on the system.
         */
        SQLiteDatabase db = this.getReadableDatabase();
        List<Repo> reps = new ArrayList<Repo>();
        String selectQuery = "SELECT * FROM " + TABLE_REPO;

        Log.e(LOG,selectQuery);

        Cursor c = db.rawQuery(selectQuery,null);

        if(c!=null){
            if(c.moveToFirst()){
                do{
                    Repo rep = new Repo();
                    rep.setId(c.getInt(c.getColumnIndex(KEY_ID)));
                    rep.setRname(c.getString(c.getColumnIndex(KEY_RNAME)));
                    rep.setUrl(c.getString(c.getColumnIndex(KEY_URL)));
                    rep.setHash(c.getString(c.getColumnIndex(KEY_HASH)));
                    reps.add(rep);
                } while(c.moveToNext());
            }
        }
    return reps;
    }

    public int updateRepo(Repo rep){
        /**
         * Updates the repo field with the new information
         */
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();

        values.put(KEY_RNAME, rep.getRname());
        values.put(KEY_URL, rep.getUrl());
        values.put(KEY_HASH, rep.getHash());

        return db.update(TABLE_REPO, values, KEY_ID + " = ? ", new String[] { String.valueOf(rep.getId()) });
    }

    public void deleteRepo(String rname){
        /**
         * Delete a repo from db of that repo name
         */
        SQLiteDatabase db = this.getWritableDatabase();
        db.delete(TABLE_REPO, KEY_RNAME + " = ?", new String[] { String.valueOf(rname) });

    }

    /**
     *
     *
     * User Database Queries
     *
     */
    public long createUser(User user){
        /**
         * Create a user.
         */
        SQLiteDatabase db = this.getWritableDatabase();
        long user_id;

        Cursor c = db.rawQuery("SELECT * FROM "+TABLE_USER + " WHERE "+ KEY_USERNAME +" = '" + user.getUsername()+"';",null);
        if(c.moveToFirst()){
            // user already exsits on database
            user_id = c.getInt(c.getColumnIndex(KEY_ID));
        }else {

            ContentValues values = new ContentValues();
            values.put(KEY_USERNAME, user.getUsername());
            values.put(KEY_FNAME, user.getFirst_name());
            values.put(KEY_LNAME, user.getLast_name());
            values.put(KEY_EMAIL, user.getEmail());
            values.put(KEY_PHOTO, user.getPhoto());

            user_id = db.insert(TABLE_USER, null, values);
        }
        return user_id;
    }

    public User getUser(String username){
        /**
         * Get user information form a username
         */
        SQLiteDatabase db = this.getReadableDatabase();

        String selectQuery = "SELECT * FROM " + TABLE_USER + " WHERE " + KEY_USERNAME + " = '" +username+"';";

        Log.e(LOG, selectQuery);

        Cursor c = db.rawQuery(selectQuery,null);

        if(c!=null){
            c.moveToFirst();
        }

        User us = new User();
        us.setId(c.getInt(c.getColumnIndex(KEY_ID)));
        us.setUsername(c.getString(c.getColumnIndex(KEY_USERNAME)));
        us.setFirst_name(c.getString(c.getColumnIndex(KEY_FNAME)));
        us.setLast_name(c.getString(c.getColumnIndex(KEY_LNAME)));
        us.setEmail(c.getString(c.getColumnIndex(KEY_EMAIL)));
        us.setPhoto(c.getString(c.getColumnIndex(KEY_PHOTO)));

        return us;
    }

    public List<User> getAllUsers(){
        /**
         * Gets all the users on the system
         */
        List<User> users = new ArrayList<User>();
        SQLiteDatabase db = this.getReadableDatabase();

        String selectQuery = "SELECT * FROM " + TABLE_USER;

        Log.e(LOG,selectQuery);

        Cursor c = db.rawQuery(selectQuery,null);

        if(c!=null){
            if(c.moveToFirst()){
                do{
                    User us = new User();
                    us.setId(c.getInt(c.getColumnIndex(KEY_ID)));
                    us.setUsername(c.getString(c.getColumnIndex(KEY_USERNAME)));
                    us.setFirst_name(c.getString(c.getColumnIndex(KEY_FNAME)));
                    us.setLast_name(c.getString(c.getColumnIndex(KEY_LNAME)));
                    us.setEmail(c.getString(c.getColumnIndex(KEY_EMAIL)));
                    us.setPhoto(c.getString(c.getColumnIndex(KEY_PHOTO)));

                    users.add(us);
                }while(c.moveToNext());
            }
        }
        return users;
    }

    public int updateUser(User use){
        /**
         * Updates the user field with the new information
         */
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(KEY_USERNAME, use.getUsername());
        values.put(KEY_FNAME,use.getFirst_name());
        values.put(KEY_LNAME,use.getLast_name());
        values.put(KEY_EMAIL,use.getEmail());
        values.put(KEY_PHOTO,use.getPhoto());

        return db.update(TABLE_USER, values, KEY_ID + " = ? ", new String[] { String.valueOf(use.getId()) });
    }

    public void deleteUser(String username){
        /**
         * Delete a user from db of that user name
         */
        SQLiteDatabase db = this.getWritableDatabase();
        db.delete(TABLE_USER, KEY_USERNAME + " = ?", new String[] { String.valueOf(username) });

    }

    /***
     *
     *
     * User Repo section Query
     *
     */
    public long createUserRepo(long repo_id, long user ){
        /**
         *  Combines the user and repo in the userrope table
         */
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();

        values.put(KEY_REPOID,repo_id);
        values.put(KEY_USERID,user);

        long id = db.insert(TABLE_USER_REPO,null,values);
        return id;
    }

    public void removeRepoUser(long repo_id, long user_id) {
        /**
         * removes the repo from the user.
         */
        SQLiteDatabase db = this.getWritableDatabase();


        // updating row
        db.delete(TABLE_USER_REPO, KEY_USERID + " = '" + user_id + "' AND " + KEY_RNAME + " = '" +repo_id+"';",null );

    }
    /**
     *
     * Login database query
     *
     */

    public long createLogin(Login log ){
        /**
         * Create login details
         * DELETE FROM login ORDER BY userid ASC LIMIT(1);
         */
        SQLiteDatabase db = this.getWritableDatabase();

        Cursor data = db.rawQuery("SELECT * FROM "+TABLE_LOGIN,null);
        if(data.getCount() > 0){
            Log.d("Agora database = ", String.valueOf(data.getCount()));
            db.execSQL("DELETE FROM " + TABLE_LOGIN + ";");
            Log.i("Agora","login detail deleted");
        }
        ContentValues values = new ContentValues();

        values.put(KEY_USERID,log.getUserid());
        values.put(KEY_COOKIE,log.getCookie());
        Log.i("Agora","login detail created");
        long i = db.insert(TABLE_LOGIN,null,values);

        return i;
    }

    public int updateLogin(Login log){
        /**
         * Updates the user field with the new information
         */
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(KEY_USERID, log.getUserid());
        values.put(KEY_COOKIE,log.getCookie());

        return db.update(TABLE_USER, values, KEY_ID + " = 0 ",null);
    }

    public void removeLogin(Long i){
        /**
         * remove the login item
         */
        SQLiteDatabase db = this.getWritableDatabase();
        db.delete(TABLE_LOGIN,KEY_ID +" = 0",null);

    }

    public Boolean islogged(){
        /**
         * This checks if the is an account active on the program
         */
        SQLiteDatabase db = this.getReadableDatabase();
        String selectQuery = "SELECT '"+ KEY_USERID +"' FROM " + TABLE_LOGIN;

        Log.e(LOG,selectQuery);

        Cursor c = db.rawQuery(selectQuery,null);

        if(c.getCount() > 0 ){
            return true;
        }else {
            return false;
        }
    }


    public void closeDataBase() {
        SQLiteDatabase db = this.getReadableDatabase();
        if (db != null && db.isOpen())
            db.close();
    }

    public List<Login> getLogin(){
        /**
         * Get all the Login on the system.
         */
        SQLiteDatabase db = this.getReadableDatabase();
        List<Login> Login = new ArrayList<Login>();
        String selectQuery = "SELECT * FROM " + TABLE_LOGIN;

        Log.e(LOG,selectQuery);

        Cursor c = db.rawQuery(selectQuery,null);

        if(c!=null){
            if(c.moveToFirst()){
                do{
                    Login log = new Login();
                    log.setUserid(c.getInt(c.getColumnIndex(KEY_USERID)));
                    log.setCookie(c.getString(c.getColumnIndex(KEY_COOKIE)));
                    Login.add(log);
                } while(c.moveToNext());
            }
        }
        return Login;
    }

    public String getUsername(){

        String uname ="";
        SQLiteDatabase db = this.getReadableDatabase();
        String selectQuery = "SELECT * FROM " + TABLE_LOGIN;
        Cursor c = db.rawQuery(selectQuery,null);
        if(c.moveToFirst()) {
            int userid = c.getInt(c.getColumnIndex(KEY_USERID));
            String usernameQuery = "SELECT " + KEY_USERNAME + " FROM " + TABLE_USER + " WHERE " + KEY_ID + " = '" + userid + "';";
            Cursor name = db.rawQuery(usernameQuery, null);
            if (name.moveToFirst()) {
                uname = name.getString(0);
            }
        }else{
            uname = "Unkown";
        }

        return uname;
    }




}
