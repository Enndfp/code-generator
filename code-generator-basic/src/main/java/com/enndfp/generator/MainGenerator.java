package com.enndfp.generator;

import com.enndfp.model.MainTemplateConfig;
import freemarker.template.TemplateException;

import java.io.File;
import java.io.IOException;

/**
 * 主生成器
 *
 * @author Enndfp
 */
public class MainGenerator {

    /**
     * 生成
     *
     * @param model 数据模型
     * @throws IOException       文件读写异常
     * @throws TemplateException 模板异常
     */
    public static void doGenerate(Object model) throws IOException, TemplateException {
        // 1. 获取输入输出路径
        String projectPath = System.getProperty("user.dir");
        File parentFile = new File(projectPath).getParentFile();
        String inputPath = new File(parentFile, "code-generator-demo-projects" + File.separator + "acm-template").getAbsolutePath();
        String outputPath = projectPath;

        // 2. 生成静态文件
        StaticGenerator.copyFilesByRecursive(inputPath, outputPath);

        // 3. 生成动态文件
        String inputDynamicFilePath = projectPath + "/src/main/resources/templates/MainTemplate.java.ftl".replace("/", File.separator);
        String outputDynamicFilePath = outputPath + "/acm-template/src/com/enndfp/acm/MainTemplate.java".replace("/", File.separator);
        DynamicGenerator.doGenerate(inputDynamicFilePath, outputDynamicFilePath, model);
    }

    public static void main(String[] args) throws TemplateException, IOException {
        MainTemplateConfig mainTemplateConfig = new MainTemplateConfig();
        mainTemplateConfig.setLoop(true);
        mainTemplateConfig.setAuthor("Enndfp");
        mainTemplateConfig.setOutputText("求和结果: ");

        doGenerate(mainTemplateConfig);
    }
}
