package com.example.calendarmanagerbeta;

public class mailEvent {

    private String subject;
    private String body;
    private String webLink;



    public mailEvent(String subject, String body, String webLink) {
        this.subject = subject;
        this.body = body;
        this.webLink = webLink;
    }

    public String getSubject() {
        return subject;
    }

    /*public void setText(String text) {
        this.text = text;
    }*/

    public String getBody() {
        return body;
    }

    /*public void setName(String name) {
        this.name = name;
    }*/

    public String getWebLink() {
        return webLink;
    }

    /*public void setPhotoUrl(String photoUrl) {
        this.photoUrl = photoUrl;
    }*/
}