package com.example.testa;

import java.util.Arrays;

import android.app.Activity;
import android.app.PendingIntent;
import android.content.Intent;
import android.content.IntentFilter;
import android.nfc.NdefMessage;
import android.nfc.NdefRecord;
import android.nfc.NfcAdapter;
import android.nfc.Tag;
import android.os.Bundle;
import android.os.Parcelable;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.Window;
import android.widget.Toast;

public class AcquireTAGActivity extends Activity {
	private PendingIntent pendingIntent;
    private IntentFilter[] mIntentFilters;
    private String[][] mNFCTechLists;
    private NfcAdapter mAdapter;
    public String inizio="";
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.activity_acquire_tag);
		Intent mIntent = getIntent();		
		inizio = mIntent.getExtras().getString("inizio");
		mAdapter = NfcAdapter.getDefaultAdapter(this);
		pendingIntent = PendingIntent.getActivity(this,0, new Intent(this, getClass()).addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP), 0);		
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.acquire_tag, menu);
		return true;
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
                    if (str.indexOf("ko")!=-1) {
                    	if (inizio=="START") {                    	
                    		Toast.makeText(this, "TAG NOT CORRESPONDING WITH THIS LOCATION", Toast.LENGTH_SHORT).show();
                    		//write_on_db("ANOMALIA TAG NON CORRISPONDENTE","AVVIO NON CONSENTITO :" + stabilimentoText ,"");
                    		
                    	} else {
                    		Toast.makeText(this, "TAG NOT CORRESPONDING WITH THIS LOCATION", Toast.LENGTH_SHORT).show();
                    		//write_on_db("ANOMALIA TAG NON CORRISPONDENTE","TERMINE ATTIVITA' NON CONSENTITO :" + stabilimentoText ,"");
                    		
                    	}
                    }
                    if (str.indexOf("ok")!=-1) {                       	
                    	switch (inizio) {
            			case "START":  
            				Toast.makeText(this, "TASK CORRECTLY STARTED", Toast.LENGTH_SHORT).show();            				
            	            break;
            	             
            	        case "INTERROMPI":
            	        	Toast.makeText(this, "TASK PAUSED", Toast.LENGTH_SHORT).show();

            	            break;            	            
            	        case "RESUME":  	    	  
            	        	Toast.makeText(this, "TASK RESUMED", Toast.LENGTH_SHORT).show();

            	            break;
            	            
            	        case "FINE":  	    	  
            	        	Toast.makeText(this, "TASK CORRECTLY DONE", Toast.LENGTH_SHORT).show();
            	            break;
            			}
                    	finish(); 		
                    } 
                    if (str.indexOf("op")!=-1) {
                    	Toast.makeText(this, "PLEASE APPROACH A RIGHT TAG", Toast.LENGTH_SHORT).show();                       		            		
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
