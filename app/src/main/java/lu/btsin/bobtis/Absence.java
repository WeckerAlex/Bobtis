package lu.btsin.bobtis;

import org.json.JSONException;
import org.json.JSONObject;

public class Absence {
    private String beginDate;
    private String beginTime;
    private String endDate;
    private String endTime;
    private String acomment;

    public Absence(String beginDate, String beginTime, String endDate, String endTime, String acomment) {
        this.beginDate = beginDate;
        this.beginTime = beginTime;
        this.endDate = endDate;
        this.endTime = endTime;
        this.acomment = acomment;
    }

    /**
     * Get the Absence in the JSON object
     * @param json JSON containing an absence
     * @return a new Absence
     */
    public static Absence getAbsence(JSONObject json){
        try {
            String beginDate = json.getString("id_student");
            String beginTime = json.getString("name");
            String endDate = json.getString("firstname");
            String endTime = json.getString("email");
            String acomment = json.getString("iam");
            return new Absence(beginDate,beginTime,endDate,endTime,acomment);
        } catch (JSONException e) {
            System.out.println(json);
            e.printStackTrace();
            return null;
        }
    }

    /**
     * Get the begindate
     * @return the begindate
     */
    public String getBeginDate() {
        return beginDate;
    }

    /**
     * Get the BeginTime
     * @return the BeginTime
     */
    public String getBeginTime() {
        return beginTime;
    }

    /**
     * Get the EndDate
     * @return the EndDate
     */
    public String getEndDate() {
        return endDate;
    }

    /**
     * Get the EndTime
     * @return the EndTime
     */
    public String getEndTime() {
        return endTime;
    }

    /**
     * Get the comment
     * @return the comment
     */
    public String getAcomment() {
        return acomment;
    }
}
