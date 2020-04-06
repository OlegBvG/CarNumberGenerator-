import java.io.FileOutputStream;
import java.io.IOException;
import java.util.concurrent.Executors;
import java.util.concurrent.ThreadPoolExecutor;

public class Loader
{
    public static void main(String[] args) throws Exception
    {
        long start = System.currentTimeMillis();

        FileOutputStream writer = new FileOutputStream("res/numbers.txt");

        char letters[] = {'У', 'К', 'Е', 'Н', 'Х', 'В', 'А', 'Р', 'О', 'С', 'М', 'Т'};
        for(int number = 1; number < 1000; number++)
        {
            int regionCode = 199;
            for (char firstLetter : letters)
            {
                for (char secondLetter : letters)
                {
                    for (char thirdLetter : letters)
                    {
                        String carNumber = firstLetter + padNumber(number, 3) +
                                secondLetter + thirdLetter + padNumber(regionCode, 2);
                        writer.write(carNumber.getBytes());
                        writer.write('\n');
                    }
                }
            }
        }

        writer.flush();
        writer.close();

        System.out.println((System.currentTimeMillis() - start) + " ms");

//        ------------------

         start = System.currentTimeMillis();

        FileOutputStream writer2 = new FileOutputStream("res/numbers2.txt");
//        PrintWriter writer2 = new PrintWriter("res/numbers2.txt");  при использовании PrintWriter результат ухудшается - вероятно из-за дополнительной проверки
        StringBuilder stringBuilder = new StringBuilder();

        for(int number = 1; number < 1000; number++)
        {
            int regionCode = 199;
            for (char firstLetter : letters)
            {
                for (char secondLetter : letters)
                {
                    for (char thirdLetter : letters)
                    {
                        stringBuilder.append(firstLetter + padNumber2(number, 3) +
                                secondLetter + thirdLetter + padNumber2(regionCode, 2) + "\n");

                    }
                }
            }
            writer2.write(stringBuilder.toString().getBytes());
             stringBuilder = new StringBuilder();

        }

        writer2.flush();
        writer2.close();

        System.out.println("При использовании StringBuilder - " + (System.currentTimeMillis() - start) + " ms");

        start = System.currentTimeMillis();
        final int THREADS = 4;
        final int NUMBERS_COUNT = 1_000;

        ThreadPoolExecutor exec = (ThreadPoolExecutor) Executors.newFixedThreadPool(THREADS);
        int numberFile = 1;

        for (int i = 0; i < NUMBERS_COUNT; i = i + NUMBERS_COUNT / THREADS) {
            int numberFrom = i + 1;
            int numberTo = i + NUMBERS_COUNT / THREADS;
            int finalNumberFile = numberFile;
            Runnable r = () -> {
                try {
                    outWithThread(letters, numberFrom, numberTo , finalNumberFile);
                } catch (IOException e) {
                    e.printStackTrace();
                }
            };
            numberFile++;

            exec.execute(r);
        }

        while (exec.getCompletedTaskCount() < THREADS)  {
            Thread.sleep(10);
        }
        exec.shutdown();

        System.out.println("При использовании ThreadPool - " + (System.currentTimeMillis() - start) + " ms");

    }

    private static String padNumber(int number, int numberLength)
    {
        String numberStr = Integer.toString(number);
        int padSize = numberLength - numberStr.length();
        for(int i = 0; i < padSize; i++) {
            numberStr = '0' + numberStr;
        }
        return numberStr;
    }

    private static String padNumber2(int number, int numberLength)
    {
        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append(number);
        int padSize = numberLength - stringBuilder.length();
        for(int i = 0; i < padSize; i++) {
            stringBuilder.insert(0,'0');
        }
        return stringBuilder.toString();

    }

    private static void  outWithThread(char letters[], int numberFrom, int numberTo, int numberFile) throws IOException {

        FileOutputStream writerThread = new FileOutputStream("res/numbersThread" + numberFile + ".txt");
        StringBuilder stringBuilder = new StringBuilder();

        for (int number = numberFrom; number < numberTo; number++) {
            int regionCode = 199;
            for (char firstLetter : letters) {
                for (char secondLetter : letters) {
                    for (char thirdLetter : letters) {
                        stringBuilder.append(firstLetter + padNumber2(number, 3) +
                                secondLetter + thirdLetter + padNumber2(regionCode, 2) + "\n");

                    }
                }
            }
            writerThread.write(stringBuilder.toString().getBytes());
            stringBuilder = new StringBuilder();

        }

        writerThread.flush();
        writerThread.close();
    }


}
