package org.example;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

@DisplayName("Тестирование класса AppRunner")
class AppRunnerTest {

    private static final String INPUT_FILE = "lng.txt";
    private static final String OUTPUT_FILE = "test_output.txt";

    @Test
    @DisplayName("Тест скорости выполнения для кейса с указанием исходного файла с данными")
    void proceedsForLessThan30SecWithExistentTestDataFile() {
        var runtime = Runtime.getRuntime();
        runtime.gc();
        long memoryBefore = runtime.totalMemory() - runtime.freeMemory();
        long startTime = System.currentTimeMillis();

        AppRunner.main(new String[] {INPUT_FILE});

        long endTime = System.currentTimeMillis();
        long totalTime = endTime - startTime;
        System.out.printf("%.4f sec\n", (double) totalTime / 1000);

        long memoryAfter = runtime.totalMemory() - runtime.freeMemory();
        long memoryUsedMb = (memoryAfter - memoryBefore) / 1024 / 1024;
        System.out.printf("Memory used: %dMb\n", memoryUsedMb);

        Assertions.assertTrue(totalTime < 30_000L);
        Assertions.assertTrue(memoryUsedMb < 1024);
    }

    @Test
    @DisplayName(
            "Тест скорости выполнения для кейса с указанием исходного файла с данными и файла для записи результата")
    void proceedsForLessThan30SecWithExistentTestDataFileAndOutput() {
        var runtime = Runtime.getRuntime();
        runtime.gc();
        long memoryBefore = runtime.totalMemory() - runtime.freeMemory();
        long startTime = System.currentTimeMillis();

        AppRunner.main(new String[] {INPUT_FILE, OUTPUT_FILE});

        long endTime = System.currentTimeMillis();
        long totalTime = endTime - startTime;
        System.out.printf("%.4f sec\n", (double) totalTime / 1000);

        long memoryAfter = runtime.totalMemory() - runtime.freeMemory();
        long memoryUsedMb = (memoryAfter - memoryBefore) / 1024 / 1024;
        System.out.printf("Memory used: %dMb\n", memoryUsedMb);

        Assertions.assertTrue(totalTime < 30_000L);
        Assertions.assertTrue(memoryUsedMb < 1024);
    }
}
