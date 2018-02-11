package com.interview.cache.conf.dev;


import com.interview.cache.model.dao.Movie;
import com.interview.cache.web.controller.ExceptionHandlingControllersAdvice;
import com.mongodb.BasicDBList;
import com.mongodb.util.JSON;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Profile;
import org.springframework.context.event.ContextRefreshedEvent;
import org.springframework.context.event.EventListener;
import org.springframework.core.io.ClassPathResource;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.stereotype.Component;
import org.springframework.util.StreamUtils;

import java.io.InputStream;
import java.nio.charset.StandardCharsets;

@Component
@Profile({"dev", "test"})
public class DBInitDataLoader {
    private final static Logger LOG = LoggerFactory.getLogger(ExceptionHandlingControllersAdvice.class);
    private static final String INIT_DATA_RESOURCE_PATH = "dev/dbInitData.json";

    private final MongoTemplate mongoTemplate;


    public DBInitDataLoader(MongoTemplate mongoTemplate) {
        this.mongoTemplate = mongoTemplate;
    }

    @EventListener
    public void onApplicationEvent(ContextRefreshedEvent event) {
        final ClassPathResource initDataResource = new ClassPathResource(INIT_DATA_RESOURCE_PATH);
        if (initDataResource.exists()) {
            try (InputStream initDataResourceIS = initDataResource.getInputStream()) {
                final String initDataJsonStr = StreamUtils.copyToString(initDataResourceIS, StandardCharsets.UTF_8);
                final BasicDBList initDataJsonObj = (BasicDBList) JSON.parse(initDataJsonStr);
                mongoTemplate.insert(initDataJsonObj, Movie.class);
            } catch (Exception e) {
                LOG.error("Can not process initial db data on path: " + INIT_DATA_RESOURCE_PATH, e);
            }
        } else {
            LOG.warn("Can not find initial db data on path: " + INIT_DATA_RESOURCE_PATH);
        }
    }


}
