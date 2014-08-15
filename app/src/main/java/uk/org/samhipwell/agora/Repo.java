package uk.org.samhipwell.agora;

/**
 * Created by sam on 15/08/14.
 */
public class Repo {

    int id;
    String rname;
    String url;
    String hash;


    public Repo(int id, String rname, String url, String hash){
        this.id = id;
        this.rname = rname;
        this.url = url;
        this.hash = hash;
    }

    public Repo() {
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getRname() {
        return rname;
    }

    public void setRname(String rname) {
        this.rname = rname;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public String getHash() {
        return hash;
    }

    public void setHash(String hash) {
        this.hash = hash;
    }

}
