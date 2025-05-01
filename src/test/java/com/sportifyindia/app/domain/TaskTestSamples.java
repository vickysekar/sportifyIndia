package com.sportifyindia.app.domain;

import java.util.Random;
import java.util.UUID;
import java.util.concurrent.atomic.AtomicLong;

public class TaskTestSamples {

    private static final Random random = new Random();
    private static final AtomicLong longCount = new AtomicLong(random.nextInt() + (2 * Integer.MAX_VALUE));

    public static Task getTaskSample1() {
        return new Task().id(1L).taskTitle("taskTitle1").taskDescription("taskDescription1");
    }

    public static Task getTaskSample2() {
        return new Task().id(2L).taskTitle("taskTitle2").taskDescription("taskDescription2");
    }

    public static Task getTaskRandomSampleGenerator() {
        return new Task()
            .id(longCount.incrementAndGet())
            .taskTitle(UUID.randomUUID().toString())
            .taskDescription(UUID.randomUUID().toString());
    }
}
