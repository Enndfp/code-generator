package com.enndfp.web.model.dto.generator;

import com.enndfp.web.meta.Meta;
import lombok.Data;

import java.io.Serializable;
import java.util.List;

/**
 * 更新请求
 *
 * @author Enndfp
 */
@Data
public class GeneratorUpdateRequest implements Serializable {

    /**
     * id
     */
    private Long id;

    /**
     * 名称
     */
    private String name;

    /**
     * 描述
     */
    private String description;

    /**
     * 基础包
     */
    private String basePackage;

    /**
     * 版本
     */
    private String version;

    /**
     * 作者
     */
    private String author;

    /**
     * 标签列表（json 数组）
     */
    private List<String> tags;

    /**
     * 图片
     */
    private String picture;

    /**
     * 文件配置（json字符串）
     */
    private Meta.FileConfig fileConfig;

    /**
     * 模型配置（json字符串）
     */
    private Meta.ModelConfig modelConfig;

    /**
     * 代码生成器产物路径
     */
    private String distPath;

    /**
     * 状态
     */
    private Integer status;

    /**
     * git版本控制
     */
    private Integer versionControl;

    /**
     * 强制交互式开关
     */
    private Integer forcedInteractive;

    private static final long serialVersionUID = 1L;
}