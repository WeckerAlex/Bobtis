package lu.btsin.bobtis;

import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.GradientDrawable;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TableRow;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link Search_Fragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class Search_Fragment extends Fragment implements AsyncResponse  {

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";
    ListView listView;
    EditText searchedittext;
    private boolean logging_in = true;
    private static ArrayList<String[]> availableClasses;
    private static ArrayList<String[]> availableRooms;
    private static ArrayList<String[]> availableTeachers;
    private static int selectedCategory = R.id.classbutton;
    private static MyAdapter adapter;


    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    public Search_Fragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment Search_Fragment.
     */
    // TODO: Rename and change types and number of parameters
    public static Search_Fragment newInstance(String param1, String param2) {
        Search_Fragment fragment = new Search_Fragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_search, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        initData();
        searchedittext = (EditText) getView().findViewById(R.id.searchEditText);
        listView = (ListView) getView().findViewById(R.id.resultsView);
        Button classbutton = (Button) getView().findViewById(R.id.classbutton);
        Button roombutton = (Button) getView().findViewById(R.id.roombutton);
        Button teacherbutton = (Button) getView().findViewById(R.id.teacherbutton);
        classbutton.setOnClickListener(v -> buttonclick(availableClasses,v));
        roombutton.setOnClickListener(v -> buttonclick(availableRooms,v));
        teacherbutton.setOnClickListener(v -> buttonclick(availableTeachers,v));
        if (adapter == null){
            adapter = new MyAdapter(getContext());
        }
        listView.setAdapter(adapter);
        listView.setTextFilterEnabled(true);
        searchedittext.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                filterResults(charSequence);
            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        });
        searchedittext.setOnEditorActionListener((v, actionId, event) -> {
            if (actionId == EditorInfo.IME_ACTION_SEARCH) {
                dismissKeybord();
                return true;
            }
            return false;
        });
        adapter.getFilter().filter("");
    }

    private void buttonclick(ArrayList<String[]> data,View sender){
        adapter.setData(data);
        searchedittext.setText("");
        selectedCategory = sender.getId();

    }

    private void display(String data){
        Log.i("Segue", "display");
        switch (selectedCategory){
            case R.id.classbutton:{
                ((MainActivity)getActivity()).displayClass(data);
                break;
            }
            case R.id.roombutton:{
                ((MainActivity)getActivity()).displayRoom(data);
                break;
            }
            case R.id.teacherbutton:{
                ((MainActivity)getActivity()).displayTeacher(data);
                break;
            }
        }
    }

    private void filterResults(CharSequence text) {
        adapter.getFilter().filter(text);
    }

    private void dismissKeybord() {
        searchedittext.clearFocus();
        InputMethodManager imm = (InputMethodManager) getActivity().getSystemService(Context.INPUT_METHOD_SERVICE);
        imm.hideSoftInputFromWindow(searchedittext.getWindowToken(), 0);
    }

    private void initData(){
        if (availableTeachers == null || availableTeachers.isEmpty()){
            getTeachers("2021-2022");
        }
        if (availableClasses == null || availableClasses.isEmpty()){
            getClasses("2021-2022");
        }
        if (availableRooms == null || availableRooms.isEmpty()){
            getRooms("2021-2022");
        }
    }

    private void getTeachers(String schoolyear){
        API task =  new API();
        task.delegate = this;
        task.execute("teachers",schoolyear);
    }

    private void getClasses(String schoolyear){
        API task =  new API();
        task.delegate = this;
        task.execute("classes",schoolyear);
    }

    private void getRooms(String schoolyear){
        API task =  new API();
        task.delegate = this;
        task.execute("rooms",schoolyear);
    }

    @Override
    public void processFinish(ServerResponse response) {
        System.out.println(response.status);
        switch (response.endpoint) {
            case LOGIN:
                //initial login response
                if (response.status==200){
                    //logged in, repeating requests
                    initData();
                }
                break;
            case SCHOOLYEARS:
            case CLASSES:
            case ROOMS:
            case TEACHERS:
                if (response.status == 200) {
                    // NOT {"error":"no valid session"}
                    prossessResponseArray(response);
                }else{
                    if (response.status == 400 && !logging_in){
                        //no valid session and not trying to log in
                        logging_in = true;
                        API.autologin(((MainActivity)getActivity()).currentUser ,this);
                    }
                }
                break;
        }
    }

    private void prossessResponseArray(ServerResponse response){
        try {
            String message;
            switch (response.status){
                case 200:{
                    JSONArray json = new JSONArray(response.response);
                    Log.i("Response",response.endpoint.toString());
                    switch (response.endpoint){
                        case CLASSES:
                            availableClasses = jsontoArrayList(json,response.endpoint);
                            Log.i("Response", String.valueOf(availableClasses.size()));
                            adapter.setData(availableClasses);
                            break;
                        case ROOMS:
                            availableRooms = jsontoArrayList(json,response.endpoint);
                            Log.i("Response", String.valueOf(availableRooms.size()));
                            break;
                        case TEACHERS:
                            availableTeachers = jsontoArrayList(json,response.endpoint);
                            Log.i("Response", String.valueOf(availableTeachers.size()));
                            break;
                    }
                    message = "Retrieved the "+response.endpoint;
                    break;
                }
                case 400:
                case 500:{
                    JSONObject json = new JSONObject(response.response);
                    message = "Error: "+json.getString("error");
                    break;
                }
                default:{
                    message = "Something went wrong";
                }
            }
            Toast.makeText(getContext(), message, Toast.LENGTH_SHORT).show();
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }
    private ArrayList<String[]> jsontoArrayList(JSONArray json, API.APIEndpoint endpoint){
        ArrayList<String[]> arr = new ArrayList<>();
        try {
            for (int i = 0; i < json.length(); i++) {
                String data = json.getString(i);
                String[] entry = new String[2];
                if (endpoint == API.APIEndpoint.TEACHERS){
                    try {
                        Log.i("jsontoArrayList",data);
                        JSONObject jsonelement = new JSONObject(data);
                        entry[0] = jsonelement.getString("code");//id
                        if (!jsonelement.getString("name").isEmpty() || !jsonelement.getString("firstname").isEmpty()){
                            entry[1] = jsonelement.getString("name") +" "+jsonelement.getString("firstname");//toString
                        }else {
                            entry[1] = entry[0];//toString
                        }
                        arr.add(entry);
                    }catch (JSONException e){
                        //no json
                        Log.i("jsontoArrayList",data);
                    }
                }else{
                    entry[0] = data;//id
                    entry[1] = data;//toString
                    arr.add(entry);
                }

            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return arr;
    }



    private class MyAdapter<String> extends BaseAdapter implements Filterable {

        private ArrayList<String[]> data = new ArrayList<String[]>();
        private ArrayList<String[]> datafiltered = new ArrayList<String[]>();
        private Context context;


        public MyAdapter(Context context) {
            this.context = context;
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
        public View getView(final int position, View convertView, ViewGroup parent) {
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
        }

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
}