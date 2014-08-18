package com.example.testa;

import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.view.*;
import android.view.View.OnTouchListener;
import android.os.Handler;
import android.widget.*;
import android.media.RingtoneManager;
import android.net.Uri;
import android.nfc.*;
import android.nfc.tech.*;
import android.content.*;
import android.app.*;
import android.util.*;
import android.os.*;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.IntentFilter.MalformedMimeTypeException;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.Reader;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.ProtocolException;
import java.net.URL;
import java.net.URLEncoder;
import java.util.*;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.ParseException;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.mime.HttpMultipartMode;
import org.apache.http.entity.mime.MultipartEntity;
import org.apache.http.entity.mime.content.FileBody;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.params.BasicHttpParams;
import org.apache.http.params.HttpConnectionParams;
import org.apache.http.params.HttpParams;
import org.apache.http.util.EntityUtils;

public class MainActivity extends ActionBarActivity {
	
	private static final String MIME_TEXT_PLAIN = null;
	private NfcAdapter mAdapter;
    private PendingIntent pendingIntent;
    private IntentFilter[] mIntentFilters;
    private String[][] mNFCTechLists;
  
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
		requestWindowFeature(Window.FEATURE_NO_TITLE);
        /*getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
            WindowManager.LayoutParams.FLAG_FULLSCREEN);*/
		setContentView(R.layout.activity_main);
		
		blink();								
		mAdapter = NfcAdapter.getDefaultAdapter(this);
		pendingIntent = PendingIntent.getActivity(this, 0, new Intent(this, getClass()).addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP), 0);		     						         		                                
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
    public void onNewIntent(Intent intent) {
        String action = intent.getAction();
        Tag tag = intent.getParcelableExtra(NfcAdapter.EXTRA_TAG);
 
        String s = action + "\n\n" + tag.toString();
 
        // parse through all NDEF messages and their records and pick text type only
        Parcelable[] data = intent.getParcelableArrayExtra(NfcAdapter.EXTRA_NDEF_MESSAGES);
        if (data != null) {
            try {
                for (int i = 0; i < data.length; i++) {
                    NdefRecord [] recs = ((NdefMessage)data[i]).getRecords();
                    byte[] k = recs [0].getPayload();
                    String str = new String(k);
                    Log.e("TagINFO", str);
                    
                    for (int j = 0; j < recs.length; j++) {
                        if (recs[j].getTnf() == NdefRecord.TNF_WELL_KNOWN &&
                            Arrays.equals(recs[j].getType(), NdefRecord.RTD_TEXT)) {
                            byte[] payload = recs[j].getPayload();
                            String textEncoding = ((payload[0] & 0200) == 0) ? "UTF-8" : "UTF-16";
                            int langCodeLen = payload[0] & 0077;
 
                            s += ("\n\nNdefMessage[" + i + "], NdefRecord[" + j + "]:\n\"" +
                                 new String(payload, langCodeLen + 1, payload.length - langCodeLen - 1,
                                 textEncoding) + "\"");
                         
                        }
                    }
                    if (str.indexOf("op")!=-1) {
                    	// UPDATE STATO TASK
                    	if (android.os.Build.VERSION.SDK_INT > 9) {
                    		StrictMode.ThreadPolicy policy = 
                    		        new StrictMode.ThreadPolicy.Builder().permitAll().build();
                    		StrictMode.setThreadPolicy(policy);
                    		}
    		        	URL url = null;
    		        	Map<String,Object> params = new LinkedHashMap<>();
    					try {        						
    						url = new URL("http://93.113.136.157/api/insertLog");
    					} catch (MalformedURLException e) {
    						// TODO Auto-generated catch block
    						e.printStackTrace();
    					}
    		        	params = new LinkedHashMap<>();
    		            params.put("dev", "Acm-e (GT-I9105P)");
    		            params.put("user", "Mauro Bianchi");
    		            params.put("operazione", "LOGIN");
    		            params.put("dettagli", "USER Mauro Bianchi");
    		            Log.e("LoginINFO", "operatore riconosciuto");
    		        	sendHTTPdata(params,url);
                    Intent intentOpen = new Intent(MainActivity.this,mainMenu.class); 		           		            		
            		Bundle b = new Bundle();
            		b.putString("key", str); //Your id
            		intentOpen.putExtras(b); //Put your id to your next Intent
            		startActivity(intentOpen);
            		finish();
                    } else {
                    	Toast.makeText(this, "WRONG TAG PLESE APPROACH AN OPERATOR TAG", Toast.LENGTH_SHORT).show();
                    	//showNotification();
                    }
                }
                
                 
            } catch (Exception e) {
                Log.e("TagDispatch", e.toString());
            }
        }		
    }
	
	@Override
    public void onResume() {
        super.onResume();
        mAdapter.enableForegroundDispatch(this, pendingIntent, null, null);
        
    }
 
    @Override
    public void onPause() {
        super.onPause();
 
        mAdapter.disableForegroundDispatch(this);
    }
	
 
	public void imageClick(View view) {  
		
		//overridePendingTransition(R.anim.abc_fade_in, R.anim.abc_fade_out);
	}  
	
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.main, menu);
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
	
	private void blink(){
	    final Handler handler = new Handler();
	    new Thread(new Runnable() {
	        @Override
	        public void run() {
	        int timeToBlink = 1000;    //in milliseconds
	        try{Thread.sleep(timeToBlink);}catch (Exception e) {}
	            handler.post(new Runnable() {
	                @Override
	                    public void run() {
	                    TextView txt = (TextView) findViewById(R.id.textView2);
	                    if(txt.getVisibility() == View.VISIBLE){
	                        txt.setVisibility(View.INVISIBLE);
	                    }else{
	                        txt.setVisibility(View.VISIBLE);
	                    }
	                    blink();
	                }
	                });
	            }
	        }).start();
	    }	

	
}
