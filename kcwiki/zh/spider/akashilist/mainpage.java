/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.kcwiki.zh.spider.akashilist;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import java.io.IOException;
import java.util.Iterator;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import com.gargoylesoftware.htmlunit.BrowserVersion;
import com.gargoylesoftware.htmlunit.FailingHttpStatusCodeException;
import com.gargoylesoftware.htmlunit.NicelyResynchronizingAjaxController;
import com.gargoylesoftware.htmlunit.ProxyConfig;
import com.gargoylesoftware.htmlunit.WebClient;
import com.gargoylesoftware.htmlunit.html.DomElement;
import com.gargoylesoftware.htmlunit.html.HtmlForm;
import com.gargoylesoftware.htmlunit.html.HtmlPage;
import com.gargoylesoftware.htmlunit.html.HtmlSubmitInput;
import com.gargoylesoftware.htmlunit.html.HtmlTextInput;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.PrintStream;
import java.io.UnsupportedEncodingException;
import static java.lang.Thread.sleep;
import java.net.MalformedURLException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.Set;
import java.util.TimeZone;
import org.apache.commons.lang3.StringUtils;
import static org.kcwiki.zh.init.AppInitializer.initApp;
import org.kcwiki.zh.utils.RWFile;
import org.kcwiki.zh.utils.RandomIdGenerator;
import redis.clients.jedis.Jedis;

import org.kcwiki.zh.spider.akashilist.ThreadPool;
import org.kcwiki.zh.utils.Encoder;
import org.kcwiki.zh.utils.constant;

/**
 *
 * @author iTeam_VEP
 */
public class mainpage {
    private static final String[] weekDays = {"日", "月", "火", "水", "木", "金", "土"};
    private static final String[] Days = {"sun", "mon", "tue", "wed", "thu", "fry", "sat"};
    private static HashMap<String,JSONObject> daysMap = new LinkedHashMap<>();
    private static HashMap<String,JSONObject> typesMap = new LinkedHashMap<>();
    private static HashMap<String,JSONObject> itemsMap = new LinkedHashMap<>();
    private static HashMap<String,String> typealias = new HashMap<>();
    private static boolean isInit = false;
    
    public static void JSONFileGenerator() {
        initApp();
        mainpage object = new mainpage();
        HashMap<String,Object> dataMap = new LinkedHashMap<>();
        HashMap<String,Object> dayMap = new LinkedHashMap<>();
        HashMap<String,Object> typeMap = new LinkedHashMap<>();
        int id = 1;
            ThreadPool.ininPool();
            HashMap<String,String> types = null;
            for(int i=0; i<5; i++){
                types = object.getType();
                if(types != null){
                    break;
                }
                try {
                    sleep(10*1000);
                } catch (InterruptedException ex) {
                    Logger.getLogger(mainpage.class.getName()).log(Level.SEVERE, null, ex);
                }
            }
            if(types == null) {
                ThreadPool.shutdown();
                return ;
            }
            for(String type:types.keySet()) {
                final String typename = type;
                    Runnable task = new Runnable() {
                        public void run() {
                            if(typename == null){
                                return;
                            }
                            typeMap.put(typename, new org.kcwiki.zh.spider.akashilist.mainpage().test1(typename,"allweek").clone());
                        }
                    };
                    ThreadPool.addTask(task, id, type);
                    id++;
            }
            for(int i=0; i<Days.length; i++) {
                final String day = Days[i];
                    final int daynum = i;
                    Runnable task = new Runnable() {
                        public void run() {
                            if(weekDays[daynum] == null){
                                return;
                            }
                            dayMap.put(weekDays[daynum], new org.kcwiki.zh.spider.akashilist.mainpage().test1("all",day).clone());
                        }
                    };
                    ThreadPool.addTask(task, id, day);
                    id++;
            }
            ThreadPool.takeTask();
            ThreadPool.shutdown();
        
        dataMap.put("types", typeMap.clone());
        dataMap.put("typealias", types.clone());
        dataMap.put("days", dayMap.clone());
        String json = JSON.toJSONString(dataMap.clone());
        RWFile.writeLog(JSON.toJSONString(dataMap.clone()), new constant().getFilterFilePath()+".bak");
        try (BufferedWriter eBfw = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(new File(new constant().getFilterFilePath())), "UTF-8"))) {
            eBfw.write(JSON.toJSONString(dataMap.clone()));
        } catch (UnsupportedEncodingException ex) {
            Logger.getLogger(mainpage.class.getName()).log(Level.SEVERE, null, ex);
        } catch (FileNotFoundException ex) {
            Logger.getLogger(mainpage.class.getName()).log(Level.SEVERE, null, ex);
        } catch (IOException ex) {
            Logger.getLogger(mainpage.class.getName()).log(Level.SEVERE, null, ex);
        }
        System.out.println(json);
    }
    
    public HashMap search(Document html){
        HashMap<String,Object> HashMap = new LinkedHashMap<>();
        Element link = html.select("#weapon-remodel").first();
        Elements links = link.children();
        for (Iterator<Element> it = links.iterator(); it.hasNext();) {
            Element e = (Element) it.next();
            
            if(e.attr("style").contains("none")){
                continue;
            }
            if(e.attr("class").contains("weapon-hidden")){
                continue;
            }
            itemBean item = parseItem(e);
            HashMap.put(item.getId(),item);
        }
        return HashMap;
    }
    
    public HashMap getType() {
        Document html = null;
        HashMap<String,String> dataMap = new LinkedHashMap<>();
        try {
            html = Jsoup.connect("http://akashi-list.me/")
                    .header("User-Agent", "Mozilla/5.0 (Windows NT 6.3; WOW64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/61.0.3163.100 Safari/537.36")
                    .proxy("127.0.0.1", 1090)
                    .timeout(3000)
                    .get();
        } catch (IOException ex) {
            Logger.getLogger(mainpage.class.getName()).log(Level.SEVERE, null, ex);
            return null;
        }
        Elements types = html.select("#all").first().parent().children();
        for(Element type:types) {
            if(type.attr("id").equals("clip") || type.attr("id").equals("dropdown-remodel")) {
                continue;
            }
            if(!type.select(".ah4").isEmpty()) {
                dataMap.put(type.attr("id"),type.select(".ah4").first().ownText());
            } else {
                if(StringUtils.isBlank(type.ownText())) {
                    if(!StringUtils.isBlank(type.attr("title"))){
                        dataMap.put(type.attr("id"),type.attr("title"));
                    } else {
                        dataMap.put(type.attr("id"),"未知装备-id:"+RandomIdGenerator.randGenerator());
                    }
                } else {
                    dataMap.put(type.attr("id"),type.ownText());
                }
                
            }
        }
        return dataMap;
    }
    
    public String User_simulation(String type,String day) {
        //if(true) {
        
            WebClient webClient=new WebClient(BrowserVersion.FIREFOX_52);
            new InterceptWebConnection(webClient);
            webClient.waitForBackgroundJavaScript(5000);	
            //webClient.setWebConnection(interceptWebConnection);
            // 启动JS  
            webClient.getOptions().setJavaScriptEnabled(true);  
            webClient.getOptions().setDownloadImages(false);
            //忽略ssl认证  
            webClient.getOptions().setUseInsecureSSL(true);  
            //禁用Css，可避免自动二次请求CSS进行渲染  
            webClient.getOptions().setCssEnabled(false);  
            //运行错误时，不抛出异常  
            webClient.getOptions().setThrowExceptionOnScriptError(false);  
            webClient.getOptions().setThrowExceptionOnFailingStatusCode(true);
            // 设置Ajax异步  
            //webClient.setAjaxController(new NicelyResynchronizingAjaxController());  
            //设置代理  
            ProxyConfig proxyConfig = webClient.getOptions().getProxyConfig();   
            proxyConfig.setProxyHost("127.0.0.1");    
            proxyConfig.setProxyPort(1090);  
                
		try {
			HtmlPage page = webClient.getPage("http://akashi-list.me/"); // 解析获取页面
                        DomElement button = page.getElementById(day); // 获取提交按钮
			HtmlPage page2 = button.click(); // 模拟点击
                        webClient.waitForBackgroundJavaScript(1000);//设置页面等待js响应时间,
                        //button = page.getElementById("heavyGun");
                        button = page.getElementById(type); 
                        page2 = button.click();
                        webClient.waitForBackgroundJavaScript(1000);//设置页面等待js响应时间,

                        webClient.close();
                        //RWFile.writeLog(page2.asXml());
                        return page2.asXml();
		} catch (FailingHttpStatusCodeException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (MalformedURLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (java.lang.RuntimeException e){
                    e.printStackTrace();
                }
                // TODO Auto-generated catch block
                finally{
                    webClient.close(); // 关闭客户端，释放内存    
		}
        
        return null;
    }
        
    private itemBean parseItem(Element element) {
        
        System.out.println(" data-title: "+element.attr("data-title"));
        
        itemBean itemBean = new itemBean();
        itemBean.setId(element.attr("id"));
        itemBean.setImgurl("http://akashi-list.me/" + element.select(".wimage").select("img").attr("data-src"));
        itemBean.setTitle(element.attr("data-title").split(":")[1].trim());
        
        HashMap<String,Object> tmpmap = new LinkedHashMap<>();
        ArrayList<String> tmpList = new ArrayList<>();
        
        Elements status = element.select(".status").select("span");
        for(Element item:status) {
            if("".equals(item.attr("class"))) {
                continue;    
            }

            if(!StringUtils.isNumericSpace(item.text()) && item.text().length() < 2 ) {
                continue;
            }
            tmpList.add(item.ownText());
            Elements items = item.children();
            for(Element i:items) {
                if(!i.text().equals("")) {
                    tmpList.add(i.text().trim());
                }
            }
            if(!tmpList.isEmpty()) {
                tmpmap.put(item.attr("class"), tmpList.clone());
            }
            tmpList.clear();
        }
        itemBean.setStatus((HashMap) tmpmap.clone());
        tmpmap.clear();
        
        Elements supportShip = element.select("div[class=support-ship]");
        HashMap<String,Object> shipData = new LinkedHashMap<>();
        ArrayList<Object> supportships = new ArrayList<>();
        String today = day();
        boolean enableToday ;
        for(Element item:supportShip) {
            enableToday = false;
            shipData.put("imgurl", "http://akashi-list.me/" + item.select("img").first().attr("data-src"));
            Elements items = item.select(".weeks").select(".enable");
            for(Element i:items) {
                tmpList.add(i.text().trim());
                if(i.text().trim().equals(today)) {
                    enableToday = true;
                }
            }
            if(enableToday) {
                shipData.put("weeks", tmpList.clone());
                supportships.add(shipData.clone());
            }
            tmpList.clear();
            shipData.clear();
        }
        //tmpmap.put("supportShip", supportships.clone());
        
        itemBean.setSupportShip((ArrayList) supportships.clone());
        tmpmap.clear();
        return itemBean;
    }
    
    public static boolean init () {
        if(readItems() && readFilter()){
            isInit = true;
        }
        return isInit();
    }
    
    /**
     * @return the isInit
     */
    public static boolean isInit() {
        return isInit;
    }
    
    public static boolean readItems() {
        String file = null;
        if(!new File(new constant().getItemsFilePath()).exists()){
            return false;
        }
        try (BufferedReader nBfr = new BufferedReader(new InputStreamReader(new FileInputStream(new constant().getItemsFilePath()), Encoder.codeString(new constant().getItemsFilePath())))) {
                String line;
                StringBuilder buffer = new StringBuilder();
                while ((line=nBfr.readLine())!=null) {
                    buffer.append(line);
                }
                file = buffer.toString();
            } catch (FileNotFoundException ex) {
            Logger.getLogger(mainpage.class.getName()).log(Level.SEVERE, null, ex);
        } catch (UnsupportedEncodingException ex) {
            Logger.getLogger(mainpage.class.getName()).log(Level.SEVERE, null, ex);
        } catch (IOException ex) {
            Logger.getLogger(mainpage.class.getName()).log(Level.SEVERE, null, ex);
        }
        JSONObject fileobj = JSON.parseObject(file);
        JSONArray jarr = fileobj.getJSONArray("items");
        for(int i=0; i<jarr.size(); i++){
            JSONObject item = jarr.getJSONObject(i);
            itemsMap.put(item.getString("id"), (JSONObject) item.clone());
        }
        return true;
    }
    
    public static boolean readFilter() {
       String file = null;
       if(!new File(new constant().getFilterFilePath()).exists()){
            return false;
        }
        try (BufferedReader nBfr = new BufferedReader(new InputStreamReader(new FileInputStream(new constant().getFilterFilePath()), Encoder.codeString(new constant().getFilterFilePath())))) {
                String line;
                StringBuilder buffer = new StringBuilder();
                while ((line=nBfr.readLine())!=null) {
                    buffer.append(line);
                }
                file = buffer.toString();
            } catch (FileNotFoundException ex) {
            Logger.getLogger(mainpage.class.getName()).log(Level.SEVERE, null, ex);
        } catch (UnsupportedEncodingException ex) {
            Logger.getLogger(mainpage.class.getName()).log(Level.SEVERE, null, ex);
        } catch (IOException ex) {
            Logger.getLogger(mainpage.class.getName()).log(Level.SEVERE, null, ex);
        }
        JSONObject fileobj = JSON.parseObject(file);
        JSONObject jobj = fileobj.getJSONObject("types");
        for(String key:jobj.keySet()){
            JSONObject item = jobj.getJSONObject(key);
            typesMap.put(key, (JSONObject) item.clone());
        }
        jobj = fileobj.getJSONObject("typealias");
        for(String key:jobj.keySet()){
            typealias.put(key, jobj.getString(key));
        }
        jobj = fileobj.getJSONObject("days");
        for(String key:jobj.keySet()){
            JSONObject item = jobj.getJSONObject(key);
            daysMap.put(key, (JSONObject) item.clone());
        }
        return true;
    }
    
    public HashMap getTypeList() {
        return typealias;
    }
    
    public HashMap getItemList(String type) {
        JSONObject daysJobj = daysMap.get(day());
        JSONObject typesJobj = typesMap.get(type);
        Set<String> daysSet = new HashSet<>();
        Set<String> typesSet = new HashSet<>();
        //Set<String> daysStor = new HashSet<>();
        //Set<String> typesStor = new HashSet<>();
        Set<String> result = new HashSet<>();
        HashMap<String,JSONObject> dataMap = new HashMap<>();
        for(String key:daysJobj.keySet()){
            daysSet.add(key);
        }
        for(String key:typesJobj.keySet()){
            typesSet.add(key);
        }
        /*daysStor.addAll(daysSet);
        daysStor.retainAll(typesSet);
        typesStor.addAll(typesSet);
        typesStor.retainAll(daysSet);
        daysStor.retainAll(typesStor);*/
        result.addAll(daysSet);
        result.retainAll(typesSet);
        for(String item:result) {
            dataMap.put(item, daysJobj.getJSONObject(item));
        }
        return dataMap;
    }
    
    public HashMap getItemDetail(String wid) {
        ArrayList<String> tmpList = new ArrayList<>();
        HashMap<String,Object> dataMap = new LinkedHashMap<>();
        HashMap<String,Object> tmpMap = new LinkedHashMap<>();
        HashMap<String,Object> resourceMap = new LinkedHashMap<>();
        HashMap<String,Object> statusMap = new LinkedHashMap<>();
        
        tmpList.add("★");
        tmpList.add("開発資材");
        tmpList.add("改修資材");
        tmpList.add("消費装備");
        resourceMap.put("title", tmpList.clone());
        tmpList.clear();
        
        JSONObject item = itemsMap.get(wid);
        
        JSONObject itemDetails = item.getJSONArray("remodel_info").getJSONObject(0).getJSONObject("resource_cost").getJSONObject("0 ～ 5");
        tmpList.add(itemDetails.getString("buildkit_num"));
        tmpList.add(itemDetails.getString("remodelkit_num"));
        tmpList.add(itemDetails.getString("equipkit"));
        resourceMap.put("level1", tmpList.clone());
        tmpList.clear();
        
        itemDetails = item.getJSONArray("remodel_info").getJSONObject(0).getJSONObject("resource_cost").getJSONObject("6 ～ 9");
        tmpList.add(itemDetails.getString("buildkit_num"));
        tmpList.add(itemDetails.getString("remodelkit_num"));
        tmpList.add(itemDetails.getString("equipkit"));
        resourceMap.put("level2", tmpList.clone());
        tmpList.clear();
        
        itemDetails = item.getJSONArray("remodel_info").getJSONObject(0).getJSONObject("upgrade").getJSONObject("cost");
        tmpList.add(itemDetails.getString("buildkit_num"));
        tmpList.add(itemDetails.getString("remodelkit_num"));
        tmpList.add(itemDetails.getString("equipkit"));
        resourceMap.put("MAX", tmpList.clone());
        tmpList.clear();
        
        String upgradeItem = item.getJSONArray("remodel_info").getJSONObject(0).getJSONObject("upgrade").getString("item_name");
        if(upgradeItem != null){
            resourceMap.put("upgrade", upgradeItem);
        }
        
        itemDetails = item.getJSONArray("remodel_info").getJSONObject(0).getJSONObject("base_cost");
        HashMap<String,Integer>  baseCost = new HashMap<>();
        for(String resourceTypes:itemDetails.keySet()){
            baseCost.put(resourceTypes, itemDetails.getIntValue(resourceTypes));
        }
        resourceMap.put("basecost", baseCost.clone());
        baseCost.clear();
        
        dataMap.put("resource", resourceMap.clone());
        resourceMap.clear();
        
        itemDetails = item.getJSONObject("item_remodel");
        if(itemDetails != null){
            for(String status:itemDetails.keySet()){
                statusMap.put(status, itemDetails.getJSONArray(status));
            }
        }
        dataMap.put("status", statusMap.clone());
        statusMap.clear();
        
        return dataMap;
    }

    public HashMap test1(String type,String day) {
        return new mainpage().search( Jsoup.parse(new mainpage().User_simulation(type,day)) );
    }
    
    public HashMap test() {
        return new mainpage().getType();
    }
    
    public static String day(){
        TimeZone tz = TimeZone.getTimeZone("Japan"); 
        Calendar jpcalendar = Calendar.getInstance(tz);
        jpcalendar.setTime(new Date());
        int w = jpcalendar.get(Calendar.DAY_OF_WEEK) - 1;
        if (w < 0)
            w = 0;
        return weekDays[w];
    }
    
    public static String dayid(){
        TimeZone tz = TimeZone.getTimeZone("Japan"); 
        Calendar jpcalendar = Calendar.getInstance(tz);
        jpcalendar.setTime(new Date());
        int w = jpcalendar.get(Calendar.DAY_OF_WEEK) - 1;
        if (w < 0)
            w = 0;
        return Days[w];
    }
    
    public static void main(String[] args) {
        //JSONFileGenerator();
        initApp();
        init(); 
        new mainpage().getTypeList();
        //JSON.toJSONString(new mainpage().getItemList("all"));
        JSON.toJSONString(new mainpage().getItemDetail("w020"));
        /*HashMap tmp;
        tmp = new mainpage().search( Jsoup.parse(new mainpage().User_simulation("all",dayid())) );
        JSON.toJSONString(tmp.clone());
        //new mainpage().getItemDetail("w026");*/
    }
    
    
    //---Deprecated---已经过时的方法---
    /*public HashMap search(Document html){
        HashMap<String,Object> HashMap = new LinkedHashMap<>();
        Element link = html.select("#weapon-remodel").first();
        Elements links = link.children();
        for (Iterator<Element> it = links.iterator(); it.hasNext();) {
            Element e = (Element) it.next();
            
            if(e.attr("style").contains("none")){
                continue;
            }
            if(e.attr("class").contains("weapon-hidden")){
                continue;
            }
            itemBean item = parseItem(e);
            HashMap.put(item.getId(),item);
        }
        return HashMap;
    }
    
    public HashMap getType() {
        Document html = null;
        HashMap<String,String> dataMap = new LinkedHashMap<>();
        try {
            html = Jsoup.connect("http://akashi-list.me/")
                    //.header("User-Agent", "Mozilla/5.0 (Windows NT 6.3; WOW64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/61.0.3163.100 Safari/537.36")
                    //.proxy("127.0.0.1", 1090)
                    .timeout(3000)
                    .get();
        } catch (IOException ex) {
            Logger.getLogger(mainpage.class.getName()).log(Level.SEVERE, null, ex);
            return null;
        }
        Elements types = html.select("#all").first().parent().children();
        for(Element type:types) {
            if(type.attr("id").equals("clip") || type.attr("id").equals("dropdown-remodel")) {
                continue;
            }
            if(!type.select(".ah4").isEmpty()) {
                dataMap.put(type.attr("id"),type.select(".ah4").first().ownText());
            } else {
                if(StringUtils.isBlank(type.ownText())) {
                    if(!StringUtils.isBlank(type.attr("title"))){
                        dataMap.put(type.attr("id"),type.attr("title"));
                    } else {
                        dataMap.put(type.attr("id"),"未知装备-id:"+RandomIdGenerator.randGenerator());
                    }
                } else {
                    dataMap.put(type.attr("id"),type.ownText());
                }
                
            }
        }
        return dataMap;
    }
    
    public String User_simulation(String type) {
        //if(true) {
        
            WebClient webClient=new WebClient(BrowserVersion.FIREFOX_52);
            new InterceptWebConnection(webClient);
            webClient.waitForBackgroundJavaScript(5000);	
            //webClient.setWebConnection(interceptWebConnection);
            // 启动JS  
            webClient.getOptions().setJavaScriptEnabled(true);  
            webClient.getOptions().setDownloadImages(false);
            //忽略ssl认证  
            webClient.getOptions().setUseInsecureSSL(true);  
            //禁用Css，可避免自动二次请求CSS进行渲染  
            webClient.getOptions().setCssEnabled(false);  
            //运行错误时，不抛出异常  
            webClient.getOptions().setThrowExceptionOnScriptError(false);  
            webClient.getOptions().setThrowExceptionOnFailingStatusCode(true);
            // 设置Ajax异步  
            //webClient.setAjaxController(new NicelyResynchronizingAjaxController());  
            //设置代理  
            //ProxyConfig proxyConfig = webClient.getOptions().getProxyConfig();   
            //proxyConfig.setProxyHost("127.0.0.1");    
            //proxyConfig.setProxyPort(1090);  
                
		try {
			HtmlPage page = webClient.getPage("http://akashi-list.me/"); // 解析获取页面
                        DomElement button = page.getElementById("today"); // 获取提交按钮
			HtmlPage page2 = button.click(); // 模拟点击
                        webClient.waitForBackgroundJavaScript(1000);//设置页面等待js响应时间,
                        //button = page.getElementById("heavyGun");
                        button = page.getElementById(type); 
                        page2 = button.click();
                        webClient.waitForBackgroundJavaScript(1000);//设置页面等待js响应时间,

                        webClient.close();
                        //RWFile.writeLog(page2.asXml());
                        return page2.asXml();
		} catch (FailingHttpStatusCodeException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (MalformedURLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (java.lang.RuntimeException e){
                    e.printStackTrace();
                }
                // TODO Auto-generated catch block
                finally{
                    webClient.close(); // 关闭客户端，释放内存    
		}
        
        return null;
    }
    
    public HashMap getItemDetail(String wid) {
        Document html = null;
        try {
            html = Jsoup.connect("http://akashi-list.me/detail/" + wid + ".html")
            //html = Jsoup.connect("http://akashi-list.me/detail/w026.html")
                    .timeout(3000)           // 设置连接超时时间
                    .get();
        } catch (IOException ex) {
            Logger.getLogger(mainpage.class.getName()).log(Level.SEVERE, null, ex);
        }
        if(html.select("div[class=detail-row]").select("div[class=resource-table]").isEmpty()) {
            return null;
        }
        Elements links = html.select("div[class=detail-row]").select("div[class=resource-table]").first().children();
        
        
        ArrayList<String> titleList = new ArrayList<>();
        ArrayList<String> dataList = new ArrayList<>();
        HashMap<String,Object> tmpMap = new LinkedHashMap<>();
        HashMap<String,Object> dataMap = new LinkedHashMap<>();
        
        Elements trs = links.select("tr");
            int i;
            for ( i = 0; i < trs.size(); i++) {
                String title = null;
                Elements ths = trs.get(i).select("th");
                
                if(ths.size()>0) {
                    System.out.println(" rows: "+i);
                    for (int j=0; j<ths.size(); j++) {
                        if (trs.get(i).select("th").size() == 1) {
                            title = ths.get(j).text().trim();
                            if (title.contains("改修必要資材")) {
                                continue;
                            }
                        }
                        
                        titleList.add(ths.get(j).text());
                        String txt = ths.get(j).text();
                        System.out.println(txt+" ");
                    }
                    if (titleList.size() > 1) {
                        tmpMap.put("title", titleList.clone());
                    }
                    titleList.clear();
                    System.out.println(" ");
                }
                
                Elements tds = trs.get(i).select("td");
                if (tds.size()>0) {
                    System.out.println(" rows: "+i);
                    if (trs.get(i).attr("class").contains("upgrade")) {
                        tmpMap.put("upgrade", tds.get(tds.size()-1).text());
                        break;
                    }
                    if(title == null) {
                        continue;
                    }
                    if (title.contains("MAX")) {
                        if(tds.get(tds.size()-1).text().trim().equals("-")) {
                            continue;
                        }
                    }
                    if (title.contains("null")) {
                        continue;
                    }
                    for (int j=0; j<tds.size(); j++) {
                        String txt = tds.get(j).text();
                        System.out.println(txt+" "); 
                        dataList.add(tds.get(j).text().trim());
                    }
                    System.out.println(" ");
                }
                
                if(!dataList.isEmpty()) {
                    if(title.contains("0 ～ 5")) {
                        title = "level1";
                    }
                    if(title.contains("6 ～ 9")) {
                        title = "level2";
                    }
                    tmpMap.put(title, dataList.clone());
                    dataList.clear();
                }
                //System.out.println(" rows: "+i);
            }
            dataMap.put("resource", tmpMap.clone());
            tmpMap.clear();
            dataList.clear();
            
          links = html.select("div[class=detail-row]").select("div[class=detail-status]").first().children();
        
         trs = links.select("tr");
         
            outer: for ( i = 0; i < trs.size(); i++) {
                ArrayList<String> title = new ArrayList<>();
                Elements ths = trs.get(i).select("th");
                
                if(ths.size()>0) {
                    System.out.println(" rows: "+i);
                    for (int j=0; j<ths.size(); j++) {
                        if (ths.get(j).text().trim().contains("装備ステータス")) {
                            continue;
                        }
                        if (ths.get(j).text().trim().contains("装備可能艦種")) {
                            break outer;
                        }
                        title.add(ths.get(j).text().trim());
                    }
                    System.out.println(" ");
                }
                
                Elements tds = trs.get(i).select("td");
                if (tds.size()>0) {
                    System.out.println(" rows: "+i);
                    
                    for (int j=0; j<tds.size(); j++) {
                        Elements spans = tds.get(j).select("span");
                        if (!spans.isEmpty()) {
                            spans = tds.get(j).select("span").last().children();
                            for (int k=0; k<spans.size(); k++) {
                                dataList.add(spans.get(k).text().trim());
                            }
                            tmpMap.put(title.get(j), dataList.clone());
                        }
                        dataList.clear();
                    }
                    System.out.println(" ");
                }
                //System.out.println(" rows: "+i);
            }
            dataMap.put("status", tmpMap.clone());
            tmpMap.clear();
            dataList.clear();

        return dataMap;
    }
    
    private itemBean parseItem(Element element) {
        
        System.out.println(" data-title: "+element.attr("data-title"));
        
        itemBean itemBean = new itemBean();
        itemBean.setId(element.attr("id"));
        itemBean.setImgurl("http://akashi-list.me/" + element.select(".wimage").select("img").attr("data-src"));
        itemBean.setTitle(element.attr("data-title").split(":")[1].trim());
        
        HashMap<String,Object> tmpmap = new LinkedHashMap<>();
        ArrayList<String> tmpList = new ArrayList<>();
        
        Elements status = element.select(".status").select("span");
        for(Element item:status) {
            if("".equals(item.attr("class"))) {
                continue;    
            }

            if(!StringUtils.isNumericSpace(item.text()) && item.text().length() < 2 ) {
                continue;
            }
            tmpList.add(item.ownText());
            Elements items = item.children();
            for(Element i:items) {
                if(!i.text().equals("")) {
                    tmpList.add(i.text().trim());
                }
            }
            if(!tmpList.isEmpty()) {
                tmpmap.put(item.attr("class"), tmpList.clone());
            }
            tmpList.clear();
        }
        itemBean.setStatus((HashMap) tmpmap.clone());
        tmpmap.clear();
        
        Elements supportShip = element.select("div[class=support-ship]");
        HashMap<String,Object> shipData = new LinkedHashMap<>();
        ArrayList<Object> supportships = new ArrayList<>();
        String today = day();
        boolean enableToday ;
        for(Element item:supportShip) {
            enableToday = false;
            shipData.put("imgurl", "http://akashi-list.me/" + item.select("img").first().attr("data-src"));
            Elements items = item.select(".weeks").select(".enable");
            for(Element i:items) {
                tmpList.add(i.text().trim());
                if(i.text().trim().equals(today)) {
                    enableToday = true;
                }
            }
            if(enableToday) {
                shipData.put("weeks", tmpList.clone());
                supportships.add(shipData.clone());
            }
            tmpList.clear();
            shipData.clear();
        }
        //tmpmap.put("supportShip", supportships.clone());
        
        itemBean.setSupportShip((ArrayList) supportships.clone());
        tmpmap.clear();
        return itemBean;
    }*/

}
