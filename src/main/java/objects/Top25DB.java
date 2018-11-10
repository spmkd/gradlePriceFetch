package objects;

import java.util.ArrayList;

public class Top25DB {

    private ArrayList<SingleGameDB> singleGameListDBArray = new ArrayList<SingleGameDB>();
    private String date;

    public ArrayList<SingleGameDB> getSingleGameListDBArray() {
        return singleGameListDBArray;
    }

    public void setSingleGameListDBArray(ArrayList<SingleGameDB> singleGameListDBArray) {
        this.singleGameListDBArray = singleGameListDBArray;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

}
