package objects;

import java.util.ArrayList;
import java.util.Date;

public class Top25 {

    private ArrayList<SingleGame> singleGameListArray = new ArrayList<SingleGame>();
    private String date;

    public ArrayList<SingleGame> getSingleGameListArray() {
        return singleGameListArray;
    }

    public void setSingleGameListArray(ArrayList<SingleGame> singleGameListArray) {
        this.singleGameListArray = singleGameListArray;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

}
