package ${basePackage}.generator;

import com.enndfp.model.DataModel;
import freemarker.template.TemplateException;

import java.io.File;
import java.io.IOException;

<#macro generateFile indent fileInfo>
${indent}inputPath = new File(inputRootPath, "${fileInfo.inputPath}").getAbsolutePath();
${indent}outputPath = new File(outputRootPath, "${fileInfo.outputPath}").getAbsolutePath();
<#if fileInfo.generateType == "static">
${indent}StaticGenerator.copyFilesByHutool(inputPath, outputPath);
<#else>
${indent}DynamicGenerator.doGenerate(inputPath, outputPath, model);
</#if>
</#macro>

/**
 * 核心生成器
 *
 * @author ${author}
 */
public class MainGenerator {

    /**
     * 生成
     *
     * @param model 数据模型
     * @throws IOException       文件读写异常
     * @throws TemplateException 模板异常
     */
    public static void doGenerate(DataModel model) throws IOException, TemplateException {

        String inputRootPath = "${fileConfig.inputRootPath}";
        String outputRootPath = "${fileConfig.outputRootPath}";

        String inputPath;
        String outputPath;

    <#list modelConfig.models as modelInfo>
        ${modelInfo.type} ${modelInfo.fieldName} = model.${modelInfo.fieldName};
    </#list>

    <#list fileConfig.files as fileInfo>
        <#if fileInfo.groupKey??>
        // groupKey = ${fileInfo.groupKey}
        <#if fileInfo.condition??>
        if (${fileInfo.condition}) {
            <#list fileInfo.files as fileInfo>
            <@generateFile fileInfo=fileInfo indent="            " />
            </#list>
        }
        <#else>
        <#list fileInfo.files as fileInfo>
        <@generateFile fileInfo=fileInfo indent="        " />
        </#list>
        </#if>
        <#else>
        <#if fileInfo.condition??>
            if (${fileInfo.condition}) {
            <@generateFile fileInfo=fileInfo indent="            " />
            }
        <#else>
        <@generateFile fileInfo=fileInfo indent="        " />
        </#if>
        </#if>
    </#list>
    }
}