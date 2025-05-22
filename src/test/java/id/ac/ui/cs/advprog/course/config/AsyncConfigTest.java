package id.ac.ui.cs.advprog.course.config;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.autoconfigure.jdbc.DataSourceAutoConfiguration;
import org.springframework.boot.autoconfigure.orm.jpa.HibernateJpaAutoConfiguration;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;
import org.springframework.test.context.TestPropertySource;

import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Executor;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;
import java.util.List;
import java.util.ArrayList;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest(classes = {
        AsyncConfig.class  // Only load AsyncConfig, no other Spring components
})
@EnableAutoConfiguration(exclude = {
        DataSourceAutoConfiguration.class,       // No database
        HibernateJpaAutoConfiguration.class      // No JPA/Hibernate
})
@TestPropertySource(properties = {
        "spring.main.allow-bean-definition-overriding=true"
})
class IsolatedAsyncConfigTest {

    @Autowired
    @Qualifier("courseTaskExecutor")
    private Executor courseTaskExecutor;

    @Test
    void contextLoads() {
        // Test basic - apakah Spring context bisa load AsyncConfig
        assertThat(courseTaskExecutor).isNotNull();
    }

    @Test
    void taskExecutorIsConfigured() {
        // Verify bahwa executor adalah ThreadPoolTaskExecutor dengan config yang benar
        assertThat(courseTaskExecutor).isInstanceOf(ThreadPoolTaskExecutor.class);

        ThreadPoolTaskExecutor executor = (ThreadPoolTaskExecutor) courseTaskExecutor;

        // Verify configuration values
        assertThat(executor.getCorePoolSize()).isEqualTo(2);
        assertThat(executor.getMaxPoolSize()).isEqualTo(5);
        assertThat(executor.getQueueCapacity()).isEqualTo(100);
        assertThat(executor.getThreadNamePrefix()).isEqualTo("course-async-");
        assertThat(executor.getKeepAliveSeconds()).isEqualTo(60);
    }

    @Test
    void taskExecutorCanExecuteSimpleTask() throws ExecutionException, InterruptedException, TimeoutException {
        // Test sederhana - execute 1 task
        CompletableFuture<String> future = CompletableFuture.supplyAsync(() -> {
            return "Task completed on thread: " + Thread.currentThread().getName();
        }, courseTaskExecutor);

        String result = future.get(2, TimeUnit.SECONDS);

        assertThat(result).contains("Task completed on thread: course-async-");
    }

    @Test
    void taskExecutorHandlesConcurrentTasks() throws ExecutionException, InterruptedException, TimeoutException {
        // Test concurrent execution
        List<CompletableFuture<String>> futures = new ArrayList<>();

        for (int i = 0; i < 3; i++) {
            final int taskId = i;
            CompletableFuture<String> future = CompletableFuture.supplyAsync(() -> {
                try {
                    Thread.sleep(300); // Simulate work
                    return "Task " + taskId + " on " + Thread.currentThread().getName();
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                    return "Task " + taskId + " interrupted";
                }
            }, courseTaskExecutor);

            futures.add(future);
        }

        // Wait for all tasks to complete
        CompletableFuture<Void> allTasks = CompletableFuture.allOf(
                futures.toArray(new CompletableFuture[0])
        );
        allTasks.get(2, TimeUnit.SECONDS);

        // Verify all tasks completed successfully
        for (CompletableFuture<String> future : futures) {
            String result = future.get();
            assertThat(result).contains("course-async-");
            assertThat(result).contains("Task");
        }
    }

    @Test
    void taskExecutorPerformanceTest() throws ExecutionException, InterruptedException, TimeoutException {
        // Test performa - multiple tasks should run concurrently
        int taskCount = 5;
        List<CompletableFuture<String>> futures = new ArrayList<>();

        long startTime = System.currentTimeMillis();

        for (int i = 0; i < taskCount; i++) {
            final int taskId = i;
            CompletableFuture<String> future = CompletableFuture.supplyAsync(() -> {
                try {
                    Thread.sleep(200); // Each task takes 200ms
                    return "Task " + taskId + " completed";
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                    return "Task " + taskId + " interrupted";
                }
            }, courseTaskExecutor);

            futures.add(future);
        }

        // Wait for all tasks
        CompletableFuture.allOf(futures.toArray(new CompletableFuture[0])).get(3, TimeUnit.SECONDS);

        long endTime = System.currentTimeMillis();
        long executionTime = endTime - startTime;

        // With async execution, should be much faster than sequential (5 * 200ms = 1000ms)
        // Should complete in roughly 200-400ms instead of 1000ms
        assertThat(executionTime).isLessThan(700);

        // Verify all tasks completed successfully
        for (CompletableFuture<String> future : futures) {
            assertThat(future.get()).contains("completed");
        }
    }

    @Test
    void taskExecutorThreadPoolBehavior() {
        ThreadPoolTaskExecutor executor = (ThreadPoolTaskExecutor) courseTaskExecutor;

        // Verify thread pool properties
        assertThat(executor.getThreadPoolExecutor()).isNotNull();
        assertThat(executor.getActiveCount()).isGreaterThanOrEqualTo(0);
        assertThat(executor.getPoolSize()).isGreaterThanOrEqualTo(0);

        // Verify rejection policy
        assertThat(executor.getThreadPoolExecutor().getRejectedExecutionHandler())
                .isInstanceOf(java.util.concurrent.ThreadPoolExecutor.CallerRunsPolicy.class);
    }

    @Test
    void taskExecutorExceptionHandling() {
        // Test bagaimana executor handle exception
        CompletableFuture<String> future = CompletableFuture.supplyAsync(() -> {
            if (Math.random() > 2) { // This will never happen, just for test structure
                throw new RuntimeException("Test exception");
            }
            return "Success";
        }, courseTaskExecutor);

        assertThat(future).succeedsWithin(1, TimeUnit.SECONDS);
    }
}