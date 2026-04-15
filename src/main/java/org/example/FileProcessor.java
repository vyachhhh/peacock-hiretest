package org.example;

import lombok.RequiredArgsConstructor;

import java.io.*;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

/**
 * Класс обработки текстовых файлов.
 *
 * <ul>
 *   Используется для:
 *   <li>1. Чтения данных с файла и получения валидированного списка строк
 *   <li>2. Записи результата обработки данных посредством {@link DisjointSetUnion} в файл
 * </ul>
 */
@RequiredArgsConstructor
public class FileProcessor {

    /**
     * Паттерн избавления от неверных строк.
     *
     * <ul>
     *   Примеры невалидных строк:
     *   <li>1) "8383"200000741652251"
     *   <li>2) "79855053897"83100000580443402";"200000133000191" ""
     * </ul>
     */
    private static final Pattern INVALID_PATTERN = Pattern.compile(".*\"\\d+\"\\d+.*");

    /**
     * Паттерн отбора строк в файле.
     * Пример: "\"(.*?)\"" будет отбирать только те строки, которые начинаются и заканчиваются знаком двойной кавычки.
     */
    private final Pattern linePattern;

    private final Character separator;

    /** Объект для считывания данных. */
    private final Reader reader;

    /** Объект для записи данных. */
    private final Writer writer;

    /**
     * Метод чтения строк из файла.
     *
     * @return Список массивов элементов (Строки<Элементы[]>) типа {@code List<String[]>}
     * @throws IOException При ошибках чтения файла
     * @see #parseLine(String)
     */
    public List<String[]> readLinesFromFile() throws IOException {
        List<String[]> lines = new ArrayList<>();

        // Построчное чтение файла
        try (BufferedReader br = new BufferedReader(reader)) {
            String line;
            while ((line = br.readLine()) != null) {
                // Парсинг строки
                String[] parsed = parseLine(line);

                if (parsed != null) {
                    // Если строка валидна, то ее элементы добавляются в список
                    lines.add(parsed);
                }
            }
        }

        return lines;
    }

    /**
     * Метод парсинга строки на подстроки / элементы.
     *
     * <ul>
     *   Условия:
     *   <li>1. Строка не пуста
     *   <li>2. Строка не имеет незакрытых кавычек
     *   <li>3. Строка соответствует паттерну {@link #INVALID_PATTERN}
     * </ul>
     *
     * @param line Строка
     * @return Массив элементов типа {@code String[]}
     * @see #formatLineToList(String)
     * @see #isQuotedValid(String)
     */
    public String[] parseLine(String line) {
        if (line == null || line.trim().isEmpty()) {
            // Строка пуста
            return null;
        }

        if (line.contains("\"") && !isQuotedValid(line)) {
            // Строка имеет незакрытую кавычку
            return null;
        }

        if (line.matches(INVALID_PATTERN.pattern())) {
            // Строка соответствует невалидному паттерну
            return null;
        }

        if (!line.matches(linePattern.pattern())) {
            // Строка не соответствует валидному паттерну
            return null;
        }

        return formatLineToList(line).toArray(new String[0]);
    }

    /**
     * Метод проверки кол-ва кавычек в строке.
     *
     * @param line Строка
     * @return {@code true} - если кол-во кавычек является четным; {@code false} - нечетным
     */
    public boolean isQuotedValid(String line) {
        int quoteCount = 0;
        for (char c : line.toCharArray()) {
            if (c == '"') {
                quoteCount++;
            }
        }

        return quoteCount % 2 == 0 && quoteCount > 0;
    }

    /**
     * Метод форматирования строки в список элементов.
     *
     * @param line Строка
     * @return Список элементов строки типа {@code List<String>}
     */
    public List<String> formatLineToList(String line) {
        List<String> elements = new ArrayList<>();

        if (line == null || line.isEmpty()) {
            return elements;
        }

        boolean quoted = false;
        StringBuilder stringBuilder = new StringBuilder();

        for (int i = 0; i < line.length(); i++) {
            char c = line.charAt(i);
            if (c == '"') {
                quoted = !quoted;
            } else if (c == this.separator && !quoted) {
                // Если текущим символом является ';' вне кавычек,
                // то добавляем хранимый в stringBuilder элемент в список,
                elements.add(stringBuilder.toString());
                // Обнуляем содержимое stringBuilder
                stringBuilder.setLength(0);
            } else {
                stringBuilder.append(c);
            }
        }
        elements.add(stringBuilder.toString());
        return elements;
    }

    /**
     * Метод записи групп в файл.
     *
     * @param largeGroups Отсортированный список больших групп множеств
     * @param lines Список строк
     * @see DisjointSetUnion.Util#formLargeGroups(DisjointSetUnion, List)
     */
    public void writeToFile(List<List<Integer>> largeGroups, List<String[]> lines) {
        try (PrintWriter pw = new PrintWriter(new BufferedWriter(writer))) {
            pw.println(largeGroups.size());

            int groupNum = 1;
            for (List<Integer> groupIdx : largeGroups) {
                pw.println("Группа " + groupNum);
                for (int idx : groupIdx) {
                    pw.println(stringify(lines.get(idx)));
                }
                groupNum++;
            }
        }
    }

    /**
     * Преобразование массива элементов в единую строку, подобную изначальной строке из считанного
     * файла.
     *
     * @param arr Массив элементов
     * @return Единая строка с элементами
     */
    private String stringify(String[] arr) {
        return Arrays.stream(arr)
                .map(item -> "\"" + item + "\"")
                .collect(Collectors.joining(this.separator.toString()));
    }
}
