/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.kcwiki.zh.spider.wikiexpedition;

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
import org.kcwiki.zh.spider.poidb.api;

/**
 *
 * @author iTeam_VEP
 */
public class mainpage {
    private static final String[] harvestTitleList = {"id","japanese","chinese","time","admiral","ship","fuel","ammo","steel","aluminum"};
    private static final String[] paymentTitleList = {"id","japanese","chinese","time","fleetlevel","flagshiplevel","lowcount","appointedship","bucket","fuelused","ammoused"};
    
    public HashMap search(Document html){
        HashMap<String,Object> dataMap = new LinkedHashMap<>();
        HashMap<String,Object> areaMap = new LinkedHashMap<>();
        
        Elements links = html.select("#mw-content-text").first().children();
        
        int contentCount = 1;
        String title = "null";
        
        for (Iterator<Element> it = links.iterator(); it.hasNext();contentCount++) {
            Element e = (Element) it.next(); 
            
                if(!e.is("div[class=tabber]")){
                    continue;
                }
                Elements tabbernav = e.select(".tabbertab");
                title = e.select(".tabbertab").select("p").first().text();
                if(title.equals("")){
                    continue;
                }
                
                if(tabbernav.size() == 2){
                    
                    HashMap<String,Object> harvestMap = new HashMap<>();
                    HashMap<String,Object> paymentMap = new HashMap<>();
                    HashMap<String,Object> tmpMap = new HashMap<>();
                    
                    Elements harvest = tabbernav.get(0).children();
                    harvest = harvest.select(".wikitable").select("tr");
                    
                    for (int i=0; i<harvest.size(); i++){
                        Elements tds = harvest.get(i).select("td");
                        if(tds.size()>0){
                            String[] tmparr;
                            HashMap<String,String> tmp = new HashMap<>();
                            ArrayList<String> tmplist = new ArrayList<>();
                            for (int j=0; j<tds.size(); j++){
                                String txt = tds.get(j).text();
                                switch(j){
                                    default:
                                        tmpMap.put(harvestTitleList[j], tds.get(j).text());
                                        break;
                                    case 6:
                                        if(tds.get(j).text().equals("")){
                                            tmpMap.put(harvestTitleList[j], "null");
                                        }else{
                                            tmparr = tds.get(j).text().split("/");
                                            tmpMap.put(harvestTitleList[j],tmparr[0] );
                                        }
                                        break;
                                    case 7:
                                        if(tds.get(j).text().equals("")){
                                            tmpMap.put(harvestTitleList[j], "null");
                                        }else{
                                            tmparr = tds.get(j).text().split("/");
                                            tmpMap.put(harvestTitleList[j],tmparr[0] );
                                        }
                                        break; 
                                    case 8:
                                        if(tds.get(j).text().equals("")){
                                            tmpMap.put(harvestTitleList[j], "null");
                                        }else{
                                            tmparr = tds.get(j).text().split("/");
                                            tmpMap.put(harvestTitleList[j],tmparr[0] );
                                        }
                                        break; 
                                    case 9:
                                        if(tds.get(j).text().equals("")){
                                            tmpMap.put(harvestTitleList[j], "null");
                                        }else{
                                            tmparr = tds.get(j).text().split("/");
                                            tmpMap.put(harvestTitleList[j],tmparr[0] );
                                        }
                                        break; 
                                    case 10:
                                        if(!tds.get(j).children().isEmpty()){
                                            if(tds.get(j).select("a").isEmpty()){
                                                tds.get(j).html();
                                                System.out.print("");
                                                tmplist.add(tds.get(j).text());
                                            }
                                            tmplist.add(tds.get(j).select("a").first().attr("title"));
                                        }
                                        break; 
                                    case 11:
                                        if(!tds.get(j).children().isEmpty()){
                                            tmplist.add(tds.get(j).select("a").first().attr("title"));
                                        }
                                        break; 
                                }
                                if(!tmplist.isEmpty()){
                                    tmpMap.put("reward", tmplist.clone());
                                } else {
                                    tmpMap.put("reward", "null");
                                }
                                System.out.println(txt+" "); 
                            }
                            harvestMap.put(tmpMap.get(harvestTitleList[0]).toString(), tmpMap.clone());
                            tmpMap.clear();
                            System.out.println(" ");
                        } 
                    } 
                    
                    Elements payment = tabbernav.get(1).children();
                    payment = payment.select(".wikitable").select("tr");
                    tmpMap.clear();
                    String html1 = payment.html();
                    System.out.println(tabbernav.get(1).attr("title")+" ");
                    for (int i=0; i<payment.size(); i++){
                        Elements tds = payment.get(i).select("td");
                        if(tds.size()>0){
                            String[] tmparr;
                            HashMap<String,String> tmp = new HashMap<>();
                            ArrayList<String> tmplist = new ArrayList<>();
                            for (int j=0; j<tds.size(); j++){
                                String txt = tds.get(j).text();
                                switch(j){
                                    default:
                                        tmpMap.put(paymentTitleList[j], tds.get(j).text());
                                        break;
                                    case 4:
                                        if(tds.get(j).text().equals("")){
                                            tmpMap.put(paymentTitleList[j], "null");
                                        }else{
                                            tmpMap.put(paymentTitleList[j],tds.get(j).text());
                                        }
                                        break;
                                    case 8:
                                        if(tds.get(j).text().equals("")){
                                            tmpMap.put(paymentTitleList[j], "null");
                                        }else{
                                            tmpMap.put(paymentTitleList[j],tds.get(j).text());
                                        }
                                        break; 
                                    case 9:
                                        if(tds.get(j).text().equals("")){
                                            tmpMap.put(paymentTitleList[j], "null");
                                        }else{
                                            tmpMap.put(paymentTitleList[j],tds.get(j).text());
                                        }
                                        break; 
                                    case 10:
                                        if(tds.get(j).text().equals("")){
                                            tmpMap.put(paymentTitleList[j], "null");
                                        }else{
                                            tmpMap.put(paymentTitleList[j],tds.get(j).text());
                                        }
                                        break;
                                }
                                System.out.println(txt+" "); 
                            }
                            paymentMap.put(tmpMap.get(paymentTitleList[0]).toString(), tmpMap.clone());
                            tmpMap.clear();
                            System.out.println(" ");
                        } 
                    }
                    
                    HashMap map1;
                    tmpMap.clear();
                    for(String key:harvestMap.keySet()){
                        tmpMap = (HashMap) harvestMap.get(key);
                        map1 = (HashMap) paymentMap.get(key);
                        tmpMap.putAll(map1);
                        areaMap.put(key, tmpMap.clone());
                        tmpMap.clear();
                        map1.clear();
                    }
                    dataMap.put(title, areaMap.clone());
                    areaMap.clear();
                    System.out.println(" ");
                }  
        }
        return dataMap; 

    }
    
    public HashMap test(){
        Document doc = null;
        try {
            doc = Jsoup.connect("https://zh.kcwiki.org/wiki/%E8%BF%9C%E5%BE%81%E5%88%97%E8%A1%A8")
                    .timeout(3000)
                    .get();
        } catch (IOException ex) {
            Logger.getLogger(mainpage.class.getName()).log(Level.SEVERE, null, ex);
        }
         return new mainpage().search(doc);
    }
    
    public static void main(String[] args) {

        try {
            Document doc = Jsoup.connect("https://zh.kcwiki.org/wiki/%E8%BF%9C%E5%BE%81%E5%88%97%E8%A1%A8")
                    .timeout(3000)
                    .get();
            
            new mainpage().search(doc);

                } catch (IOException ex) {
                    Logger.getLogger(org.kcwiki.zh.spider.wikimap.mainpage.class.getName()).log(Level.SEVERE, null, ex);
                }
    }
}
