package pers.futuremac.service;


import com.corundumstudio.socketio.*;
import com.corundumstudio.socketio.listener.ConnectListener;
import com.corundumstudio.socketio.listener.DataListener;
import com.corundumstudio.socketio.listener.DisconnectListener;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import pers.futuremac.domain.ChatItem;
import pers.futuremac.utils.NetUtil;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;
import java.net.SocketException;
import java.util.HashMap;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Created by 前程 on 2015/7/17.
 */
@Service
public class ChatService {

    private final static Logger logger = LoggerFactory.getLogger(ChatService.class);

    private static HashMap<String,UUID> uidSocketMap = new HashMap<String, UUID>();
    private static SocketIOServer server = null;

    private static String hostName = "127.0.0.1";
    private final static int port = 9092;

    @Autowired
    UserService us;

    @PostConstruct
    public void init() throws SocketException {
        //1. init chat server
        hostName= NetUtil.getRealIp();
        Configuration config = new Configuration();
        config.setHostname(hostName);
        config.setPort(port);

        server = new SocketIOServer(config);
        logger.info("Create SoketServer {} listen on {}",hostName,port);

        //login event
        server.addEventListener("login",String.class, new DataListener<String> (){
            @Override
            public void onData(SocketIOClient socketIOClient,String data,AckRequest request){
                //add to use and UUID mapping
                logger.info("user {} login",data);
                uidSocketMap.put(data,socketIOClient.getSessionId());
            }
        });

        //newmessage event
        server.addEventListener("newmessage", ChatItem.class,new DataListener<ChatItem>() {
            @Override
            public void onData(SocketIOClient socketIOClient, ChatItem chatItem, AckRequest request) throws Exception {
                //sendMessageToUser(chatItem);
                //fake response
                us.storeChatItem(chatItem);
                ChatItem ci = new ChatItem();
                ci.from = chatItem.to;
                ci.to = chatItem.from;
                ci.text = "收到你的消息了";
                sendMessageToUser(ci);
                us.storeChatItem(ci);
            }

        });
        //connect event
        server.addConnectListener(new ConnectListener() {
            @Override
            public void onConnect(SocketIOClient socketIOClient) {
                logger.info("user connectd! sessionid: {}, address: {}", socketIOClient.getSessionId(), socketIOClient.getRemoteAddress()) ;
            }
        });

        //disconnect
        server.addDisconnectListener(new DisconnectListener() {
            @Override
            public void onDisconnect(SocketIOClient socketIOClient) {
                logger.info("user disconnected! sessionid: {}, address: {}", socketIOClient.getSessionId(), socketIOClient.getRemoteAddress());
            }
        });

        server.start();
    }

    @PreDestroy
    public void destroy(){
        if(server != null) {
            server.stop();
            server = null;
        }
    }

    public static void sendMessageToUser(ChatItem  item){
        if(uidSocketMap.containsKey(item.to)) {
            //instant message
            UUID uuid = uidSocketMap.get(item.to);
            logger.info("user {} online,send msg  via Socket", item.to);
            server.getClient(uuid).sendEvent("newmessage",item);
        }
        else{
            logger.warn("user {} offline,send msg notification via JPush", item.to);
        }
    }
    public static void main(String[] args) throws SocketException {
        ChatService server = new ChatService();
        server.init();
    }


}
