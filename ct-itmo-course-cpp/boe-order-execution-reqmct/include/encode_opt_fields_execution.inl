#if !defined(ENCODE_FIELD)
#error You need to define ENCODE_FIELD macro
#else

ENCODE_FIELD(symbol, 1, 1)
ENCODE_FIELD(last_mkt, 6, 128)
ENCODE_FIELD(fee_code, 7, 1)

#endif
