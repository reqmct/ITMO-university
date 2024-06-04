#if !defined(ENCODE_FIELD)
#error You need to define ENCODE_FIELD macro
#else

ENCODE_FIELD(active_volume, 4, 2)
ENCODE_FIELD(secondary_order_id, 5, 1)

#endif
