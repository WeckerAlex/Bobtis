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

    /**
     * Get the Homework in the JSON object
     * @param json JSON containing an absence
     * @return a new Homework
     */
    public static Homework getHomework(JSONObject json){
        try {
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

    /**
     * Get the homeworks id
     * @return the id
     */
    public int getId_homework() {
        return id_homework;
    }

    /**
     * Get the homeworks due date
     * @return the due date
     */
    public String getDate_due() {
        return date_due;
    }

    /**
     * Get the homeworks content
     * @return the content
     */
    public String getContent() {
        return content;
    }
}
