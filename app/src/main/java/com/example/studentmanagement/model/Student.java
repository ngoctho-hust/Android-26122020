package com.example.studentmanagement.model;

public class Student {
    int id;
    String mssv;
    String hoten;
    String ngaysinh;
    String email;
    String diachi;

    public Student(int id, String mssv, String hoten, String ngaysinh, String email, String diachi) {
        this.id = id;
        this.mssv = mssv;
        this.hoten = hoten;
        this.ngaysinh = ngaysinh;
        this.email = email;
        this.diachi = diachi;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getMssv() {
        return mssv;
    }

    public void setMssv(String mssv) {
        this.mssv = mssv;
    }

    public String getHoten() {
        return hoten;
    }

    public void setHoten(String hoten) {
        this.hoten = hoten;
    }

    public String getNgaysinh() {
        return ngaysinh;
    }

    public void setNgaysinh(String ngaysinh) {
        this.ngaysinh = ngaysinh;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getDiachi() {
        return diachi;
    }

    public void setDiachi(String diachi) {
        this.diachi = diachi;
    }
}
