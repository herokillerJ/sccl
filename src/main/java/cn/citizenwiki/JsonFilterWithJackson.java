package cn.citizenwiki;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

public class JsonFilterWithJackson {
    public static void main(String[] args) {
        // 初始化 ObjectMapper
        ObjectMapper mapper = new ObjectMapper();
        mapper.enable(SerializationFeature.INDENT_OUTPUT);

        try {
            // 读取数据
            List<Map<String, String>> data = mapper.readValue(
                new File("global.json"),
                new TypeReference<List<Map<String, String>>>() {}
            );

            // 定义规则
            List<String> rules = Arrays.asList("item_Name", "vehicle_Name", "Pyro_JumpPoint_", "Stanton", "Terra_JumpPoint",
                                                "stanton2", "Pyro", "mission_location", "mission_Item", "mission_client", "items_");

            List<Map<String, String>> filteredData = new ArrayList<>();

            // 过滤和更新数据
            for (Map<String, String> item : data) {
                String keyLower = item.get("key").toLowerCase();
                if ((rules.stream().anyMatch(rule -> keyLower.startsWith(rule.toLowerCase()))
                        || keyLower.contains("_repui")
                        || keyLower.endsWith("_from"))
                        && !keyLower.contains("desc")) {
                    item.put("translation", item.get("original"));
                    filteredData.add(item);
                }
            }

            // 写入修改后的数据到文件
            mapper.writerWithDefaultPrettyPrinter()
                  .writeValue(new File("translated_data.json"), filteredData);

            System.out.println("Translation updated and filtered for items with keys matching the rules (case-insensitive) and not containing 'desc' (case-insensitive).");

        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
