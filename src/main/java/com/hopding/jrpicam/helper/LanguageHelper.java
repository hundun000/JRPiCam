package com.hopding.jrpicam.helper;
/**
 * @author hundun
 * Created on 2021/07/27
 */

import java.util.Properties;

public class LanguageHelper {
    private static Properties languageProperties;
    
    public static void setLanguage(String name) {
        String fileName = "language_" + name + ".properties";
        languageProperties = SettingHelper.getProperties(fileName);
    }
    
    
    public static String tanslate(String text) {
        if (languageProperties == null) {
            return text;
        }
        String result = String.valueOf(languageProperties.getOrDefault(text, text));
        return result;
    }

}
