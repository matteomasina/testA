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
	private static final int TIMEOUT_MILLISEC = 10000;
	
	ListView list;
    CheckAdapter adapter;
    String id;
    String stabilimento;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		//requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.activity_workcheck_list);
		ColorDrawable colorDrawable = new ColorDrawable();
		colorDrawable.setColor(0xffFFFFFF);
		ActionBar bar = getActionBar();
		bar.setBackgroundDrawable(colorDrawable);
		bar.setIcon(R.drawable.lavori_a_caldo);
		bar.setTitle(Html.fromHtml("<font color='#000000'>Check List | Lavoro a caldo </font>"));
		
		Intent mIntent = getIntent();		
		id = mIntent.getExtras().getString("id");
		stabilimento = mIntent.getExtras().getString("stabilimento");
		Log.i(getClass().getSimpleName(), "id task =" + id);
		
		getJSONdata();
		Button button = (Button) findViewById(R.id.btnAcceptWorkList);
		

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
	            params.put("operazione", "CHECKLIST SICUREZZA");
	            params.put("dettagli", "APPROVATA CHECK LIST.STAZIONE : " +stabilimento);
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
	            params.put("stato", "CHECKLIST SICUREZZA");		            
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
	
	public void getJSONdata() {
	
	        // http://androidarabia.net/quran4android/phpserver/connecttoserver.php
	    	StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
	    	StrictMode.setThreadPolicy(policy);
	        // Log.i(getClass().getSimpleName(), "send  task - start");
	       

	        // Instantiate a GET HTTP method
	        try {
	            Log.i(getClass().getSimpleName(), "send  task - start");


	            ArrayList<HashMap<String, String>> mylist = 
	                   new ArrayList<HashMap<String, String>>();

	            
	                HashMap<String, String> map = new HashMap<String, String>();	            	
	               	map.put("id", "");	                
	                map.put("title", "");
	                map.put("artist", "");
	                map.put("duration", "");
	                map.put("checkBoxField", "1. Determinare se è possibile completare l'attività senza eseguire lavori a caldo o al di fuori dell'area ristretta.");
	                mylist.add(map);
	                map = new HashMap<String, String>();	            	
	               	map.put("id", "");	                
	                map.put("title", "");
	                map.put("artist", "");
	                map.put("duration", "");
	                map.put("checkBoxField", "2. Se possibile, spostare le attività a caldo a 10 m da qualsiasi materiale combustibile o infiammabile. Se ciò non fosse possibile,coprire il materiale combustibile.");
	                mylist.add(map);
	                map = new HashMap<String, String>();	            	
	               	map.put("id", "");	                
	                map.put("title", "");
	                map.put("artist", "");
	                map.put("duration", "");
	                map.put("checkBoxField", "3. Realizzare barriere adeguate e posizionare estintori carichi a portata di mano.");
	                mylist.add(map);
	                map = new HashMap<String, String>();	            	
	               	map.put("id", "");	                
	                map.put("title", "");
	                map.put("artist", "");
	                map.put("duration", "");
	                map.put("checkBoxField", "4. Identificare tutte le fonti di vapore potenzialmente esplosivi (ad esempio carburante, fogne,sostanze chimiche).");
	                mylist.add(map);
	                map = new HashMap<String, String>();	            	
	               	map.put("id", "");	                
	                map.put("title", "");
	                map.put("artist", "");
	                map.put("duration", "");
	                map.put("checkBoxField", "5. Tutte le attrezzature dispongono di protezioni di sicurezza adeguate e sono messe a massa (MT).");
	                mylist.add(map);
	                map = new HashMap<String, String>();	            	
	               	map.put("id", "");	                
	                map.put("title", "");
	                map.put("artist", "");
	                map.put("duration", "");
	                map.put("checkBoxField", "6. Quando il lavoro a caldo viene svolto entro 10 m da materiale combustibile che non è possibile spostare, ad esempio strutture in legno, un addetto dovrà occuparsi unicamente di controllare eventuali incendi durante il lavoro e nei 30 minuti successivi.");
	                mylist.add(map);	           
	                map = new HashMap<String, String>();	            	
	               	map.put("id", "");	                
	                map.put("title", "");
	                map.put("artist", "");
	                map.put("duration", "");
	                map.put("checkBoxField", "7. Il Bump Test di OGGI dell’ esplosimetro/LEL è stato completato e superato.");
	                mylist.add(map);	 
	                map = new HashMap<String, String>();	            	
	               	map.put("id", "");	                
	                map.put("title", "");
	                map.put("artist", "");
	                map.put("duration", "");
	                map.put("checkBoxField", "8. Esegui test atmosferici per confermare LEL 0% utilizzando un dispositivo di test certificato (vedere il registro di seguito).");
	                mylist.add(map);
	                map = new HashMap<String, String>();	            	
	               	map.put("id", "");	                
	                map.put("title", "");
	                map.put("artist", "");
	                map.put("duration", "");
	                map.put("checkBoxField", "9. Non vengono utilizzate attrezzature che producano scintille o fiamme libere in area ristretta (es. saldatrice,smerigliatrice angolare,cannello, ecc.)");
	                mylist.add(map);
	                map = new HashMap<String, String>();	            	
	               	map.put("id", "");	                
	                map.put("title", "");
	                map.put("artist", "");
	                map.put("duration", "");
	                map.put("checkBoxField", "10. Tutte le attività che richiedono l'utilizzo di lavori a caldo vengono interrotte durante la consegna di carburante.");
	                mylist.add(map);
	                map = new HashMap<String, String>();	            	
	               	map.put("id", "");	                
	                map.put("title", "");
	                map.put("artist", "");
	                map.put("duration", "");
	                map.put("checkBoxField", "11. Sono state ridotte le fonti di vapori esplosivi (ad esempio, copertura delle fogne).");
	                mylist.add(map);
	                map = new HashMap<String, String>();	            	
	               	map.put("id", "");	                
	                map.put("title", "");
	                map.put("artist", "");
	                map.put("duration", "");
	                map.put("checkBoxField", "12. È disponibile il controllo di eventuali incendi (addetto anticendio) durante il lavoro e per 30 minuti dal suo completamento.");
	                mylist.add(map);	                
	                
	                 
	            list=(ListView)findViewById(R.id.list);
	    		
	    		// Getting adapter by passing xml data ArrayList
	            adapter=new CheckAdapter(this, mylist);        
	            list.setAdapter(adapter);
	            
	            

	        
	        // Log.i(getClass().getSimpleName(), "send  task - end");

	    } catch (Throwable t) {
	        Toast.makeText(this, "Request failed: " + t.toString(),
	                Toast.LENGTH_LONG).show();
	    }
	}

	
}
