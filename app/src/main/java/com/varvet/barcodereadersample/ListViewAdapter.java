package com.varvet.barcodereadersample;

import android.app.Activity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import java.util.ArrayList;

public class ListViewAdapter extends BaseAdapter
{
    Activity context;
    ArrayList<String> title;
    ArrayList<String> description;

    public ListViewAdapter(Activity context, ArrayList<String> title, ArrayList<String> description) {
        super();
        this.context = context;
        this.title = title;
        this.description = description;
    }
   //new added
    public boolean add(String title,String description){
        this.title.add(title);
        this.description.add(description);
        return true;
    }
    //new added
    public boolean clear()
    {
        title.clear();
        description.clear();
        return true;
    }

    public int getCount() {
        // TODO Auto-generated method stub
        return title.size();
    }

    public Object getItem(int position) {
        // TODO Auto-generated method stub
        return null;
    }

    public long getItemId(int position) {
        // TODO Auto-generated method stub
        return 0;
    }

    private class ViewHolder {
        TextView txtViewTitle;
        TextView txtViewDescription;
    }

    public View getView(int position, View convertView, ViewGroup parent)
    {
        // TODO Auto-generated method stub
        ViewHolder holder;
        LayoutInflater inflater =  context.getLayoutInflater();

        if (convertView == null)
        {
            convertView = inflater.inflate(R.layout.row_content, null);
            holder = new ViewHolder();
            holder.txtViewTitle = (TextView) convertView.findViewById(R.id.tv1);
            holder.txtViewDescription = (TextView) convertView.findViewById(R.id.tv2);
            convertView.setTag(holder);
        }
        else
        {
            holder = (ViewHolder) convertView.getTag();
        }

        holder.txtViewTitle.setText(title.get(position));
        holder.txtViewDescription.setText(description.get(position));

        return convertView;
    }

}