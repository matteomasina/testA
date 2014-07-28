package com.example.testa;

import java.io.BufferedReader;
import java.io.File;
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
import org.apache.http.ParseException;
import org.apache.http.StatusLine;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.ResponseHandler;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.mime.HttpMultipartMode;
import org.apache.http.entity.mime.MultipartEntity;
import org.apache.http.entity.mime.content.FileBody;
import org.apache.http.impl.client.BasicResponseHandler;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.params.BasicHttpParams;
import org.apache.http.params.HttpConnectionParams;
import org.apache.http.params.HttpParams;
import org.apache.http.util.EntityUtils;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.app.ActionBar;
import android.app.Activity;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.graphics.drawable.ColorDrawable;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
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
import android.widget.ListView;
import android.widget.Toast;

public class Task_list extends Activity {
	private static final int TIMEOUT_MILLISEC = 10000;
	public String urlServer;
	//public String urlServer="http://93.113.136.157/nfc-tracker/public/taskALL";
	ListView list;
    LazyAdapter adapter;
    android.os.Handler customHandler;
    int lastItemID=-1;
    ArrayList<HashMap<String, String>> mylist;
    int operationType;
    String titleNotification;
    String textNotification;
    
    
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		//requestWindowFeature(Window.FEATURE_NO_TITLE);
		
		Intent mIntent = getIntent();
		operationType = mIntent.getIntExtra("opType", 0);
		
		ColorDrawable colorDrawable = new ColorDrawable();
		colorDrawable.setColor(0xffFFFFFF);
		ActionBar bar = getActionBar();
		bar.setIcon(R.drawable.nfc40x40);
		bar.setBackgroundDrawable(colorDrawable);
		if (operationType==0) {
			bar.setTitle(Html.fromHtml("<font color='#000000'>I MIEI TASK</font>"));
			urlServer="http://93.113.136.157/api/showUSERTASK";
		}
		if (operationType==1) {
			bar.setTitle(Html.fromHtml("<font color='#000000'>NUOVI TASK</font>"));
			urlServer="http://93.113.136.157/api/taskNOTASSIGNED";
			//Decode(downloadFile(urlServer));
			customHandler = new android.os.Handler();
	        customHandler.postDelayed(updateTimerThread, 0);
		}				
		setContentView(R.layout.activity_task_list);						
		getJSONdata();
	}
	
	
	public void write_on_db(String operazione,String dettagli,String stato,String id) {
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
        params.put("operazione", operazione);
        params.put("dettagli", dettagli);
        Date d = new Date();
        params.put("timestamp", d.toString());
        sendHTTPdataCheckLIST(params,url);
        if (stato.length()!=0){
	        url = null;
	    	try {
				url = new URL("http://93.113.136.157/api/updateTaskStatus");
			} catch (MalformedURLException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
	    	
	        params.put("id", id);
	        params.put("field", "stato");
	        params.put("stato", stato);		            
	        sendHTTPdataCheckLIST(params,url);
        }
    	finish();
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
	
	private void sendFile(String fileName) throws ParseException, IOException  {
		 
	 	 //fileName="/storage/emulated/0/NFC-TRACKER/" + nomeFile;
		
		String[] parts = fileName.split("id");
		String s = parts[1]; 
		String[] parts2 = parts[1].split("-");
		String stabilimento = parts2[0];
		
		// the file to be posted
		 String textFile = fileName;
		 //Log.v(TAG, "textFile: " + textFile);
		  
		 // the URL where the file will be posted
		 String postReceiverUrl = "http://93.113.136.157/api/uploadfile";
		 //Log.v(TAG, "postURL: " + postReceiverUrl);
		 final HttpParams httpParams = new BasicHttpParams();
		    HttpConnectionParams.setConnectionTimeout(httpParams, 30000);
		 // new HttpClient
		 HttpClient httpClient = new DefaultHttpClient(httpParams);
		  
		 // post header
		 HttpPost httpPost = new HttpPost(postReceiverUrl);
		  
		 File file = new File(textFile);
		 FileBody fileBody = new FileBody(file);
		  
		 MultipartEntity reqEntity = new MultipartEntity(HttpMultipartMode.BROWSER_COMPATIBLE);
		 reqEntity.addPart("file", fileBody);
		 httpPost.setEntity(reqEntity);
		 HttpResponse response = null;
		 // execute HTTP post request
		 try {
			 response = httpClient.execute(httpPost);
			 Toast.makeText(getApplicationContext(),
	                 "IMMAGINE INVIATA", Toast.LENGTH_SHORT)
	                 .show();
			 write_on_db("IMMAGINE ACQUISITA", "STAZIONE : " + stabilimento, "","");
			} catch (ClientProtocolException e) {
			  e.printStackTrace();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		 
		 
		 HttpEntity resEntity = response.getEntity();
		  
		 if (resEntity != null) {
		      
		     String responseStr = EntityUtils.toString(resEntity).trim();
		    // Log.v(TAG, "Response: " +  responseStr);
		      
		     // you can add an if statement here and do other actions based on the response
		 }
		 
	 }
	 	 

	private void sendCapturedFileToServer(){
		  String path = Environment.getExternalStorageDirectory().toString()+"/NFC-TRACKER";
		  Log.d("Files", "Path: " + path);
		  File f = new File(path);        
		  File file[] = f.listFiles();
		  Log.d("Files", "Size: "+ file.length);		  
		  try {
			  if (file.length>0) {
				  for (int i=0; i < file.length; i++)
				  {
				      Log.d("Files", "FileName:" + file[i].getName());
				      sendFile(path + "/" + file[i].getName());
				      boolean deleted = file[i].delete();
				      if (deleted) {
				    	  Log.d("FileName:" + file[i].getName(),"DELETE");
				      } else {
				    	  Log.d("FileName:" + file[i].getName(),"NOT DELETE");
				      }
				  }
			  }
			} catch (ParseException | IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
				Log.d("e.printStackTrace","");
			}
	}
	
	private Runnable updateTimerThread = new Runnable()
	{
	        public void run()
	        {
	            customHandler.postDelayed(this, 20000);
	            getJSONdata();
	            sendCapturedFileToServer();
	        }
	};

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
	
	public void showNotification(HashMap<String, String> map){

        // define sound URI, the sound to be played when there's a notification
        Uri soundUri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);

        // intent triggered, you can add other intent for other actions
        Intent intent = new Intent(Task_list.this, AcquireTAGActivity.class);
        PendingIntent pIntent = PendingIntent.getActivity(Task_list.this, 0, intent, 0);
        
        String impianto = map.get("title").toString();
        int icona = 0;
        switch(map.get("priorita")) {
        case "p0":
      	  icona=(R.drawable.p0);
        	break;
        case "p1":
        	icona=(R.drawable.p1);
        	break;
        case "p2":
        	icona=(R.drawable.p2);
        	break;
        case "p3":
        	icona=(R.drawable.p3);
        	break;
        case "p4":
        	icona=(R.drawable.p4);
        	break;
        case "p5":
        	icona=(R.drawable.p5);
        	break;	                	           
		  }	
        // this is it, we'll build the notification!
        // in the addAction method, if you don't want any icon, just set the first param to 0
        
        
        
        Notification mNotification = new Notification.Builder(this)
            .setContentTitle( map.get("artist").toString())
            .setContentText(impianto)
            .setSmallIcon(icona)
            .setContentIntent(pIntent)
            .setSound(soundUri)
            .setWhen(System.currentTimeMillis())            
            .addAction(0, "Remind", pIntent)            
            .build();
        
        
        NotificationManager notificationManager = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);

        notificationManager.notify(0, mNotification);
    }

    public void cancelNotification(int notificationId){

        if (Context.NOTIFICATION_SERVICE!=null) {
            String ns = Context.NOTIFICATION_SERVICE;
            NotificationManager nMgr = (NotificationManager) getApplicationContext().getSystemService(ns);
            nMgr.cancel(notificationId);
        }
    }
	
	public void getJSONdata() {
	    try {	        
	    	StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
	    	StrictMode.setThreadPolicy(policy);
	        HttpParams httpParams = new BasicHttpParams();
	        HttpConnectionParams.setConnectionTimeout(httpParams,TIMEOUT_MILLISEC);
	        HttpConnectionParams.setSoTimeout(httpParams, TIMEOUT_MILLISEC);
	        HttpParams p = new BasicHttpParams();
	        // p.setParameter("name", pvo.getName());
	        p.setParameter("user", "1");

	        // Instantiate an HttpClient
	        HttpClient httpclient = new DefaultHttpClient(p);
	        
	        HttpGet  httppost = new HttpGet (urlServer);

	        // Instantiate a GET HTTP method
	        try {
	            Log.i(getClass().getSimpleName(), "send  task - start");
	            //
	            List<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>(
	                    2);
	            nameValuePairs.add(new BasicNameValuePair("user", "1"));
	            //httppost.setEntity(new UrlEncodedFormEntity(nameValuePairs));
	            ResponseHandler<String> responseHandler = new BasicResponseHandler();
	            String responseBody = httpclient.execute(httppost,
	                    responseHandler);
	            JSONArray  jArray     =  new JSONArray(responseBody);

	            mylist = new ArrayList<HashMap<String, String>>();

	            for (int i = 0; i < jArray.length(); i++) {
	                HashMap<String, String> map = new HashMap<String, String>();
	            	JSONObject jsonPage = jArray.getJSONObject(i);
	               
					String id = jsonPage.getString("id");
					
					
	                String impianto = jsonPage.getString("impianto");
	                String[] parts = impianto.split("-");
	                String priority=jsonPage.getString("priorita");
	                
	                String citta=parts[0].trim();
	                //String provincia=parts[1].trim();
	                //String cap=parts[2].trim();
	                String via=parts[1].trim();
	                

//	                Toast.makeText(this, jsonPage.getString("id"), Toast.LENGTH_LONG).show();
	                map.put("citta", citta);
	                //map.put("provincia",provincia );
	                //map.put("cap",cap );
	                map.put("via",via );
	                map.put("stato", jsonPage.getString("stato"));
	                map.put("creationtime", jsonPage.getString("creationTime"));
	                map.put("priorita", jsonPage.getString("priorita"));
	                map.put("note", jsonPage.getString("note"));
	                map.put("stopTime", jsonPage.getString("stopTime"));
	                map.put("id", jsonPage.getString("id"));
	                map.put("title", parts[0].trim() +" (" + parts[1].trim() + ")");
	                map.put("artist", jsonPage.getString("oggetto"));
	                map.put("duration", jsonPage.getString("creationTime"));
	            	map.put("thumb_url", priority);           
	              mylist.add(map);
	              int actualItemID=Integer.parseInt(id);
					
					if (actualItemID>lastItemID) {
						if (lastItemID!=-1) {
							showNotification(map);
						}
						//showNotification();
						lastItemID=actualItemID;
					}
	            }
	            list=(ListView)findViewById(R.id.list);
	    		
	    		// Getting adapter by passing xml data ArrayList
	            adapter=new LazyAdapter(this, mylist);        
	            list.setAdapter(adapter);
	            
	            list.setClickable(true);

	            list.setOnItemClickListener(new OnItemClickListener() {
	            //
	            			@Override
	            			public void onItemClick(AdapterView<?> parent, View view,
	            					int position, long id) {
	            				Intent intentOpen = null;	
	            				HashMap<String, String> map = new HashMap<String, String>();
	            				map=mylist.get(position);
	            				if (operationType==0) {
	            					intentOpen = new Intent(Task_list.this,MyTaskDetailActivity.class);
	            				}
	            				if (operationType==1) {
	            					intentOpen = new Intent(Task_list.this,AcceptTaskActivity.class);
	            				}
	            				
	            				 		           		            			                    		        		
	                    		intentOpen.putExtra("key", map);             		
	                    		startActivity(intentOpen);
//	                    		finish();	            				
	            			}
	            		});		            	           
	        } catch (ClientProtocolException e) {
	            // TODO Auto-generated catch block
	            e.printStackTrace();
	        } catch (IOException e) {
	            // TODO Auto-generated catch block
	            e.printStackTrace();
	        }
	        // Log.i(getClass().getSimpleName(), "send  task - end");

	    } catch (Throwable t) {
	        Toast.makeText(this, "Request failed: " + t.toString(),
	                Toast.LENGTH_LONG).show();
	    }
	}

	
}
