package com.jobnet.tanintharyi;

public class User {
    private int id;
    private String name;
    private String phone;
    private String gender;
    private String profile;

    public String getProfile() {
        return profile;
    }

    public void setProfile(String profile) {
        this.profile = profile;
    }



    public User(int id, String name, String phone) {
        this.id = id;
        this.phone = phone;
        this.gender = gender;
        this.name = name;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }


    public String getGender() {
        return gender;
    }

    public void setGender(String gender) {
        this.gender= gender;
    }
}
