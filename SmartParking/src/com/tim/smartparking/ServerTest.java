package com.tim.smartparking;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

public class ServerTest extends Activity {
	
	String web_site = "http://www.testing44.rurs.net/"; // then we will change it
	
	private static int count_of_cars = 2;
	private static ImageView[] ivCar = new ImageView[count_of_cars];
	public static String LOG_TAG = "my_log";
	public static String s = "";
	private int sch = 0;
	private double TimeIn = 0;
	


	  private static void setColorCars(String s) {
		  int i;
		  for(i = 0; i < Math.min(count_of_cars, s.length()); i++) {
			  if(s.charAt(i) == '1') {
				  ivCar[i].setBackgroundResource(R.drawable.redcar);
			  } else{
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
		
		ivCar[0].setOnClickListener(new OnClickListener() {
			
			private int i = 0;
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				
				if(sch == 0)
				{
					sch = 1;
					TimeIn = System.currentTimeMillis();
				}
				
				double TimeOut = System.currentTimeMillis();
				
				if((TimeOut-TimeIn)*0.001<=15) sch++;
				else sch = 0;
				
				if(sch==8)
				{
					AlertDialog.Builder dialog = new AlertDialog.Builder(ServerTest.this);
					final EditText txt = new EditText(ServerTest.this);
					txt.setVisibility(View.VISIBLE);
					dialog.setView(txt);
					
					dialog.setCancelable(true);
					dialog.setPositiveButton("Set", new DialogInterface.OnClickListener(){
						
						@Override
						public void onClick(DialogInterface dialog, int which) {
							// TODO Auto-generated method stub
							
				        	String text = String.valueOf(txt.getText());
				        	int nom = 0;
				        	int val = 0;
				        	for(int i = 0; i<text.length(); i++)
				        	{
				        		if(text.charAt(i)!='=')
				        		{
				        			nom = nom * 10 + (int)(text.charAt(i)-'0');
				        		}
				        		else break;
				        	}
				        	
				        	if(text.charAt(text.length()-1)=='1')
				        	{
				        		val = 1;
				        	}
				        	
				            GettingInfo gf = new GettingInfo(ServerTest.this);
				            try {
								try {
									String inf = gf.execute(web_site + "?nom=" + nom + "&val=" +  val).get(3000, TimeUnit.MILLISECONDS);
								} catch (TimeoutException e) {
									// TODO Auto-generated catch block
									Toast.makeText(ServerTest.this, "Error in time out", Toast.LENGTH_SHORT).show();
								}
							} catch (InterruptedException e) {
								Toast.makeText(ServerTest.this, "Error connection", Toast.LENGTH_SHORT).show();
								
							} catch (ExecutionException e) {
								// TODO Auto-generated catch block
								Toast.makeText(ServerTest.this, "Error in connection", Toast.LENGTH_SHORT).show();
							}
				            
				            
				        dialog.dismiss();
				        get_place();
							
						}
					});
					
					dialog.setNegativeButton("Close", new DialogInterface.OnClickListener(){
						@Override
						public void onClick(DialogInterface dialog, int which) {
							dialog.dismiss();
						}
					});
					
					dialog.show();
					sch = 0;
				}
				
			}
		});;
		
    

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
        // Log.e("here", "111w");
			ginfo = info.execute(web_site).get(5000, TimeUnit.MILLISECONDS);
			//Log.e("here", "222w");
			if(ginfo.equals("Error"))
			{
				Toast.makeText(getApplicationContext(), "Error getting info", Toast.LENGTH_SHORT).show();
				return;
			}
			
			//Toast.makeText(getApplicationContext(), ginfo, Toast.LENGTH_LONG).show();
			//ginfo. = 'g';
		//	ginfo = ginfo.substring(0, 1) +'o' + ginfo.substring(3);
			
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
						
						scolor = scolor + used;
					}
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
			Toast.makeText(getApplicationContext(), "Error 1", Toast.LENGTH_SHORT).show();
		} catch (ExecutionException e) {
			// TODO Auto-generated catch block
			Toast.makeText(getApplicationContext(), "Error 2", Toast.LENGTH_SHORT).show();
		} catch (TimeoutException e) {
			// TODO Auto-generated catch block
			Toast.makeText(getApplicationContext(), "Error Time out", Toast.LENGTH_SHORT).show();
		}
         

	}
}
