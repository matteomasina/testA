package com.example.testa;

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
import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;

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

import android.app.ActionBar;
import android.app.Activity;
import android.app.PendingIntent;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.nfc.NdefMessage;
import android.nfc.NdefRecord;
import android.nfc.NfcAdapter;
import android.nfc.Tag;
import android.os.Bundle;
import android.os.Environment;
import android.os.Parcelable;
import android.os.StrictMode;
import android.provider.MediaStore;
import android.text.Html;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.VideoView;

public class MyTaskDetailActivity extends Activity {
	private static final String MIME_TEXT_PLAIN = null;
	
    
	String nomeFile;
	String id;
	String stabilimentoText;
	  private static final int CAMERA_CAPTURE_IMAGE_REQUEST_CODE = 100;
	  private static final int CAMERA_CAPTURE_VIDEO_REQUEST_CODE = 200;
	  private static final int CHECKLISTCODE = 300;
	  public static final int MEDIA_TYPE_IMAGE = 1;
	  public static final int MEDIA_TYPE_VIDEO = 2;
	  private ImageView imgPreview;
	  private VideoView videoPreview;
	  public String pathFile="";
	  // directory name to store captured images and videos
	  private static final String IMAGE_DIRECTORY_NAME = "Hello Camera";

	  private Uri fileUri; // file url to store image/video	
	  File image;
	  Button btnStart;
	  Button button;
	  Button btnSnapshot;
	  boolean inizio;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		ColorDrawable colorDrawable = new ColorDrawable();
		colorDrawable.setColor(0xffFFFFFF);
		ActionBar bar = getActionBar();
		bar.setIcon(R.drawable.nfc40x40);
		bar.setBackgroundDrawable(colorDrawable);
		bar.setTitle(Html.fromHtml("<font color='#000000'>DETTAGLI TASK</font>"));
		setContentView(R.layout.activity_mydetailtask);
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
		  
		  stabilimentoText=hashMap.get("citta");
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
		  
		button = (Button) findViewById(R.id.btnOpenCheckList);
		btnSnapshot = (Button) findViewById(R.id.btnGetSnapshot);
	    btnStart = (Button) findViewById(R.id.btnStartTask);
		
		switch(hashMap.get("stato")) {
        case "CREATO":
        	//btn start
        	btnStart.setText("AVVIA LAVORAZIONE");
        	btnStart.setEnabled(false);        	
        	//btn check list        	
        	//btn snap
        	btnSnapshot.setText("TERMINA LAVORAZIONE");
        	btnSnapshot.setEnabled(false);  
        	break;
        case "ASSEGNATO":
        	//btn start
        	btnStart.setText("AVVIA LAVORAZIONE");
        	btnStart.setEnabled(false);        	
        	//btn check list        	
        	//btn snap
        	btnSnapshot.setText("TERMINA LAVORAZIONE");
        	btnSnapshot.setEnabled(false);          	
        	break;
        case "CHECKLIST SICUREZZA":
        	//btn start
        	btnStart.setText("AVVIA LAVORAZIONE");
        	btnStart.setEnabled(true);        	
        	//btn check list  
        	button.setText("CHECKLIST APPROVATA");
        	button.setVisibility(View.GONE);
        	//btn snap        	
        	btnSnapshot.setEnabled(true);          	
        	break;     
        case "IN LAVORAZIONE":  
        	//btn start
        	btnStart.setText("TERMINA LAVORAZIONE");
        	btnStart.setEnabled(true);        	
        	//btn check list  
        	//button.setText("CHECKLIST APPROVATA");
        	//btn snap
        	btnSnapshot.setEnabled(true);         	
        	break;
        case "INTERROTTO":        	
        	break; 
        case "ESEGUITO":   
        	btnStart.setText("TASK CHIUSO");
        	btnStart.setEnabled(false); 
        	btnSnapshot.setEnabled(false);  
        	button.setEnabled(false);  
        	break;
		}
		
		
		btnStart.setOnClickListener(new View.OnClickListener(){
	        @Override
	        public void onClick(View v) {
	        	String buttonText = btnStart.getText().toString();
	        	switch(buttonText) {
		            case "AVVIA LAVORAZIONE":
		            	//btnStart.setText("TERMINA LAVORAZIONE");
		            	Toast.makeText(MyTaskDetailActivity.this, "AVVICINARE IL TAG IDENTIFICATIVO PER AVVIARE LA MANUTENZIONE", Toast.LENGTH_SHORT).show();
		            	//write_on_db("IN LAVORAZIONE","TASK AVVIATO:" + hashMap.get("citta"),"IN LAVORAZIONE");		            	
		            	inizio=true;
		            	break;
//		            case "INTERROMPI LAVORAZIONE":
//		            	break;
//		            case "RIPRENDI LAVORAZIONE":
//		            	break;     
		            case "TERMINA LAVORAZIONE":
		            	//btnStart.setText("TERMINA LAVORAZIONE");
		            	Toast.makeText(MyTaskDetailActivity.this, "AVVICINARE IL TAG IDENTIFICATIVO PER TERMINARE LA MANUTENZIONE", Toast.LENGTH_SHORT).show();
		            	inizio=false;
		            	//write_on_db("ESEGUITO","TASK CHIUSO:" + hashMap.get("citta"),"ESEGUITO");
		            	break;
	          }
	          //throw new IllegalArgumentException(buttonText);
	        	
	        }
	    });
		
		btnSnapshot.setOnClickListener(new View.OnClickListener(){
	        @Override
	        public void onClick(View v) {
	        	captureImage();
	        }
	    });

	    button.setOnClickListener(new View.OnClickListener(){
	        @Override
	        public void onClick(View v) {

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
	            params.put("dettagli", "APERTA CHECKLIST " + hashMap.get("citta"));
	            Date d = new Date();
	            params.put("timestamp", d.toString());
	        	sendHTTPdata(params,url);
	        	Intent openPage1 = new Intent(MyTaskDetailActivity.this,ChecklistWorkActivity.class);
	        	openPage1.putExtra("id", id);
	        	openPage1.putExtra("stabilimento", stabilimentoText);	        		        	
	        	startActivityForResult(openPage1, CHECKLISTCODE);    	  			        	
	        }
	    });
	
	    
		
	}
	

	

//	@Override
//    public void onNewIntent(Intent intent) {
//        String action = intent.getAction();
//        Tag tag = intent.getParcelableExtra(NfcAdapter.EXTRA_TAG);
// 
//        String s = action + "\n\n" + tag.toString();
// 
//        // parse through all NDEF messages and their records and pick text type only
//        Parcelable[] data = intent.getParcelableArrayExtra(NfcAdapter.EXTRA_NDEF_MESSAGES);
//        if (data != null) {
//            try {
//                for (int i = 0; i < data.length; i++) {
//                    NdefRecord [] recs = ((NdefMessage)data[i]).getRecords();
//                    byte[] k = recs [0].getPayload();
//                    String str = new String(k);
//                    Log.e("TagINFO", str);
//                    
//                    for (int j = 0; j < recs.length; j++) {
//                        if (recs[j].getTnf() == NdefRecord.TNF_WELL_KNOWN &&
//                            Arrays.equals(recs[j].getType(), NdefRecord.RTD_TEXT)) {
//                            byte[] payload = recs[j].getPayload();
//                            String textEncoding = ((payload[0] & 0200) == 0) ? "UTF-8" : "UTF-16";
//                            int langCodeLen = payload[0] & 0077;
// 
//                            s += ("\n\nNdefMessage[" + i + "], NdefRecord[" + j + "]:\n\"" +
//                                 new String(payload, langCodeLen + 1, payload.length - langCodeLen - 1,
//                                 textEncoding) + "\"");
//                         
//                        }
//                    }
//                    if (str.indexOf("ko")!=-1) {
//                    	if (inizio) {                    	
//                    		Toast.makeText(this, "TAG NON CORRISPONDENTE A QUESTA STAZIONE", Toast.LENGTH_SHORT).show();
//                    		write_on_db("ANOMALIA TAG NON CORRISPONDENTE","AVVIO NON CONSENTITO :" + stabilimentoText ,"");
//                    		
//                    	} else {
//                    		Toast.makeText(this, "TAG NON CORRISPONDENTE A QUESTA STAZIONE", Toast.LENGTH_SHORT).show();
//                    		write_on_db("ANOMALIA TAG NON CORRISPONDENTE","TERMINE ATTIVITA' NON CONSENTITO :" + stabilimentoText ,"");
//                    		
//                    	}
//                    }
//                    if (str.indexOf("ok")!=-1) {   
//                    	if (inizio) { 
//                    		Toast.makeText(this, "TASK CORRETTAMENTE AVVIATO", Toast.LENGTH_SHORT).show();
//	    					write_on_db("IN LAVORAZIONE","TASK AVVIATO:" + stabilimentoText ,"IN LAVORAZIONE"); 
//	    					btnStart.setText("TERMINA LAVORAZIONE");
//	    					
//                    	} else {
//                    		Toast.makeText(this, "TASK CORRETTAMENTE CHIUSO", Toast.LENGTH_SHORT).show();
//                    		write_on_db("ESEGUITO","TASK CHIUSO :" + stabilimentoText ,"ESEGUITO");
//                    	
//                    	}
//    					//finish();            		
//                    } 
//                    if (str.indexOf("op")!=-1) {
//                    	Toast.makeText(this, "AVVICINARE TAG DISPOSITIVO NON TAG OPERATORE", Toast.LENGTH_SHORT).show();                       		            		
//                    }  
//                    
//                }                                 
//            } catch (Exception e) {
//                Log.e("TagDispatch", e.toString());
//            }
//        }		
//    }	
	
	public void write_on_db(String operazione,String dettagli,String stato) {
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
	
	private void captureImage() {
		 Intent imageIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
	     File imagesFolder = new File(Environment.getExternalStorageDirectory(), "NFC-TRACKER");     
	     imagesFolder.mkdirs(); // 
	     Date now = new Date();
	     
	     Date date = new Date() ;
	     SimpleDateFormat dateFormat = new SimpleDateFormat("MMddHHmmss") ;
	     //File file = new File(dateFormat.format(date) + ".tsv") ;
	     
	     
	     nomeFile=id.toString();
	     nomeFile= nomeFile + "id" + stabilimentoText + "-" + dateFormat.format(date) + ".jpg";
	     image = new File(imagesFolder,   nomeFile);
	     
	     
	     Uri uriSavedImage = Uri.fromFile(image);
	     imageIntent.putExtra(MediaStore.EXTRA_OUTPUT, uriSavedImage);
	     pathFile=image.toString();
	  
	     // start the image capture Intent
	     startActivityForResult(imageIntent, CAMERA_CAPTURE_IMAGE_REQUEST_CODE);
	     
	 }  
	
	@Override
	 protected void onActivityResult(int requestCode, int resultCode, Intent data) {
	     // if the result is capturing Image
		//Uri u = intent.getData();
		
		if (requestCode == CHECKLISTCODE) {
			Toast.makeText(getApplicationContext(), "CHECKLIST SICUREZZA APPROVATA.SI PUO' AVVIARE LA LAVORAZIONE", Toast.LENGTH_SHORT).show();
			button.setVisibility(View.GONE);
			btnStart.setEnabled(true); 
        	btnSnapshot.setEnabled(true);
        	btnStart.setText("AVVIA LAVORAZIONE");
        	btnSnapshot.setText("ACQUISISCI IMMAGINE");
		}
	     if (requestCode == CAMERA_CAPTURE_IMAGE_REQUEST_CODE) {
	         if (resultCode == RESULT_OK) {
	             // successfully captured the image
	             // display it in image view
	             //(previewCapturedImage();

	        	 Toast.makeText(getApplicationContext(),
	                     "IMMAGINE ACQUISITA", Toast.LENGTH_SHORT)
	                     .show();
//	        	 try {
//	        		 try {
////	        			 
////						sendFile(pathFile);
//					} catch (IOException e) {
//						// TODO Auto-generated catch block
//						e.printStackTrace();
//					}
//				} catch (ParseException e) {
//					// TODO Auto-generated catch block
//					e.printStackTrace();
//				}
	         } else if (resultCode == RESULT_CANCELED) {
	             // user cancelled Image capture
	             Toast.makeText(getApplicationContext(),
	                     "User cancelled image capture", Toast.LENGTH_SHORT)
	                     .show();
	         } else {
	             // failed to capture image
	             Toast.makeText(getApplicationContext(),
	                     "Sorry! Failed to capture image", Toast.LENGTH_SHORT)
	                     .show();
	         }
	     }
	 } 
	
	 /*
	  * Display image from a path to ImageView
	  */
	 private void previewCapturedImage() {
	     try {
	         // hide video preview
	         videoPreview.setVisibility(View.GONE);

	         imgPreview.setVisibility(View.VISIBLE);

	         // bimatp factory
	         BitmapFactory.Options options = new BitmapFactory.Options();

	         // downsizing image as it throws OutOfMemory Exception for larger
	         // images
	         options.inSampleSize = 8;

	         final Bitmap bitmap = BitmapFactory.decodeFile(fileUri.getPath(),
	                 options);

	         imgPreview.setImageBitmap(bitmap);
	     } catch (NullPointerException e) {
	         e.printStackTrace();
	     }
	 }
	 
	 private void recordVideo() {
	     Intent intent = new Intent(MediaStore.ACTION_VIDEO_CAPTURE);

	     
	     File imagesFolder = new File(Environment.getExternalStorageDirectory(), "NFC-TRACKER");
	     imagesFolder.mkdirs(); // 
	     Date now = new Date();
	     File image = new File(imagesFolder, now + ".avi");
	     Uri uriSavedImage = Uri.fromFile(image);

	     // set video quality
	     intent.putExtra(MediaStore.EXTRA_VIDEO_QUALITY, 1);

	     intent.putExtra(MediaStore.EXTRA_OUTPUT, uriSavedImage); // set the image file
	     pathFile=image.toString();                                                         // name

	     // start the video capture Intent
	     startActivityForResult(intent, CAMERA_CAPTURE_VIDEO_REQUEST_CODE);
	 }
	 	 
	 
	 private void sendFile(String fileName) throws ParseException, IOException  {
		 
		 	 //fileName="/storage/emulated/0/NFC-TRACKER/" + nomeFile;
		 	
		 	
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
				 write_on_db("IMMAGINE ACQUISITA", "task snapshot", "");
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
		getMenuInflater().inflate(R.menu.my_task_detail, menu);
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
