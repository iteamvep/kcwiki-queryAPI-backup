/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.kcwiki.zh.querytrafficmanager;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Set;
import javax.servlet.http.HttpServletRequest;
import org.apache.commons.lang3.StringUtils;
import static org.kcwiki.zh.spider.akashilist.mainpage.dayid;
import redis.clients.jedis.Jedis;

/**
 *
 * @author iTeam_VEP
 */
public class ControlTower {
    
    public JSONObject controller(String channel, HttpServletRequest request) {
        Jedis jedis = org.kcwiki.zh.redis.JedisPoolUtils.getJedis();
        JSONObject result = null ;
        String str = null ;
        String querystring = null ;
        switch(channel){
                default:
                    return null;
                case "area":
                    querystring = "area";
                    if(!jedis.exists(querystring)) {
                        str = JSON.toJSONString(new org.kcwiki.zh.spider.poidb.mainpage().test().clone());
                        result = JSON.parseObject(str);
                        jedis.set(querystring, str);
                    } else {
                        result = JSON.parseObject(jedis.get(querystring));
                    }
                    break;
                case "map":
                    String mapno = request.getParameter("mapno");
                    querystring = "map" + mapno;
                    if(!jedis.exists(querystring)) {
                        str = JSON.toJSONString(new org.kcwiki.zh.spider.poidb.mainpage().test1(mapno).clone());
                        result = JSON.parseObject(str);
                        jedis.set(querystring, str);
                    } else {
                        result = JSON.parseObject(jedis.get(querystring));
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
                        str = JSON.toJSONString(new org.kcwiki.zh.spider.poidb.api().test(mapno,point,difficulty,assessment).clone());
                        result = JSON.parseObject(str);
                        jedis.set(querystring, str);
                    } else {
                        result = JSON.parseObject(jedis.get(querystring));
                    }
                    break; 
                case "expedition":
                    querystring = "expedition" ;
                    if(!jedis.exists(querystring)) {
                        str = JSON.toJSONString(new org.kcwiki.zh.spider.wikiexpedition.mainpage().test().clone());
                        result = JSON.parseObject(str);
                        jedis.set(querystring, str);
                    } else {
                        result = JSON.parseObject(jedis.get(querystring));
                    }
                    break; 
                case "mapfast":
                    querystring = "mapfast" ;
                    if(!jedis.exists(querystring)) {
                        str = JSON.toJSONString(new org.kcwiki.zh.spider.wikimap.fastSearch().test().clone());
                        result = JSON.parseObject(str);
                        jedis.set(querystring, str);
                    } else {
                        result = JSON.parseObject(jedis.get(querystring));
                    }
                    break; 
                case "akashitype":
                    querystring = "akashitype" ;
                    if(!jedis.exists(querystring)) {
                        str = JSON.toJSONString(new org.kcwiki.zh.spider.akashilist.mainpage().getTypeList().clone());
                        result = JSON.parseObject(str);
                        jedis.set(querystring, str);
                    } else {
                        result = JSON.parseObject(jedis.get(querystring));
                    }
                    break;
                case "akashilist":
                    String type = request.getParameter("type");
                    if(type == null || StringUtils.isBlank(type)) type = "all";
                    querystring = "akashilist" + dayid() + type ;
                    if(!jedis.exists(querystring)) {
                        str = JSON.toJSONString(new org.kcwiki.zh.spider.akashilist.mainpage().getItemList(type).clone());
                        result = JSON.parseObject(str);
                        jedis.set(querystring, str);
                    } else {
                        result = JSON.parseObject(jedis.get(querystring));
                    }
                    break;
                case "akashiitem":
                    String wid = request.getParameter("wid");
                    querystring = "akashiitem" + wid ;
                    if(!jedis.exists(querystring)) {
                        HashMap tmp = new org.kcwiki.zh.spider.akashilist.mainpage().getItemDetail(wid);
                        if(tmp == null){
                            tmp = new HashMap<>();
                            tmp.put("failure", "改修不可");
                        }
                        str = JSON.toJSONString(tmp.clone());
                        result = JSON.parseObject(str);
                        jedis.set(querystring, str);
                    } else {
                        result = JSON.parseObject(jedis.get(querystring));
                    }
                    break;   
                case "thankslist":
                    //String name = request.getParameter("name");
                    querystring = "thankslist" ;
                    if(!jedis.exists(querystring)) {
                        str = JSON.toJSONString(new org.kcwiki.zh.spider.akashilist.thankslist().test1().clone());
                        result = JSON.parseObject(str);
                        jedis.set(querystring, str);
                    } else {
                        result = JSON.parseObject(jedis.get(querystring));
                    }
                    break;       
            }
        
        org.kcwiki.zh.redis.JedisPoolUtils.returnRes(jedis);
        return result;
    }
}
