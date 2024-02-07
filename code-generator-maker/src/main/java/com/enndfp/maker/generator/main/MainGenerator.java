package com.enndfp.maker.generator.main;

import com.enndfp.maker.meta.Meta;

import java.io.IOException;

/**
 * @author Enndfp
 */
public class MainGenerator extends GenerateTemplate {

    @Override
    protected void buildDist(String outputPath, String jarPath, String shellOutputPath, String sourceOutputPath) {
        System.out.println("重写子类 不生成dist精简版程序");
    }

    @Override
    protected void buildGit(Meta meta, String outputPath, String inputResourcePath) throws IOException, InterruptedException {
        System.out.println("重写子类 不生成git版本控制文件 和 .gitignore 文件啦");
    }
}
