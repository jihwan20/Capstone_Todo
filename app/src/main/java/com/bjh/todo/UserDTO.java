package com.bjh.todo;

public class UserDTO {
    private String userId;  // 사용자 ID
    private String userPw;  // 사용자 비밀번호

    // 생성자
    public UserDTO(String userId, String userPw) {
        this.userId = userId;
        this.userPw = userPw;
    }

    // Getter와 Setter
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
