package com.jun.vacancyclassroom.item;

public class Lecture {
    String code,title,classroom,time;
    String unit;//학점
    String prof_nm;//강의교수
    String lect_quota;//수강정원
    String lect_req_cnt;//수강신청인원
    private int button_color;

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

    public String getUnit() {
        return unit;
    }

    public void setUnit(String unit) {
        this.unit = unit;
    }

    public String getProf_nm() {
        return prof_nm;
    }

    public void setProf_nm(String prof_nm) {
        this.prof_nm = prof_nm;
    }

    public String getLect_quota() {
        return lect_quota;
    }

    public void setLect_quota(String lect_quota) {
        this.lect_quota = lect_quota;
    }

    public String getLect_req_cnt() {
        return lect_req_cnt;
    }

    public void setLect_req_cnt(String lect_req_cnt) {
        this.lect_req_cnt = lect_req_cnt;
    }

    public int getButton_color() {
        return button_color;
    }

    public void setButton_color(int button_color) {
        this.button_color = button_color;
    }

    public Lecture() {
    }

    public Lecture(String code, String title, String classroom, String time)
    {
        this.code = code;
        this.title = title;
        this.classroom = classroom;
        this.time = time;
    }

    public Lecture(String code, String title, String classroom, String time, String unit, String prof_nm, String lect_quota, String lect_req_cnt) {
        this.code = code;
        this.title = title;
        this.classroom = classroom;
        this.time = time;
        this.unit = unit;
        this.prof_nm = prof_nm;
        this.lect_quota = lect_quota;
        this.lect_req_cnt = lect_req_cnt;
    }


}
