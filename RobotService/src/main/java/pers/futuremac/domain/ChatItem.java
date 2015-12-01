package pers.futuremac.domain;


import java.util.HashMap;

/**
 * Created by 前程 on 2015/7/17.
 * Class for Socket Server
 */
public class ChatItem {
    public String from;
    public String text;
    public String to;

    public ChatItem(){
        from = text = to = "";
    }


    public void setFromName(String from){
        this.from = from;
    }

    public void setText(String text){
        this.text = text;
    }

    public void setToName(String to){
        this.to = to;
    }

    public HashMap<String,String> toHashMap(){
        HashMap<String,String> cmap = new HashMap<String, String>();
        cmap.put("from",from);
        cmap.put("to",to);
        cmap.put("text",text);
        return cmap;
    }

    @Override
    public String toString(){
        return "msg from: " + from + " to: " +to +" text: " +text;
    }

}
