package com.enndfp.web.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.enndfp.web.model.entity.Generator;
import org.apache.ibatis.annotations.Select;

import java.util.List;

/**
 * 代码生成器数据库操作
 *
 * @author Enndfp
 */
public interface GeneratorMapper extends BaseMapper<Generator> {

    @Select("SELECT id, distPath FROM generator WHERE isDelete = 1")
    List<Generator> listDeletedGenerator();

}




