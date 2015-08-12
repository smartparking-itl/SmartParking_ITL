package com.tim.smartparking;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnLongClickListener;
import android.view.View.OnTouchListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.RelativeLayout.LayoutParams;
import android.widget.TextView;
import android.widget.Toast;

public class ServerTest extends Activity implements OnTouchListener {
	
	String web_site = "http://www.testing44.rurs.net/"; // then we will change it
	
	private static int count_of_cars = 10;
	public static String s = "";
	private int sch = 0;
	private int id = -1;
	private double TimeIn = 0;

	private boolean down = false;

	private long ins;
	


	  private void setColorCars(String s) {
		  
		  RelativeLayout rl = (RelativeLayout)findViewById(R.id.batya);
		  count_of_cars = rl.getChildCount();
		  
		  while(count_of_cars<s.length())
			  s += "0";
		  for(int i = 0, v = 0; i < Math.min(count_of_cars, s.length()) && v<count_of_cars; v++) {
			  
			  if(!(rl.getChildAt(v) instanceof TextView))
			  {
				//  Log.e("view", String.valueOf(rl.getChildAt(v)));
				  continue;
			  }
			  
			  if(rl.getChildAt(v) instanceof Button)
			  {
				//  Log.e("view", String.valueOf(rl.getChildAt(v)));
				  continue;
			  }

			  if(s.charAt(i) == '1') {
				  ((TextView)rl.getChildAt(v)).setBackgroundResource(R.drawable.redcar);
			  } else{
				  ((TextView)rl.getChildAt(v)).setBackgroundResource(R.drawable.greencar);
			  }
			  i++;
			  
			  
		  }
		  ((TextView)findViewById(R.id.hel)).setBackgroundResource(R.drawable.redcar);
		  
		  if(id!=-1)
			  ((TextView)findViewById(id)).setBackgroundResource(R.drawable.bluecar);
	  }
 
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.kolco_map1);
        
        ((ImageView)findViewById(R.id.spiv1)).setOnTouchListener(this);
        ((TextView)findViewById(R.id.hel)).setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				
				// TODO Auto-generated method stub
				//v.setRotationX(v.getRotationX()+10);
				//v.setRotationY(v.getRotationY()+10);
				v.setRotation(v.getRotation()+45);
		        SharedPreferences storage = ServerTest.this.getSharedPreferences("Configuration", MODE_MULTI_PROCESS);
		        SharedPreferences.Editor editor = storage.edit();
		        editor.putInt("rotation", (int) v.getRotation());
		        editor.commit();
			}
		});
        //setContentView(R.layout.kolco_map);
        
        
        
       /* OnClickListener ocl = new OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				new ParseTask().execute();
			}
        	
        };*/
		
		
		SharedPreferences storage = this.getSharedPreferences("Configuration", MODE_MULTI_PROCESS);
		final String name = storage.getString("name", "Me");
		id = storage.getInt("id", -1);
		Log.e("eclipse", "eclispe");
		
		 RelativeLayout rl = (RelativeLayout)findViewById(R.id.batya);
		 count_of_cars = rl.getChildCount();
		  
		  for(int i = 0; i <count_of_cars; i++) {
			  
			  if(!(rl.getChildAt(i) instanceof TextView))
			  {
				  continue;
			  }
			//  if(rl.getChildAt(i).getId()==R.id.hel)
				//  continue;
			  ((TextView)rl.getChildAt(i)).setOnLongClickListener(new OnLongClickListener() {
				
					@Override
					public boolean onLongClick(View v) {
						// TODO Auto-generated method stub
				        SharedPreferences storage = ServerTest.this.getSharedPreferences("Configuration", MODE_MULTI_PROCESS);
				        SharedPreferences.Editor editor = storage.edit();
				        int hd = storage.getInt("id", -1);
				        if(hd!=-1)
				        {
				        	((TextView)findViewById(hd)).setText("");
				        }
				        editor.putInt("id", v.getId());
				        id = v.getId();
				        editor.commit();
				        v.setBackgroundResource(R.drawable.bluecar);
				        ((TextView)v).setText(name);
				        Toast.makeText(ServerTest.this, "Saved", Toast.LENGTH_SHORT).show();
				        Notification.Builder nb = new Notification.Builder(getApplicationContext());
				        NotificationManager nm = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
				        nm.cancel(5);
				        nb.setOngoing(true)
				          .setSmallIcon(R.drawable.ic_launcher)
				          .setContentText("Вы припарковались на месте "+ v.getTag())
				          .setContentTitle("Smart Parking")
				          .setWhen(System.currentTimeMillis())
				          .setContentIntent(PendingIntent.getActivity(getApplicationContext(), 0, new Intent(getApplicationContext(), ServerTest.class), PendingIntent.FLAG_UPDATE_CURRENT));
				        nm.notify(5, nb.build());
				        refresh();
						return false;
					}
			  });
			}
			

		
		
		if(id!=-1)
		{
			((TextView)findViewById(id)).setBackgroundResource(R.drawable.bluecar);
			((TextView)findViewById(id)).setText(name);
			
	        SharedPreferences storage1 = ServerTest.this.getSharedPreferences("Configuration", MODE_MULTI_PROCESS);
			LayoutParams lp = new LayoutParams(((TextView)findViewById(R.id.hel)).getLayoutParams());
			lp.leftMargin = (int) storage1.getInt("x", 0);
			lp.topMargin = (int) storage1.getInt("y", 0);
			((TextView)findViewById(R.id.hel)).setLayoutParams(lp);
			((TextView)findViewById(R.id.hel)).setRotation(storage1.getInt("rotation", 0));
			
			if(id==R.id.hel)
				((TextView)findViewById(R.id.hel)).setVisibility(View.VISIBLE);
		}
		

		
		((TextView)(findViewById(R.id.imageView1))).setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				
				if(sch == 0)
				{
					sch = 1;
					TimeIn = System.currentTimeMillis();
				}
				
				double TimeOut = System.currentTimeMillis();
				
				if((TimeOut-TimeIn)<=1000) sch++;
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
									@SuppressWarnings("unused")
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

	protected void refresh() {
		get_place();
		// TODO Auto-generated method stub
		
	}

	private void get_place() {
		 GettingInfo info = new GettingInfo(getApplicationContext());
         String ginfo = "";
	     String scolor = "";
         
         try {
        // Log.e("here", "111w");
			ginfo = info.execute(web_site).get(7000, TimeUnit.MILLISECONDS);
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
         
         setColorCars(scolor);
	}

	@Override
	public boolean onTouch(View v, MotionEvent event) {
		// TODO Auto-generated method stub
		
		//Toast.makeText(ServerTest.this, (int) v.getId(), Toast.LENGTH_SHORT).show();
		//Log.e("sp", String.valueOf(R.id.spiv1));
		
		if(v.getId()==R.id.spiv1)
		{
			if(event.getAction()==MotionEvent.ACTION_DOWN)
			{
				if(!down)
				{
					down = true;
					ins = System.currentTimeMillis();
					//Toast.makeText(ServerTest.this, (int) System.currentTimeMillis(), Toast.LENGTH_SHORT).show();
				}
				else
				{
					long now = System.currentTimeMillis();
					if((now-ins)<=500)
					{
						float x = event.getX();
						float y = event.getY();
						//Toast.makeText(ServerTest.this, (int) System.currentTimeMillis() + "out", Toast.LENGTH_SHORT).show();
						
						LayoutParams lp = new LayoutParams(((TextView)findViewById(R.id.hel)).getLayoutParams());
						lp.leftMargin = (int) x;
						lp.topMargin = (int) y;
						((TextView)findViewById(R.id.hel)).setLayoutParams(lp);
						((TextView)findViewById(R.id.hel)).setVisibility(View.VISIBLE);
						
						
				        SharedPreferences storage = ServerTest.this.getSharedPreferences("Configuration", MODE_MULTI_PROCESS);
				        SharedPreferences.Editor editor = storage.edit();
				        editor.putInt("x", (int) x);
				        editor.putInt("y", (int) y);
				        editor.commit();
						
						
						down = false;
						ins = 0;
					}
					else
					{
						down = true;
						ins = now;
					}

				}
			}
		}
			
			
		return false;
	}
}
