package com.example.testa;

import java.util.ArrayList;
import java.util.HashMap;

import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.TextView;

public class CheckAdapter extends BaseAdapter {
    
    private Activity activity;
    private ArrayList<HashMap<String, String>> data;
    private static LayoutInflater inflater=null;
    public ImageLoader imageLoader; 
    
    public CheckAdapter(Activity a, ArrayList<HashMap<String, String>> d) {
        activity = a;
        data=d;
        inflater = (LayoutInflater)activity.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        imageLoader=new ImageLoader(activity.getApplicationContext());
    }

    public int getCount() {
        return data.size();
    }

    public Object getItem(int position) {
        return position;
    }

    public long getItemId(int position) {
        return position;
    }
    
    public View getView(int position, View convertView, ViewGroup parent) {
        View vi=convertView;
        if(convertView==null)
            vi = inflater.inflate(R.layout.list_checkrow, null);

        TextView title = (TextView)vi.findViewById(R.id.stabilimentoMyTask); // title
        TextView artist = (TextView)vi.findViewById(R.id.artist); // artist name
        TextView duration = (TextView)vi.findViewById(R.id.startTimeMyTask); // duration
        ImageView thumb_image=(ImageView)vi.findViewById(R.id.iconaMyTask); // thumb image
        CheckBox chk=(CheckBox)vi.findViewById(R.id.checkBoxField);
        
        title.setVisibility(View.INVISIBLE);
        artist.setVisibility(View.INVISIBLE);
        duration.setVisibility(View.INVISIBLE);
        thumb_image.setVisibility(View.INVISIBLE);
        
        HashMap<String, String> song = new HashMap<String, String>();
        song = data.get(position);
        
        // Setting all values in listview
        title.setText(song.get(CheckCustomizedListView.KEY_TITLE));
        artist.setText(song.get(CheckCustomizedListView.KEY_ARTIST));
        duration.setText(song.get(CheckCustomizedListView.KEY_DURATION));
        chk.setText(song.get(CheckCustomizedListView.KEY_CHECKBOX));
        imageLoader.DisplayImage(song.get(CheckCustomizedListView.KEY_THUMB_URL), thumb_image);
        return vi;
    }
}