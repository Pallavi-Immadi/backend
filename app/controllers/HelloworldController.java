package controllers;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.JsonNode;
import play.Logger;
import play.libs.Json;
import play.mvc.Controller;
import play.mvc.Result;

public class HelloworldController extends Controller {

    public Result HelloWorld(){

        Logger.debug("Logging test");
        return ok("Hello World");

    }
    public Result hello(String name){
        return ok("hello"+name);
    }
    public Result hellos(String name,Integer count){
        StringBuffer sb=new StringBuffer();
        sb.append("Hello");
        for (int i=0;i<count;i++)
        {
            sb.append(name+" ");

        }
        return ok(sb.toString());
    }
    public Result Greeting(){
        final JsonNode jsonnode=request().body().asJson();
        final String firstname=jsonnode.get("firstname").asText();
        final String lastname=jsonnode.get("lastname").asText();
        return ok("greetings"+firstname+" "+lastname);
    }
    static class Person{
        private String firstname;
        private String lastname;

        @JsonProperty("first_name")
        public String getFirstname() {
            return firstname;
        }

        public void setFirst_name(String firstname) {
            this.firstname = firstname;
        }
        @JsonProperty("last_name")
        public String getLastname() {
            return lastname;
        }

        public void setLast_name(String lastname) {
            this.lastname = lastname;
        }
    }
    public Result me(){
        final Person me=new Person();
        me.firstname="Pallavi";
        me.lastname="Immadi";
        final JsonNode jsonnode= Json.toJson(me);
        return ok(jsonnode);
    }
}
