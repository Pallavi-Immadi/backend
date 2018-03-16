package models;


import com.fasterxml.jackson.annotation.JsonIgnore;
import com.sun.org.apache.xpath.internal.operations.Bool;

import javax.persistence.*;


@Entity
public class User{

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public enum Role {
        Admin,
        User
    }


    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    Integer id;


    @Basic
    String uname;

    @Basic
    String pwd;

    @Basic
    Role role;



    @Basic
    Long phone;

    @Basic
    String proname;

    @Basic
    String token;
    @Basic
    Long threshold;

    @Basic
    String salt;

    @Basic
    String reftoken;




    /*@Basic
    Boolean verified;
    */


    public User(Integer id, String uname, String pwd, Role role, Long phone, String proname, String token, Long threshold, String salt, String reftoken) {
        this.id = id;
        this.uname = uname;
        this.pwd = pwd;
        this.role = role;
        this.phone = phone;
        this.proname = proname;
        this.token = token;
        this.threshold = threshold;
        this.salt = salt;
        this.reftoken = reftoken;
      //  this.verified = verified;
    }


    public User(String uname){
        this.uname = uname;
    }

    public Long getThreshold() {

        return threshold;
    }

    public void setThreshold(Long threshold) {
        this.threshold = threshold;
    }

    public String getSalt() {
        return salt;
    }

    public void setSalt(String salt) {
        this.salt = salt;
    }

    public String getReftoken() {
        return reftoken;
    }

    public void setReftoken(String reftoken) {
        this.reftoken = reftoken;
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

    public Role getRole() {
        return role;
    }

    public void setRole(Role role) {
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

    public void setToken(String token){this.token = token; }



   /* public Boolean getVerified() {
        return verified;
    }

    public void setVerified(Boolean verified) {
        this.verified = verified;
    }
    */

    @JsonIgnore
    public String getToken(){ return  token; }


}
