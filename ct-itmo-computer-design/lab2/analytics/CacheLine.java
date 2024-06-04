

public class CacheLine {
    public boolean valid; // пуста ли строка
    public boolean dirty; // выгрузили ли в память
    public int tag;// tag для памяти
    public int lru; // порядок вытеснения

    public CacheLine(){
        valid = false;
    }
    public CacheLine(int tag, boolean valid, boolean dirty, int lru){
        this.tag = tag;
        this.lru = lru;
        this.valid = valid;
        this.dirty = dirty;
    }

}
