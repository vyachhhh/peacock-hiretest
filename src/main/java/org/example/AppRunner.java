package org.example;

import java.io.*;
import java.util.*;
import java.util.regex.Pattern;

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
        String linePattern = System.getProperty("linePattern", "\"(.*?)\"");
        String separator = System.getProperty("separator");
        String input = System.getProperty("input");
        String output = System.getProperty("output", "output.txt");

        if (linePattern == null || linePattern.isEmpty()) {
            throw new IllegalArgumentException("Требуется указать паттерн строки");
        }
        if (separator == null || separator.length() != 1) {
            throw new IllegalArgumentException("Длина разделителя элементов строки должна равняться 1: " + separator);
        }
        if (input == null || input.isEmpty()) {
            throw new IllegalArgumentException("Не указан путь к обрабатываемому файлу");
        }

        try {
            // Инициализируем объект обработчика файла с данными для обработки
            FileProcessor fileProcessor =
                    new FileProcessor(Pattern.compile(linePattern), separator.charAt(0), new FileReader(input), new FileWriter(output));

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
