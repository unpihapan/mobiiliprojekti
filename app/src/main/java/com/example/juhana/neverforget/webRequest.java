package com.example.juhana.neverforget;

import android.util.Log;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

class webRequest {

    // GET JSON http request
    static String doWebRequest(String requestURL){
        HttpURLConnection urlConnection = null;
        BufferedReader reader = null;
        String jsonStr;

        try {
            // URL connect
            URL url = new URL(requestURL);
            urlConnection = (HttpURLConnection) url.openConnection();
            urlConnection.setRequestMethod("GET");
            urlConnection.connect();
            InputStream inputStream = urlConnection.getInputStream();
            // return if input stream is null
            if (inputStream == null){
                return null;
            }

            // read JSON data into buffer
            StringBuilder buffer = new StringBuilder();
            reader = new BufferedReader(new InputStreamReader(inputStream));
            String line;
            while ((line = reader.readLine()) != null){
                buffer.append(line).append("\n");
            }
            if (buffer.length() == 0){
                // Stream was empty,  no point in parsing
                return null;
            }
            // read buffer to string
            jsonStr = buffer.toString();

        } catch (IOException e){
            return null;

        } finally{
            if (urlConnection != null){
                urlConnection.disconnect();
            }
            if (reader != null){
                try {
                    reader.close();
                } catch (final IOException e){
                    Log.d("IOExc", "Failed to close reader");
                }
            }
        }
        return jsonStr;
    }
}
