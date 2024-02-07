package com.enndfp.maker.generator;

import java.io.*;

/**
 * 生成 .gitignore
 *
 * @author Enndfp
 */
public class GitGenerator {

    public static void doGenerate(String projectDir) throws IOException, InterruptedException {
        String gitCommand = "git init";
        Process process = new ProcessBuilder(gitCommand.split(" ")).directory(new File(projectDir)).start();
        InputStream inputStream = process.getInputStream();
        BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream, "GBK"));
        String line;
        while ((line = reader.readLine()) != null) {
            System.out.println(line);
        }
        System.out.println("git 初始化完成!");
        int exitCode = process.waitFor();
        System.out.println("git命令执行结束,退出码:" + exitCode);
    }

}
