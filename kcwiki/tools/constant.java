/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.kcwiki.tools;

import java.io.File;
import java.util.HashMap;

/**
 *
 * @author iTeam_VEP
 */
public class constant {

   
    public final static String LINESEPARATOR = System.getProperty("line.separator", "\n");
    public final static String FILESEPARATOR = File.separator;
    private static String webrootPath = null;
    private static String dataPath = null;
    private static String localPath = null;
    private final String itemsFilePath = getDataPath() + constant.FILESEPARATOR + "akashi.json";
    private final String filterFilePath = getDataPath() + constant.FILESEPARATOR + "akashiFilter.json";
    private static final String StartUrl = "https://acc.kcwiki.org/start2";
    //private static final String kcdata_ship = "http://kcwikizh.github.io/kcdata/ship/all.json";
    //private static final String kcdata_slotitem = "http://kcwikizh.github.io/kcdata/slotitem/all.json";
    private static final String kcdata_ship = "https://acc.kcwiki.org/kcdata/ship/all.json";
    private static final String kcdata_slotitem = "https://acc.kcwiki.org/kcdata/slotitem/all.json";
    
    /**
     * @return the webrootPath
     */
    public static String getWebrootPath() {
        return webrootPath;
    }

    /**
     * @param aWebrootPath the webrootPath to set
     */
    public static void setWebrootPath(String aWebrootPath) {
        webrootPath = aWebrootPath;
    }

    /**
     * @return the dataPath
     */
    public static String getDataPath() {
        return dataPath;
    }

    /**
     * @param aDataPath the dataPath to set
     */
    public static void setDataPath(String aDataPath) {
        dataPath = aDataPath;
    }

    /**
     * @return the localPath
     */
    public static String getLocalPath() {
        return localPath;
    }

    /**
     * @param aLocalPath the localPath to set
     */
    public static void setLocalPath(String aLocalPath) {
        localPath = aLocalPath;
    }

    /**
     * @return the itemsFilePath
     */
    public String getItemsFilePath() {
        return itemsFilePath;
    }

    /**
     * @return the filterFilePath
     */
    public String getFilterFilePath() {
        return filterFilePath;
    }

    /**
     * @return the StartUrl
     */
    public static String getStartUrl() {
        return StartUrl;
    }

    /**
     * @return the kcdata_ship
     */
    public static String getKcdata_ship() {
        return kcdata_ship;
    }

    /**
     * @return the kcdata_slotitem
     */
    public static String getKcdata_slotitem() {
        return kcdata_slotitem;
    }

    
}
