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
}
