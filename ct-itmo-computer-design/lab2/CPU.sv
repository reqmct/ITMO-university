`define get(adr, value, bitSize) \ // считывание из кэша
    if (bitSize == 8) \ //отправка нужной команды
        _C1 = 1; \
    if (bitSize == 16) \
        _C1 = 2; \
    if (bitSize == 32) \
        _C1 = 3; \           
    A1 = adr >> CACHE_OFSET_SIZE; \
    #(1 * 2); \
    A1 = adr % (1 << CACHE_OFSET_SIZE); \
    #(1 * 2); \
    _C1 = 0; \
    while(C1 != 7) begin \ // ожидание ответа от кэша
        #(1 * 2); \
    end \
    value = D1; \ // запись data
    #(1 * 2); \
    if(bitSize == 32) begin \ // продожение записи для 32 бит
        value = (D1 << 16) + value; \
        #(1 * 2); \
    end \
    _C1 = 0;


`define set(adr, value, bitSize) \ //запись в кэш
    if (bitSize == 8) \ //отправка нужной команды
        _C1 = 5; \
    if (bitSize == 16) \
        _C1 = 6; \
    if (bitSize == 32) \
        _C1 = 7; \
    _D1 = value % (1<<16); \ // посылаем данные по шине D1
    A1 = adr >> CACHE_OFSET_SIZE; \
    #(1 * 2); \
    A1 = adr % (1 << CACHE_OFSET_SIZE); \
    _D1 = (value >> 16); \ // посылаем оставшиеся данные 
    #(1 * 2); \
    _C1 = 0; \
    while(C1 != 7) begin \ // ожидание ответа от кэша
        #(1 * 2); \
    end \
    #(1 * 2); \
    _D1 = 'z; \
    _C1 = 0;


`define M 64
`define N 60
`define K 32

module CPU #(
    parameter CACHE_LINE_SIZE = 16,
    parameter CACHE_TAZ_SIZE = 10,
    parameter CACHE_SET_SIZE = 5,
    parameter CACHE_OFSET_SIZE = 4,
    parameter CACHE_WAY = 2,
    parameter CACHE_SETS_COUNT = 32,
    parameter CACHE_LINE_COUNT = 64
)
(
    input wire clk,
    
    output reg[CACHE_TAZ_SIZE+CACHE_SET_SIZE-1:0] A1,
    inout wire[15:0] D1,
    inout wire[3:0] C1,

    output reg C_DUMP,
    output reg M_DUMP,
    output reg RESET
);
    reg[15:0] _D1;
    reg[3:0] _C1;


    assign D1 = _D1;
    assign(supply1, strong0) C1 = _C1;
    
    //массивы из условия
    reg[7:0] a[0:`M-1][0:`K-1];
    reg[15:0] b[0:`K-1][0:`N-1];
    reg[31:0] c[0:`M-1][0:`N-1];

    int bytesOfA = `M * `K; //размер массива a
    int bytesOfB = `K * `N * 2;//размер массива b
    int bytesOfC = `M * `N * 4;//размер массива C

    int pa;
    int pb;
    int pc;

    int count = 0;

    reg [31:0] s;
    reg [7:0] getPa;//считывание pa из памяти
    reg [15:0] getPb;//считывание pb из памяти
    int t;//время

    int cacheHit;//число кэш попаданий
    int cacheCount;//число кэш обращений


    initial begin
        _D1 = 'z;
        _C1 = 0;
    end

    always @(posedge clk) begin
        cacheHit = 0;
        cacheCount = 0;

        #(3 * 2); // инициализация 3 массивов
        pa = 0;
        #(1 * 2);// int8 *pa = a; инициализация

        pc = bytesOfA + bytesOfB;
        #(1 * 2);// int32 *pc = c; инициализация

        for (int y = 0; y < `M; y += 1) begin
            for (int x = 0; x < `N; x += 1) begin
                pb = bytesOfA;
                #(1 * 2);// int16 *pb = b; инициализация

                s = 0;
                #(1 * 2);// int32 s = 0; инициализация
                for (int k = 0; k < `K; k += 1) begin
                    t = $time;//запоминаем на каком такте начинается чтение
                    `get(pa + k, getPa, 8) //pa[k] чтение из памяти
                    if ($time - t < 2 * 100)
                        cacheHit += 1;
                    cacheCount += 1;
                    t = $time;//запоминаем на каком такте начинается чтение
                    `get(pb + x * 2, getPb, 16) //pb[x] чтение из памяти
                    if ($time - t < 2 * 100)
                        cacheHit += 1;
                    cacheCount += 1;

                    s += getPa * getPb;

                    #(5 * 2);//pa[k] * pb[x] умножение
                    #(1 *2);//s + pa[k] * pb[x]; сложение
                    #(1 * 2);//s = s + pa[k] * pb[x]; инициализация

                    pb += `N * 2;
                    #(1 * 2);//pb + N; сложение
                    #(1 * 2);//pb = pb + N; инициализация

                    #2;//переход на новую итерацию цикла
                end

                t = $time;//запоминаем на каком такте начинается запись
                `set(pc + x * 4, s, 32)//pc[x] = s; запись в память
                if ($time - t < 100)
                    cacheHit += 1;
                cacheCount += 1;

                #(1 * 2);//переход на новую итерацию цикла
            end
            pa += `K;
            #(1 * 2);//pa + K сложение
            #(1 * 2);//pa = pa + K инициализация

            pc += `N * 4;
            #(1 * 2);//pc + N сложение
            #(1 * 2);//pc = pc + N инициализация

            #(1 * 2);//переход на новую итерацию цикла
        end
        #(1 * 2);//выход из функции
        $display("Time: %d",$time / 2);
        $display("Percentage of hits: %d/%d %d\%",cacheHit, cacheCount, cacheHit * 100 / cacheCount);
        $finish;
    end

endmodule