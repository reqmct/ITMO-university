#pragma once

#include <cstdint>
#include <string>
#include <vector>

inline unsigned char * encode(unsigned char * start, const uint8_t value)
{
    *start = value;
    return start + 1;
}

inline unsigned char * encode(unsigned char * start, const uint16_t value)
{
    *start++ = static_cast<unsigned char>(value & 0xFF);
    *start++ = static_cast<unsigned char>((value >> 8) & 0xFF);
    return start;
}

inline unsigned char * encode(unsigned char * start, const uint32_t value)
{
    *start++ = static_cast<unsigned char>(value & 0xFF);
    *start++ = static_cast<unsigned char>((value >> 8) & 0xFF);
    *start++ = static_cast<unsigned char>((value >> 16) & 0xFF);
    *start++ = static_cast<unsigned char>((value >> 24) & 0xFF);
    return start;
}

inline unsigned char * encode(unsigned char * start, int64_t value)
{
    for (int i = 0; i < 8; ++i) {
        *start++ = static_cast<unsigned char>(value & 0xFF);
        value >>= 8;
    }
    return start;
}

inline unsigned char * encode(unsigned char * start, const std::string & str, const size_t field_size)
{
    size_t i = 0;
    while (i < str.size() && i < field_size) {
        *start++ = str[i];
        ++i;
    }
    while (i < field_size) {
        *start++ = '\0';
        ++i;
    }
    return start;
}

inline unsigned long long decode_binary(const std::vector<unsigned char> & bytes, const int start, const int size)
{
    unsigned long long result = 0;
    for (int i = start; i - start < size; i++) {
        result = result + bytes[i] * (1ULL << ((i - start) * 8));
    }
    return result;
}
