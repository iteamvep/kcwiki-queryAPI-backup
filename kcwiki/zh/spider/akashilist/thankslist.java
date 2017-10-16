/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.kcwiki.zh.spider.akashilist;

import com.gargoylesoftware.htmlunit.BrowserVersion;
import com.gargoylesoftware.htmlunit.FailingHttpStatusCodeException;
import com.gargoylesoftware.htmlunit.ProxyConfig;
import com.gargoylesoftware.htmlunit.WebClient;
import com.gargoylesoftware.htmlunit.html.DomElement;
import com.gargoylesoftware.htmlunit.html.DomNode;
import com.gargoylesoftware.htmlunit.html.DomNodeList;
import com.gargoylesoftware.htmlunit.html.HtmlPage;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.MalformedURLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.stream.Collectors;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.kcwiki.zh.init.AppInitializer;
import org.kcwiki.zh.utils.RWFile;
import org.kcwiki.zh.utils.constant;
import static org.kcwiki.zh.utils.constant.FILESEPARATOR;

/**
 *
 * @author iTeam_VEP
 */
public class thankslist {
    static HtmlPage page1 = null;
    private static final HashMap<String,Object> dataMap = new LinkedHashMap<>();
    
    public HashMap search(Document html){
        //html.select(".menu-view").select("div[class=menu-text novisible]").first().html();
        Element link = html.select(".menu-view").select("div[class=menu-text novisible]").first();
        Elements links = link.children().first().children().first().children();
        
        ArrayList<String> tmp = new ArrayList<>();
        String title = "";
        for (Iterator<Element> it = links.iterator(); it.hasNext();) {
            Element e = (Element) it.next();
            if(e.is("p")) {
                title = e.text();
                e = (Element) it.next();
            }
            if(e.is("ul")) {
                Elements lis = e.children();
                for(Element li:lis) {
                    tmp.add(li.text());
                }
                dataMap.put(title, tmp.clone());
                tmp.clear();
                title = "";
            }
        }
        
        return dataMap;
    }
    
    public String User_simulation() {
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
			HtmlPage page=webClient.getPage("http://akashi-list.me/"); // 解析获取页面

                        DomNodeList<DomElement> domNodeList = page.getElementsByTagName("ul"); // 获取提交按钮

                for(DomElement domElement:domNodeList){
                    if (domElement.getTextContent().contains("改修効果・フィット・装備情報")) {
                        System.out.println(domElement.getTextContent());
                        Iterable<DomElement> items = domElement.getChildElements();
                        for(DomElement item:items){
                            if(item.getTextContent().contains("改修効果・フィット・装備情報")) {
                                
                                try {
                                    page1=item.click();
                                } catch (IOException ex) {
                                    Logger.getLogger(thankslist.class.getName()).log(Level.SEVERE, null, ex);
                                }
                                webClient.close();
                                page1.asXml();
                            }
                        }
                    }
                }
            
        } catch (IOException ex) {
            Logger.getLogger(thankslist.class.getName()).log(Level.SEVERE, null, ex);
        } catch (FailingHttpStatusCodeException ex) {
            Logger.getLogger(thankslist.class.getName()).log(Level.SEVERE, null, ex);
        }finally{
            webClient.close(); // 关闭客户端，释放内存    
	}
        return page1.asXml();
    }
    
    public HashMap test1() {
        Document doc = null;
        try {
            doc = Jsoup.connect("http://akashi-list.me/")
                    .timeout(3000)           // 设置连接超时时间
                    .get(); // 使用 POST 方法访问 URL   
        } catch (IOException ex) {
            Logger.getLogger(org.kcwiki.zh.spider.poidb.mainpage.class.getName()).log(Level.SEVERE, null, ex);
        }
        //RWFile.writeLog(doc.html());
        return new thankslist().search(doc);
    }
    
    public static void main(String[] args) {
        //new thankslist().search( Jsoup.parse(new thankslist().User_simulation()));
        new thankslist().test1();
    }
}
