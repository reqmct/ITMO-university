#include <fields.h>

inline std::vector<unsigned char> get_optional_fields_order_execution()
{
    std::vector<unsigned char> bytes(8);
#define FIELD(name, _, __, ___) \
    fields_order_execution::encode_field_##name(bytes);
#define VAR_FIELD(name, _, __, ___, ____) \
    fields_order_execution::encode_field_##name(bytes);
#include "optional_fields_order_execution.inl"
#undef FIELD
#undef VAR_FIELD
    return bytes;
}

inline std::vector<unsigned char> get_optional_fields_order_restatement()
{
    std::vector<unsigned char> bytes(6);
#define FIELD(name, _, __, ___) \
    fields_order_restatement::encode_field_##name(bytes);
#define VAR_FIELD(name, _, __, ___, ____) \
    fields_order_restatement::encode_field_##name(bytes);
#include "optional_fields_order_restatement.inl"
#undef FIELD
#undef VAR_FIELD
    return bytes;
}
