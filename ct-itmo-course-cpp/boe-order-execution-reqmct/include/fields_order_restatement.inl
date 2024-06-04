#if !defined(FIELD) || !defined(VAR_FIELD)
#error You need to define FIELD and VAR_FIELD macro
#else

VAR_FIELD(cl_ord_id, text, 18, 20, )
FIELD(reason, alphanumeric, 46, convert_reason)

#endif
