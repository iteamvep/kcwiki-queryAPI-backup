/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.kcwiki.tools.wikitextparser.module;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.StringReader;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.kcwiki.tools.TextUtils;
/**
 *
 * @author iTeam_VEP
 */
public class seasonalgalleryparser {
    
    public ArrayList wikitext2hashmap(String wikitext){
        ArrayList<Gallery> galleryList = new ArrayList<>();
        try {
            boolean needNextLine = false;
            try (BufferedReader br = new BufferedReader(new StringReader(wikitext))) {
                String line = null;
                StringBuilder sb = null;
                while ((line = br.readLine()) != null) {
                    if (line.contains("页头") || line.contains("页尾") || line.contains("页首")) {
                        continue;
                    }
                    
                    if (line.equals("</gallery>")) {
                        break;
                    }

                    if (!line.equals("<gallery>")) {
                        parse(galleryList, line);
                    }
                }
            }
            //print(galleryList);

        } catch (IOException ex) {
            Logger.getLogger(voiceparser.class.getName()).log(Level.SEVERE, null, ex);
        }
        return galleryList;
    }
    
    public static void main(String[] args) {
        List<Gallery> galleryList = new ArrayList<>();

        Scanner scanner = new Scanner(System.in);
        while (scanner.hasNext()) {
            String line = scanner.nextLine();

            if (line.equals("</gallery>")) {
                break;
            }

            if (!line.equals("<gallery>")) {
                parse(galleryList, line);
            }
        }

        //print(galleryList);
    }

    private static Pattern GALLERY_PATTERN = Pattern.compile("([^\\|]+)\\|(.+)");
    private static Pattern GALLERY_PATTERN2 = Pattern.compile("([^\\|]+)");

    public static List parse(List<Gallery> list, String line) {
        Matcher m = GALLERY_PATTERN.matcher(line/*.replace("File:", "文件:")*/);

        if (m.find()) {
            list.add(new Gallery(
                    m.group(1).replace("File:", "").replace("文件:", "").trim(),
                    m.group(2).replaceAll("\\[\\[(.+)]].+\\|.+]]", "$1")/*.replace(" ", "")*/.trim()));
        } else {
            m = GALLERY_PATTERN2.matcher(line);
            if (m.find()) {
                list.add(new Gallery(m.group(1), null));
            }
        }
        return list;
    }

    public static void print(List<Gallery> list) {
        System.out.println("$data = null;");
        System.out.println("$data['type'] = $TYPE_GALLERY;");
        System.out.println("$data['not_safe'] = true;");
        System.out.println("$data['object']['title']['zh_cn'] = \"title\";");
        //System.out.println("$data['object']['summary'] = \"summary\";");
        //System.out.println("$data['object']['content'] = \"content\";");
        System.out.println("$data['object']['urls'] = array(");
        for (Gallery entry : list) {
            System.out.println("\"" + entry.url + "\",");
        }
        System.out.println(");");

        if (list.size() > 0 && !TextUtils.isEmpty(list.get(0).name)) {
            System.out.println("$data['object']['names'] = array(");
            for (Gallery entry : list) {
                System.out.println("\"" + entry.name + "\",");
            }
            System.out.println(");");
        } else {
            System.out.println("$data['object']['names'] = null;");
        }

        System.out.println("array_push($json, $data);");
    }

    private static class Gallery {
        public String url;
        public String name;

        public Gallery(String url, String name) {
            this.url = url;
            this.name = name;
        }
    }
}
