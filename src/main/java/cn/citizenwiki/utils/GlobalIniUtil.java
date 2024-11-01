package cn.citizenwiki.utils;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.LinkedHashMap;

public class GlobalIniUtil {

    public static LinkedHashMap<String, String> convertIniToMap(String filePath) {
        // 读取并替换 global.ini 文件中的 \u00A0（NBSP）字符
        String iniContent = null;
        try {
            iniContent = Files.readString(Paths.get(filePath), StandardCharsets.UTF_8);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        String modifiedContent = iniContent.replaceAll("(?<!\u00C2)\u00A0", "\u00C2\u00A0");

        // 用于存储键值对的 Map
        LinkedHashMap<String, String> iniMap = new LinkedHashMap<>();

        // 按行解析文件内容
        String[] lines = modifiedContent.split("\n");
        for (String line : lines) {

            // 检查是否包含键值对
            if (line.contains("=")) {
                String[] keyValue = line.split("=", 2); // 分割成键和值
                String key = keyValue[0];
                String value = keyValue[1];
                iniMap.put(key, value);
            }
        }

        return iniMap;
    }

}
