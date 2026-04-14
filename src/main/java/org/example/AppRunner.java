package org.example;

import java.io.*;
import java.util.*;

/**
 * Основной класс программы.
 *
 * <ul>
 *   <li>1. Считывает файл, путь к которому передан в качестве 1-го аргумента
 *   <li>2. Группирует строки используя алгоритм {@link DisjointSetUnion структуры непересекающихся
 *       множеств}
 *   <li>3. Записывает большие группы в файл (output.txt в исполняемой директории либо путь,
 *       переданный в качестве 2-го аргумента)
 * </ul>
 *
 * @see DisjointSetUnion
 * @see FileProcessor
 */
public class AppRunner {

    /**
     * Запуск программы.</br>
     *
     * @param args Аргументы: путь к исходному файлу, (опционально) путь к результирующему файлу
     */
    public static void main(String[] args) {
        // Путь к файлу с данными
        String inputPath;
        if (args.length > 0) {
            inputPath = args[0];
        } else {
            throw new IllegalArgumentException("Не указан путь к обрабатываемому файлу.");
        }

        // Путь к файлу для записи результата
        String outputPath = (args.length > 1 && !args[1].isEmpty()) ? args[1] : "output.txt";

        try {
            // Инициализируем объект обработчика файла с данными для обработки
            FileProcessor fileProcessor =
                    new FileProcessor(new FileReader(inputPath), new FileWriter(outputPath));

            // Читаем валидные строки из файла
            List<String[]> lines = fileProcessor.readLinesFromFile();

            // Обработка строк
            DisjointSetUnion dsu = DisjointSetUnion.Util.processDsuForLines(lines);

            // Составляем отсортированный список больших групп множеств
            List<List<Integer>> largeGroups = DisjointSetUnion.Util.formLargeGroups(dsu, lines);

            // Записываем большие группы в файл
            fileProcessor.writeToFile(largeGroups, lines);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
