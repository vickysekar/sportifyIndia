package com.sportifyindia.app.domain;

import java.util.Random;
import java.util.UUID;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicLong;

public class CourseTestSamples {

    private static final Random random = new Random();
    private static final AtomicLong longCount = new AtomicLong(random.nextInt() + (2 * Integer.MAX_VALUE));
    private static final AtomicInteger intCount = new AtomicInteger(random.nextInt() + (2 * Short.MAX_VALUE));

    public static Course getCourseSample1() {
        return new Course()
            .id(1L)
            .name("name1")
            .sport("sport1")
            .level("level1")
            .description("description1")
            .duration(1)
            .imageLinks("imageLinks1")
            .termsAndConditions("termsAndConditions1");
    }

    public static Course getCourseSample2() {
        return new Course()
            .id(2L)
            .name("name2")
            .sport("sport2")
            .level("level2")
            .description("description2")
            .duration(2)
            .imageLinks("imageLinks2")
            .termsAndConditions("termsAndConditions2");
    }

    public static Course getCourseRandomSampleGenerator() {
        return new Course()
            .id(longCount.incrementAndGet())
            .name(UUID.randomUUID().toString())
            .sport(UUID.randomUUID().toString())
            .level(UUID.randomUUID().toString())
            .description(UUID.randomUUID().toString())
            .duration(intCount.incrementAndGet())
            .imageLinks(UUID.randomUUID().toString())
            .termsAndConditions(UUID.randomUUID().toString());
    }
}
