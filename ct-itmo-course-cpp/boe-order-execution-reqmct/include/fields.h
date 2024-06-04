#pragma once

#include "codec.h"

#include <cmath>

/*
 * Fields
 *  Account : Text(16)
 *  Capacity : Alpha(1)
 *  ClOrdID : Text(20)
 *  MaxFloor : Binary(4)
 *  OrderQty : Binary(4)
 *  OrdType : Alphanum(1)
 *  Price : BinaryPrice(8)
 *  Side : Alphanum(1)
 *  Symbol : Alphanum(8)
 *  TimeInForce : Alphanum(1)
 */
inline unsigned char * encode_text(unsigned char * start, const std::string & str, const size_t field_size)
{
    return encode(start, str, field_size);
}

inline unsigned char * encode_char(unsigned char * start, const char ch)
{
    return encode(start, static_cast<uint8_t>(ch));
}

inline unsigned char * encode_binary4(unsigned char * start, const uint32_t value)
{
    return encode(start, value);
}

inline unsigned char * encode_price(unsigned char * start, const double value)
{
    const double order = 10000;
    const double epsilon = 1e-5;
    // beware: no precision loss check
    return encode(start, static_cast<int64_t>(value * order + std::copysign(epsilon, value)));
}

inline std::string_view delete_empty_symbol(const std::string & str)
{
    std::string_view source = str;
    size_t size = source.size();
    while (size != 0) {
        if (source[size - 1] != '\0') {
            break;
        }
        size--;
    }
    return source.substr(0, size);
}

inline std::string decode_text(const std::vector<unsigned char> & bytes, const int start, const size_t size)
{
    std::string tmp = std::string{reinterpret_cast<const char *>(&bytes[start]), size};
    return std::string{delete_empty_symbol(tmp)};
}

inline unsigned long long decode_binary8(const std::vector<unsigned char> & bytes, const int start)
{
    return decode_binary(bytes, start, 8);
}

inline unsigned int decode_binary4(const std::vector<unsigned char> & bytes, const int start)
{
    return decode_binary(bytes, start, 4);
}

inline double decode_price(const std::vector<unsigned char> & bytes, const int start)
{
    return decode_binary(bytes, start, 8) / 10000.0;
}

inline unsigned char decode_alphanumeric(const std::vector<unsigned char> & bytes, const int start)
{
    return bytes[start];
}

inline constexpr size_t char_size = 1;
inline constexpr size_t binary4_size = 4;
inline constexpr size_t price_size = 8;

#define FIELD(name, protocol_type, ctype)                                                \
    inline unsigned char * encode_field_##name(unsigned char * start, const ctype value) \
    {                                                                                    \
        return encode_##protocol_type(start, value);                                     \
    }

#define VAR_FIELD(name, size)                                                                  \
    inline unsigned char * encode_field_##name(unsigned char * start, const std::string & str) \
    {                                                                                          \
        return encode_text(start, str, size);                                                  \
    }

#include "fields.inl"

#define FIELD(name, protocol_type, _) inline constexpr size_t name##_field_size = protocol_type##_size;
#define VAR_FIELD(name, size) inline constexpr size_t name##_field_size = size;

#include "fields.inl"

inline void set_opt_field_bit(unsigned char * bitfield_start, unsigned bitfield_num, unsigned bit)
{
    *(bitfield_start + bitfield_num - 1) |= bit;
}

#define FIELD(name, protocol_type, start, _)                                  \
    inline auto decode_field_##name(const std::vector<unsigned char> & bytes) \
    {                                                                         \
        return decode_##protocol_type(bytes, start);                          \
    }

#define VAR_FIELD(name, protocol_type, start, size, _)                        \
    inline auto decode_field_##name(const std::vector<unsigned char> & bytes) \
    {                                                                         \
        return decode_##protocol_type(bytes, start, size);                    \
    }

#define ENCODE_FIELD(name, byte_num, byte)                              \
    inline void encode_field_##name(std::vector<unsigned char> & bytes) \
    {                                                                   \
        bytes[byte_num] = byte;                                         \
    }

namespace fields_order_execution {
#include "encode_opt_fields_execution.inl"
#include "fields_order_execution.inl"
#include "optional_fields_order_execution.inl"
} // namespace fields_order_execution

namespace fields_order_restatement {
#include "encode_opt_fields_restatement.inl"
#include "fields_order_restatement.inl"
#include "optional_fields_order_restatement.inl"
} // namespace fields_order_restatement
#undef FIELD
#undef VAR_FIELD
#undef ENCODE_FIELD
