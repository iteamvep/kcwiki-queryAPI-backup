/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.kcwiki.spider.akashilist;

import com.gargoylesoftware.htmlunit.BrowserVersion;
import com.gargoylesoftware.htmlunit.NicelyResynchronizingAjaxController;
import com.gargoylesoftware.htmlunit.ProxyConfig;
import com.gargoylesoftware.htmlunit.WebClient;

/**
 *
 * @author iTeam_VEP
 */
public enum  WebClientUtil {

    INSTANCE;

    public WebClient webClient;

    WebClientUtil() {
        webClient = new WebClient(BrowserVersion.CHROME);
        webClient.getOptions().setUseInsecureSSL(true);//支持https
        webClient.getOptions().setJavaScriptEnabled(true); // 启用JS解释器，默认为true
        webClient.getOptions().setCssEnabled(false); // 禁用css支持
        webClient.getOptions().setThrowExceptionOnScriptError(false); // js运行错误时，是否抛出异常
        webClient.getOptions().setThrowExceptionOnFailingStatusCode(true);
        webClient.setAjaxController(new NicelyResynchronizingAjaxController()); // 设置Ajax异步
        webClient.getOptions().setTimeout(10000); // 设置连接超时时间 ，这里是10S。如果为0，则无限期等待
        webClient.getOptions().setDoNotTrackEnabled(false);
        webClient.setJavaScriptTimeout(8000);//设置js运行超时时间
        webClient.waitForBackgroundJavaScript(500);//设置页面等待js响应时间,
        webClient.getOptions().setJavaScriptEnabled(true);  
        webClient.getOptions().setDownloadImages(false); 
        webClient.waitForBackgroundJavaScript(5000);
        //设置代理  
        ProxyConfig proxyConfig = webClient.getOptions().getProxyConfig();   
        proxyConfig.setProxyHost("127.0.0.1");    
        proxyConfig.setProxyPort(1090);  
        new InterceptWebConnection(webClient);
        //webClient.setWebConnection(interceptWebConnection);
    }
}
