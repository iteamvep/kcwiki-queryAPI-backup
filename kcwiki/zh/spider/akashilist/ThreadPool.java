/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.kcwiki.zh.spider.akashilist;

import java.util.HashMap;
import java.util.concurrent.Callable;
import java.util.concurrent.CompletionService;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorCompletionService;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author iTeam_VEP
 */
public class ThreadPool {
    private static  ExecutorService cachedThreadPool;  
    private static CompletionService<Integer> cs ;
    private static HashMap<Integer,String> taskList;
    private static AtomicInteger taskNum ;
    private static boolean isInit = false;
    
    public static boolean ininPool(){
        if(isInit){
            return false;
        }
        cachedThreadPool = Executors.newCachedThreadPool();  
        cs = new ExecutorCompletionService<>(getCachedThreadPool());
        taskNum = new AtomicInteger(0);
        taskList = new HashMap<>();
        isInit = true;
        return isInit;
    }
    
    public static void addTask(Runnable task,int taskID,String taskName) {
        if(!isInit){
            return;
        }
        cs.submit(task, taskID);
        taskNum.getAndIncrement();
        taskList.put(taskID, taskName);
    }
    
    public static boolean takeTask(){
        for(int taskid=0;taskid<taskNum.get();){
            try {
                Future<Integer> task=cs.take();
                if(task.get() != null){
                    taskid++;
                }
            } catch (InterruptedException ex) {
                Logger.getLogger(ThreadPool.class.getName()).log(Level.SEVERE, null, ex);
                taskid++;
            } catch (ExecutionException ex) {
                Logger.getLogger(ThreadPool.class.getName()).log(Level.SEVERE, null, ex);
                taskid++;
            }
        }
        return true;
    }
    
    public static boolean shutdown(){
        cachedThreadPool.shutdown(); 
        try {
          if (taskNum.get() >0 && !cachedThreadPool.awaitTermination(30, TimeUnit.SECONDS)) {
            cachedThreadPool.shutdownNow(); 
            if (!cachedThreadPool.awaitTermination(60, TimeUnit.SECONDS))
                System.err.println("Pool did not terminate");
          }
        } catch (InterruptedException ie) {
            // (Re-)Cancel if current thread also interrupted
            cachedThreadPool.shutdownNow();
            // Preserve interrupt status
            Thread.currentThread().interrupt();
        }
        return true;
    }
    /**
     * @return the cachedThreadPool
     */
    public static ExecutorService getCachedThreadPool() {
        return cachedThreadPool;
    }
}
