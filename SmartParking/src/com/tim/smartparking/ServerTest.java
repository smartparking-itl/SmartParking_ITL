package com.tim.smartparking;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

import android.app.Activity;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Toast;

public class ServerTest extends Activity {
	
	String web_site = "http://www.testing44.rurs.net/"; // then we will change it
	
	private static int count_of_cars = 2;
	private static ImageView[] ivCar = new ImageView[count_of_cars];
	public static String LOG_TAG = "my_log";
	public static String s = "";


	  private static void setColorCars(String s) {
		  int i;
		  for(i = 0; i < Math.min(count_of_cars, s.length()); i++) {
			  if(s.charAt(i) == '0') {
				  ivCar[i].setBackgroundResource(R.drawable.redcar);
			  } else if(s.charAt(i) == '1') {
				  ivCar[i].setBackgroundResource(R.drawable.greencar);
			  }
		  }
	  }
 
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.kolco_map1);
        //setContentView(R.layout.kolco_map);
        
        
       /* OnClickListener ocl = new OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				new ParseTask().execute();
			}
        	
        };*/
		ivCar[0] = (ImageView) findViewById(R.id.imageView2);
		ivCar[1] = (ImageView) findViewById(R.id.ImageView3);
		//Toast.makeText(getApplicationContext(), "Created", Toast.LENGTH_SHORT).show();
		//ivCar[0].setOnClickListener(ocl);
		//ivCar[1].setOnClickListener(ocl);

        get_place();
        
        Button refresh = (Button) findViewById(R.id.refresh);
        refresh.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				get_place();
				// TODO Auto-generated method stub
				
			}
		});
    }

	private void get_place() {
		 GettingInfo info = new GettingInfo(getApplicationContext());
         String ginfo = "";
         
         try {
			ginfo = info.execute(web_site).get(5000, TimeUnit.MILLISECONDS);
			if(ginfo.equals("Error"))
			{
				Toast.makeText(getApplicationContext(), "Error getting info", Toast.LENGTH_SHORT).show();
				return;
			}
			JSONWorking jw = new JSONWorking(getApplicationContext());
	         
	         String scolor = "";
	         
	         try {
					ArrayList<HashMap<String, String>>  res = jw.execute(ginfo).get();
					
				//	Log.e("json","done");
					
					if(res==null)
					{
						Toast.makeText(getApplicationContext(), "Error Array equals to null", Toast.LENGTH_SHORT).show();
						return;
					}
		
					for(int i = 0; i<res.size(); i++)
					{
						HashMap<String, String> item = res.get(i);
						String used = item.get("Used");
						char l = '0';
						if(used.equals("true"))
							l = '1';
						scolor = scolor + l;
					}
					Log.e("scolor", scolor);
		            setColorCars(scolor);
				} catch (InterruptedException e) {
					

					Toast.makeText(getApplicationContext(), "Error in using JSON", Toast.LENGTH_SHORT).show();
					// TODO Auto-generated catch block
					//e.printStackTrace();
				} catch (ExecutionException e) {
					Toast.makeText(getApplicationContext(), "Error in using JSON", Toast.LENGTH_SHORT).show();
					// TODO Auto-generated catch block
					//e.printStackTrace();
				}
			//test
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			Toast.makeText(getApplicationContext(), "Error", Toast.LENGTH_SHORT).show();
		} catch (ExecutionException e) {
			// TODO Auto-generated catch block
			Toast.makeText(getApplicationContext(), "Error", Toast.LENGTH_SHORT).show();
		} catch (TimeoutException e) {
			// TODO Auto-generated catch block
			Toast.makeText(getApplicationContext(), "Error Time out", Toast.LENGTH_SHORT).show();
		}
         

	}
}
