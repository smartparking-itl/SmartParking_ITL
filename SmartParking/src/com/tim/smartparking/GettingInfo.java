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
import java.net.ConnectException;
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
            HttpURLConnection httpURLConnection = null;
            try {
                httpURLConnection = (HttpURLConnection) url.openConnection();
                httpURLConnection.setDoInput(true);
                InputStream in = null;
                try {
                    in = new BufferedInputStream(httpURLConnection.getInputStream());
                    BufferedReader reader = new BufferedReader(new InputStreamReader(in));
                    try {
                        String inf = "";
                        while((inf = reader.readLine())!=null)
                            info += inf;
                        
                        httpURLConnection.disconnect();
                        return info;
                        
                    } catch (IOException e) {
                    	Log.e("m", "I m here3");
                   //	if(context!=null)
                    //		Toast.makeText(context, "Error in Stream URL", Toast.LENGTH_SHORT).show();
                    }
                        
                    } catch (IOException e) {
                    	Log.e("m", "I m here2");
                    	//if(context!=null)
                    		//Toast.makeText(context, "Error in reading Line", Toast.LENGTH_SHORT).show();
                    }
                   
                
            } catch (IOException e) {
            	Log.e("m", "I m here");
            	//if(context!=null)
            		//Toast.makeText(context, "Error in connection to URL", Toast.LENGTH_SHORT).show();
            }
         /*   catch(ConnectException e){
            	if(context!=null)
            		Toast.makeText(context, "Error in connecting", Toast.LENGTH_SHORT).show();
            }*/

        } catch (MalformedURLException e) {
        //	if(context!=null)
        		//Toast.makeText(context, "Error in getting URL", Toast.LENGTH_SHORT).show();
        }
        
        return "Error";
        
        
    }
}
