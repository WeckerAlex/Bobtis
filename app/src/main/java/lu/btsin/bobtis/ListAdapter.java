package lu.btsin.bobtis;

import android.content.Context;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Filter;
import android.widget.Filterable;

import java.util.ArrayList;

public abstract class ListAdapter<String> extends BaseAdapter implements Filterable {

    private ArrayList<String[]> data = new ArrayList<String[]>();
    private ArrayList<String[]> datafiltered = new ArrayList<String[]>();

    public boolean is_data_set(){
        return data.isEmpty();
    }

    public ListAdapter() {

    }

    public ArrayList<String[]> getDatafiltered() {
        return datafiltered;
    }

    public void setData(ArrayList<String[]> list) {
        this.data = list;
        getFilter().filter("");
    }

    @Override
    public int getCount() {
        return datafiltered.size();
    }

    @Override
    public Object getItem(int i) {
        return datafiltered.get(i);
    }

    @Override
    public long getItemId(int i) {
        return i;
    }

    @Override
    public abstract View getView(final int position, View convertView, ViewGroup parent);/* {
        TextView tw = new TextView(getContext());
        LinearLayout.LayoutParams layoutParamsText = new TableRow.LayoutParams(TableRow.LayoutParams.MATCH_PARENT, TableRow.LayoutParams.WRAP_CONTENT);
        layoutParamsText.setMargins(1,1,1,1);
        layoutParamsText.weight = 1;

        tw.setPadding(1,0,1,0);
        tw.setLayoutParams(layoutParamsText);
        tw.setText(((String[])datafiltered.get(position))[1].toString());

        LinearLayout ll = new LinearLayout(context);
        ll.setOnClickListener(view -> display(((String[])datafiltered.get(position))[0].toString()));
        ll.setGravity(Gravity.CLIP_HORIZONTAL);
        ll.setBackgroundResource(R.drawable.coursebackground);
        ((GradientDrawable) ll.getBackground()).setColor(Color.parseColor("#FFFF99"));
        TableRow.LayoutParams layoutParams = new TableRow.LayoutParams(TableRow.LayoutParams.MATCH_PARENT, TableRow.LayoutParams.WRAP_CONTENT);
        ll.setPadding(10,0,10,0);
        ll.setLayoutParams(layoutParams);

        ll.setOrientation(LinearLayout.HORIZONTAL);
        ll.addView(tw);
        Button button = new Button(context);
        button.setText("Add to favorites");
        ll.addView(button);
        return ll;
    }*/

    @Override
    public Filter getFilter() {
        return new Filter() {
            @Override
            protected FilterResults performFiltering(CharSequence constraint) {
                FilterResults results = new FilterResults();
                Log.i("Filter", (java.lang.String) constraint);
                if (constraint == null || constraint.length() == 0) {
                    //no constraint given, just return all the data. (no search)
                    results.count = data.size();
                    results.values = data;
                } else {//do the search
                    ArrayList<String[]> resultsData = new ArrayList<String[]>();
                    java.lang.String searchStr = constraint.toString().toUpperCase();
                    for (int i = 0; i < data.size(); i++) {
                        String entry = data.get(i)[1];
                        if (((java.lang.String)entry).toUpperCase().contains(((java.lang.String) constraint).toUpperCase())){
                            resultsData.add(data.get(i));
                        }
                    }
                    results.count = resultsData.size();
                    results.values = resultsData;
                }
                return results;
            }

            @Override
            protected void publishResults(CharSequence constraint, FilterResults results) {
                datafiltered = (ArrayList<String[]>) results.values;
                notifyDataSetChanged();
            }
        };
    }
}