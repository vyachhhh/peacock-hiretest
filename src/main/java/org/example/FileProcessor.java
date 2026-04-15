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
     * Паттерн отбора строк в файле. Пример: "\"(.*?)\"" будет отбирать только те строки, которые
     * начинаются и заканчиваются знаком двойной кавычки.
     */
    private final Pattern linePattern;

    /** Символ разделения элементов внутри строки */
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
     * </ul>
     *
     * @param line Строка
     * @return Массив элементов типа {@code String[]}
     * @see #formatLineToList(String)
     */
    public String[] parseLine(String line) {
        if (line.isEmpty()) return null;

        // Проверка на незакрытые кавычки
        int quoteCount = 0;
        for (int i = 0; i < line.length(); i++) {
            if (line.charAt(i) == '"') quoteCount++;
        }

        if ((quoteCount & 1) != 0) {
            // Нечетное кол-во кавычек
            return null;
        }

        if (hasInvalidPattern(line)) {
            // Проверка на невалидный паттерн (отсутствие сепаратора)
            return null;
        }

        if (!linePattern.matcher(line).matches()) {
            // Если строка не соответствует переданному паттерну this.linePattern,
            // то она является невалидной
            return null;
        }

        // Разбор на колонки
        return formatLineToList(line).toArray(new String[0]);
    }

    /**
     * Проверка на соответствие строки паттерну с кавычками.</br> Пример валидной строки:
     * "\"100\";\"200\";\"300\"". Пример невалидной строки: "\"100\"200\"" - здесь отсутствует
     * сепаратор.
     *
     * @param line Строка
     * @return {@code True} - строка является невалидной; {@code False} - является валидной.
     */
    private boolean hasInvalidPattern(String line) {
        int len = line.length();
        for (int i = 0; i < len - 1; i++) {
            if (line.charAt(i) == '"' && Character.isDigit(line.charAt(i + 1))) {
                if (i > 0 && Character.isDigit(line.charAt(i - 1))) {
                    // Строка имеет невалидный паттерн если после закрывающей
                    // кавычки следует цифра без сепаратора
                    return true;
                }
            }
        }
        return false;
    }

    /**
     * Метод форматирования строки в список элементов.
     *
     * @param line Строка
     * @return Список элементов строки типа {@code List<String>}
     */
    public List<String> formatLineToList(String line) {
        List<String> elements = new ArrayList<>(8);

        if (line == null || line.isEmpty()) {
            return elements;
        }

        boolean quoted = false;
        StringBuilder stringBuilder = new StringBuilder(16);

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
     * Метод проверки кол-ва кавычек в строке.
     *
     * @param line Строка
     * @return {@code True} - если кол-во кавычек является четным и больше нуля; {@code False} - нечетным
     */
    @Deprecated
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
            for (List<Integer> groupIndices : largeGroups) {
                pw.println("Группа " + groupNum);
                for (int index : groupIndices) {
                    pw.println(stringify(lines.get(index)));
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
