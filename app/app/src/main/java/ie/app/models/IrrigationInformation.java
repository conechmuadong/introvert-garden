package ie.app.models;

import java.sql.Time;

public class IrrigationInformation {
    float duration = 0;
    Time startTime, endTime;
    boolean irrigationCheck;

    @Override
    public String toString() {
        return "IrrigationInformation: " + startTime.toString() + " - " + endTime.toString()
                + ", auto is " + irrigationCheck + "\n";
    }
}
