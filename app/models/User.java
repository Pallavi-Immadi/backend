package models;


import javax.persistence.*;


@Entity
public class User{

    @Id
    String uname;

    @Basic
    String pwd;

    @Basic
    String role;

    @Basic
    Long phone;

    @Basic
    String proname;



    public User(String uname, String pwd,String role,Long phone,String proname) {
        this.uname = uname;
        this.pwd = pwd;
        this.role=role;
        this.phone=phone;
        this.proname=proname;
    }

    public User(){}


    public String getUname() {
        return uname;
    }

    public void setUname(String uname) {
        this.uname = uname;
    }

    public String getPwd() {
        return pwd;
    }

    public void setPwd(String pwd) {
        this.pwd = pwd;
    }

    public String getRole() {
        return role;
    }

    public void setRole(String role) {
        this.role = role;
    }

    public Long getPhone() {
        return phone;
    }

    public void setPhone(Long phone) {
        this.phone = phone;
    }

    public String getProname() {
        return proname;
    }

    public void setProname(String proname) {
        this.proname = proname;
    }
}
