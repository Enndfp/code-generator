package com.enndfp.maker.template;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.io.FileUtil;
import cn.hutool.core.util.IdUtil;
import cn.hutool.core.util.StrUtil;
import cn.hutool.json.JSONUtil;
import com.enndfp.maker.meta.Meta;
import com.enndfp.maker.meta.enums.FileGenerateTypeEnum;
import com.enndfp.maker.meta.enums.FileTypeEnum;
import com.enndfp.maker.template.enums.FileFilterRangeEnum;
import com.enndfp.maker.template.enums.FileFilterRuleEnum;
import com.enndfp.maker.template.model.FileFilterConfig;
import com.enndfp.maker.template.model.TemplateMakerConfig;
import com.enndfp.maker.template.model.TemplateMakerFileConfig;
import com.enndfp.maker.template.model.TemplateMakerModelConfig;

import java.io.File;
import java.nio.file.Paths;
import java.util.*;
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

    /**
     * 生成模板
     *
     * @param templateMakerConfig 模板制作配置
     * @return id
     */
    public static long makeTemplate(TemplateMakerConfig templateMakerConfig) {
        Meta meta = templateMakerConfig.getMeta();
        String originProjectPath = templateMakerConfig.getOriginProjectPath();
        TemplateMakerFileConfig templateMakerFileConfig = templateMakerConfig.getFileConfig();
        TemplateMakerModelConfig templateMakerModelConfig = templateMakerConfig.getModelConfig();
        Long id = templateMakerConfig.getId();
        return makeTemplate(meta, originProjectPath, templateMakerFileConfig, templateMakerModelConfig, id);
    }

    /**
     * 生成模板
     *
     * @param newMeta                  元信息
     * @param originProjectPath        原始项目目录
     * @param templateMakerFileConfig  原始文件列表 + 过滤配置
     * @param templateMakerModelConfig 原始模型参数列表 + 替换配置
     * @param id                       模板 id
     * @return 模板 id
     */
    public static long makeTemplate(Meta newMeta,
                                    String originProjectPath,
                                    TemplateMakerFileConfig templateMakerFileConfig,
                                    TemplateMakerModelConfig templateMakerModelConfig,
                                    Long id) {
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
        String sourceRootPath = FileUtil.normalize(FileUtil.loopFiles(new File(templatePath), 1, null)
                .stream()
                .filter(File::isDirectory)
                .findFirst()
                .orElseThrow(RuntimeException::new)
                .getAbsolutePath());

        // 生成模版文件
        List<Meta.FileConfig.FileInfo> newFileInfoList = makeFileTemplates(templateMakerFileConfig, templateMakerModelConfig, sourceRootPath);

        // 处理模型信息
        List<Meta.ModelConfig.ModelInfo> newModelInfoList = getModelInfoList(templateMakerModelConfig);

        // 3. 使用输入信息来创建 meta.json 元信息文件
        String metaOutputPath = FileUtil.normalize(templatePath + File.separator + META_FILE_NAME);

        // 构造配置参数
        Meta meta;
        if (FileUtil.exist(metaOutputPath)) {
            // 读取元信息文件
            String metaContent = FileUtil.readUtf8String(metaOutputPath);
            meta = JSONUtil.toBean(metaContent, Meta.class);
            meta.getFileConfig().getFiles().addAll(newFileInfoList);
            meta.getModelConfig().getModels().addAll(newModelInfoList);

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
            modelInfoList.addAll(newModelInfoList);
            modelConfig.setModels(modelInfoList);
            meta.setModelConfig(modelConfig);
        }

        // 输出 meta.json 元信息文件
        FileUtil.writeUtf8String(JSONUtil.toJsonPrettyStr(meta), metaOutputPath);

        return id;
    }

    /**
     * 获取模型配置
     *
     * @param templateMakerModelConfig 模型配置
     * @return 模型信息
     */
    private static List<Meta.ModelConfig.ModelInfo> getModelInfoList(TemplateMakerModelConfig templateMakerModelConfig) {
        List<Meta.ModelConfig.ModelInfo> newModelInfoList = new ArrayList<>();
        // 非空校验
        if (templateMakerModelConfig == null) {
            return newModelInfoList;
        }

        List<TemplateMakerModelConfig.ModelInfoConfig> models = templateMakerModelConfig.getModels();
        if (CollUtil.isEmpty(models)) {
            return newModelInfoList;
        }

        // 处理模型信息
        List<Meta.ModelConfig.ModelInfo> inputModelInfoList = models.stream().map(modelInfoConfig -> {
            Meta.ModelConfig.ModelInfo modelInfo = new Meta.ModelConfig.ModelInfo();
            BeanUtil.copyProperties(modelInfoConfig, modelInfo);
            return modelInfo;
        }).collect(Collectors.toList());
        TemplateMakerModelConfig.ModelGroupConfig modelGroupConfig = templateMakerModelConfig.getModelGroupConfig();
        if (modelGroupConfig != null) {
            String groupKey = modelGroupConfig.getGroupKey();
            String groupName = modelGroupConfig.getGroupName();
            String condition = modelGroupConfig.getCondition();
            Meta.ModelConfig.ModelInfo groupModelInfo = new Meta.ModelConfig.ModelInfo();
            groupModelInfo.setGroupName(groupName);
            groupModelInfo.setGroupKey(groupKey);
            groupModelInfo.setCondition(condition);
            groupModelInfo.setModels(inputModelInfoList);
            newModelInfoList.add(groupModelInfo);
        } else {
            // 不分组
            newModelInfoList.addAll(inputModelInfoList);
        }
        return newModelInfoList;
    }

    /**
     * 生成多个文件
     *
     * @param templateMakerFileConfig  文件配置
     * @param templateMakerModelConfig 模型配置
     * @param sourceRootPath           源文件路径
     * @return 文件列表
     */
    private static List<Meta.FileConfig.FileInfo> makeFileTemplates(TemplateMakerFileConfig templateMakerFileConfig, TemplateMakerModelConfig templateMakerModelConfig, String sourceRootPath) {
        List<Meta.FileConfig.FileInfo> newFileInfoList = new ArrayList<>();
        // 非空校验
        if (templateMakerFileConfig == null) {
            return newFileInfoList;
        }

        List<TemplateMakerFileConfig.FileInfoConfig> infoConfigList = templateMakerFileConfig.getFiles();
        if (CollUtil.isEmpty(infoConfigList)) {
            return newFileInfoList;
        }

        // 生成模版文件
        for (TemplateMakerFileConfig.FileInfoConfig fileInfoConfig : infoConfigList) {
            String fileInputPath = fileInfoConfig.getPath();
            String inputFileAbsolutePath = FileUtil.normalize(sourceRootPath + File.separator + fileInputPath);

            List<File> fileList = FileFilter.doFilter(inputFileAbsolutePath, fileInfoConfig.getFilterConfigList());
            // 生成过的 FTL 模版文件不需要再生成元信息配置
            fileList = fileList.stream().filter(file -> !file.getAbsolutePath().endsWith(TEMPLATE_FILE_SUFFIX)).collect(Collectors.toList());
            for (File file : fileList) {
                Meta.FileConfig.FileInfo fileInfo = makeFileTemplate(file, templateMakerModelConfig, sourceRootPath);
                newFileInfoList.add(fileInfo);
            }
        }

        // 如果是文件组配置，则需要将文件信息添加到组中
        TemplateMakerFileConfig.FileGroupConfig fileGroupConfig = templateMakerFileConfig.getFileGroupConfig();
        if (fileGroupConfig != null) {
            String groupKey = fileGroupConfig.getGroupKey();
            String groupName = fileGroupConfig.getGroupName();
            String condition = fileGroupConfig.getCondition();

            // 新增组配置
            Meta.FileConfig.FileInfo fileGroup = new Meta.FileConfig.FileInfo();
            fileGroup.setGroupKey(groupKey);
            fileGroup.setGroupName(groupName);
            fileGroup.setCondition(condition);
            fileGroup.setType(FileTypeEnum.GROUP.getValue());

            fileGroup.setFiles(newFileInfoList);
            newFileInfoList = new ArrayList<>();
            newFileInfoList.add(fileGroup);
        }
        return newFileInfoList;
    }

    /**
     * 制作单个模版
     *
     * @param inputFile                需要制作模板的文件对象
     * @param templateMakerModelConfig 原始模型参数列表 + 替换配置
     * @param sourceRootPath           源文件根路径
     * @return 文件信息
     */
    private static Meta.FileConfig.FileInfo makeFileTemplate(File inputFile, TemplateMakerModelConfig templateMakerModelConfig, String sourceRootPath) {
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
        boolean hasTemplateFile = FileUtil.exist(fileOutputAbsolutePath);
        if (hasTemplateFile) {
            fileContent = FileUtil.readUtf8String(fileOutputAbsolutePath);
        } else {
            fileContent = FileUtil.readUtf8String(fileInputAbsolutePath);
        }
        TemplateMakerModelConfig.ModelGroupConfig modelGroupConfig = templateMakerModelConfig.getModelGroupConfig();
        String newFileContent = fileContent;
        for (TemplateMakerModelConfig.ModelInfoConfig modelInfoConfig : templateMakerModelConfig.getModels()) {
            String fieldName = modelInfoConfig.getFieldName();
            String replacement;
            if (modelGroupConfig == null) {
                replacement = String.format("${%s}", fieldName);
            } else {
                replacement = String.format("${%s.%s}", modelGroupConfig.getGroupKey(), fieldName);
            }
            newFileContent = StrUtil.replace(newFileContent, modelInfoConfig.getReplaceText(), replacement);
        }

        // 构造文件信息
        Meta.FileConfig.FileInfo fileInfo = new Meta.FileConfig.FileInfo();
        fileInfo.setInputPath(fileOutputPath);
        fileInfo.setOutputPath(fileInputPath);
        fileInfo.setType(FileTypeEnum.FILE.getValue());
        fileInfo.setGenerateType(FileGenerateTypeEnum.DYNAMIC.getValue());

        // 是否更改了文件内容
        boolean contentEquals = newFileContent.equals(fileContent);
        // 存在模版
        if (hasTemplateFile) {
            // 存在模版而且又新增了新的“坑”
            if (!contentEquals) {
                FileUtil.writeUtf8String(newFileContent, fileOutputAbsolutePath);
            }
        } else {
            // 不存在模版而且没有更改过文件内容
            if (contentEquals) {
                fileInfo.setGenerateType(FileGenerateTypeEnum.STATIC.getValue());
                fileInfo.setInputPath(fileInputPath);
            } else {
                FileUtil.writeUtf8String(newFileContent, fileOutputAbsolutePath);
            }
        }
        return fileInfo;
    }

    /**
     * 文件去重
     *
     * @param fileInfoList 文件列表
     * @return 去重后的文件列表
     */
    public static List<Meta.FileConfig.FileInfo> distinctFiles(List<Meta.FileConfig.FileInfo> fileInfoList) {
        // 策略：同组内文件合并，不同分组保留

        // 1. 有分组的，以组为单位划分
        Map<String, List<Meta.FileConfig.FileInfo>> groupKeyFileInfoListMap = fileInfoList
                .stream()
                .filter(fileInfo -> StrUtil.isNotBlank(fileInfo.getGroupKey()))
                .collect(
                        Collectors.groupingBy(Meta.FileConfig.FileInfo::getGroupKey)
                );

        // 2. 同组内的文件配置合并
        // 保存每个组对应的合并后的对象 map
        Map<String, Meta.FileConfig.FileInfo> groupKeyMergedFileInfoMap = new HashMap<>();
        for (Map.Entry<String, List<Meta.FileConfig.FileInfo>> entry : groupKeyFileInfoListMap.entrySet()) {
            List<Meta.FileConfig.FileInfo> tempFileInfoList = entry.getValue();
            ArrayList<Meta.FileConfig.FileInfo> newFileInfoList = new ArrayList<>(tempFileInfoList.stream()
                    .flatMap(fileInfo -> fileInfo.getFiles().stream())
                    .collect(
                            Collectors.toMap(Meta.FileConfig.FileInfo::getOutputPath, Function.identity(), (existing, replacement) -> replacement)
                    ).values());
            // 使用新的 group 配置
            Meta.FileConfig.FileInfo newFileInfo = CollUtil.getLast(tempFileInfoList);
            newFileInfo.setFiles(newFileInfoList);
            String groupKey = entry.getKey();
            groupKeyMergedFileInfoMap.put(groupKey, newFileInfo);
        }

        // 3. 将文件分组添加到结果列表
        List<Meta.FileConfig.FileInfo> resultList = new ArrayList<>(groupKeyMergedFileInfoMap.values());

        // 4. 将未分组的文件添加到结果列表
        List<Meta.FileConfig.FileInfo> noGroupFileInfoList = fileInfoList.stream().filter(fileInfo -> StrUtil.isBlank(fileInfo.getGroupKey()))
                .collect(Collectors.toList());
        resultList.addAll(new ArrayList<>(noGroupFileInfoList.stream()
                .collect(
                        Collectors.toMap(Meta.FileConfig.FileInfo::getOutputPath, Function.identity(), (existing, replacement) -> replacement)
                ).values()));
        return resultList;
    }

    /**
     * 模型去重
     *
     * @param modelInfoList 模型列表
     * @return 去重后的模型列表
     */
    public static List<Meta.ModelConfig.ModelInfo> distinctModels(List<Meta.ModelConfig.ModelInfo> modelInfoList) {
        // 策略：同组内文件合并，不同模型保留

        // 1. 有模型的，以组为单位划分
        Map<String, List<Meta.ModelConfig.ModelInfo>> groupKeyModelInfoListMap = modelInfoList
                .stream()
                .filter(modelInfo -> StrUtil.isNotBlank(modelInfo.getGroupKey()))
                .collect(
                        Collectors.groupingBy(Meta.ModelConfig.ModelInfo::getGroupKey)
                );

        // 2. 同组内的文件配置合并
        // 保存每个组对应的合并后的对象 map
        Map<String, Meta.ModelConfig.ModelInfo> groupKeyMergedModelInfoMap = new HashMap<>();
        for (Map.Entry<String, List<Meta.ModelConfig.ModelInfo>> entry : groupKeyModelInfoListMap.entrySet()) {
            List<Meta.ModelConfig.ModelInfo> tempModelInfoList = entry.getValue();
            ArrayList<Meta.ModelConfig.ModelInfo> newModelInfoList = new ArrayList<>(tempModelInfoList.stream()
                    .flatMap(modelInfo -> modelInfo.getModels().stream())
                    .collect(
                            Collectors.toMap(Meta.ModelConfig.ModelInfo::getFieldName, Function.identity(), (existing, replacement) -> replacement)
                    ).values());
            // 使用新的 group 配置
            Meta.ModelConfig.ModelInfo newModelInfo = CollUtil.getLast(tempModelInfoList);
            newModelInfo.setModels(newModelInfoList);
            String groupKey = entry.getKey();
            groupKeyMergedModelInfoMap.put(groupKey, newModelInfo);
        }

        // 3. 将文件模型添加到结果列表
        List<Meta.ModelConfig.ModelInfo> resultList = new ArrayList<>(groupKeyMergedModelInfoMap.values());

        // 4. 将未模型的文件添加到结果列表
        List<Meta.ModelConfig.ModelInfo> noGroupModelInfoList = modelInfoList.stream().filter(modelInfo -> StrUtil.isBlank(modelInfo.getGroupKey()))
                .collect(Collectors.toList());
        resultList.addAll(new ArrayList<>(noGroupModelInfoList.stream()
                .collect(
                        Collectors.toMap(Meta.ModelConfig.ModelInfo::getFieldName, Function.identity(), (existing, replacement) -> replacement)
                ).values()));
        return resultList;
    }
}
