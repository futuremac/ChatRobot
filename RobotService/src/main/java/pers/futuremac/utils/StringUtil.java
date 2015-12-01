package pers.futuremac.utils;

import org.apache.commons.codec.binary.Base64;
/**
 * Created by 前程 on 2015/7/27.
 */
public class StringUtil {

    public static String getBASE64String(String s){
        if(s == null)
            return null;
        String str = (new Base64(true)).encodeAsString(s.getBytes());
        str = str.replace("\r\n","");
        return str;

    }


    public  static void main(String[] args){
       String aa = getBASE64String("image1.jpg");
       System.out.println(aa);
    }
}
