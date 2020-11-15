package bullscows;

import java.util.Random;
import java.util.Scanner;

public class GameManager {
    private int codeLength;
    private int numberOfUniqueSymbols;

    public GameManager() {
        initializing();
    }

    private void initializing() {
        if (initialInput()) {
            startOfTheGame();
        }
    }

    private boolean initialInput() {
        Scanner scanner = new Scanner(System.in);

        System.out.println("Input the length of the secret code:");
        if (scanner.hasNextInt()) {
            codeLength = scanner.nextInt();
            if (codeLength > 36) {
                System.out.println("Error: it's not possible to generate a code with a length that more than 36.");
                return false;
            }
            if (codeLength < 1) {
                System.out.println("Error: it's not possible to generate a code with a length that less than 1.");
                return false;
            }
        }
        else {
            System.out.println("Error: enter a valid number");
            return false;
        }

        System.out.println("Input the number of possible symbols in the code:");
        if (scanner.hasNextInt()) {
            numberOfUniqueSymbols = scanner.nextInt();
            if (numberOfUniqueSymbols > 36 || codeLength > numberOfUniqueSymbols) {
                System.out.println("Error: The number of possible symbols must be less than 36 and more or equals to code length.");
                return false;
            }
            if (numberOfUniqueSymbols < 10) {
                System.out.println("Error: it's not possible to generate a code with a number of unique symbols that less than 10.");
                return false;
            }
        }
        else {
            System.out.println("Error: enter a valid number");
            return false;
        }

        return true;
    }

    private void startOfTheGame() {
        String secretCode;
        System.out.print("The secret is prepared: ");
        for (int i = 0; i < codeLength; i++) {
            System.out.print("*");
        }

        if (numberOfUniqueSymbols > 10) {
            secretCode = secretCodeGeneratorCharSequence(codeLength, numberOfUniqueSymbols);
            System.out.print(" (0-9, " + (char)97 + "-" + (char)(numberOfUniqueSymbols - 10 + 97 - 1) + ").\n");
        }
        else {
            secretCode = secretCodeGeneratorNumber(codeLength);
            System.out.print(" (0-9).\n");
        }

        System.out.println("Okay, let's start a game!");
        coreGameLoop(secretCode);
    }

    private void coreGameLoop(String secretCode) {
        Scanner scanner = new Scanner(System.in);
        int turnCounter = 1;
        boolean isCodeGuessed = false;
        String answer;
        while (!isCodeGuessed) {
            System.out.println("Turn " + turnCounter + ":");
            do {
                answer = scanner.next();
            }
            while (!isAnswerCorrect(answer));

            isCodeGuessed = grader(answer, secretCode);
            turnCounter++;
        }
    }

    private boolean isAnswerCorrect(String answer) {
        if (answer.length() != codeLength) {
            System.out.println("Error: answer must be equals to code length.");
            System.out.println("Enter new answer:");
            return false;
        }
        return true;
    }

    /**
     * Метод возвращает строку сгенерированного секретного кода в виде последовательности символов.
     * Возможен код с симовлами (0-9) и (a-z).
     * @param codeLength Длина кода.
     * @param numberOfUniqueSymbols Число уникальных символов. Не больше 36 (цифры + латинский алфавит).
     * @return Строка с секретным кодом.
     */
    private static String secretCodeGeneratorCharSequence(int codeLength, int numberOfUniqueSymbols) {
        // локальные переменные для работы
        Random random = new Random();
        StringBuilder codeSB = new StringBuilder();
        String nextSymbol;

        // генерация символов
        while (codeSB.length() < codeLength) {
            // если в рандоме выпадет 0, то следующий символ будет цифра, иначе следующий символ
            // выберется среди букв латинского алфавита в соответствии с числом уникальных символов в коде
            if (random.nextInt(2) == 0) {
                nextSymbol = Integer.toString(random.nextInt(10));
            } else {
                // символ в int коде Unicode. от 97 до 122. маленькие латинские буквы алфавита.
                nextSymbol = Character.toString((char)(random.nextInt(numberOfUniqueSymbols - 10) + 97));
            }

            if (!codeSB.toString().contains(nextSymbol)) {
                codeSB.append(nextSymbol);
            }
        }

        return codeSB.toString();
    }

    /**
     * Метод возвращает строку сгенерированного секретного кода.
     * Строка будет содержать только цифры. Код не может начинатся с нуля.
     * @param codeLength Длина кода.
     * @return Строка с секретным кодом.
     */
    private static String secretCodeGeneratorNumber(int codeLength) {
        // локальные переменные для работы
        Random random = new Random();
        StringBuilder codeSB = new StringBuilder();
        String nextDigit;

        // генерация первого символа, чтобы он был отличен от нуля
        while (codeSB.length() < 1) {
            if (!(nextDigit = Integer.toString(random.nextInt(9))).contains("0")) {
                codeSB.append(nextDigit);
            }
        }

        // генерация оставшихся цифр
        while (codeSB.length() < codeLength) {
            nextDigit = Integer.toString(random.nextInt(9));
            if (!codeSB.toString().contains(nextDigit)) {
                codeSB.append(nextDigit);
            }
        }

        return codeSB.toString();
    }

    /**
     * Осуществляет оценку быков и коров по переданным строкам секретного кода и ответа.
     * @param answer Ответ для проверки.
     * @param secretCode Загаданный секретный код.
     * @return Возвращает булево значение, был ли угадан секретный код.
     */
    private static boolean grader(String answer, String secretCode) {
        int bullsCounter = 0;
        int cowsCounter = 0;

        for (int i = 0; i < secretCode.length(); i++) {
            if (secretCode.charAt(i) == answer.charAt(i)) {
                bullsCounter++;
            }
            else if (secretCode.contains(Character.toString(answer.charAt(i)))) {
                cowsCounter++;
            }
        }

        if (bullsCounter > 0 && cowsCounter > 0) {
            System.out.println("Grade: " + bullsCounter + " bull(s) and " + cowsCounter + " cow(s).");
        } else if (bullsCounter > 0) {
            System.out.println("Grade: " + bullsCounter + " bull(s).");
            if (bullsCounter == secretCode.length()) {
                System.out.println("Congratulations! You guessed the secret code.");
                return true;
            }
        } else if (cowsCounter > 0) {
            System.out.println("Grade: " + cowsCounter + " cow(s).");
        } else {
            System.out.println("Grade: None.");
        }

        return false;
    }
}
