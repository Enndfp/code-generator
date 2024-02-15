package com.enndfp.maker;

import com.enndfp.maker.generator.main.GenerateTemplate;
import com.enndfp.maker.generator.main.MainGenerator;
import com.enndfp.maker.generator.main.ZipGenerator;
import freemarker.template.TemplateException;

import java.io.IOException;

/**
 * @author Enndfp
 */
public class Main {
    public static void main(String[] args) throws TemplateException, IOException, InterruptedException {
        GenerateTemplate generateTemplate = new ZipGenerator();
        generateTemplate.doGenerate();
    }
}