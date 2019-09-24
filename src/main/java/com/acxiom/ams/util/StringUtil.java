package com.acxiom.ams.util;

import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

/**
 * Created by cldong on 12/22/2017.
 */
public class StringUtil {

    private StringUtil(){}

    public static String parseRegexLike(String key) {
        key = key.replace("\\","\\\\");
        key = key.replace("%", "\\%");
        key = key.replace("_", "\\_");
        key = key.replace("[", "\\[");
        key = key.replace("]", "\\]");
        key = key.replace("|", "\\|");
        key = key.replace("{", "\\{");
        key = key.replace("}", "\\}");
        StringBuilder stringBuilder = new StringBuilder("");
        stringBuilder.append("%");
        stringBuilder.append(key);
        stringBuilder.append("%");
        return stringBuilder.toString();
    }

    public static String getFileNameByPath(String path) {
        String reg = ".*[/,\\\\](.*)";
        Pattern pattern = Pattern.compile(reg);
        Matcher matcher = pattern.matcher(path);
        while (matcher.find()) {
            return matcher.group(1);
        }
        return "";
    }


    public static String getCurrentDate() {
        SimpleDateFormat sdf=new SimpleDateFormat("yyyy-MM-dd");
        return sdf.format( new Date());
    }

    public static String getDateString(Date date) {
        SimpleDateFormat sdf=new SimpleDateFormat("yyyy-MM-dd");
        return sdf.format( date);
    }

    public static String getFormat(Long count) {
        DecimalFormat decimalFormat=new DecimalFormat(",###");
        return decimalFormat.format(count);
    }

    public static String getFormat(Integer count) {
        DecimalFormat decimalFormat=new DecimalFormat(",###");
        return decimalFormat.format(count);
    }

    public static String formatPercent(Double number) {
        DecimalFormat df = new DecimalFormat("######0.00");
        return df.format(number * 100) + "%";
    }

    public static List<Long> parseUniverseIdsToList(String universeIds) {
        try {
            return Arrays.asList(universeIds.split(",")).stream().map(Long::valueOf).collect(Collectors.toList());
        } catch (Exception e) {
            return Collections.emptyList();
        }
    }
}
