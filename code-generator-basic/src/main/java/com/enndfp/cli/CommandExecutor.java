package com.enndfp.cli;

import com.enndfp.cli.command.ConfigCommand;
import com.enndfp.cli.command.GenerateCommand;
import com.enndfp.cli.command.ListCommand;
import picocli.CommandLine;

/**
 * 主命令执行器
 *
 * @author Enndfp
 */
@CommandLine.Command(name = "code", mixinStandardHelpOptions = true)
public class CommandExecutor implements Runnable {

    private final CommandLine commandLine;

    {
        commandLine = new CommandLine(this)
                .addSubcommand(new ConfigCommand())
                .addSubcommand(new GenerateCommand())
                .addSubcommand(new ListCommand());
    }

    @Override
    public void run() {
        System.out.println("请输入具体命令，或者输入 --help 查看帮助手册");
    }

    /**
     * 执行命令
     *
     * @param args 命令参数
     * @return 命令执行结果
     */
    public Integer doExecute(String[] args) {
        return commandLine.execute(args);
    }
}
