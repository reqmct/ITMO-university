public class Memory {

    int changeMemory(int C2) {
        int tact = 0; // время отклика
        tact++;//смена синхронизации
        tact += (100 * 2); // временная задержка памяти
        if (C2 == 2) { // читаем
            tact += (8 * 2); // передача от Memory к Cache
        }
        if (C2 == 3) { // пишем
            tact += (1 * 2);// передача данных от Memory к Cache
        }
        return tact;
    }
}
