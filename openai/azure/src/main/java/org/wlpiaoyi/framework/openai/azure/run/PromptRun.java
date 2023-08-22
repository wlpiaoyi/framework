package org.wlpiaoyi.framework.openai.azure.run;

import org.wlpiaoyi.framework.openai.azure.client.ChatClient;
import org.wlpiaoyi.framework.openai.azure.client.PromptClient;
import org.wlpiaoyi.framework.openai.azure.client.Utils;

import java.util.Scanner;

/**
 * {@code @author:}         wlpiaoyi
 * {@code @description:}    TODO
 * {@code @date:}           2023/8/18 12:49
 * {@code @version:}:       1.0
 */
public class PromptRun implements Run {

    private final int tag;

    private String serverName;

    PromptRun(int tag, String serverName){
        this.tag = tag;
        this.serverName = serverName;
    }

    public static Run instance(int tag, String serverName) {
        return new PromptRun(tag, serverName);
    }

    @Override
    public int run(String[] args) throws Exception {
        System.out.print("\n文档说明:\n");
        Utils.printBehaviorDoc();
        System.out.println("\n标题:PromptRun " + this.getDescribe() + "\n");
        System.out.println("==============================>");
        System.out.print("我自己:");
        //创建一个扫描器对象，用于接收键盘数据
        Scanner scanner = new Scanner(System.in);
        //nextLine方式接收字符串(可以接收空格)
        //判断用户还有没有输入字符
        while (scanner.hasNextLine()) {
            String str = scanner.nextLine();
            int behaviorValue = Utils.behaviorAnalysis(str);
            if(behaviorValue != 0){
                return behaviorValue;
            }
            try{
                ChatClient.ChatMessage chatMessage = new ChatClient.ChatMessage();
                Utils.waitTimer(chatMessage);
                String rep = PromptClient.req(str, this.serverName);
                chatMessage.setWaitFlagEnd(true);
                System.out.print("\n");
                System.out.println("Chat-gpt:" + rep);
                System.out.println("<==============================\n");
            }catch (Exception e){
                e.printStackTrace();
            }finally {
                System.out.println("==============================>");
            }
            System.out.print("我自己:");
        }
        return 0;
    }

    @Override
    public int getTag() {
        return this.tag;
    }
    @Override
    public String getDescribe() {
        return "问题解答";
    }
}
