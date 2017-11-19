/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.kcwiki.tools.wikitextparser;

import com.alibaba.fastjson.JSON;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.Reader;
import java.io.StringReader;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Random;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import okhttp3.ResponseBody;
import org.apache.commons.lang3.StringUtils;
import static org.apache.commons.lang3.StringUtils.isBlank;
import org.eclipse.jetty.util.StringUtil;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.kcwiki.spider.wikimap.mainpage;
import retrofit2.Retrofit;

import org.kcwiki.tools.wikitextparser.module.controller;

/**
 *
 * @author iTeam_VEP
 */

//https://zh.kcwiki.org/wiki/User:TrickLin/Sandbox/4
//https://zh.kcwiki.org/wiki/Help:%E5%B8%B8%E7%94%A8%E4%BB%A3%E7%A0%81
//https://zh.kcwiki.org/wiki/%E6%96%B0%E6%89%8B%E5%BC%95%E5%AF%BC
//https://zh.kcwiki.org/wiki/Help:%E7%BC%96%E8%BE%91%E6%8C%87%E5%8D%97
//https://www.18dao.org/Wiki%E8%AF%AD%E6%B3%95%E5%A4%A7%E5%85%A8
public class pageparser {
    private Pattern pattern = null; 
    private Matcher matcher = null; 
    controller wikitext2json = new controller();
    
    public static void main(String[] args) {
        HashMap<String,Object> DataMap = new LinkedHashMap<>();
        HashMap<String,Object> tmpMap = new LinkedHashMap<>();
        try {
            Retrofit retrofit = new Retrofit.Builder()
                    .baseUrl("https://zh.kcwiki.org/")
                    .build();
            
            RetrofitAPI.KcwikiService service = retrofit.create(RetrofitAPI.KcwikiService.class);
            ResponseBody body = service.getPage("任务", "raw").execute().body();
            //ResponseBody body = service.getPage("季节性/2周年纪念", "raw").execute().body();
            String html = body.string();
            /*Document doc = null;
            try {
                doc = Jsoup.connect("https://zh.moegirl.org/index.php?title=舰队Collection&action=raw")
                        .timeout(3000)
                        .get();
            } catch (IOException ex) {
                Logger.getLogger(mainpage.class.getName()).log(Level.SEVERE, null, ex);
            }
            html = doc.text();*/
            //System.out.println(body.string());
            
            HashMap<String,String> catalogMap = new pageparser().splitOrginText(html,"=");
            if(!catalogMap.isEmpty()){
                for(String id:catalogMap.keySet()) {
                    tmpMap = new pageparser().getH2Catalog(catalogMap.get(id));
                    if(tmpMap == null)
                        continue;
                    if(!tmpMap.isEmpty()){
                        DataMap.put(id,tmpMap);
                    }   else {
                        DataMap.put(id, catalogMap.get(id));
                    }
                }
            } else {
                tmpMap = new pageparser().getH2Catalog(html);
                if(!tmpMap.isEmpty()){
                    DataMap.putAll(tmpMap);
                }   else {
                    DataMap.put("MainPage", html);
                }
            }
            org.kcwiki.tools.RWFile.writeLog(JSON.toJSONString(DataMap.clone()),null);
            System.out.print(JSON.toJSONString(DataMap.clone()));
        } catch (IOException ex) {
            Logger.getLogger(pageparser.class.getName()).log(Level.SEVERE, null, ex);
        }
        
    }
    
    public HashMap wikitest2hashmap(String title) {
        HashMap<String,Object> DataMap = new LinkedHashMap<>();
        HashMap<String,Object> tmpMap = new LinkedHashMap<>();
        try {
            Retrofit retrofit = new Retrofit.Builder()
                    .baseUrl("https://zh.kcwiki.org/")
                    .build();
            
            RetrofitAPI.KcwikiService service = retrofit.create(RetrofitAPI.KcwikiService.class);
            //ResponseBody body = service.getPage("岚任务", "raw").execute().body();
            //ResponseBody body = service.getPage("季节性/2周年纪念", "raw").execute().body();
            ResponseBody body = service.getPage(java.net.URLDecoder.decode(title, "utf-8"), "raw").execute().body();
            
        if(java.net.URLDecoder.decode(title, "utf-8").contains("2016年")){
            System.out.print(body);
        }
            //System.out.println(body.string());
            String html = body.string();
            HashMap<String,String> catalogMap = new pageparser().splitOrginText(html,"=");
            if(!catalogMap.isEmpty()){
                for(String id:catalogMap.keySet()) {
                    tmpMap = new pageparser().getH2Catalog(catalogMap.get(id));
                    if(tmpMap == null)
                        continue;
                    if(!tmpMap.isEmpty()){
                        DataMap.put(id,tmpMap);
                    }   else {
                        DataMap.put(id, catalogMap.get(id));
                    }
                }
            } else {
                tmpMap = new pageparser().getH2Catalog(html);
                if(!tmpMap.isEmpty()){
                    DataMap.putAll(tmpMap);
                }   else {
                    DataMap.put("MainPage", html);
                }
            }
            org.kcwiki.tools.RWFile.writeLog(JSON.toJSONString(DataMap.clone()),null);
            return DataMap;
        } catch (IOException ex) {
            Logger.getLogger(pageparser.class.getName()).log(Level.SEVERE, null, ex);
        }
        return null;
    }
    
    public HashMap getH2Catalog(String wikitext) {
        HashMap<String,String> catalogMap = new LinkedHashMap<>();
        HashMap<String,String> subMap = new LinkedHashMap<>();
        HashMap<String,Object> dataMap = new LinkedHashMap<>();
        HashMap<String,Object> tmpMap = new LinkedHashMap<>();
        String title = null;
        if(StringUtil.isBlank(wikitext)){
            return null;
        }
        catalogMap = new pageparser().splitOrginText(wikitext,"==");
        HashMap<String,Object> object;
                if(!catalogMap.isEmpty()){
                    for(String subid:catalogMap.keySet()) {
                        title = null;
                        subMap = new pageparser().getH3Catalog(catalogMap.get(subid));
                        if(subMap == null)
                            continue;
                        if(!subMap.isEmpty()) {
                            for(String id:subMap.keySet()){
                                if(id.contains("API-AutoGenerator-block")){
                                    String parameter = id.split(":")[1];
                                    if(subMap.get(id) instanceof String){
                                        object = wikitext2json.filter(subMap.get(id),parameter);
                                        if(object.isEmpty()){
                                            tmpMap.put(id, subMap.get(id)); 
                                            System.err.println(id+"\t isEmpty");
                                        }else{
                                            title = object.keySet().iterator().next();
                                            tmpMap.put(title, object.get(title)); 
                                        }
                                    }else{
                                        tmpMap.put(id, subMap.get(id));
                                    }
                                } else {
                                    if(subMap.get(id) instanceof String){
                                        object = wikitext2json.filter(subMap.get(id),null);
                                        if(object.isEmpty()){
                                            tmpMap.put(id, subMap.get(id)); 
                                            System.err.println(id+"\t isEmpty");
                                        }else{
                                            title = object.keySet().iterator().next();
                                            tmpMap.put(title, object.get(title)); 
                                        }
                                    }else{
                                        tmpMap.put(id, subMap.get(id));
                                    }
                                }
                            }
                            dataMap.put(subid, tmpMap.clone());
                            tmpMap.clear();
                        } else {
                            if(subid.contains("API-AutoGenerator-block")){
                                String parameter = subid.split(":")[1];
                                if(catalogMap.get(subid) instanceof String){
                                    object = wikitext2json.filter(catalogMap.get(subid),parameter);
                                    if(object.isEmpty()){
                                        dataMap.put(subid, catalogMap.get(subid)); 
                                        System.err.println(subid+"\t isEmpty");
                                    }else{
                                        title = object.keySet().iterator().next();
                                        dataMap.put(title, object.get(title)); 
                                    }
                                }else{
                                    dataMap.put(subid, catalogMap.get(subid));
                                }
                            } else {
                                if(catalogMap.get(subid) instanceof String){
                                    object = wikitext2json.filter(catalogMap.get(subid),null);
                                    if(!object.isEmpty()) {
                                        title = object.keySet().iterator().next();
                                        dataMap.put(title+"-"+subid, object.get(title)); 
                                    } else {
                                        dataMap.put(subid, catalogMap.get(subid));
                                    }
                                }else{
                                    dataMap.put(subid, catalogMap.get(subid));
                                }
                            }
                        }
                    }
                } else {
                    subMap = new pageparser().getH3Catalog(wikitext);
                    if(!subMap.isEmpty()) {
                        for(String id:subMap.keySet()){
                            title = null;
                            if(id.contains("API-AutoGenerator-block")){
                                String parameter = id.split(":")[1];
                                if(subMap.get(id) instanceof String){
                                    object = wikitext2json.filter(subMap.get(id),parameter);
                                    if(object.isEmpty()){
                                        tmpMap.put(id, subMap.get(id)); 
                                        System.err.println(id+"\t isEmpty");
                                    }else{
                                        title = object.keySet().iterator().next();
                                        tmpMap.put(title+"-"+id, object.get(title)); 
                                    }
                                }else{
                                    tmpMap.put(id, subMap.get(id));
                                }
                            } else {
                                if(subMap.get(id) instanceof String){
                                    object = wikitext2json.filter(subMap.get(id),null);
                                    if(object.isEmpty()){
                                        tmpMap.put(id, subMap.get(id)); 
                                        System.err.println(id+"\t isEmpty");
                                    }else{
                                        title = object.keySet().iterator().next();
                                        tmpMap.put(title+"-"+id, object.get(title)); 
                                    }
                                }else{
                                    tmpMap.put(id, subMap.get(id));
                                }
                            }
                        }
                        return tmpMap;
                    } else {
                        if(wikitext instanceof String){
                            object = wikitext2json.filter(wikitext,null);
                            if(!object.isEmpty()){
                                title = object.keySet().iterator().next();
                                dataMap.put(title, object.get(title)); 
                            }
                        }else{
                            dataMap.put("API-AutoGenerator-noneTitlePage", wikitext);
                        }
                    }
                }
        return dataMap;
    }
    
    public HashMap getH3Catalog(String wikitext) {
        HashMap<String,String> catalogMap = new LinkedHashMap<>();
        HashMap<String,String> subMap = new LinkedHashMap<>();
        HashMap<String,Object> dataMap = new LinkedHashMap<>();
        HashMap<String,Object> tmpMap = new LinkedHashMap<>();
        String title = null;
        if(StringUtil.isBlank(wikitext)){
            return null;
        }
        catalogMap = new pageparser().splitOrginText(wikitext,"===");
        HashMap<String,Object> object;
                if(!catalogMap.isEmpty()){
                    for(String subid:catalogMap.keySet()) {
                        title = null;
                        subMap = new pageparser().getH4Catalog(catalogMap.get(subid));
                        if(!subMap.isEmpty()) {
                            for(String id:subMap.keySet()){
                                if(id.contains("API-AutoGenerator-block")){
                                    String parameter = id.split(":")[1];
                                    if(subMap.get(id) instanceof String){
                                        object = wikitext2json.filter(subMap.get(id),parameter);
                                        if(object.isEmpty()){
                                            tmpMap.put(id, subMap.get(id)); 
                                            System.err.println(id+"\t isEmpty");
                                        }else{
                                            title = object.keySet().iterator().next();
                                            tmpMap.put(title, object.get(title)); 
                                        }
                                    }else{
                                        tmpMap.put(id, subMap.get(id));
                                    }
                                } else {
                                    if(subMap.get(id) instanceof String){
                                        object = wikitext2json.filter(subMap.get(id),null);
                                        if(object.isEmpty()){
                                            tmpMap.put(id, subMap.get(id)); 
                                            System.err.println(id+"\t isEmpty");
                                        }else{
                                            title = object.keySet().iterator().next();
                                            tmpMap.put(title, object.get(title)); 
                                        }
                                    }else{
                                        tmpMap.put(id, subMap.get(id));
                                    }
                                }
                            }
                            dataMap.put(subid, tmpMap.clone());
                            tmpMap.clear();
                        } else {
                            if(subid.contains("API-AutoGenerator-block")){
                                String parameter = subid.split(":")[1];
                                if(catalogMap.get(subid) instanceof String){
                                    object = wikitext2json.filter(catalogMap.get(subid),parameter);
                                    if(object.isEmpty()){
                                        dataMap.put(subid, catalogMap.get(subid)); 
                                        System.err.println(subid+"\t isEmpty");
                                    }else{
                                        title = object.keySet().iterator().next();
                                        dataMap.put(title, object.get(title)); 
                                    }
                                }else{
                                    dataMap.put(subid, catalogMap.get(subid));
                                }
                            } else {
                                if(catalogMap.get(subid) instanceof String){
                                    object = wikitext2json.filter(catalogMap.get(subid),null);
                                    if(!object.isEmpty()) {
                                        title = object.keySet().iterator().next();
                                        dataMap.put(title+"-"+subid, object.get(title)); 
                                    } else {
                                        dataMap.put(subid, catalogMap.get(subid));
                                    }
                                }else{
                                    dataMap.put(subid, catalogMap.get(subid));
                                }
                            }
                        }
                    }
                } else {
                    subMap = new pageparser().getH4Catalog(wikitext);
                    if(!subMap.isEmpty()) {
                        for(String id:subMap.keySet()){
                            title = null;
                            if(id.contains("API-AutoGenerator-block")){
                                String parameter = id.split(":")[1];
                                if(subMap.get(id) instanceof String){
                                    object = wikitext2json.filter(subMap.get(id),parameter);
                                    if(object.isEmpty()){
                                        tmpMap.put(id, subMap.get(id)); 
                                        System.err.println(id+"\t isEmpty");
                                    }else{
                                        title = object.keySet().iterator().next();
                                        tmpMap.put(title+"-"+id, object.get(title)); 
                                    }
                                }else{
                                    tmpMap.put(id, subMap.get(id));
                                }
                            } else {
                                if(subMap.get(id) instanceof String){
                                    object = wikitext2json.filter(subMap.get(id),null);
                                    if(object.isEmpty()){
                                        tmpMap.put(id, subMap.get(id)); 
                                        System.err.println(id+"\t isEmpty");
                                    }else{
                                        title = object.keySet().iterator().next();
                                        tmpMap.put(title+"-"+id, object.get(title)); 
                                    }
                                }else{
                                    tmpMap.put(id, subMap.get(id));
                                }
                            }
                        }
                        return tmpMap;
                    } else {
                        if(wikitext instanceof String){
                            object = wikitext2json.filter(wikitext,null);
                            if(!object.isEmpty()){
                                title = object.keySet().iterator().next();
                                dataMap.put(title, object.get(title)); 
                            }
                        }else{
                            dataMap.put("API-AutoGenerator-noneTitlePage", wikitext);
                        }
                    }
                }
        return dataMap;
    }
    
    
    /*
    public HashMap getH3Catalog(String wikitext) {
        HashMap<String,String> catalogMap = new LinkedHashMap<>();
        HashMap<String,String> subMap = new LinkedHashMap<>();
        HashMap<String,Object> dataMap = new LinkedHashMap<>();
        HashMap<String,Object> tmpMap = new LinkedHashMap<>();
        HashMap<String,Object> object;
        String title = null;
        String subtitle = null;
        if(StringUtil.isBlank(wikitext)){
            return null;
        }
        catalogMap = new pageparser().splitOrginText(wikitext,"===");
        if(!catalogMap.isEmpty()){
                    for(String subid:catalogMap.keySet()) {
                        title = null;
                        subMap = new pageparser().getH4Catalog(catalogMap.get(subid));
                        if(!subMap.isEmpty()) {
                            for(String id:subMap.keySet()){
                                if(id.contains("API-AutoGenerator-block")){
                                    String parameter = id.split(":")[1];
                                    object = wikitext2json.filter(subMap.get(id),parameter);
                                    if(object.isEmpty()){
                                        tmpMap.put(id, subMap.get(id)); 
                                        System.err.println(id+"\t isEmpty");
                                    }else{
                                        title = object.keySet().iterator().next();
                                        tmpMap.put(title, object.get(title)); 
                                    }
                                } else {
                                    object = wikitext2json.filter(subMap.get(id),null);
                                    if(object.isEmpty()){
                                        tmpMap.put(id, subMap.get(id)); 
                                        subtitle = id;
                                        System.err.println(id+"\t isEmpty");
                                    }else{
                                        title = object.keySet().iterator().next();
                                        subtitle = id;
                                        tmpMap.put(id, object.get(title)); 
                                    }
                                }
                            }
                            if(title == null){
                                dataMap.put(subid+"-"+subtitle, tmpMap.clone());
                            }else{
                                dataMap.put(subid+"-"+title+"-"+subtitle, tmpMap.clone());
                            }
                            tmpMap.clear();
                        } else {
                            if(subid.contains("API-AutoGenerator-block")){
                                String parameter = subid.split(":")[1];
                                object = wikitext2json.filter(catalogMap.get(subid),parameter);
                                if(object.isEmpty()){
                                    dataMap.put(subid, catalogMap.get(subid));
                                    System.err.println(subid+"\t isEmpty");
                                }else{
                                    title = object.keySet().iterator().next();
                                    dataMap.put(title, object.get(title)); 
                                }
                            } else {
                                object = wikitext2json.filter(catalogMap.get(subid),null);
                                if(object.isEmpty()){
                                    dataMap.put(subid, catalogMap.get(subid)); 
                                }else{
                                    title = object.keySet().iterator().next();
                                    dataMap.put(title+"-"+subid, object.get(title)); 
                                }
                            }
                        }
                    }
                } else {
                    subMap = new pageparser().getH4Catalog(wikitext);
                    if(!subMap.isEmpty()) {
                        for(String id:subMap.keySet()){
                            title = null;
                            if(id.contains("API-AutoGenerator-block")){
                                String parameter = id.split(":")[1];
                                object = wikitext2json.filter(subMap.get(id),parameter);
                                if(object.isEmpty()){
                                    tmpMap.put(id, subMap.get(id)); 
                                    System.err.println(id+"\t isEmpty");
                                }else{
                                    title = object.keySet().iterator().next();
                                    tmpMap.put(title, object.get(title)); 
                                }
                            } else {
                                object = wikitext2json.filter(subMap.get(id),null);
                                if(object.isEmpty()){
                                    tmpMap.put(id, subMap.get(id)); 
                                    System.err.println(id+"\t isEmpty");
                                }else{
                                    title = object.keySet().iterator().next();
                                    tmpMap.put(title+"-"+id, object.get(title)); 
                                }
                            }
                        }
                        return tmpMap;
                    } else {
                        object = wikitext2json.filter(wikitext,null);
                        if(!object.isEmpty()) {
                            title = object.keySet().iterator().next();
                            dataMap.put(title+"-"+"API-AutoGenerator-noneTitlePage", object.get(title)); 
                        }
                    }
                }
        return dataMap;
    }
    */
    
    public HashMap getH4Catalog(String wikitext) {
        HashMap<String,String> catalogMap = null;
        HashMap<String,Object> object;
        HashMap<String,Object> dataMap = new LinkedHashMap<>();
        String title;
        catalogMap = new pageparser().splitOrginText(wikitext,"====");
        if(!catalogMap.isEmpty()){
            for(String id:catalogMap.keySet()){
                if(id.contains("API-AutoGenerator-block")){
                                String parameter = id.split(":")[1];
                                object = wikitext2json.filter(catalogMap.get(id),parameter);
                                if(object.isEmpty()){
                                    dataMap.put(id, catalogMap.get(id)); 
                                    System.err.println(id+"\t isEmpty");
                                }else{
                                    title = object.keySet().iterator().next();
                                    dataMap.put(title, object.get(title)); 
                                }
                } else {
                                object = wikitext2json.filter(catalogMap.get(id),null);
                                if(object.isEmpty()){
                                    dataMap.put(id, catalogMap.get(id)); 
                                    System.err.println(id+"\t isEmpty");
                                }else{
                                    title = object.keySet().iterator().next();
                                    dataMap.put(title+"-"+id, object.get(title)); 
                                }
                }
            }
        }
        return dataMap;
    }
    
    public HashMap splitOrginText(String wikitext,String sectionSignal) {
        String line ;
        HashMap<String,String> pageCatalog = new LinkedHashMap<>();
        
        
        BufferedReader reader = new BufferedReader(new StringReader(wikitext));
        ArrayList<String> lineArray = new ArrayList<>();
        try {
            while ((line = reader.readLine()) != null) {
                lineArray.add(line);
            }
        } catch (IOException ex) {
            Logger.getLogger(pageparser.class.getName()).log(Level.SEVERE, null, ex);
        }
        
        int lastIdCount = -1;
        String idTitle = null;
        int[] idRange = new int[2];
        ArrayList<Object> idRangeList = new ArrayList<>();
        ArrayList<String> idTitleList = new ArrayList<>();
        for(int lineCount = 0; lineCount < lineArray.size(); lineCount++ ) {
            String lineString = lineArray.get(lineCount); 
            if(lineString.startsWith(sectionSignal)) {
                //pattern = Pattern.compile("(=+)"); 
                /*
                if(sectionSignal.equals("=")){
                    pattern = Pattern.compile("^(=)[\\s|\\u4e00-\\u9fa5|\\w|\\p{InHiragana}\\p{InKatakana}\\p{InBopomofo}]+(=)$"); 
                }
                if(sectionSignal.equals("==")){
                    pattern = Pattern.compile("^(==)[\\s|\\u4e00-\\u9fa5|\\w|\\p{InHiragana}\\p{InKatakana}\\p{InBopomofo}]+(==)$"); 
                }
                if(sectionSignal.equals("===")){
                    pattern = Pattern.compile("^(===)[\\s|\\u4e00-\\u9fa5|\\w|\\p{InHiragana}\\p{InKatakana}\\p{InBopomofo}]+(===)$"); 
                }*/    
                pattern = Pattern.compile("^("+sectionSignal+")[\\s|\\u4e00-\\u9fa5|\\w|\\p{InHiragana}\\p{InKatakana}\\p{InBopomofo}]+("+sectionSignal+")$"); 
                matcher = pattern.matcher(lineString);
                if(matcher.find() && matcher.group(1).equals(matcher.group(2))){ 
                    idTitle = lineString.replaceAll(matcher.group(1), "");
                    if(lastIdCount == -1 && lineCount != 0) {
                        idRange[0] = 0;
                        idRange[1] = lineCount - 1;
                        lastIdCount = lineCount;
                        idRangeList.add(idRange.clone());
                        if(lineString.startsWith("<")){
                            
                        }
                        if((lineString.startsWith("<") && lineString.endsWith(">")) && lastIdCount == -1) {
                            idTitleList.add("API-AutoGenerator-block:"+lineString.substring(2, lineString.length()-1));
                        }else{
                            idTitleList.add("headTitle"+"-API-AutoGenerator-randomid-"+randGenerator());
                        }
                    } else if (lastIdCount == -1 && lineCount == 0) {
                        lastIdCount = 0;
                    } else {
                        idRange[0] = lastIdCount + 1;
                        idRange[1] = lineCount - 1;
                        lastIdCount = lineCount;
                        idRangeList.add(idRange.clone());
                    }
                    idTitleList.add(idTitle);
                }
            }
            if(lineString.startsWith("<!--") && lineString.endsWith("-->")) {
                continue;
            }
            if(lineCount == lineArray.size()-1) {
                idRange[0] = lastIdCount + 1;
                idRange[1] = lineCount;
                idRangeList.add(idRange.clone());
                if((lineString.startsWith("</") && lineString.endsWith(">")) && lastIdCount == -1) {
                    idTitleList.add("API-AutoGenerator-block:"+lineString.substring(2, lineString.length()-1));
                } 
            }
        }
        if(!idTitleList.isEmpty()) {
            pageparser pageparser= new pageparser();
            for(int idCount = 0; idCount < idRangeList.size(); idCount++ ) {
                int[] range = (int[]) idRangeList.get(idCount);
                /*if(idTitleList.get(idCount).contains("API-AutoGenerator-block")){
                    pageCatalog.put(idTitleList.get(idCount), pageparser.getIDContent(range[0]+1,range[1]-1,lineArray));
                } else {
                    pageCatalog.put(idTitleList.get(idCount), pageparser.getIDContent(range[0],range[1],lineArray));
                }*/
                pageCatalog.put(idTitleList.get(idCount), pageparser.getIDContent(range[0],range[1],lineArray));
            }
        }
        
        return pageCatalog;
    }
    
    public String getIDContent(int beginCount,int endCount,ArrayList lineList) {
        String result = new String();
        for(int lineCount = beginCount; lineCount <= endCount; lineCount++ ) {
            if(StringUtils.isBlank((CharSequence) lineList.get(lineCount))) {
                continue;
            }
            result += lineList.get(lineCount) + System.getProperty("line.separator");
        }
        return result;
    }
    
    
    public String randGenerator() {
        String val = "";   

        Random random = new Random();   
        for(int i = 0; i < 6; i++)   
        {   
          String charOrNum = random.nextInt(2) % 2 == 0 ? "char" : "num"; // 输出字母还是数字   

          if("char".equalsIgnoreCase(charOrNum)) // 字符串   
          {   
            int choice = random.nextInt(2) % 2 == 0 ? 65 : 97; //取得大写字母还是小写字母   
            val += (char) (choice + random.nextInt(26));   
          }   
          else if("num".equalsIgnoreCase(charOrNum)) // 数字   
          {   
            val += String.valueOf(random.nextInt(10));   
          }   
        }
        return val.toLowerCase();
    }
    
    
    
}
