# Обработка текстовых данных посредством использования структуры непересекающихся множеств (Disjoint Set Union)

## Структура

```text
DisjointSetUnion - класс структуры системы непересекающихся множеств
FileProcessor - класс для работы с файлами
Main - класс запуска программы
```

## Использование

1. Скопировать тестируемый файл в корень проекта
2. Собрать проект:

```bash
mvn clean package
```

3. Запустить программу выполнив команду:

```bash
java -Xmx1G -jar target/peacock-hiretest-1.0.jar <путь к искомому файлу для обработки> # создаст файл output.txt в текущей директории для записи результата
```

или

```bash
java -Xmx1G -jar target/peacock-hiretest-1.0.jar <путь к искомому файлу для обработки> <путь к файлу с результирующими данными>
```

**Пример**:
```bash
java -Xmx1G -jar target/peacock-hiretest-1.0.jar lng.txt output.txt
```

## Тесты

```text
DisjointSetUnionTest - тестирование класса DisjointSetUnion
FileProcessorTest - тестирование класса FileProcessor
MainTest - тестирование класса Main
```
