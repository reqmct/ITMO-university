#include "acp/Pool.hpp"

#include <iostream>

PoolAllocator::PoolAllocator(std::size_t const block_size, std::initializer_list<std::size_t> sizes) {
    data.resize(sizes.size(), std::vector<std::byte>(block_size));
    used.resize(sizes.size());
    std::size_t i = 0;
    for (auto size : sizes) {
        used[i].resize(block_size / size);
        convert_size[size] = i;
        i++;
    }
}

PoolAllocator::pos_type PoolAllocator::find_empty_call(std::size_t size) {
    if (!convert_size.contains(size)) {
        return {npos, npos};
    }
    std::size_t i = convert_size[size];
    for (std::size_t j = 0; j < used[i].size(); j++) {
        if (!used[i][j]) {
            return {i, j};
        }
    }
    return {npos, npos};
}

void* PoolAllocator::allocate(std::size_t const size) {
    pos_type pos = find_empty_call(size);
    if (pos.first == npos || pos.second == npos) {
        throw std::bad_alloc{};
    }
    used[pos.first][pos.second] = true;
    return &data[pos.first][pos.second * size];
}

void PoolAllocator::deallocate(void const* ptr) {
    const auto byte_ptr = static_cast<const std::byte*>(ptr);
    for (auto element : convert_size) {
        std::size_t i    = element.second;
        std::size_t size = element.first;
        for (std::size_t j = 0; j < data[i].size(); j += size) {
            if (&data[i][j] == byte_ptr) {
                const std::size_t offset = (byte_ptr - &data[i][0]) / size;
                used[i][offset] = false;
                return;
            }
        }
    }
}
