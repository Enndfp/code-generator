package com.enndfp.maker.template;

import cn.hutool.core.io.FileUtil;
import cn.hutool.core.io.resource.ResourceUtil;
import cn.hutool.json.JSONUtil;
import com.enndfp.maker.meta.Meta;
import com.enndfp.maker.template.enums.FileFilterRangeEnum;
import com.enndfp.maker.template.enums.FileFilterRuleEnum;
import com.enndfp.maker.template.model.FileFilterConfig;
import com.enndfp.maker.template.model.TemplateMakerConfig;
import com.enndfp.maker.template.model.TemplateMakerFileConfig;
import com.enndfp.maker.template.model.TemplateMakerModelConfig;
import org.junit.Test;

import java.io.File;
import java.util.ArrayList;
import java.util.Collections;

/**
 * 测试同配置多次生成时，强制变为静态生成
 *
 * @author Enndfp
 */
public class TemplateMakerTest {

    @Test
    public void makeTemplateBug1() {
        System.out.println("---------------------------------    测试Spring boot init 项目    ----------------------------------------------------");
        Meta meta = new Meta();
        // 基本信息
        meta.setName("spring boot init ");
        meta.setDescription("spring boot 初始化项目");

        String projectPath = System.getProperty("user.dir");
        String originProjectPath = FileUtil.normalize(new File(projectPath).getParent() + File.separator + "code-generator-demo-projects" + File.separator + "springboot-init");

        String fileInputPath1 = "src/main/resources/application.yml";

        TemplateMakerModelConfig templateMakerModelConfig = new TemplateMakerModelConfig();
        ArrayList<TemplateMakerModelConfig.ModelInfoConfig> modelInfoConfigList = new ArrayList<>();
        TemplateMakerModelConfig.ModelGroupConfig modelGroupConfig = new TemplateMakerModelConfig.ModelGroupConfig();
        modelGroupConfig.setGroupKey("mysql");
        modelGroupConfig.setGroupName("数据库配置");

        TemplateMakerModelConfig.ModelInfoConfig modelInfoConfig1 = new TemplateMakerModelConfig.ModelInfoConfig();
        modelInfoConfig1.setFieldName("url");
        modelInfoConfig1.setType("String");
        modelInfoConfig1.setDescription("数据库url设置");
        modelInfoConfig1.setDefaultValue("jdbc:mysql://localhost:3306/my_db");
        modelInfoConfig1.setAbbr("-h");
        modelInfoConfig1.setReplaceText("jdbc:mysql://localhost:3306/my_db");
        modelInfoConfigList.add(modelInfoConfig1);

        TemplateMakerModelConfig.ModelInfoConfig modelInfoConfig2 = new TemplateMakerModelConfig.ModelInfoConfig();
        modelInfoConfig2.setFieldName("username");
        modelInfoConfig2.setType("String");
        modelInfoConfig2.setDescription("账号");
        modelInfoConfig2.setDefaultValue("root");
        modelInfoConfig2.setAbbr("-u");
        modelInfoConfig2.setReplaceText("root");
        modelInfoConfigList.add(modelInfoConfig2);

        templateMakerModelConfig.setModels(modelInfoConfigList);
        templateMakerModelConfig.setModelGroupConfig(modelGroupConfig);


        TemplateMakerFileConfig makerFileConfig = new TemplateMakerFileConfig();
        TemplateMakerFileConfig.FileInfoConfig fileInfoConfig1 = new TemplateMakerFileConfig.FileInfoConfig();
        fileInfoConfig1.setPath(fileInputPath1);
        ArrayList<FileFilterConfig> configArrayList2 = new ArrayList<>();
        FileFilterConfig filterConfig2 = FileFilterConfig.builder()
                .range(FileFilterRangeEnum.FILE_NAME.getValue())
                .rule(FileFilterRuleEnum.CONTAINS.getValue())
                .value("application")
                .build();
        configArrayList2.add(filterConfig2);
        fileInfoConfig1.setFilterConfigList(configArrayList2);

        makerFileConfig.setFiles(Collections.singletonList(fileInfoConfig1));

        // 配置分组
        TemplateMakerFileConfig.FileGroupConfig fileGroupConfig = new TemplateMakerFileConfig.FileGroupConfig();
        fileGroupConfig.setGroupKey("mysql");
        fileGroupConfig.setGroupName("mysql配置文件");
        fileGroupConfig.setCondition("mysql");
        makerFileConfig.setFileGroupConfig(fileGroupConfig);

        long id = TemplateMaker.makeTemplate(meta, originProjectPath, makerFileConfig, templateMakerModelConfig, null, null);
        id = TemplateMaker.makeTemplate(meta, originProjectPath, makerFileConfig, templateMakerModelConfig, null, id);
        System.out.println("id:" + id);

        System.out.println("---------------------------------    测试完成Spring boot init 项目    ----------------------------------------------------");

    }

    @Test
    public void makeTemplateBug2() {
        System.out.println("-------------------    测试Spring boot init 项目  makeTemplateBug2   ------------------");
        Meta meta = new Meta();
        // 基本信息
        meta.setName("spring boot init ");
        meta.setDescription("spring boot 初始化项目");

        String projectPath = System.getProperty("user.dir");
        String originProjectPath = FileUtil.normalize(new File(projectPath).getParent() + File.separator + "code-generator-demo-projects" + File.separator + "springboot-init");

//        String fileInputPath2 = "src/main/java/com/enndfp/springbootinit/common";
        String fileInputPath2 = "./";

        TemplateMakerModelConfig templateMakerModelConfig = new TemplateMakerModelConfig();
        ArrayList<TemplateMakerModelConfig.ModelInfoConfig> modelInfoConfigList = new ArrayList<>();

        TemplateMakerModelConfig.ModelInfoConfig modelInfoConfig1 = new TemplateMakerModelConfig.ModelInfoConfig();
        modelInfoConfig1.setFieldName("className");
        modelInfoConfig1.setType("String");
        modelInfoConfig1.setDescription("类名替换");
        modelInfoConfig1.setReplaceText("BaseResponse");
        modelInfoConfigList.add(modelInfoConfig1);

        templateMakerModelConfig.setModels(modelInfoConfigList);


        TemplateMakerFileConfig makerFileConfig = new TemplateMakerFileConfig();
        TemplateMakerFileConfig.FileInfoConfig fileInfoConfig = new TemplateMakerFileConfig.FileInfoConfig();
        fileInfoConfig.setPath(fileInputPath2);
        makerFileConfig.setFiles(Collections.singletonList(fileInfoConfig));

        long id = TemplateMaker.makeTemplate(meta, originProjectPath, makerFileConfig, templateMakerModelConfig, null, 1753283378534051840L);
        System.out.println("id:" + id);

        System.out.println("---------------------    测试完成Spring boot init 项目   makeTemplateBug2 -----------------------");
    }

    @Test
    public void makeTemplateWithJSON() {
        String configStr = ResourceUtil.readUtf8Str("templateMaker.json");
        TemplateMakerConfig templateMakerConfig = JSONUtil.toBean(configStr, TemplateMakerConfig.class);
        long id = TemplateMaker.makeTemplate(templateMakerConfig);
        System.out.println(id);
    }

    /**
     * 制作 Spring Boot 模板
     */
    @Test
    public void makeSpringBootTemplate() {
        String rootPath = "examples/springboot-init/";
        String configStr = ResourceUtil.readUtf8Str(rootPath + "templateMaker.json");
        TemplateMakerConfig templateMakerConfig = JSONUtil.toBean(configStr, TemplateMakerConfig.class);
        long id = TemplateMaker.makeTemplate(templateMakerConfig);

        configStr = ResourceUtil.readUtf8Str(rootPath + "templateMaker1.json");
        templateMakerConfig = JSONUtil.toBean(configStr, TemplateMakerConfig.class);
        TemplateMaker.makeTemplate(templateMakerConfig);

        configStr = ResourceUtil.readUtf8Str(rootPath + "templateMaker2.json");
        templateMakerConfig = JSONUtil.toBean(configStr, TemplateMakerConfig.class);
        TemplateMaker.makeTemplate(templateMakerConfig);

        configStr = ResourceUtil.readUtf8Str(rootPath + "templateMaker3.json");
        templateMakerConfig = JSONUtil.toBean(configStr, TemplateMakerConfig.class);
        TemplateMaker.makeTemplate(templateMakerConfig);

        configStr = ResourceUtil.readUtf8Str(rootPath + "templateMaker4.json");
        templateMakerConfig = JSONUtil.toBean(configStr, TemplateMakerConfig.class);
        TemplateMaker.makeTemplate(templateMakerConfig);

        configStr = ResourceUtil.readUtf8Str(rootPath + "templateMaker5.json");
        templateMakerConfig = JSONUtil.toBean(configStr, TemplateMakerConfig.class);
        TemplateMaker.makeTemplate(templateMakerConfig);

        System.out.println(id);
    }
}