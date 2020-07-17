package com.example.cs496_week2;

import android.util.Log;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Map;

public class Remote{
    public static String sendDataGetMethod(String string){
        final StringBuilder result = new StringBuilder();
        try{
            URL url = new URL(string);
            HttpURLConnection conn = (HttpURLConnection)url.openConnection();
            conn.setRequestMethod("GET");

            if(conn.getResponseCode() == HttpURLConnection.HTTP_OK){
                InputStreamReader isr = new InputStreamReader(conn.getInputStream());
                BufferedReader br = new BufferedReader(isr);
                String tmp = "";

                while((tmp = br.readLine()) != null){
                    result.append(tmp);
                }

                br.close();
                isr.close();
            }
            else{
                Log.e("not okay", conn.getResponseCode()+"");
            }

            conn.disconnect();
        }
        catch (Exception e){
            Log.e("exception", e.toString());
        }

        return result.toString();
    }

    public static String sendDataPostMethod(String address, Map postdata){
        final StringBuilder result = new StringBuilder();

        try{
            URL url = new URL(address);
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setRequestMethod("POST");

            String tmp = "";
            for (Object key: postdata.keySet()){
                tmp += "&";
                tmp += key+"="+postdata.get(key);
            }

            tmp = tmp.substring(1);
            Log.d(tmp, tmp);
            conn.setDoOutput(true);
            OutputStream os = conn.getOutputStream();
            os.write(tmp.getBytes());
            os.flush();
            os.close();

            if(conn.getResponseCode() == HttpURLConnection.HTTP_OK) {
                InputStreamReader isr = new InputStreamReader(conn.getInputStream());
                BufferedReader br = new BufferedReader(isr);
                tmp = "";

                while ((tmp = br.readLine()) != null) {
                    result.append(tmp);
                }

                br.close();
                isr.close();
            }
            else{
                Log.e("not okay", conn.getResponseCode()+"");
            }
            conn.disconnect();
        }
        catch (Exception e){
            Log.e("exception", e.toString());
        }

        return result.toString();
    }
}