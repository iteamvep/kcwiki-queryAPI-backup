/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.kcwiki.handler.qtc;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.alibaba.fastjson.TypeReference;
import com.alibaba.fastjson.parser.Feature;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.Set;
import javax.servlet.http.HttpServletRequest;
import org.apache.commons.lang3.StringUtils;
import static org.kcwiki.spider.akashilist.mainpage.dayid;
import redis.clients.jedis.Jedis;

/**
 *
 * @author iTeam_VEP
 */
public class ControlTower {
    
    public HashMap controller(String channel, HttpServletRequest request) {
        Jedis jedis = org.kcwiki.redis.JedisPoolUtils.getJedis();
        String result = null ;
        String querystring = null ;
        switch(channel){
                default:
                    return null;
                case "area":
                    querystring = "area";
                    if(!jedis.exists(querystring)) {
                        result = JSON.toJSONString(new org.kcwiki.spider.poidb.mainpage().test());
                        jedis.set(querystring, result);
                    } else {
                        result = jedis.get(querystring);
                    }
                    break;
                case "map":
                    String mapno = request.getParameter("mapno");
                    querystring = "map" + mapno;
                    if(!jedis.exists(querystring)) {
                        result = JSON.toJSONString(new org.kcwiki.spider.poidb.mainpage().test1(mapno));
                        
                        jedis.set(querystring, result);
                    } else {
                        result = jedis.get(querystring);
                    }
                    break; 
                case "point":
                    mapno = request.getParameter("mapno");
                    String point = request.getParameter("point");
                    if(point.endsWith("null")){
                        point = null;
                    }else if(point.length()>1){
                        //point = point.substring(0,point.indexOf("(B"));
                        point = point.substring(0,1);
                    }
                    String difficulty = request.getParameter("difficulty");
                    String assessment = request.getParameter("assessment");
                    if(Integer.valueOf(mapno) > 300){
                        querystring = "point" + mapno + point + difficulty + assessment ;
                    } else {
                        querystring = "point" + mapno + point ;
                    }
                    if(!jedis.exists(querystring)) {
                        result = JSON.toJSONString(new org.kcwiki.spider.poidb.api().test(mapno,point,difficulty,assessment));
                        jedis.set(querystring, result);
                    } else {
                        result = jedis.get(querystring);
                    }
                    break; 
                case "expedition":
                    querystring = "expedition" ;
                    if(!jedis.exists(querystring)) {
                        result = JSON.toJSONString(new org.kcwiki.spider.wikiexpedition.mainpage().test());
                        jedis.set(querystring, result);
                    } else {
                        result = jedis.get(querystring);
                    }
                    break; 
                case "mapfast":
                    querystring = "mapfast" ;
                    if(!jedis.exists(querystring)) {
                        result = JSON.toJSONString(new org.kcwiki.spider.wikimap.fastSearch().test());
                        jedis.set(querystring, result);
                    } else {
                        result = jedis.get(querystring);
                    }
                    break; 
                case "akashitype":
                    querystring = "akashitype" ;
                    if(!jedis.exists(querystring)) {
                        result = JSON.toJSONString(new org.kcwiki.spider.akashilist.mainpage().getTypeList());
                        jedis.set(querystring, result);
                    } else {
                        result = jedis.get(querystring);
                    }
                    break;
                case "akashilist":
                    String type = request.getParameter("type");
                    if(type == null || StringUtils.isBlank(type)) type = "all";
                    querystring = "akashilist" + dayid() + type ;
                    if(!jedis.exists(querystring)) {
                        result = JSON.toJSONString(new org.kcwiki.spider.akashilist.mainpage().getItemList(type));
                        jedis.set(querystring, result);
                    } else {
                        result = jedis.get(querystring);
                    }
                    break;
                case "akashiitem":
                    String wid = request.getParameter("wid");
                    querystring = "akashiitem" + wid ;
                    if(!jedis.exists(querystring)) {
                        HashMap tmp = new org.kcwiki.spider.akashilist.mainpage().getItemDetail(wid);
                        result = JSON.toJSONString(tmp);
                        jedis.set(querystring, result);
                    } else {
                        result = jedis.get(querystring);
                    }
                    break;   
                case "thankslist":
                    //String name = request.getParameter("name");
                    querystring = "thankslist" ;
                    if(!jedis.exists(querystring)) {
                        result = JSON.toJSONString(new org.kcwiki.spider.akashilist.thankslist().test1());
                        jedis.set(querystring, result);
                    } else {
                        result = jedis.get(querystring);
                    }
                    break;       
            }
        
        org.kcwiki.redis.JedisPoolUtils.returnRes(jedis);
        HashMap<String,Object> data = JSON.parseObject(result,new TypeReference<LinkedHashMap<String, Object>>() {},Feature.OrderedField);
        
        /*JSONObject tmpJobj = new JSONObject(true);
        HashMap<String,Object> tmpHm = new LinkedHashMap<>();
        for(String key:data.keySet()){
            tmpJobj.put(key, data.get(key));
            tmpHm.put(key, data.get(key));
        }
        if(JSON.toJSONString(tmpJobj).equals(JSON.toJSONString(tmpHm))) System.out.println(true);
        if(JSON.toJSONString(tmpJobj).equals(JSON.toJSONString(data))) System.out.println(true);
        if(JSON.toJSONString(tmpHm).equals(JSON.toJSONString(data))) System.out.println(true);*/
        
        return data;
    }
}
