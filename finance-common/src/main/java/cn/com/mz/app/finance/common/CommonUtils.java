package cn.com.mz.app.finance.common;

import com.google.common.collect.Lists;

import java.util.*;
import java.util.function.Function;

/**
 * @author 马震
 * @version 1.0
 * @date 2025/5/6 14:21
 */

public class CommonUtils {

    public static String DM_LAND_APP_CODE = "theordinaryCrmMao";
    public static String DM_APP_CODE = "theordinaryCrm";

    public static String DM_LAND_APP_ID = "theordinaryCrmMao-Tmall";
    public static String DM_APP_ID= "theordinaryCrm-Tmall";
    public static <T> Set<T> listToSet(List<T> list) {
        Set<T> set = new HashSet<>();
        if (list == null) {
            return set;
        }
        for (T t : list) {
            if (t != null) {
                set.add(t);
            }
        }
        return set;
    }

    public static <T, O> List<T> listToList(List<O> list, Function<O, T> keyGen) {
        if (list == null || keyGen == null) {
            return Collections.emptyList();
        }
        List<T> res = Lists.newArrayListWithExpectedSize(list.size());
        list.forEach(it -> {
            if (it != null) {
                T key = keyGen.apply(it);
                if (key != null) {
                    res.add(key);
                }
            }
        });
        return res;
    }

    public static <K, V> Map<K, V> listToMap(List<V> list, Function<V, K> keyGen) {
        Map<K, V> map = new HashMap<>();
        if (list == null || keyGen == null) {
            return map;
        }
        list.forEach(it -> {
            if (it != null) {
                K key = keyGen.apply(it);
                if (key != null) {
                    map.put(key, it);
                }
            }
        });
        return map;
    }
}
