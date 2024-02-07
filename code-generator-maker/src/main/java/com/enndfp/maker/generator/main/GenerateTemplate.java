package com.enndfp.maker.generator.main;

import cn.hutool.core.io.FileUtil;
import cn.hutool.core.io.resource.ClassPathResource;
import cn.hutool.core.util.StrUtil;
import com.enndfp.maker.generator.GitGenerator;
import com.enndfp.maker.generator.JarGenerator;
import com.enndfp.maker.generator.ScriptGenerator;
import com.enndfp.maker.generator.file.DynamicFileGenerator;
import com.enndfp.maker.meta.Meta;
import com.enndfp.maker.meta.MetaManager;
import freemarker.template.TemplateException;

import java.io.File;
import java.io.IOException;

/**
 * 生成模版方法
 *
 * @author Enndfp
 */
public abstract class GenerateTemplate {

    public void doGenerate() throws TemplateException, IOException, InterruptedException {
        Meta meta = MetaManager.getMeta();

        // 输出根路径
        String projectPath = System.getProperty("user.dir");
        String outputPath = projectPath + File.separator + "generated" + File.separator + meta.getName();
        if (!FileUtil.exist(outputPath)) {
            FileUtil.mkdir(outputPath);
        }
        // 读取 resource 目录
        ClassPathResource classPathResource = new ClassPathResource("");
        String inputResourcePath = classPathResource.getAbsolutePath();

        // 1. 拷贝原始的源文件
        String sourceCopyDestPath = copySource(meta, outputPath);

        // 2. 代码生成
        generateCode(meta, outputPath, inputResourcePath);

        // 3. 构建 jar 包
        String jarPath = buildJar(meta, outputPath);

        // 4. 封装脚本
        String shellOutputFilePath = buildScript(outputPath, jarPath);

        // 5. 版本控制
        buildGit(meta, outputPath, inputResourcePath);

        // 6. 生成精简版的程序
        buildDist(outputPath, sourceCopyDestPath, jarPath, shellOutputFilePath);
    }

    /**
     * 生成精简版的程序
     *
     * @param outputPath          生成的路径
     * @param sourceCopyDestPath  源模板文件拷贝的路径
     * @param jarPath             jar 包路径
     * @param shellOutputFilePath 脚本文件路径
     */
    protected void buildDist(String outputPath, String sourceCopyDestPath, String jarPath, String shellOutputFilePath) {
        // 生成精简版的程序（产物包）
        String distOutputPath = outputPath + "-dist";
        // - 拷贝 jar 包
        String targetAbsolutePath = distOutputPath + File.separator + "target";
        FileUtil.mkdir(targetAbsolutePath);
        String jarAbsolutePath = outputPath + File.separator + jarPath;
        FileUtil.copy(jarAbsolutePath, targetAbsolutePath, true);
        // - 拷贝脚本文件
        FileUtil.copy(shellOutputFilePath, distOutputPath, true);
        FileUtil.copy(shellOutputFilePath + ".bat", distOutputPath, true);
        // - 拷贝源模板文件
        FileUtil.copy(sourceCopyDestPath, distOutputPath, true);
    }

    /**
     * 构建版本控制
     *
     * @param meta              元信息
     * @param outputPath        生成的路径
     * @param inputResourcePath 模板文件路径
     * @throws IOException          文件操作异常
     * @throws InterruptedException 线程异常
     */
    protected void buildGit(Meta meta, String outputPath, String inputResourcePath) throws IOException, InterruptedException {
        if (meta.getVersionControl()) {
            GitGenerator.doGenerate(outputPath);
            String inputFilePath = inputResourcePath + File.separator + "templates/static/.gitignore";
            FileUtil.copy(inputFilePath, outputPath, true);
        }
    }

    /**
     * 封装脚本
     *
     * @param outputPath 生成的路径
     * @param jarPath    jar 包路径
     * @return 脚本文件路径
     */
    protected String buildScript(String outputPath, String jarPath) {
        String shellOutputFilePath = outputPath + File.separator + "generator";
        ScriptGenerator.doGenerate(shellOutputFilePath, jarPath);
        return shellOutputFilePath;
    }

    /**
     * 构建 jar 包
     *
     * @param meta       元信息
     * @param outputPath 生成的路径
     * @return jar 包路径
     * @throws IOException          文件操作异常
     * @throws InterruptedException 线程异常
     */
    protected String buildJar(Meta meta, String outputPath) throws IOException, InterruptedException {
        JarGenerator.doGenerate(outputPath);
        String jarName = String.format("%s-%s-jar-with-dependencies.jar", meta.getName(), meta.getVersion());
        return "target/" + jarName;
    }

    /**
     * 代码生成
     *
     * @param meta              元信息
     * @param outputPath        生成的路径
     * @param inputResourcePath 模板文件路径
     * @throws IOException       文件操作异常
     * @throws TemplateException 模板异常
     */
    protected void generateCode(Meta meta, String outputPath, String inputResourcePath) throws IOException, TemplateException {
        // Java 包基础路径
        // com.enndfp
        String outputBasePackage = meta.getBasePackage();
        // com/enndfp
        String outputBasePackagePath = StrUtil.join("/", StrUtil.split(outputBasePackage, "."));
        // generated/xxx/src/main/java/com/enndfp
        String outputBaseJavaPackagePath = outputPath + File.separator + "src/main/java/" + outputBasePackagePath;

        String inputFilePath;
        String outputFilePath;

        // model.DataModel
        inputFilePath = inputResourcePath + File.separator + "templates/java/model/DataModel.java.ftl";
        outputFilePath = outputBaseJavaPackagePath + "/model/DataModel.java";
        DynamicFileGenerator.doGenerate(inputFilePath, outputFilePath, meta);

        // cli.command.ConfigCommand
        inputFilePath = inputResourcePath + File.separator + "templates/java/cli/command/ConfigCommand.java.ftl";
        outputFilePath = outputBaseJavaPackagePath + "/cli/command/ConfigCommand.java";
        DynamicFileGenerator.doGenerate(inputFilePath, outputFilePath, meta);

        // cli.command.GenerateCommand
        inputFilePath = inputResourcePath + File.separator + "templates/java/cli/command/GenerateCommand.java.ftl";
        outputFilePath = outputBaseJavaPackagePath + "/cli/command/GenerateCommand.java";
        DynamicFileGenerator.doGenerate(inputFilePath, outputFilePath, meta);

        // cli.command.ListCommand
        inputFilePath = inputResourcePath + File.separator + "templates/java/cli/command/ListCommand.java.ftl";
        outputFilePath = outputBaseJavaPackagePath + "/cli/command/ListCommand.java";
        DynamicFileGenerator.doGenerate(inputFilePath, outputFilePath, meta);

        // cli.CommandExecutor
        inputFilePath = inputResourcePath + File.separator + "templates/java/cli/CommandExecutor.java.ftl";
        outputFilePath = outputBaseJavaPackagePath + "/cli/CommandExecutor.java";
        DynamicFileGenerator.doGenerate(inputFilePath, outputFilePath, meta);

        // Main
        inputFilePath = inputResourcePath + File.separator + "templates/java/Main.java.ftl";
        outputFilePath = outputBaseJavaPackagePath + "/Main.java";
        DynamicFileGenerator.doGenerate(inputFilePath, outputFilePath, meta);

        // generator.DynamicGenerator
        inputFilePath = inputResourcePath + File.separator + "templates/java/generator/DynamicGenerator.java.ftl";
        outputFilePath = outputBaseJavaPackagePath + "/generator/DynamicGenerator.java";
        DynamicFileGenerator.doGenerate(inputFilePath, outputFilePath, meta);

        // generator.MainGenerator
        inputFilePath = inputResourcePath + File.separator + "templates/java/generator/MainGenerator.java.ftl";
        outputFilePath = outputBaseJavaPackagePath + "/generator/MainGenerator.java";
        DynamicFileGenerator.doGenerate(inputFilePath, outputFilePath, meta);

        // generator.StaticGenerator
        inputFilePath = inputResourcePath + File.separator + "templates/java/generator/StaticGenerator.java.ftl";
        outputFilePath = outputBaseJavaPackagePath + "/generator/StaticGenerator.java";
        DynamicFileGenerator.doGenerate(inputFilePath, outputFilePath, meta);

        // pom.xml
        inputFilePath = inputResourcePath + File.separator + "templates/pom.xml.ftl";
        outputFilePath = outputPath + File.separator + "pom.xml";
        DynamicFileGenerator.doGenerate(inputFilePath, outputFilePath, meta);

        // README.md
        inputFilePath = inputResourcePath + File.separator + "templates/README.md.ftl";
        outputFilePath = outputPath + File.separator + "README.md";
        DynamicFileGenerator.doGenerate(inputFilePath, outputFilePath, meta);
    }

    /**
     * 拷贝原始的源文件
     *
     * @param meta       元信息
     * @param outputPath 生成的路径
     * @return 源文件拷贝的路径
     */
    protected String copySource(Meta meta, String outputPath) {
        String sourceRootPath = meta.getFileConfig().getSourceRootPath();
        String sourceCopyDestPath = outputPath + File.separator + ".source";
        FileUtil.copy(sourceRootPath, sourceCopyDestPath, false);
        return sourceCopyDestPath;
    }
}
