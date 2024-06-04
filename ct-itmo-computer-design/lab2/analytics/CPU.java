public class CPU {

    static final int M = 64;
    static final int N = 60;
    static final int K = 32;

    public static void main(String args[]) {
        Cache cache = new Cache();
        int cacheTact;//сколько тактов потратил кэш
        int cacheCount = 0;//количество обращение к кэшу
        int cacheHit = 0;//количество кэш попаданий

        int bytesOfA = M * K;
        int bytesOfB = K * N * 2;
        int bytesOfC = M * N * 4;

        int pa;
        int pb;
        int pc;

        int tact = 0; // количество тактов
        tact += (3 * 2); // инициализация 3 массивов

        pa = 0;
        tact += (1 * 2); // int8 *pa = a; инициализация

        pc = bytesOfA + bytesOfB;
        tact += (1 * 2);// int32 *pc = c; инициализация

        for (int y = 0; y < M; y++) {
            for (int x = 0; x < N; x++) {
                pb = bytesOfA;
                tact += (1 * 2);// int16 *pb = b; инициализация

                tact += 2;// int32 s = 0; инициализация
                for (int k = 0; k < K; k++) {
                    cacheTact = cache.changeCache(pa + k, 1);
                    tact++;//смена синхронизации
                    if (cacheTact < 2 * 100) {
                        cacheHit += 1;
                    }
                    cacheCount += 1;
                    tact += cacheTact;//pa[k] чтение из памяти

                    cacheTact = cache.changeCache(pb + x * 2, 2);
                    tact++;//смена синхронизации
                    if (cacheTact < 2 * 100) {
                        cacheHit += 1;
                    }
                    cacheCount += 1;
                    tact += cacheTact;//pb[x] чтение из памяти

                    tact += (5 * 2);//pa[k] * pb[x] умножение
                    tact += (1 * 2);//s + pa[k] * pb[x]; сложение
                    tact += (1 * 2);//s = s + pa[k] * pb[x]; инициализация

                    pb += N * 2;
                    tact += (1 * 2);//pb + N; сложение
                    tact += (1 * 2);//pb = pb + N; инициализация

                    tact += (1 * 2);//переход на новую итерацию цикла
                }

                cacheTact = cache.changeCache(pc + x * 4, 7);
                tact++;//смена синхронизации
                if (cacheTact < 2 * 100) {
                    cacheHit += 1;
                }
                tact += cacheTact;//pc[x] = s; запись в память
                cacheCount += 1;

                tact += (1 * 2);//переход на новую итерацию цикла
            }
            pa += K;
            tact += (1 * 2);//pa + K сложение
            tact += (1 * 2);//pa = pa + K инициализация

            pc += N * 4;
            tact += (1 * 2);//pc + N сложение
            tact += (1 * 2);//pc = pc + N инициализация

            tact += (1 * 2);//переход на новую итерацию цикла
        }
        tact += (1 * 2);//выход из функции

        System.out.println("Time: " + tact / 2);
        System.out.println("Percentage of hits: " + cacheHit + " / " + (cacheCount) + " = " + cacheHit * 100 / cacheCount + "%");
    }
}
