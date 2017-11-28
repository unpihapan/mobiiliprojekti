package com.example.juhana.neverforget;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

/**
 * Created by Elmeri on 28.11.2017.
 */

public class webRequest {

    // GET JSON http request
    public static String doWebRequest(String requestURL){
        HttpURLConnection urlConnection = null;
        BufferedReader reader = null;
        String jsonStr = null;

        try {
            // URL connect
            URL url = new URL( requestURL);
            urlConnection = (HttpURLConnection) url.openConnection();
            urlConnection.setRequestMethod("GET");
            urlConnection.connect();
            InputStream inputStream = urlConnection.getInputStream();
            // return if input stream is null
            if (inputStream == null){
                return jsonStr;
            }

            // read JSON data into buffer
            StringBuffer buffer = new StringBuffer();
            reader = new BufferedReader(new InputStreamReader(inputStream));
            String line;
            while ((line = reader.readLine()) != null){
                buffer.append(line + "\n");
            }
            if (buffer.length() == 0){
                // Stream was empty,  no point in parsing
                return jsonStr;
            }
            // read buffer to string
            jsonStr = buffer.toString();

        } catch (IOException e){
            return jsonStr;

        } finally{
            if (urlConnection != null){
                urlConnection.disconnect();
            }
            if (reader != null){
                try {
                    reader.close();
                } catch (final IOException e){}
            }
        }
        return jsonStr;
    }

}
