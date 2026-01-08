package com.yu.histoaiagent.rag;

import jakarta.annotation.Resource;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
class TherapyAppDocumentLoaderTest {

    @Resource
    TherapyAppDocumentLoader therapyAppDocumentLoader;

    @Test
    void loadMarkdowns() {
        therapyAppDocumentLoader.loadMarkdowns();
    }
}