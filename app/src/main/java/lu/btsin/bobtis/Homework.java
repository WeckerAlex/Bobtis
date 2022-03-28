package lu.btsin.bobtis;

import android.util.Log;

import org.json.JSONException;
import org.json.JSONObject;

public class Homework {
    private int id_homework;
    private String date_due;
    private String content;

    public Homework(int id_homework, String date_due, String content) {
        this.id_homework = id_homework;
        this.date_due = date_due;
        this.content = content;
    }

    public static Homework getHomework(JSONObject json){
        try {
            //Student st = new Student();
            int id_homework = Integer.parseInt(json.getString("id_homework"));
            String date_due = json.getString("date_due");
            String content = json.getString("content");
            return new Homework(id_homework,date_due,content);
        } catch (JSONException e) {
            System.out.println(json);
            e.printStackTrace();
            return null;
        }
    }

    public int getId_homework() {
        return id_homework;
    }

    public String getDate_due() {
        return date_due;
    }

    public String getContent() {
        return content;
    }
}
