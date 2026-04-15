package org.example;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.Properties;

@DisplayName("Тестирование класса AppRunner")
class AppRunnerTest {

    private Properties propertiesForSmallFile = new Properties();
    private Properties propertiesForLargeFile = new Properties();

    @BeforeEach
    public void prepare() {
        propertiesForSmallFile.put("linePattern", "\"(.*?)\"");
        propertiesForSmallFile.put("separator", ";");
        propertiesForSmallFile.put("input", "lng.txt");
        propertiesForSmallFile.put("output", "test_output.txt");

        propertiesForLargeFile.put("linePattern", "\"(.*?)\"");
        propertiesForLargeFile.put("separator", ";");
        propertiesForLargeFile.put("input", "lng-big.csv");
        propertiesForLargeFile.put("output", "test_output.csv");
    }

    @Test
    @DisplayName("Тест скорости выполнения для файла с небольшим набором данных")
    void proceedsForLessThan30SecForSmallTestFile() {
        var runtime = Runtime.getRuntime();
        runtime.gc();
        long memoryBefore = runtime.totalMemory() - runtime.freeMemory();
        long startTime = System.currentTimeMillis();

        var sysProperties = System.getProperties();
        sysProperties.putAll(propertiesForSmallFile);
        AppRunner.main(new String[]{});

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
            "Тест скорости выполнения для файла с большим набором данных")
    void proceedsForLessThan30SecForLargeTestFile() {
        var runtime = Runtime.getRuntime();
        runtime.gc();
        long memoryBefore = runtime.totalMemory() - runtime.freeMemory();
        long startTime = System.currentTimeMillis();

        var sysProperties = System.getProperties();
        sysProperties.putAll(propertiesForLargeFile);
        AppRunner.main(new String[] {});

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
