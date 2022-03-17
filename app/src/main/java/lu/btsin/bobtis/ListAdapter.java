package lu.btsin.bobtis;

import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Filter;
import android.widget.Filterable;

import java.util.ArrayList;

public abstract class ListAdapter<T> extends BaseAdapter implements Filterable {

    private ArrayList<T> data = new ArrayList<T>();
    private ArrayList<T> datafiltered = new ArrayList<T>();

    public boolean is_data_set(){
        return data.isEmpty();
    }

    public ListAdapter() {

    }

    public ArrayList<T> getDatafiltered() {
        return datafiltered;
    }

    public void setData(ArrayList<T> list) {
        Log.i("initinit","setData");
        this.data = list;
        getFilter().filter("");
    }

    @Override
    public int getCount() {
        Log.i("initinit","getCount " + datafiltered.size());
        return datafiltered.size();
    }

    @Override
    public Object getItem(int i) {
        Log.i("initinit","getItem " + i);
        return datafiltered.get(i);
    }

    @Override
    public long getItemId(int i) {
        Log.i("initinit","getItemId " + i);
        return i;
    }

    @Override
    public abstract View getView(final int position, View convertView, ViewGroup parent);

    public abstract boolean filterEntry(T entry, CharSequence constraint);

    @Override
    public Filter getFilter() {
        return new Filter() {
            @Override
            protected FilterResults performFiltering(CharSequence constraint) {
                FilterResults results = new FilterResults();
                if (constraint == null || constraint.length() == 0) {
                    //no constraint given, just return all the data. (no search)
                    results.count = data.size();
                    results.values = data;
                } else {//do the search
                    ArrayList<T> resultsData = new ArrayList<T>();
                    //java.lang.String searchStr = constraint.toString().toUpperCase();
                    for (int i = 0; i < data.size(); i++) {
//                        String entry = data.get(i)[1];
//                        if (((java.lang.String)entry).toUpperCase().contains(((java.lang.String) constraint).toUpperCase())){
//                            resultsData.add(data.get(i));
//                        }
//                        Log.i("initinit","fintering "+ constraint);
                        if (filterEntry(data.get(i),constraint)){
                            resultsData.add(data.get(i));
//                            Log.i("initinit","successful "+ constraint);
                        }
                    }
                    results.count = resultsData.size();
                    results.values = resultsData;
                    Log.i("initinit","end "+ resultsData.size());
                }
                return results;
            }

            @Override
            protected void publishResults(CharSequence constraint, FilterResults results) {
                datafiltered = (ArrayList<T>) results.values;
                Log.i("initinit","publishResults "+ results.values.toString());
                notifyDataSetChanged();
                Log.i("initinit","publishResultc "+ results.values.toString());
            }
        };
    }
}