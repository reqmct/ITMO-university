#if !defined(FIELD) || !defined(VAR_FIELD)
#error You need to define FIELD and VAR_FIELD macro
#else

FIELD(active_volume, binary4, 49 + 6, )
FIELD(secondary_order_id, binary8, 49 + 6 + 4, base36)

#endif
