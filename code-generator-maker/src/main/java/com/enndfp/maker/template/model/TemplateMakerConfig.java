package com.enndfp.maker.template.model;

import com.enndfp.maker.meta.Meta;
import lombok.Data;

/**
 * 封装模板生成配置
 *
 * @author Enndfp
 */
@Data
public class TemplateMakerConfig {

    /**
     * 元信息
     */
    private Meta meta = new Meta();

    /**
     * 源项目路径
     */
    private String originProjectPath;

    /**
     * 模板文件生成配置
     */
    private TemplateMakerFileConfig fileConfig = new TemplateMakerFileConfig();

    /**
     * 模板模型生成配置
     */
    private TemplateMakerModelConfig modelConfig = new TemplateMakerModelConfig();

    /**
     * 生成的唯一标识 多次生成同一个id 标识为同一个项目
     */
    private Long id;
}
