package com.enndfp.web.model.dto.generator;

import lombok.Data;

import java.util.Map;

/**
 * 使用代码生成器请求
 *
 * @author Enndfp
 */
@Data
public class GeneratorUseRequest {

    /**
     * 生成器的 id
     */
    private Long id;

    /**
     * 数据模型
     */
    Map<String, Object> dataModel;

    private static final long serialVersionUID = 1L;
}
