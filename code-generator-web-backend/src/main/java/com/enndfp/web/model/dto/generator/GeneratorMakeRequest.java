package com.enndfp.web.model.dto.generator;

import com.enndfp.maker.meta.Meta;
import lombok.Data;

/**
 * 制作代码生成器请求
 *
 * @author Enndfp
 */
@Data
public class GeneratorMakeRequest {

    /**
     * 压缩文件路径
     */
    private String zipFilePath;

    /**
     * 元信息
     */
    private Meta meta;

    private static final long serialVersionUID = 1L;
}
