package com.enndfp.web;

import com.enndfp.web.model.entity.Generator;
import com.enndfp.web.service.GeneratorService;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

import javax.annotation.Resource;

@SpringBootTest
class GeneratorServiceTest {

    @Resource
    private GeneratorService generatorService;

    @Test
    public void testInsert() {
        Generator generator = generatorService.getById(1758126802260062211L);
        for (int i = 0; i < 100000; i++) {
            generator.setId(null);
            generatorService.save(generator);
        }
    }
}