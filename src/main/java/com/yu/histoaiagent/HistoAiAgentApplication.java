package com.yu.histoaiagent;

import com.yu.histoaiagent.rag.PgVectorVectorStoreConfig;
import org.springframework.ai.vectorstore.pgvector.autoconfigure.PgVectorStoreAutoConfiguration;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.jdbc.DataSourceAutoConfiguration;

@SpringBootApplication(exclude = PgVectorStoreAutoConfiguration.class)
public class HistoAiAgentApplication {

    public static void main(String[] args) {
        SpringApplication.run(HistoAiAgentApplication.class, args);
    }

}
