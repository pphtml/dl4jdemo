package org.superbiz.util;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class ListMerger {
    public static <T> List<T> mergeLists(List<T> oldList, List<T> newList) {
        if (newList.size() == 0) {
            return oldList;
        } else {
            ArrayList<T> mergedList = new ArrayList<>(newList);
            T lastRecord = mergedList.get(mergedList.size() - 1);
            boolean matchFound = false;
            for (T record : oldList) {
                if (matchFound) {
                    mergedList.add(record);
                } else if (lastRecord.equals(record)) {
                    matchFound = true;
                }
            }
            return mergedList;
        }
    }
}
