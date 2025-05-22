package id.ac.ui.cs.advprog.course.config;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;
import org.springframework.test.context.TestPropertySource;

import java.util.concurrent.Executor;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@TestPropertySource(properties = {
        "spring.main.allow-bean-definition-overriding=true"
})
class AsyncConfigTest {

    @Autowired
    @Qualifier("courseTaskExecutor")
    private Executor courseTaskExecutor;

    @Test
    void contextLoads() {
        assertThat(courseTaskExecutor).isNotNull();
    }

    @Test
    void taskExecutorIsConfigured() {
        assertThat(courseTaskExecutor).isInstanceOf(ThreadPoolTaskExecutor.class);

        ThreadPoolTaskExecutor executor = (ThreadPoolTaskExecutor) courseTaskExecutor;

        assertThat(executor.getCorePoolSize()).isEqualTo(2);
        assertThat(executor.getMaxPoolSize()).isEqualTo(5);
        assertThat(executor.getQueueCapacity()).isEqualTo(100);
        assertThat(executor.getThreadNamePrefix()).isEqualTo("course-async-");
        assertThat(executor.getKeepAliveSeconds()).isEqualTo(60);
    }

    @Test
    void taskExecutorCanExecuteAsyncTasks() throws ExecutionException, InterruptedException, TimeoutException {
        // Test apakah executor bisa menjalankan task async
        CompletableFuture<String> future = CompletableFuture.supplyAsync(() -> {
            try {
                Thread.sleep(1000); // Simulate work
                return "Task completed on thread: " + Thread.currentThread().getName();
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
                return "Task interrupted";
            }
        }, courseTaskExecutor);

        String result = future.get(3, TimeUnit.SECONDS);

        assertThat(result).contains("Task completed on thread: course-async-");
    }

    @Test
    void taskExecutorHandlesMultipleConcurrentTasks() throws ExecutionException, InterruptedException, TimeoutException {

        CompletableFuture<String> task1 = CompletableFuture.supplyAsync(() -> {
            try {
                Thread.sleep(500);
                return "Task 1: " + Thread.currentThread().getName();
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
                return "Task 1 interrupted";
            }
        }, courseTaskExecutor);

        CompletableFuture<String> task2 = CompletableFuture.supplyAsync(() -> {
            try {
                Thread.sleep(500);
                return "Task 2: " + Thread.currentThread().getName();
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
                return "Task 2 interrupted";
            }
        }, courseTaskExecutor);

        CompletableFuture<String> task3 = CompletableFuture.supplyAsync(() -> {
            try {
                Thread.sleep(500);
                return "Task 3: " + Thread.currentThread().getName();
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
                return "Task 3 interrupted";
            }
        }, courseTaskExecutor);

        // Wait for all tasks to complete
        CompletableFuture<Void> allTasks = CompletableFuture.allOf(task1, task2, task3);
        allTasks.get(3, TimeUnit.SECONDS);

        // Verify all tasks completed
        String result1 = task1.get();
        String result2 = task2.get();
        String result3 = task3.get();

        assertThat(result1).contains("Task 1: course-async-");
        assertThat(result2).contains("Task 2: course-async-");
        assertThat(result3).contains("Task 3: course-async-");
    }

    @Test
    void taskExecutorThreadPoolProperties() {
        ThreadPoolTaskExecutor executor = (ThreadPoolTaskExecutor) courseTaskExecutor;

        // Verify thread pool is active
        assertThat(executor.getThreadPoolExecutor()).isNotNull();

        // Verify initial state
        assertThat(executor.getActiveCount()).isGreaterThanOrEqualTo(0);
        assertThat(executor.getPoolSize()).isGreaterThanOrEqualTo(0);

        // Verify thread naming works
        assertThat(executor.getThreadNamePrefix()).isEqualTo("course-async-");
    }

    @Test
    void taskExecutorRejectionPolicy() {
        ThreadPoolTaskExecutor executor = (ThreadPoolTaskExecutor) courseTaskExecutor;

        // Verify rejection policy is set
        assertThat(executor.getThreadPoolExecutor().getRejectedExecutionHandler())
                .isInstanceOf(java.util.concurrent.ThreadPoolExecutor.CallerRunsPolicy.class);
    }

    @Test
    void taskExecutorPerformanceTest() throws InterruptedException {
        // Test performance dengan banyak task
        int taskCount = 10;
        CompletableFuture<String>[] futures = new CompletableFuture[taskCount];

        long startTime = System.currentTimeMillis();

        for (int i = 0; i < taskCount; i++) {
            final int taskId = i;
            futures[i] = CompletableFuture.supplyAsync(() -> {
                try {
                    Thread.sleep(100); // Simulate work
                    return "Task " + taskId + " completed";
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                    return "Task " + taskId + " interrupted";
                }
            }, courseTaskExecutor);
        }

        // Wait for all tasks
        CompletableFuture.allOf(futures).join();

        long endTime = System.currentTimeMillis();
        long executionTime = endTime - startTime;

        // With async execution, should be much faster than sequential (10 * 100ms = 1000ms)
        // Allow some overhead, but should be significantly less than 1000ms
        assertThat(executionTime).isLessThan(800);

        // Verify all tasks completed successfully
        for (CompletableFuture<String> future : futures) {
            assertDoesNotThrow(() -> {
                String result = future.get();
                assertThat(result).contains("completed");
            });
        }
    }
}