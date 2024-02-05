package com.enndfp.maker.generator;

import java.io.*;

/**
 * 生成 Jar
 *
 * @author Enndfp
 */
public class JarGenerator {

    public static void doGenerate(String projectDir) throws IOException, InterruptedException {
        // 清理之前的构建 并打包
        // 不同操作系统，执行的命令也不同 maven 打包
        String mavenCommand;
        if (System.getProperty("os.name").contains("Windows")) {
            mavenCommand = "mvn.cmd clean package -Dmaven.test.skip=true";
        } else {
            mavenCommand = "mvn clean package -Dmaven.test.skip=true";
        }
        System.out.println("开始打包......");
        System.out.println(mavenCommand);

        Process process = new ProcessBuilder(mavenCommand.split(" ")).directory(new File(projectDir)).start();

        InputStream inputStream = process.getInputStream();
        BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream));
        String line;
        while ((line = reader.readLine()) != null) {
            // 根据日志级别设置颜色
            if (line.contains("INFO")) {
                // 设置日志级别为蓝色
                System.out.println("\033[0;34m" + line + "\033[0m");

            } else if (line.contains("WARN")) {
                // 设置日志级别为黄色
                System.out.println("\033[0;33m" + line + "\033[0m");
            } else if (line.contains("ERROR")) {
                // 设置日志级别为红色
                System.out.println("\033[0;31m" + line + "\033[0m");
            } else {
                // 其他情况不设置颜色
                System.out.println(line);
            }
        }
        System.out.println("打包结束!");
        int exitCode = process.waitFor();
        System.out.println("命令执行结束,退出码:" + exitCode);
    }

    public static void main(String[] args) throws IOException, InterruptedException {
        doGenerate("E:\\planet\\code-generator\\code-generator-basic");
    }
}