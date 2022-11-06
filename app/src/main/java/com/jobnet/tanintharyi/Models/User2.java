package com.jobnet.tanintharyi.Models;

public class User2 {
    String uid;
    String name;
    String email;
    String profile;

    public String getProfile() {
        return profile;
    }

    public void setProfile(String profile) {
        this.profile = profile;
    }

    public User2(){}

    public String getUid() {
        return uid;
    }

    public void setUid(String uid) {
        this.uid = uid;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String toString(){
        return "User2{"+
                "uid='"+uid+'\''+
                ", name='"+name+'\''+
                ", email='"+email+'\''+
                ", profile='"+profile+'\''+
                '}';
    }


}
