#ifndef ACP_POOL_HPP
#define ACP_POOL_HPP

#include <cstddef>
#include <initializer_list>
#include <map>
#include <new>
#include <vector>

class PoolAllocator {
    static constexpr std::size_t npos = static_cast<std::size_t>(-1);
    using pos_type = std::pair<std::size_t, std::size_t>;
public:
    PoolAllocator(std::size_t const /*block_size*/, std::initializer_list<std::size_t> /*sizes*/);

    void* allocate(std::size_t const /*n*/);

    void deallocate(void const* /*ptr*/);

private:
    pos_type find_empty_call(std::size_t size);
    std::vector<std::vector<std::byte>> data;
    std::vector<std::vector<bool>> used;
    std::map<std::size_t, std::size_t> convert_size;
};

#endif  // ACP_POOL_HPP
