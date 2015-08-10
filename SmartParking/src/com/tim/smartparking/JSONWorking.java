package com.tim.smartparking;

import android.content.Context;
import android.os.AsyncTask;
import android.util.Log;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;

/**
 * Created by Malik on 30.07.2015.
 */
public class JSONWorking extends AsyncTask <String, Void, ArrayList<HashMap<String, String>>> {

	
	Context context = null;
	
    public JSONWorking(Context applicationContext) {
		// TODO Auto-generated constructor stub
    	super();
    	context = applicationContext;
    	
    	
	}

	@Override
    protected  ArrayList<HashMap<String, String>> doInBackground(String... params) {
        //Log.e("json", "work");

        ArrayList<HashMap<String, String>> result = new  ArrayList<HashMap<String, String>>();
        JSONObject jbObject = null;
        try {
            jbObject = new JSONObject(params[0]);
            JSONArray information = null;
            try {
                information = jbObject.getJSONArray("result");
                String res = "";

                for(int i = 0; i<information.length(); i++)
                {
                    JSONObject jObject = null;
                    try {
                        jObject = information.getJSONObject(i);
                        HashMap<String, String> new_item = new HashMap<String, String>();
                        
                        try {

                            String number = jObject.getString("number");
                            String used = jObject.getString("used");
                            String useful = jObject.getString("useful");
                            

                            if(used.equals("0"))
                                used = "0";
                            else used = "1";

                            new_item.put("Number", number);
                            new_item.put("Used", used);
                            new_item.put("Useful", useful);
                            result.add(new_item);

                        } catch (JSONException e) {
                    		result.clear();
                        //	if(context!=null)
                      //  		Toast.makeText(context, "Second Error in getting Object " + i + " in Json", Toast.LENGTH_SHORT).show();
                        }
                    
                        
                    } catch (JSONException e) {
                    	result.clear();
                    //	if(context!=null)
                  //  		Toast.makeText(context, "First Error in getting Object " + i + " in Json", Toast.LENGTH_SHORT).show();
                    }
                }
                    
                
            } catch (JSONException e) {
            	result.clear();
            	//if(context!=null)
            		//Toast.makeText(context, "Error on Getting Array in JSON", Toast.LENGTH_SHORT).show();
            }
        } catch (JSONException e) {
        	result.clear();
        	//if(context!=null)
        		//Toast.makeText(context, "Error in JSON", Toast.LENGTH_SHORT).show();
        }
        
       return result;
    }
}
