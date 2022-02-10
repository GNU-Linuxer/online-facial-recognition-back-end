package com.davidxie.Online.Facial.Recognition;

public class User {
    private int userID;
    private String userName, email;
    byte[] image;

    public User(int userID, String userName, String email, byte[] image) {
        this.userID = userID;
        this.userName = userName;
        this.email = email;
        this.image = image;
    }

    @Override
    public String toString() {
        return String.format(
                "Customer[id=%d, userName='%s', email='%s']",
                userID, userName, email);
    }

    // getters & setters omitted for brevity
}