package com.fs.websocketProject.websocket;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.websocket.*;
import javax.websocket.server.ServerEndpoint;
import java.io.IOException;
import java.util.List;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CopyOnWriteArraySet;
import java.util.concurrent.atomic.AtomicInteger;

@ServerEndpoint("/server")
public class WebSocketServer {

    private final Logger logger = LoggerFactory.getLogger(WebSocketServer.class);

    private static AtomicInteger onlineCount = new AtomicInteger(); //在线人数

    private static CopyOnWriteArraySet<WebSocketServer> set = new CopyOnWriteArraySet<>(); //存放所有websocketServer实例

    private static ConcurrentHashMap<String,WebSocketServer> map = new ConcurrentHashMap<>(); //存放每个用户与websocketServer实例的对应关系

    private Session session; //每个用户对应一个会话

    public WebSocketServer(){}

    @OnOpen
    public void onOpen(Session session){
        this.session = session;
        set.add(this);
        map.put(session.getId(),this);
        addOnlineCount();
        logger.info("新用户{}连接成功，目前在线人数为{}",session,getOnlineCount());
    }

    @OnMessage
    public void onMessage(String message, Session session) throws IOException {
        logger.info("用户{}发来消息：{}",session,message);
        for (WebSocketServer webSocketServer : set) {
            webSocketServer.send(message); //群发消息
        }
    }

    @OnClose
    public void onClose(Session session){
        set.remove(this);
        map.remove(session.getId());
        subOnlineCount();
        logger.info("用户{}断开连接，当前在线人数为{}",session,onlineCount.get());
    }

    @OnError
    public void onError(Session session, Throwable error){
        logger.info("出现异常{}",error);
    }

    public Session getSession(){
        return session;
    }

    public Set<WebSocketServer> getSet(){
        return set;
    }

    public void sendMsg(Session session, String msg) throws IOException {
        session.getBasicRemote().sendText(msg);
    }

    private void subOnlineCount() {
        onlineCount.getAndDecrement();
    }

    public void send(String message) throws IOException {
        session.getBasicRemote().sendText(message);
    }

    private int getOnlineCount() {
        return onlineCount.get();
    }

    private void addOnlineCount() {
        onlineCount.getAndIncrement();
    }


}
