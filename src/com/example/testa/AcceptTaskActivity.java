package com.example.testa;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.Reader;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.ProtocolException;
import java.net.URL;
import java.net.URLEncoder;
import java.util.Date;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;

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
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

public class AcceptTaskActivity extends Activity {
	String id;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_accept_task2);
		ColorDrawable colorDrawable = new ColorDrawable();
		  colorDrawable.setColor(0xffFFFFFF);
		  ActionBar bar = getActionBar();
		  bar.setIcon(R.drawable.nfc40x40);
		  bar.setBackgroundDrawable(colorDrawable);
		  bar.setTitle(Html.fromHtml("<font color='#000000'>DETTAGLI TASK</font>"));
		  Intent intent = getIntent(); 
		  @SuppressWarnings("unchecked")
		final
		  HashMap<String, String> hashMap = (HashMap<String, String>) intent.getSerializableExtra("key");		  
		  TextView stabilimento = (TextView)findViewById(R.id.stabilimentoMyTask);
		  TextView indirizzo = (TextView)findViewById(R.id.indirizzoMyTask);
		  TextView startTime = (TextView)findViewById(R.id.startTimeMyTask);
		  TextView note = (TextView)findViewById(R.id.noteMyTask);
		  TextView problema = (TextView)findViewById(R.id.problemaMyTask);
		  ImageView icona = (ImageView)findViewById(R.id.iconaMyTask);
		  id=hashMap.get("id");
		  stabilimento.setText(hashMap.get("citta"));
		  indirizzo.setText(hashMap.get("via"));
		  startTime.setText("TASK APERTO IL : " + hashMap.get("creationtime"));
		  if (hashMap.get("note") != "null") note.setText(hashMap.get("note"));
		  problema.setText(hashMap.get("artist"));
		  switch(hashMap.get("priorita")) {
          case "P0":
        	  icona.setImageResource(R.drawable.p0);
          	break;
          case "P1":
        	  icona.setImageResource(R.drawable.p1);
          	break;
          case "P2":
        	  icona.setImageResource(R.drawable.p2);
          	break;
          case "P3":
        	  icona.setImageResource(R.drawable.p3);
          	break;
          case "P4":
        	  icona.setImageResource(R.drawable.p4);
          	break;
          case "P5":
        	  icona.setImageResource(R.drawable.p5);
          	break;	                	           
		  }	   
		  Button button = (Button) findViewById(R.id.btnAccept);

		    button.setOnClickListener(new View.OnClickListener(){
		        @Override
		        public void onClick(View v) {
		            // INSERIMENTO NEL DEVICE LOG
		        	if (android.os.Build.VERSION.SDK_INT > 9) {
                		StrictMode.ThreadPolicy policy = 
                		        new StrictMode.ThreadPolicy.Builder().permitAll().build();
                		StrictMode.setThreadPolicy(policy);
                		}
		        	URL url = null;
		        	Map<String,Object> params = new LinkedHashMap<>();
//					
//		        	// UPDATE STATO TASK
//		        	
		        	
					try {
						
						url = new URL("http://93.113.136.157/api/insertLog");
					} catch (MalformedURLException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
		        	params = new LinkedHashMap<>();
		            params.put("dev", "Acm-e (GT-I9105P)");
		            params.put("user", "Mauro Bianchi");
		            params.put("operazione", "ASSEGNATO");
		            params.put("dettagli", "il task " + hashMap.get("citta") + " è stato assegnato");		            
		        	sendHTTPdata(params,url);
		        	Log.e("insert log", "ASSEGNATO");
		        	url = null;
		        	try {
						url = new URL("http://93.113.136.157/api/updateTaskStatus");
					} catch (MalformedURLException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
		        	
		            params.put("id", id);
		            params.put("field", "stato");
		            params.put("stato", "ASSEGNATO");		            
		        	sendHTTPdata(params,url);
		        	finish();
		        }
		    });
	}

	public static void sendHTTPdata(Map<String,Object> params,URL url) {
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
	
	
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.accept_task, menu);
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
}
