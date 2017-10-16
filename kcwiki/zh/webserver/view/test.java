/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.kcwiki.zh.webserver.view;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.HashMap;
import java.util.HashSet;
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
            
    HashMap<String, Object> data = new  HashMap<>();

        String parameter=request.getParameter("query");
        //parameter = request.getQueryString();
        if(parameter != null){
            String mapno;
            Object tmp;
            org.kcwiki.zh.querytrafficmanager.ControlTower controlTower = new org.kcwiki.zh.querytrafficmanager.ControlTower();
            switch(parameter){
                default:
                    data.put("status", "failure");
                    data.put("data", "请求参数有误。");
                    break;
                case "area":
                    tmp = controlTower.controller("area", request).clone();
                    data.put("status", tmp !=null? "success":"error");
                    data.put("data",tmp !=null? tmp:null);
                    break;
                case "map":
                    tmp = controlTower.controller("map", request).clone();
                    data.put("status", tmp !=null? "success":"error");
                    data.put("data",tmp !=null? tmp:null);
                    break; 
                case "point":
                    tmp = controlTower.controller("point", request).clone();
                    data.put("status", tmp !=null? "success":"error");
                    data.put("data",tmp !=null? tmp:null);
                    break; 
                case "expedition":
                    tmp = controlTower.controller("expedition", request).clone();
                    data.put("status", tmp !=null? "success":"error");
                    data.put("data",tmp !=null? tmp:null);
                    break; 
                case "mapfast":
                    tmp = controlTower.controller("mapfast", request).clone();
                    data.put("status", tmp !=null? "success":"error");
                    data.put("data",tmp !=null? tmp:null);
                    break; 
                case "akashitype":
                    tmp = controlTower.controller("akashitype", request).clone();
                    data.put("status", tmp !=null? "success":"error");
                    data.put("data",tmp !=null? tmp:null);
                    break;
                case "akashilist":
                    JSONObject jobj = controlTower.controller("akashilist", request);
                    if(jobj ==null) {
                        data.put("status", "error");
                        data.put("data", null);
                    } else if(jobj.containsKey("failure")) {
                        data.put("status", "failure");
                        data.put("data",jobj.get("failure"));
                    } else {
                        data.put("status", "success");
                        data.put("data",jobj.clone());
                    }
                    break;
                case "akashiitem":
                    tmp = controlTower.controller("akashiitem", request).clone();
                    data.put("status", tmp !=null? "success":"error");
                    data.put("data",tmp !=null? tmp:null);
                    break;   
                case "thankslist":
                    tmp = controlTower.controller("thankslist", request).clone();
                    data.put("status", tmp !=null? "success":"error");
                    data.put("data",tmp !=null? tmp:null);
                    break;       
            }
        } else {
            data.put("status", "error");
            data.put("data", "请附带请求参数。");
        }

        //data.put("data", new org.kcwiki.zh.spider.poidb.mainpage().test().clone());
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
