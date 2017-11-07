/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.kcwiki.spider.poidb;

import com.alibaba.fastjson.JSON;
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
import org.kcwiki.tools.RWFile;

/**
 *
 * @author iTeam_VEP
 */
public class mainpage {
    private static final HashMap<String,Object> MapNames = new LinkedHashMap<>();
    
    public HashMap getPoints(Document html){
        HashMap<String,Object> pointsMap = new LinkedHashMap<>();
        String img = html.select("div[class=col-xs-12 col-md-8]").select("img").first().attr("src");
        Elements links = html.select("div[class=col-xs-12 col-md-4]").select("ul[class=nav nav-pills]").first().children();
        Elements points = links.select("a[href]");
        System.out.println("battlepoints");
        pointsMap.put("img", img);
        ArrayList<String> pointsList = new ArrayList<>();
        for (int i=0; i<points.size(); i++){
            pointsList.add(points.get(i).text());
        }
        pointsMap.put("point", pointsList.clone());
        pointsList.clear();
        pointsList = null;
        JSON.toJSONString(pointsMap.clone());
        return pointsMap;
    }
    
    public HashMap getArea(Document html){
        Elements links = html.select("#accordion").first().children();
        
        for (int i=0; i<links.size(); i++){
            Elements maps = links.get(i).select("div[class=list-group]").select("a");
            HashMap<String,Object> tmpMap = new LinkedHashMap<>();
            String mapNo = null;
            for (int j=0; j<maps.size(); j++){
                String mapnum = maps.get(j).text();
                String mapno = mapnum.substring(mapnum.indexOf("(") + 1, mapnum.indexOf(")"));
                String[] tmp = mapno.split("-");
                mapnum = tmp[0] + tmp[1];
                mapNo = tmp[0];
                tmpMap.put(mapnum, maps.get(j).text());
                System.out.println(mapnum+ "\t" + maps.get(j).text());
            }
            MapNames.put(mapNo + "-" +links.get(i).select("div[class=panel-heading]").select("a").first().text(), tmpMap.clone());
            tmpMap.clear();
            System.out.println();
            //System.out.println(links.get(i).text());
        }          

        return null;
    }
    
    /**
    public boolean getMaps(Document html){
        ArrayList<String> mapName = new ArrayList<>();
        
        Elements links = html.select("div[class=panel panel-primary]").select("ul[class=nav nav-pills]").first().children();
        for (int i=0; i<links.size(); i++){
            //System.out.println("area name "+links.get(i).text());
            mapName.add(links.get(i).text());
        } 
        
        links = html.select("div[class=panel panel-primary]").select("div[class=tab-content top15]").first().children();
        for (int i=0; i<links.size(); i++){
            ArrayList<String> tmp = new ArrayList<>();
            //System.out.println("area name "+links.get(i).attr("id"));
            Elements maps = links.get(i).select("div[class=row]").select("a");
            String mapname = mapName.get(i);
            if(mapname == null){
                mapname = "null";
            }
            for (int j=0; j<maps.size(); j++){
                System.out.println("map href "+"https://db.kcwiki.org" + maps.get(j).attr("href"));
                tmp.add("https://db.kcwiki.org" + maps.get(j).attr("href"));
            }
            MapLinks.put(mapname, tmp.clone());
            tmp.clear();
            System.out.println();
            //System.out.println(links.get(i).text());
        }           

        return true;
    }
    */
    
    public HashMap test() {
        Document doc = null;
        try {
            doc = Jsoup.connect("https://db.kcwiki.org/drop/map/11")
                    .timeout(3000)
                    .get();
        } catch (IOException ex) {
            Logger.getLogger(mainpage.class.getName()).log(Level.SEVERE, null, ex);
        }
            //System.out.println(doc.location());
            RWFile.writeLog(doc.html(),null);
            new mainpage().getArea(doc);
            JSON.toJSONString(MapNames.clone());
            return MapNames;
    }
    
    public HashMap test1(String mapno) {
        Document doc = null;
        try {
            doc = Jsoup.connect("https://db.kcwiki.org/drop/map/"+mapno)
                    .timeout(3000)
                    .get();
        } catch (IOException ex) {
            Logger.getLogger(mainpage.class.getName()).log(Level.SEVERE, null, ex);
        }
            //System.out.println(doc.location());
            RWFile.writeLog(doc.html(),null);
            return new mainpage().getPoints(doc);
    }
    
    public static void main(String[] args) {

        new mainpage().test();
        
        /**
        doc = Jsoup.connect("https://db.kcwiki.org/drop/")
        //.data("query", "Java")   // 请求参数
        //.userAgent("I ’ m jsoup") // 设置 User-Agent
        //.cookie("auth", "token") // 设置 cookie
        .timeout(3000)           // 设置连接超时时间
        .get();                 // 使用 POST 方法访问 URL
        //System.out.println(doc.location());
        RWFile.writeLog(doc.html());
        new mainpage().getMaps(doc);
        */
        
        new mainpage().test1("11");
    }
}
