package com.dry3.util;import org.apache.commons.lang3.StringUtils;import org.slf4j.Logger;import org.slf4j.LoggerFactory;import java.io.IOException;import java.io.InputStreamReader;import java.util.Properties;/** * Created by dry3 */public class PropertiesUtil {    private static final Logger logger = LoggerFactory.getLogger(PropertiesUtil.class);    private static Properties props;    static {        String fileName = "mmall.properties";        props = new Properties();        try {            props.load(new InputStreamReader(PropertiesUtil.class.getClassLoader().getResourceAsStream(fileName), "UTF-8"));        } catch (IOException e) {            logger.error("配置文件读取异常", e);        }    }    public static String getProperty(String key) {        if (key != null) {            String value = props.getProperty(key.trim());            if (StringUtils.isNotBlank(value)) {                return value.trim();            }        }        return null;    }    public static String getProperty(String key, String defaultValue) {        if (key != null) {            String value = props.getProperty(key.trim());            if (StringUtils.isBlank(value) ) {                if (StringUtils.isNotBlank(defaultValue))                    value = defaultValue;                else                    return null;            }            return value.trim();        }        return null;    }    /*public static String getProperty(String key) {        String value = null;        if (StringUtils.isNotBlank(key)) {            value = props.getProperty(key);            if (StringUtils.isBlank(value)) {                return null;            }        }        return value;    }    public static String getProperty(String key, String defaultValue) {        String value = null;        if (StringUtils.isNotBlank(key)) {            value = props.getProperty(key);            if (StringUtils.isBlank(value)) {                value = defaultValue;            }        }        return value;    }*/}