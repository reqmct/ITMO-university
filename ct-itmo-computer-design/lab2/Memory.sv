module Memory #(
    parameter _SEED = 225526,
    parameter MEM_SIZE = 512 * 1024,
    parameter CACHE_LINE_SIZE = 16,
    parameter CACHE_TAZ_SIZE = 10,
    parameter CACHE_SET_SIZE = 5
) 
(
    input wire clk,
    input wire[CACHE_TAZ_SIZE + CACHE_SET_SIZE - 1:0] A2,
    inout wire[15:0] D2,
    inout wire[1:0] C2,
    
    input wire M_DUMP,
    input wire RESET
);

    integer SEED = _SEED;

    reg[CACHE_TAZ_SIZE + CACHE_SET_SIZE - 1:0] A2_Buffer;// буффер для сохранения A2
    reg[15:0] _D2;
    reg[1:0] _C2;

    assign D2 = _D2;
    assign(supply1, strong0) C2 = _C2;

    reg[7:0] memory[0:MEM_SIZE/CACHE_LINE_SIZE-1][0:CACHE_LINE_SIZE-1]; // data

    initial begin
        _C2 = 0;// по C2 отправляем NOP
        _D2 = 'z;
        for (int i = 0; i < MEM_SIZE / CACHE_LINE_SIZE; i += 1) begin //инициализация
            for (int j = 0; j < CACHE_LINE_SIZE; j += 1) begin 
                memory[i][j] = $random(SEED)>>16;
            end
        end

    end


    always @(posedge clk) begin 
        A2_Buffer = A2; // сохранение значения A2
        if(C2 == 2) begin // чтение
            #(100 * 2); // задержка памяти
            _C2 = 1;// ответ
            for (int i = CACHE_LINE_SIZE - 2; i >= 0; i -= 2) begin // little endian
                _D2 =  (memory[A2_Buffer][i] << 8) + memory[A2_Buffer][i + 1];
                #(1 * 2);
            end
            _C2 = 0;
            _D2 = 'z;
        end
        if(C2 == 3) begin // запись
            for (int i = CACHE_LINE_SIZE - 2; i >= 0; i -= 2) begin // little endian
                memory[A2_Buffer][i] = (D2 >> 8);
                memory[A2_Buffer][i + 1] = D2 % (1 << 8); 
                #(1 * 2);
            end
            #(92 * 2);// задержка 92 т.к в for тратит 8 тактов 100 - 8 = 92
            _C2 = 1;// ответ
            #(1 * 2);
            _C2 = 0;
        end
    end


    always @(posedge M_DUMP) begin
        for (int i = 0; i < MEM_SIZE / CACHE_LINE_SIZE; i += 1) begin // выгрузка памяти
            for (int j = 0; j < CACHE_LINE_SIZE; j += 1) begin 
                $write("%d ", memory[i][j]);
            end
            $display();
        end
    end

    always @(posedge RESET) begin
        for (int i = 0; i < MEM_SIZE / CACHE_LINE_SIZE; i += 1) begin // сброс к начальным значениям
            for (int j = 0; j < CACHE_LINE_SIZE; j += 1) begin 
                memory[i][j] = $random(SEED)>>16;
            end
        end
    end

endmodule