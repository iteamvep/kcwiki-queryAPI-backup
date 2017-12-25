/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.kcwiki.init;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.util.Properties;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.kcwiki.tools.constant;
import static org.kcwiki.tools.constant.FILESEPARATOR;

/**
 *
 * @author VEP
 */
public class MainServer {
    private static String azurestorName;
    private static String azurestorKey;
    private static String azurestorBucketName;
    private static String azurestorBucketArea;
    private static String qingstorName;
    private static String qingstorKey;
    private static String qingstorBucketName;
    private static String qingstorBucketArea;
    
    private static String OAuthConsumerKey;
    private static String OAuthConsumerSecret;
    private static String OAuthAccessToken;
    private static String OAuthAccessTokenSecret;
    
    private static String webrootPath;
    private static String publishFolder;
    private static String dataFolder;
    private static String classFolder;
    
    private static boolean useproxy = true;
    //private static String proxyhost = "127.0.0.1";
    //private static int proxyport = 1090;
    private static String proxyhost = "192.168.20.3";
    private static int proxyport = 8118;
    
    private static boolean isInis = false;
    private static boolean debug = false;
    private static boolean hasStorage = false;
    private static boolean hasPreset = false;
    private static boolean hasTwitter = false;
    
    public static void init() {
        
        String classPath = MainServer.class.getResource("/").toString();
        String path = null;
        try {
            if(System.getProperty("os.name").toLowerCase().startsWith("windows")){
                path = java.net.URLDecoder.decode(classPath.substring(6, classPath.length()), "utf-8");
            } else {
                path = java.net.URLDecoder.decode(classPath.substring(5, classPath.length()), "utf-8");
            }
        
            int lastIndex = path.lastIndexOf("/WEB-INF/classes/") ;
            if(lastIndex == -1) {
                webrootPath = path.substring(0, path.length() -1);
            } else {
                webrootPath = path.substring(0, lastIndex);
            }
            String publicPath = java.net.URLDecoder.decode(getWebrootPath() , "utf-8");
            String privatePath = java.net.URLDecoder.decode(getWebrootPath() + FILESEPARATOR + "WEB-INF" + FILESEPARATOR + "custom", "utf-8"); 
            MainServer.publishFolder = publicPath + FILESEPARATOR + constant.getPublishName();
            MainServer.dataFolder = privatePath + FILESEPARATOR + constant.getDataName();
        } catch (UnsupportedEncodingException ex) {
            Logger.getLogger(MainServer.class.getName()).log(Level.SEVERE, null, ex);
        }
        
        // 读取配置文件    
        Properties prop = new Properties();
        
        if(MainServer.class.getResource("/storage.properties") != null) {
            InputStream in = MainServer.class.getResourceAsStream("/storage.properties");
            try {
                prop.load(in);
                MainServer.azurestorName = prop.getProperty("azurestorName").trim();
                MainServer.azurestorKey = prop.getProperty("azurestorKey").trim();
                MainServer.azurestorBucketName = prop.getProperty("azurestorBucketName").trim();
                MainServer.azurestorBucketArea = prop.getProperty("azurestorBucketArea").trim();
                MainServer.qingstorName = prop.getProperty("qingstorName").trim();
                MainServer.qingstorKey = prop.getProperty("qingstorKey").trim();
                MainServer.qingstorBucketName = prop.getProperty("qingstorBucketName").trim();
                MainServer.qingstorBucketArea = prop.getProperty("qingstorBucketArea").trim();
                hasStorage = true;
                //System.out.println(prop.getProperty("").trim());
            } catch (IOException e) {
                e.printStackTrace();
                Logger.getLogger(MainServer.class.getName()).log(Level.SEVERE, null, e);
            }
        }
        if(MainServer.class.getResource("/preset.properties") != null) {
            InputStream in = MainServer.class.getResourceAsStream("/preset.properties");
            try {
                prop.load(in);
                MainServer.useproxy = prop.getProperty("useproxy").trim().equals("1");
                MainServer.proxyhost = prop.getProperty("proxyhost").trim();
                MainServer.proxyport = Integer.valueOf(prop.getProperty("proxyport").trim());
                hasPreset = true;
            } catch (IOException e) {
                e.printStackTrace();
                Logger.getLogger(MainServer.class.getName()).log(Level.SEVERE, null, e);
            }
        }
        if(MainServer.class.getResource("/twitter.properties") != null) {
            InputStream in = MainServer.class.getResourceAsStream("/twitter.properties");
            try {
                prop.load(in);
                MainServer.OAuthConsumerKey = prop.getProperty("OAuthConsumerKey").trim();
                MainServer.OAuthConsumerSecret = prop.getProperty("OAuthConsumerSecret").trim();
                MainServer.OAuthAccessToken = prop.getProperty("OAuthAccessToken").trim();
                MainServer.OAuthAccessTokenSecret = prop.getProperty("OAuthAccessTokenSecret").trim();
                hasTwitter = true;
            } catch (IOException e) {
                e.printStackTrace();
                Logger.getLogger(MainServer.class.getName()).log(Level.SEVERE, null, e);
            }
        }
        
        if(!(new File(MainServer.publishFolder).exists() || new File(MainServer.publishFolder).isDirectory())){new File(MainServer.getPublishFolder()).mkdirs();}
        if(!(new File(MainServer.dataFolder).exists() || new File(MainServer.dataFolder).isDirectory())){new File(MainServer.getDataFolder()).mkdirs();}
        MainServer.isInis = true;
    }
    
    public static void main(String[] args) {
        init();
    }

    /**
     * @return the azurestorName
     */
    public static String getAzurestorName() {
        return azurestorName;
    }

    /**
     * @return the azurestorKey
     */
    public static String getAzurestorKey() {
        return azurestorKey;
    }

    /**
     * @return the azurestorBucketName
     */
    public static String getAzurestorBucketName() {
        return azurestorBucketName;
    }

    /**
     * @return the azurestorBucketArea
     */
    public static String getAzurestorBucketArea() {
        return azurestorBucketArea;
    }

    /**
     * @return the qingstorName
     */
    public static String getQingstorName() {
        return qingstorName;
    }

    /**
     * @return the qingstorKey
     */
    public static String getQingstorKey() {
        return qingstorKey;
    }

    /**
     * @return the qingstorBucketName
     */
    public static String getQingstorBucketName() {
        return qingstorBucketName;
    }

    /**
     * @return the qingstorBucketArea
     */
    public static String getQingstorBucketArea() {
        return qingstorBucketArea;
    }

    /**
     * @return the webrootPath
     */
    public static String getWebrootPath() {
        return webrootPath;
    }

    /**
     * @return the publishFolder
     */
    public static String getPublishFolder() {
        return publishFolder;
    }

    /**
     * @return the dataFolder
     */
    public static String getDataFolder() {
        return dataFolder;
    }

    /**
     * @return the proxyhost
     */
    public static String getProxyhost() {
        return proxyhost;
    }

    /**
     * @return the proxyport
     */
    public static int getProxyport() {
        return proxyport;
    }

    /**
     * @return the useproxy
     */
    public static boolean isUseproxy() {
        return useproxy;
    }

    /**
     * @param aPublishFolder the publishFolder to set
     */
    public static void setPublishFolder(String aPublishFolder) {
        publishFolder = aPublishFolder;
    }

    /**
     * @return the classFolder
     */
    public static String getClassFolder() {
        return classFolder;
    }

    /**
     * @return the isInis
     */
    public static boolean isIsInis() {
        return isInis;
    }

    /**
     * @return the debug
     */
    public static boolean isDebug() {
        return debug;
    }

    /**
     * @return the OAuthConsumerKey
     */
    public static String getOAuthConsumerKey() {
        return OAuthConsumerKey;
    }

    /**
     * @return the OAuthConsumerSecret
     */
    public static String getOAuthConsumerSecret() {
        return OAuthConsumerSecret;
    }

    /**
     * @return the OAuthAccessToken
     */
    public static String getOAuthAccessToken() {
        return OAuthAccessToken;
    }

    /**
     * @return the OAuthAccessTokenSecret
     */
    public static String getOAuthAccessTokenSecret() {
        return OAuthAccessTokenSecret;
    }

    /**
     * @return the hasStorage
     */
    public static boolean isHasStorage() {
        return hasStorage;
    }

    /**
     * @return the hasPreset
     */
    public static boolean isHasPreset() {
        return hasPreset;
    }

    /**
     * @return the hasTwitter
     */
    public static boolean isHasTwitter() {
        return hasTwitter;
    }

}
