
`include "Cache.sv"
`include "CPU.sv"
`include "Memory.sv"
module testbench #(
    parameter CACHE_LINE_SIZE = 16,
    parameter CACHE_TAZ_SIZE = 10,
    parameter CACHE_SET_SIZE = 5,
    parameter CACHE_OFSET_SIZE = 4,
    parameter CACHE_WAY = 2,
    parameter CACHE_SETS_COUNT = 32,
    parameter CACHE_LINE_COUNT = 64
);
    reg clk = 0;
    reg M_DUMP;
    reg C_DUMP;
    reg RESET;
    
    wire[CACHE_TAZ_SIZE+CACHE_SET_SIZE-1:0] A1;
    wire[15:0] D1;
    wire[3:0] C1;
    
    reg[CACHE_TAZ_SIZE + CACHE_SET_SIZE - 1:0] A2;
    wire[15:0] D2;
    wire[1:0] C2;
    CPU cpu(clk, A1, D1, C1, C_DUMP, M_DUMP, RESET);
    Cache cache(clk, A1, D1, C1, A2, D2, C2, C_DUMP, RESET);
    Memory memory(clk, A2, D2, C2, M_DUMP, RESET);
    int count;

    initial begin
        count = 0;
        while(count <= 15000000) begin // изменение синхронизации
            count += 1;
            clk = 1 - clk;
            #1;
        end
    end
endmodule