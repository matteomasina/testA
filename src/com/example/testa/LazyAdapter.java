package com.example.testa;

import java.util.ArrayList;
import java.util.HashMap;

import android.app.Activity;
import android.content.Context;
import android.text.Html;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

public class LazyAdapter extends BaseAdapter {
    
    private Activity activity;
    private ArrayList<HashMap<String, String>> data;
    private static LayoutInflater inflater=null;
    public ImageLoader imageLoader; 
    
    public LazyAdapter(Activity a, ArrayList<HashMap<String, String>> d) {
        activity = a;
        data=d;
        inflater = (LayoutInflater)activity.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        imageLoader=new ImageLoader(activity.getApplicationContext());
    }

    public int getCount() {
        return data.size();
    }
//
    public Object getItem(int position) {
        return position;
    }
//
    public long getItemId(int position) {
        return position;
    }
    
    public View getView(int position, View convertView, ViewGroup parent) {
        View vi=convertView;
        if(convertView==null)
            vi = inflater.inflate(R.layout.list_row, null);

        TextView title = (TextView)vi.findViewById(R.id.stabilimentoMyTask); // title
        TextView artist = (TextView)vi.findViewById(R.id.artist); // artist name
        TextView duration = (TextView)vi.findViewById(R.id.startTimeMyTask); // duration
        ImageView thumb_image=(ImageView)vi.findViewById(R.id.iconaMyTask); // thumb image
        
        HashMap<String, String> song = new HashMap<String, String>();
        song = data.get(position);
        
        switch(song.get(CustomizedListView.KEY_THUMB_URL)) {
        case "P0":
        	thumb_image.setImageResource(R.drawable.p0);
        	break;
        case "P1":
        	thumb_image.setImageResource(R.drawable.p1);
        	break;
        case "P2":
        	thumb_image.setImageResource(R.drawable.p2);
        	break;
        case "P3":
        	thumb_image.setImageResource(R.drawable.p3);
        	break;
        case "P4":
        	thumb_image.setImageResource(R.drawable.p4);
        	break;
        case "P5":
        	thumb_image.setImageResource(R.drawable.p5);
        	break;	                	
         
      }	     
        
        // Setting all values in listview
        String sourceString = "<b>STATO : " + song.get(CustomizedListView.KEY_STAO) + "</b> "; 
        
        title.setText(song.get(CustomizedListView.KEY_TITLE));
        artist.setText(Html.fromHtml(sourceString) + "\n" + song.get(CustomizedListView.KEY_ARTIST));
        duration.setText(song.get(CustomizedListView.KEY_DURATION));
        //thumb_image.setImageResource(R.drawable.p4);
        //imageLoader.DisplayImage(song.get(CustomizedListView.KEY_THUMB_URL), thumb_image);
        return vi;
    }
    
    
}