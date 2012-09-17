package com.br.simplemq;

import java.util.HashMap;
import java.util.Map;

/**
 *
 * @author dalmir
 */
public class FrameHeader {

    public static final String DESTINATION_ENTRY_NAME = "destination",
                ID_ENTRY_NAME = "id",
                MAX_REDELIVERIES_ENTRY_NAME = "max-redeliveries",
                SUBSCRIPTIONS_ENTRY_NAME = "subscription",
                LOGIN_ENTRY_NAME = "login",
                PASSCODE_ENTRY_NAME = "passcode",
                SESSION_ENTRY_NAME = "session",
                RECEIPT_ENTRY_NAME = "receipt",
                RECEIPT_ID_ENTRY_NAME = "receipt-id",
                DELIVERIES_COUNT_ENTRY_NAME = "delivery-count";
    
    Map<String, String> map;

    public FrameHeader() {
        map = new HashMap<String, String>();
    }

    public FrameHeader(Map<String, String> map) {
        this.map = map;
    }

    public Map<String, String> getMap() {
        return map;
    }

    public void setMap(Map<String, String> map) {
        this.map = map;
    }

    public void put(String key, String value) {
        if (map != null) {
            map.put(key, value);
        }
    }

    void put(String key, int value) {
        put(key, Integer.toString(value));
    }

    public String get(String key) {
        return map.get(key);
    }

    @Override
    public String toString() {
        return map.toString();
    }
}
