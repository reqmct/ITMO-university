#include "requests.h"

inline char number_to_base36(const int number)
{
    if (number < 10) {
        return number + '0';
    }
    return number - 10 + 'A';
}

inline std::string base36(unsigned long long number)
{
    std::string result = "";
    while (number != 0) {
        result = result + number_to_base36(number % 36);
        number /= 36;
    }
    std::reverse(result.begin(), result.end());
    return result;
}

inline LiquidityIndicator convert_liquidity_indicator(const unsigned char c)
{
    switch (c) {
    case 'A':
        return LiquidityIndicator::Added;
    case 'R':
        return LiquidityIndicator::Removed;
    default:
        return LiquidityIndicator::Removed;
    }
}

inline RestatementReason convert_reason(const unsigned char c)
{
    switch (c) {
    case 'R':
        return RestatementReason::Reroute;
    case 'X':
        return RestatementReason::LockedInCross;
    case 'W':
        return RestatementReason::Wash;
    case 'L':
        return RestatementReason::Reload;
    case 'Q':
        return RestatementReason::LiquidityUpdated;
    default:
        return RestatementReason::Unknown;
    }
}
