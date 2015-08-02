package com.tim.smartparking;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.util.Log;

public class GetDistance {

public String GetRoutDistane(double startLat, double startLong, double endLat, double endLong)
{
  String Distance = "error";
  String Status = "error";
  try {
      Log.e("Distance Link : ", "http://maps.googleapis.com/maps/api/directions/json?origin="+ startLat +","+ startLong +"&destination="+ endLat +","+ endLong +"&sensor=false");
        JSONObject jsonObj = parser_Json.getJSONfromURL("http://maps.googleapis.com/maps/api/directions/json?origin="+ startLat +","+ startLong +"&destination="+ endLat +","+ endLong +"&sensor=false"); 
        Status = jsonObj.getString("status");
        if(Status.equalsIgnoreCase("OK"))
        {
        JSONArray routes = jsonObj.getJSONArray("routes"); 
         JSONObject zero = routes.getJSONObject(0);
         JSONArray legs = zero.getJSONArray("legs");
         JSONObject zero2 = legs.getJSONObject(0);
         JSONObject dist = zero2.getJSONObject("distance");
         Distance = dist.getString("text");
        }
        else
        {
            Distance = "Too Far";
        }
    } catch (JSONException e) {
        // TODO Auto-generated catch block
        e.printStackTrace();
    }
return Distance;


} 
}
