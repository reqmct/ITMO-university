#ifndef TREE_HPP
#define TREE_HPP

#include <cstddef>
#include <cstdint>
#include <vector>

class Splay {
    struct Node {
        Node *left;
        Node *right;
        Node *parent;
        int key;
        int size = 1;

        constexpr explicit Node(int key) : left(nullptr), right(nullptr), parent(nullptr), key(key) {}
    };

    Node *root = nullptr;

    [[nodiscard]] std::vector<Node *> get_all_nodes() const noexcept;
    void clear();

    void zig(Node *x);

    void zig_zig(Node *x);

    void zig_zag(Node *x);

    void splay(Node *x);

    Node *find(int value) noexcept;
    static void upd_size(Node *x);

public:
    [[nodiscard]] bool contains(int value) noexcept;

    bool insert(int value);

    bool remove(int value);

    [[nodiscard]] std::size_t size() const noexcept;

    [[nodiscard]] bool empty() const noexcept;

    [[nodiscard]] std::vector<int> values() const noexcept;

    std::vector<std::pair<int,int>> vec();
    int h(Node * tmp);
    int h();
    void out();

    ~Splay();
};

#endif
