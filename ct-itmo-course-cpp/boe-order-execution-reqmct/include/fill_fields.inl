#if !defined(RETURN_VALUE) || !defined(NAMESPACE) || !defined(VARIABLES_LOCATION) || !defined(OPTIONAL_VARIABLES_LOCATION)
#error You need to define RETURN_VALUE, NAMESPACE, VARIABLES_LOCATION  and OPTIONAL_VARIABLES_LOCATION macro
#else
#define FIELD(name, _, __, convert) \
    RETURN_VALUE.name = convert(NAMESPACE::decode_field_##name(message));
#define VAR_FIELD(name, _, __, ___, convert) \
    RETURN_VALUE.name = convert(NAMESPACE::decode_field_##name(message));
#include VARIABLES_LOCATION
#include OPTIONAL_VARIABLES_LOCATION
#undef FIELD
#undef VAR_FIELD
#undef NAMESPACE
#undef RETURN_VALUE
#undef VARIABLES_LOCATION
#undef OPTIONAL_VARIABLES_LOCATION
#endif
