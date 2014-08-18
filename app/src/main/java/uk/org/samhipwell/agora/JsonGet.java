package uk.org.samhipwell.agora;


import android.util.Log;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;

public class JsonGet extends JSONObject {
    JSONObject jsonResult;

    public String convert(InputStream content) {
        BufferedReader reader = null;
        try {
            reader = new BufferedReader(new InputStreamReader(content, "UTF-8"), 8);
            StringBuilder buidler = new StringBuilder();

            String line;

            while ((line = reader.readLine()) != null) {
                buidler.append(line + "\n");
            }

            String result = buidler.toString();

            //Log.d("Agora json", result);

            return result;
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }
}


