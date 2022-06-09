package com.fs.websocketProject;

import com.fs.websocketProject.websocket.WebSocketServer;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.socket.server.standard.ServerEndpointExporter;

import java.io.IOException;
import java.util.Set;

@RestController
@SpringBootApplication
public class WebsocketProjectApplication {

    @Autowired
    WebSocketServer webSocketServer;

    public static void main(String[] args) {
        SpringApplication.run(WebsocketProjectApplication.class, args);
    }

    @RequestMapping("/test")
    public String test(){
        return "Do Test!";
    }

    @RequestMapping("/sendOne")
    public void sendOne(String id) throws IOException {
        System.out.println(webSocketServer.getSession());
        for (WebSocketServer wss : webSocketServer.getSet()) {
            if (!wss.getSession().getId().equals("123")) {
                wss.sendMsg(wss.getSession(),"这个消息只有你知道。");
            }
        }
    }

    @RequestMapping("/sendAll")
    public void sendAll() throws IOException {
        Set<WebSocketServer> set = webSocketServer.getSet();
        for (WebSocketServer wss : set) {
            wss.send("测试群发消息...");
        }
    }

    @Bean
    public ServerEndpointExporter serverEndpointExporter() {
        return new ServerEndpointExporter();
    }

    @Bean
    public WebSocketServer webSocketServer(){
        return new WebSocketServer();
    }

}
