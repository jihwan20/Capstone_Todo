package com.bjh.todo;

    public class ScheduleDTO {
        private int scheduleId;
        private String scheduleDate;
        private String scheduleText;
        private int userNoFk;
        private String startTime; // 시작 시간
        private String endTime; // 종료 시간
        private String location; // 위치 정보

        public ScheduleDTO(int scheduleId, String scheduleDate, String scheduleText, int userNoFk, String startTime, String endTime, String location) {
            this.scheduleId = scheduleId;
            this.scheduleDate = scheduleDate;
            this.scheduleText = scheduleText;
            this.userNoFk = userNoFk;
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

        public int getUserNoFk() {
            return userNoFk;
        }

        public void setUserNoFk(int userNoFk) {
            this.userNoFk = userNoFk;
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

