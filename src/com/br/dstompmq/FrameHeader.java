
package com.br.dstompmq;

import java.util.HashMap;
import java.util.Map;

/**
 *
 * @author dalmir
 */
public class FrameHeader {
    
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
    
    public String get(String key) {
        return map.get(key);
    }
    
    @Override
    public String toString() {
        return map.toString();
    }
}
