package org.wlpiaoyi.framework.openai.azure.run;

import org.wlpiaoyi.framework.openai.azure.client.ChatClient;
import org.wlpiaoyi.framework.openai.azure.client.Utils;
import org.wlpiaoyi.framework.utils.MapUtils;
import org.wlpiaoyi.framework.utils.StringUtils;
import org.wlpiaoyi.framework.utils.ValueUtils;
import org.wlpiaoyi.framework.utils.data.ReaderUtils;
import org.wlpiaoyi.framework.utils.data.WriterUtils;
import org.wlpiaoyi.framework.utils.encrypt.aes.Aes;
import org.wlpiaoyi.framework.utils.gson.GsonBuilder;

import java.io.File;
import java.io.FileInputStream;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Scanner;

/**
 * {@code @author:}         wlpiaoyi
 * {@code @description:}    TODO
 * {@code @date:}           2023/8/18 12:33
 * {@code @version:}:       1.0
 */
public class ChatRun implements Run {

    private static int HISTORY_MESSAGE_NUM = 20;
    private static boolean AUTO_SAVE_MESSAGE = false;
    private final int tag;
    private String serverName;

    private Aes aes;
    public Aes getAes() throws Exception {
        if(this.aes != null){
            return this.aes;
        }
        this.aes = Aes.create().setKey("5d839b6ef1f04051831403a3a7fdb78f").setIV("f9c3bec8b33549c6").load();
        return this.aes;
    }

    ChatRun(int tag, String serverName){
        this.tag = tag;
        this.serverName = serverName;
    }

    public static Run instance(int tag, String serverName) {
        return new ChatRun(tag, serverName);
    }

    public int run(String[] args) throws Exception {

        File configFile = new File(Utils.PROJECT_ROOT_DIRECTORY + "/config");
        if (configFile.exists()){
            String confStr = ReaderUtils.loadString(new FileInputStream(configFile), StandardCharsets.UTF_8);
            Map confMap = Utils.GSON.fromJson(confStr, Map.class);
            HISTORY_MESSAGE_NUM = MapUtils.getInteger(confMap, "history_message_num", 20);
            AUTO_SAVE_MESSAGE = MapUtils.getBoolean(confMap, "auto_save_message", false);
        }

        File dbFile = new File(Utils.PROJECT_ROOT_DIRECTORY + "/db");
        if(!dbFile.exists()){
            dbFile.mkdirs();
        }
        dbFile = new File(dbFile.getAbsolutePath() + "/data");
        ChatClient.ChatBody body = null;
        List<ChatClient.ChatMessage> bodyMessages = null;
        if (dbFile.exists()){
            String str = new String(this.getAes().decrypt(ReaderUtils.loadBytes(dbFile)), StandardCharsets.UTF_8);
            body = Utils.GSON.fromJson(str, ChatClient.ChatBody.class);
            if(ValueUtils.isNotBlank(body.getMessages())){
                bodyMessages = body.getMessages();
            }
        }
        if(body == null) {
            body = new ChatClient.ChatBody();
            body.setUser(StringUtils.getUUID32().substring(0, 10));
        }
        if(ValueUtils.isBlank(bodyMessages)){
            bodyMessages = new ArrayList<>();
        }else{
            StringBuffer sb = new StringBuffer();
            for (ChatClient.ChatMessage message : bodyMessages){
                if("user".equals(message.getRole())){
                    sb.append("\n===============================>\n");
                    sb.append("我自己:");
                    sb.append(message.getContent());
                    sb.append('\n');
                    sb.append("<................................>");
                    sb.append('\n');
                }else{
                    sb.append("Chat-gpt:");
                    sb.append(message.getContent());
                    sb.append('\n');
                    sb.append("<==============================\n");
                }
            }
            sb.append("以上是历史记录\n");
            System.out.println(sb);
        }
        body.setMessages(bodyMessages);

        System.out.println("最多保存历史消息数量:\t" + HISTORY_MESSAGE_NUM
                + "\n自动保存消息:\t\t\t" + AUTO_SAVE_MESSAGE
                + "\n\n文档说明:\n@save\t\t\t\t【手动保存历史消息】");
        Utils.printBehaviorDoc();
        System.out.println("\n标题:ChatRun " + this.getDescribe() + "\n");
        System.out.println("==============================>");
        System.out.print("我自己:");

        //创建一个扫描器对象，用于接收键盘数据
        Scanner scanner = new Scanner(System.in);
        //nextLine方式接收字符串(可以接收空格)
        //判断用户还有没有输入字符
        while (scanner.hasNextLine()) {
            String str = scanner.nextLine();
            if(str.equals("@save")){
                String dbStr = GsonBuilder.gsonDefault().toJson(body);
                WriterUtils.overwrite(dbFile, this.getAes().encrypt(dbStr.getBytes(StandardCharsets.UTF_8)));
                System.out.println("<................................>");
                System.out.println("is save history message");
                System.out.println("<==============================\n");
                System.out.println("==============================>");
                System.out.print("我自己:");
                continue;
            }
            int behaviorValue = Utils.behaviorAnalysis(str);
            if(behaviorValue != 0){
                return behaviorValue;
            }
            ChatClient.ChatMessage chatMessage = new ChatClient.ChatMessage();
            chatMessage.setRole("user");
            chatMessage.setContent(str);
            ChatClient.ChatMessage respMsg;
            try{
                bodyMessages.add(chatMessage);
                Utils.waitTimer(chatMessage);
                respMsg = ChatClient.req(body, this.serverName);
                chatMessage.setWaitFlagEnd(true);
                try {
                    Thread.sleep(200);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                    System.exit(0);
                }
                System.out.print("\n");
                System.out.println("Chat-gpt:" + respMsg.getContent());
                System.out.println("<==============================\n");
            }catch (Exception e){
                System.out.println("");
                e.printStackTrace();
                chatMessage.setWaitFlagEnd(true);
                try {
                    Thread.sleep(200);
                } catch (InterruptedException ex) {
                    e.printStackTrace();
                    System.exit(0);
                }
                bodyMessages.remove(chatMessage);
                continue;
            }finally {
                System.out.println("==============================>");
                System.out.print("我自己:");
            }

            bodyMessages.add(respMsg);
            while (bodyMessages.size() > HISTORY_MESSAGE_NUM){
                bodyMessages.remove(0);
            }
            if(AUTO_SAVE_MESSAGE){
                String dbStr = GsonBuilder.gsonDefault().toJson(body);
                WriterUtils.overwrite(dbFile, this.getAes().encrypt(dbStr.getBytes(StandardCharsets.UTF_8)));
            }
        }

        return 0;
    }

    @Override
    public int getTag() {
        return this.tag;
    }

    @Override
    public String getDescribe() {
        return "聊天对话";
    }
}
