package com.hopding.jrpicam.helper;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.Properties;


/**
 * @author hundun
 * Created on 2021/07/27
 */
public class SettingHelper {
    
    private static String BASE_PATH = "config";

    public static void init() {
        Properties settings = getProperties("settings.properties");
        if (settings.containsKey("Language")) {
            LanguageHelper.setLanguage((String) settings.get("Language"));
        }
    }
    
    public static Properties getProperties(String fileName) {
        // load properties
        Properties properties = new Properties();
        try {
            String path = BASE_PATH + File.separator + fileName;
            InputStreamReader reader = new InputStreamReader(new FileInputStream(path), StandardCharsets.UTF_8);
            properties.load(reader);
        } catch (FileNotFoundException e) {
            //logger.error("找不到配置文件!", e);
        } catch (IOException e) {
            //logger.error("I/O错误！读取文件失败！", e);
        }
        return properties;
    }
}
