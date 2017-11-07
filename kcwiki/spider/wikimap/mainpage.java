/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.kcwiki.spider.wikimap;

import java.io.IOException;
import java.util.Iterator;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

/**
 *
 * @author iTeam_VEP
 * @地图详细资料页
 */
public class mainpage {
    
    public boolean search(Document html){
        
        Elements links = html.select(".mw-collapsible");
        int collapsible = 1;
        for (Iterator<Element> it = links.iterator(); it.hasNext();collapsible++) {
            
            Element e = (Element) it.next();
            System.out.println(" collapsible Name: "+e.select(".mw-collapsible-toggle").text());
            //System.out.println(e.text() + " " + e.attr("href")+"\r\n");
            Elements wikitable = e.select(".wikitable");
            
            
            Elements trs = wikitable.select("tr");
            int i;
            for ( i=0; i<trs.size(); i++){
                Elements ths = trs.get(i).select("th");
                if(ths.size()>0){
                    System.out.println(" rows: "+i);
                    for (int j=0; j<ths.size(); j++){
                        String txt = ths.get(j).text();
                        System.out.println(txt+" ");
                    }
                    System.out.println(" ");
                }
                
                Elements tds = trs.get(i).select("td");
                if(tds.size()>0){
                    System.out.println(" rows: "+i);
                    for (int j=0; j<tds.size(); j++){
                        String txt = tds.get(j).text();
                        System.out.println(txt+" "); 
                    }
                    System.out.println(" ");
                }
                //System.out.println(" rows: "+i);
            }
        }
        return true;
    }
    
    public static void main(String[] args) {

        try {
            Document doc = Jsoup.connect("https://zh.kcwiki.org/wiki/2017%E5%B9%B4%E5%A4%8F%E5%AD%A3%E6%B4%BB%E5%8A%A8/E-3")
                    //.data("query", "Java")   // 请求参数
                    //.userAgent("I ’ m jsoup") // 设置 User-Agent
                    //.cookie("auth", "token") // 设置 cookie
                    .timeout(3000)           // 设置连接超时时间
                    .get();                 // 使用 POST 方法访问 URL   
            
            new mainpage().search(doc);

                } catch (IOException ex) {
                    Logger.getLogger(mainpage.class.getName()).log(Level.SEVERE, null, ex);
                }
    }
}
