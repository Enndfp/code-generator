package com.enndfp.maker.template;

import cn.hutool.core.io.FileUtil;
import cn.hutool.core.util.IdUtil;
import cn.hutool.core.util.StrUtil;
import cn.hutool.json.JSONUtil;
import com.enndfp.maker.meta.Meta;
import com.enndfp.maker.meta.enums.FileGenerateTypeEnum;
import com.enndfp.maker.meta.enums.FileTypeEnum;

import java.io.File;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;

/**
 * 模版制作器
 *
 * @author Enndfp
 */
public class TemplateMaker {

    public static void main(String[] args) {
        // 1. 提供输入参数：包括生成器基本信息、原始项目目录、原始文件、模型参数
        String name = "acm-template-generator";
        String description = "ACM 示例模版生成器";

        // 原始项目目录
        String projectPath = System.getProperty("user.dir");
        String originProjectPath = FileUtil.normalize(new File(projectPath).getParent() + File.separator + "code-generator-demo-projects" + File.separator + "acm-template");

        // 生成一个随机 ID
        long id = IdUtil.getSnowflakeNextId();
        String templatePath = FileUtil.normalize(projectPath + File.separator + ".temp" + File.separator + id);
        if (!FileUtil.exist(templatePath)) {
            FileUtil.mkdir(templatePath);
        }
        FileUtil.copy(originProjectPath, templatePath, true);

        // 原始目录变成工作空间的目录
        String sourceRootPath = FileUtil.normalize(templatePath + File.separator + FileUtil.getLastPathEle(Paths.get(originProjectPath)));

        // 原始文件
        String fileInputPath = "src/com/enndfp/acm/MainTemplate.java";
        String fileOutputPath = FileUtil.normalize(fileInputPath + ".ftl");

        // 输入模型参数信息
        Meta.ModelConfig.ModelInfo modelInfo = new Meta.ModelConfig.ModelInfo();
        modelInfo.setFieldName("outputText");
        modelInfo.setDefaultValue("sum = ");
        modelInfo.setType("String");

        // 2. 基于字符串替换算法，使用模型参数的字段名称来替换原始文件的指定内容，并使用替换后的内容来创建 FTL 动态模板文件
        String fileInputAbsolutePath = FileUtil.normalize(sourceRootPath + File.separator + fileInputPath);
        String fileContent = FileUtil.readUtf8String(fileInputAbsolutePath);
        String replacement = String.format("${%s}", modelInfo.getFieldName());
        String newFileContent = StrUtil.replace(fileContent, "Sum: ", replacement);

        // 输出模板文件
        String fileOutputAbsolutePath = FileUtil.normalize(sourceRootPath + File.separator + fileOutputPath);
        FileUtil.writeUtf8String(newFileContent, fileOutputAbsolutePath);

        // 3. 使用输入信息来创建 meta.json 元信息文件
        String metaOutputPath = FileUtil.normalize(sourceRootPath + File.separator + "meta.json");
        // 构造配置参数
        Meta meta = new Meta();
        // 基本信息
        meta.setName(name);
        meta.setDescription(description);

        // fileConfig
        Meta.FileConfig fileConfig = new Meta.FileConfig();
        fileConfig.setSourceRootPath(sourceRootPath);
        List<Meta.FileConfig.FileInfo> fileInfoList = new ArrayList<>();
        Meta.FileConfig.FileInfo fileInfo = new Meta.FileConfig.FileInfo();
        fileInfo.setInputPath(fileInputPath);
        fileInfo.setOutputPath(fileOutputPath);
        fileInfo.setType(FileTypeEnum.FILE.getValue());
        fileInfo.setGenerateType(FileGenerateTypeEnum.DYNAMIC.getValue());
        fileInfoList.add(fileInfo);
        fileConfig.setFiles(fileInfoList);
        meta.setFileConfig(fileConfig);

        // modelConfig
        Meta.ModelConfig modelConfig = new Meta.ModelConfig();
        List<Meta.ModelConfig.ModelInfo> modelInfoList = new ArrayList<>();
        modelInfoList.add(modelInfo);
        modelConfig.setModels(modelInfoList);
        meta.setModelConfig(modelConfig);

        // 输出 meta.json 元信息文件
        FileUtil.writeUtf8String(JSONUtil.toJsonPrettyStr(meta), metaOutputPath);
    }
}
