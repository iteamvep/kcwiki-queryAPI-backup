/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.kcwiki.tools.wikitextparser.module;

import com.alibaba.fastjson.JSON;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;

/**
 *
 * @author iTeam_VEP
 */
public class controller {
    
    public HashMap filter(Object object,String parameter) {
        HashMap<String,Object> result = new LinkedHashMap<>();
        String wikitext = null;
        if(object instanceof HashMap){
            return (HashMap) object;
        }else if(object instanceof List){
            String title = null;
            if(((List) object).isEmpty()){
                return result;
            }
            String classType = ((List) object).get(0).getClass().toString();
            if(classType.toLowerCase().contains("gallery")) {
                title = "gallery";
            }else if(classType.toLowerCase().contains("voice")) {
                title = "voice";
            }else if(classType.toLowerCase().contains("seasonal") && classType.toLowerCase().contains("voice")) {
                title = "seasonalvoice";
            }else if(classType.toLowerCase().contains("quest")) {
                title = "mission";
            } else {
                return result;
            }
            result.put(title, object);
            return result;
        }else if(object instanceof String){
            wikitext = String.valueOf(object);
        } else {
            return result;
        }
        
        for(;;){
            if(parameter != null){
                switch(parameter){
                    case "gallery":
                        result.put("gallery", new seasonalgalleryparser().wikitext2hashmap(wikitext).clone());
                        break;
                }    
            }
            
            
            if(wikitext.contains("台词翻译表")){
                if(wikitext.contains("type=seasonal")){
                    result.put("seasonalvoice", new seasonalvoiceparser().wikitext2hashmap(wikitext).clone());
                } else {
                    result.put("voice", new voiceparser().wikitext2hashmap(wikitext).clone());
                }
                break;
            }
            if(wikitext.contains("任务表")){
                result.put("mission", new missionparser().wikitext2hashmap(wikitext).clone());
                break;
            }
            
            
            break;
        }
        return result;
    }
}
