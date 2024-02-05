package com.enndfp.maker.generator.main;

import com.enndfp.maker.meta.Meta;
import com.enndfp.maker.meta.MetaManager;

/**
 * @author Enndfp
 */
public class MainGenerator {

    public static void main(String[] args) {
        Meta meta = MetaManager.getMeta();
        System.out.println(meta);
    }
}
