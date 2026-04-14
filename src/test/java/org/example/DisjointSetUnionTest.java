package org.example;

import org.junit.jupiter.api.*;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

@DisplayName("Тестирование класса #DisjointSetUnion")
class DisjointSetUnionTest {

    private static final int DSU_NUMBER = 1024 * 1024;

    private DisjointSetUnion disjointSetUnion;

    @BeforeEach
    public void prepare() {
        this.disjointSetUnion = new DisjointSetUnion(DSU_NUMBER);
    }

    @Nested
    @DisplayName("Тестирование метода #find")
    class FindTest {

        @Test
        @DisplayName("Выбрасывает ArrayIndexOutOfBoundsException для X > N")
        public void throwsArrayIndexOutOfBoundsExceptionForXGreaterThanN() {
            Assertions.assertThrows(
                    ArrayIndexOutOfBoundsException.class,
                    () -> disjointSetUnion.find(DSU_NUMBER + 1));
        }

        @Test
        @DisplayName("Выбрасывает ArrayIndexOutOfBoundsException для X == N")
        public void throwsArrayIndexOutOfBoundsExceptionForXEqualsN() {
            Assertions.assertThrows(
                    ArrayIndexOutOfBoundsException.class, () -> disjointSetUnion.find(DSU_NUMBER));
        }

        @Test
        @DisplayName("Не выбрасывает исключения для всякого X в диапазоне [0;N-1]")
        public void doesNotThrowIndexOutOfBoundsExceptionForXGreaterThanZeroAndLessThanN() {
            Assertions.assertDoesNotThrow(() -> disjointSetUnion.find(0));
            Assertions.assertDoesNotThrow(() -> disjointSetUnion.find(DSU_NUMBER - 1));
        }

        @Test
        @DisplayName("Выбрасывает ArrayIndexOutOfBoundsException для X < N")
        public void throwsArrayIndexOutOfBoundsExceptionForXLessThanZero() {
            Assertions.assertThrows(
                    ArrayIndexOutOfBoundsException.class, () -> disjointSetUnion.find(-1));
        }
    }

    @Nested
    @DisplayName("Тестирование метода #unite")
    class UniteTest {
        @Test
        @DisplayName("Y подвешивается к X, если X < Y")
        public void uniteTrueForXLessThanY() {
            var uniteCompeted = disjointSetUnion.unite(100, 200);
            Assertions.assertTrue(uniteCompeted);
            Assertions.assertEquals(100, disjointSetUnion.find(100));
            Assertions.assertEquals(100, disjointSetUnion.find(200));
        }

        @Test
        @DisplayName("Y подвешивается к X, если X > Y")
        public void uniteTrueForXGreaterThanY() {
            var uniteCompleted = disjointSetUnion.unite(200, 100);
            Assertions.assertTrue(uniteCompleted);
            Assertions.assertEquals(200, disjointSetUnion.find(200));
            Assertions.assertEquals(200, disjointSetUnion.find(100));
        }

        @Test
        @DisplayName("Объединения не происходит, если X == Y")
        public void uniteFalseForXEqualsY() {
            var uniteCompleted = disjointSetUnion.unite(100, 100);
            Assertions.assertFalse(uniteCompleted);
            Assertions.assertEquals(100, disjointSetUnion.find(100));
        }

        @Test
        @DisplayName("Выбрасывает ArrayIndexOutOfBoundsException при X > N")
        public void throwsArrayIndexOutOfBoundsExceptionForXGreaterThanN() {
            Assertions.assertThrows(
                    ArrayIndexOutOfBoundsException.class,
                    () -> disjointSetUnion.unite(DSU_NUMBER + 1, 1));
        }

        @Test
        @DisplayName("Выбрасывает ArrayIndexOutOfBoundsException при X == N")
        public void throwsArrayIndexOutOfBoundsExceptionForXEqualsN() {
            Assertions.assertThrows(
                    ArrayIndexOutOfBoundsException.class,
                    () -> disjointSetUnion.unite(DSU_NUMBER, 1));
        }

        @Test
        @DisplayName("Выбрасывает ArrayIndexOutOfBoundsException при Y > N")
        public void throwsArrayIndexOutOfBoundsExceptionForYGreaterThanN() {
            Assertions.assertThrows(
                    ArrayIndexOutOfBoundsException.class,
                    () -> disjointSetUnion.unite(1, DSU_NUMBER + 1));
        }

        @Test
        @DisplayName("Выбрасывает ArrayIndexOutOfBoundsException при Y == N")
        public void throwsArrayIndexOutOfBoundsExceptionForYEqualsN() {
            Assertions.assertThrows(
                    ArrayIndexOutOfBoundsException.class,
                    () -> disjointSetUnion.unite(1, DSU_NUMBER));
        }
    }

    @Nested
    @DisplayName("Тестирование утилитарного вложенного класса Util")
    class UtilTest {

        @Nested
        @DisplayName("Тестирование метода Util#processDsuForLines")
        class ProcessDsuForLinesTest {

            @Test
            @DisplayName("Не должен вызывать исключения для пустых списков")
            public void doesNotThrowExceptionForEmptyList() {
                Assertions.assertDoesNotThrow(
                        () -> DisjointSetUnion.Util.processDsuForLines(Collections.emptyList()));
            }

            @Test
            @DisplayName("Не должен вызывать исключения для списка с одним массивом")
            public void doesNotThrowExceptionForListWithSingleArray() {
                List<String[]> list = new ArrayList<>();
                list.add(new String[] {"100", "200", "300"});
                var dsu = DisjointSetUnion.Util.processDsuForLines(list);

                Assertions.assertEquals(1, dsu.size());
                Assertions.assertEquals(0, dsu.find(0));
            }

            @Test
            @DisplayName("Проводит объединение и ребалансировку для пересекающихся множеств")
            public void unitesAndRebalances() {
                List<String[]> list = new ArrayList<>();
                list.add(new String[] {"111", "123", "222"});
                list.add(new String[] {"200", "123", "100"});
                list.add(new String[] {"300", "", "100"});
                var dsu = DisjointSetUnion.Util.processDsuForLines(list);

                Assertions.assertEquals(3, dsu.size());
                // Ожидаем индекс '1', т.к. все другие массивы подвешиваются к нему
                // по разным признакам (0 по 123 во второй колонке, 2 по третей колонке)
                Assertions.assertEquals(1, dsu.find(0));
                Assertions.assertEquals(1, dsu.find(1));
                Assertions.assertEquals(1, dsu.find(2));
            }

            @Test
            @DisplayName("Непересекающиеся элементы не подвешиваются")
            public void doesNotUniteDisjointSets() {
                List<String[]> list = new ArrayList<>();
                list.add(new String[] {"100", "200", "300"});
                list.add(new String[] {"200", "300", "100"});
                var dsu = DisjointSetUnion.Util.processDsuForLines(list);

                Assertions.assertEquals(2, dsu.size());
                // Ожидаем разные индексы, т.к. множества не были объединены
                Assertions.assertEquals(0, dsu.find(0));
                Assertions.assertEquals(1, dsu.find(1));
            }
        }

        @Nested
        @DisplayName("Тестирование метода Util#formGroups")
        class FormGroupsTest {

            @Test
            @DisplayName("Размер возвращаемой мапы совпадает с размером структуры DSU")
            public void returnsMapWithSizeEqualsToDsuSize() {
                var dsu = new DisjointSetUnion(DSU_NUMBER);
                var groupMap = DisjointSetUnion.Util.formGroups(dsu);

                Assertions.assertEquals(DSU_NUMBER, groupMap.size());
                Assertions.assertEquals(DSU_NUMBER - 1, groupMap.get(DSU_NUMBER - 1).get(0));
            }

            @Test
            @DisplayName("Возвращает пустую мапу для пустого DSU")
            public void returnsEmptyMapForEmptyDsu() {
                var dsu = new DisjointSetUnion(0);
                var groupMap = DisjointSetUnion.Util.formGroups(dsu);

                Assertions.assertEquals(0, groupMap.size());
            }
        }

        @Nested
        @DisplayName("Тестирование метода Util#formLargeGroups")
        class FormLargeGroupsTest {

            @Test
            @DisplayName(
                    "Возвращает пустой список для DSU с непересекающимися множествами без обработки Util#processDsuForLines")
            public void doesNotAddDisjointSets() {
                var dsu = new DisjointSetUnion(DSU_NUMBER);
                var groups = DisjointSetUnion.Util.formLargeGroups(dsu, Collections.emptyList());

                Assertions.assertEquals(0, groups.size());
            }

            @Test
            @DisplayName(
                    "Возвращает пустой список для DSU с непересекающимися множествами после обработки Util#processDsuForLines")
            public void doesNotUniteDisjointSets() {
                List<String[]> list = new ArrayList<>();
                list.add(new String[] {"100", "200", "300"});
                list.add(new String[] {"200", "300", "100"});
                var dsu = DisjointSetUnion.Util.processDsuForLines(list);
                var groups = DisjointSetUnion.Util.formLargeGroups(dsu, list);

                Assertions.assertEquals(0, groups.size());
            }

            @Test
            @DisplayName(
                    "Возвращает непустой список для DSU с пересекающимися множествами, который не содержит в себе малые группы")
            public void unitesAndAbandonsSmallGroups() {
                List<String[]> list = new ArrayList<>();
                list.add(new String[] {"999", "888", "777"});
                list.add(new String[] {"111", "123", "222"});
                list.add(new String[] {"200", "123", "100"});
                list.add(new String[] {"300", "", "100"});
                var dsu = DisjointSetUnion.Util.processDsuForLines(list);
                var groups = DisjointSetUnion.Util.formLargeGroups(dsu, list);

                // Т.к. массивы [0] и [1;2;3] в списке относятся к разным группам,
                // а formLargeGroups возвращает только группы с размером > 1,
                // то размер будет 1
                Assertions.assertEquals(1, groups.size());
                // Ожидаем [1, 2, 3] т.к. это индексы пересекающихся множеств в списке list
                Assertions.assertEquals(List.of(1, 2, 3), groups.get(0));
            }
        }
    }
}
