/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.kcwiki.zh.init;

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

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
    
    public static void init() {
        // 读取配置文件    
        Properties prop = new Properties();
        InputStream in = Object.class.getResourceAsStream("/storage.properties");
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
            //System.out.println(prop.getProperty("").trim());
        } catch (IOException e) {
            e.printStackTrace();
        }
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
}
