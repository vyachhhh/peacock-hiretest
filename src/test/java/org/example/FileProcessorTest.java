package org.example;

import org.junit.jupiter.api.*;

import java.io.*;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

@DisplayName("Тестирование класса работы с файлами FileProcessor")
class FileProcessorTest {

    private Writer writer;
    private FileProcessor validFileProcessor;
    private FileProcessor emptyFileProcessor;

    @BeforeEach
    public void prepare() {
        Reader reader =
                new StringReader(
                        "\"111\";\"123\";\"222\"\n"
                                + "\"200\";\"123\";\"100\"\n"
                                + "\"300\";\"\";\"100\"");
        writer = new StringWriter();

        validFileProcessor = new FileProcessor(reader, writer);

        emptyFileProcessor = new FileProcessor(new StringReader(""), new StringWriter());
    }

    @Nested
    @DisplayName("Тестирование метода #readLinesFromFile")
    class ReadLinesFromFileTest {

        @Test
        @DisplayName("Валидные строки не отбрасываются")
        public void doesNotThrowExceptionForValidLines() throws IOException {
            var lines = validFileProcessor.readLinesFromFile();

            Assertions.assertEquals(3, lines.size());
            Assertions.assertEquals(
                    Arrays.deepToString(new String[] {"111", "123", "222"}),
                    Arrays.deepToString(lines.get(0)));
        }

        @Test
        @DisplayName("Не выбрасывает исключение при чтении невалидных данных")
        public void doesNotThrowExceptionForEmptyReader() throws IOException {
            var lines = emptyFileProcessor.readLinesFromFile();

            Assertions.assertEquals(0, lines.size());
        }
    }

    @Nested
    @DisplayName("Тестирование метода #parseLine")
    class ParseLineTest {

        @Test
        @DisplayName("Возвращает null для пустой строки")
        public void returnsNullForEmptyLine() {
            var elements = validFileProcessor.parseLine("");

            Assertions.assertNull(elements);
        }

        @Test
        @DisplayName("Возвращает null для строки с неверно проставленными кавычками")
        public void returnsNullForInvalidQuotes() {
            var elements = validFileProcessor.parseLine("\"100\";\"200");

            Assertions.assertNull(elements);
        }

        @Test
        @DisplayName("Возвращает null для строки с неверным паттерном")
        public void returnsNullForInvalidPattern() {
            var elements = validFileProcessor.parseLine("'100';'200'");

            Assertions.assertNull(elements);
        }

        @Test
        @DisplayName("Возвращает не null объект для валидной строки")
        public void returnsNotNullForValidPattern() {
            var elements = validFileProcessor.parseLine("\"100\";\"200\"");

            Assertions.assertNotNull(elements);
            Assertions.assertEquals(2, elements.length);
            Assertions.assertEquals("100", elements[0]);
            Assertions.assertEquals("200", elements[1]);
        }
    }

    @Nested
    @DisplayName("Тестирование метода #isQoutedValid")
    class IsQuotedValidTest {

        @Test
        @DisplayName("Возвращает False при нечетном кол-ве кавычек")
        public void returnsFalseForOddQuoteCount() {
            Assertions.assertFalse(validFileProcessor.isQuotedValid("\"123\";\"200"));
        }

        @Test
        @DisplayName("Возвращает False если кавычек нет вовсе")
        public void returnsFalseForZeroQuoteCount() {
            Assertions.assertFalse(validFileProcessor.isQuotedValid("123;200"));
        }

        @Test
        @DisplayName("Возвращает True при четном кол-ве кавычек")
        public void returnsTrueForEvenQuoteCount() {
            Assertions.assertTrue(validFileProcessor.isQuotedValid("\"123\";\"200\""));
        }
    }

    @Nested
    @DisplayName("Тестирование метода #formatLineToList")
    class FormatLineToListTest {

        @Test
        @DisplayName("Возвращает пустой список при пустой строке")
        public void returnsEmptyListForEmptyLine() {
            var list = validFileProcessor.formatLineToList("");

            Assertions.assertTrue(list.isEmpty());
        }

        @Test
        @DisplayName("Возвращает пустой список при null строке")
        public void returnsEmptyListForNull() {
            var list = validFileProcessor.formatLineToList(null);

            Assertions.assertTrue(list.isEmpty());
        }

        @Test
        @DisplayName("Сохраняет целостность для пустых элементов")
        public void keepsEmptyElementsInLine() {
            var list = validFileProcessor.formatLineToList("\"100\";\"\";\"300\"");

            Assertions.assertEquals(3, list.size());
            Assertions.assertEquals("", list.get(1));
        }

        @Test
        @DisplayName("Поддерживает отрицательные числа")
        public void keepsNegativeElementsInLine() {
            var list = validFileProcessor.formatLineToList("\"100\";\"-200\";\"300\"");

            Assertions.assertEquals(3, list.size());
            Assertions.assertEquals("-200", list.get(1));
        }
    }

    @Nested
    @DisplayName("Тестирование метода #writeToFile")
    class WriteToFileTest {

        @Test
        @DisplayName("Не выбрасывает исключения если пусты как список групп, так и список строк")
        public void doesNotThrowsExceptionForEmptyGroupsAndEmptyLines() {
            Assertions.assertDoesNotThrow(
                    () ->
                            validFileProcessor.writeToFile(
                                    Collections.emptyList(), Collections.emptyList()));

            var writedString = writer.toString();
            System.out.println(writedString);

            Assertions.assertFalse(writedString.isEmpty());
            Assertions.assertTrue(writedString.contains("0"));
        }

        @Test
        @DisplayName(
                "Выбрасывает IndexOutOfBoundsException если список групп не пуст, но пуст список строк")
        public void throwsIndexOutOfBoundsForNotEmptyGroupsAndEmptyLines() {
            List<List<Integer>> largeGroups = List.of(List.of(0, 1, 2));
            List<String[]> lines = Collections.emptyList();

            Assertions.assertThrows(
                    IndexOutOfBoundsException.class,
                    () -> validFileProcessor.writeToFile(largeGroups, lines));
        }

        @Test
        @DisplayName("Не выбрасывает исключения если список групп пуст, но список строк не пуст")
        public void doesNotThrowsExceptionForEmptyGroupsAndNotEmptyLines() {
            List<List<Integer>> largeGroups = Collections.emptyList();
            List<String[]> lines =
                    List.of(
                            new String[] {"111", "123", "222"},
                            new String[] {"200", "123", "100"},
                            new String[] {"300", "", "100"});

            Assertions.assertDoesNotThrow(() -> validFileProcessor.writeToFile(largeGroups, lines));
            var writedString = writer.toString();
            Assertions.assertTrue(writedString.contains("0"));
        }

        @Test
        @DisplayName("Запись производится для непустых списков групп и строк")
        public void writesGroupsInValidFormat() {
            List<List<Integer>> largeGroups = List.of(List.of(0, 1, 2));

            List<String[]> lines =
                    List.of(
                            new String[] {"111", "123", "222"},
                            new String[] {"200", "123", "100"},
                            new String[] {"300", "", "100"});

            validFileProcessor.writeToFile(largeGroups, lines);

            var writedString = writer.toString();
            System.out.println(writedString);

            Assertions.assertFalse(writedString.isEmpty());
            Assertions.assertTrue(writedString.contains("1"));
            Assertions.assertTrue(writedString.contains("Группа 1"));
            Assertions.assertTrue(writedString.contains("\"111\";\"123\";\"222\""));
            Assertions.assertTrue(writedString.contains("\"200\";\"123\";\"100\""));
            Assertions.assertTrue(writedString.contains("\"300\";\"\";\"100\""));
        }
    }
}
