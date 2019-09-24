package com.acxiom.ams.mapper.util;

import org.springframework.stereotype.Component;

import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * @Author: Gavin
 * @Description:
 * @Date: 12/7/2017 4:27 PM
 */
@Component
public class Date2String {
    public String toMessageType(Date date) {
        SimpleDateFormat  formatter = new SimpleDateFormat ("yyyy-MM-dd HH:mm:ss");
        return formatter.format(date);
    }
}
