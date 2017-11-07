/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.kcwiki.spider.wikimission;

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
public class fastSearch {
    
    public boolean search(Document html){
        Elements links = html.select("#mw-content-text").first().children();

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
                            String txt = tds.get(j).select("img").first().attr("data-url");
                            
                            System.out.println(txt+" "); 
                        }
                    }
                }
                System.out.println();
            }
        }
        
        return true;
    }
    
    private String absurl(String txt){
        //Node.absUrl("");
        return null;    
    }
    
    public static void main(String[] args) {

        try {
            Document doc = Jsoup.connect("https://zh.kcwiki.org/wiki/%E4%BB%BB%E5%8A%A1%E9%85%8D%E7%BD%AE%E9%80%9F%E6%9F%A5")
                    .timeout(3000)
                    .get();
            
            new fastSearch().search(doc);

                } catch (IOException ex) {
                    Logger.getLogger(org.kcwiki.spider.wikimap.fastSearch.class.getName()).log(Level.SEVERE, null, ex);
                }
    }
}
