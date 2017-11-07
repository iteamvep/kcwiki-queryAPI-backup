/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.kcwiki.spider.akashilist;

import java.util.ArrayList;
import java.util.HashMap;

/**
 *
 * @author iTeam_VEP
 */
public class itemBean {

    /**
     * @return the id
     */
    public String getId() {
        return id;
    }

    /**
     * @return the title
     */
    public String getTitle() {
        return title;
    }

    /**
     * @return the imgurl
     */
    public String getImgurl() {
        return imgurl;
    }

    /**
     * @return the status
     */
    public HashMap getStatus() {
        return status;
    }

    /**
     * @return the supportShip
     */
    public ArrayList getSupportShip() {
        return supportShip;
    }

    /**
     * @param id the id to set
     */
    public void setId(String id) {
        this.id = id;
    }

    /**
     * @param title the title to set
     */
    public void setTitle(String title) {
        this.title = title;
    }

    /**
     * @param imgurl the imgurl to set
     */
    public void setImgurl(String imgurl) {
        this.imgurl = imgurl;
    }

    /**
     * @param status the status to set
     */
    public void setStatus(HashMap status) {
        this.status = status;
    }

    /**
     * @param supportShip the supportShip to set
     */
    public void setSupportShip(ArrayList supportShip) {
        this.supportShip = supportShip;
    }
        private String id;
        private String title;
        private String imgurl;
        private HashMap status;
        private ArrayList supportShip;
        
}
