package uk.org.samhipwell.agora;

/**
 * Created by sam on 15/08/14.
 */
public class Login {
    long userid;
    String cookie;

    public Login(long userid,  String cookie) {
        this.userid = userid;
        this.cookie = cookie;
    }

    public Login() {
    }


    public long getUserid() {
        return userid;
    }

    public void setUserid(int userid) {
        this.userid = userid;
    }


    public String getCookie() {
        return cookie;
    }

    public void setCookie(String cookie) {
        this.cookie = cookie;
    }


}
