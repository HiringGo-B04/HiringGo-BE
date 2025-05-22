package id.ac.ui.cs.advprog.course.service;

import id.ac.ui.cs.advprog.course.dto.MataKuliahDto;
import org.springframework.data.domain.Pageable;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.concurrent.CompletableFuture;

@Service
public class AsyncMataKuliahService {

    private final MataKuliahService mataKuliahService;

    public AsyncMataKuliahService(MataKuliahService mataKuliahService) {
        this.mataKuliahService = mataKuliahService;
    }

    // ========== ASYNC BATCH OPERATIONS (MOST IMPORTANT) ==========

    /**
     * Create multiple courses asynchronously - useful for bulk imports
     */
    @Async("courseTaskExecutor")
    public CompletableFuture<List<MataKuliahDto>> createMultipleAsync(List<MataKuliahDto> courses) {
        try {
            // Validate each course before creation
            for (MataKuliahDto course : courses) {
                validateCourseData(course);
            }

            List<MataKuliahDto> createdCourses = courses.stream()
                    .map(mataKuliahService::create)
                    .toList();

            // Send batch notification
            sendBatchCoursesCreatedNotification(createdCourses);

            return CompletableFuture.completedFuture(createdCourses);
        } catch (Exception e) {
            CompletableFuture<List<MataKuliahDto>> future = new CompletableFuture<>();
            future.completeExceptionally(e);
            return future;
        }
    }

    // ========== ASYNC SEARCH OPERATIONS ==========

    /**
     * Search courses with complex filtering - can be slow with large datasets
     */
    @Async("courseTaskExecutor")
    public CompletableFuture<List<MataKuliahDto>> searchCoursesAsync(String searchTerm) {
        try {
            // Simulate complex search that might involve multiple data sources
            Thread.sleep(200); // Remove in production

            // Get all courses and filter (in real implementation, use database search)
            var allCourses = mataKuliahService.findAll(Pageable.unpaged());
            List<MataKuliahDto> results = allCourses.getContent().stream()
                    .filter(course ->
                            course.nama().toLowerCase().contains(searchTerm.toLowerCase()) ||
                                    course.kode().toLowerCase().contains(searchTerm.toLowerCase()) ||
                                    (course.deskripsi() != null &&
                                            course.deskripsi().toLowerCase().contains(searchTerm.toLowerCase()))
                    )
                    .toList();

            return CompletableFuture.completedFuture(results);
        } catch (Exception e) {
            CompletableFuture<List<MataKuliahDto>> future = new CompletableFuture<>();
            future.completeExceptionally(e);
            return future;
        }
    }

    /**
     * Get courses by lecturer - might need to join multiple tables
     */
    @Async("courseTaskExecutor")
    public CompletableFuture<List<MataKuliahDto>> getCoursesByLecturerAsync(java.util.UUID lecturerId) {
        try {
            // This would require complex query joining mata_kuliah and mata_kuliah_lecturer tables
            Thread.sleep(300); // Simulate complex query

            var allCourses = mataKuliahService.findAll(Pageable.unpaged());
            List<MataKuliahDto> results = allCourses.getContent().stream()
                    .filter(course -> course.dosenPengampu().contains(lecturerId))
                    .toList();

            return CompletableFuture.completedFuture(results);
        } catch (Exception e) {
            CompletableFuture<List<MataKuliahDto>> future = new CompletableFuture<>();
            future.completeExceptionally(e);
            return future;
        }
    }

    // ========== PRIVATE HELPER METHODS ==========

    private void validateCourseData(MataKuliahDto courseDto) {
        // Basic validation - could be expanded to check external services
        if (courseDto.kode() == null || courseDto.kode().trim().isEmpty()) {
            throw new IllegalArgumentException("Course code cannot be empty");
        }
        if (courseDto.nama() == null || courseDto.nama().trim().isEmpty()) {
            throw new IllegalArgumentException("Course name cannot be empty");
        }
        if (courseDto.sks() <= 0) {
            throw new IllegalArgumentException("SKS must be greater than 0");
        }
    }

    private void sendBatchCoursesCreatedNotification(List<MataKuliahDto> courses) {
        // Send notification to administrators about bulk course creation
        // This could be email, message queue, or webhook
        try {
            Thread.sleep(50); // Simulate notification sending
            System.out.println("Notification sent: " + courses.size() + " courses created");
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
    }
}