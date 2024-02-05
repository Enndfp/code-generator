package com.enndfp.maker.cli.command;

import cn.hutool.core.util.ReflectUtil;
import com.enndfp.maker.model.DataModel;
import picocli.CommandLine;

import java.lang.reflect.Field;

/**
 * 查看参数信息
 *
 * @author Enndfp
 */
@CommandLine.Command(name = "config", description = "查看参数信息", mixinStandardHelpOptions = true)
public class ConfigCommand implements Runnable {

    @Override
    public void run() {
        Field[] fields = ReflectUtil.getFields(DataModel.class);
        for (Field field : fields) {
            System.out.println("字段名称：" + field.getName());
            System.out.println("字段类型：" + field.getType());
        }
    }
}
