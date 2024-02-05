package com.enndfp.maker.generator;

import cn.hutool.core.io.FileUtil;

import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.nio.file.attribute.PosixFilePermission;
import java.nio.file.attribute.PosixFilePermissions;
import java.util.Set;

/**
 * 生成脚本
 *
 * @author Enndfp
 */
public class ScriptGenerator {

    public static void doGenerate(String outputPath, String jarPath) {

        /**
         * Windows：
         *         @echo off
         *         java -jar -Dfile.encoding=UTF-8 target/acm-template-pro-generator-1.0-jar-with-dependencies.jar %*
         * Linux：
         *         #!/bin/bash
         *         java -jar -Dfile.encoding=UTF-8 target/acm-template-pro-generator-1.0-jar-with-dependencies.jar "$@"
         */

        StringBuilder sb = new StringBuilder();
        // 根据系统类型生成脚本 windows
        sb.append("@echo off").append("\n");
        sb.append("java -jar ").append("-Dfile.encoding=UTF-8 ").append(jarPath).append(" %*").append("\n");
        FileUtil.writeBytes(sb.toString().getBytes(StandardCharsets.UTF_8), outputPath + ".bat");


        // linux
        sb = new StringBuilder();
        sb.append("#!/bin/bash").append("\n");
        sb.append("java -jar ").append("-Dfile.encoding=UTF-8 ").append(jarPath).append(" \"$@\"").append("\n");
        FileUtil.writeBytes(sb.toString().getBytes(StandardCharsets.UTF_8), outputPath);
        try {
            // 添加可执行权限
            Set<PosixFilePermission> permissions = PosixFilePermissions.fromString("rwxrwxrwx");
            Files.setPosixFilePermissions(Paths.get(outputPath), permissions);
        } catch (Exception e) {

        }
    }
}
