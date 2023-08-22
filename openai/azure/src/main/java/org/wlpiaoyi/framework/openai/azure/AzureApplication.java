package org.wlpiaoyi.framework.openai.azure;

import org.wlpiaoyi.framework.openai.azure.client.Utils;
import org.wlpiaoyi.framework.openai.azure.run.Run;
import org.wlpiaoyi.framework.utils.MapUtils;
import org.wlpiaoyi.framework.utils.data.ReaderUtils;
import org.wlpiaoyi.framework.utils.exception.BusinessException;

import java.io.File;
import java.io.FileInputStream;
import java.nio.charset.StandardCharsets;
import java.util.Map;

import static org.wlpiaoyi.framework.openai.azure.client.Utils.RUN_LIST;

/**
 * {@code @author:}         wlpiaoyi
 * {@code @description:}    TODO
 * {@code @date:}           2023/8/18 12:49
 * {@code @version:}:       1.0
 */
public class AzureApplication {

    public static void main(String[] args) throws Exception {
        int index = 0;
        int value = 0;

        File configFile = new File(Utils.PROJECT_ROOT_DIRECTORY + "/config");
        if(configFile.exists() && configFile.isFile()){
            Map configMap = Utils.GSON.fromJson(ReaderUtils.loadString(new FileInputStream(configFile), StandardCharsets.UTF_8), Map.class);
            value = MapUtils.getInteger(configMap, "server_index", 0);
        }
        while (true){
            try{
                if(value == 0){
                    Run run = RUN_LIST.get((index % 2));
                    value = run.run(args);
                }else if (value == -1) {
                    System.out.println("bye bye !!");
                    Thread.sleep(1000);
                    System.exit(0);
                } else if(value > 0 && value <= 100){
                    if(value > RUN_LIST.size()){
                        value = value % RUN_LIST.size();
                    }
                    Run run = RUN_LIST.get(value - 1);
                    value = run.run(args);
                }else{
                    throw new BusinessException("value出现异常:" + value);
                }
            }catch (Exception e){
                e.printStackTrace();
                try{
                    Thread.sleep(500);
                }catch (Exception ex){
                    ex.printStackTrace();
                    System.exit(0);
                }
            }finally {
                index ++;
            }
        }
    }

}