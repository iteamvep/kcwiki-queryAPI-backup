/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.kcwiki.web.view;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import org.eclipse.jetty.util.log.Log;

/**
 *
 * @author iTeam_VEP
 */

//http://www.cnblogs.com/younggun/archive/2013/12/12/3470821.html
public class test extends HttpServlet{
    public static final Set<String> sessionSet = new HashSet<>();

  protected void processRequest(HttpServletRequest request,HttpServletResponse response,String method) throws ServletException,IOException
  {
    response.setContentType("text/xml");
    response.setContentType("text/html;charset=UTF-8;pageEncoding=UTF-8"); 
            
    HashMap<String,Object> data = new LinkedHashMap<>();

        String parameter=request.getParameter("query");
        //parameter = request.getQueryString();
        if(parameter != null){
            HashMap<String,Object> tmp;
            org.kcwiki.handler.qtc.ControlTower controlTower = new org.kcwiki.handler.qtc.ControlTower();
            switch(parameter){
                default:
                    data.put("status", "failure");
                    data.put("data", "请求参数有误。");
                    break;
                case "area":
                    tmp = controlTower.controller("area", request);
                    data.put("status", tmp !=null? "success":"error");
                    data.put("data",tmp !=null? tmp:null);
                    break;
                case "map":
                    tmp = controlTower.controller("map", request);
                    data.put("status", tmp !=null? "success":"error");
                    data.put("data",tmp !=null? tmp:null);
                    break; 
                case "point":
                    tmp = controlTower.controller("point", request);
                    data.put("status", tmp !=null? "success":"error");
                    data.put("data",tmp !=null? tmp:null);
                    break; 
                case "expedition":
                    tmp = controlTower.controller("expedition", request);
                    data.put("status", tmp !=null? "success":"error");
                    data.put("data",tmp !=null? tmp:null);
                    break; 
                case "mapfast":
                    tmp = controlTower.controller("mapfast", request);
                    data.put("status", tmp !=null? "success":"error");
                    data.put("data",tmp !=null? tmp:null);
                    break; 
                case "akashitype":
                    tmp = controlTower.controller("akashitype", request);
                    data.put("status", tmp !=null? "success":"error");
                    data.put("data",tmp !=null? tmp:null);
                    break;
                case "akashilist":
                    tmp = controlTower.controller("akashilist", request);
                    data.put("status", tmp !=null? "success":"error");
                    data.put("data",tmp !=null? tmp:null);
                    break;
                case "akashiitem":
                    tmp = controlTower.controller("akashiitem", request);
                    if(tmp ==null) {
                        data.put("status", "error");
                        data.put("data", null);
                    } else if(tmp.containsKey("failure")) {
                        data.put("status", "failure");
                        data.put("data",tmp.get("failure"));
                    } else {
                        data.put("status", "success");
                        data.put("data",tmp);
                    }
                    break;   
                case "thankslist":
                    tmp = controlTower.controller("thankslist", request);
                    data.put("status", tmp !=null? "success":"error");
                    data.put("data",tmp !=null? tmp:null);
                    break;       
            }
        } else {
            data.put("status", "error");
            data.put("data", "请附带请求参数。");
        }

        //data.put("data", new org.kcwiki.zh.spider.poidb.mainpage().test());
    try (PrintWriter out = response.getWriter()) {
        out.println(JSON.toJSONString(data));
    } catch (Exception e) {
        e.printStackTrace();
        Logger.getLogger(test.class.getName()).log(Level.SEVERE, null, e);
        Logger.getLogger(test.class.getName()).log(Level.WARNING, "客户端异常关闭" , e);
    }
    data.clear();
    
  }

  @Override
  protected void doGet(HttpServletRequest request,HttpServletResponse response) throws ServletException,IOException
  {
    processRequest(request,response,"GET");
  }

  @Override
  protected void doPost(HttpServletRequest request,HttpServletResponse response) throws ServletException,IOException
  {
    processRequest(request,response,"POST");
  }

}
