import java.util.Comparator;
import java.util.HashMap;
import java.util.Map;
import java.util.TreeMap;


public class Main {
    // Пример использования класса
    public static void main(String[] args) {
        // Создадим полином p1: 3x^2 + 2x + 1
        Polynomial p1 = new Polynomial();
        p1.addTerm(3, 3);
        p1.addTerm(1, 2);
        p1.addTerm(0, 1);

        // Полином p2: x^2 - 4x + 5
        Polynomial p2 = new Polynomial();
        p2.addTerm(2, 1);
        p2.addTerm(1, -4);
        p2.addTerm(0, 5);

        System.out.println("p1: " + p1);
        System.out.println("p2: " + p2);
        System.out.println("p1 + p2: " + p1.add(p2));
        System.out.println("p1 - p2: " + p1.subtract(p2));
        System.out.println("p1 * p2: " + p1.multiply(p2));
        System.out.println("p1 * 2: " + p1.multiply(2));

        // Пример деления: dividend / divisor
        // dividend: x^4 - 3x + 2
        Polynomial dividend = new Polynomial();
        dividend.addTerm(4, 1);
        dividend.addTerm(1, -3);
        dividend.addTerm(0, 2);

        // divisor: x^2 - 1
        Polynomial divisor = new Polynomial();
        divisor.addTerm(2, 1);
        divisor.addTerm(0, -1);

        Polynomial[] result = dividend.divide(divisor);
        System.out.println("Делимое: " + dividend);
        System.out.println("Делитель: " + divisor);
        System.out.println("Частное: " + result[0]);
        System.out.println("Остаток: " + result[1]);
    }
}