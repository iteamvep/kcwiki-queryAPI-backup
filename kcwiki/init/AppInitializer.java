/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.kcwiki.init;

import java.io.File;
import java.io.UnsupportedEncodingException;
import java.util.Date;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.TimeUnit;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;

import org.kcwiki.redis.JedisPoolUtils;
import org.kcwiki.tools.constant;
import static org.kcwiki.tools.constant.FILESEPARATOR;
/**
 *
 * @author iTeam_VEP
 */
public class AppInitializer implements ServletContextListener {

    /**
     * @return the isInit
     */
    public static boolean isInit() {
        return isInit;
    }
    private static boolean isInit = false;
    
    public static boolean initApp() {
        if(!isInit()) {
            try {
                String classPath = AppInitializer.class.getResource("/").toString();
                String path = null;
                if(System.getProperty("os.name").toLowerCase().startsWith("windows")){
                    path = java.net.URLDecoder.decode(classPath.substring(6, classPath.length()), "utf-8");
                } else {
                    path = java.net.URLDecoder.decode(classPath.substring(5, classPath.length()), "utf-8");
                }
                int lastIndex = path.lastIndexOf("/WEB-INF/classes/") ;
                if(lastIndex == -1) {
                    constant.setWebrootPath(path.substring(0, path.length()-1));
                } else {
                    constant.setWebrootPath(path.substring(0, lastIndex));
                }
                constant.setLocalPath(java.net.URLDecoder.decode(constant.getWebrootPath() + FILESEPARATOR + "custom", "utf-8"));
                constant.setDataPath(constant.getWebrootPath() + FILESEPARATOR + "WEB-INF" + FILESEPARATOR + "custom" + FILESEPARATOR + "data");
                File dataPath = new File(constant.getDataPath());
                if(!dataPath.exists())
                    dataPath.mkdirs();
                if(org.kcwiki.spider.akashilist.mainpage.init())
                    isInit = true;
            } catch (UnsupportedEncodingException ex) {
                Logger.getLogger(AppInitializer.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
        new constant().getFilterFilePath();
        return isInit();
    }

    @Override
    public void contextInitialized(ServletContextEvent sce) {
        //System.out.println("程序开始初始化： "+new Date());
        //long startTime=System.nanoTime();   
        new Thread () {
            public void run() {
                AppInitializer.initApp();
                System.gc();
            }
        }.start();
        
        //long endTime=System.nanoTime(); 
        //System.out.println("程序初始化完成： "+new Date());
        //System.out.println("程序运行时间： "+(endTime-startTime)+"ns");
    }

    @Override
    public void contextDestroyed(ServletContextEvent sce) {
        //JedisPoolUtils.getJedis().flushDB();
        JedisPoolUtils.close(JedisPoolUtils.getJedis());
        
        System.out.println("context has been Destroyed.");
    }
}
