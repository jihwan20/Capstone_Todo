package com.bjh.todo;

public class ScheduleDTO {
    private int scheduleId;
    private String scheduleDate;
    private String scheduleText;
    private int userNoFk; // 사용자 번호 외래 키

    public ScheduleDTO(int scheduleId, String scheduleDate, String scheduleText, int userNoFk) {
        this.scheduleId = scheduleId;
        this.scheduleDate = scheduleDate;
        this.scheduleText = scheduleText;
        this.userNoFk = userNoFk;
    }

    // Getter와 Setter
    public int getScheduleId() {
        return scheduleId;
    }

    public void setScheduleId(int scheduleId) {
        this.scheduleId = scheduleId;
    }

    public String getScheduleDate() {
        return scheduleDate;
    }

    public void setScheduleDate(String scheduleDate) {
        this.scheduleDate = scheduleDate;
    }

    public String getScheduleText() {
        return scheduleText;
    }

    public void setScheduleText(String scheduleText) {
        this.scheduleText = scheduleText;
    }

    public int getUserNoFk() {
        return userNoFk;
    }

    public void setUserNoFk(int userNoFk) {
        this.userNoFk = userNoFk;
    }
}
