package lu.btsin.bobtis;

import android.graphics.Color;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.TableRow;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.Arrays;
import java.util.HashMap;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link Timetable_fragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class Timetable_fragment extends Fragment implements AsyncResponse {

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    private int hourHeight = 200;
    private int startHour = 7;
    private int endHour = 20;
    private int week = 10;

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    public Timetable_fragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment Timetable_fragment.
     */
    // TODO: Rename and change types and number of parameters
    public static Timetable_fragment newInstance(String param1, String param2) {
        Timetable_fragment fragment = new Timetable_fragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_timetable, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        drawTimetable();
        getClass("2021-2022",10,"B2IN");
    }

    protected void getClass(String schoolyear, int week, String requestedclass){
        API task =  new API();
        task.delegate = this;
        task.execute("class",schoolyear,week,requestedclass);
    }

    @Override
    public void processFinish(ServerResponse response) {
        switch (response.endpoint){
            case LOGIN:
//                prossessLogin(response);
                break;
            case SCHOOLYEARS:
//                prossessSchoolyear(response);
                break;
            case CLASSES:
//                prossessClasses(response);
                break;
            case CLASS:
                prossessClass(response);
                break;
        }
    }

    protected void drawTimetable(){
        LinearLayout legend = getView().findViewById(R.id.legend);
        for (int i = startHour;i<endHour;i++){
            TextView tw = new TextView(getContext());
            tw.setGravity(Gravity.CENTER);
            LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, hourHeight);
            params.weight = 1f;
            tw.setLines(3);
            tw.setLayoutParams(params);
            tw.setText(i+":00"+"\n-\n"+(i+1)+":00");
            tw.setBackgroundResource(R.drawable.legend);
            legend.addView(tw);
        }
    }

    protected void insertDayEvents(JSONArray daylessons,int day){
        System.out.println("Day");
        HashMap<String,String> extensions = new HashMap<>();
        try {
            //loop through every timeslot starting at the last course
            for (int hour=daylessons.length()-1; hour>=0; hour--){
                JSONArray concurrentarray = daylessons.getJSONArray(hour);//get the array containing all concurrent hours
                //loop through the concurrent lessons
                for (int lessoncount=concurrentarray.length()-1; lessoncount>=0; lessoncount--){
                    JSONObject schoolclass = new JSONObject(concurrentarray.getString(lessoncount));
                    //update endtime if nessesary
                    if (extensions.get(schoolclass.getString("id_lesson")) != null) {
                        //this lesson does not end at the saved end
                        schoolclass.put("end",extensions.get(schoolclass.getString("id_lesson")));
                        //remove the extension
                        extensions.remove(schoolclass.getString("id_lesson"));
                    }
                    //update hashmap
                    if (schoolclass.getString("fi_parent_lesson") != "null"){
                        //lesson belongs to other lesson
                        //save it into the extensions
                        extensions.put(schoolclass.getString("fi_parent_lesson"),schoolclass.getString("end"));
                        //remove the extension so it does not get displayed twice
                        concurrentarray.remove(lessoncount);
                    }else {
                        //lesson has no parent
                        //draw it
                        //TODO: draw the event

                        drawEvent(day,schoolclass.getString("begin"),schoolclass.getString("end"), schoolclass.getString("color"), schoolclass.getString("classe"), schoolclass.getString("teacher"), schoolclass.getString("subject"), schoolclass.getString("room"));

                    }
                }

            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    protected void drawEvent(int dayIndex,String startTime,String endTime,String color,String classText,String teacherAbb,String branchName,String roomAbb){
        LinearLayout parent = (LinearLayout) getView().findViewById(R.id.timetableLayout);
        FrameLayout dayLayout = (FrameLayout) parent.getChildAt(dayIndex+1);
        System.out.println("DayLayout "+dayIndex+":"+dayLayout);
        int[] startime = Arrays.stream(startTime.split(":")).mapToInt(Integer::parseInt).toArray();
        int[] endtime = Arrays.stream(endTime.split(":")).mapToInt(Integer::parseInt).toArray();
        int startheight = Math.round((startime[0]-startHour+startime[1]/60f)*hourHeight);
        int duration = Math.round((Math.min(endtime[0],endHour)+endtime[1]/60f)*hourHeight)-Math.round((startime[0]+startime[1]/60f)*hourHeight);
        System.out.println(startheight);
        System.out.println(duration);
        //the layout on which you are working
//        FrameLayout day1Layout = (FrameLayout) findViewById(view);//The whole day


        //create new entry
        LinearLayout mainLayout = new LinearLayout(getContext());
        mainLayout.setGravity(Gravity.CLIP_HORIZONTAL);
        mainLayout.setBackgroundResource(R.drawable.coursebackground);
        mainLayout.getBackground().setTint(Color.parseColor(color));
        TableRow.LayoutParams layoutParams = new TableRow.LayoutParams(TableRow.LayoutParams.MATCH_PARENT, duration);
        layoutParams.topMargin = startheight;
        mainLayout.setLayoutParams(layoutParams);

        LinearLayout ln1 = new LinearLayout(getContext());
        ln1.setLayoutParams(new TableRow.LayoutParams(TableRow.LayoutParams.WRAP_CONTENT, TableRow.LayoutParams.MATCH_PARENT,1f));
        ln1.setOrientation(LinearLayout.VERTICAL);
        ln1.addView(createTextView(classText));
        ln1.addView(createTextView(branchName));

        LinearLayout ln2 = new LinearLayout(getContext());
        ln2.setLayoutParams(new TableRow.LayoutParams(TableRow.LayoutParams.WRAP_CONTENT, TableRow.LayoutParams.MATCH_PARENT,1f));
        ln2.setOrientation(LinearLayout.VERTICAL);
        ln2.addView(createTextView(teacherAbb));
        ln2.addView(createTextView(roomAbb));

        mainLayout.addView(ln1);
        mainLayout.addView(ln2);
        mainLayout.setPadding(10,10,10,10);
        dayLayout.addView(mainLayout);
    }

    protected TextView createTextView(String text){
        TextView tw = new TextView(getContext());
        tw.setText(text);
        tw.setGravity(Gravity.CENTER);
        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);
        params.weight = 1f;
        tw.setLines(1);
        tw.setLayoutParams(params);
        return tw;
    }

    private void prossessClass(ServerResponse response){
        try {
            String message;
            switch (response.status){
                case 200:{
                    JSONArray dayarray = new JSONArray(response.response);//get the array containing the single days
                    for (int day=0;day< dayarray.length();day++){
                        //insert a single day
                        insertDayEvents(dayarray.getJSONArray(day),day);
                    }
                    break;
                }
                case 400:
                case 500:{
                    JSONObject json = new JSONObject(response.response);
                    message = "Error: "+json.getString("error");
                    Toast.makeText(getContext(), message, Toast.LENGTH_LONG).show();
                    break;
                }
                default:{
                    message = "Something went wrong";
                }
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }
}