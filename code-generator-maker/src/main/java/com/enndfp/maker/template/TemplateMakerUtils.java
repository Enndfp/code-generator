package com.enndfp.maker.template;

import cn.hutool.core.util.StrUtil;
import com.enndfp.maker.meta.Meta;

import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * 模版制作工具
 *
 * @author Enndfp
 */
public class TemplateMakerUtils {

    /**
     * 从未分组文件中移除组内同名文件
     *
     * @param fileInfoList
     * @return
     */
    public static List<Meta.FileConfig.FileInfo> removeGroupFilesFromRoot(List<Meta.FileConfig.FileInfo> fileInfoList) {
        // 1. 获取到所有分组
        List<Meta.FileConfig.FileInfo> groupInfoList = fileInfoList.stream().
                filter(fileInfo -> StrUtil.isNotBlank(fileInfo.getGroupKey()))
                .collect(Collectors.toList());

        // 2. 获取到所有分组内的文件列表
        List<Meta.FileConfig.FileInfo> groupInnerFileInfoList = groupInfoList.stream()
                .flatMap(fileInfo -> fileInfo.getFiles().stream())
                .collect(Collectors.toList());

        // 3. 获取所有分组内的文件输入路径集合
        Set<String> groupInnerFileInputPathSet = groupInnerFileInfoList.stream()
                .map(Meta.FileConfig.FileInfo::getInputPath)
                .collect(Collectors.toSet());

        // 4. 移除所有输入路径在集合中的外层的文件
        return fileInfoList.stream()
                .filter(fileInfo -> !groupInnerFileInputPathSet.contains(fileInfo.getInputPath()))
                .collect(Collectors.toList());
    }
}
