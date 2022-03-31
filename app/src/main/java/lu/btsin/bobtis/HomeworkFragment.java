package lu.btsin.bobtis;

import android.content.res.ColorStateList;
import android.graphics.Color;
import android.graphics.drawable.GradientDrawable;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;

import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TableRow;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;

import java.util.ArrayList;

public class HomeworkFragment extends Fragment implements AsyncResponse {

    private ArrayList<Homework> data = new ArrayList<>();
    private static ListAdapter adapter;
    private String schoolyear;
    private int id_lesson;
    private static boolean editing_allowed;

    public HomeworkFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_homework, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        init();
        ImageButton addButton = getView().findViewById(R.id.addHomeworkButton);
        addButton.setOnClickListener(view1 -> addbuttonclick());
        if (editing_allowed){
            addButton.setVisibility(View.VISIBLE);
        }else {
            addButton.setVisibility(View.INVISIBLE);
        }

        super.onViewCreated(view, savedInstanceState);
    }

    public void init(){
        ListView list = getView().findViewById(R.id.homeworkList);
        Log.i("enumerstudent_init_view", "list is null: " +(list==null));
        Log.i("enumerstudent_init_view", "context is null: " +(getContext()==null));
        if (adapter == null){
            data = new ArrayList<>();
            adapter = new ListAdapter<String>() {
                @Override
                public View getView(int position, View convertView, ViewGroup parent) {
                    Homework homework = data.get(position);
                    TextView twcontent = new TextView(parent.getContext());
                    TextView twdate = new TextView(parent.getContext());
                    LinearLayout.LayoutParams layoutParamsText = new TableRow.LayoutParams(TableRow.LayoutParams.WRAP_CONTENT, TableRow.LayoutParams.MATCH_PARENT);
                    layoutParamsText.weight = 1;


                    twcontent.setPadding(1,0,1,0);
                    twcontent.setTextSize(13);
                    twcontent.setGravity(Gravity.CENTER_VERTICAL);
                    twcontent.setLayoutParams(layoutParamsText);
                    twcontent.setText(homework.getContent());

                    twdate.setPadding(1,0,1,0);
                    twdate.setTextSize(13);
                    twdate.setGravity(Gravity.CENTER_VERTICAL);
                    twdate.setLayoutParams(layoutParamsText);
                    twdate.setText(homework.getDate_due());

                    LinearLayout ll = new LinearLayout(parent.getContext());
                    ll.setGravity(Gravity.CLIP_HORIZONTAL);
                    ll.setBackgroundResource(R.drawable.coursebackground);
                    ((GradientDrawable) ll.getBackground()).setColor(ContextCompat.getColor(getContext(),R.color.listelement));
                    TableRow.LayoutParams layoutParams = new TableRow.LayoutParams(TableRow.LayoutParams.MATCH_PARENT, TableRow.LayoutParams.WRAP_CONTENT);
                    ll.setPadding(10,0,10,0);
                    layoutParams.setMargins(0,3,0,3);
                    ll.setLayoutParams(layoutParams);

                    ll.setOrientation(LinearLayout.HORIZONTAL);
                    ll.addView(twdate);
                    ll.addView(twcontent);

                    if (editing_allowed){
                        ImageButton buttonAbsence = new ImageButton(parent.getContext());
                        buttonAbsence.setImageTintList(ColorStateList.valueOf(Color.BLACK));
                        LinearLayout.LayoutParams layoutParamsInsert = new TableRow.LayoutParams(TableRow.LayoutParams.WRAP_CONTENT, TableRow.LayoutParams.WRAP_CONTENT);
                        layoutParamsInsert.setMargins(0,3,0,3);
                        buttonAbsence.setLayoutParams(layoutParamsInsert);
                        buttonAbsence.setImageDrawable(getResources().getDrawable(R.drawable.ic_baseline_edit_24_small,null));
                        buttonAbsence.setOnClickListener(view -> editbuttonclick(homework));
                        ll.addView(buttonAbsence);
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
        API.getHomeworks(schoolyear,id_lesson,this);
    }

    public void setData(String schoolyear,int id_lesson,boolean editing_allowed) {
        this.schoolyear = schoolyear;
        this.id_lesson = id_lesson;
        this.editing_allowed = editing_allowed;
    }

    private void addbuttonclick(){
        //student has already got an absence
        //create homework
        Homework_dialog dialog = new Homework_dialog(id_lesson,schoolyear,this);
        dialog.show(getParentFragmentManager(),null);
    }

    private void editbuttonclick(Homework homework){
        //student has already got an absence
        //update homework
        Homework_dialog dialog = new Homework_dialog(homework.getId_homework(),id_lesson,homework.getContent(),homework.getDate_due(),schoolyear,this);
        dialog.show(getParentFragmentManager(),null);
    }

    @Override
    public void processFinish(ServerResponse response) {
        switch (response.endpoint){
            case HOMEWORKS:
                Log.i("HOMEWORKS_fragment","HOMEWORKS");
                switch (response.status){
                    case 200:{
                        try {
                            data.clear();
                            JSONArray json = new JSONArray(response.response);
                            for (int i = 0; i < json.length(); i++) {
                                if (!json.getJSONObject(i).getString("id_homework").equalsIgnoreCase("null")){
                                    Homework homework = Homework.getHomework(json.getJSONObject(i));
                                    data.add(homework);
                                }
                            }
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
        }
    }
}