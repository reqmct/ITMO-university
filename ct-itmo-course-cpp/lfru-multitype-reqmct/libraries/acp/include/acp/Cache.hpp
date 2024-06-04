#ifndef ACP_CACHE_HPP
#define ACP_CACHE_HPP

#include <cstddef>
#include <iostream>
#include <list>
#include <new>
#include <ostream>

template <class Key, class KeyProvider, class Allocator>
class Cache {
public:
    template <class... AllocArgs>
    Cache(const std::size_t cache_size, AllocArgs &&...alloc_args)
        : m_max_top_size(cache_size), m_max_low_size(cache_size), m_alloc(std::forward<AllocArgs>(alloc_args)...) {
    }

    std::size_t size() const { return privileged_queue.size() + unprivileged_queue.size(); }

    bool empty() const { return size() == 0; }

    template <class T>
    T &get(const Key &key);

    template <class T>
    T &add_to_privileged(KeyProvider * new_element) {
        if (privileged_queue.size() == m_max_top_size) {
            add_to_unprivileged<T>(*(--privileged_queue.end()));
            privileged_queue.pop_back();
        }
        privileged_queue.push_front(new_element);
        return *static_cast<T *>(privileged_queue.front());
    }

    template <class T>
    T &add_to_unprivileged(const Key &key) {
        if (unprivileged_queue.size() == m_max_low_size) {
            m_alloc.template destroy<T>(*(--unprivileged_queue.end()));
            unprivileged_queue.pop_back();
        }
        T *new_key = m_alloc.template create<T>(T(key));
        KeyProvider *new_element(new_key);
        unprivileged_queue.push_front(new_element);
        return *static_cast<T *>(unprivileged_queue.front());
    }

    template <class T>
    T & add_to_unprivileged(KeyProvider * new_element) {
        if (unprivileged_queue.size() == m_max_low_size) {
            m_alloc.template destroy<T>(*(--unprivileged_queue.end()));
            unprivileged_queue.pop_back();
        }
        unprivileged_queue.push_front(new_element);
        return *static_cast<T *>(unprivileged_queue.front());

    }

    std::ostream &print(std::ostream &strm) const;

    friend std::ostream &operator<<(std::ostream &strm, const Cache &cache) { return cache.print(strm); }

private:
    const std::size_t m_max_top_size;
    const std::size_t m_max_low_size;
    Allocator m_alloc;
    std::list<KeyProvider *> privileged_queue;
    std::list<KeyProvider *> unprivileged_queue;
};

template <class Key, class KeyProvider, class Allocator>
template <class T>
inline T &Cache<Key, KeyProvider, Allocator>::get(const Key &key) {
    auto privileged_element = privileged_queue.begin();
    for (; privileged_element != privileged_queue.end(); privileged_element++) {
        if (KeyProvider(**privileged_element) == key) {
            break;
        }
    }
    if (privileged_element != privileged_queue.end()) {
        privileged_queue.splice(privileged_queue.begin(), privileged_queue, privileged_element);
        return *static_cast<T *>(privileged_queue.front());
    }
    auto unprivileged_element = unprivileged_queue.begin();
    for (; unprivileged_element != unprivileged_queue.end(); unprivileged_element++) {
        if (KeyProvider(**unprivileged_element) == key) {
            break;
        }
    }
    if (unprivileged_element != unprivileged_queue.end()) {
        KeyProvider * new_element = *unprivileged_element;
        unprivileged_queue.erase(unprivileged_element);
        T & res = add_to_privileged<T>(new_element);
        return res;
    } else {
        T & res = add_to_unprivileged<T>(key);
        return res;
    }
}

template <class Key, class KeyProvider, class Allocator>
inline std::ostream &Cache<Key, KeyProvider, Allocator>::print(std::ostream &strm) const {
    return strm << "Priority: <empty>"
                << "\nRegular: <che>"
                << "\n";
}

#endif  // ACP_CACHE_HPP
