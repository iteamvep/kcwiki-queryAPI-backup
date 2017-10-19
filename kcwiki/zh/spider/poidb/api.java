/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.kcwiki.zh.spider.poidb;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONException;
import com.alibaba.fastjson.JSONObject;
import java.io.IOException;
import java.io.InputStream;
import static java.lang.Thread.sleep;
import java.net.SocketTimeoutException;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import javax.script.ScriptEngine;
import javax.script.ScriptEngineManager;
import javax.script.ScriptException;
import javax.swing.JOptionPane;
import jdk.nashorn.api.scripting.ScriptObjectMirror;
import net.sourceforge.htmlunit.corejs.javascript.NativeArray;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.jsoup.Jsoup;


public class api {
    
    //private static Map<String, org.apache.http.cookie.Cookie> cookies = new HashMap<String, org.apache.http.cookie.Cookie>();
    private static HttpUtil util = HttpUtil.getInstance();
    private static HashMap<String,Boolean> consistData = new HashMap<>();
    private static HashMap<String,String> MapNames = new HashMap<>();
    
    static {
        if(consistData.isEmpty()){
            new api().getConstantData("https://static.kcwiki.org/db/scripts/drop/constant.js");
        }
    }
    
    public boolean getMapData(String httpsUrl){
        LinkedHashMap<String, String> Headers = new LinkedHashMap();
        Headers.put("cache-control", "max-age=0");  
        Headers.put("upgrade-insecure-requests", "1"); 
        Headers.put("User-Agent","Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/57.0.2987.98 Safari/537.36");  
        Headers.put("Accept", "text/html,application/xhtml+xml,application/xml;q=0.9,image/webp,image/apng,*/*;q=0.8"); 
        Headers.put("DNT", "1");       
        Headers.put("Accept-Encoding", "gzip, deflate, sdch, br");  
        Headers.put("Accept-Language", "zh-CN,zh;q=0.8");

        try {
            try (CloseableHttpResponse response = (CloseableHttpResponse) util.doGet(httpsUrl,null,Headers)) {
                if(response.getStatusLine().getStatusCode()==500){
                    sleep(5*1000);
                    return false;
                }
                if(response.getStatusLine().getStatusCode()==200){
                    InputStream in=response.getEntity().getContent();
                    String retVal = HttpUtil.readStream(in, HttpUtil.defaultEncoding);  
                    in.close();
                    //System.out.println(retVal);
                    org.kcwiki.zh.utils.RWFile.writeLog(retVal,null);
                    JSONArray Jobj = (JSONArray) JSON.parseArray(retVal);
                    
                    return true;
                }
                //System.out.println(util.getCookie("www.dmm.com", null, null, null).toString());
                //System.out.println(retVal);
            }
        } catch (URISyntaxException ex) {
            ex.printStackTrace();
            Logger.getLogger(api.class.getName()).log(Level.SEVERE, null, ex);
            return false;
        } catch (IOException ex) {
            ex.printStackTrace();
            Logger.getLogger(api.class.getName()).log(Level.SEVERE, null, ex);
            return false;
        } catch (InterruptedException ex) {
            ex.printStackTrace();
            Logger.getLogger(api.class.getName()).log(Level.SEVERE, null, ex);
            return false;
        }
        return false;
    }
    
    public HashMap getDropData(String httpsUrl){
        LinkedHashMap<String, String> Headers = new LinkedHashMap();
        Headers.put("cache-control", "max-age=0");  
        Headers.put("upgrade-insecure-requests", "1"); 
        Headers.put("User-Agent","Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/57.0.2987.98 Safari/537.36");  
        Headers.put("Accept", "text/html,application/xhtml+xml,application/xml;q=0.9,image/webp,image/apng,*/*;q=0.8"); 
        Headers.put("DNT", "1");       
        Headers.put("Accept-Encoding", "gzip, deflate, sdch, br");  
        Headers.put("Accept-Language", "zh-CN,zh;q=0.8");

        try {
            try (CloseableHttpResponse response = (CloseableHttpResponse) util.doGet(httpsUrl,null,Headers)) {
                if(response.getStatusLine().getStatusCode()==500){
                    sleep(5*1000);
                    return null;
                }
                if(response.getStatusLine().getStatusCode()==403){
                    sleep(5*1000);
                    System.out.println("出现403错误");
                    return null;
                }
                if(response.getStatusLine().getStatusCode()==200){
                    InputStream in=response.getEntity().getContent();
                    String retVal = HttpUtil.readStream(in, HttpUtil.defaultEncoding);  
                    in.close();
                    //System.out.println(retVal);
                    org.kcwiki.zh.utils.RWFile.writeLog(retVal,null);
                    JSONObject Jobj = (JSONObject) JSON.parseObject(retVal).get("data");
                    HashMap<String,Object> rare = new LinkedHashMap<>();
                    HashMap<String,Object> notrare = new LinkedHashMap<>();
                    HashMap<String,Object> mapData = new LinkedHashMap<>();
                    HashMap<String,String> tmp = new LinkedHashMap<>();
                    for(String ship:Jobj.keySet()){
                        JSONObject dataList=(JSONObject) Jobj.get(ship);
                        tmp.put("ship", ship);
                        tmp.put("rate", String.valueOf(dataList.get("rate")));
                        if(consistData.containsKey(ship) && !consistData.get(ship)){
                            tmp.put("rare", String.valueOf("false"));
                            notrare.put(ship, tmp.clone());
                        }else{
                            tmp.put("rare", String.valueOf("true"));
                            rare.put(ship, tmp.clone());
                        }
                        tmp.clear();
                    }
                    if(!rare.isEmpty()){
                        mapData.put("rare", rare.clone());
                    }else{
                        mapData.put("rare", "Empty");
                    }
                    if(!notrare.isEmpty()){
                        mapData.put("notrare", notrare.clone());
                    }else{
                        mapData.put("notrare", "Empty");
                    }
                    rare.clear();
                    notrare.clear();
                    JSON.toJSONString(mapData.clone());
                    return mapData;
                }
            }
        } catch (URISyntaxException ex) {
            ex.printStackTrace();
            Logger.getLogger(api.class.getName()).log(Level.SEVERE, null, ex);
            return null;
        } catch (IOException ex) {
            ex.printStackTrace();
            Logger.getLogger(api.class.getName()).log(Level.SEVERE, null, ex);
            return null;
        } catch (InterruptedException ex) {
            ex.printStackTrace();
            Logger.getLogger(api.class.getName()).log(Level.SEVERE, null, ex);
            return null;
        }
        return null;
    }
    
    public boolean getConstantData(String httpsUrl){
        LinkedHashMap<String, String> Headers = new LinkedHashMap();
        Headers.put("cache-control", "max-age=0");  
        Headers.put("upgrade-insecure-requests", "1"); 
        Headers.put("User-Agent","Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/57.0.2987.98 Safari/537.36");  
        Headers.put("Accept", "text/html,application/xhtml+xml,application/xml;q=0.9,image/webp,image/apng,*/*;q=0.8"); 
        Headers.put("DNT", "1");       
        Headers.put("Accept-Encoding", "gzip, deflate, sdch, br");  
        Headers.put("Accept-Language", "zh-CN,zh;q=0.8");

        try {
            try (CloseableHttpResponse response = (CloseableHttpResponse) util.doGet(httpsUrl,null,Headers)) {
                if(response.getStatusLine().getStatusCode()==500){
                    sleep(5*1000);
                    return false;
                }
                if(response.getStatusLine().getStatusCode()==403){
                    sleep(5*1000);
                    System.out.println("出现403错误");
                    return false;
                }
                if(response.getStatusLine().getStatusCode()==200){
                    InputStream in=response.getEntity().getContent();
                    String retVal = HttpUtil.readStream(in, HttpUtil.defaultEncoding);  
                    //System.out.println(retVal);
                    retVal = retVal.replace("window.","");
                    org.kcwiki.zh.utils.RWFile.writeLog(retVal,null);
                    
                    //ScriptEngine engine = new ScriptEngineManager().getEngineByName("javascript");
                    ScriptEngineManager sem = new ScriptEngineManager();  
                    // 创建一个处理JavaScript的脚本引擎  
                    ScriptEngine engine = sem.getEngineByExtension("js");  
                    try {  
                        // 执行js公式  
                        engine.eval(retVal);  
                    } catch (ScriptException ex) {
                        ex.printStackTrace();  
                    }
                    ScriptObjectMirror ShipObjectMap = (ScriptObjectMirror) engine.get("shipData");
                    ScriptObjectMirror ShipObject;
                    for(String key : ShipObjectMap.keySet()){
                        ShipObject = (ScriptObjectMirror) ShipObjectMap.get(key);
                        consistData.put(key, (Boolean) ShipObject.get("rare"));
                        //System.out.println(ScriptObjectMap.get(key));
                    }
                    return true;
                }
                //System.out.println(retVal);
            }
        } catch (URISyntaxException ex) {
            ex.printStackTrace();
            Logger.getLogger(api.class.getName()).log(Level.SEVERE, null, ex);
            return false;
        } catch (IOException ex) {
            ex.printStackTrace();
            Logger.getLogger(api.class.getName()).log(Level.SEVERE, null, ex);
            return false;
        } catch (InterruptedException ex) {
            ex.printStackTrace();
            Logger.getLogger(api.class.getName()).log(Level.SEVERE, null, ex);
            return false;
        }
        return false;
    }
    
    public boolean netStart(){
        try {
            getConstantData("https://static.kcwiki.org/db/scripts/drop/constant.js");
            for(int i = 0 ; i < 3 ; i++){
                if(getDropData("https://db.kcwiki.org/drop/map/397/3/U-SAB.json") != null){
                    break;
                }
            }
            
            
        } catch (Exception ex) {
            Logger.getLogger(api.class.getName()).log(Level.SEVERE, null, ex);
            return false;
        }
        return true;  
    }
    
    public HashMap test(String mapno,String point,String difficulty,String assessment){
        if(point == null){
            String url = "";
            try {
                url = Jsoup.connect("https://db.kcwiki.org/drop/map/"+mapno)
                        .timeout(3000)           // 设置连接超时时间
                        .get().location();                 // 使用 POST 方法访问 URL   
            } catch (IOException ex) {
                Logger.getLogger(api.class.getName()).log(Level.SEVERE, null, ex);
            }
            url = url.replace(".html", ".json");
            return new api().getDropData(url);
        }
        if(Integer.valueOf(mapno) > 300){
            return new api().getDropData("https://db.kcwiki.org/drop/map/"+mapno+"/"+difficulty+"/"+point+"-"+assessment+".json");
        }else{
            return new api().getDropData("https://db.kcwiki.org/drop/map/"+mapno+"/"+point+"-"+assessment+".json");
        }
    }
    
    public static void main(String args[]){
        try {
            util = HttpUtil.getInstance();
            
            if(consistData.isEmpty()){
                new api().getConstantData("https://static.kcwiki.org/db/scripts/drop/constant.js");
            }
            new api().getDropData("https://db.kcwiki.org/drop/map/397/3/U-SAB.json");
            
        } catch (Exception ex) {
            Logger.getLogger(api.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
    
    
}
