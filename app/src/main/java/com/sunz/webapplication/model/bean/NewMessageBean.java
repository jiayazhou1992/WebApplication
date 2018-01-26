package com.sunz.webapplication.model.bean;

import java.io.Serializable;
import java.util.List;

/**
 * Created by Administrator on 2018/1/26 0026.
 */

public class NewMessageBean implements Serializable{

    /**
     * total : 4
     * data : [{"id":"4BE6A3B178874C84859ADD63E9E63B55","category":"ytdj_msgtype_07","businessid":"6caf6356596447fa9a8657e37858bdf7","msgtitle":"通知","msgcontent":"789...","mobiletemplate":"5FE04A5FDC58D94EE05110AC09C84970","targetuser":"8D22D5AE8D24405EB0BA6446719F11D2","source":"中共贵溪市文化广电新闻出版局机关支部委员会","createtime":"2017-12-29 15:35:13","readtime":"2018-01-26 23:03:23"},{"id":"120097BC7E094B15A7A8EEFF807576D0","category":"ytdj_msgtype_05","businessid":null,"msgtitle":"党费提醒","msgcontent":"请于2018-01-01前缴纳本月党费","mobiletemplate":"5FE04A5FDC58D94EE05010AC09C84967","targetuser":"8D22D5AE8D24405EB0BA6446719F11D2","source":"中共贵溪市文化广电新闻出版局机关支部委员会","createtime":"2018-01-01 07:12:37","readtime":"2018-01-26 23:02:46"},{"id":"B37C997702F64DFF9FE8A4AC44A8DE92","category":"ytdj_msgtype_06","businessid":"6caf6356596447fa9a8657e37858bdf7","msgtitle":"活动提醒","msgcontent":"12月29日15:31,\"123\"将在123召开,请准时参加!","mobiletemplate":"5FE04A5FDC58D94EE05010AC09C84966","targetuser":"8D22D5AE8D24405EB0BA6446719F11D2","source":"系统","createtime":"2017-12-29 15:30:04","readtime":"2018-01-26 23:02:44"},{"id":"49C6184EC8644414971DEB7CCFC1DA6C","category":"ytdj_msgtype_06","businessid":"d1e72f2c7b6845098a7dec025a5ae0c0","msgtitle":"活动提醒","msgcontent":"12月29日13:24,\"123\"将在鹰潭召开,请准时参加!","mobiletemplate":"5FE04A5FDC58D94EE05010AC09C84966","targetuser":"8D22D5AE8D24405EB0BA6446719F11D2","source":"系统","createtime":"2017-12-29 14:10:20","readtime":"2018-01-26 20:58:11"}]
     * msg : null
     * success : true
     */

    private int total;
    private String msg;
    private boolean success;
    private List<DataBean> data ;

    public NewMessageBean() {
    }

    public int getTotal() {
        return total;
    }

    public void setTotal(int total) {
        this.total = total;
    }

    public String getMsg() {
        return msg;
    }

    public void setMsg(String msg) {
        this.msg = msg;
    }

    public boolean getSuccess() {
        return success;
    }

    public void setSuccess(boolean success) {
        this.success = success;
    }

    public List<DataBean> getData() {
        return data;
    }

    public void setData(List<DataBean> data) {
        this.data = data;
    }

    public static class DataBean implements Serializable{
        /**
         * id : 4BE6A3B178874C84859ADD63E9E63B55
         * category : ytdj_msgtype_07
         * businessid : 6caf6356596447fa9a8657e37858bdf7
         * msgtitle : 通知
         * msgcontent : 789...
         * mobiletemplate : 5FE04A5FDC58D94EE05110AC09C84970
         * targetuser : 8D22D5AE8D24405EB0BA6446719F11D2
         * source : 中共贵溪市文化广电新闻出版局机关支部委员会
         * createtime : 2017-12-29 15:35:13
         * readtime : 2018-01-26 23:03:23
         */

        private String id;
        private String category;
        private String businessid;
        private String msgtitle;
        private String msgcontent;
        private String mobiletemplate;
        private String targetuser;
        private String source;
        private String createtime;
        private String readtime;

        public DataBean() {
        }

        public String getId() {
            return id;
        }

        public void setId(String id) {
            this.id = id;
        }

        public String getCategory() {
            return category;
        }

        public void setCategory(String category) {
            this.category = category;
        }

        public String getBusinessid() {
            return businessid;
        }

        public void setBusinessid(String businessid) {
            this.businessid = businessid;
        }

        public String getMsgtitle() {
            return msgtitle;
        }

        public void setMsgtitle(String msgtitle) {
            this.msgtitle = msgtitle;
        }

        public String getMsgcontent() {
            return msgcontent;
        }

        public void setMsgcontent(String msgcontent) {
            this.msgcontent = msgcontent;
        }

        public String getMobiletemplate() {
            return mobiletemplate;
        }

        public void setMobiletemplate(String mobiletemplate) {
            this.mobiletemplate = mobiletemplate;
        }

        public String getTargetuser() {
            return targetuser;
        }

        public void setTargetuser(String targetuser) {
            this.targetuser = targetuser;
        }

        public String getSource() {
            return source;
        }

        public void setSource(String source) {
            this.source = source;
        }

        public String getCreatetime() {
            return createtime;
        }

        public void setCreatetime(String createtime) {
            this.createtime = createtime;
        }

        public String getReadtime() {
            return readtime;
        }

        public void setReadtime(String readtime) {
            this.readtime = readtime;
        }
    }
}
