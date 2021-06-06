package com.app.mg.aoe.upc.WebSocket;

import android.util.Log;

import com.app.mg.connectionlibraryandroid.Implementations.MessageMethods;
import com.app.mg.aoe.upc.Entities.MessageBody;

import org.java_websocket.WebSocket;
import org.java_websocket.client.WebSocketClient;
import org.java_websocket.handshake.ServerHandshake;

import java.net.URI;

public class WebsocketClient extends WebSocketClient {
    MessageMethods<MessageBody,WebsocketClient,WebSocket> messageMethods;

    public WebsocketClient(URI serverUri) {
        super(serverUri);
        messageMethods = new MessageMethods<>();
    }

    @Override
    public void onOpen(ServerHandshake handshakedata) {
    Log.i("OPEN","llega al open");
    }

    @Override
    public void onMessage(String message) {
        MessageBody messageBody = messageMethods.ReceiveMessageBody(message).getBody();
        Log.i("MESSAGE","llega al message");

    }

    @Override
    public void onClose(int code, String reason, boolean remote) {
        Log.i("CLOSe","llega al close");

    }

    @Override
    public void onError(Exception ex) {
        Log.i("ERROR","llega al open: "+ex.getMessage());

    }
}
