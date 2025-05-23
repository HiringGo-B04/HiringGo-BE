package id.ac.ui.cs.advprog.authjwt.config;

import org.junit.jupiter.api.Test;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;

import java.util.concurrent.Executor;

import static org.junit.jupiter.api.Assertions.*;

class AsyncConfigTest {

    @Test
    void testTaskExecutorBeanConfiguration() {
        // Given
        AsyncConfig config = new AsyncConfig();

        // When
        Executor executor = config.taskExecutor();

        // Then
        assertNotNull(executor);
        assertTrue(executor instanceof ThreadPoolTaskExecutor);

        ThreadPoolTaskExecutor threadPoolExecutor = (ThreadPoolTaskExecutor) executor;
        assertEquals(5, threadPoolExecutor.getCorePoolSize());
        assertEquals(20, threadPoolExecutor.getMaxPoolSize());
        assertEquals(100, threadPoolExecutor.getThreadPoolExecutor().getQueue().remainingCapacity());
        assertTrue(threadPoolExecutor.getThreadNamePrefix().startsWith("HiringGo-"));
    }
}
