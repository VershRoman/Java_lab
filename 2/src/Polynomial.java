import java.util.Comparator;
import java.util.HashMap;
import java.util.Map;
import java.util.TreeMap;

public class Polynomial implements Comparable<Polynomial>, Cloneable {
    // Хранение коэффициентов: ключ – степень, значение – коэффициент
    private HashMap<Integer, Integer> coefficients;

    public Polynomial() {
        coefficients = new HashMap<>();
    }

    public Polynomial(HashMap<Integer, Integer> coefficients) {
        this.coefficients = new HashMap<>();
        // Копируем только ненулевые коэффициенты
        for (Map.Entry<Integer, Integer> entry : coefficients.entrySet()) {
            if (Math.abs(entry.getValue()) > 1e-9) {
                this.coefficients.put(entry.getKey(), entry.getValue());
            }
        }
    }

    // Метод для добавления (или обновления) монома
    public void addTerm(int exponent, Integer coefficient) {
        if (Math.abs(coefficient) < 1e-9) {
            return; // пропускаем нулевой коэффициент
        }
        Integer newCoef = coefficients.getOrDefault(exponent, 0) + coefficient;
        if (Math.abs(newCoef) < 1e-9) {
            coefficients.remove(exponent);
        } else {
            coefficients.put(exponent, newCoef);
        }
    }

    // Получение степени полинома (наибольшая степень с ненулевым коэффициентом)
    public int degree() {
        if (coefficients.isEmpty()) {
            return 0;
        }
        int max = 0;
        for (int exp : coefficients.keySet()) {
            if (exp > max) {
                max = exp;
            }
        }
        return max;
    }

    // Получение ведущего коэффициента (коэффициент при максимальной степени)
    public Integer leadingCoefficient() {
        return coefficients.getOrDefault(degree(), 0);
    }

    // Сложение полиномов
    public Polynomial add(Polynomial other) {
        Polynomial result = this.clone();
        for (Map.Entry<Integer, Integer> entry : other.coefficients.entrySet()) {
            result.addTerm(entry.getKey(), entry.getValue());
        }
        return result;
    }

    // Вычитание полиномов
    public Polynomial subtract(Polynomial other) {
        Polynomial result = this.clone();
        for (Map.Entry<Integer, Integer> entry : other.coefficients.entrySet()) {
            result.addTerm(entry.getKey(), -entry.getValue());
        }
        return result;
    }

    // Умножение полиномов
    public Polynomial multiply(Polynomial other) {
        Polynomial result = new Polynomial();
        for (Map.Entry<Integer, Integer> term1 : this.coefficients.entrySet()) {
            for (Map.Entry<Integer, Integer> term2 : other.coefficients.entrySet()) {
                int exp = term1.getKey() + term2.getKey();
                Integer coef = term1.getValue() * term2.getValue();
                result.addTerm(exp, coef);
            }
        }
        return result;
    }

    // Умножение полинома на число
    public Polynomial multiply(Integer scalar) {
        Polynomial result = new Polynomial();
        for (Map.Entry<Integer, Integer> entry : this.coefficients.entrySet()) {
            result.addTerm(entry.getKey(), entry.getValue() * scalar);
        }
        return result;
    }

    /**
     * Деление полиномов с получением частного и остатка.
     * Возвращается массив из двух полиномов: [quotient, remainder].
     */
    public Polynomial[] divide(Polynomial divisor) {
        if (divisor.coefficients.isEmpty()) {
            throw new ArithmeticException("Деление на нулевой полином");
        }
        Polynomial dividend = this.clone();
        Polynomial quotient = new Polynomial();
        int divisorDegree = divisor.degree();
        Integer divisorLeadingCoef = divisor.leadingCoefficient();

        // Алгоритм длинного деления
        while (!dividend.coefficients.isEmpty() && dividend.degree() >= divisorDegree) {
            int degreeDiff = dividend.degree() - divisorDegree;
            Integer coefQuotient = dividend.leadingCoefficient() / divisorLeadingCoef;
            // Создаем моном term = coefQuotient * x^degreeDiff
            Polynomial term = new Polynomial();
            term.addTerm(degreeDiff, coefQuotient);
            quotient = quotient.add(term);
            // Вычитаем произведение делителя на найденный моном из делимого
            Polynomial subtraction = divisor.multiply(term);
            dividend = dividend.subtract(subtraction);
        }
        return new Polynomial[]{quotient, dividend};
    }

    // Переопределение метода toString() для строкового представления полинома
    @Override
    public String toString() {
        if (coefficients.isEmpty()) return "0";
        // Используем TreeMap для сортировки по убыванию степени
        TreeMap<Integer, Integer> sorted = new TreeMap<>(Comparator.reverseOrder());
        sorted.putAll(coefficients);

        StringBuilder sb = new StringBuilder();
        for (Map.Entry<Integer, Integer> entry : sorted.entrySet()) {
            int exp = entry.getKey();
            Integer coef = entry.getValue();
            // Добавляем знак перед следующим мономом
            if (sb.length() > 0) {
                sb.append(coef >= 0 ? " + " : " - ");
            } else {
                if (coef < 0) {
                    sb.append("-");
                }
            }
            Integer absCoef = Math.abs(coef);
            // Если степень равна 0 или коэффициент не равен 1, выводим коэффициент
            if (exp == 0 || Math.abs(absCoef - 1.0) > 1e-9) {
                sb.append(absCoef);
            }
            if (exp > 0) {
                sb.append("x");
                if (exp > 1) {
                    sb.append("^").append(exp);
                }
            }
        }
        return sb.toString();
    }

    // Реализация интерфейса Comparable: сравнение по степени, а при равенстве – по коэффициентам
    @Override
    public int compareTo(Polynomial other) {
        int thisDegree = this.degree();
        int otherDegree = other.degree();
        if (thisDegree != otherDegree) {
            return Integer.compare(thisDegree, otherDegree);
        }
        // Если степени равны, сравниваем коэффициенты по убыванию степени
        TreeMap<Integer, Integer> thisSorted = new TreeMap<>(Comparator.reverseOrder());
        TreeMap<Integer, Integer> otherSorted = new TreeMap<>(Comparator.reverseOrder());
        thisSorted.putAll(this.coefficients);
        otherSorted.putAll(other.coefficients);
        for (int exp : thisSorted.keySet()) {
            Integer diff = thisSorted.get(exp) - otherSorted.getOrDefault(exp, 0);
            if (Math.abs(diff) > 1e-9) {
                return diff > 0 ? 1 : -1;
            }
        }
        return 0;
    }

    // Реализация интерфейса Cloneable
    @Override
    public Polynomial clone() {
        try {
            Polynomial cloned = (Polynomial) super.clone();
            cloned.coefficients = new HashMap<>(this.coefficients);
            return cloned;
        } catch (CloneNotSupportedException e) {
            throw new AssertionError(e);
        }
    }
}