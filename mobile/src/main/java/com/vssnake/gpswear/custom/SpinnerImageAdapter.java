package com.vssnake.gpswear.custom;

import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.vssnake.gpswear.R;

import java.util.List;

/**
 * Created by vssnake on 07/01/2015.
 */
public class SpinnerImageAdapter extends ArrayAdapter<SpinnerImage>{

    public SpinnerImageAdapter(Activity activity,Context context, int resource,int itemListId, List<SpinnerImage> objects) {
        super(context, resource,itemListId, objects);

        mActivity = activity;
        mInflater = (LayoutInflater)activity.getSystemService(Context.LAYOUT_INFLATER_SERVICE);



    }



    Activity mActivity;
    LayoutInflater mInflater;


    @Override
    public View getDropDownView(int position, View convertView,ViewGroup parent) {
        return getCustomView(position, convertView, parent);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        return getCustomView(position, convertView, parent);
    }

    // This funtion called for each row ( Called data.size() times )
    public View getCustomView(int position, View convertView, ViewGroup parent) {

        /********** Inflate spinner_rows.xml file for each row ( Defined below ) ************/
        View row = mInflater.inflate(R.layout.custom_spinner, parent, false);

        /***** Get each Model object from Arraylist ********/
        SpinnerImage tempValues=getItem(position);


        TextView title        = (TextView)row.findViewById(R.id.spinner_title);
        ImageView background = (ImageView)row.findViewById(R.id.spinner_image);

        // Set values for spinner each row
        title.setText(tempValues.getText());
        background.setImageResource(tempValues.getImageUrl());

        return row;
    }
    public SpinnerImage getItem(int position){
        return super.getItem(position);
    }


    public int getPositionMapID(String id){
        for (int i = 0; i < getCount(); i++) {
            SpinnerImage image = getItem(i);
            if (image.getCode().equalsIgnoreCase(id)){
                return i;
            }
        }
        return -1;
    }
}
