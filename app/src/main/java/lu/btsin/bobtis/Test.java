package lu.btsin.bobtis;

import org.json.JSONException;
import org.json.JSONObject;

public class Test {
    private int id_test;
    private String title;
    private String content;

    /**
     * Constructor
     * @param id_test the test's id
     * @param title the test's title
     * @param content the test's content
     */
    public Test(int id_test, String title, String content) {
        this.id_test = id_test;
        this.title = title;
        this.content = content;
    }

    /**
     * Get the Test in the JSON object
     * @param json JSON containing an Test
     * @return a new Test
     */
    public static Test getTest(JSONObject json){
        try {
            int id_test = Integer.parseInt(json.getString("id_test"));
            String title = json.getString("title");
            String content = json.getString("content");
            return new Test(id_test,title,content);
        } catch (JSONException e) {
            e.printStackTrace();
            return null;
        }
    }

    /**
     * Gets the tests id
     * @return the tests id
     */
    public int getId_test() {
        return id_test;
    }

    /**
     * Gets the tests title
     * @return the tests title
     */
    public String getTitle() {
        return title;
    }

    /**
     * Gets the tests content
     * @return the tests content
     */
    public String getContent() {
        return content;
    }
}
