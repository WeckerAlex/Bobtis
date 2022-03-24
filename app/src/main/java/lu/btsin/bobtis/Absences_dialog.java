package lu.btsin.bobtis;

import android.app.Dialog;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.NumberPicker;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.DialogFragment;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Arrays;

public class Absences_dialog extends DialogFragment implements AsyncResponse{

    int absenceId;
    int endHour;
    int endMinute;
    Reason[] reasons;
    Spinner sp;
    NumberPicker npHour;
    NumberPicker npMinute;
    EditText commentET;

    public Absences_dialog(int id_absence, String endTime) {
        super();
        absenceId = id_absence;
        int[] temp = new int[2];
        temp = Arrays.stream(endTime.split(":")).mapToInt(Integer::parseInt).limit(2).toArray();
        endHour = temp[0];
        endMinute = temp[1];
    }

    public Dialog onCreateDialog(Bundle savedInstanceState) {
        // Use the Builder class for convenient dialog construction
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setTitle("Edit absence")
                .setView(createView())
                .setPositiveButton("Save", (dialog, id) -> {})
                .setNeutralButton("Cancel", (dialog, id) -> {})
                .setNegativeButton("Delete", (dialog, id) -> {});
        getReasons();
        // Create the AlertDialog object and return it
        AlertDialog dialog = builder.create();
        dialog.setOnShowListener(dialogInterface -> {
            Button buttonpositive = ((AlertDialog) dialog).getButton(AlertDialog.BUTTON_POSITIVE);
            Button buttonneutral = ((AlertDialog) dialog).getButton(AlertDialog.BUTTON_NEUTRAL);
            Button buttonnegative = ((AlertDialog) dialog).getButton(AlertDialog.BUTTON_NEGATIVE);
            buttonpositive.setOnClickListener(view -> saveUpdate(absenceId,commentET.getText().toString(),reasons[sp.getSelectedItemPosition()].id_reason,npHour.getValue(),npMinute.getValue()));
            buttonneutral.setOnClickListener(view -> dismiss());
            buttonnegative.setOnClickListener(view -> deleteAbsence(absenceId));
        });
        return dialog;
    }

    private View createView(){
        View vw = getLayoutInflater().inflate(R.layout.alertdialog_abscence,null);

        sp = vw.findViewById(R.id.reasons_spinner);
        npHour = vw.findViewById(R.id.picker_hour);
        npMinute = vw.findViewById(R.id.picker_minute);
        commentET = vw.findViewById(R.id.TEAbsenceComment);

        npHour.setMinValue(0);
        npHour.setMaxValue(23);
        npHour.setValue(endHour);
        npMinute.setMinValue(0);
        npMinute.setMaxValue(59);
        npMinute.setValue(endMinute);
        return vw;
    }
    private void initElements(){
        ArrayAdapter aa = new ArrayAdapter(getContext(),android.R.layout.simple_spinner_item, reasons);
        sp.setAdapter(aa);
        sp.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                Log.i("Absences_dialog", String.valueOf(reasons[i].id_reason + " : "+reasons[i].name));
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });

    }

    @Override
    public void processFinish(ServerResponse response) {
        switch (response.endpoint){
            case AREASONS:
                Log.i("Absences_dialog", String.valueOf(response.status));
                reasons = jsontoArrayList(response.response);
                initElements();
                break;
            case ABSENCES:
                break;
            case ABSENCE_SPEED:
                break;
            case ABSENCE_UPDATE:
                switch (response.status){
                    case 200:{
                        Toast.makeText(getContext(), "Absence updated", Toast.LENGTH_SHORT).show();
                        dismiss();
                        break;
                    }
                    default:{
                        Toast.makeText(getContext(), response.status + ": "+ response.response, Toast.LENGTH_SHORT).show();
                    }
                }
                break;
            case ABSENCE_SHORTEN:
                break;
            case ABSENCE_REMOVE:
                switch (response.status){
                    case 200:{
                        Toast.makeText(getContext(), "Absence removed", Toast.LENGTH_SHORT).show();
                        dismiss();
                        break;
                    }
                    default:{
                        Log.i("Absences_dialog", String.valueOf(response.status));
                        Log.i("Absences_dialog", String.valueOf(response.response));
                        Toast.makeText(getContext(), response.status + ": "+ response.response, Toast.LENGTH_SHORT).show();
                    }
                }
                break;
        }
    }

    private void getReasons(){
        API task =  new API();
        task.delegate = this;
        task.execute(/*API.APIEndpoint.AREASONS*/"areasons");
    }

    private void saveUpdate(int id_absence, String comment, int id_reason, int endHour, int endMinute){
        Log.i("Absences_dialog","saveUpdate");
        API task =  new API();
        task.delegate = this;
        task.execute(/*API.APIEndpoint.ABSENCE_UPDATE*/"ABSENCE_UPDATE",id_absence,comment,id_reason,endHour+":"+endMinute);
    }

    private void deleteAbsence(int absenceId) {
        Log.i("Absences_dialog","deleteAbsence");
        API task =  new API();
        task.delegate = this;
        task.execute(/*API.APIEndpoint.ABSENCE_REMOVE*/"ABSENCE_REMOVE",absenceId);
    }

    private Reason[] jsontoArrayList(String response){
        ArrayList<Reason> arr = new ArrayList<>();
        try {
            JSONArray json = new JSONArray(response);
            for (int i = 0; i < json.length(); i++) {
                String data = json.getString(i);
                String[] entry = new String[2];
                try {
                    JSONObject jsonelement = new JSONObject(data);
                    arr.add(new Reason(Integer.parseInt(jsonelement.getString("id_areason")),jsonelement.getString("name")));
                }catch (JSONException e){
                    //no json
                    Log.i("jsontoArrayList",data);
                }
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
        Reason[] r_arr = new Reason[arr.size()];
        for (int i = 0; i < arr.size(); i++) {
            r_arr[i] = (Reason) arr.get(i);
        }
        return r_arr;
    }
    private class Reason{
        private int id_reason;
        private String name;

        public Reason(int id_reason, String name) {
            this.id_reason = id_reason;
            this.name = name;
        }

        @Override
        public String toString() {
            return name;
        }
    }
}