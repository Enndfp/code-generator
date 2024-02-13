package com.enndfp.maker.template.model;

import lombok.Data;

/**
 * 输出规则配置
 *
 * @author Enndfp
 */
@Data
public class TemplateMakerOutputConfig {

    /**
     * 是否从未分组文件配置中移除和已分组文件同名的文件配置
     */
    private boolean removeGroupFileFromRoot = true;
}