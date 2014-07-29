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
	        	titleList = "Sollevamenti critici | Checklist Rischi Maggiori";
	        	iconList=R.drawable.sollevamenti_critici;
	            break;
	        case 2:  
	        	titleList = "Check List | Lavoro a caldo ";
	        	iconList=R.drawable.lavori_a_caldo;
	            break;
	        case 3:  
	        	titleList = "Lavori in quota | Higher Risk Checklist";
	        	iconList=R.drawable.lavori_in_quota;
	            break;
	        case 4:  
	        	titleList = "Sollevamenti minori | Higher Risk Checklist";
	        	iconList=R.drawable.sollevamenti_minori;
	            break;        
	    }
		
		
		getJSONdata(checkListID);
		ActionBar bar = getActionBar();
		bar.setBackgroundDrawable(colorDrawable);
		bar.setIcon(iconList);
		bar.setTitle(Html.fromHtml("<font color='#000000'>" + titleList + "</font>"));
		
		
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
	
	public ArrayList<HashMap<String, String>> checklist1() {
		mylist = new ArrayList<HashMap<String, String>>();        
        HashMap<String, String> map = new HashMap<String, String>();	
        map.put("id", "");map.put("title", "");map.put("artist", "");map.put("duration", "");
        map.put("checkBoxField", "1. Almeno 72 ore prima del sollevamento, la persona qualificata prepara un Piano di sollevamento critico");
        mylist.add(map);
        map = new HashMap<String, String>();map.put("id", "");map.put("title", "");map.put("artist", "");map.put("duration", "");	            	
        map.put("checkBoxField", "2. Gru dislocate nella posizione corretta come indicato nel Piano di sollevamento critico");
        mylist.add(map);
        map = new HashMap<String, String>();map.put("id", "");map.put("title", "");map.put("artist", "");map.put("duration", "");	            	
        map.put("checkBoxField", "3. Ogni gru poggia una base stabile e solida");
        mylist.add(map);
        map = new HashMap<String, String>();map.put("id", "");map.put("title", "");map.put("artist", "");map.put("duration", "");	            	
        map.put("checkBoxField", "4. Il peso del carico effettivo non supera il peso considerato nell’ analisi del sollevamento");
        mylist.add(map);
        map = new HashMap<String, String>();map.put("id", "");map.put("title", "");map.put("artist", "");map.put("duration", "");	            		              
        map.put("checkBoxField", "5. Il mezzo di sollevamento,i dispositivi di sollevamento (imbragature/cinghie/ganci) sono in buone condizioni, sono adeguati al sollevamento e vengono utilizzati nel rispetto della loro capacità nominale");
        mylist.add(map);
        map = new HashMap<String, String>();map.put("id", "");map.put("title", "");map.put("artist", "");map.put("duration", "");	            	
        map.put("checkBoxField", "6. L’attrezzatura o il materiale da sollevare sono stabili, imballati e legati.I dispositivi di sollevamento sono saldamente fissati ed equilibrati");
        mylist.add(map);	           
        map = new HashMap<String, String>();map.put("id", "");map.put("title", "");map.put("artist", "");map.put("duration", "");	            	
        map.put("checkBoxField", "7. Rivedere lo distanza minima di sicurezza tra i cavi elettrici e qualsiasi parte della gru, del carico o del cavo di carico.");
        mylist.add(map);	 
        map = new HashMap<String, String>();map.put("id", "");map.put("title", "");map.put("artist", "");map.put("duration", "");       	
        map.put("checkBoxField", "8. L'area di sollevamento e il raggio di oscillazione sono protetti ed il traffico è controllato.");
        mylist.add(map);
        map = new HashMap<String, String>();map.put("id", "");map.put("title", "");map.put("artist", "");map.put("duration", "");            	
        map.put("checkBoxField", "9. Gli operatori della gru hanno una vista chiara dell'area di lavoro.");
        mylist.add(map);
        map = new HashMap<String, String>();map.put("id", "");map.put("title", "");map.put("artist", "");map.put("duration", "");	            	
        map.put("checkBoxField", "10.Vengono utilizzati segnalatori/osservatori (Moviere) dedicati e, per ognuno di essi,è stato definito il metodo di comunicazione utilizzato. ");
        mylist.add(map);
        map = new HashMap<String, String>();map.put("id", "");map.put("title", "");map.put("artist", "");map.put("duration", "");	            	
        map.put("checkBoxField", "11. Vengono utilizzati cavi di controvento/funi guida per i carichi sospesi.");
        mylist.add(map);

		
		return mylist;
	}
	
	public ArrayList<HashMap<String, String>> checklist2() {
		mylist = new ArrayList<HashMap<String, String>>();
        
        HashMap<String, String> map = new HashMap<String, String>();	
        map.put("id", "");map.put("title", "");map.put("artist", "");map.put("duration", "");
        map.put("checkBoxField", "1. Determinare se è possibile completare l'attività senza eseguire lavori a caldo o al di fuori dell'area ristretta.");
        mylist.add(map);
        map = new HashMap<String, String>();map.put("id", "");map.put("title", "");map.put("artist", "");map.put("duration", "");	            	
        map.put("checkBoxField", "2. Se possibile, spostare le attività a caldo a 10 m da qualsiasi materiale combustibile o infiammabile. Se ciò non fosse possibile,coprire il materiale combustibile.");
        mylist.add(map);
        map = new HashMap<String, String>();map.put("id", "");map.put("title", "");map.put("artist", "");map.put("duration", "");	            	
        map.put("checkBoxField", "3. Realizzare barriere adeguate e posizionare estintori carichi a portata di mano.");
        mylist.add(map);
        map = new HashMap<String, String>();map.put("id", "");map.put("title", "");map.put("artist", "");map.put("duration", "");	            	
        map.put("checkBoxField", "4. Identificare tutte le fonti di vapore potenzialmente esplosivi (ad esempio carburante, fogne,sostanze chimiche).");
        mylist.add(map);
        map = new HashMap<String, String>();map.put("id", "");map.put("title", "");map.put("artist", "");map.put("duration", "");	            		              
        map.put("checkBoxField", "5. Tutte le attrezzature dispongono di protezioni di sicurezza adeguate e sono messe a massa (MT).");
        mylist.add(map);
        map = new HashMap<String, String>();map.put("id", "");map.put("title", "");map.put("artist", "");map.put("duration", "");	            	
        map.put("checkBoxField", "6. Quando il lavoro a caldo viene svolto entro 10 m da materiale combustibile che non è possibile spostare, ad esempio strutture in legno, un addetto dovrà occuparsi unicamente di controllare eventuali incendi durante il lavoro e nei 30 minuti successivi.");
        mylist.add(map);	           
        map = new HashMap<String, String>();map.put("id", "");map.put("title", "");map.put("artist", "");map.put("duration", "");	            	
        map.put("checkBoxField", "7. Il Bump Test di OGGI dell’ esplosimetro/LEL è stato completato e superato.");
        mylist.add(map);	 
        map = new HashMap<String, String>();map.put("id", "");map.put("title", "");map.put("artist", "");map.put("duration", "");       	
        map.put("checkBoxField", "8. Esegui test atmosferici per confermare LEL 0% utilizzando un dispositivo di test certificato (vedere il registro di seguito).");
        mylist.add(map);
        map = new HashMap<String, String>();map.put("id", "");map.put("title", "");map.put("artist", "");map.put("duration", "");            	
        map.put("checkBoxField", "9. Non vengono utilizzate attrezzature che producano scintille o fiamme libere in area ristretta (es. saldatrice,smerigliatrice angolare,cannello, ecc.)");
        mylist.add(map);
        map = new HashMap<String, String>();map.put("id", "");map.put("title", "");map.put("artist", "");map.put("duration", "");	            	
        map.put("checkBoxField", "10. Tutte le attività che richiedono l'utilizzo di lavori a caldo vengono interrotte durante la consegna di carburante.");
        mylist.add(map);
        map = new HashMap<String, String>();map.put("id", "");map.put("title", "");map.put("artist", "");map.put("duration", "");	            	
        map.put("checkBoxField", "11. Sono state ridotte le fonti di vapori esplosivi (ad esempio, copertura delle fogne).");
        mylist.add(map);
        map = new HashMap<String, String>();map.put("id", "");map.put("title", "");map.put("artist", "");map.put("duration", "");
       	map.put("checkBoxField", "12. È disponibile il controllo di eventuali incendi (addetto anticendio) durante il lavoro e per 30 minuti dal suo completamento.");
        mylist.add(map);
		
		return mylist;		
	}	
	
	public ArrayList<HashMap<String, String>> checklist3() {
		mylist = new ArrayList<HashMap<String, String>>();        
        HashMap<String, String> map = new HashMap<String, String>();	
        map.put("id", "");map.put("title", "");map.put("artist", "");map.put("duration", "");
        map.put("checkBoxField", "1. Scala A LIBRETTO (non lavorare sulla scala).");
        mylist.add(map);
        map = new HashMap<String, String>();map.put("id", "");map.put("title", "");map.put("artist", "");map.put("duration", "");	            	
        map.put("checkBoxField", "2. Scala A SFILO | usare solo per ispezioni/accesso (non lavorare sulla scala)");
        mylist.add(map);
        map = new HashMap<String, String>();map.put("id", "");map.put("title", "");map.put("artist", "");map.put("duration", "");	            	
        map.put("checkBoxField", "3. Scala a PIATTAFORMA");
        mylist.add(map);
        map = new HashMap<String, String>();map.put("id", "");map.put("title", "");map.put("artist", "");map.put("duration", "");	            	
        map.put("checkBoxField", "4. PIATTAFORMA A ELEVAZIONE (elevatore a pantografo/ a cestello)");
        mylist.add(map);
        map = new HashMap<String, String>();map.put("id", "");map.put("title", "");map.put("artist", "");map.put("duration", "");	            		              
        map.put("checkBoxField", "5. IMPALCATURA MOBILE (Trabattello).E’ necessaria autorizzazione di Exxonmobil per usarlo.");
        mylist.add(map);
        map = new HashMap<String, String>();map.put("id", "");map.put("title", "");map.put("artist", "");map.put("duration", "");	            	
        map.put("checkBoxField", "6. Per LAVORI entro 2m da un margine non protetto o su un tetto con pendenza > 20%,usare un'imbragatura di sicurezza approvata, il cordino e un punto di ancoraggio idoneo.");
        mylist.add(map);	           
        
		
		return mylist;
	}
	
	public ArrayList<HashMap<String, String>> checklist4() {
		mylist = new ArrayList<HashMap<String, String>>();        
        HashMap<String, String> map = new HashMap<String, String>();	
        map.put("id", "");map.put("title", "");map.put("artist", "");map.put("duration", "");
        map.put("checkBoxField", "1. L’area di lavoro è priva di ostacoli aerei o laterali.");
        mylist.add(map);
        map = new HashMap<String, String>();map.put("id", "");map.put("title", "");map.put("artist", "");map.put("duration", "");	            	
        map.put("checkBoxField", "2. Controlla la distanza minima dei cavi elettrici da qualsiasi parte del mezzo di sollevamento, del carico e dei dispositivi di sollevamento.");
        mylist.add(map);
        map = new HashMap<String, String>();map.put("id", "");map.put("title", "");map.put("artist", "");map.put("duration", "");	            	
        map.put("checkBoxField", "3. Il mezzo di sollevamento è idoneo e rispetta le specifiche del produttore.");
        mylist.add(map);
        map = new HashMap<String, String>();map.put("id", "");map.put("title", "");map.put("artist", "");map.put("duration", "");	            	
        map.put("checkBoxField", "4. Il mezzo di sollevamento è in buone condizioni e dotato di un rapporto tra carico e capacità di carico adeguata.");
        mylist.add(map);
        map = new HashMap<String, String>();map.put("id", "");map.put("title", "");map.put("artist", "");map.put("duration", "");	            		              
        map.put("checkBoxField", "5.I dispositivi di sollevamento (imbragature/cinghie/ganci) sono in buone condizioni e dotati di un rapporto tra carico e capacità di carico adeguata.");
        mylist.add(map);
        map = new HashMap<String, String>();map.put("id", "");map.put("title", "");map.put("artist", "");map.put("duration", "");	            	
        map.put("checkBoxField", "6. Le attrezzature o i materiali da sollevare sono stabili, imbragati e legati,i dispositivi di sollevamento (imbragature/cinghie) sono fissate saldamente e sono in equilibrio. Se si usa una pedana, che sia delle giuste dimensioni.");
        mylist.add(map);	           
        map = new HashMap<String, String>();map.put("id", "");map.put("title", "");map.put("artist", "");map.put("duration", "");	            	
        map.put("checkBoxField", "7. Appropriate barriere sono poste a delimitazione dell’area di lavoro.");
        mylist.add(map);	 
        map = new HashMap<String, String>();map.put("id", "");map.put("title", "");map.put("artist", "");map.put("duration", "");       	
        map.put("checkBoxField", "8. Il conducente del mezzo di sollevamento ha una visuale chiara dell'area di lavoro.");
        mylist.add(map);
        map = new HashMap<String, String>();map.put("id", "");map.put("title", "");map.put("artist", "");map.put("duration", "");            	
        map.put("checkBoxField", "9. E’ previsto un segnalatore/osservatore (moviere) ed il metodo di comunicazione con esso è definito.");
        mylist.add(map);
        map = new HashMap<String, String>();map.put("id", "");map.put("title", "");map.put("artist", "");map.put("duration", "");	            	
        map.put("checkBoxField", "10. Utilizza cavi di controvento/funi guida per carichi sospesi, ove necessari al carico.");
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
