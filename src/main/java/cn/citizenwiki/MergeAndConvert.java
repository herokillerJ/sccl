package cn.citizenwiki;

import cn.citizenwiki.model.dto.paratranz.PZFile;
import cn.citizenwiki.model.dto.paratranz.PZTranslation;
import cn.citizenwiki.paratranz.ParatranzApi;
import cn.citizenwiki.paratranz.processor.translation.OriginalTranslationProcessor;
import cn.citizenwiki.paratranz.processor.translation.TranslationProcessor;
import cn.citizenwiki.utils.GlobalIniUtil;

import java.util.*;

/**
 * Hello world!
 */
public class MergeAndConvert {

    //Paratranz Apibao包装类
    private final ParatranzApi paratranzApi = new ParatranzApi();
    //词条处理器
    private final TranslationProcessor[] translationProcessors =
            new TranslationProcessor[]{new OriginalTranslationProcessor()};

    public static void main(String[] args) {
        new MergeAndConvert().fetchAndMergeTranslations();
    }

    /**
     * 合并 Paratranz 上的所有汉化文件,并调用translationProcessors进行处理
     */
    public void fetchAndMergeTranslations() {
        System.out.println("从 Paratranz 拉取数据...");
        //拉取所有汉化文件
        List<PZFile> pzFiles = paratranzApi.projectFiles();
        System.out.println("拉取到 " + pzFiles.size() + " 个文件");
        if (pzFiles.isEmpty()){
            return;
        }
        //读取原始global.ini文件,顺序与文件中一致
        System.out.println("从global.ini读取数据");
        LinkedHashMap<String, String> globalIniMap = GlobalIniUtil.convertIniToMap("global.ini");
        if (globalIniMap.isEmpty()){
            return;
        }
        System.out.printf("读取到%d行数据%n", globalIniMap.size());
        Map<String, PZTranslation> mergedTranslateMap = mergeTranslateData(globalIniMap, pzFiles);
        if (mergedTranslateMap.isEmpty()){
            return;
        }
        beforProcess();
        process(mergedTranslateMap);
        afterProcess();
    }

    /**
     * 处理合并后的内容,生成各种文件
     * @param mergedTranslateMap
     */
    private void process(Map<String, PZTranslation> mergedTranslateMap) {
        for (Map.Entry<String, PZTranslation> entry : mergedTranslateMap.entrySet()) {
            for (int i = 0; i < translationProcessors.length; i++) {
                try {
                    translationProcessors[i].process(entry.getValue());
                } catch (Exception e) {
                    System.out.printf("processor %s afterProcess方法抛出异常:%s%n%n", translationProcessors[i].getClass().getName(), e.getMessage());
                    e.printStackTrace();
                }
            }
        }
    }

    /**
     * 在所有词条处理完成后调用所有processor的afterProcess方法以让processor进行各自的结束操作
     */
    private void afterProcess() {
        for (int i = 0; i < translationProcessors.length; i++) {
            try {
                translationProcessors[i].afterProcess();
            } catch (Exception e) {
                System.out.printf("processor %s afterProcess方法抛出异常:%s%n%n", translationProcessors[i].getClass().getName(), e.getMessage());
                e.printStackTrace();
            }
        }
    }

    /**
     * 在开始处理所有词条前调用所有processor的beforeProcess方法以让processor进行初始化资源等操作
     */
    private void beforProcess() {
        for (int i = 0; i < translationProcessors.length; i++) {
            try {
                translationProcessors[i].beforeProcess();
            }catch (Exception e){
                System.out.printf("processor %s beforeProcess方法抛出异常:%s%n%n", translationProcessors[i].getClass().getName(), e.getMessage());
                e.printStackTrace();
            }
        }
    }

    /**
     * 合并所有汉化文件,按照key的字典序排序(原来的逻辑)
     * 这里会显得用LinkedHashMap没有必要,不过以防万一用上,就用LinkedHashMap了
     */
    private Map<String, PZTranslation> mergeTranslateData(LinkedHashMap<String, String> globalIniMap, List<PZFile> pzFiles) {
        SequencedSet<String> globalIniSequencedKeySet = globalIniMap.sequencedKeySet();
        Map<String, PZTranslation> mergedTranslateMap = new TreeMap<>();
        for (PZFile pzFile : pzFiles) {
            if (pzFile.getFolder().equals("汉化规则")){
                //跳过非汉化文件
                continue;
            }
            List<PZTranslation> pzTranslations = paratranzApi.fileTranslation(pzFile.getId());
            for (PZTranslation pzTranslation : pzTranslations) {
                // 只处理 global.ini 中存在的 key,这样能够过滤掉已经被删除的key
                if (globalIniSequencedKeySet.contains(pzTranslation.getKey())){
                    mergedTranslateMap.put(pzTranslation.getKey(), pzTranslation);
                }else{
                    System.out.printf("key:%s在global.ini不存在,跳过 %n", pzTranslation.getKey());
                }
            }
        }
        System.out.printf("合并汉化文件后共有%s行数据%n", mergedTranslateMap.size());
        return mergedTranslateMap;
    }


}
