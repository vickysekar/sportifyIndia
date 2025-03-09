package com.sportifyindia.app;

import com.sportifyindia.app.config.AsyncSyncConfiguration;
import com.sportifyindia.app.config.EmbeddedElasticsearch;
import com.sportifyindia.app.config.EmbeddedKafka;
import com.sportifyindia.app.config.EmbeddedRedis;
import com.sportifyindia.app.config.EmbeddedSQL;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.DirtiesContext;

/**
 * Base composite annotation for integration tests.
 */
@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
@SpringBootTest(classes = { SportifyingIndiaApp.class, AsyncSyncConfiguration.class })
@EmbeddedRedis
@EmbeddedElasticsearch
@EmbeddedSQL
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_CLASS)
@EmbeddedKafka
public @interface IntegrationTest {
}
