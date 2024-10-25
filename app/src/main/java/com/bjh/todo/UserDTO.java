package com.bjh.todo;

public class UserDTO {
    private int userNo;
    private String userId;
    private String userPw;

    public UserDTO(int userNo, String userId, String userPw) {
        this.userNo = userNo;
        this.userId = userId;
        this.userPw = userPw;
    }
    // Getter와 Setter
    public int getUserNo() {
        return userNo;
    }

    public void setUserNo(int userNo) {
        this.userNo = userNo;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getUserPw() {
        return userPw;
    }

    public void setUserPw(String userPw) {
        this.userPw = userPw;
    }
}

