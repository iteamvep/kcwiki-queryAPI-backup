/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.kcwiki.spider.wikimap;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

/**
 *
 * @author iTeam_VEP
 * @制空带路速查
 */
public class fastSearch {
    private static final String[] areaList = {"镇守府海域","南西群岛海域","北方海域","西方海域","南方海域","中部海域"};
    //海域  敌制空  优势    确保    推荐配置    带路条件    一句话攻略/说明
    private static final String[] titleList = {"world","efp","as","asplus","rec","guide","tips"};
    HashMap<String,Object> dataMap = new LinkedHashMap<>();
    HashMap<String,Object> areaMap = new LinkedHashMap<>();
        
    public HashMap search(Document html){
        //System.out.println(doc.toString());
        // 使用jsoup将html里面的a标签里面的数据全部读取出来（假如想读取其他标签，直接将a改为其他标签名称即可，例如"img"）
        Elements links = html.select(".tabs tabs-tabbox");
        Elements contents = html.select(".tabs-container").first().children();
        int contentCount = 1;
        String title = null;
        
        // 使用循环遍历每个标签数据
        for (Iterator<Element> it = contents.iterator(); it.hasNext();contentCount++) {
            Element e = (Element) it.next(); 
            Elements wikitable = e.select(".wikitable");
            System.out.println(" wikitable Num: "+contentCount);
            
            Elements trs = wikitable.select("tr");
            title = areaList[contentCount-1];
            HashMap<String,Object> tmpMap = new HashMap<>();
            
            String mapno = "null";
            String mapurl = "null";
            ArrayList<Object> configureList = new ArrayList<>();
            Boolean multiConfigure = false;
            Boolean isAdd = false;
                
            for (int i=0; i<trs.size(); i++){
                
                Elements tds = trs.get(i).select("td");
                if(tds.size()>0){
                    isAdd = false;
                    //System.out.println(" rows: "+i);
                    if(tds.size() < 7) {
                        multiConfigure = true;
                    } else {
                        if (!mapno.equals("null") && !configureList.isEmpty()){
                            multiConfigure = false;
                            areaMap.put(mapno, configureList.clone());
                            configureList.clear();
                            isAdd = true;
                        }
                    }
                    for (int j=0; j<tds.size(); j++){
                        if(j == 0 && tds.size() == 7){
                            mapno = tds.get(j).text();
                            mapurl = "https://zh.kcwiki.org" + tds.get(j).select("a").first().attr("href");
                        }else{
                            if(tds.get(j).text().equals("")){
                                if(multiConfigure){
                                    tmpMap.put(titleList[j+1], "null");
                                } else {
                                    tmpMap.put(titleList[j], "null");
                                }
                            } else {
                                if(multiConfigure){
                                    tmpMap.put(titleList[j+1], tds.get(j).text());
                                } else {
                                    tmpMap.put(titleList[j], tds.get(j).text());
                                }
                            }
                        }
                        //System.out.println(txt+" "); 
                    }
                    System.out.println(" ");
                }
                if(!tmpMap.isEmpty()){
                    tmpMap.put("url", mapurl);
                    configureList.add(tmpMap.clone());
                    tmpMap.clear();
                    isAdd = false;
                }
            }
            if(!isAdd) {
                areaMap.put(mapno, configureList.clone());
                configureList.clear();
            }
            dataMap.put(title, areaMap.clone());
            areaMap.clear();
            System.out.println();
        }
        return dataMap;
    }
    
    public HashMap test(){
        Document doc = null;
        try {
            doc = Jsoup.connect("https://zh.kcwiki.org/wiki/%E5%88%B6%E7%A9%BA%E5%B8%A6%E8%B7%AF%E9%80%9F%E6%9F%A5")
                    .timeout(3000)
                    .get();
        } catch (IOException ex) {
            Logger.getLogger(org.kcwiki.spider.wikiexpedition.mainpage.class.getName()).log(Level.SEVERE, null, ex);
        }
         return new fastSearch().search(doc);
    }
    
    public static void main(String[] args) {

        try {
            Document doc = Jsoup.connect("https://zh.kcwiki.org/wiki/%E5%88%B6%E7%A9%BA%E5%B8%A6%E8%B7%AF%E9%80%9F%E6%9F%A5")
                    //.data("query", "Java")   // 请求参数
                    //.userAgent("I ’ m jsoup") // 设置 User-Agent
                    //.cookie("auth", "token") // 设置 cookie
                    .timeout(3000)           // 设置连接超时时间
                    .get();                 // 使用 POST 方法访问 URL   
            
            new fastSearch().search(doc);

                } catch (IOException ex) {
                    Logger.getLogger(fastSearch.class.getName()).log(Level.SEVERE, null, ex);
                }
    }
    
}
