
package org.kcwiki.tools;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.RandomAccessFile;
import java.net.ConnectException;
import java.net.HttpURLConnection;
import java.net.InetSocketAddress;
import java.net.Proxy;
import java.net.SocketTimeoutException;
import java.net.URL;
import java.net.UnknownHostException;
import org.apache.http.HttpEntity;
import org.apache.http.HttpHost;
import org.apache.http.HttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.conn.params.ConnRoutePNames;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.commons.lang3.StringUtils;
        
public class DlCore {
    
    public int download(String furl, String fPath, String folder, String proxyhost, int proxyport) {
        if(furl==null|| StringUtils.isBlank(furl)){return 2;}
        if(!furl.contains("http")){return 2;}
        
        try{
            URL serverUrl = new URL(furl);
            HttpURLConnection urlcon;
            if(proxyhost != null){
                Proxy tempproxy = new Proxy(Proxy.Type.HTTP,new InetSocketAddress(proxyhost, proxyport));
                //Proxy tempproxy = new Proxy(Proxy.Type.SOCKS,new InetSocketAddress(proxyhost, proxyport));
                urlcon = (HttpURLConnection) serverUrl.openConnection(tempproxy);
            }else{
                urlcon = (HttpURLConnection) serverUrl.openConnection();
            }
            urlcon.setConnectTimeout(3*1000);
            urlcon.setRequestMethod("HEAD");

            urlcon.connect();

            int responseCode = urlcon.getResponseCode();//文件存在‘HTTP/1.1 200 OK’ 文件不存在 ‘HTTP/1.1 404 Not Found’
            if (responseCode==404) {urlcon.disconnect();return 2;}
            if(responseCode<=300){
                urlcon.disconnect();
                if  (!((new File(folder).exists()) || (new File(folder).isDirectory()))) { 
                    new File(folder) .mkdirs();
                }

                try{
                    final RandomAccessFile file = new RandomAccessFile(fPath, "rw");
                    org.apache.http.client.HttpClient client = new DefaultHttpClient();
                    HttpGet get = new HttpGet(furl);
                    if(proxyhost != null){
                        HttpHost proxy=new HttpHost(proxyhost, proxyport);
                        client.getParams().setParameter(ConnRoutePNames.DEFAULT_PROXY, proxy); 
                    }	
                    client.getParams().setParameter("http.socket.timeout", 15*1000); 
                    HttpResponse response = client.execute(get);

                    if (300 >= response.getStatusLine().getStatusCode()) {
                        HttpEntity en = response.getEntity();
                        try (InputStream in = en.getContent()) {
                            byte[] by = new byte[512];
                            int len = -1;

                            // 开始下载
                            while (-1 != (len = in.read(by))) {
                                file.write(by, 0, len);
                            }
                        }
                        file.close();
                        client.getConnectionManager().shutdown();
                        return 1;
                    }else{
                        return 2;
                    }
                }catch(UnknownHostException e){
                    urlcon.disconnect();
                    System.err.println("downloader"+"下载端口出错,请检测网络连接。");
                    return 0;
                }catch(ConnectException e){
                    urlcon.disconnect();
                    System.err.println("downloader"+"下载文件连接出错。");
                    return 2;
                }catch(SocketTimeoutException e){
                    urlcon.disconnect();
                    e.printStackTrace();
                    System.err.println("downloader"+"下载文件发生超时。");
                    return 2;
                }catch(IOException | UnsupportedOperationException e){
                    urlcon.disconnect();
                    e.printStackTrace();
                    System.err.println("downloader"+"下载文件时发生IOException错误。");
                    return 2;
                }    
            }
            
        }catch(UnknownHostException e){
            System.err.println("downloader"+"下载端口出错,请检测网络连接。（HEAD）");
            return 0;
        }catch(ConnectException e){
            System.err.println("downloader"+"下载文件连接超时。（HEAD）");
            return 2;
        }catch(IOException | UnsupportedOperationException e){
            System.err.println("downloader"+"下载文件时发生IOException错误。（HEAD）");
            return 2;
        } 

        return 2;
    }
}
