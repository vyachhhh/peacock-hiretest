package org.example;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

/** Класс структуры системы непересекающихся множеств Disjoint Set Union (Union-Find). */
public class DisjointSetUnion {

    /**
     * Массив родителей (корней) объекта. {@code parent[i]} хранит родителя i-го элемента. <br>
     * Если{@code parent[i] == i}, то корнем элемента является он сам и следовательно он не
     * находится ни в каком из множеств; в ином случае - корнем элемента является иной элемент.
     */
    private final int[] parent;

    /**
     * Массив рангов объекта. Множество с меньшим рангом подвешивается к множеству со старшим рангом
     * при {@link #unite(int, int) их объединении}.
     */
    private final int[] rank;

    /**
     * Конструктор.
     *
     * @param n Кол-во элементов
     */
    public DisjointSetUnion(int n) {
        parent = new int[n];
        rank = new int[n];
        IntStream.range(0, n).forEach(index -> parent[index] = index);
    }

    /**
     * Метод поиска корня элемента {@code x} в структуре.
     *
     * @param x Элемент
     * @return Корень элемента {@code x}. Корень равен {@code x} если у него нет иных корней
     * @throws ArrayIndexOutOfBoundsException если {@code x <0 || x >= n}
     */
    public int find(int x) {
        int root = x; // Элемент является собственным родителем по умолчанию

        // Поиск корня множества
        while (parent[root] != root) {
            root = parent[root];
        }

        // Сжатие пути:
        // root становится корнем для всех пройденных узлов
        while (parent[x] != root) {
            int next = parent[x];
            parent[x] = root;
            x = next;
        }

        return root;
    }

    /**
     * Метод объединения множеств, которые содержат элементы X и Y. Используется объединение по
     * {@link #rank рангу}.
     *
     * @param x Индекс первого элемента
     * @param y Индекс второго элемента
     * @return {@code True} - при объединении множеств; {@code False} - если множества уже объединены.
     */
    public boolean unite(int x, int y) {
        int xRoot = find(x);
        int yRoot = find(y);
        if (xRoot == yRoot) {
            // Если X и Y имеют одинаковый корень, то они уже объединены
            return false;
        }

        if (rank[xRoot] < rank[yRoot]) {
            // Если ранг корня X меньше ранга корня Y,
            // то присваиваем корень Y в качестве родителя для корня X
            parent[xRoot] = yRoot;
        } else if (rank[xRoot] > rank[yRoot]) {
            // Если ранг корня X больше ранга корня Y,
            // то присваиваем корень X в качестве родителя для корня Y
            parent[yRoot] = xRoot;
        } else {
            // Если ранги корней X и Y одинаковы,
            // то присваиваем корень X в качестве родителя для корня Y
            // и инкрементируем ранг корня X
            parent[yRoot] = xRoot;
            rank[xRoot]++;
        }
        return true;
    }

    /**
     * Метод получения размера системы.
     *
     * @return Размер, с которым был создан объект
     */
    public int size() {
        return this.parent.length;
    }

    /** Утилитарный вложенный класс для работы с множествами и группами множеств. */
    static class Util {

        /**
         * Метод обработки структуры {@link DisjointSetUnion} для списка строк.
         *
         * @param lines Список массивов строк для выполнения обработки системой непересекающихся
         *     множеств
         * @return Объект {@link DisjointSetUnion системы непересекающихся множеств}
         * @see DisjointSetUnion
         */
        public static DisjointSetUnion processDsuForLines(List<String[]> lines) {
            int lineCount = lines.size();

            // Инициализируем объект DSU для итогового кол-ва строк
            DisjointSetUnion disjointSetUnion = new DisjointSetUnion(lineCount);

            // Получаем максимальное число элементов в строке
            int columnCount = lines.stream().mapToInt(arr -> arr.length).max().orElse(0);

            // Проходимся по всем колонкам
            for (int col = 0; col < columnCount; col++) {
                // Мапа для хранения индексов связанных со строкой индексов
                Map<String, Integer> firstOccurrence = new HashMap<>();

                // Проходимся по всем строкам
                for (int row = 0; row < lineCount; row++) {
                    String[] line = lines.get(row);
                    if (line.length <= col) {
                        // Пропускаем строку, если ее длина меньше текущей колонки
                        continue;
                    }

                    // Элемент текущей строки
                    String element = line[col].trim();
                    if (element.isEmpty()) {
                        // Пустые элементы не участвуют в объединении
                        continue;
                    }

                    // Значение предыдущей строки
                    Integer previousRow = firstOccurrence.putIfAbsent(element, row);
                    if (previousRow != null) {
                        // Объединяем множества, если предыдущая строка != null
                        disjointSetUnion.unite(row, previousRow);
                    }
                }
            }
            return disjointSetUnion;
        }

        /**
         * Метод формирования групп строк (множеств) исходя из состояния объекта {@code dsu}.
         *
         * @param dsu Объект {@link DisjointSetUnion} после выполнения {@link
         *     #processDsuForLines(List) метода обработки}
         * @return Мапа групп типа {@code Map<integer, List<Integer>>} (Корень -> Индексы связанных
         *     множеств)
         * @see DisjointSetUnion
         * @see #processDsuForLines(List)
         */
        public static Map<Integer, List<Integer>> formGroups(DisjointSetUnion dsu) {
            Map<Integer, List<Integer>> groups = new HashMap<>();
            for (int i = 0; i < dsu.size(); i++) {
                int root = dsu.find(i);
                groups.computeIfAbsent(root, k -> new ArrayList<>()).add(i);
            }
            return groups;
        }

        /**
         * Метод составления больших групп в виде отсортированного списка по: 1) размеру группы; 2)
         * максимальной размерности массива в группе.
         *
         * @param dsu dsu Объект {@link DisjointSetUnion} после выполнения {@link
         *     #processDsuForLines(List) метода обработки}
         * @param lines Список массивов элементов {@code List<String[]>}
         * @return Список больших групп множеств
         */
        public static List<List<Integer>> formLargeGroups(
                DisjointSetUnion dsu, List<String[]> lines) {
            return formGroups(dsu).values().stream()
                    .filter(group -> group.size() > 1)
                    .sorted(
                            (a, b) -> {
                                // Первичная сортировка по размеру группы
                                int sizeCompare = Integer.compare(b.size(), a.size());
                                if (sizeCompare != 0) {
                                    return sizeCompare;
                                }
                                // Вторичная сортировка по максимальному количеству колонок в группе
                                int maxColsA =
                                        a.stream()
                                                .mapToInt(idx -> lines.get(idx).length)
                                                .max()
                                                .orElse(0);
                                int maxColsB =
                                        b.stream()
                                                .mapToInt(idx -> lines.get(idx).length)
                                                .max()
                                                .orElse(0);

                                return Integer.compare(maxColsB, maxColsA);
                            })
                    .collect(Collectors.toList());
        }
    }
}
