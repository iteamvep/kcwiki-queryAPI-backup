/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.kcwiki.web.api;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.ListIterator;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import org.kcwiki.database.DBCenter;
import static org.kcwiki.tools.constant.LINESEPARATOR;

/**
 *
 * @author VEP
 */
public class newquest {
    private StringBuilder sb = null;
    private HashMap<String,JSONObject> slotitemMap = new HashMap<>();
    private HashMap<String,JSONObject> useitemmMap = new HashMap<>();
    //private JSONObject start2 = JSON.parseObject(new Start2Api().GetStart2Api("https://acc.kcwiki.org/start2/"));
    
    public String getData() {
        sb = new StringBuilder();
        if(DBCenter.unknownQuestMap.isEmpty()){
            this.addString("<!DOCTYPE html><html><body>");
            this.addString("没有发现新任务，请等待poi插件收集数据。");
            this.addString("Size:\t"+DBCenter.unknownQuestMap.size());
            this.addString("</body></html>");
        } else {
            this.addString(LINESEPARATOR);
            this.addString("<h1>新任务情报：</h1> "+LINESEPARATOR);
            for(int id:DBCenter.unknownQuestMap.keySet()) {
                if(!DBCenter.unknownQuestCounter.isEmpty()){
                    this.addString("<strong>任务ID：</strong>\t"+id+"\t\t\t提交数量：\t" + DBCenter.unknownQuestCounter.get(id));
                } else {
                    this.addString("<strong>任务ID：</strong>\t"+id+"\t\t\t暂未有人提交");
                }
                this.addString("<strong>任务标题：</strong>\t"+""+DBCenter.unknownQuestMap.get(id).getString("api_title"));
                this.addString("<strong>任务信息：</strong>\t"+""+DBCenter.unknownQuestMap.get(id).getString("api_detail"));
                String questsList = "</br>";
                if(!DBCenter.unknownQuestConjecture.isEmpty()){
                    for(int item:DBCenter.unknownQuestConjecture.get(id)){
                        JSONObject quest = DBCenter.questsMap.get(item);
                        if(quest == null){
                            if(DBCenter.unknownQuestConjecture.get(item) != null)
                                continue;
                            questsList += "ID为： " + item + "的任务" + "</br>"; 
                        } else {
                            questsList += quest.getString("title")+ "</br>"; 
                        }
                    }
                    this.addString("<strong>任务推测前置为：</strong>\t"+questsList);
                }
                this.addString(LINESEPARATOR);
                this.addString(LINESEPARATOR);
            }
            this.addString(LINESEPARATOR);
        }
        
        return sb.toString();
    }
    
    public void addData(String str) {
        if(sb == null){
            sb = new StringBuilder();
        }
        this.addString(str);
    }

    
    private void addString(String str) {
        sb.append(str).append(LINESEPARATOR).append("</br>").append(LINESEPARATOR);
    }
}
