package com.example.journalentry.getapi;

import com.google.gson.annotations.SerializedName;

import java.lang.reflect.Array;
import java.util.List;

public class Post {
    @SerializedName("text")
    public String jtext;
    @SerializedName("intents")
    public Object jintents;
    @SerializedName("entities")
    public Object jentities;
    @SerializedName("traits")
    public Object jtraits;

//    public static class Datum1{
//        @SerializedName("Abstract:Feeling")
//        private List<Datum4> feelings = null;
//        @SerializedName("Abstract:State")
//        private List<Datum4> state = null;
//        @SerializedName("Person:Person")
//        private List<Datum4> person = null;
//        @SerializedName("Physical:Physical")
//        private List<Datum4> physical = null;
//        @SerializedName("wit$reminder:reminder")
//        private List<Datum4> reminder = null;
//        @SerializedName("wit$agenda_entry:agenda_entry")
//        private List<Datum4> agenda = null;
//        @SerializedName("wit$datetime:datetime")
//        private List<Datum4> datetime = null;
//    }
//    public static class Datum2{
//        @SerializedName("name")
//        private String name;
//        @SerializedName("confidence")
//        private Float confidence;
//    }
//    public static class Datum3{
//        @SerializedName("wit$sentiment")
//        private List<Datum5> sentiment = null;
//    }
//    public static class Datum4{
//        @SerializedName("id")
//        private String id;
//        @SerializedName("name")
//        private String name;
//        @SerializedName("role")
//        private String role;
//        @SerializedName("start")
//        private Integer start;
//        @SerializedName("end")
//        private Integer end;
//        @SerializedName("body")
//        private String body;
//        @SerializedName("confidence")
//        private Float confidence;
//        @SerializedName("entities")
//        private List<String> entities = null;
//        @SerializedName("value")
//        private String value;
//        @SerializedName("type")
//        private String type;
//    }
//    public static class Datum5{
//        @SerializedName("id")
//        private String id;
//        @SerializedName("value")
//        private String value;
//        @SerializedName("wit$datetime:datetime")
//        private Float confidence;
//    }
}
