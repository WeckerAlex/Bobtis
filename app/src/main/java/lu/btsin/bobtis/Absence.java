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
     *
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

    public String getBeginDate() {
        return beginDate;
    }

    public String getBeginTime() {
        return beginTime;
    }

    public String getEndDate() {
        return endDate;
    }

    public String getEndTime() {
        return endTime;
    }

    public String getAcomment() {
        return acomment;
    }
}
