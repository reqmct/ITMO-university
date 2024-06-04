#if !defined(FIELD) || !defined(VAR_FIELD)
#error You need to define FIELD and VAR_FIELD macro
#else

VAR_FIELD(cl_ord_id, text, 18, 20, )
FIELD(exec_id, binary8, 38, base36)
FIELD(filled_volume, binary4, 46, )
FIELD(price, price, 50, )
FIELD(active_volume, binary4, 58, )
FIELD(liquidity_indicator, alphanumeric, 62, convert_liquidity_indicator)

#endif
