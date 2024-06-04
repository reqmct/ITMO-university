public class Block {
    final static int CACHE_WAY = 2;
    CacheLine lines[] = new CacheLine[CACHE_WAY]; // блок кэш линий
    Memory memory = new Memory(); // наша память (т.к там одна функция можем делать сколько угодно экземпляров)

    Block() {
        for (int i = 0; i < CACHE_WAY; i++) {
            lines[i] = new CacheLine(); // наши линии
        }
    }

    int changeBlock(int tag, int C1) {
        int tact = 0; // количество тактов
        if (lines[0].valid == true && lines[0].tag == tag) {// попали в кэш линию
            lines[0].lru = 1;
            lines[1].lru = 0;
            return 6 * 2; // 6 тактов на попадание
        }
        if (lines[1].valid == true && lines[1].tag == tag) {// попали в кэш линию
            lines[0].lru = 0;
            lines[1].lru = 1;
            return 6 * 2;// 6 тактов на попадание
        }
        tact += (4 * 2);// кэш промах
        int ind;
        if (lines[1].lru == 0) {// политики вытеснения LRU
            ind = 1;
            lines[0].lru = 0;
        } else {
            ind = 0;
            lines[1].lru = 0;
        }
        if (lines[ind].valid == true && lines[ind].dirty == true) { // проверка нужно ли записать строчку в память
            tact += memory.changeMemory(3);
            tact++;//смена синхронизации
        }

        if (1 <= C1 && C1 <= 3) { // чтение
            lines[ind] = new CacheLine(tag, true, false, 1);
            tact += memory.changeMemory(2);
            tact++;//смена синхронизации
        }
        if (5 <= C1 && C1 <= 7){
            lines[ind] = new CacheLine(tag, true, true, 1);
            tact += memory.changeMemory(2);
            tact++;//смена синхронизации
        }
        return tact;
    }
}
