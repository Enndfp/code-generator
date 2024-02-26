package ${basePackage}.cli;

import ${basePackage}.cli.command.GenerateCommand;
import ${basePackage}.cli.command.ListCommand;
import ${basePackage}.cli.command.JsonGenerateCommand;
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
                .addSubcommand(new ListCommand())
                .addSubcommand(new JsonGenerateCommand());
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
        Set<String> paramsSet = new HashSet<>();
        List<String> argument = new ArrayList<>();

        // 2. 先处理等号连接的参数，确保参数名被加入到 Set 中，同时保留完整的参数到列表中
        for (String param : params) {
            if (param.contains("=")) {
                String[] parts = param.split("=", 2);
                // 仅将参数名部分加入到 Set 中用于后续检查
                paramsSet.add(parts[0]);
            } else {
                paramsSet.add(param);
            }
            // 保留完整的参数到列表中，无论是否包含等号
            argument.add(param);
        }

        // 3. 判断如果是非用户自定义参数则直接返回
        String[] excludeParams = {"-h", "--help", "-V", "--version", "config", "list"};
        if (Arrays.stream(excludeParams).anyMatch(paramsSet::contains)) {
            return argument.toArray(new String[0]);
        }

        // 4. 获取 GenerateCommand 类的所有字段
        Class<GenerateCommand> generateCommandClass = GenerateCommand.class;
        Field[] declaredFields = generateCommandClass.getDeclaredFields();

        // 5. 遍历所有字段，检查是否有 Option 注解，如果有则检查 interactive 属性
        for (Field declaredField : declaredFields) {
            if (!declaredField.isAnnotationPresent(CommandLine.Option.class)) continue;
            declaredField.setAccessible(true);
            CommandLine.Option option = declaredField.getAnnotation(CommandLine.Option.class);

            // 6. 如果 interactive 属性为 true，则检查参数是否存在，如果不存在则添加
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