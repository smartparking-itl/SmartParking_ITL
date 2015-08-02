package com.tim.smartparking;

import android.os.AsyncTask;
import android.content.Context;
import android.util.Log;
import android.widget.Toast;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;

/**
 * Created by Malik on 30.07.2015.
 */
public class GettingInfo extends AsyncTask<String, Void, String> {

	static Context context = null;

    public GettingInfo(Context applicationContext) {
		// TODO Auto-generated constructor stub
    	super();
    	context = applicationContext;
	}

	@Override
    protected String doInBackground(String... params) {
		
		//Log.e("GettingINFO", "get");

        String info = "";

        URL url = null;
        try {
            url = new URL(params[0]);
            Log.e("url", "url");
        } catch (MalformedURLException e) {
        	if(context!=null)
        		Toast.makeText(context, "Error in getting URL", Toast.LENGTH_SHORT).show();
        }
        if(url!=null)
        {
            HttpURLConnection httpURLConnection = null;
            try {
            	Log.e("connection", "connection");
                httpURLConnection = (HttpURLConnection) url.openConnection();
                Log.e("connection", String.valueOf(httpURLConnection));
            } catch (IOException e) {
            	Log.e("here", "0001");
            	if(context!=null)
            		Toast.makeText(context, "Error in connection to URL", Toast.LENGTH_SHORT).show();
            }
            
            Log.e("connection", "connected");

            if(httpURLConnection!=null)
            {
                httpURLConnection.setDoInput(true);
                InputStream in = null;
                try {
                    in = new BufferedInputStream(httpURLConnection.getInputStream());
                    Log.e("read", "read");
                } catch (IOException e) {
                	if(context!=null)
                		Toast.makeText(context, "Error in Stream URL", Toast.LENGTH_SHORT).show();
                }
                if(in!=null)
                {

                    BufferedReader reader = new BufferedReader(new InputStreamReader(in));
                    try {
                        String inf = "";
                        while((inf = reader.readLine())!=null)
                            info += inf;
                    } catch (IOException e) {
                        e.printStackTrace();
                    }


                    httpURLConnection.disconnect();
                }
            }

        }

      // Log.e("setted", "setted");
        return info;
        
    }
}
