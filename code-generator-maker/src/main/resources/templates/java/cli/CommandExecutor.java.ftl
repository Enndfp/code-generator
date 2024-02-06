package ${basePackage}.cli;

import ${basePackage}.cli.command.GenerateCommand;
import ${basePackage}.cli.command.ListCommand;
import ${basePackage}.cli.command.ConfigCommand;
import picocli.CommandLine;
import picocli.CommandLine.Command;
<#if forcedInteractive>

import java.lang.reflect.Field;
import java.util.*;
</#if>

/**
 * 主命令执行器
 *
 * @author ${author}
 */
@Command(name = "${name}", mixinStandardHelpOptions = true)
public class CommandExecutor implements Runnable {

    private final CommandLine commandLine;

    {
        commandLine = new CommandLine(this)
                .addSubcommand(new GenerateCommand())
                .addSubcommand(new ConfigCommand())
                .addSubcommand(new ListCommand());
    }

    @Override
    public void run() {
        // 不输入子命令时，给出友好提示
        System.out.println("请输入具体命令，或者输入 --help 查看帮助手册");
    }

    /**
     * 执行命令
     *
     * @param args 命令参数
     * @return 命令执行结果
     */
    public Integer doExecute(String[] args) {
    <#if forcedInteractive>
        args = checkParams(args);
    </#if>
        return commandLine.execute(args);
    }
<#if forcedInteractive>

    /**
     * 检查参数是否存在，如果不存在则添加
     *
     * @param params 参数数组
     * @return 新的参数数组
     */
    private static String[] checkParams(String[] params) {
        // 1. 将参数数组转换为 Set 提高查找效率
        Set<String> paramsSet = new HashSet<>(Arrays.asList(params));
        List<String> argument = new ArrayList<>(Arrays.asList(params));

        // 2. 判断如果是非用户自定义参数则直接返回
        String[] excludeParams = {"-h", "--help", "-V", "--version", "config", "list"};
        if (Arrays.stream(excludeParams).anyMatch(paramsSet::contains)) {
            return argument.toArray(new String[0]);
        }

        // 3. 获取 GenerateCommand 类的所有字段
        Class<GenerateCommand> generateCommandClass = GenerateCommand.class;
        Field[] declaredFields = generateCommandClass.getDeclaredFields();

        // 4. 遍历所有字段，检查是否有 Option 注解，如果有则检查 interactive 属性
        for (Field declaredField : declaredFields) {
            if (!declaredField.isAnnotationPresent(CommandLine.Option.class)) continue;
            declaredField.setAccessible(true);
            CommandLine.Option option = declaredField.getAnnotation(CommandLine.Option.class);

            // 5. 如果 interactive 属性为 true，则检查参数是否存在，如果不存在则添加
            if (option.interactive()) {
                String[] names = option.names();
                boolean flag = Arrays.stream(names).anyMatch(paramsSet::contains);
                if (!flag && names.length > 0) {
                    argument.add(names[0]);
                }
            }
        }
        return argument.toArray(new String[0]);
    }
</#if>
}