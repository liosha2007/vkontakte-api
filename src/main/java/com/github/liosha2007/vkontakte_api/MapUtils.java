package com.github.liosha2007.vkontakte_api;

import java.util.HashMap;

/**
 * Created with IntelliJ IDEA.
 * User: liosha
 * Date: 09.11.13
 * Time: 23:27
 * https://gist.github.com/smat/1058578
 */
public class MapUtils {
    public static <K, V> MapBuilder<K, V> asMap(K key, V value) {
        return new MapBuilder<K, V>().entry(key, value);
    }

    public static class MapBuilder<K, V> extends HashMap<K, V> {
        public MapBuilder<K, V> entry(K key, V value) {
            this.put(key, value);
            return this;
        }

        public MapBuilder<K, V> pt(K key, V value) {
            super.put(key,  value);
            return this;
        }
    }
}
