import java.util.Scanner;

public class Main {
    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);
        int[][] matrix = new int[3][3];

        System.out.println("Введите матрицу 3x3 (цифры от 1 до 9 без повторений):");
        for (int i = 0; i < 3; i++) {
            for (int j = 0; j < 3; j++) {
                System.out.printf("Элемент [%d][%d]: ", i, j);
                matrix[i][j] = scanner.nextInt();

                // Проверка ввода
                if (matrix[i][j] < 1 || matrix[i][j] > 9) {
                    System.out.println("Ошибка: только цифры от 1 до 9!");
                    return;
                }
            }
        }

        // Проверка на уникальность элементов
        if (!Matrix_walker.hasUniqueElements(matrix)) {
            System.out.println("Ошибка: цифры не должны повторяться!");
            return;
        }

        Matrix_walker walker = new Matrix_walker();
        int result = walker.findMaxNumber(matrix);
        System.out.println("Максимальное число: " + result);
    }
}
