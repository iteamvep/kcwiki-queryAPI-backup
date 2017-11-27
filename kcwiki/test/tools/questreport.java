/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.kcwiki.test.tools;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.kcwiki.database.DBCenter;
import org.kcwiki.tools.Start2Api;
import org.kcwiki.tools.constant;

/**
 *
 * @author iTeam_VEP
 */
public class questreport {
    private static Set<Integer> idSet = new HashSet<>();
    //private static HashMap<Integer,JSONObject> questsMap = new LinkedHashMap<>();
    
    public static boolean init() {
        String alldata = new Start2Api().GetStart2Api(constant.getKcdata_quest());
        /*if(alldata == null){
            return false;
        }*/
        List<Integer> Bd = new ArrayList<>();
        List<Integer> Bw = new ArrayList<>();
        List<Integer> Bm = new ArrayList<>();
        List<Integer> Bq = new ArrayList<>();
        JSONArray jarr = JSON.parseArray(alldata);
        for(Object obj:jarr){
            JSONObject jobj = (JSONObject) obj;
            idSet.add(jobj.getInteger("id"));
            DBCenter.questsMap.put(jobj.getInteger("id"), jobj);
            if(jobj.getString("wiki_id").contains("Bd")){
                Bd.add(jobj.getInteger("id"));
            }
            if(jobj.getString("wiki_id").contains("Bw")){
                Bw.add(jobj.getInteger("id"));
            }
            if(jobj.getString("wiki_id").contains("Bm")){
                Bm.add(jobj.getInteger("id"));
            }
            if(jobj.getString("wiki_id").contains("Bq")){
                Bq.add(jobj.getInteger("id"));
            }
        }
        idSet.remove(101);
        return true;
    }
    
    public String test(String list,String map,Boolean isdirection) {
        
        Set<Integer> playerQuests = new HashSet<>();
        String result = null;
        JSONArray questsmap = null;
        if(map != null){
            questsmap = JSON.parseArray(map);
        }
        if(isdirection){
            for(Object item:questsmap){
                JSONArray questList = (JSONArray) item;
                if(questList.getInteger(0) == null){
                    System.err.println(new Date() + JSON.toJSONString(questsmap));
                    continue;
                }
                int id = questList.getInteger(0);
                JSONObject quest = questList.getJSONObject(1);
                playerQuests.add(id);
                if(!idSet.contains(id)){
                    DBCenter.unknownQuestMap.put(id, (JSONObject) quest.clone());
                    if(DBCenter.unknownQuestCounter.containsKey(id)){
                        int count = DBCenter.unknownQuestCounter.get(id);
                        DBCenter.unknownQuestCounter.put(id, ++count);
                    }else{
                        DBCenter.unknownQuestCounter.put(id, 1);
                    }
                }
            }
            ArrayList<Integer> guessList = new ArrayList();
            for(int id:DBCenter.unknownQuestMap.keySet()){
                Set<Integer> guessSet = DBCenter.unknownQuestConjecture.get(id);
                if(guessSet == null){
                    for(int p_id:playerQuests){
                        if(DBCenter.questsMap.containsKey(p_id)){
                            JSONArray arr = DBCenter.questsMap.get(p_id).getJSONArray("prerequisite");
                            for(Object pre_id:arr){
                                guessList.add((Integer) pre_id);
                            }
                        }else{
                            guessList.add((p_id * p_id));
                        }
                    }
                    DBCenter.unknownQuestConjecture.put(id, List2Set((List<Integer>) guessList.clone()));
                } else {
                    List<Integer> tmp = new ArrayList();
                    for(int p_id:playerQuests){
                        if(DBCenter.questsMap.containsKey(p_id)){
                            JSONArray arr = DBCenter.questsMap.get(p_id).getJSONArray("prerequisite");
                            for(Object pre_id:arr){
                                tmp.add((Integer) pre_id);
                            }
                        }else{
                            tmp.add((p_id * p_id));
                        }
                    }
                    guessSet.retainAll(tmp);
                    guessList.addAll(guessSet);
                    DBCenter.unknownQuestConjecture.put(id, List2Set((List<Integer>) guessList.clone()));
                }
                guessList.clear();
            }
            result = "Finish";
        }else{
            JSONArray questslist = JSON.parseArray(list);
            HashMap<Integer,JSONObject> tmpMap = new HashMap<>();
            for(Object item:questsmap){
                JSONArray questList = (JSONArray) item;
                tmpMap.put((Integer)questList.get(0),(JSONObject) questList.get(1));
            }
            for(Object item:questslist){
                int id = (int) item;
                if(!idSet.contains(id)){
                    result = "Continue";
                    if(tmpMap.containsKey(id)){
                        DBCenter.unknownQuestMap.put(id, (JSONObject) tmpMap.get(id).clone());
                    }
                }
            }
        }
        return result;
    }
    
    private Set<Integer> List2Set(List<Integer> list) {
        Set<Integer> tmp = new HashSet<>();
        for(int i:list){
            tmp.add(i);
        }
        return tmp;
    }
    
    public static void main(String[] args) {
        new questreport().init();
        JSON.toJSONString(idSet);
    }
}
