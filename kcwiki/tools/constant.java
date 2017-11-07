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

    
}
