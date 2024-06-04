#include "calc.hpp"

#include <cctype>
#include <cmath>
#include <iostream>

namespace {

const std::size_t max_decimal_digits = 10;

enum class Op
{
    ERR,
    SET,
    ADD,
    SUB,
    MUL,
    DIV,
    REM,
    NEG,
    POW,
    SQRT
};

std::size_t arity(const Op op)
{
    switch (op) {
    // error
    case Op::ERR: return 0;
    // unary
    case Op::NEG: return 1;
    case Op::SQRT: return 1;
    // binary
    case Op::SET: return 2;
    case Op::ADD: return 2;
    case Op::SUB: return 2;
    case Op::MUL: return 2;
    case Op::DIV: return 2;
    case Op::REM: return 2;
    case Op::POW: return 2;
    }
    return 0;
}

Op parse_op(std::string_view line, std::size_t & i)
{
    const auto rollback = [&i, &line](const std::size_t n) {
        i -= n;
        std::cerr << "Unknown operation " << line << std::endl;
        return Op::ERR;
    };
    switch (line[i++]) {
    case '0':
    case '1':
    case '2':
    case '3':
    case '4':
    case '5':
    case '6':
    case '7':
    case '8':
    case '9':
        --i; // a first digit is a part of op's argument
        return Op::SET;
    case '+':
        return Op::ADD;
    case '-':
        return Op::SUB;
    case '*':
        return Op::MUL;
    case '/':
        return Op::DIV;
    case '%':
        return Op::REM;
    case '_':
        return Op::NEG;
    case '^':
        return Op::POW;
    case 'S':
        switch (line[i++]) {
        case 'Q':
            switch (line[i++]) {
            case 'R':
                switch (line[i++]) {
                case 'T':
                    return Op::SQRT;
                default:
                    return rollback(4);
                }
            default:
                return rollback(3);
            }
        default:
            return rollback(2);
        }
    default:
        return rollback(1);
    }
}

std::size_t skip_ws(const std::string & line, std::size_t i)
{
    while (i < line.size() && std::isspace(line[i])) {
        ++i;
    }
    return i;
}

int character_to_int(char c)
{
    if ('a' <= c && c <= 'f') {
        return c - 'a' + 10;
    }
    else if ('A' <= c && c <= 'F') {
        return c - 'A' + 10;
    }
    return c - '0';
}

double parse_arg(const std::string & line, std::size_t & i)
{
    double res = 0;
    std::size_t count = 0;
    bool isCorrectNumber = true;
    bool integer = true;
    double fraction = 1;
    int multiplier = 10;
    if (i == line.size()) {
        return res;
    }
    if (i + 1 < line.size() && line[i] == '0') {
        switch (line[i + 1]) {
        case 'b':
        case 'B':
            multiplier = 2;
            break;
        case '0':
        case '1':
        case '2':
        case '3':
        case '4':
        case '5':
        case '6':
        case '7':
        case '8':
        case '9':
            multiplier = 8;
            break;
        case 'x':
        case 'X':
            multiplier = 16;
            break;
        }
        if (multiplier == 8) {
            i += 1;
        }
        else if (multiplier != 10) {
            i += 2;
        }
    }
    if (!(i != line.size() && 0 <= character_to_int(line[i]) && character_to_int(line[i]) < 16)) {
        isCorrectNumber = false;
    }
    while (isCorrectNumber && i < line.size() && count < max_decimal_digits) {
        int current;
        switch (line[i]) {
        case 'a':
        case 'b':
        case 'c':
        case 'd':
        case 'e':
        case 'f':
        case 'A':
        case 'B':
        case 'C':
        case 'D':
        case 'E':
        case 'F':
        case '0':
        case '1':
        case '2':
        case '3':
        case '4':
        case '5':
        case '6':
        case '7':
        case '8':
        case '9':
            current = character_to_int(line[i]);
            if (current >= multiplier) {
                isCorrectNumber = false;
                break;
            }
            if (integer) {
                res *= multiplier;
                res += current;
            }
            else {
                fraction /= multiplier;
                res += current * fraction;
            }
            ++i;
            ++count;
            break;
        case '.':
            if (!integer) {
                isCorrectNumber = false;
                break;
            }
            integer = false;
            ++i;
            break;
        default:
            isCorrectNumber = false;
            break;
        }
    }
    if (!isCorrectNumber) {
        if (i == line.size()) {
            i--;
        }
        std::cerr << "Argument parsing error at " << i << ": '" << line.substr(i) << "'" << std::endl;
    }
    else if (i < line.size()) {
        std::cerr << "Argument isn't fully parsed, suffix left: '" << line.substr(i) << "'" << std::endl;
    }
    return res;
}

double unary(const double current, const Op op)
{
    switch (op) {
    case Op::NEG:
        return -current;
    case Op::SQRT:
        if (current > 0) {
            return std::sqrt(current);
        }
        else {
            std::cerr << "Bad argument for SQRT: " << current << std::endl;
            [[fallthrough]];
        }
    default:
        return current;
    }
}

double binary(const Op op, const double left, const double right)
{
    switch (op) {
    case Op::SET:
        return right;
    case Op::ADD:
        return left + right;
    case Op::SUB:
        return left - right;
    case Op::MUL:
        return left * right;
    case Op::DIV:
        if (right != 0) {
            return left / right;
        }
        else {
            std::cerr << "Bad right argument for division: " << right << std::endl;
            return left;
        }
    case Op::REM:
        if (right != 0) {
            return std::fmod(left, right);
        }
        else {
            std::cerr << "Bad right argument for remainder: " << right << std::endl;
            return left;
        }
    case Op::POW:
        return std::pow(left, right);
    default:
        return left;
    }
}

} // anonymous namespace

double process_line(double currentValue,
                    std::string const & line)
{
    std::size_t i = 0;
    const auto op = parse_op(line, i);
    switch (arity(op)) {
    case 2: {
        i = skip_ws(line, i);
        const auto old_i = i;
        const auto arg = parse_arg(line, i);
        if (i == old_i) {
            std::cerr << "No argument for a binary operation" << std::endl;
            break;
        }
        else if (i < line.size()) {
            break;
        }
        return binary(op, currentValue, arg);
    }
    case 1: {
        if (i < line.size()) {
            std::cerr << "Unexpected suffix for a unary operation: '" << line.substr(i) << "'" << std::endl;
            break;
        }
        return unary(currentValue, op);
    }
    default: break;
    }
    return currentValue;
}

double process_line(double currentValue, bool &, const std::string & line)
{
    return process_line(currentValue, line);
}