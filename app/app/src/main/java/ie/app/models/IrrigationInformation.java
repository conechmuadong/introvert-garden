package ie.app.models;

import java.sql.Time;
import java.util.Date;

public class IrrigationInformation {
    public String startTime, endTime, duration;
    public boolean irrigationCheck;
    Date start, end;

    @Override
    public String toString() {
        return "IrrigationInformation: " + startTime + " - " + endTime
                + ", auto is " + irrigationCheck + "\n";
    }

    public String getStartTime() {
        return startTime;
    }

    public String getEndTime() {
        return endTime;
    }

    public String getDuration() {
        return duration;
    }

    public boolean isIrrigationCheck() {
        return irrigationCheck;
    }

    public void setStartTime(String startTime) {
        this.startTime = startTime;
    }

    public void setEndTime(String endTime) {
        this.endTime = endTime;
    }

    public void setDuration(String duration) {
        this.duration = duration;
    }

    public void setIrrigationCheck(boolean irrigationCheck) {
        this.irrigationCheck = irrigationCheck;
    }
}
