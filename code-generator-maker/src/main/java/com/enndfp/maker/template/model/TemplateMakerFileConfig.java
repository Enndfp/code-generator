package com.enndfp.maker.template.model;

import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

/**
 * 封装所有相关的文件过滤配置信息
 *
 * @author Enndfp
 */
@Data
public class TemplateMakerFileConfig {

    /**
     * 文件过滤信息配置列表
     */
    private List<FileInfoConfig> fileInfoConfigList;

    /**
     * 文件分组配置
     */
    private FileGroupConfig fileGroupConfig;

    @Data
    @NoArgsConstructor
    public static class FileInfoConfig {

        /**
         * 文件路径
         */
        private String path;

        /**
         * 文件过滤配置
         */
        private List<FileFilterConfig> filterConfigList;
    }

    @Data
    @NoArgsConstructor
    public static class FileGroupConfig {

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
    }
}
