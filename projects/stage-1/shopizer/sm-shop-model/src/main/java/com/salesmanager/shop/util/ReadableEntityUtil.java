package com.salesmanager.shop.util;

import com.salesmanager.shop.model.entity.ReadableEntityList;
import org.springframework.data.domain.Page;

import java.util.List;

public final class ReadableEntityUtil {

    private ReadableEntityUtil() {}

    public static  <T,R> ReadableEntityList<R> createReadableList(Page<T> availabilities, List<R> items) {
        ReadableEntityList<R> readableList = new ReadableEntityList<>();
        readableList.setItems(items);
        readableList.setTotalPages(availabilities.getTotalPages());
        readableList.setRecordsTotal(availabilities.getTotalElements());
        readableList.setNumber(availabilities.getNumber());
        return readableList;
    }
}
