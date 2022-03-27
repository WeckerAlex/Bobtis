package lu.btsin.bobtis;

import android.util.Log;

import org.json.JSONException;
import org.json.JSONObject;

public class Test {
    private String id_test;
    private String title;
    private String content;

    public Test(String id_test, String title, String content) {
        this.id_test = id_test;
        this.title = title;
        this.content = content;
    }

    public static Test getTest(JSONObject json){
        try {
            String id_test = json.getString("id_test");
            String title = json.getString("title");
            String content = json.getString("content");
            return new Test(id_test,title,content);
        } catch (JSONException e) {
            System.out.println(json);
            e.printStackTrace();
            return null;
        }
    }

    public String getId_test() {
        return id_test;
    }

    public String getTitle() {
        return title;
    }

    public String getContent() {
        return content;
    }
}
