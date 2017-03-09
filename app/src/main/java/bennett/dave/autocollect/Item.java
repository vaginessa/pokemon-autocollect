package bennett.dave.autocollect;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * Created by David on 8/10/2016.
 */
public class Item {
    private String name;
    private Date date;
    private String time;

    public Item(String name, Date date){
        this.name = name;
        this.date = date;
        setTime(date);
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Date getDate() {
        return date;
    }

    public void setDate(Date date) {
        this.date = date;
        setTime(date);
    }

    public String getTime() {
        return time;
    }

    private void setTime(Date d) {
        DateFormat df = new SimpleDateFormat("hh:mm a");
        time = df.format(d);

    }
}
