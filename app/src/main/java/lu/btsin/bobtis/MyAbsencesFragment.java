package lu.btsin.bobtis;

import android.graphics.drawable.GradientDrawable;
import android.os.Bundle;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
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

import java.util.ArrayList;

public class MyAbsencesFragment extends Fragment implements AsyncResponse {

    private ArrayList<Absence> data = new ArrayList<>();
    private static ListAdapter adapter;
    private int idStudent;

    /**
     * Constructor
     */
    public MyAbsencesFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_my_absences, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        init();
        super.onViewCreated(view, savedInstanceState);
    }

    /**
     * Initializes the list and it's adapter. Retrieves the Absences
     */
    public void init(){
        ListView list = getView().findViewById(R.id.myHomeworkList);
        if (adapter == null){
            data = new ArrayList<>();
            adapter = new ListAdapter<String>() {
                @Override
                public View getView(int position, View convertView, ViewGroup parent) {
                    Absence absece = data.get(position);
                    TextView twcontent = new TextView(parent.getContext());
                    TextView twdate = new TextView(parent.getContext());
                    LinearLayout.LayoutParams layoutParamsText = new TableRow.LayoutParams(TableRow.LayoutParams.MATCH_PARENT, TableRow.LayoutParams.MATCH_PARENT);
                    layoutParamsText.weight = 1;

                    twcontent.setPadding(1,0,1,0);
                    twcontent.setTextSize(13);
                    twcontent.setGravity(Gravity.CENTER_VERTICAL);
                    twcontent.setLayoutParams(layoutParamsText);
                    twcontent.setText(absece.getAcomment());

                    twdate.setPadding(1,0,1,0);
                    twdate.setTextSize(13);
                    twdate.setGravity(Gravity.CENTER_VERTICAL);
                    twdate.setLayoutParams(layoutParamsText);
                    twdate.setText(absece.getBeginDate()+" "+absece.getBeginTime() + " - "+absece.getEndDate()+" "+absece.getEndTime());

                    LinearLayout ll = new LinearLayout(parent.getContext());
                    ll.setGravity(Gravity.CLIP_HORIZONTAL);
                    ll.setBackgroundResource(R.drawable.coursebackground);
                    ((GradientDrawable) ll.getBackground()).setColor(ContextCompat.getColor(getContext(),R.color.listelement));
                    TableRow.LayoutParams layoutParams = new TableRow.LayoutParams(TableRow.LayoutParams.MATCH_PARENT, 100);
                    ll.setPadding(10,0,10,0);
                    layoutParams.setMargins(0,3,0,3);
                    ll.setLayoutParams(layoutParams);

                    ll.setOrientation(LinearLayout.VERTICAL);
                    ll.addView(twdate);
                    ll.addView(twcontent);
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
        //retrieve the users absences
        API.getAbsencesByStudent(idStudent,this);
    }

    /**
     * Sets the id
     * @param idStudent the students id
     */
    public void setData(int idStudent) {
        this.idStudent = idStudent;
    }

    @Override
    public void processFinish(ServerResponse response) {
        switch (response.endpoint){
            case ABSENCES:
                switch (response.status){
                    case 200:
                        try {
                            //remove the existing data
                            data.clear();
                            JSONArray json = new JSONArray(response.response);
                            for (int i = 0; i < json.length(); i++) {
                                if (!json.getJSONObject(i).getString("id_homework").equalsIgnoreCase("null")){
                                    Absence absence = Absence.getAbsence(json.getJSONObject(i));
                                    //add absence to arraylist
                                    data.add(absence);
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