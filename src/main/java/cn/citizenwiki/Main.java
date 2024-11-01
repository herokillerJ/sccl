package cn.citizenwiki;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.*;

public class Main {

    private static final String AUTHORIZATION = System.getenv("AUTHORIZATION");
    private static final ObjectMapper objectMapper = new ObjectMapper();
    private static final HttpClient httpClient = HttpClient.newHttpClient();

    // 从 Paratranz 获取所有文件并拼合为一个 JSON 对象，同时处理 "汉化规则" 文件和 "汉化规则/3d替换.json"
    public static Map<String, Object> fetchAndMergeTranslations() throws Exception {
        System.out.println("Fetching files from Paratranz...");
        ArrayNode files = fetchFilesFromParatranz();

        System.out.println("Fetched " + files.size() + " files.");

        Map<String, Map<String, Object>> mergedData = new HashMap<>();
        Map<String, Map<String, String>> translationRules = new HashMap<>();
        List<String> ruleFiles = new ArrayList<>();
        Map<String, String> replace3dData = null;

        for (JsonNode file : files) {
            String fileName = file.get("name").asText();
            int fileId = file.get("id").asInt();

            System.out.println("Processing file: " + fileName);
            ArrayNode fileData = fetchTranslationData(fileId);

            if ("汉化规则/3d替换.json".equals(fileName)) {
                System.out.println("Found 3d替换.json, processing...");
                replace3dData = new HashMap<>();
                for (JsonNode item : fileData) {
                    replace3dData.put(item.get("key").asText(), item.get("translation").asText());
                }
            } else if ("汉化规则".equals(file.get("folder").asText()) && !fileName.equals("汉化规则/3d替换.json")) {
                System.out.println("Found 汉化规则 file: " + fileName + ", processing...");
                Map<String, String> rule = new HashMap<>();
                for (JsonNode item : fileData) {
                    rule.put(item.get("key").asText(), item.get("translation").asText());
                }
                translationRules.put(fileName, rule);
                ruleFiles.add(fileName);
            } else {
                System.out.println("Merging data from file: " + fileName);
                for (JsonNode item : fileData) {
                    String key = item.get("key").asText();
                    int id = item.get("id").asInt();
                    String translation = item.get("translation").asText();

                    mergedData.computeIfAbsent(key, k -> new HashMap<>()).put("translation", translation);
                    mergedData.computeIfAbsent(key, k -> new HashMap<>()).put("id", id);
                }
            }
        }

        Map<String, Object> result = new HashMap<>();
        result.put("mergedData", mergedData);
        result.put("translationRules", translationRules);
        result.put("ruleFiles", ruleFiles);
        result.put("replace3dData", replace3dData);
        return result;
    }

    // 根据文件 ID 获取翻译数据
    private static ArrayNode fetchTranslationData(int fileId) throws Exception {
        String urlString = "https://paratranz.cn/api/projects/8340/files/" + fileId + "/translation";
        System.out.println("Fetching translation data for file ID: " + fileId);
        String response = makeGetRequest(urlString);
        return (ArrayNode) objectMapper.readTree(response);
    }

    // 获取所有文件
    private static ArrayNode fetchFilesFromParatranz() throws Exception {
        String urlString = "https://paratranz.cn/api/projects/8340/files";
        String response = makeGetRequest(urlString);
        return (ArrayNode) objectMapper.readTree(response);
    }

    // 使用 HttpClient 发送 GET 请求
    private static String makeGetRequest(String urlString) throws Exception {
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(urlString))
                .header("Authorization", AUTHORIZATION)
                .GET()
                .build();

        HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());
        return response.body();
    }

    // 将拼合后的 JSON 和规则应用到 INI 格式，并在开头添加 BOM（EF BB BF）
    private static String convertJsonToIni(Map<String, Map<String, Object>> jsonData, Map<String, String> translationRules) {
        StringBuilder iniContent = new StringBuilder("\uFEFF");

        List<String> sortedKeys = new ArrayList<>(jsonData.keySet());
        Collections.sort(sortedKeys);

        for (String key : sortedKeys) {
            String value = (String) jsonData.get(key).get("translation");
            if (translationRules.containsKey(key)) {
                value = translationRules.get(key);
            }
            iniContent.append(key).append("=").append(value).append("\n");
        }

        return iniContent.toString();
    }

    // 确保目录存在
    private static void ensureDirectoryExistence(String filePath) {
        Path path = Paths.get(filePath).getParent();
        if (path != null && !Files.exists(path)) {
            try {
                Files.createDirectories(path);
                System.out.println("Created directory: " + path);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    public static void main(String[] args) {
        try {
            Map<String, Object> result = fetchAndMergeTranslations();

            Map<String, Map<String, Object>> mergedData = (Map<String, Map<String, Object>>) result.get("mergedData");
            Map<String, Map<String, String>> translationRules = (Map<String, Map<String, String>>) result.get("translationRules");
            List<String> ruleFiles = (List<String>) result.get("ruleFiles");
            Map<String, String> replace3dData = (Map<String, String>) result.get("replace3dData");

            if (replace3dData == null) {
                throw new Exception("汉化规则/3d替换.json 未找到");
            }

            String outputDir = "final_output";
            ensureDirectoryExistence(outputDir);

            for (String ruleFileName : ruleFiles) {
                System.out.println("Generating INI file for rule: " + ruleFileName);
                Map<String, String> rules = translationRules.get(ruleFileName);
                Map<String, String> combinedRules = new HashMap<>(replace3dData);
                combinedRules.putAll(rules);

                String iniContent = convertJsonToIni(mergedData, combinedRules);
                String outputFileName = outputDir + "/final_output_" + ruleFileName.replace("汉化规则/", "").replace(".json", "") + ".ini";

                ensureDirectoryExistence(outputFileName);
                Files.write(Paths.get(outputFileName), iniContent.getBytes("UTF-8"));
                System.out.println("拼合后的翻译内容已转换为 INI 格式并保存到 " + outputFileName);
            }

            System.out.println("Generating final.ini with only 3d替换.json applied.");
            String finalIniContent = convertJsonToIni(mergedData, replace3dData);
            String finalOutputFileName = outputDir + "/final.ini";
            Files.write(Paths.get(finalOutputFileName), finalIniContent.getBytes("UTF-8"));
            System.out.println("Generated final.ini and saved to " + finalOutputFileName);

        } catch (Exception e) {
            System.out.println("发生错误: " + e.getMessage());
            e.printStackTrace();
        }
    }
}
