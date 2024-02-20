package com.enndfp.web.job;

import cn.hutool.core.util.StrUtil;
import com.enndfp.web.manager.CosManager;
import com.enndfp.web.mapper.GeneratorMapper;
import com.enndfp.web.model.entity.Generator;
import com.xxl.job.core.handler.annotation.XxlJob;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.util.List;
import java.util.stream.Collectors;

/**
 * 每日定时清理腾讯云对象存储中的垃圾文件
 *
 * @author Enndfp
 */
@Slf4j
@Component
public class ClearCosJobHandler {

    @Resource
    private CosManager cosManager;

    @Resource
    private GeneratorMapper generatorMapper;

    /**
     * 每天凌晨 0 点执行一次
     */
    @XxlJob("clearCosJobHandler")
    public void clearCosJobHandler() {
        log.info("clearCosJobHandler start");

        // 1. 清理用户上传的模板制作文件
        cosManager.deleteDir("/generator_make_template/");

        // 2. 清理已删除的代码生成器产物包
        List<Generator> generatorList = generatorMapper.listDeletedGenerator();
        List<String> keyList = generatorList.stream().map(Generator::getDistPath)
                .filter(StrUtil::isNotBlank)
                // 去掉前缀斜杠
                .map(distPath -> distPath.substring(1))
                .collect(Collectors.toList());
        cosManager.deleteObjects(keyList);

        log.info("clearCosJobHandler end");
    }

}
