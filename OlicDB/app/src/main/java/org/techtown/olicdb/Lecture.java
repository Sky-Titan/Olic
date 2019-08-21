package org.techtown.olicdb;

public class Lecture {
    String code,title,classroom,time;

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getClassroom() {
        return classroom;
    }

    public void setClassroom(String classroom) {
        this.classroom = classroom;
    }

    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }

    public Lecture(String code, String title, String classroom, String time){
        setCode(code);
        setClassroom(classroom);
        setTitle(title);
        setTime(time);
    }
}
