package myprivate.githubrepo.Adapters;

import android.content.Context;
import android.graphics.Color;
import android.support.annotation.Nullable;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import java.util.ArrayList;

import myprivate.githubrepo.Model.License;
import myprivate.githubrepo.R;

/**
 * Created by mahitej on 22-12-2017.
 */

public class LicenceAdapter extends ArrayAdapter<License> {
    private Context context;
    private ArrayList<License> licenses;

    public LicenceAdapter(Context context, int textViewResourceId,ArrayList<License> licenses) {
        super(context, textViewResourceId, licenses);
        this.context = context;
        this.licenses = licenses;
        this.licenses.add(0,new License("",context.getString(R.string.prompt_license)));
    }

    @Override
    public int getCount(){
        return licenses.size();
    }

    @Override
    public License getItem(int position){
        return licenses.get(position);
    }

    @Override
    public long getItemId(int position){
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        TextView label = new TextView(context);
        label.setTextColor(Color.BLACK);
        label.setText(licenses.get(position).getName());

        return label;
    }

    @Override
    public View getDropDownView(int position, View convertView,
                                ViewGroup parent) {
        TextView label = new TextView(context);
        label.setTextColor(Color.BLACK);
        label.setText(licenses.get(position).getName());

        return label;
    }
}
