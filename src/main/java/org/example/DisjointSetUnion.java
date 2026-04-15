package org.example;

import java.util.*;
import java.util.stream.Collectors;

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
        for (int i = 0; i < n; i++) {
            parent[i] = i;
        }
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
     * @return {@code True} - при объединении множеств; {@code False} - если множества уже
     *     объединены.
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
            DisjointSetUnion dsu = new DisjointSetUnion(lineCount);

            // Предварительно сохраняем длины строк для быстрого доступа
            int[] lengths = new int[lineCount];
            for (int i = 0; i < lineCount; i++) {
                lengths[i] = lines.get(i).length;
            }

            // Максимальное количество колонок
            int columnCount = 0;
            for (int length : lengths) {
                if (length > columnCount) {
                    columnCount = length;
                }
            }

            for (int col = 0; col < columnCount; col++) {
                Map<String, Integer> firstOccurrence = new HashMap<>(lineCount / 10);
                for (int row = 0; row < lineCount; row++) {
                    if (lengths[row] <= col) continue;

                    String element = lines.get(row)[col];
                    if (element == null) {
                        // Если текущий элемент null, то переходим к следующему
                        continue;
                    }
                    element = element.trim();
                    if (element.isEmpty()) {
                        // Если текущий элемент пуст, то переходим к следующему
                        continue;
                    }

                    // Индекс предыдущей строки соответствующей текущему элементу
                    Integer previousRow = firstOccurrence.putIfAbsent(element, row);
                    if (previousRow != null) {
                        dsu.unite(row, previousRow);
                    }
                }
            }
            return dsu;
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
        @Deprecated
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
            int n = dsu.size();

            if (lines.size() != n) {
                throw new IllegalArgumentException("Dsu и список строк имеют разную размерность");
            }

            // Массив размерностей строк (число элементов)
            int[] lengths = new int[n];
            for (int i = 0; i < n; i++) {
                lengths[i] = lines.get(i).length;
            }

            // Мапа групп формата Индекс группы -> Список индексов релевантных строк
            Map<Integer, List<Integer>> groups = new HashMap<>(n / 10);
            for (int i = 0; i < n; i++) {
                int root = dsu.find(i);
                groups.computeIfAbsent(root, value -> new ArrayList<>(4)).add(i);
            }

            return groups.values().stream()
                    .filter(g -> g.size() > 1)
                    .sorted(
                            (a, b) -> {
                                // Сортировка по размеру группы
                                int cmp = Integer.compare(b.size(), a.size());
                                if (cmp != 0) return cmp;
                                //  Сортировка по максимальной длине строки в группе
                                int maxA = 0, maxB = 0;
                                for (int index : a)
                                    if (lengths[index] > maxA) maxA = lengths[index];
                                for (int index : b)
                                    if (lengths[index] > maxB) maxB = lengths[index];
                                return Integer.compare(maxB, maxA);
                            })
                    .map(group -> sortGroupLinesByJoints(group, lines, lengths))
                    .collect(Collectors.toList());
        }

        /**
         * Метод сортировки строк внутри группы по пересекающимся множеством. То есть, если имеется
         * группа [A, B, C, D], в которой A <- B <- [C, D], то получаемый список будет иметь вид [0,
         * 1, 2, 3], а для случая B <- A, B <- C <- D порядок будет [1, 0, 2, 3]
         *
         * @param group Список индексов группы
         * @param lines Список строк группы
         * @param lengths Массив размеров массивов элементов списка строк
         * @return Отсортированный список индексов группы
         */
        private static List<Integer> sortGroupLinesByJoints(
                List<Integer> group, List<String[]> lines, int[] lengths) {
            if (group.size() <= 2) return group;

            // Мапа строки и ее индексов
            Map<String, List<Integer>> valueToRows = new HashMap<>(group.size() * 4);
            for (int index : group) {
                String[] line = lines.get(index);
                int length = lengths[index];

                for (int i = 0; i < length; i++) {
                    String key = line[i];
                    if (key != null && !key.isEmpty()) {
                        valueToRows.computeIfAbsent(key, value -> new ArrayList<>(4)).add(index);
                    }
                }
            }

            // Сортируемый список индексов элементов группы
            List<Integer> sorted = new ArrayList<>(group.size());
            // Сет индексов еще не обработанных в группе элементов
            Set<Integer> remaining = new HashSet<>(group);

            // Индекс текущего элемента в группе
            int current = group.stream().min(Integer::compareTo).get();
            sorted.add(current);
            remaining.remove(current);

            while (!remaining.isEmpty()) {
                int nextIndex = -1;
                String[] currentLine = lines.get(current);
                int currentLength = lengths[current];

                // Поиск первого кандидата, имеющего общее значение в той же колонке
                outer:
                for (int col = 0; col < currentLength; col++) {
                    String element = currentLine[col];
                    if (element == null || element.isEmpty()) continue;
                    List<Integer> candidates = valueToRows.get(element);
                    if (candidates != null) {
                        for (int candidateIndex : candidates) {
                            if (remaining.contains(candidateIndex)
                                    && col < lengths[candidateIndex]
                                    && element.equals(lines.get(candidateIndex)[col])) {
                                // Если среди оставшихся индексов в группе находится
                                // необработанный валидный по колонке и значению кандидат,
                                // то нужно рассмотреть его пересечения и
                                // добавить в отсортированный список
                                nextIndex = candidateIndex;
                                break outer;
                            }
                        }
                    }
                }

                if (nextIndex == -1) {
                    // Нет прямой связи по одинаковой колонке ->
                    // берём любой оставшийся элемент
                    nextIndex = remaining.iterator().next();
                }

                // Добавляем элемент в сортируемый список
                sorted.add(nextIndex);
                // И убираем его из сета необработанных элементов
                remaining.remove(nextIndex);
                // Берем следующий элемент группы
                current = nextIndex;
            }
            return sorted;
        }
    }
}
