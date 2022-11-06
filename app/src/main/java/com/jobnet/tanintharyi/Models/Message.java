package com.jobnet.tanintharyi.Models;

public class Message {
    String message;
    String name;
    String profile;
    String key;


    public String getProfile() {
        return profile;
    }

    public void setProfile(String profile) {
        this.profile = profile;
    }


    public  Message(){}

    public Message(String message,String name,String profile){
        this.message=message;
        this.name=name;
        this.profile=profile;
        this.key=key;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getKey() {
        return key;
    }

    public void setKey(String key) {
        this.key = key;
    }

    public String toString(){
        return "Message{"+
                "message='"+message+'\''+
                ", name='"+name+'\''+
                ", profile='"+profile+'\''+
                ", key='"+key+'\''+
                '}';
    }


}
