package lu.btsin.bobtis;

import android.util.Log;

import org.json.JSONException;
import org.json.JSONObject;

public class Homework {
    private String id_homework;
    private String type;
    private String date_due;
    private String title;
    private String content;

    public Homework(String id_homework,String type, String date_due,String title, String content) {
        this.id_homework = id_homework;
        this.type = type;
        this.date_due = date_due;
        this.title = title;
        this.content = content;
    }

    public static Homework getHomework(JSONObject json){
        try {
            //Student st = new Student();
            Log.i("Homework",json.toString());
            Log.i("Homework",json.getString("id_homework"));
            String id_homework = json.getString("id_homework");
            String type = json.getString("type");
            String date_due = json.getString("date_due");
            String title = json.getString("title");
            String content = json.getString("content");
            return new Homework(id_homework,type,date_due,title,content);
        } catch (JSONException e) {
            System.out.println(json);
            e.printStackTrace();
            return null;
        }
    }

    public String getType() {
        return type;
    }

    public String getTitle() {
        return title;
    }

    public String getId_homework() {
        return id_homework;
    }

    public String getDate_due() {
        return date_due;
    }

    public String getContent() {
        return content;
    }
}
