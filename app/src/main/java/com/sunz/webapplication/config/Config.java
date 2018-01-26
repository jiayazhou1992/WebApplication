package com.sunz.webapplication.config;

import com.sunz.webapplication.utils.SDCardUtils;

import java.io.File;

/**
 * Created by Administrator on 2018/1/14 0014.
 */

public class Config {

    public static String home_url = "http://111.72.252.163:8090/mobile/page/gude.jsp";

    public static String message_url = "http://111.72.252.163:8090/framework/query.do?search&k=b_m_message";

    public static String file_home = "ytdj";

    public static String file_download = file_home+"/download";

    public static String tag_download = "tag_download";

    public static File getFileHome(){
        File file = new File(SDCardUtils.getSDCardPath()+file_home);
        if (!file.exists()){
            file.mkdir();
        }
        return file;
    }

    public static File getFileDownLoad(){
        File file = new File(SDCardUtils.getSDCardPath()+file_download);
        if (!file.exists()){
            file.mkdir();
        }
        return file;
    }
}
