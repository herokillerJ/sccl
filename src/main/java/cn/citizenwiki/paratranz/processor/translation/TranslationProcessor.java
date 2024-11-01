package cn.citizenwiki.paratranz.processor.translation;

import cn.citizenwiki.model.dto.paratranz.PZTranslation;

public interface TranslationProcessor {

    /**
     * 在所有词条开始处理前调用
     */
    void beforeProcess();

    /**
     * 处理词条
     * @param PZTranslation 词条对象
     */
    void process(PZTranslation PZTranslation);

    /**
     * 在所有词条开始处理后调用
     */
    void afterProcess();



}
