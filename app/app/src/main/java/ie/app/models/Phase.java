package ie.app.models;

public class Phase {
    String name;
    public float threshHold = 0;
    public String startTime, endTime;

    public String getName() {
        return name;
    }

    public float getThreshHold() {
        return threshHold;
    }

    public void setThreshHold(float threshHold) {
        this.threshHold = threshHold;
    }

    public String getStartTime() {
        return startTime;
    }

    public void setStartTime(String startTime) {
        this.startTime = startTime;
    }

    public String getEndTime() {
        return endTime;
    }

    public void setEndTime(String endTime) {
        this.endTime = endTime;
    }

    public void setName(String name) {
        this.name = name;
    }


    @Override
    public String toString() {
        return name + "\nthreshHold = " + threshHold + "\nstart: " + startTime + "\nend: " + endTime + "\n";
    }
}
