package ie.app.models;

import java.util.Date;

public class Stage {
    String name;
    int humidity = 0;
    Date startTime, endTime;

    @Override
    public String toString() {
        return name + " : độ ẩm " + humidity + ", ngày bắt đầu " + startTime.toString() +
                ", ngày kết thúc " + endTime.toString() + "\n";
    }

}
