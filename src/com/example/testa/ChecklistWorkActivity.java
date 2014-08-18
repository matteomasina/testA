package com.example.testa;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.ProtocolException;
import java.net.URL;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.StatusLine;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.ResponseHandler;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.BasicResponseHandler;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.params.BasicHttpParams;
import org.apache.http.params.HttpConnectionParams;
import org.apache.http.params.HttpParams;
import org.json.JSONArray;
import org.json.JSONObject;

import android.app.ActionBar;
import android.app.Activity;
import android.content.Intent;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.os.StrictMode;
import android.text.Html;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.Button;
import android.widget.ListView;
import android.widget.Toast;

public class ChecklistWorkActivity extends Activity {
	@SuppressWarnings("unused")
	private static final int TIMEOUT_MILLISEC = 10000;
	
	ListView list;
    CheckAdapter adapter;
    String id;
    String stabilimento;
    String checklist;
    ArrayList<HashMap<String, String>> mylist;
    
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		//requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.activity_workcheck_list);
		ColorDrawable colorDrawable = new ColorDrawable();
		colorDrawable.setColor(0xffFFFFFF);				
		Intent mIntent = getIntent();			
		id = mIntent.getExtras().getString("id");
		stabilimento = mIntent.getExtras().getString("stabilimento");
		checklist = mIntent.getExtras().getString("checklist");
		Log.i(getClass().getSimpleName(), "id task =" + id);
		String titleList = null;
		int iconList = 0;
		//obtain checklistID
		int checkListID = Integer.parseInt(checklist);
		switch (checkListID) {
	        case 1:  
	        	titleList = "Critical lifting";
	        	iconList=R.drawable.sollevamenti_critici;
	            break;
	        case 2:  
	        	titleList = "Hot work";
	        	iconList=R.drawable.lavori_a_caldo;
	            break;
	        case 3:  
	        	titleList = "Working at heights";
	        	iconList=R.drawable.lavori_in_quota;
	            break;
	        case 4:  
	        	titleList = "Minor lifting";
	        	iconList=R.drawable.sollevamenti_minori;
	            break;        
	    }
		
		
		getJSONdata(checkListID);
		ActionBar bar = getActionBar();
		bar.setBackgroundDrawable(colorDrawable);
		bar.setIcon(iconList);
		bar.setTitle(Html.fromHtml("<font color='#000000'>" + titleList + "</font>"));
		
		
		Button button = (Button) findViewById(R.id.btnAcceptWorkList);
		button.setText("ACCEPT CHECKLIST");
	    button.setOnClickListener(new View.OnClickListener(){
	        @Override
	        public void onClick(View v) {
	        	// INSERIMENTO NEL DEVICE LOG	
	        	URL url = null;
				try {
					url = new URL("http://93.113.136.157/api/insertLog");
				} catch (MalformedURLException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				Map<String,Object> params = new LinkedHashMap<>();
	        	params = new LinkedHashMap<>();
	            params.put("dev", "Acm-e (GT-I9105P)");
	            params.put("user", "Mauro Bianchi");
	            params.put("operazione", "SECURITY CHECKLIST");
	            params.put("dettagli", "Security checklist approved.Site : " +stabilimento);
	            Date d = new Date();
	            params.put("timestamp", d.toString());
	            sendHTTPdataCheckLIST(params,url);
	            url = null;
	        	try {
					url = new URL("http://93.113.136.157/api/updateTaskStatus");
				} catch (MalformedURLException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
	        	
	            params.put("id", id);
	            params.put("field", "stato");
	            params.put("stato", "SECURITY CHECKLIST");		            
	            sendHTTPdataCheckLIST(params,url);
	        	finish();
	        	
	        }
	    });
	}

	public static void sendHTTPdataCheckLIST(Map<String,Object> params,URL url) {
        StringBuilder postData = new StringBuilder();
        for (Map.Entry<String,Object> param : params.entrySet()) {
            if (postData.length() != 0) postData.append('&');
            try {
				postData.append(URLEncoder.encode(param.getKey(), "UTF-8"));
			} catch (UnsupportedEncodingException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
            postData.append('=');
            try {
				postData.append(URLEncoder.encode(String.valueOf(param.getValue()), "UTF-8"));
			} catch (UnsupportedEncodingException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
        }
        byte[] postDataBytes = null;
		try {
			postDataBytes = postData.toString().getBytes("UTF-8");
		} catch (UnsupportedEncodingException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

        HttpURLConnection conn = null;
        //conn.setConnectTimeout(5000);
		try {
			conn = (HttpURLConnection)url.openConnection();
			//conn.setConnectTimeout(5000);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
        try {
			conn.setRequestMethod("POST");
		} catch (ProtocolException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
        conn.setRequestProperty("Content-Type", "application/x-www-form-urlencoded");
        conn.setRequestProperty("Content-Length", String.valueOf(postDataBytes.length));
        conn.setDoOutput(true);
        try {
			conn.getOutputStream().write(postDataBytes);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

        Reader in = null;
		try {
			in = new BufferedReader(new InputStreamReader(conn.getInputStream(), "UTF-8"));
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
        try {
			for (int c; (c = in.read()) >= 0; System.out.print((char)c));
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
    }	
	
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.task_list, menu);
		return true;
	}
	
	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		// Handle action bar item clicks here. The action bar will
		// automatically handle clicks on the Home/Up button, so long
		// as you specify a parent activity in AndroidManifest.xml.
		int id = item.getItemId();
		if (id == R.id.action_settings) {
			return true;
		}
		return super.onOptionsItemSelected(item);
	}
	
	public static void Decode(CharSequence result) {
		// TODO decode the JSON CharSequence (result)
		//Log.e("char read", result);
		String i;
		i="ciao";
		}
	
	/* Helper function. put it in the same page, or in a library */
	protected static String downloadFile(String url) {

	// to fill-in url content
	StringBuilder builder = new StringBuilder();

	// local objects declarations
	HttpClient client = new DefaultHttpClient();
	HttpGet httpGet = new HttpGet(url);

	try {
	HttpResponse response = client.execute(httpGet);
	StatusLine statusLine = response.getStatusLine();
	int statusCode = statusLine.getStatusCode();
	if (statusCode == 200) {
	HttpEntity entity = response.getEntity();
	InputStream content = entity.getContent();
	BufferedReader reader = new BufferedReader(
	new InputStreamReader(content));
	String line;
	while ((line = reader.readLine()) != null) {
	builder.append(line);
	}
	} else {
	// Failed to download file

	}
	} catch (ClientProtocolException e) {
	e.printStackTrace();
	} catch (IOException e) {
	e.printStackTrace();
	} catch (Exception e) {
	e.printStackTrace();
	}
	return builder.toString();
	}
	
	public ArrayList<HashMap<String, String>> checklist1() {
		mylist = new ArrayList<HashMap<String, String>>();        
        HashMap<String, String> map = new HashMap<String, String>();	
        map.put("id", "");map.put("title", "");map.put("artist", "");map.put("duration", "");
        map.put("checkBoxField", "1. At least 72 hours before list,the qualified operator prepare a Plan for Critical Lifting");
        mylist.add(map);
        map = new HashMap<String, String>();map.put("id", "");map.put("title", "");map.put("artist", "");map.put("duration", "");	            	
        map.put("checkBoxField", "2. Tow truck located in right position as in Plan for Critical Lifting");
        mylist.add(map);
        map = new HashMap<String, String>();map.put("id", "");map.put("title", "");map.put("artist", "");map.put("duration", "");	            	
        map.put("checkBoxField", "3. Each tow truck has a stable and solid base");
        mylist.add(map);
        map = new HashMap<String, String>();map.put("id", "");map.put("title", "");map.put("artist", "");map.put("duration", "");	            	
        map.put("checkBoxField", "4. The load weigth don't overtake the weigth considered on plan");
        mylist.add(map);
        map = new HashMap<String, String>();map.put("id", "");map.put("title", "");map.put("artist", "");map.put("duration", "");	            		              
        map.put("checkBoxField", "5. All tools and equipments are in good conditions and suitable for lifting");
        mylist.add(map);
        map = new HashMap<String, String>();map.put("id", "");map.put("title", "");map.put("artist", "");map.put("duration", "");	            	
        map.put("checkBoxField", "6. All tools and equipments to lift are stable,packed and secured.");
        mylist.add(map);	           
        map = new HashMap<String, String>();map.put("id", "");map.put("title", "");map.put("artist", "");map.put("duration", "");	            	
        map.put("checkBoxField", "7. Review the minimum security distance between cables and all the tow truck parts.");
        mylist.add(map);	 
        map = new HashMap<String, String>();map.put("id", "");map.put("title", "");map.put("artist", "");map.put("duration", "");       	
        map.put("checkBoxField", "8. The lifting area is protected and the traffic is monitored.");
        mylist.add(map);
        map = new HashMap<String, String>();map.put("id", "");map.put("title", "");map.put("artist", "");map.put("duration", "");            	
        map.put("checkBoxField", "9. The tow truck operater has a clear view of the lifting area.");
        mylist.add(map);        
		
		return mylist;
	}
	
	public ArrayList<HashMap<String, String>> checklist2() {
		mylist = new ArrayList<HashMap<String, String>>();
        
        HashMap<String, String> map = new HashMap<String, String>();	
        map.put("id", "");map.put("title", "");map.put("artist", "");map.put("duration", "");
        map.put("checkBoxField", "1. Verifiy if it's possibile to complete hot work activity without execute hot works outside the restricted area.");
        mylist.add(map);
        map = new HashMap<String, String>();map.put("id", "");map.put("title", "");map.put("artist", "");map.put("duration", "");	            	
        map.put("checkBoxField", "2. Verify if it's possibile to move each hot work activity at 10 metres from any flammable source. If it's not possible, cover the flammable source.");
        mylist.add(map);
        map = new HashMap<String, String>();map.put("id", "");map.put("title", "");map.put("artist", "");map.put("duration", "");	            	
        map.put("checkBoxField", "3. Realize suitable barriers and positioning fire extinguisher close at hands.");
        mylist.add(map);
        map = new HashMap<String, String>();map.put("id", "");map.put("title", "");map.put("artist", "");map.put("duration", "");	            	
        map.put("checkBoxField", "4. Identify all the potential flammable source (for example fuel,chemistry sources and flavours).");
        mylist.add(map);
        map = new HashMap<String, String>();map.put("id", "");map.put("title", "");map.put("artist", "");map.put("duration", "");	            		              
        map.put("checkBoxField", "5. All the equipment are perfectly working.");
        mylist.add(map);
        map = new HashMap<String, String>();map.put("id", "");map.put("title", "");map.put("artist", "");map.put("duration", "");	            	
        map.put("checkBoxField", "6. When is not possible to move flamable sources, an operator will check the sources during the work and after 30 minut till the end of work .");
        mylist.add(map);	           
        map = new HashMap<String, String>();map.put("id", "");map.put("title", "");map.put("artist", "");map.put("duration", "");	            	
        map.put("checkBoxField", "7. The today bump test has been checked.");
        mylist.add(map);	 
        map = new HashMap<String, String>();map.put("id", "");map.put("title", "");map.put("artist", "");map.put("duration", "");       	
        map.put("checkBoxField", "8. Execute atmosphere to test using specific tools.");
        mylist.add(map);
        map = new HashMap<String, String>();map.put("id", "");map.put("title", "");map.put("artist", "");map.put("duration", "");            	
        map.put("checkBoxField", "9. Do not use open flame");
        mylist.add(map);
        map = new HashMap<String, String>();map.put("id", "");map.put("title", "");map.put("artist", "");map.put("duration", "");	            	
        map.put("checkBoxField", "10. All the hot work acitivities are suspended during the fuel refurniscing.");
        mylist.add(map);        
		
		return mylist;		
	}	
	
	public ArrayList<HashMap<String, String>> checklist3() {
		mylist = new ArrayList<HashMap<String, String>>();        
        HashMap<String, String> map = new HashMap<String, String>();	
        map.put("id", "");map.put("title", "");map.put("artist", "");map.put("duration", "");
        map.put("checkBoxField", "1. Stepladder (don't works on the stair).");
        mylist.add(map);
        map = new HashMap<String, String>();map.put("id", "");map.put("title", "");map.put("artist", "");map.put("duration", "");	            	
        map.put("checkBoxField", "2. Extension ladder | use only for access and inspection");
        mylist.add(map);
        map = new HashMap<String, String>();map.put("id", "");map.put("title", "");map.put("artist", "");map.put("duration", "");	            	
        map.put("checkBoxField", "3. Platform ladder");
        mylist.add(map);
        map = new HashMap<String, String>();map.put("id", "");map.put("title", "");map.put("artist", "");map.put("duration", "");	            	
        map.put("checkBoxField", "4. Elevator platform");
        mylist.add(map);
        map = new HashMap<String, String>();map.put("id", "");map.put("title", "");map.put("artist", "");map.put("duration", "");	            		              
        map.put("checkBoxField", "5. Mobile scaffolding.");
        mylist.add(map);
        map = new HashMap<String, String>();map.put("id", "");map.put("title", "");map.put("artist", "");map.put("duration", "");	            	
        map.put("checkBoxField", "6. For working on a roof in a non protected area use fastening belt.");
        mylist.add(map);	           
        
		
		return mylist;
	}
	
	public ArrayList<HashMap<String, String>> checklist4() {
		mylist = new ArrayList<HashMap<String, String>>();        
        HashMap<String, String> map = new HashMap<String, String>();	
        map.put("id", "");map.put("title", "");map.put("artist", "");map.put("duration", "");
        map.put("checkBoxField", "1. The work area is totale free from obstacles.");
        mylist.add(map);
        map = new HashMap<String, String>();map.put("id", "");map.put("title", "");map.put("artist", "");map.put("duration", "");	            	
        map.put("checkBoxField", "2. Check the minimum distance between electric cables and lifting equipment");
        mylist.add(map);
        map = new HashMap<String, String>();map.put("id", "");map.put("title", "");map.put("artist", "");map.put("duration", "");	            	
        map.put("checkBoxField", "3. The lifting tools respect the product specifications.");
        mylist.add(map);
        map = new HashMap<String, String>();map.put("id", "");map.put("title", "");map.put("artist", "");map.put("duration", "");	            	
        map.put("checkBoxField", "4. The lifting tools are in good conditions.");
        mylist.add(map);        
        map = new HashMap<String, String>();map.put("id", "");map.put("title", "");map.put("artist", "");map.put("duration", "");	            		              
        map.put("checkBoxField", "5. All tools and equipments are in good conditions and suitable for lifting");
        mylist.add(map);	                   
        map = new HashMap<String, String>();map.put("id", "");map.put("title", "");map.put("artist", "");map.put("duration", "");	            	
        map.put("checkBoxField", "6. All tools and equipments to lift are stable,packed and secured.");
        mylist.add(map);	           
        map = new HashMap<String, String>();map.put("id", "");map.put("title", "");map.put("artist", "");map.put("duration", "");	            	
        map.put("checkBoxField", "7. Review the minimum security distance between cables and all the tow truck parts.");
        mylist.add(map);	 
        map = new HashMap<String, String>();map.put("id", "");map.put("title", "");map.put("artist", "");map.put("duration", "");       	
        map.put("checkBoxField", "8. The lifting area is protected and the traffic is monitored.");
        mylist.add(map);
        map = new HashMap<String, String>();map.put("id", "");map.put("title", "");map.put("artist", "");map.put("duration", "");            	
        map.put("checkBoxField", "9. The tow truck operater has a clear view of the lifting area.");
        mylist.add(map);   
        		
		return mylist;		
	}
	
	public void getJSONdata(int checklistID) {
	
	        // http://androidarabia.net/quran4android/phpserver/connecttoserver.php
	    	StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
	    	StrictMode.setThreadPolicy(policy);
	        // Log.i(getClass().getSimpleName(), "send  task - start");
	       

	        // Instantiate a GET HTTP method
	        try {
	            	Log.i(getClass().getSimpleName(), "send  task - start");
	            	switch (checklistID) {
	    	        case 1:  
	    	        	mylist =checklist1();
	    	            break;
	    	        case 2:
	    	        	mylist =checklist2();
	    	            break;
	    	        case 3:  	    	  
	    	        	mylist =checklist3();
	    	            break;
	    	        case 4:  	    	  
	    	        	mylist =checklist4();
	    	            break;        
	    	    }

	            	 	                
	                
	                 
	            list=(ListView)findViewById(R.id.list);
	    		
	    		// Getting adapter by passing xml data ArrayList
	            adapter=new CheckAdapter(this, mylist);        
	            list.setAdapter(adapter);
	            
	            list.setClickable(true);

	            list.setOnItemClickListener(new OnItemClickListener() {
	            //
	            			@Override
	            			public void onItemClick(AdapterView<?> parent, View view,
	            					int position, long id) {
	            				Intent intentOpen = null;	
	            				HashMap<String, String> map = new HashMap<String, String>();
	            				//map=list.get(position);
	            				
	            				
	            				Object o = list.getItemAtPosition(position);	           		            			                    		        		
//	                    		intentOpen.putExtra("key", map);             		
//	                    		startActivity(intentOpen);
	                    		finish();	            				
	            			}
	            		});
	            
//	            .setOnItemClickListener(new AdapterView.OnItemClickListener() {
//
//	            	  @Override
//	            	  public void onItemClick(AdapterView<?> arg0, View arg1, int position, long arg3) {
//
//	            	    Object o = lv.getItemAtPosition(position);
//	            	    /* write you handling code like...
//	            	    String st = "sdcard/";
//	            	    File f = new File(st+o.toString());
//	            	    // do whatever u want to do with 'f' File object
//	            	    */  
//	            	  }
//	            	}); 

	        
	        // Log.i(getClass().getSimpleName(), "send  task - end");

	    } catch (Throwable t) {
	        Toast.makeText(this, "Request failed: " + t.toString(),
	                Toast.LENGTH_LONG).show();
	    }
	}

	
}
