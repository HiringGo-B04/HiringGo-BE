//package id.ac.ui.cs.advprog.course.service;
//
//import id.ac.ui.cs.advprog.course.config.AsyncConfig;
//import id.ac.ui.cs.advprog.course.dto.MataKuliahDto;
//import id.ac.ui.cs.advprog.course.dto.MataKuliahPatch;
//import org.junit.jupiter.api.BeforeEach;
//import org.junit.jupiter.api.Test;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
//import org.springframework.boot.autoconfigure.jdbc.DataSourceAutoConfiguration;
//import org.springframework.boot.autoconfigure.orm.jpa.HibernateJpaAutoConfiguration;
//import org.springframework.boot.test.context.SpringBootTest;
//import org.springframework.boot.test.mock.mockito.MockBean;
//import org.springframework.context.annotation.Import;
//import org.springframework.data.domain.Page;
//import org.springframework.data.domain.PageImpl;
//import org.springframework.data.domain.Pageable;
//import org.springframework.test.context.TestPropertySource;
//
//import java.util.List;
//import java.util.UUID;
//import java.util.concurrent.CompletableFuture;
//import java.util.concurrent.ExecutionException;
//import java.util.concurrent.TimeUnit;
//import java.util.concurrent.TimeoutException;
//
//import static org.assertj.core.api.Assertions.assertThat;
//import static org.junit.jupiter.api.Assertions.assertThrows;
//import static org.mockito.ArgumentMatchers.any;
//import static org.mockito.ArgumentMatchers.anyString;
//import static org.mockito.ArgumentMatchers.eq;
//import static org.mockito.Mockito.*;
//
//@SpringBootTest(classes = {
//        AsyncConfig.class,
//        AsyncMataKuliahService.class
//})
//@EnableAutoConfiguration(exclude = {
//        DataSourceAutoConfiguration.class,
//        HibernateJpaAutoConfiguration.class
//})
//@TestPropertySource(properties = {
//        "spring.main.allow-bean-definition-overriding=true"
//})
//class AsyncMataKuliahServiceTest {
//
//    @Autowired
//    private AsyncMataKuliahService asyncMataKuliahService;
//
//    @MockBean  // ← Gunakan @MockBean agar Spring create mock bean
//    private MataKuliahService mataKuliahService;
//
//    private MataKuliahDto sampleCourse;
//    private MataKuliahPatch samplePatch;
//
//    @BeforeEach
//    void setUp() {
//        // Setup sample data
//        sampleCourse = new MataKuliahDto(
//                "CS101",
//                "Introduction to Computer Science",
//                3,
//                "Basic computer science course",
//                List.of(UUID.randomUUID())
//        );
//
//        samplePatch = new MataKuliahPatch(
//                4,
//                "Updated description",
//                List.of(UUID.randomUUID())
//        );
//
//        // Reset mock untuk setiap test
//        reset(mataKuliahService);
//    }
//
//    // ========== ASYNC READ OPERATIONS TESTS ==========
//
//    @Test
//    void findAllAsync_ShouldReturnCoursesAsynchronously() throws ExecutionException, InterruptedException, TimeoutException {
//        // Arrange
//        Page<MataKuliahDto> mockPage = new PageImpl<>(List.of(sampleCourse));
//        when(mataKuliahService.findAll(any(Pageable.class))).thenReturn(mockPage);
//
//        // Act
//        CompletableFuture<Page<MataKuliahDto>> future = asyncMataKuliahService.findAllAsync(Pageable.unpaged());
//
//        // Assert
//        assertThat(future).isNotNull();
//        Page<MataKuliahDto> result = future.get(3, TimeUnit.SECONDS);
//
//        assertThat(result).isNotNull();
//        assertThat(result.getContent()).hasSize(1);
//        assertThat(result.getContent().get(0).kode()).isEqualTo("CS101");
//
//        verify(mataKuliahService, times(1)).findAll(any(Pageable.class));
//    }
//
//    @Test
//    void findByKodeAsync_ShouldReturnSpecificCourse() throws ExecutionException, InterruptedException, TimeoutException {
//        // Arrange
//        when(mataKuliahService.findByKode("CS101")).thenReturn(sampleCourse);
//
//        // Act
//        CompletableFuture<MataKuliahDto> future = asyncMataKuliahService.findByKodeAsync("CS101");
//
//        // Assert
//        assertThat(future).isNotNull();
//        MataKuliahDto result = future.get(2, TimeUnit.SECONDS);
//
//        assertThat(result).isNotNull();
//        assertThat(result.kode()).isEqualTo("CS101");
//        assertThat(result.nama()).isEqualTo("Introduction to Computer Science");
//
//        verify(mataKuliahService, times(1)).findByKode("CS101");
//    }
//
//    @Test
//    void findByKodeAsync_ShouldHandleException() {
//        // Arrange
//        when(mataKuliahService.findByKode("INVALID")).thenThrow(new RuntimeException("Course not found"));
//
//        // Act
//        CompletableFuture<MataKuliahDto> future = asyncMataKuliahService.findByKodeAsync("INVALID");
//
//        // Assert
//        assertThat(future).isNotNull();
//        assertThrows(ExecutionException.class, () -> {
//            future.get(2, TimeUnit.SECONDS);
//        });
//
//        verify(mataKuliahService, times(1)).findByKode("INVALID");
//    }
//
//    // ========== ASYNC WRITE OPERATIONS TESTS ==========
//
//    @Test
//    void createAsync_ShouldCreateCourseAsynchronously() throws ExecutionException, InterruptedException, TimeoutException {
//        // Arrange
//        when(mataKuliahService.create(any(MataKuliahDto.class))).thenReturn(sampleCourse);
//
//        // Act
//        CompletableFuture<MataKuliahDto> future = asyncMataKuliahService.createAsync(sampleCourse);
//
//        // Assert
//        assertThat(future).isNotNull();
//        MataKuliahDto result = future.get(3, TimeUnit.SECONDS);
//
//        assertThat(result).isNotNull();
//        assertThat(result.kode()).isEqualTo("CS101");
//
//        verify(mataKuliahService, times(1)).create(any(MataKuliahDto.class));
//    }
//
//    @Test
//    void updateAsync_ShouldUpdateCourseAsynchronously() throws ExecutionException, InterruptedException, TimeoutException {
//        // Arrange
//        MataKuliahDto updatedCourse = new MataKuliahDto(
//                "CS101",
//                "Updated Course Name",
//                4,
//                "Updated description",
//                List.of(UUID.randomUUID())
//        );
//        when(mataKuliahService.update(eq("CS101"), any(MataKuliahDto.class))).thenReturn(updatedCourse);
//
//        // Act
//        CompletableFuture<MataKuliahDto> future = asyncMataKuliahService.updateAsync("CS101", sampleCourse);
//
//        // Assert
//        assertThat(future).isNotNull();
//        MataKuliahDto result = future.get(3, TimeUnit.SECONDS);
//
//        assertThat(result).isNotNull();
//        assertThat(result.nama()).isEqualTo("Updated Course Name");
//        assertThat(result.sks()).isEqualTo(4);
//
//        verify(mataKuliahService, times(1)).update(eq("CS101"), any(MataKuliahDto.class));
//    }
//
//    @Test
//    void partialUpdateAsync_ShouldApplyPatchAsynchronously() throws ExecutionException, InterruptedException, TimeoutException {
//        // Arrange
//        MataKuliahDto patchedCourse = new MataKuliahDto(
//                "CS101",
//                "Introduction to Computer Science",
//                4,
//                "Updated description",
//                List.of(UUID.randomUUID())
//        );
//        when(mataKuliahService.partialUpdate(eq("CS101"), any(MataKuliahPatch.class))).thenReturn(patchedCourse);
//
//        // Act
//        CompletableFuture<MataKuliahDto> future = asyncMataKuliahService.partialUpdateAsync("CS101", samplePatch);
//
//        // Assert
//        assertThat(future).isNotNull();
//        MataKuliahDto result = future.get(3, TimeUnit.SECONDS);
//
//        assertThat(result).isNotNull();
//        assertThat(result.sks()).isEqualTo(4);
//        assertThat(result.deskripsi()).isEqualTo("Updated description");
//
//        verify(mataKuliahService, times(1)).partialUpdate(eq("CS101"), any(MataKuliahPatch.class));
//    }
//
//    @Test
//    void deleteAsync_ShouldDeleteCourseAsynchronously() throws ExecutionException, InterruptedException, TimeoutException {
//        // Arrange
//        doNothing().when(mataKuliahService).delete("CS101");
//
//        // Act
//        CompletableFuture<Void> future = asyncMataKuliahService.deleteAsync("CS101");
//
//        // Assert
//        assertThat(future).isNotNull();
//        Void result = future.get(3, TimeUnit.SECONDS);
//
//        assertThat(result).isNull();
//
//        verify(mataKuliahService, times(1)).delete("CS101");
//    }
//
//    // ========== ASYNC LECTURER OPERATIONS TESTS ==========
//
//    @Test
//    void addLecturerAsync_ShouldAddLecturerAsynchronously() throws ExecutionException, InterruptedException, TimeoutException {
//        // Arrange
//        UUID lecturerId = UUID.randomUUID();
//        doNothing().when(mataKuliahService).addLecturer("CS101", lecturerId);
//
//        // Act
//        CompletableFuture<Void> future = asyncMataKuliahService.addLecturerAsync("CS101", lecturerId);
//
//        // Assert
//        assertThat(future).isNotNull();
//        Void result = future.get(3, TimeUnit.SECONDS);
//
//        assertThat(result).isNull();
//
//        verify(mataKuliahService, times(1)).addLecturer("CS101", lecturerId);
//    }
//
//    @Test
//    void removeLecturerAsync_ShouldRemoveLecturerAsynchronously() throws ExecutionException, InterruptedException, TimeoutException {
//        // Arrange
//        UUID lecturerId = UUID.randomUUID();
//        doNothing().when(mataKuliahService).removeLecturer("CS101", lecturerId);
//
//        // Act
//        CompletableFuture<Void> future = asyncMataKuliahService.removeLecturerAsync("CS101", lecturerId);
//
//        // Assert
//        assertThat(future).isNotNull();
//        Void result = future.get(3, TimeUnit.SECONDS);
//
//        assertThat(result).isNull();
//
//        verify(mataKuliahService, times(1)).removeLecturer("CS101", lecturerId);
//    }
//
//    // ========== ASYNC BATCH OPERATIONS TESTS ==========
//
//    @Test
//    void createMultipleAsync_ShouldCreateMultipleCoursesAsynchronously() throws ExecutionException, InterruptedException, TimeoutException {
//        // Arrange
//        MataKuliahDto course1 = new MataKuliahDto("CS101", "Course 1", 3, "Desc 1", List.of());
//        MataKuliahDto course2 = new MataKuliahDto("CS102", "Course 2", 3, "Desc 2", List.of());
//        List<MataKuliahDto> inputCourses = List.of(course1, course2);
//
//        when(mataKuliahService.create(any(MataKuliahDto.class)))
//                .thenReturn(course1)
//                .thenReturn(course2);
//
//        // Act
//        CompletableFuture<List<MataKuliahDto>> future = asyncMataKuliahService.createMultipleAsync(inputCourses);
//
//        // Assert
//        assertThat(future).isNotNull();
//        List<MataKuliahDto> result = future.get(3, TimeUnit.SECONDS);
//
//        assertThat(result).isNotNull();
//        assertThat(result).hasSize(2);
//
//        verify(mataKuliahService, times(2)).create(any(MataKuliahDto.class));
//    }
//
//    @Test
//    void searchCoursesAsync_ShouldPerformSearchAsynchronously() throws ExecutionException, InterruptedException, TimeoutException {
//        // Arrange
//        MataKuliahDto course1 = new MataKuliahDto("CS101", "Computer Science", 3, "Desc", List.of());
//        MataKuliahDto course2 = new MataKuliahDto("MATH101", "Mathematics", 3, "Desc", List.of());
//        Page<MataKuliahDto> allCourses = new PageImpl<>(List.of(course1, course2));
//
//        when(mataKuliahService.findAll(any(Pageable.class))).thenReturn(allCourses);
//
//        // Act
//        CompletableFuture<List<MataKuliahDto>> future = asyncMataKuliahService.searchCoursesAsync("Computer");
//
//        // Assert
//        assertThat(future).isNotNull();
//        List<MataKuliahDto> result = future.get(3, TimeUnit.SECONDS);
//
//        assertThat(result).isNotNull();
//        assertThat(result).hasSize(1);
//        assertThat(result.get(0).kode()).isEqualTo("CS101");
//
//        verify(mataKuliahService, times(1)).findAll(any(Pageable.class));
//    }
//
//    // ========== PERFORMANCE TESTS ==========
//
//    @Test
//    void asyncOperations_ShouldExecuteConcurrently() throws ExecutionException, InterruptedException, TimeoutException {
//        // Arrange
//        when(mataKuliahService.findByKode(anyString())).thenReturn(sampleCourse);
//
//        // Act
//        long startTime = System.currentTimeMillis();
//
//        CompletableFuture<MataKuliahDto> future1 = asyncMataKuliahService.findByKodeAsync("CS101");
//        CompletableFuture<MataKuliahDto> future2 = asyncMataKuliahService.findByKodeAsync("CS102");
//        CompletableFuture<MataKuliahDto> future3 = asyncMataKuliahService.findByKodeAsync("CS103");
//
//        CompletableFuture.allOf(future1, future2, future3).get(5, TimeUnit.SECONDS);
//
//        long endTime = System.currentTimeMillis();
//        long executionTime = endTime - startTime;
//
//        // Assert
//        assertThat(executionTime).isLessThan(1000);
//
//        verify(mataKuliahService, times(3)).findByKode(anyString());
//    }
//
//    @Test
//    void asyncOperations_ShouldHandleExceptionsGracefully() {
//        // Arrange
//        when(mataKuliahService.findByKode("VALID")).thenReturn(sampleCourse);
//        when(mataKuliahService.findByKode("INVALID")).thenThrow(new RuntimeException("Not found"));
//
//        // Act
//        CompletableFuture<MataKuliahDto> validFuture = asyncMataKuliahService.findByKodeAsync("VALID");
//        CompletableFuture<MataKuliahDto> invalidFuture = asyncMataKuliahService.findByKodeAsync("INVALID");
//
//        // Assert
//        assertThat(validFuture).succeedsWithin(2, TimeUnit.SECONDS);
//        assertThat(invalidFuture).failsWithin(2, TimeUnit.SECONDS);
//    }
//}