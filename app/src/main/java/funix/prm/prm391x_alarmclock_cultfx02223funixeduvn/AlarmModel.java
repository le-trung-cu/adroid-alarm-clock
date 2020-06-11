package funix.prm.prm391x_alarmclock_cultfx02223funixeduvn;

public class AlarmModel {
    private int id;
    private int hour;

    private int minute;
    private boolean isOn;

    public AlarmModel(int id, int hour, int minute, boolean isOn) {
        this.id = id;
        this.hour = hour;
        this.minute = minute;
        this.isOn = isOn;
    }
    public AlarmModel(int hour, int minute, boolean isOn) {
        this.hour = hour;
        this.minute = minute;
        this.isOn = isOn;
    }

    public void setId(int id){
        this.id = id;
    }
    public void setHour(int hour) {
        this.hour = hour;
    }

    public void setMinute(int minute) {
        this.minute = minute;
    }

    public void setOn(boolean on) {
        isOn = on;
    }


    public int getId() {
        return id;
    }

    public int getHour() {
        return hour;
    }

    public int getMinute() {
        return minute;
    }

    public boolean isOn() {
        return isOn;
    }
}
