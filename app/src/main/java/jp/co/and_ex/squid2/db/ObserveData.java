package jp.co.and_ex.squid2.db;

/**
 * Created by obata on 2014/09/10.
 */
public class ObserveData {
    private Integer id;
    private String global_id;
    private String observe_date;
    private double latitude;
    private double longitude;
    private String user_id;
    private Integer uploaded;
    private String data;

    public Integer getUploaded() {
        return uploaded;
    }

    public void setUploaded(Integer uploaded) {
        this.uploaded = uploaded;
    }


    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getGlobal_id() {
        return global_id;
    }

    public void setGlobal_id(String global_id) {
        this.global_id = global_id;
    }

    public String getObserve_date() {
        return observe_date;
    }

    public void setObserve_date(String observe_date) {
        this.observe_date = observe_date;
    }

    public double getLatitude() {
        return latitude;
    }

    public void setLatitude(double latitude) {
        this.latitude = latitude;
    }

    public double getLongitude() {
        return longitude;
    }

    public void setLongitude(double longitude) {
        this.longitude = longitude;
    }

    public String getUser_id() {
        return user_id;
    }

    public void setUser_id(String user_id) {
        this.user_id = user_id;
    }

    public String getData() {
        return data;
    }

    public void setData(String data) {
        this.data = data;
    }


}
