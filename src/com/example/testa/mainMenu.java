package com.example.testa;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.ProtocolException;
import java.net.URL;
import java.net.URLConnection;
import java.net.URLEncoder;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.LinkedHashMap;
import java.util.Map;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.HttpVersion;
import org.apache.http.ParseException;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.mime.HttpMultipartMode;
import org.apache.http.entity.mime.MultipartEntity;
import org.apache.http.entity.mime.content.ContentBody;
import org.apache.http.entity.mime.content.FileBody;
//import org.apache.http.entity.mime.MultipartEntity;
//import org.apache.http.entity.mime.content.ContentBody;
//import org.apache.http.entity.mime.content.FileBody;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.params.BasicHttpParams;
import org.apache.http.params.CoreProtocolPNames;
import org.apache.http.params.HttpConnectionParams;
import org.apache.http.params.HttpParams;
import org.apache.http.util.EntityUtils;

import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.os.StrictMode;
import android.provider.MediaStore;
import android.app.ActionBar;
import android.app.Activity;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.ColorDrawable;
import android.text.Html;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.Toast;
import android.widget.VideoView;
import android.view.View.OnTouchListener;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;


public class mainMenu extends Activity {
  GridView gridView;
  ArrayList<Item> gridArray = new ArrayList<Item>();
  CustomGridViewAdapter customGridAdapter;
  ArrayList<File> listSnapshot,results;
  
  
  private String upLoadServerUri = null;
  private int serverResponseCode = 0;
  



 @Override
 protected void onCreate(Bundle savedInstanceState) {
  super.onCreate(savedInstanceState);
  if (android.os.Build.VERSION.SDK_INT > 9) {
      StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
      StrictMode.setThreadPolicy(policy);
    }
  getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,  WindowManager.LayoutParams.FLAG_FULLSCREEN);
  setContentView(R.layout.activiy_mainmenu);
  Bundle b = getIntent().getExtras();
  String value = b.getString("key");
  //Toast.makeText(this, value.trim(), Toast.LENGTH_SHORT).show();
  //
  upLoadServerUri = "http://93.113.136.157/nfc/api/uploadNFC.php";
  
  ColorDrawable colorDrawable = new ColorDrawable();
  colorDrawable.setColor(0xffFFFFFF);
  ActionBar bar = getActionBar();
  bar.setIcon(R.drawable.nfc40x40);
  bar.setBackgroundDrawable(colorDrawable);
  bar.setTitle(Html.fromHtml("<font color='#000000'>NFC MAINTENANCE TRACKER</font>")); 
  
  
  
  
  //set grid view item
  Bitmap userprofile = BitmapFactory.decodeResource(this.getResources(), R.drawable.userprofile128);
  Bitmap taskaperti = BitmapFactory.decodeResource(this.getResources(), R.drawable.tasksaperti128);
  Bitmap myTasks = BitmapFactory.decodeResource(this.getResources(), R.drawable.mytasks128);
  Bitmap systemconfig= BitmapFactory.decodeResource(this.getResources(), R.drawable.systemconfig);
  Bitmap endapplication = BitmapFactory.decodeResource(this.getResources(), R.drawable.endapp128);

  gridArray.add(new Item(myTasks,"MY TASKS"));
  gridArray.add(new Item(taskaperti,"TASKS"));
//  gridArray.add(new Item(systemconfig,"CONFIGURAZIONE"));
//  gridArray.add(new Item(userprofile,"PROFILO UTENTE"));
  //gridArray.add(new Item(null,""));
  //gridArray.add(new Item(null,""));
  //gridArray.add(new Item(null,""));
  //gridArray.add(new Item(null,""));
//  gridArray.add(new Item(endapplication,"ESCI"));
  
  gridView = (GridView) findViewById(R.id.gridView1);
  customGridAdapter = new CustomGridViewAdapter(this, R.layout.row_grid, gridArray);
  gridView.setAdapter(customGridAdapter);
  
  
  
  
  gridView.setOnItemClickListener(new OnItemClickListener() {
      @Override
      public void onItemClick(AdapterView<?> parent, View v,
              int position, long id) {
    	 

    	  		Log.w("NFC app", String.valueOf(position));
    	  		Log.w("NFC app", String.valueOf(id));
    	  		//Intent openPage1 = new Intent(mainMenu.this,SettingsActivity.class); 
    	  		//captureImage();    	  		
    	  		Intent openPage1 = new Intent(mainMenu.this,Task_list.class);    	  		    	  	
    	  		openPage1.putExtra("opType", position);    	  		    	  		
    	  		startActivity(openPage1);


    	  		//recordVideo();
    	  		       
      }
  });
  
   
 }
 
 /**
  * Checking device has camera hardware or not
  * */
 private boolean isDeviceSupportCamera() {
     if (getApplicationContext().getPackageManager().hasSystemFeature(
             PackageManager.FEATURE_CAMERA)) {
         // this device has a camera
         return true;
     } else {
         // no camera on this device
         return false;
     }
 }  
 
 
 
	
	


 



 
 
 
 
 
 
}