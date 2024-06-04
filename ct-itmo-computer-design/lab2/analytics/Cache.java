public class Cache {
    final static int CACHE_SETS_COUNT = 32;
    Block blocks[] = new Block[CACHE_SETS_COUNT];

    Cache() {
        for (int i = 0; i < CACHE_SETS_COUNT; i++) {
            blocks[i] = new Block();
        }
    }

    int changeCache(int A1, int C1) {
        int tact = 0;
        tact++;//смена синхронизации
        int tag = (A1 >> 9); // формирование tag
        int set = (A1 >> 4) % (1 << 5);// формирование set
        tact += blocks[set].changeBlock(tag, C1);// внесение изменений в блок
        if (1 <= C1 && C1 <= 3) { //чтение
            if (C1 == 1 || C1 == 2) {
                tact += (1 * 2);//передача данных из Cache в CPU
            }
            if (C1 == 3) {
                tact += (2 * 2);//передача данных из Cache в CPU
            }
        }
        if (5 <= C1 && C1 <= 7) {//запись
            tact += (1 * 2);//передача данных из Cache в CPU
        }
        return tact;
    }
}
