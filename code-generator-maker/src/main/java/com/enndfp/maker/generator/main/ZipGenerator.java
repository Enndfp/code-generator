package com.enndfp.maker.generator.main;

import com.enndfp.maker.meta.Meta;

import java.io.IOException;

/**
 * 生成zip包
 *
 * @author Enndfp
 */
public class ZipGenerator extends GenerateTemplate {

    @Override
    protected String buildDist(String outputPath, String sourceCopyDestPath, String jarPath, String shellOutputFilePath) {
        String distPath = super.buildDist(outputPath, sourceCopyDestPath, jarPath, shellOutputFilePath);
        return super.buildZip(distPath);
    }

    @Override
    protected void buildGit(Meta meta, String outputPath, String inputResourcePath) throws IOException, InterruptedException {
        System.out.println("重写子类 不生成git版本控制文件 和 .gitignore 文件啦");
    }
}
