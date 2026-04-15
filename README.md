# Обработка текстовых данных посредством использования структуры непересекающихся множеств (Disjoint Set Union)

## Структура

```text
DisjointSetUnion - класс структуры системы непересекающихся множеств
FileProcessor - класс для работы с файлами
AppRunner - класс запуска программы
```

## Использование

1. Скопировать тестируемый файл в корень проекта
2. Собрать проект:

```bash
mvn clean package
```

3. Запустить программу выполнив команду:

```bash
java -Xms1G -Xmx1G -XX:+UseParallelGC -XX:NewRatio=3 -XX:SurvivorRatio=8 -DlinePattern="\"(.*?)\"" -Dseparator=";" -Dinput="исходный_файл"
```

или

```bash
java -Xms1G -Xmx1G -XX:+UseParallelGC -XX:NewRatio=3 -XX:SurvivorRatio=8 -DlinePattern="\"(.*?)\"" -Dseparator=";" -Dinput="исходный_файл" -Doutput="результирующий_файл"
```

**Пример**:
```bash
java -Xms1G -Xmx1G -XX:+UseParallelGC -XX:NewRatio=3 -XX:SurvivorRatio=8 -DlinePattern="\"(.*?)\"" -Dseparator=";" -Dinput="lng-big.csv" -Doutput="output.csv"
```

## Тесты

```text
DisjointSetUnionTest - тестирование класса DisjointSetUnion
FileProcessorTest - тестирование класса FileProcessor
AppRunnerTest - тестирование класса AppRunner
```
