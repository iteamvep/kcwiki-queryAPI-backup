/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.kcwiki.spider.wikiraiders;

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
 */
public class mainpage {
    
    public boolean search(Document html){
        //System.out.println(doc.toString());
        // 使用jsoup将html里面的a标签里面的数据全部读取出来（假如想读取其他标签，直接将a改为其他标签名称即可，例如"img"）
        Elements links = html.select("#mw-content-text").first().children();
        /*
        Elements h2 = links.select("h2");
        Elements h3 = links.select("h3");
        Elements collapsible = links.select("table[class=wikitable zebra mw-collapsible]");
        */
        String headline = "null";
        String title = "null";
        for(Iterator<Element> it = links.iterator(); it.hasNext();){
            Element e = (Element) it.next(); 
            
            if(e.is("h2")){
                headline = e.text();
                System.out.println(" wikitable headline: "+headline);
                e = (Element) it.next();
            }
            if(e.is("ul")){
                title = e.select("li").first().text();
                System.out.println(" wikitable title: "+title);
                e = (Element) it.next();
            }
            if(e.is("table[class=wikitable mw-collapsible mw-collapsed]")){
                Element wikitable = e.clone();
                Elements trs = wikitable.select("tr");
                int i;
                String subtitle = "null";
                for ( i=0; i<trs.size(); i++){
                    Elements ths = trs.get(i).select("th");
                    if(ths.size()>0){
                        //System.out.println(" th: "+i);
                        for (int j=0; j<ths.size(); j++){
                            String txt = ths.get(j).text();
                            subtitle = txt;
                            System.out.println(txt+" ");
                        }
                    }

                    Elements tds = trs.get(i).select("td");
                    if(tds.size()>0){
                        //System.out.println(" td: "+i);
                        for (int j=0; j<tds.size(); j++){
                            if(tds.get(j).select("img").size() > 0){
                               String txt = tds.get(j).select("img").first().attr("data-url");
                                System.out.println(txt+" "); 
                            }
                            if(tds.get(j).select("li").size() > 0){
                               String txt = tds.get(j).select("li").first().text();
                                System.out.println(txt+" "); 
                            }
                        }
                    }
                }
                System.out.println();
            }
        }
        
        /*
        Element link = html.select(".tabs-container").first();
        Elements contents = link.children();
*/
        /*
        int contentCount = 1;
        // 使用循环遍历每个标签数据
        for (Iterator<Element> it = collapsible.iterator(); it.hasNext();contentCount++) {
            Element e = (Element) it.next(); 
            Elements wikitable = e.select(".wikitable");
            System.out.println(" wikitable Num: "+contentCount);
            
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
            }
            System.out.println();
        }
        */
        return true;
    }
    
    private String absurl(String txt){
        //Node.absUrl("");
        return null;    
    }
    
    public static void main(String[] args) {

        try {
            Document doc = Jsoup.connect("https://zh.kcwiki.org/wiki/%E6%94%BB%E7%95%A5:2017%E5%B9%B4%E5%A4%8F%E5%AD%A3%E6%B4%BB%E5%8A%A8%E6%94%BB%E7%95%A5")
                    //.data("query", "Java")   // 请求参数
                    //.userAgent("I ’ m jsoup") // 设置 User-Agent
                    //.cookie("auth", "token") // 设置 cookie
                    .timeout(3000)           // 设置连接超时时间
                    .get();                 // 使用 POST 方法访问 URL   
            
            new mainpage().search(doc);

                } catch (IOException ex) {
                    Logger.getLogger(org.kcwiki.spider.wikimap.fastSearch.class.getName()).log(Level.SEVERE, null, ex);
                }
    }
}
