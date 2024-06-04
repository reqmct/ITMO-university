
//Запись кэш линии в память
`define writeToMemory(cacheLine) \ //кэш линия которую записываем
    A2 = (tag << CACHE_SET_SIZE) + set; \ // адрес куда пишем
    _C2 = 3; \ // команда записи
    for (int i = CACHE_LINE_SIZE - 2; i >= 0; i -= 2) begin \ // запись в шину D2
        _D2 =  (cacheLine[i] << 8) + cacheLine[i + 1]; \
        #(1 * 2); \
    end \
    _C2 = 0; \
    _D2 = 'z; \
    while(C2 != 1) begin \ // ожидание ответа от памяти 
        #(1 * 2); \
    end \
    #(1 * 2);


//Чтение кэш линии из памяти
`define readFromMemory(cacheLine) \ // кэш линия, куда нужно читать
    A2 = (tag << CACHE_SET_SIZE) + set; \ // адрес куда пишем
    _C2 = 2; \ // команда чтения
    #(1 * 2); \
    _C2 = 0; \
    while (C2 != 1) begin \ // ожидание ответа от памяти
        #(1 * 2); \
    end \
    for (int i = CACHE_LINE_SIZE - 2; i >= 0; i -= 2) begin \ //чтение в кэш линию
        cacheLine[i] = (D2 >> 8); \
        cacheLine[i + 1] = D2 % (1 << 8); \ 
        #(1 * 2); \
    end

module Cache #(
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
    
    input wire[CACHE_TAZ_SIZE+CACHE_SET_SIZE-1:0] A1,
    inout wire[15:0] D1,
    inout wire[3:0] C1,  
    
    output reg[CACHE_TAZ_SIZE + CACHE_SET_SIZE - 1:0] A2,
    inout wire[15:0] D2,
    inout wire[1:0] C2, 
    
    input wire C_DUMP,
    input wire RESET
);

    reg[15:0] _D1;
    reg[3:0] _C1;
    reg[15:0] _D2;
    reg[1:0] _C2;

    reg[3:0] C1_buffer;// буффер для сохранения C1

    assign D1 = _D1;
    assign(supply1, strong0) C1 = _C1;
    assign D2 = _D2;
    assign(supply1, strong0) C2 = _C2;

    reg valid[0:CACHE_SETS_COUNT-1][0:CACHE_WAY-1]; // битики valid
    reg dirty[0:CACHE_SETS_COUNT-1][0:CACHE_WAY-1];// битики dirty
    reg[CACHE_TAZ_SIZE-1:0] cacheTag[0:CACHE_SETS_COUNT-1][0:CACHE_WAY-1];// тэги кэш линий
    reg[7:0] cache[0:CACHE_SETS_COUNT-1][0:CACHE_WAY-1][0:CACHE_LINE_SIZE-1]; // полезная информация

    reg LRU[0:CACHE_SETS_COUNT-1][0:CACHE_WAY-1]; //счетчик LRU

    //разделние адреса A1
    reg[CACHE_TAZ_SIZE-1:0] tag;// tag из адреса
    reg[CACHE_SET_SIZE-1:0] set;// set из адреса
    reg[CACHE_OFSET_SIZE-1:0] offset;// ofset из адреса

    int cacheHitIndex;//индекс кэш попадания
    reg[7:0] cpuData[0:3];//данные, пришедшие по D1 из CPU

    initial begin
        _D1 = 'z;
        _C1 = 0;// NOP
        _D2 = 'z;
        _C2 = 0;// NOP
        for (int i = 0; i < CACHE_SETS_COUNT; i += 1) begin // инициализация кэша
            for (int j = 0; j < CACHE_WAY; j += 1) begin 
                valid[i][j] = 0;
            end
        end
    end

    always @(negedge clk) begin
        if (1 <= C1 && C1 <= 3) begin //чтение
            cacheHitIndex = -1; 
            C1_buffer = C1;// сохранение C1

            tag = A1 >> CACHE_SET_SIZE;
            set = A1 % (1 << CACHE_SET_SIZE);
            #(1 * 2);
            offset = A1;

            for (int i = 0; i < CACHE_WAY; i += 1) begin
                if (cacheTag[set][i] == tag && valid[set][i] == 1) begin// кэш попадание
                    cacheHitIndex = i;
                end
            end
            if (cacheHitIndex == -1) begin // кэш промах
                #(3 * 2);// задержка при кэш промахе
                for (int i = 0; i < CACHE_WAY; i += 1) begin
                    if (valid[set][i] == 0 || LRU[set][i] == 0) begin // LRU вытеснение
                        cacheHitIndex = i;
                    end
                end
                
                if (valid[set][cacheHitIndex] == 1 && dirty[set][cacheHitIndex] == 1) begin // линия занята, ее нужно записать в память
                    `writeToMemory(cache[set][cacheHitIndex])
                end

                dirty[set][cacheHitIndex] = 0;
                valid[set][cacheHitIndex] = 1;
                `readFromMemory(cache[set][cacheHitIndex])//чтение кэш линии из памяти
            end
            else begin
                #(5 * 2);// задержка при кэш попадании
            end

            LRU[set][cacheHitIndex] = 1;
            LRU[set][1 - cacheHitIndex] = 0;
            cacheTag[set][cacheHitIndex] = tag;

            
            // Отправка в CPU
            if (C1_buffer == 1) begin
                _C1 = 7;//ответ
                _D1 = cache[set][cacheHitIndex][offset];
                #(1 * 2);
                _C1 = 0;
                _D1 = 'z;
            end

            if (C1_buffer == 2) begin
                _C1 = 7;//ответ
                _D1 = (cache[set][cacheHitIndex][offset] << 8) + cache[set][cacheHitIndex][offset + 1];
                #(1 * 2);
                _C1 = 0;
                _D1 = 'z;
            end

            if (C1_buffer == 3) begin
                _C1 = 7;//ответ
                _D1 = (cache[set][cacheHitIndex][offset + 2] << 8) + cache[set][cacheHitIndex][offset + 3];
                #(1 * 2);// передаем 32 бита за 2 такта
                _D1 = (cache[set][cacheHitIndex][offset] << 8) + cache[set][cacheHitIndex][offset + 1];
                #(1 * 2);
                _C1 = 0;
                _D1 = 'z;
            end
        end

        else if (5 <= C1 && C1 <= 7) begin//запись
            C1_buffer = C1;
            cacheHitIndex = -1;
            //чтение data
            if (C1_buffer == 5) begin
                cpuData[0] = D1;
            end

            if (C1_buffer == 6) begin
                cpuData[0] = (D1 >> 8);
                cpuData[1] = D1 % (1 << 8);
            end

            if (C1_buffer == 7) begin
                cpuData[2] = (D1 >> 8);
                cpuData[3] = D1 % (1 << 8);
            end

            tag = A1 >> CACHE_SET_SIZE;
            set = A1 % (1 << CACHE_SET_SIZE);
            #(1 * 2);
            offset = A1;
            if (C1_buffer == 7) begin// чтение оставшихся 16 бит
                cpuData[0] = (D1 >> 8);
                cpuData[1] = D1 % (1 << 8);
            end

            for (int i = 0; i < CACHE_WAY; i += 1) begin
                if (cacheTag[set][i] == tag && valid[set][i] == 1) begin // кэш попадание
                    cacheHitIndex = i;
                end
            end
            if (cacheHitIndex == -1) begin // кэш промах
                #(3 * 2);// задержка при кэш промахе
                for (int i = 0; i < CACHE_WAY; i += 1) begin
                    if (valid[set][i] == 0 || LRU[set][i] == 0) begin // LRU вытеснение
                        cacheHitIndex = i;
                    end
                end
                
                if (valid[set][cacheHitIndex] == 1 && dirty[set][cacheHitIndex] == 1) begin // линия занята, ее нужно записать в память
                    `writeToMemory(cache[set][cacheHitIndex])
                end

                dirty[set][cacheHitIndex] = 1;
                valid[set][cacheHitIndex] = 1;
                `readFromMemory(cache[set][cacheHitIndex])// чтение кэш линии из памяти
            end
            else begin
                #(5 * 2);//зарержка при кэш попадании
            end
            LRU[set][cacheHitIndex] = 1;
            LRU[set][1 - cacheHitIndex] = 0;
            cacheTag[set][cacheHitIndex] = tag;

            //внесение изменений в кэш линию
            if (C1_buffer == 5) begin
                cache[set][cacheHitIndex][offset] = cpuData[0];
            end
            if (C1_buffer == 6) begin
                cache[set][cacheHitIndex][offset] = cpuData[0];
                cache[set][cacheHitIndex][offset + 1] = cpuData[1];
            end
            if (C1_buffer == 7) begin
                cache[set][cacheHitIndex][offset] = cpuData[0];
                cache[set][cacheHitIndex][offset + 1] = cpuData[1];
                cache[set][cacheHitIndex][offset + 2] = cpuData[2];
                cache[set][cacheHitIndex][offset + 3] = cpuData[3];
            end

            _C1 = 7;// ответ
            #(1 * 2);
            _C1 = 0;
        end
        else if (C1 == 4) begin // инвалидация
            cacheHitIndex = -1;

            tag = A1 >> CACHE_SET_SIZE;
            set = A1 % (1 << CACHE_SET_SIZE);
            #(1 * 2);
            offset = A1;

            for (int i = 0; i < CACHE_WAY; i += 1) begin //ищем нужную кэш линию
                if (cacheTag[set][i] == tag && valid[set][i] == 1) begin
                    cacheHitIndex = i;
                end
            end

            if (cacheHitIndex != -1) begin//если нашли - то делаем ее невалидной
                valid[set][cacheHitIndex] = 0;
                LRU[set][1 - cacheHitIndex] = 1;
                #(5 * 2);//задержка при кэш попадании
            end
            else begin
                #(3 * 2);//задержка при кэш промахе
            end
            _C1 = 7;
            #(1 * 2);
            _C1 = 0;
        end
    end


    always @(posedge C_DUMP) begin //вывод полезных данных
        for (int i = 0; i < CACHE_LINE_COUNT; i += 1) begin 
            $display("Line = %d:", i);
            for (int j = 0; j < CACHE_LINE_SIZE; j += 1) begin 
                $write("%d ", cache[i/2][i%2][j]);
            end
            $display();
        end
    end

    always @(posedge RESET) begin //сброс данных к начальному состоянию
        for (int i = 0; i < CACHE_SETS_COUNT; i += 1) begin
            for (int j = 0; j < CACHE_WAY; j += 1) begin
                valid[i][j] = 0;
            end
        end
    end

endmodule