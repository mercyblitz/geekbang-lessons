/*
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements.  See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.geektimes.projects.spring.batch.job;

import org.springframework.batch.core.Job;
import org.springframework.batch.core.JobExecution;
import org.springframework.batch.core.JobExecutionListener;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.configuration.annotation.EnableBatchProcessing;
import org.springframework.batch.core.configuration.annotation.JobBuilderFactory;
import org.springframework.batch.core.configuration.annotation.StepBuilderFactory;
import org.springframework.batch.item.ItemProcessor;
import org.springframework.batch.item.ItemReader;
import org.springframework.batch.item.ItemWriter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.core.task.SimpleAsyncTaskExecutor;
import org.springframework.core.task.TaskExecutor;

import java.util.Random;

/**
 * Spring Batch Job 示例
 *
 * @author <a href="mailto:mercyblitz@gmail.com">Mercy</a>
 * @since 1.0.0
 */
@EnableBatchProcessing
@SpringBootApplication
public class JobBootstrap {

    @Autowired
    private JobBuilderFactory jobBuilderFactory;

    @Autowired
    private StepBuilderFactory stepBuilderFactory;

    @Bean
    public Job simpleJob() {
        return this.jobBuilderFactory.get("simple-job-" + System.currentTimeMillis())
                .start(firstStep())
                .listener(jobExecutionListener())
                .build();
    }

    private JobExecutionListener jobExecutionListener() {
        return new JobExecutionListener(){

            @Override
            public void beforeJob(JobExecution jobExecution) {

            }

            @Override
            public void afterJob(JobExecution jobExecution) {

            }
        };
    }

    private final Random random = new Random();

    private Step firstStep() {
        return stepBuilderFactory.get("first-step")
                .<String, Long>chunk(3)
                .reader(firstReader())       // 必须的
                .processor(firstProcessor()) // 可选的
                .writer(firstWriter())       // 必须的
//                .taskExecutor(taskExecutor()) // 切换 TaskExecutor
                .listener()
                .build();
    }

    private TaskExecutor taskExecutor() {
        return new SimpleAsyncTaskExecutor();
    }

    private ItemReader<String> firstReader() {
        return () -> {
            if (random.nextInt(100) < 10) {
                return null;
            } else {
                return String.valueOf(System.nanoTime());
            }
        };
    }

    private ItemProcessor<? super String, Long> firstProcessor() {
        return str -> Long.valueOf(str);
    }

    private ItemWriter<? super Long> firstWriter() {
        return items -> {
            System.out.printf("[线程: %s] : %s\n",
                    Thread.currentThread().getName(),
                    items);
        };
    }

    public static void main(String[] args) {
        SpringApplication.run(JobBootstrap.class, args);
    }
}
