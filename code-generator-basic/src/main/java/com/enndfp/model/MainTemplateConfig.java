package com.enndfp.model;

import lombok.Data;

/**
 * 动态模版数据配置
 *
 * @author Enndfp
 */
@Data
public class MainTemplateConfig {

    /**
     * 是否生成循环
     */
    private boolean loop;

    /**
     * 作者
     */
    private String author = "Enndfp";

    /**
     * 输出信息
     */
    private String outputText = "求和结果: ";
}
