package org.wlpiaoyi.framework.openai.azure.client;

import com.google.gson.Gson;
import org.wlpiaoyi.framework.openai.azure.run.ChatRun;
import org.wlpiaoyi.framework.openai.azure.run.PromptRun;
import org.wlpiaoyi.framework.openai.azure.run.Run;
import org.wlpiaoyi.framework.utils.ValueUtils;
import org.wlpiaoyi.framework.utils.data.ReaderUtils;
import org.wlpiaoyi.framework.utils.data.WriterUtils;
import org.wlpiaoyi.framework.utils.encrypt.aes.Aes;
import org.wlpiaoyi.framework.utils.exception.BusinessException;
import org.wlpiaoyi.framework.utils.gson.GsonBuilder;

import java.io.File;
import java.io.FileNotFoundException;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * {@code @author:}         wlpiaoyi
 * {@code @description:}    TODO
 * {@code @date:}           2023/8/17 15:19
 * {@code @version:}:       1.0
 */
public class Utils {

    public final static Gson GSON = GsonBuilder.gsonDefault();
    public final static String PROJECT_ROOT_DIRECTORY;
    final static Map<String, Map<String, String>> AUTH_MAP;
    final static String AUTH_MAP_OPENAI_KEY = "AZURE_OPENAI_KEY";
    final static String AUTH_MAP_OPENAI_ENDPOINT = "AZURE_OPENAI_ENDPOINT";
    final static String AUTH_MAP_MODEL_ID = "AZURE_MODEL_ID";
    public static final List<Run> RUN_LIST = new ArrayList(){{
        add(ChatRun.instance(1, "tag.001"));
        add(PromptRun.instance(2, "tag.002"));
    }};

    static {

        String active = System.getenv("wlpiaoyi.openai.active");
        if(ValueUtils.isBlank(active)){
            active = "prod";
        }
        if(active.equals("prod")){
            PROJECT_ROOT_DIRECTORY = System.getProperty("user.dir").replaceAll("\\\\", "/") + "/A7F41D";
        }else {
            PROJECT_ROOT_DIRECTORY = System.getProperty("user.dir").replaceAll("\\\\", "/") + "/_tmp_/openai/azure";
        }

        Map<String, Map<String, String>> authMap = null;
        try {

            File authFile = new File(PROJECT_ROOT_DIRECTORY + "/auth-" + active);
            if(!authFile.exists()){
                throw new FileNotFoundException("Auth file path is not found: " + authFile.getAbsolutePath());
            }

            Aes aes = Aes.create().setKey("5d839b6ef1f04051831403a3a7fdb78f").setIV("f9c3bec8b33549c6").load();
            byte[] authBuffer = ReaderUtils.loadBytes(authFile);
            if("prod".equals(active)){
                authBuffer = aes.decrypt(authBuffer);
            }else if("dev".equals(active)){
                System.out.println("auth dev:\n\t" + new String(authBuffer, StandardCharsets.UTF_8));
            }else if("dev-aes".equals(active)){
                System.out.println("auth dev-aes:\n\t" + new String(authBuffer, StandardCharsets.UTF_8));
                WriterUtils.overwrite(new File(PROJECT_ROOT_DIRECTORY + "/auth-prod"), aes.encrypt(authBuffer));
            }
            authMap = GSON.fromJson(new String(authBuffer), Map.class);
        } catch (Exception e) {
            e.printStackTrace();
            authMap = new HashMap<>(0);
            System.exit(0);
        }
        AUTH_MAP = authMap;

    }

    public static void printBehaviorDoc(){
        System.out.println("@exit\t\t\t\t【退出软件】");
        for (Run run : RUN_LIST){
            System.out.println("@goto " + run.getTag() + "\t\t\t\t【" + run.getDescribe() + "】");
        }
    }

    public static int behaviorAnalysis(String str){
        if(str.equals("@exit")){
            return -1;
        }

        if(str.startsWith("@goto")){
            String[] strs = str.split(" ");
            Integer tag = new Integer(strs[1]);
            return tag;
        }

        return 0;
    }

    public interface WaitTimer{
        int getWaitTimer();
        void setWaitTimer(int waitTimer);
        boolean isWaitFlagEnd();
    }

    public static void waitTimer(WaitTimer waitTimer){
        new Thread(() -> {
            long initTimer = System.currentTimeMillis();
            int s = 0;
            waitTimer.setWaitTimer(0);
            while (!waitTimer.isWaitFlagEnd()){
                long currentTimer = System.currentTimeMillis();
                try {
                    Thread.sleep(100);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                    break;
                }
                if(currentTimer - initTimer >= s * 1000){
                    waitTimer.setWaitTimer(++s);
                    System.out.print(".");
                }
                if(s > 120){
                    throw new BusinessException(500, "已经超时");
                }
            }
        }).start();
    }

}
