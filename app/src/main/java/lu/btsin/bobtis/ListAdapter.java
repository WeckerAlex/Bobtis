package lu.btsin.bobtis;

import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Filter;
import android.widget.Filterable;

import java.util.ArrayList;

/**
 * A BaseAdapter with abstract getView and filter method
 * Can be used to display any Object in a List and filter this List
 * @param <T> An object to be displayed in a list
 */
public abstract class ListAdapter<T> extends BaseAdapter implements Filterable {

    private ArrayList<T> data = new ArrayList<>();
    private ArrayList<T> dataFiltered = new ArrayList<>();

    /**
     * Checks if data is set
     * @return is the data empty
     */
    public boolean is_data_set(){
        return data.isEmpty();
    }

    /**
     * Constructor
     */
    public ListAdapter() {

    }

    /**
     * Returns the data after it has been filtered
     * @return an Arraylist of the used Object
     */
    public ArrayList<T> getDataFiltered() {
        return dataFiltered;
    }

    /**
     * sets the data to be displayed
     * @param list an Arraylist of objects to display
     */
    public void setData(ArrayList<T> list) {
        this.data = list;
        getFilter().filter("");
    }

    @Override
    public int getCount() {
        return dataFiltered.size();
    }

    @Override
    public Object getItem(int i) {
        return dataFiltered.get(i);
    }

    @Override
    public long getItemId(int i) {
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
                    ArrayList<T> resultsData = new ArrayList<>();
                    for (int i = 0; i < data.size(); i++) {
                        if (filterEntry(data.get(i),constraint)){
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
                dataFiltered = (ArrayList<T>) results.values;
                notifyDataSetChanged();
            }
        };
    }
}