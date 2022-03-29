package lu.btsin.bobtis;

import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.GradientDrawable;
import android.graphics.drawable.RippleDrawable;
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
import android.widget.Button;
import android.widget.EditText;
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

    ListView listView;
    EditText searchedittext;
    private boolean logging_in = true;
    private static ArrayList<String[]> availableClasses = new ArrayList<>();
    private static ArrayList<String[]> availableRooms = new ArrayList<>();
    private static ArrayList<String[]> availableTeachers = new ArrayList<>();
    private static int selectedCategory = R.id.classbutton;
    private static ListAdapter adapter;
    private Button classbutton;
    private Button roombutton;
    private Button teacherbutton;

    public Search_Fragment() {
        // Required empty public constructor
    }

    public static Search_Fragment newInstance(String param1, String param2) {
        Search_Fragment fragment = new Search_Fragment();
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
        classbutton = (Button) getView().findViewById(R.id.classbutton);
        roombutton = (Button) getView().findViewById(R.id.roombutton);
        teacherbutton = (Button) getView().findViewById(R.id.teacherbutton);

        classbutton.setOnClickListener(v -> buttonclick(availableClasses,v));
        roombutton.setOnClickListener(v -> buttonclick(availableRooms,v));
        teacherbutton.setOnClickListener(v -> buttonclick(availableTeachers,v));

        if (adapter == null){
            //adapter = new MyAdapter(getContext());
            adapter = new ListAdapter<String[]>() {
                @Override
                public View getView(int position, View convertView, ViewGroup parent) {
                    TextView tw = new TextView(getContext());
                    LinearLayout.LayoutParams layoutParamsText = new TableRow.LayoutParams(TableRow.LayoutParams.MATCH_PARENT, TableRow.LayoutParams.WRAP_CONTENT);
                    layoutParamsText.setMargins(1,1,1,1);
                    layoutParamsText.weight = 1;

                    tw.setPadding(1,0,1,0);
                    tw.setLayoutParams(layoutParamsText);
                    tw.setText(((String[])getDatafiltered().get(position))[1].toString());

                    LinearLayout ll = new LinearLayout(getContext());
                    ll.setOnClickListener(view -> display(((String[])getDatafiltered().get(position))[0].toString()));
                    ll.setGravity(Gravity.CLIP_HORIZONTAL);
                    ll.setBackgroundResource(R.drawable.coursebackground);
                    ((GradientDrawable) ll.getBackground()).setColor(Color.parseColor("#FFFF99"));
                    TableRow.LayoutParams layoutParams = new TableRow.LayoutParams(TableRow.LayoutParams.MATCH_PARENT, TableRow.LayoutParams.WRAP_CONTENT);
                    ll.setPadding(10,0,10,0);
                    ll.setLayoutParams(layoutParams);

                    ll.setOrientation(LinearLayout.HORIZONTAL);
                    ll.addView(tw);
                    Button button = new Button(getContext());
                    button.setText("Add to favorites");
                    button.setOnClickListener(view1 -> {
                        addEntry(((String[])getDatafiltered().get(position)));
                        button.setVisibility(View.INVISIBLE);
                    });
                    ll.addView(button);
                    if (hasEntry(((String[])getDatafiltered().get(position))[1])){
                        button.setVisibility(View.INVISIBLE);
                    }
                    return ll;
                }

                @Override
                public boolean filterEntry(String[] entry, CharSequence constraint) {
                    return ((entry[1]).toUpperCase().contains(((String) constraint).toUpperCase()));
                }
            };
        }
        listView.setAdapter(adapter);
        listView.setTextFilterEnabled(true);
        searchedittext.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                adapter.getFilter().filter(charSequence);
            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        });
        searchedittext.setOnEditorActionListener((v, actionId, event) -> {
            if (actionId == EditorInfo.IME_ACTION_SEARCH) {
                dismissKeyboard();
                return true;
            }
            return false;
        });
        adapter.getFilter().filter("");
        //disable category if the user has not the permission
        if (((MainActivity)getActivity()).currentUser != null){
            if (!((MainActivity)getActivity()).currentUser.has_Permission(User.Right.SCHEDULE_TEACHERS)){
                teacherbutton.setVisibility(View.GONE);
            }
            if (!((MainActivity)getActivity()).currentUser.has_Permission(User.Right.SCHEDULE_CLASSES)){
                classbutton.setVisibility(View.GONE);
            }
            if (!((MainActivity)getActivity()).currentUser.has_Permission(User.Right.SCHEDULE_ROOMS)){
                roombutton.setVisibility(View.GONE);
            }
        }
    }

    private void buttonclick(ArrayList<String[]> data,View sender){
        Log.i("Colorswitch",sender.toString());
        Log.i("Colorswitch",classbutton.toString());
        Log.i("Colorswitch", String.valueOf((((Button)sender).getId() == classbutton.getId())));
        adapter.setData(data);
        searchedittext.setText("");
        selectedCategory = sender.getId();
    }

    private void addEntry(String[] data) {
        switch (selectedCategory){
            //switch on the currently selected category
            case R.id.classbutton:{
                ((MainActivity)getActivity()).addClass(data[0]);
                break;
            }
            case R.id.roombutton:{
                ((MainActivity)getActivity()).addRoom(data[0]);
                break;
            }
            case R.id.teacherbutton:{
                ((MainActivity)getActivity()).addTeacher(data);
                break;
            }
        }
    }

    private boolean hasEntry(String data) {
        switch (selectedCategory){
            //switch on the currently selected category
            case R.id.classbutton:
                return ((MainActivity)getActivity()).currentUser.hasClass(data);
            case R.id.roombutton:
                return ((MainActivity)getActivity()).currentUser.hasRoom(data);
            case R.id.teacherbutton:
                return ((MainActivity)getActivity()).currentUser.hasTeacher(data);
            default:
                return false;
        }
    }

    /**
     * This function gets called after an item has been selected
     * @param data the lable on the selected item
     */
    private void display(String data){
        switch (selectedCategory){
            //switch on the currently selected category
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

    private void dismissKeyboard() {
        searchedittext.clearFocus();
        InputMethodManager imm = (InputMethodManager) getActivity().getSystemService(Context.INPUT_METHOD_SERVICE);
        imm.hideSoftInputFromWindow(searchedittext.getWindowToken(), 0);
    }

    private void initData(){
        Log.i("User", String.valueOf(((MainActivity)getActivity()).currentUser));
        if (((MainActivity)getActivity()).currentUser != null){
            if (((MainActivity)getActivity()).currentUser.has_Permission(User.Right.SCHEDULE_CLASSES) && (availableClasses == null || availableClasses.isEmpty())){
                API.getClasses("2021-2022",this);
            }
            if (((MainActivity)getActivity()).currentUser.has_Permission(User.Right.SCHEDULE_ROOMS) && (availableRooms == null || availableRooms.isEmpty())){
                API.getRooms("2021-2022",this);
            }
            if (((MainActivity)getActivity()).currentUser.has_Permission(User.Right.SCHEDULE_TEACHERS) && (availableTeachers == null || availableTeachers.isEmpty())){
                API.getTeachers("2021-2022",this);
            }
        }

    }

    @Override
    public void processFinish(ServerResponse response) {
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
            //String message;
            switch (response.status){
                case 200:{
                    JSONArray json = new JSONArray(response.response);
                    Log.i("Response",response.endpoint.toString());
                    switch (response.endpoint){
                        case CLASSES:
                            availableClasses = jsontoArrayList(json,response.endpoint);
                            Log.i("Response", String.valueOf(availableClasses.size()));
                            if (adapter.is_data_set()){
                                adapter.setData(availableClasses);
                            }
                            break;
                        case ROOMS:
                            availableRooms = jsontoArrayList(json,response.endpoint);
                            Log.i("Response", String.valueOf(availableRooms.size()));
                            if (adapter.is_data_set()){
                                adapter.setData(availableRooms);
                            }
                            break;
                        case TEACHERS:
                            availableTeachers = jsontoArrayList(json,response.endpoint);
                            Log.i("Response", String.valueOf(availableTeachers.size()));
                            if (adapter.is_data_set()){
                                adapter.setData(availableTeachers);
                            }
                            break;
                    }
//                    message = "Retrieved the "+response.endpoint;
                    break;
                }
                case 400:
                case 500:{
                    JSONObject json = new JSONObject(response.response);
                    Toast.makeText(getContext(), "Error: "+json.getString("error"), Toast.LENGTH_SHORT).show();
                    break;
                }
                default:{
                    Toast.makeText(getContext(), "Something went wrong", Toast.LENGTH_SHORT).show();
                }
            }
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
}