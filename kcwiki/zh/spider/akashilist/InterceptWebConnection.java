/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.kcwiki.zh.spider.akashilist;

import com.gargoylesoftware.htmlunit.BrowserVersion;
import com.gargoylesoftware.htmlunit.WebClient;
import com.gargoylesoftware.htmlunit.WebRequest;
import com.gargoylesoftware.htmlunit.WebResponse;
import com.gargoylesoftware.htmlunit.util.FalsifyingWebConnection;
import java.io.IOException;

/**
 *
 * @author iTeam_VEP
 */
class InterceptWebConnection extends FalsifyingWebConnection{
    public InterceptWebConnection(WebClient webClient) throws IllegalArgumentException{
        super(webClient);
    }
    @Override
    public WebResponse getResponse(WebRequest request) throws IOException {
        WebResponse response=super.getResponse(request);
        if(response.getWebRequest().getUrl().toString().contains("google") || response.getWebRequest().getUrl().toString().contains("addthis")){
            return createWebResponse(response.getWebRequest(), "", "application/javascript", 200, "Ok");
        }
        if(response.getWebRequest().getUrl().toString().contains("addthis") || response.getWebRequest().getUrl().toString().contains("addthis")){
            return createWebResponse(response.getWebRequest(), "", "application/javascript", 200, "Ok");
        }
        
        return super.getResponse(request);
    }
}
