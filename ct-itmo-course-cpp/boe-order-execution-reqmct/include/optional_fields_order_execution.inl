#if !defined(FIELD) || !defined(VAR_FIELD)
#error You need to define FIELD and VAR_FIELD macro
#else

VAR_FIELD(symbol, text, 70 + 8, 8, )
VAR_FIELD(last_mkt, text, 70 + 8 + 8, 4, )
VAR_FIELD(fee_code, text, 70 + 8 + 8 + 4, 2, )

#endif
