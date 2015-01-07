package com.vssnake.library.customview;

import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;

import com.vssnake.test.customview.R;

import java.util.List;

/**
 * Created by vssnake on 07/01/2015.
 */
public class SpinnerImageAdapter extends ArrayAdapter<SpinnerImage>{

    public SpinnerImageAdapter(Activity activity,Context context, int resource, List<SpinnerImage> objects) {
        super(context, resource, objects);

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
        View row = inflater.inflate(R.layout.spinner_rows, parent, false);

        /***** Get each Model object from Arraylist ********/
        tempValues = null;
        tempValues = (SpinnerModel) data.get(position);

        TextView label        = (TextView)row.findViewById(R.id.company);
        TextView sub          = (TextView)row.findViewById(R.id.sub);
        ImageView companyLogo = (ImageView)row.findViewById(R.id.image);

        if(position==0){

            // Default selected Spinner item
            label.setText("Please select company");
            sub.setText("");
        }
        else
        {
            // Set values for spinner each row
            label.setText(tempValues.getCompanyName());
            sub.setText(tempValues.getUrl());
            companyLogo.setImageResource(res.getIdentifier
                    ("com.androidexample.customspinner:drawable/"
                            + tempValues.getImage(),null,null));

        }

        return row;
    }
}
