package com.enndfp.maker.template.enums;

import lombok.Getter;

/**
 * 文件过滤范围枚举
 *
 * @author Enndfp
 */
@Getter
public enum FileFilterRangeEnum {

    FILE_NAME("文件名称", "fileName"),
    FILE_CONTENT("文件内容", "fileContent");

    private final String text;

    private final String value;

    FileFilterRangeEnum(String text, String value) {
        this.text = text;
        this.value = value;
    }

    /**
     * 获取枚举
     *
     * @param value 文件过滤值
     * @return 枚举类
     */
    public static FileFilterRangeEnum getEnumByValue(String value) {
        if (value == null) {
            return null;
        }
        for (FileFilterRangeEnum item : FileFilterRangeEnum.values()) {
            if (item.getValue().equals(value)) {
                return item;
            }
        }
        return null;
    }
}
