package com.acxiom.ams.config;

/**
 * Created by cldong on 12/5/2017.
 */

import com.acxiom.ams.common.exception.AMSInvalidInputException;
import com.acxiom.ams.common.utils.DecryptionUtils;

import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import com.acxiom.ams.common.utils.LogUtils;
import org.springframework.boot.env.PropertySourceLoader;
import org.springframework.core.env.MapPropertySource;
import org.springframework.core.env.PropertySource;
import org.springframework.core.io.Resource;

/**
 * Created by cldong on 11/27/2017.
 */
public class EncodePropertySourceLoader implements PropertySourceLoader {

    @Override
    public String[] getFileExtensions() {
        return new String[]{"properties"};
    }

    @Override
    public PropertySource<?> load(String name, Resource resource, String profile) {
        Map<String, Object> result = mapPropertySource(resource);
        return new MapPropertySource(name, result);
    }

    private Map<String, Object> mapPropertySource(Resource resource) {
        if (resource == null) {
            return null;
        }
        Map<String, Object> result;
        result = readFile(resource);
        return result;
    }

    public Map<String, Object> readFile(Resource resource) {
        Map<String, Object> map = new HashMap<>();
        try (InputStream inputStream = resource.getInputStream()) {
            //Construct BufferedReader from InputStreamReader
            Properties p = new Properties();
            p.load(inputStream);
            Set<String> stringSet = p.stringPropertyNames();
            for (String str : stringSet) {
                String pattern = "^AMS[(](.*)[)]$";
                Pattern r = Pattern.compile(pattern);
                Matcher m = r.matcher(p.getProperty(str));
                if (m.find()) {
                    map.put(str, DecryptionUtils.decrypt(m.group(1)));
                } else {
                    map.put(str, p.getProperty(str));
                }
            }

        } catch (IOException e) {
            LogUtils.error(e);
        } catch (AMSInvalidInputException e) {
            LogUtils.error(e);
        }
        return map;
    }
}
