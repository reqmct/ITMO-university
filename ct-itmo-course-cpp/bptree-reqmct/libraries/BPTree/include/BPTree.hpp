#ifndef BPTREE_HPP
#define BPTREE_HPP

#include <cstddef>
#include <functional>
#include <iostream>
#include <queue>
#include <vector>

template <class Key, class Value, std::size_t BlockSize = 4096, class Less = std::less<Key>>
class BPTree {
    static const std::size_t max_size =
        (BlockSize - sizeof(std::size_t) - sizeof(void *)) / (sizeof(Key) + sizeof(void *));

    static const std::size_t neutral = std::size_t(-1);

    struct Node {
        using key_type    = Key;
        using mapped_type = Value;
        using value_type  = std::pair<Key, Value>;
        using node_type   = Node;
        bool is_leaf;
        std::vector<value_type> keys;
        std::size_t size;
        node_type *parent = nullptr;
        std::vector<node_type *> children;
        static bool check(const Key &first, const Key &second, const std::string &operation) {
            if (operation == "==") {
                return Less{}(first, second) == Less{}(second, first);
            }
            if (operation == "<") {
                return Less{}(first, second);
            }
            if (operation == ">") {
                return Less{}(second, first);
            }
            if (operation == "<=") {
                return check(first, second, "==") || check(first, second, "<");
            }
            if (operation == ">=") {
                return check(first, second, "==") || check(first, second, ">");
            }
            return false;
        }

        void clear() {
            is_leaf = true;
            delete this;
        }

        Node(const Node &node) {
            is_leaf = node.is_leaf;
            size    = node.size;
            keys.clear();
            keys.resize(max_size + 1);
            children.clear();
            children.resize(max_size + 2, nullptr);
            for (std::size_t i = 0; i < size; i++) {
                keys[i] = value_type(node.keys[i].first, node.keys[i].second);
            }
            if (this->is_leaf) {
                return;
            }
            for (std::size_t i = 0; i <= size; i++) {
                children[i]         = new Node(*node.children[i]);
                children[i]->parent = this;
            }
        }

        Node &operator=(const Node &source) { return *new (this) Node(source); }

        ~Node() {
            parent = nullptr;
            if (!is_leaf) {
                for (std::size_t i = 0; i <= size; i++) {
                    delete children[i];
                }
            }
        }

        Node() {
            is_leaf = false;
            keys.clear();
            keys.resize(max_size + 1);
            size   = 0;
            parent = nullptr;
            children.clear();
            children.resize(max_size + 2, nullptr);
        }

        std::size_t getIndex(const Key &find) {
            for (std::size_t i = 0; i < size; i++) {
                if (check(find, keys[i].first, "==")) {
                    return i;
                }
            }
            return neutral;
        }

        std::size_t getChildIndex(const Key &find) {
            if (size == 0 || check(find, keys[0].first, "<=")) {
                return 0;
            }
            for (std::size_t i = 1; i < size; i++) {
                if (check(keys[i - 1].first, find, "<") && check(find, keys[i].first, "<=")) {
                    return i;
                }
            }
            return size;
        }

        std::size_t getChildrenByNode(Node *node) {
            for (std::size_t i = 0; i <= size; i++) {
                if (children[i] == node) {
                    return i;
                }
            }
            return neutral;
        }

        template <class forward_type>
        bool add_element(const Key &key, forward_type &&value, node_type *child1, node_type *child2) {
            if (child1 != nullptr) {
                child1->parent = this;
            }
            if (child2 != nullptr) {
                child2->parent = this;
            }
            if (size == 0) {
                if (!is_leaf) {
                    children[0] = child1;
                    children[1] = child2;
                } else {
                    if (children[0] != nullptr) {
                        children[0]->children[1] = this;
                    }
                    if (children[1] != nullptr) {
                        children[1]->children[0] = this;
                    }
                }
                keys[0] = value_type(key, std::forward<forward_type>(value));
                size++;
                return true;
            }
            std::size_t ind = getIndex(key);
            if (ind != neutral) {
                keys[ind] = value_type(key, std::forward<forward_type>(value));
                return false;
            }
            ind = getChildIndex(key);
            for (std::size_t i = size; i > ind; i--) {
                std::swap(keys[i], keys[i - 1]);
            }
            keys[ind] = value_type(key, std::forward<forward_type>(value));
            size++;
            if (is_leaf) {
                return true;
            }
            for (std::size_t i = size; i > ind; i--) {
                children[i] = children[i - 1];
            }
            children[ind]     = child1;
            children[ind + 1] = child2;
            return true;
        }

        void delete_by_ind(int ind) {
            for (std::size_t i = ind; i < size - 1; i++) {
                std::swap(keys[i], keys[i + 1]);
            }
            if (!is_leaf) {
                for (std::size_t i = ind; i < size; i++) {
                    std::swap(children[i], children[i + 1]);
                }
                children[size] = nullptr;
            }
            size--;
        }

        void merge_leaf(Node *prev) {
            children[0] = prev->children[0];
            if (children[0] != nullptr) {
                children[0]->children[1] = this;
            }
            if (size >= 1) {
                for (std::size_t i = size - 1;; i--) {
                    keys[i + prev->size] = keys[i];
                    if (i == 0) {
                        break;
                    }
                }
            }
            for (std::size_t i = 0; i < prev->size; i++) {
                keys[i] = prev->keys[i];
            }
            size += prev->size;
        }

        void merge(Node *prev, value_type element) {
            if (is_leaf) {
                merge_leaf(prev);
                return;
            }
            if (size >= 1) {
                for (std::size_t i = size - 1;; i--) {
                    keys[i + prev->size + 1] = keys[i];
                    if (i == 0) {
                        break;
                    }
                }
            }
            for (std::size_t i = 0; i < prev->size; i++) {
                keys[i] = prev->keys[i];
            }
            keys[prev->size] = element;
            for (std::size_t i = size;; i--) {
                children[i + prev->size + 1] = children[i];
                if (i == 0) {
                    break;
                }
            }
            for (std::size_t i = 0; i < prev->size + 1; i++) {
                children[i]         = prev->children[i];
                children[i]->parent = this;
            }
            size = size + prev->size + 1;
        }

        node_type *split_node() {
            std::size_t size1 = size / 2 + size % 2;
            node_type *node   = make_new_node(size1, size);
            return node;
        }

    private:
        node_type *make_new_node(std::size_t start, std::size_t finish) {
            node_type *node = new node_type();
            node->size      = finish - start;
            this->size      = start;
            node->is_leaf   = this->is_leaf;
            node->parent    = this->parent;
            for (std::size_t i = start; i < finish; i++) {
                std::swap(node->keys[i - start], this->keys[i]);
            }
            if (node->is_leaf) {
                if (this->children[1] != nullptr) {
                    this->children[1]->children[0] = node;
                }
                node->children[0] = this;
                node->children[1] = this->children[1];
                this->children[1] = node;
            } else {
                this->size--;
                for (std::size_t i = start; i <= finish; i++) {
                    std::swap(node->children[i - start], this->children[i]);
                    if (node->children[i - start] != nullptr) {
                        node->children[i - start]->parent = node;
                    }
                }
            }
            return node;
        }
    };

    template <class iterator_value>
    class CustomIterator {
    public:
        using iterator_category = std::forward_iterator_tag;
        using difference_type   = std::ptrdiff_t;
        using value_type        = std::pair<Key, Value>;
        using pointer           = iterator_value *;
        using reference         = iterator_value &;

    private:
        using iterator_type       = CustomIterator<iterator_value>;
        using const_iterator_type = CustomIterator<const iterator_value>;
        using node_type           = Node;
        node_type *leaf;
        std::size_t ind = 0;

    public:
        CustomIterator() : leaf(nullptr) {}

        CustomIterator(node_type *leaf) { this->leaf = leaf; }

        CustomIterator(node_type *leaf, std::size_t ind) : leaf(leaf), ind(ind) {}

        operator const_iterator_type() const { return const_iterator_type(leaf, ind); }

        reference operator*() const { return leaf->keys[ind]; }

        pointer operator->() const { return &(operator*()); }

        iterator_type &operator++() {
            ind++;
            if (ind == leaf->size) {
                ind  = 0;
                leaf = leaf->children[1];
            }
            return *this;
        }

        iterator_type operator++(int) {
            iterator_type tmp = *this;
            ++(*this);
            return tmp;
        }

        friend bool operator==(const iterator_type &a, const iterator_type &b) {
            return a.ind == b.ind && a.leaf == b.leaf;
        }

        friend bool operator!=(const iterator_type &a, const iterator_type &b) {
            return !(a.ind == b.ind && a.leaf == b.leaf);
        }
    };

public:
    using key_type        = Key;
    using mapped_type     = Value;
    using value_type      = std::pair<Key, Value>;  // NB: a digression from std::map
    using reference       = value_type &;
    using const_reference = const value_type &;
    using pointer         = value_type *;
    using const_pointer   = const value_type *;
    using size_type       = std::size_t;

    using iterator       = CustomIterator<value_type>;
    using const_iterator = CustomIterator<const value_type>;

    BPTree() {}

    BPTree(std::initializer_list<std::pair<Key, Value>> list) {
        for (auto element : list) {
            insert(element.first, element.second);
        }
    }

private:
    void copy(const BPTree<Key, Value, BlockSize, Less> &prototype) {
        clear();
        root = new Node(*prototype.root);
        std::vector<Node *> leafs;
        std::queue<Node *> q;
        q.push(root);
        while (!q.empty()) {
            Node *top = q.front();
            q.pop();
            if (top->is_leaf) {
                leafs.push_back(top);
            } else {
                for (std::size_t i = 0; i < top->size + 1; i++) {
                    q.push(top->children[i]);
                }
            }
        }
        first_node              = leafs[0];
        first_node->children[0] = nullptr;
        for (std::size_t i = 1; i < leafs.size(); i++) {
            leafs[i - 1]->children[1] = leafs[i];
            leafs[i]->children[0]     = leafs[i - 1];
        }
        leafs[leafs.size() - 1]->children[1] = nullptr;
        tree_size                            = prototype.tree_size;
    }
    void move_source(BPTree &&prototype) {
        clear();
        std::swap(root, prototype.root);
        std::swap(first_node, prototype.first_node);
        std::swap(tree_size, prototype.tree_size);
    }

public:
    BPTree(const BPTree<Key, Value, BlockSize, Less> &prototype) { copy(prototype); }

    BPTree(BPTree &&prototype) { move_source(std::move(prototype)); }

    iterator begin() {
        iterator begin = iterator(first_node);
        return begin;
    }

    const_iterator cbegin() const { return const_iterator(first_node); }

    const_iterator begin() const { return cbegin(); }

    iterator end() { return iterator(nullptr); }

    const_iterator cend() const { return const_iterator(nullptr); }

    const_iterator end() const { return cend(); }

    bool empty() const { return root == nullptr; }

    size_type size() const { return tree_size; }

    ~BPTree() { clear(); }

    void clear() {
        tree_size = 0;
        delete root;
        root       = nullptr;
        first_node = nullptr;
    }

    size_type count(const Key &key) const { return contains(key); }

    bool contains(const Key &key) const {
        node_type *tmp = find_leaf(key);
        return tmp != nullptr && tmp->getIndex(key) != neutral;
    }

    std::pair<iterator, iterator> equal_range(const Key &key) {
        iterator start  = lower_bound(key);
        iterator finish = start;
        if (start != end()) {
            finish++;
        }
        return {start, finish};
    }

    std::pair<const_iterator, const_iterator> equal_range(const Key &key) const {
        const_iterator start  = lower_bound(key);
        const_iterator finish = start;
        if (start != end()) {
            finish++;
        }
        return {start, finish};
    }

    iterator lower_bound(const Key &key) {
        std::pair<node_type *, std::size_t> tmp = tree_lower_bound(key);
        return iterator(tmp.first, tmp.second);
    }

    const_iterator lower_bound(const Key &key) const {
        std::pair<node_type *, std::size_t> tmp = tree_lower_bound(key);
        return const_iterator(tmp.first, tmp.second);
    }

    iterator upper_bound(const Key &key) {
        std::pair<node_type *, std::size_t> tmp = tree_upper_bound(key);
        return iterator(tmp.first, tmp.second);
    }

    const_iterator upper_bound(const Key &key) const {
        std::pair<node_type *, std::size_t> tmp = tree_upper_bound(key);
        return const_iterator(tmp.first, tmp.second);
    }

    iterator find(const Key &key) {
        std::pair<node_type *, std::size_t> tmp = tree_find(key);
        if (tmp.first == nullptr) {
            return end();
        }
        return iterator(tmp.first, tmp.second);
    }

    const_iterator find(const Key &key) const {
        std::pair<node_type *, std::size_t> tmp = tree_find(key);
        if (tmp.first == nullptr) {
            return end();
        }
        return const_iterator(tmp.first, tmp.second);
    }

    BPTree<Key, Value, BlockSize, Less> &operator=(BPTree<Key, Value, BlockSize, Less> &&source) {
        move_source(std::move(source));
        return *this;
    }

    BPTree<Key, Value, BlockSize, Less> &operator=(const BPTree<Key, Value, BlockSize, Less> &source) {
        copy(source);
        return *this;
    }

    // 'at' method throws std::out_of_range if there is no such key
    Value &at(const Key &key) {
        iterator tmp = find(key);
        if (tmp == end()) {
            throw std::out_of_range("Incorrect key");
        }
        return (tmp->second);
    }

    const Value &at(const Key &key) const {
        const_iterator tmp = find(key);
        if (tmp == end()) {
            throw std::out_of_range("Incorrect key");
        }
        return (find(key)->second);
    }

    // '[]' operator inserts a new element if there is no such key
    Value &operator[](const Key &key) {
        const_iterator tmp = find(key);
        if (tmp == end()) {
            insert_to_tree(key, defaultValue);
        }
        return (find(key)->second);
    }

    std::pair<iterator, bool> insert(const Key &key, const Value &value) {
        bool add = find(key) == end();
        insert_to_tree(key, value);
        return {find(key), add};
    }

    std::pair<iterator, bool> insert(const Key &key, Value &&value) {
        bool add = find(key) == end();
        insert_to_tree(key, std::forward<Value &&>(value));
        return {find(key), add};
    }

    // NB: a digression from std::map
    template <class ForwardIt>
    void insert(ForwardIt begin, ForwardIt end) {
        ForwardIt element = begin;
        while (element != end) {
            insert_to_tree(element->first, element->second);
            element++;
        }
    }

    void insert(std::initializer_list<std::pair<Key, Value>> list) {
        for (auto element : list) {
            insert_to_tree(element.first, element.second);
        }
    }

private:
    Node *getPrev(Node *node) {
        int h      = 0;
        Node *curr = node->parent;
        Node *tmp  = node;
        if (node->is_leaf) {
            return node->children[0];
        }
        while (curr != nullptr) {
            std::size_t ind = curr->getChildrenByNode(tmp);
            if (ind != 0) {
                Node *prev = curr->children[ind - 1];
                while (h--) {
                    prev = prev->children[prev->size];
                }
                return prev;
            }
            tmp  = curr;
            curr = curr->parent;
            h++;
        }
        return nullptr;
    }

    Node *getNext(Node *node) {
        int h      = 0;
        Node *curr = node->parent;
        Node *tmp  = node;
        if (node->is_leaf) {
            return node->children[1];
        }
        while (curr != nullptr) {
            std::size_t ind = curr->getChildrenByNode(tmp);
            if (ind != curr->size) {
                Node *next = curr->children[ind + 1];
                while (h--) {
                    next = next->children[0];
                }
                return next;
            }
            tmp  = curr;
            curr = curr->parent;
            h++;
        }
        return nullptr;
    }

    void upd_parent(Node *node) {
        while (!node->is_leaf) {
            node = node->children[node->size];
        }
        value_type element;
        if (node->size != 0) {
            element = node->keys[node->size - 1];
        } else if (node->children[0] != nullptr) {
            Node *prev = node->children[0];
            element    = prev->keys[prev->size - 1];
        }
        Node *curr = node->parent;
        Node *tmp  = node;
        while (curr != nullptr) {
            std::size_t ind = curr->getChildrenByNode(tmp);
            if (ind != curr->size) {
                curr->keys[ind] = element;
                break;
            }
            tmp  = curr;
            curr = curr->parent;
        }
    }

    void merge_node(Node *left, Node *right) {
        if (left == first_node) {
            first_node = right;
        }
        std::size_t delete_ind  = left->parent->getChildrenByNode(left);
        value_type move_element = left->parent->keys[delete_ind];
        erase(left->parent, delete_ind);
        right->merge(left, move_element);
        upd_parent(right);
        left->clear();
    }

    void erase(Node *node, const std::size_t &delete_ind) {
        if (tree_size == 0) {
            clear();
            return;
        }
        node->delete_by_ind(delete_ind);
        if (node->size < max_size / 2) {
            Node *prev = getPrev(node);
            Node *next = getNext(node);
            if (prev != nullptr && prev->size > max_size / 2) {
                Node *child1 = nullptr;
                Node *child2 = nullptr;
                if (!node->is_leaf) {
                    child1 = prev->children[prev->size];
                    child2 = node->children[0];
                }
                value_type move_element = prev->keys[prev->size - 1];
                erase(prev, prev->size - std::size_t(prev->is_leaf));
                node->add_element(move_element.first, move_element.second, child1, child2);
                if (!node->is_leaf) {
                    upd_parent(node->children[0]);
                }
                upd_parent(node);
            } else if (next != nullptr && next->size > max_size / 2) {
                Node *child1 = nullptr;
                Node *child2 = nullptr;
                if (!node->is_leaf) {
                    child1 = node->children[node->size];
                    child2 = next->children[0];
                }
                value_type move_element = next->keys[0];
                erase(next, 0);
                node->add_element(move_element.first, move_element.second, child1, child2);
                upd_parent(node);
            } else if (prev != nullptr && prev->parent == node->parent) {
                merge_node(prev, node);
            } else if (next != nullptr && next->parent == node->parent) {
                merge_node(node, next);
            } else {
                if (node->size == 0) {
                    if (root == node) {
                        root                      = node->children[0];
                        node->children[0]->parent = nullptr;
                    }
                    node->clear();
                }
            }
        } else {
            upd_parent(node);
        }
    }

public:
    iterator erase(const_iterator source) {
        if (source == end()) {
            return end();
        }
        tree_size--;
        node_type *leaf           = find_leaf(source->first);
        const key_type source_key = source->first;
        erase(leaf, leaf->getIndex(source_key));
        return upper_bound(source_key);
    }

    iterator erase(const_iterator start, const_iterator finish) {
        std::vector<Key> v;
        while (start != finish) {
            v.push_back(start->first);
            start++;
        }
        const Key res_key = finish->first;
        for (std::size_t i = 0; i < v.size(); i++) {
            erase(v[i]);
        }
        v.clear();
        return find(res_key);
    }

    size_type erase(const Key &key) {
        const_iterator it = find(key);
        if (it == end()) {
            return 0;
        }
        erase(it);
        return 1;
    }

private:
    using node_type       = Node;
    node_type *root       = nullptr;
    node_type *first_node = nullptr;
    Value defaultValue    = Value();
    size_type tree_size   = 0;

    template <class forward_type>
    void insert_to_tree(const Key &key, forward_type &&value) {
        if (root == nullptr) {
            root       = new node_type();
            first_node = root;

            root->children[0] = nullptr;
            root->children[1] = nullptr;
            root->is_leaf     = true;

            add_to_node(root, key, std::forward<forward_type>(value), nullptr, nullptr);

            tree_size++;
            return;
        }
        node_type *leaf = find_leaf(key);
        bool add        = add_to_node(leaf, key, std::forward<forward_type>(value), nullptr, nullptr);
        tree_size += add;
    }

    template <class forward_type>
    bool add_to_node(node_type *node, const Key &key, forward_type &&value, node_type *child1, node_type *child2) {
        bool add = node->add_element(key, std::forward<forward_type>(value), child1, child2);
        if (max_size + 1 == node->size) {
            node_type *node2  = node->split_node();
            node_type *parent = node->parent;
            if (parent == nullptr) {
                parent = new node_type{};
                root   = parent;
            }
            add_to_node(parent, node->keys[node->size - node->is_leaf].first, std::move(Value()), node, node2);
            return add;
        }
        return add;
    }

    node_type *find_leaf(const Key &key) const {
        node_type *tmp = root;
        while (tmp != nullptr) {
            if (tmp->is_leaf) {
                return tmp;
            }
            std::size_t ind = tmp->getChildIndex(key);
            tmp             = tmp->children[ind];
        }
        return nullptr;
    }

    node_type *find_node(const Key &key) const {
        node_type *tmp = root;
        while (tmp != nullptr) {
            if (tmp->is_leaf) {
                return nullptr;
            }
            std::size_t ind = tmp->getIndex(key);
            if (ind != neutral) {
                return tmp;
            }
            ind = tmp->getChildIndex(key);
            tmp = tmp->children[ind];
        }
        return nullptr;
    }

    std::pair<node_type *, std::size_t> tree_lower_bound(const Key &key) const {
        node_type *tmp = find_leaf(key);
        if (tmp == nullptr) {
            return {nullptr, 0};
        }
        std::size_t ind = tmp->getChildIndex(key);
        if (ind >= tmp->size) {
            return {tmp->children[1], 0};
        }
        return {tmp, ind};
    }

    std::pair<node_type *, std::size_t> tree_upper_bound(const Key &key) const {
        node_type *tmp = find_leaf(key);
        if (tmp == nullptr) {
            return {nullptr, 0};
        }
        std::size_t ind = tmp->getChildIndex(key) + (tmp->getIndex(key) != neutral);
        if (ind >= tmp->size) {
            return {tmp->children[1], 0};
        }
        return {tmp, ind};
    }

    std::pair<node_type *, std::size_t> tree_find(const Key &key) const {
        node_type *tmp = find_leaf(key);
        if (tmp == nullptr) {
            return {nullptr, 0};
        }
        std::size_t ind = tmp->getIndex(key);
        if (ind == neutral) {
            return {nullptr, 0};
        }
        return {tmp, ind};
    }
};

#endif