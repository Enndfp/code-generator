package com.enndfp.maker.meta;

import cn.hutool.core.io.resource.ResourceUtil;
import cn.hutool.json.JSONUtil;

/**
 * 元信息管理器
 *
 * @author Enndfp
 */
public class MetaManager {

    private static volatile Meta meta;

    private MetaManager() {
        // 私有构造器，防止被外部实例化
    }

    /**
     * 获取元信息对象
     *
     * @return 元信息对象
     */
    public static Meta getMeta() {
        if (meta == null) {
            synchronized (MetaManager.class) {
                if (meta == null) {
                    meta = initMeta();
                }
            }
        }
        return meta;
    }

    /**
     * 初始化元信息
     *
     * @return 元信息
     */
    private static Meta initMeta() {
        String metaJson = ResourceUtil.readUtf8Str("meta.json");
        Meta newMeta = JSONUtil.toBean(metaJson, Meta.class);
        // 校验和处理默认值
        MetaValidator.doValidAndFill(newMeta);
        return newMeta;
    }
}
