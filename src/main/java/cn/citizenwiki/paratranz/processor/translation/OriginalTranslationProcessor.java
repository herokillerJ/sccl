package cn.citizenwiki.paratranz.processor.translation;


import cn.citizenwiki.model.dto.paratranz.PZTranslation;

import java.util.Arrays;
import java.util.List;

/**
 * 符合要求的翻译词条将译文还原成原文
 */
public class OriginalTranslationProcessor implements TranslationProcessor{

    // 定义规则
    List<String> startWithWords = Arrays.stream(new String[]{"item_Name", "vehicle_Name", "Pyro_JumpPoint_", "Stanton", "Terra_JumpPoint",
            "stanton2", "Pyro", "mission_location", "mission_Item", "mission_client", "items_"}).map(String::toLowerCase).toList();

    public boolean needProcess(PZTranslation PZTranslation) {
        String keyLower = PZTranslation.getKey().toLowerCase();
        return ((startWithWords.stream().anyMatch(keyLower::startsWith)
                || keyLower.contains("_repui")
                || keyLower.endsWith("_from"))
                && !keyLower.contains("desc"));
    }

    @Override
    public void beforeProcess() {
        //todo 创建文件,开启输入流
    }

    @Override
    public void process(PZTranslation pzTranslation) {
        if (needProcess(pzTranslation)){
            //还原成原文
            pzTranslation.setTranslation(pzTranslation.getOriginal());
        }
        //todo 写入文件
    }

    @Override
    public void afterProcess() {
        //todo 关闭文件输入流
    }
}
