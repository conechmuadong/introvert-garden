package ie.app.models;

import java.sql.Time;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

import ie.app.api.FirebaseAPI;

public class IrrigationInformation {
    private String startTime, endTime, duration;
    private String _startDate, _startTime;

    private Date dateTime;
    public int amount = 100;
    private static SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
    public boolean irrigationCheck;

    @Override
    public String toString() {
        return "IrrigationInformation: " + startTime + " - " + endTime
                + ", auto is " + irrigationCheck + "\n";
    }

    public String getStartDate() throws ParseException {
        dateTime = dateFormat.parse(startTime);
        SimpleDateFormat dateFormatDate = new SimpleDateFormat("yyyy-MM-dd");
        String date = dateFormatDate.format(dateTime);
        _startDate = date;
        return date;
    }

    public String getStartTime() throws ParseException {
        dateTime = dateFormat.parse(startTime);
        SimpleDateFormat dateFormatTime = new SimpleDateFormat("HH:mm:ss");
        String time = dateFormatTime.format(dateTime); // định dạng đối tượng Date thành chuỗi định dạng giờ phút giây
        _startTime = time;
        return time;
    }

    public String getEndDate() throws ParseException {
        dateTime = dateFormat.parse(endTime);
        SimpleDateFormat dateFormatDate = new SimpleDateFormat("yyyy-MM-dd");
        String date = dateFormatDate.format(dateTime);
        return date;
    }

    public String getEndTime() throws ParseException {
        dateTime = dateFormat.parse(endTime);
        SimpleDateFormat dateFormatTime = new SimpleDateFormat("HH:mm:ss");
        String time = dateFormatTime.format(dateTime); // định dạng đối tượng Date thành chuỗi định dạng giờ phút giây
        return time;
    }

    public String getDuration() {
        return duration;
    }

    public boolean isIrrigationCheck() {
        return irrigationCheck;
    }

    public void setStartTime(String startTime) throws ParseException {
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

    public void setNewStartDate(String newStartDate, String field) {
        this._startDate = newStartDate;
        this.startTime = newStartDate + " " + _startTime;
        FirebaseAPI.insert("user", field, startTime);
    }

    public void setNewStartTime(String newStartTime, String field) {
        this._startTime = newStartTime;
        this.startTime = _startDate + " " + newStartTime;
        FirebaseAPI.insert("user", field, startTime);
    }
}

/* TODO
- Lấy lượng nước tưới được nhập
- Tính toán lại thời gian kết thúc tưới
- Cài đặt chế độ Auto không cho phép chỉnh sửa
- Cập nhật chế độ manual - auto lên database
*/