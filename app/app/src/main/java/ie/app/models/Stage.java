package ie.app.models;

import java.util.Date;

public class Stage {
    static int count = 0;
    String name;
    float humidity = 0;
    Date startDate, endDate;

    public Stage() {
        ++count;
        this.name = "GĐ " + count;
    }

    @Override
    public String toString() {
        return name + " : độ ẩm " + humidity + ", ngày bắt đầu " + startDate.toString() +
                ", ngày kết thúc " + endDate.toString() + "\n";
    }

    public String getName() {
        return this.name;
    }

    public float getHumidity()  {
        return this.humidity;
    }

    public Date getStartDate() {
        return startDate;
    }

    public Date getEndDate() {
        return endDate;
    }
}
