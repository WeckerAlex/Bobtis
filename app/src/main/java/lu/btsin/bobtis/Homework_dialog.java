package lu.btsin.bobtis;

import android.app.Dialog;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.DialogFragment;

import java.util.Arrays;

public class Homework_dialog extends DialogFragment implements AsyncResponse{

    int homeworkId;
    boolean is_creating;
    int lessonId;
    String content;
    String date;
    String schoolyear;
    DatePicker dp;
    EditText contentET;
    AsyncResponse parent;

    public Homework_dialog(int id_homework, int lessonId, String content,String date, String schoolyear, AsyncResponse parent) {
        super();
        Log.i("lessonIdlessonId", String.valueOf(lessonId));
        this.is_creating = false;
        this.homeworkId = id_homework;
        this.lessonId = lessonId;
        this.content = content;
        this.date = date;
        this.parent = parent;
        this.schoolyear = schoolyear;
    }
    public Homework_dialog(int lessonId, String schoolyear, AsyncResponse parent) {
        super();
        this.is_creating = true;
        this.lessonId = lessonId;
        this.parent = parent;
        this.schoolyear = schoolyear;
    }

    public Dialog onCreateDialog(Bundle savedInstanceState) {
        // Use the Builder class for convenient dialog construction
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setTitle("Edit absence")
                .setView(createView())
                .setPositiveButton("Save", (dialog, id) -> {})
                .setNeutralButton("Cancel", (dialog, id) -> {})
                .setNegativeButton("Delete", (dialog, id) -> {});
        API.getReasons(this);
        // Create the AlertDialog object and return it
        AlertDialog dialog = builder.create();
        dialog.setOnShowListener(dialogInterface -> {
            //overwrite default actions
            Button buttonpositive = dialog.getButton(AlertDialog.BUTTON_POSITIVE);
            Button buttonneutral = dialog.getButton(AlertDialog.BUTTON_NEUTRAL);
            Button buttonnegative = dialog.getButton(AlertDialog.BUTTON_NEGATIVE);

            buttonpositive.setOnClickListener(view -> {
                if (is_creating){
                    Log.i("HWDialog","addHomework");
                    API.addHomework(lessonId, contentET.getText().toString(), getDate(), this);
                }else{
                    Log.i("HWDialog","updateHomework");
                    API.updateHomework(homeworkId, contentET.getText().toString(), getDate(), this);
                }
            });
            buttonneutral.setOnClickListener(view -> dismiss());
            buttonnegative.setOnClickListener(view -> {
                if (!is_creating){
                    Log.i("","API.removeHomework");
                    API.removeHomework(homeworkId, this);
                }
            });
        });
        return dialog;
    }

    private View createView(){
        View vw = getLayoutInflater().inflate(R.layout.alertdialog_homework,null);

        dp = vw.findViewById(R.id.HomeworkDatePicker);
        contentET = vw.findViewById(R.id.TEHomeworkComment);
        if (!is_creating){
            Log.i("HWDialog",date);
            int[] datearray = Arrays.stream(date.split("\\.")).mapToInt(Integer::parseInt).toArray();
            Log.i("HWDialog", String.valueOf(datearray.length));

            contentET.setText(content);
            dp.updateDate(datearray[0],datearray[1]-1,datearray[2]);
        }
        return vw;
    }

    @Override
    public void processFinish(ServerResponse response) {
        switch (response.endpoint){
            case HOMEWORK_ADD:
                switch (response.status){
                    case 200:{
                        Toast.makeText(getContext(), "Homework saved", Toast.LENGTH_SHORT).show();
                        dismiss();
                        API.getHomeworks(schoolyear,lessonId,parent);
                        break;
                    }
                }
                break;
            case HOMEWORK_UPDATE:
                switch (response.status){
                    case 200:{
                        Toast.makeText(getContext(), "Homework updated", Toast.LENGTH_SHORT).show();
                        dismiss();
                        API.getHomeworks(schoolyear,lessonId,parent);
                        break;
                    }
                    default:{
                        Toast.makeText(getContext(), response.status + ": "+ response.response, Toast.LENGTH_SHORT).show();
                    }
                }
                break;
            case HOMEWORK_REMOVE:
                switch (response.status){
                    case 200:{
                        Toast.makeText(getContext(), "Homework removed", Toast.LENGTH_SHORT).show();
                        dismiss();
                        API.getHomeworks(schoolyear,lessonId,parent);
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

    private String getDate(){
        return dp.getYear()+"."+(dp.getMonth()+1)+"."+dp.getDayOfMonth();
    }
}