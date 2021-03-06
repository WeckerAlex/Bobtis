package lu.btsin.bobtis;

import android.content.res.ColorStateList;
import android.graphics.Color;
import android.graphics.drawable.GradientDrawable;
import android.os.Bundle;
import android.text.SpannableStringBuilder;
import android.text.style.CharacterStyle;
import android.text.style.ForegroundColorSpan;
import android.text.style.StrikethroughSpan;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TableRow;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;

public class Absenses_fragment extends Fragment implements AsyncResponse {

    private ArrayList<Student> data = new ArrayList<>();
    private static ListAdapter adapter;
    private static boolean is_allowed_create_absences;
    private String schoolyear;
    private int id_lesson;

    public Absenses_fragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        Log.i("Atest","onCreateView");
        View view = inflater.inflate(R.layout.fragment_absenses,container,false);
        return view;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        Log.i("Atest","onCreate");
        super.onCreate(savedInstanceState);
        Log.i("enumerstudent", "onCreate");
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        Log.i("Atest","onViewCreated");
        init();
        super.onViewCreated(view, savedInstanceState);
    }

    public void init(){
        Log.i("DetailsonViewCreated","ABonViewCreated");
        ListView list = getView().findViewById(R.id.studentList);
        Log.i("enumerstudent_init_view", "list is null: " +(list==null));
        Log.i("enumerstudent_init_view", "context is null: " +(getContext()==null));
        if (adapter == null){
            data = new ArrayList<>();
            adapter = new ListAdapter<String>() {
                @Override
                public View getView(int position, View convertView, ViewGroup parent) {
                    Student student = data.get(position);
                    TextView tw = new TextView(parent.getContext());
                    LinearLayout.LayoutParams layoutParamsText = new TableRow.LayoutParams(TableRow.LayoutParams.WRAP_CONTENT, TableRow.LayoutParams.WRAP_CONTENT);
                    layoutParamsText.weight = 1;

                    TextView twdate = new TextView(parent.getContext());

                    LinearLayout.LayoutParams textLayoutparams = new TableRow.LayoutParams(TableRow.LayoutParams.MATCH_PARENT, TableRow.LayoutParams.MATCH_PARENT);
                    textLayoutparams.weight = 1;
                    textLayoutparams.setMargins(1,1,1,1);
                    LinearLayout textLayout = new LinearLayout(parent.getContext());
                    textLayout.setOrientation(LinearLayout.VERTICAL);
                    textLayout.setGravity(Gravity.CENTER_VERTICAL);

                    textLayout.setLayoutParams(textLayoutparams);


                    tw.setPadding(1,0,1,0);
                    tw.setTextSize(18);
                    tw.setLayoutParams(layoutParamsText);
                    tw.setText(student.getFirstname()+" "+student.getName(), TextView.BufferType.SPANNABLE);
                    twdate.setTextSize(13);
                    twdate.setPadding(1,0,1,0);
                    twdate.setLayoutParams(layoutParamsText);
                    twdate.setText(student.getAbsenceEndDate()+ " " + formathour(student.getAbsenceEndTime()));

                    LinearLayout ll = new LinearLayout(parent.getContext());
                    ll.setGravity(Gravity.CLIP_HORIZONTAL);
                    ll.setBackgroundResource(R.drawable.coursebackground);
                    ((GradientDrawable) ll.getBackground()).setColor(ContextCompat.getColor(getContext(),R.color.listelement));
                    TableRow.LayoutParams layoutParams = new TableRow.LayoutParams(TableRow.LayoutParams.MATCH_PARENT, TableRow.LayoutParams.WRAP_CONTENT);
                    ll.setPadding(10,0,10,0);
                    layoutParams.setMargins(0,3,0,3);
                    ll.setLayoutParams(layoutParams);

                    ll.setOrientation(LinearLayout.HORIZONTAL);
                    textLayout.addView(tw);
                    textLayout.addView(twdate);
                    ll.addView(textLayout);
                    if (is_allowed_create_absences){
                        Button buttonVtt = new Button(parent.getContext());
                        LinearLayout.LayoutParams layoutParamsShorten = new TableRow.LayoutParams(TableRow.LayoutParams.WRAP_CONTENT, TableRow.LayoutParams.WRAP_CONTENT);
                        layoutParamsShorten.setMargins(0,3,0,3);
                        buttonVtt.setLayoutParams(layoutParamsShorten);
                        buttonVtt.setOnClickListener(view -> API.shortenAbsence(student.getAbsenceId(),getParent()));
                        buttonVtt.setText(getResources().getString(R.string.VTT));
                        ll.addView(buttonVtt);

                        ImageButton buttonAbsence = new ImageButton(parent.getContext());
                        buttonAbsence.setImageTintList(ColorStateList.valueOf(Color.BLACK));
                        LinearLayout.LayoutParams layoutParamsInsert = new TableRow.LayoutParams(TableRow.LayoutParams.WRAP_CONTENT, TableRow.LayoutParams.MATCH_PARENT);
                        layoutParamsInsert.setMargins(0,3,0,3);
                        buttonAbsence.setLayoutParams(layoutParamsInsert);
                        if (student.getAbsenceId() != 0){
                            SpannableStringBuilder ssb = new SpannableStringBuilder( tw.getText() );
                            CharacterStyle span0 = new ForegroundColorSpan(getResources().getColor(R.color.strikethrough));
                            StrikethroughSpan span = new StrikethroughSpan();
                            ssb.setSpan( span, 0, tw.getText().length(), 0 );
                            ssb.setSpan( span0, 0, tw.getText().length(), 0 );
                            tw.setText(ssb);

                            buttonVtt.setVisibility(View.VISIBLE);
                            buttonAbsence.setImageDrawable(getResources().getDrawable(R.drawable.ic_baseline_edit_24_small));
                        }else{
                            buttonVtt.setVisibility(View.INVISIBLE);
                            buttonAbsence.setImageDrawable(getResources().getDrawable(R.drawable.ic_baseline_post_add_24));
                        }
                        buttonAbsence.setOnClickListener(view -> absencebuttonclick(student));
                        ll.addView(buttonAbsence);
                    }
                    if (((MainActivity)getActivity()).currentUser.has_Permission(User.Right.SCHEDULE_STUDENTS)){
                        ll.setOnClickListener(view -> ((MainActivity)getActivity()).displayStudent(student.getId(),false));
                    }
                    return ll;
                }

                @Override
                public boolean filterEntry(String entry, CharSequence constraint) {
                    return ((entry).toUpperCase().contains(((String) constraint).toUpperCase()));
                }
            };
        }
        adapter.setData(data);
        list.setAdapter(adapter);
        API.getStudents(schoolyear, id_lesson,this);
    }

    private String formathour(String absenceEndTime) {
        if (absenceEndTime.equalsIgnoreCase("0:0")){
            return "";
        }else{
            return absenceEndTime;
        }
    }

    private AsyncResponse getParent(){
        return this;
    }

    public void setData(String schoolyear,int id_lesson,boolean is_allowed_create_absences) {
//        API.getStudents(schoolyear, id_lesson,this);
        Log.i("DetailsonViewCreated","onViewCreatedsD");
        this.schoolyear = schoolyear;
        this.id_lesson = id_lesson;
        this.is_allowed_create_absences = is_allowed_create_absences;
    }

    private void absencebuttonclick(Student student){
        if (student.getAbsenceId() != 0){
            //student has already got an absence
            //update absence
            Absences_dialog dialog = new Absences_dialog(student.getAbsenceId(), student.getAbsenceEndTime(), student.getReasonId(),student.getComment(),schoolyear,id_lesson,this);
            dialog.show(getParentFragmentManager(),null);
        }else{
            //student has no absence
            //create absence
            API.setAbsenceSpeed(id_lesson,student.getId(),this);
        }
    }

    @Override
    public void processFinish(ServerResponse response) {
        switch (response.endpoint){
            case STUDENTS:
                Log.i("Absenses_fragment","STUDENTS");
                switch (response.status){
                    case 200:{
                        try {
                            Log.i("Atest","200");
                            data.clear();
                            JSONArray json = new JSONArray(response.response);
                            for (int i = 0; i < json.length(); i++) {
                                Student student = Student.getStudent(json.getJSONObject(i));
                                Log.i("enumerstudent", student.getFirstname());
                                data.add(student);
                            }
                            API.getAbsences(schoolyear, id_lesson,this);
                            adapter.notifyDataSetChanged();
                        } catch (JSONException e) {
                            Log.i("enumerstudent", data.toString());
                            e.printStackTrace();
                        }
                        break;
                    }
                    default:{
                        Toast.makeText(getActivity(),response.response,Toast.LENGTH_LONG).show();
                    }
                }
                break;
            case ABSENCE_SPEED:
                Log.i("ABSENCE_SPEED", String.valueOf(response.status));
                switch (response.status){
                    case 200:{
                        try {
                            JSONObject json = new JSONObject(response.response);
                            for (int i = 0; i < data.size(); i++) {
                                if (data.get(i).getId() == Integer.parseInt(json.getString("id_student"))){
                                    data.get(i).setAbsenceId(Integer.parseInt(json.getString("id_absence")));
                                }
                            }
                            adapter.notifyDataSetChanged();
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                        break;
                    }
                    default:{
                        Toast.makeText(getContext(),response.response,Toast.LENGTH_SHORT).show();
                    }
                }
                Log.i("Absenses_fragment","ABSENCE_SPEED");
                break;
            case ABSENCE_SHORTEN:
                switch (response.status){
                    case 200:{
                        adapter.notifyDataSetChanged();
                        break;
                    }
                    default:{
                        Toast.makeText(getContext(),response.response,Toast.LENGTH_SHORT).show();
                    }
                }
                Log.i("Absenses_fragment","ABSENCE_SHORTEN");
                break;
            case ABSENCES:
                switch (response.status){
                    case 200:
                        try {
                            JSONArray json = new JSONArray(response.response);
                            HashMap<Integer,HashMap<String,String>> responsedata = new HashMap<Integer, HashMap<String,String>>();
                            for (int i = 0; i < json.length(); i++) {
                                Log.i("Student",json.getJSONObject(i).toString());
                                HashMap<String,String> studentData = new HashMap();
                                studentData.put("id_absence",json.getJSONObject(i).getString("id_absence"));
                                studentData.put("endTime",json.getJSONObject(i).getString("endTime"));
                                studentData.put("acomment",json.getJSONObject(i).getString("acomment"));
                                studentData.put("reasonId",json.getJSONObject(i).getString("fi_areason"));
                                studentData.put("absenceEndDate",json.getJSONObject(i).getString("endDate"));
                                studentData.put("absenceEndTime",json.getJSONObject(i).getString("endTime"));
                                responsedata.put(Integer.parseInt(json.getJSONObject(i).getString("id_student")),studentData);
                            }
                            for (int i = 0; i < data.size()-1; i++) {
                                if (responsedata.containsKey(data.get(i).getId())){
                                    data.get(i).setAbsenceId(Integer.parseInt(responsedata.get(data.get(i).getId()).get("id_absence")));
                                    data.get(i).setAbsenceEndTime(responsedata.get(data.get(i).getId()).get("endTime"));
                                    data.get(i).setComment(responsedata.get(data.get(i).getId()).get("acomment"));
                                    data.get(i).setReasonId(Integer.parseInt(responsedata.get(data.get(i).getId()).get("reasonId")));
                                    data.get(i).setAbsenceEndDate(responsedata.get(data.get(i).getId()).get("absenceEndDate"));
                                    data.get(i).setAbsenceEndTime(responsedata.get(data.get(i).getId()).get("absenceEndTime"));
                                }
                            }
                            adapter.notifyDataSetChanged();
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                        break;
                    default:{
                        Toast.makeText(getContext(),response.response,Toast.LENGTH_SHORT).show();
                    }
                }
                break;
        }
    }

}