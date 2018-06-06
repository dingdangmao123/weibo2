package com.gapcoder.weico.Message.Model;

import com.gapcoder.weico.General.SysMsg;
import com.gapcoder.weico.UserList.UserListModel;

import java.util.LinkedList;

/**
 * Created by gapcoder on 2018/6/4.
 */

public class CoverModel extends SysMsg {

    private LinkedList<CoverModel.InnerBean> inner;


    public LinkedList<CoverModel.InnerBean> getInner() {
        return inner;
    }

    public void setInner(LinkedList<CoverModel.InnerBean> inner) {
        this.inner = inner;
    }

    public static class InnerBean {


        private int id;
        private int uid;
        private String name;
        private String face;
        private int time;

        public int getId() {
            return id;
        }

        public void setId(int id) {
            this.id = id;
        }

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }

        public String getFace() {
            return face;
        }

        public void setFace(String face) {
            this.face = face;
        }

        public int getTime() {
            return time;
        }

        public void setTime(int time) {
            this.time = time;
        }

        public int getUid() {
            return uid;
        }

        public void setUid(int uid) {
            this.uid = uid;
        }
    }
}
