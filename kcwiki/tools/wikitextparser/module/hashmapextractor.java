/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.kcwiki.tools.wikitextparser.module;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/**
 *
 * @author iTeam_VEP
 */
public class hashmapextractor {
    private static final String[] wikitype = {"gallery","voice","seasonalvoice","mission","API","AutoGenerator"};
    private static final List<String> wikitypes = new ArrayList<>();
    
    public HashMap getData(HashMap<String,Object> source){
        HashMap<String,Object> dataMap = new LinkedHashMap<>();
        Object tmpobj ;
        for(String key:wikitype){
            wikitypes.add(key);
        }
        for(String index:source.keySet()){
            tmpobj = source.get(index);
            if (tmpobj instanceof HashMap){
                Object tmp = hashmapextractor.getMap((HashMap<String, Object>) tmpobj);
                dataMap.put(index, tmp);
            } else if (tmpobj instanceof List) {
                dataMap.put(index, tmpobj);
            } else if (tmpobj instanceof String) {
                dataMap.put(index, tmpobj);
            }
        }
        return dataMap;
    }
    
    
    public static Object getMap(HashMap<String,Object> sourceMap){
        HashMap<String,Object> data = new LinkedHashMap<>();
        ArrayList<Object> dataList = new ArrayList<>();
        HashMap<String,Object> tmpMap = new LinkedHashMap<>();
        ArrayList<Object> tmpList = new ArrayList<>();
        Object tmpobj,obj ;
        String title ;
        for(String index:sourceMap.keySet()){
                if(index.contains("AutoGenerator")) {
                    title = "AutoGenerator";
                } else if(index.contains("-") ) {
                    title = index.split("-")[1];
                } else {
                    title = index;
                }
                if(wikitypes.contains(title)) {
                    tmpobj = sourceMap.get(index);
                    if(tmpobj instanceof List){
                        tmpList.addAll((Collection<? extends Object>) tmpobj);
                    } else if(tmpobj instanceof HashMap) {
                        obj = hashmapextractor.getMap((HashMap<String, Object>) tmpobj);
                        if(obj instanceof HashMap){
                            data.putAll((Map<? extends String, ? extends Object>) obj );
                        } else {
                            tmpList.add(obj);
                        }
                    }else {
                        tmpList.add(tmpobj);
                    }
                    
                } else {
                    if(!tmpList.isEmpty()){
                        dataList.add(tmpList.clone());
                        tmpList.clear();
                    }
                    if(sourceMap.get(index) instanceof HashMap ) {
                        tmpobj = hashmapextractor.getMap((HashMap<String, Object>) sourceMap.get(index));
                    } else {
                        tmpobj = sourceMap.get(index);
                    }
                    if(!dataList.isEmpty()) {
                        tmpMap.put(title, tmpobj);
                        dataList.add(tmpMap.clone());
                        data.put(index, dataList.clone());
                        tmpMap.clear();
                        dataList.clear();
                    /*} else if(!tmpMap.isEmpty()) {
                        dataList.add(tmpMap.clone());
                        tmpMap.clear();
                        tmpMap.put(title, tmpobj);
                        dataList.add(tmpMap.clone());
                        data.put(index, dataList.clone());
                        tmpMap.clear();
                        dataList.clear();*/
                    } else {
                        data.put(title, tmpobj);
                    }
                }
        }
        if(!tmpList.isEmpty()){
            return tmpList.clone();
        }
        return data;
    }
    
    
}
