package de.thi.informatik.edi.simple.websocket;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import jakarta.websocket.OnClose;
import jakarta.websocket.OnError;
import jakarta.websocket.OnMessage;
import jakarta.websocket.OnOpen;
import jakarta.websocket.Session;
import jakarta.websocket.server.PathParam;
import jakarta.websocket.server.ServerEndpoint;


@ServerEndpoint("/{topic}")
public class WebSocketServer {
    List<Session> clients = new ArrayList<>();
    @OnOpen
    public void onOpen(Session session, @PathParam("topic") String topic) throws IOException {
        //session.getAsyncRemote().sendText()
        clients.add(session);
    }

    @OnMessage
    public void onMessage(String message) {
        System.out.println("Message: " + message);
        clients.forEach(el -> el.getAsyncRemote().sendText(message));
    }

    @OnError
    public void onError(Throwable t) {
    }

    @OnClose
    public void onClose(Session session) {
    }
}