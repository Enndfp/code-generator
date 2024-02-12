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
import java.util.Arrays;
import java.util.List;
import java.util.function.Function;
import java.util.stream.Collectors;

/**
 * 模版制作器
 *
 * @author Enndfp
 */
public class TemplateMaker {

    /**
     * 工作空间目录
     */
    public static final String WORKSPACE_DIRECTORY = ".temp";
    /**
     * 模板文件后缀
     */
    public static final String TEMPLATE_FILE_SUFFIX = ".ftl";
    /**
     * 元信息文件名
     */
    public static final String META_FILE_NAME = "meta.json";
    /**
     * 当前项目目录
     */
    public static final String USER_DIR = "user.dir";

    public static void main(String[] args) {
//        // 1. 提供输入参数：包括生成器基本信息、原始项目目录、原始文件、模型参数
//        Meta meta = new Meta();
//        // 基本信息
//        meta.setName("acm-template-generator");
//        meta.setDescription("ACM 示例模版生成器");
//
//        String projectPath = System.getProperty(USER_DIR);
//        String originProjectPath = FileUtil.normalize(new File(projectPath).getParent() + File.separator + "code-generator-demo-projects" + File.separator + "acm-template");
//
//        String fileInputPath = "src/com/enndfp/acm/MainTemplate.java";
//
//        // 输入模型参数信息 1
//        Meta.ModelConfig.ModelInfo modelInfo = new Meta.ModelConfig.ModelInfo();
//        modelInfo.setFieldName("outputText");
//        modelInfo.setDefaultValue("Sum: ");
//        modelInfo.setType("String");
//        String searchStr = "Sum: ";
//
//        // 输入模型参数信息 2
//        Meta.ModelConfig.ModelInfo modelInfo2 = new Meta.ModelConfig.ModelInfo();
//        modelInfo2.setFieldName("className");
//        modelInfo2.setDefaultValue("MainTemplate");
//        modelInfo2.setType("String");
//        String searchStr2 = "MainTemplate";
//
//        long id = TemplateMaker.makeTemplate(meta, originProjectPath, fileInputPath, modelInfo, searchStr, null);
//        TemplateMaker.makeTemplate(meta, originProjectPath, fileInputPath, modelInfo2, searchStr2, id);

        System.out.println("---------------------------------    测试Spring boot init 项目    ----------------------------------------------------");
        Meta meta = new Meta();
        // 基本信息
        meta.setName("spring boot init ");
        meta.setDescription("spring boot 初始化项目");

        String projectPath = System.getProperty("user.dir");
        String originProjectPath = FileUtil.normalize(new File(projectPath).getParent() + File.separator + "code-generator-demo-projects" + File.separator + "springboot-init");

        String fileInputPath1 = "src/main/java/com/enndfp/springbootinit/common";
        String fileInputPath2 = "src/main/java/com/enndfp/springbootinit/controller";


        // 输入模型参数信息 1
        Meta.ModelConfig.ModelInfo modelInfo = new Meta.ModelConfig.ModelInfo();
        modelInfo.setFieldName("className");
        modelInfo.setDefaultValue("BaseResponse");
        modelInfo.setType("String");
        String searchStr = "BaseResponse";

        TemplateMaker.makeTemplate(meta, originProjectPath, Arrays.asList(fileInputPath1, fileInputPath2), modelInfo, searchStr, 1L);
    }

    /**
     * 生成模板
     *
     * @param newMeta           元信息
     * @param originProjectPath 原始项目目录
     * @param fileInputPathList 文件输入路径列表
     * @param modelInfo         模型参数
     * @param searchStr         搜索字符串
     * @param id                模板 id
     * @return 模板 id
     */
    public static long makeTemplate(Meta newMeta, String originProjectPath, List<String> fileInputPathList, Meta.ModelConfig.ModelInfo modelInfo, String searchStr, Long id) {
        // 没有 id 则生成
        if (id == null) {
            id = IdUtil.getSnowflakeNextId();
        }
        // 当前项目目录
        String projectPath = System.getProperty(USER_DIR);

        // 判断是否存在模板目录，不存在则创建
        String templatePath = FileUtil.normalize(projectPath + File.separator + WORKSPACE_DIRECTORY + File.separator + id);
        if (!FileUtil.exist(templatePath)) {
            FileUtil.mkdir(templatePath);
            FileUtil.copy(originProjectPath, templatePath, true);
        }

        // 工作空间的目录
        String sourceRootPath = FileUtil.normalize(templatePath + File.separator + FileUtil.getLastPathEle(Paths.get(originProjectPath)));

        // 生成模版文件
        List<Meta.FileConfig.FileInfo> newFileInfoList = new ArrayList<>();
        for (String fileInputPath : fileInputPathList) {
            String inputFileAbsolutePath = sourceRootPath + File.separator + fileInputPath;
            if (FileUtil.isDirectory(inputFileAbsolutePath)) {
                List<File> files = FileUtil.loopFiles(inputFileAbsolutePath);
                for (File file : files) {
                    Meta.FileConfig.FileInfo fileInfo = makeFileTemplate(file, modelInfo, searchStr, sourceRootPath);
                    newFileInfoList.add(fileInfo);
                }
            } else {
                Meta.FileConfig.FileInfo fileInfo = makeFileTemplate(new File(inputFileAbsolutePath), modelInfo, searchStr, sourceRootPath);
                newFileInfoList.add(fileInfo);
            }
        }

        // 3. 使用输入信息来创建 meta.json 元信息文件
        String metaOutputPath = FileUtil.normalize(sourceRootPath + File.separator + META_FILE_NAME);

        // 构造配置参数
        Meta meta;
        if (FileUtil.exist(metaOutputPath)) {
            // 读取元信息文件
            String metaContent = FileUtil.readUtf8String(metaOutputPath);
            meta = JSONUtil.toBean(metaContent, Meta.class);
            meta.getFileConfig().getFiles().addAll(newFileInfoList);
            meta.getModelConfig().getModels().add(modelInfo);

            // 去重
            meta.getFileConfig().setFiles(distinctFiles(meta.getFileConfig().getFiles()));
            meta.getModelConfig().setModels(distinctModels(meta.getModelConfig().getModels()));
        } else {
            meta = newMeta;

            // fileConfig 配置
            Meta.FileConfig fileConfig = new Meta.FileConfig();
            fileConfig.setSourceRootPath(sourceRootPath);
            List<Meta.FileConfig.FileInfo> fileInfoList = new ArrayList<>();
            fileConfig.setFiles(fileInfoList);
            meta.setFileConfig(fileConfig);
            fileInfoList.addAll(newFileInfoList);

            // modelConfig 配置
            Meta.ModelConfig modelConfig = new Meta.ModelConfig();
            List<Meta.ModelConfig.ModelInfo> modelInfoList = new ArrayList<>();
            modelInfoList.add(modelInfo);
            modelConfig.setModels(modelInfoList);
            meta.setModelConfig(modelConfig);
        }

        // 输出 meta.json 元信息文件
        FileUtil.writeUtf8String(JSONUtil.toJsonPrettyStr(meta), metaOutputPath);

        return id;
    }

    /**
     * 制作单个模版
     *
     * @param inputFile      需要制作模板的文件对象
     * @param modelInfo      模型参数
     * @param searchStr      需要替换的字符串
     * @param sourceRootPath 源文件根路径
     * @return 文件信息
     */
    private static Meta.FileConfig.FileInfo makeFileTemplate(File inputFile, Meta.ModelConfig.ModelInfo modelInfo, String searchStr, String sourceRootPath) {
        // 输入文件的绝对路径
        String fileInputAbsolutePath = FileUtil.normalize(inputFile.getAbsolutePath());
        // 输出模板文件的绝对路径
        String fileOutputAbsolutePath = FileUtil.normalize(fileInputAbsolutePath + TEMPLATE_FILE_SUFFIX);

        // 输入文件路径
        String fileInputPath = FileUtil.normalize(fileInputAbsolutePath.replace(sourceRootPath + "/", ""));
        // 输出文件路径
        String fileOutputPath = FileUtil.normalize(fileInputPath + TEMPLATE_FILE_SUFFIX);


        // 2. 基于字符串替换算法，使用模型参数的字段名称来替换原始文件的指定内容，并使用替换后的内容来创建 FTL 动态模板文件
        String fileContent;
        if (FileUtil.exist(fileOutputAbsolutePath)) {
            fileContent = FileUtil.readUtf8String(fileOutputAbsolutePath);
        } else {
            fileContent = FileUtil.readUtf8String(fileInputAbsolutePath);
        }
        String replacement = String.format("${%s}", modelInfo.getFieldName());
        String newFileContent = StrUtil.replace(fileContent, searchStr, replacement);

        // 构造文件信息
        Meta.FileConfig.FileInfo fileInfo = new Meta.FileConfig.FileInfo();
        fileInfo.setInputPath(fileInputPath);
        fileInfo.setOutputPath(fileOutputPath);
        fileInfo.setType(FileTypeEnum.FILE.getValue());
        if (newFileContent.equals(fileContent)) {
            fileInfo.setGenerateType(FileGenerateTypeEnum.STATIC.getValue());
            fileInfo.setOutputPath(fileInputPath);
        } else {
            fileInfo.setGenerateType(FileGenerateTypeEnum.DYNAMIC.getValue());
            FileUtil.writeUtf8String(newFileContent, fileOutputAbsolutePath);
        }
        return fileInfo;
    }

    /**
     * 文件去重
     *
     * @param files 文件列表
     * @return 去重后的文件列表
     */
    public static List<Meta.FileConfig.FileInfo> distinctFiles(List<Meta.FileConfig.FileInfo> files) {
        return new ArrayList<>(files.stream()
                .collect(
                        Collectors.toMap(Meta.FileConfig.FileInfo::getInputPath, Function.identity(), (existing, replacement) -> replacement)
                ).values());
    }

    /**
     * 模型去重
     *
     * @param models 模型列表
     * @return 去重后的模型列表
     */
    public static List<Meta.ModelConfig.ModelInfo> distinctModels(List<Meta.ModelConfig.ModelInfo> models) {
        return new ArrayList<>(models.stream()
                .collect(
                        Collectors.toMap(Meta.ModelConfig.ModelInfo::getFieldName, Function.identity(), (existing, replacement) -> replacement)
                ).values());
    }
}
