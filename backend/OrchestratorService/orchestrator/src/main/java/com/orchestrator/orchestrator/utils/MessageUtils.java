package com.orchestrator.orchestrator.utils;

import org.json.JSONObject;


public class MessageUtils {

    public static String empacotaMensagem(String message, String payload) {
        JSONObject jsonMessage = new JSONObject();
        jsonMessage.put("message", message);
        jsonMessage.put("payload", payload);
        return jsonMessage.toString();
    }
}