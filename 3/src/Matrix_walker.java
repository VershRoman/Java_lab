public class Matrix_walker {

    private int[][] matrix;
    private boolean[][] visited;
    private int maxNumber = 0;

    public int findMaxNumber(int[][] matrix) {
        this.matrix = matrix;
        this.visited = new boolean[3][3];

        // Перебираем все возможные стартовые позиции
        for (int i = 0; i < 3; i++) {
            for (int j = 0; j < 3; j++) {
                dfs(i, j, 0);
            }
        }

        return maxNumber;
    }

    private void dfs(int i, int j, int currentNumber) {
        // Проверка границ и посещенных клеток
        if (i < 0 || i >= 3 || j < 0 || j >= 3 || visited[i][j]) {
            return;
        }

        visited[i][j] = true;
        currentNumber = currentNumber * 10 + matrix[i][j];
        maxNumber = Math.max(maxNumber, currentNumber);

        // Рекурсивный обход соседей
        dfs(i + 1, j, currentNumber); // Вниз
        dfs(i - 1, j, currentNumber); // Вверх
        dfs(i, j + 1, currentNumber); // Вправо
        dfs(i, j - 1, currentNumber); // Влево

        visited[i][j] = false; // Возврат
    }

    public static boolean hasUniqueElements(int[][] matrix) {
        boolean[] seen = new boolean[10]; // Индексы 1-9
        for (int[] row : matrix) {
            for (int num : row) {
                if (seen[num]) {
                    return false;
                }
                seen[num] = true;
            }
        }
        return true;
    }
}