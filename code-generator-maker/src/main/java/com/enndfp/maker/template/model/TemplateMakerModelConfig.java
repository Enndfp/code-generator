package com.enndfp.maker.template.model;

import lombok.Data;

import java.util.List;

/**
 * 封装模型参数
 *
 * @author Enndfp
 */
@Data
public class TemplateMakerModelConfig {

    private List<ModelInfoConfig> models;

    private ModelGroupConfig modelGroupConfig;

    @Data
    public static class ModelInfoConfig {

        /**
         * 模型名称
         */
        private String fieldName;

        /**
         * 模型类型
         */
        private String type;

        /**
         * 模型描述
         */
        private String description;

        /**
         * 默认值
         */
        private Object defaultValue;

        /**
         * 简写
         */
        private String abbr;

        /**
         * 用于替换哪些文本
         */
        private String replaceText;
    }

    @Data
    public static class ModelGroupConfig {

        /**
         * 组的唯一标识
         */
        private String groupKey;

        /**
         * 组的名称
         */
        private String groupName;

        /**
         * 控制该组的生成条件
         */
        private String condition;

        /**
         * 类型
         */
        private String type;

        /**
         * 描述
         */
        private String description;
    }
}
