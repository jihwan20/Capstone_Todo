package com.bjh.todo;

public class ScheduleDTO {
    private int scheduleId;         // 일정 ID
    private String scheduleDate;    // 일정 날짜
    private String scheduleText;    // 일정 내용
    private String userId;          // 사용자 ID (외래 키)
    private String startTime;       // 시작 시간
    private String endTime;         // 종료 시간
    private String location;        // 위치 정보

    public ScheduleDTO(int scheduleId, String scheduleDate, String scheduleText, String userId, String startTime, String endTime, String location) {
        this.scheduleId = scheduleId;
        this.scheduleDate = scheduleDate;
        this.scheduleText = scheduleText;
        this.userId = userId;      // 사용자 ID로 수정
        this.startTime = startTime;
        this.endTime = endTime;
        this.location = location;
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

    public String getUserId() {          // 사용자 ID getter
        return userId;
    }

    public void setUserId(String userId) { // 사용자 ID setter
        this.userId = userId;
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

    public String getLocation() {
        return location;
    }

    public void setLocation(String location) {
        this.location = location;
    }
}
