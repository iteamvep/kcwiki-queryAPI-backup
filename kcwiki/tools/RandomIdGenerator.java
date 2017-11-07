/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.kcwiki.tools;

import java.util.Random;

/**
 *
 * @author iTeam_VEP
 */
public class RandomIdGenerator {
    
    public static String randGenerator() {
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
    
    public static String randGenerator(int length) {
        String val = "";   

        Random random = new Random();   
        for(int i = 0; i < length; i++)   
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
