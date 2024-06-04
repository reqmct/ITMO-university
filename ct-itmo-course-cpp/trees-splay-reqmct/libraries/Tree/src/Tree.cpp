#include "tree/Tree.hpp"

#include <iostream>
#include <queue>
#include <stack>

std::vector<Splay::Node *> Splay::get_all_nodes() const noexcept {
    std::vector<Node *> result;
    if (root == nullptr) {
        return result;
    }
    result.reserve(root->size + 1);
    std::stack<Node *> st;
    st.push(root);
    while (!st.empty()) {
        Node *top = st.top();
        if (top->left != nullptr) {
            st.push(top->left);
            continue;
        }
        while (!st.empty() && st.top()->right == nullptr) {
            top = st.top();
            st.pop();
            result.push_back(top);
        }
        if (!st.empty()) {
            top = st.top();
            result.push_back(st.top());
            st.pop();
            st.push(top->right);
        }
    }
    return result;
}

void Splay::upd_size(Node *x) {
    if (x == nullptr) {
        return;
    }
    x->size = 1;
    if (x->left != nullptr) {
        x->size += x->left->size;
    }
    if (x->right != nullptr) {
        x->size += x->right->size;
    }
}

void Splay::zig(Node *x) {
    Node *first_parent = x->parent;
    Node *grand_parent = x->parent->parent;
    if (grand_parent == nullptr) {
        root = x;
    } else {
        if (grand_parent->left == first_parent) {
            grand_parent->left = x;
        } else {
            grand_parent->right = x;
        }
    }
    if (x->parent->left == x) {
        Node *b              = x->right;
        x->parent            = first_parent->parent;
        first_parent->parent = x;
        first_parent->left   = b;
        x->right             = first_parent;
        if (b != nullptr) {
            b->parent = x->right;
        }
    } else {
        Node *b              = x->left;
        x->parent            = first_parent->parent;
        first_parent->parent = x;
        first_parent->right  = b;
        x->left              = first_parent;
        if (b != nullptr) {
            b->parent = x->left;
        }
    }
    upd_size(x->left);
    upd_size(x->right);
    upd_size(x);
    upd_size(grand_parent);
}

void Splay::zig_zig(Node *x) {
    zig(x->parent);
    zig(x);
}

void Splay::zig_zag(Node *x) {
    zig(x);
    zig(x);
}

void Splay::splay(Node *x) {
    while (x->parent != nullptr) {
        Node *parent       = x->parent;
        Node *grand_parent = parent->parent;
        if (grand_parent == nullptr) {
            zig(x);
        } else if (grand_parent->left == parent && parent->left == x) {
            zig_zig(x);
        } else if (grand_parent->right == parent && parent->right == x) {
            zig_zig(x);
        } else {
            zig_zag(x);
        }
    }
}

std::vector<int> Splay::values() const noexcept {
    std::vector<Node *> all_nodes = get_all_nodes();
    std::vector<int> result;
    for (auto node : all_nodes) {
        result.push_back(node->key);
    }
    return result;
}

Splay::Node *Splay::find(int value)

    noexcept {
    Node *tmp = root;
    while (tmp != nullptr) {
        if (tmp->key == value) {
            splay(tmp);
            return tmp;
        }
        if (value < tmp->key) {
            tmp = tmp->left;
        } else {
            tmp = tmp->right;
        }
    }
    return nullptr;
}

bool Splay::contains(int value)

    noexcept {
    if (find(value) == nullptr) {
        return false;
    }
    return true;
}

bool Splay::insert(int value) {
    if (root == nullptr) {
        root = new Node(value);
        return true;
    }
    Node *tmp = root;
    while (true) {
        if (tmp->key == value) {
            return false;
        }
        if (value < tmp->key) {
            if (tmp->left == nullptr) {
                break;
            }
            tmp = tmp->left;
        } else {
            if (tmp->right == nullptr) {
                break;
            }
            tmp = tmp->right;
        }
    }
    Node *new_node   = new Node(value);
    new_node->parent = tmp;
    if (tmp->key > value) {
        tmp->left = new_node;
    } else {
        tmp->right = new_node;
    }
    splay(new_node);
    return true;
}

bool Splay::remove(int value) {
    Node *element = find(value);
    if (element == nullptr) {
        return false;
    }
    if (element->left == nullptr) {
        root = element->right;
        if (root != nullptr) {
            root->parent = nullptr;
        }
        delete element;
        return true;
    }
    Node *left     = element->left;
    int right_size = element->right == nullptr ? 0 : element->right->size;
    left->size += right_size;
    while (left->right != nullptr) {
        left = left->right;
        left->size += right_size;
    }
    if (element->right != nullptr) {
        left->right         = element->right;
        left->right->parent = left;
    }
    root         = element->left;
    root->parent = nullptr;
    delete element;
    return true;
}

bool Splay::empty() const

    noexcept {
    return root == nullptr;
}

std::size_t Splay::size() const

    noexcept {
    return root->size;
}

void Splay::clear() {
    std::vector<Node *> all_nodes = get_all_nodes();
    for (auto &all_node : all_nodes) {
        delete all_node;
    }
}

void Splay::out() {
    std::queue<std::pair<Node *, int>> q;
    q.push({root, 0});
    int h = 0;
    std::cout << h << " : ";
    while (!q.empty()) {
        std::pair<Node *, int> tmp = q.front();
        q.pop();
        if (tmp.first == nullptr) {
            continue;
        }
        if (h != tmp.second) {
            h++;
            std::cout << "\n" << h << " : ";
        }
        std::cout << tmp.first->key << " ";
        q.push({tmp.first->left, tmp.second + 1});
        q.push({tmp.first->right, tmp.second + 1});
    }
}

std::vector<std::pair<int,int>> Splay::vec() {
    std::vector<std::pair<int,int>> rez;
    std::queue<std::pair<Node *, int>> q;
    q.push({root, 0});
    int h = 0;
    while (!q.empty()) {
        std::pair<Node *, int> tmp = q.front();
        q.pop();
        if (tmp.first == nullptr) {
            continue;
        }
        if (h != tmp.second) {
            h++;
        }
        rez.push_back({tmp.first->key, tmp.second});
        q.push({tmp.first->left, tmp.second + 1});
        q.push({tmp.first->right, tmp.second + 1});
    }
    return rez;
}

int Splay::h(Node * tmp) {
    if(tmp == nullptr) {
        return 0;
    }
    int h1 = h(tmp->left);
    int h2 = h(tmp->right);
    if(h1 > h2) {
        return h1 + 1;
    }
    return h2 + 1;
}
int Splay::h() {
    return h(root);
}

Splay::~Splay() {
    clear();
    root = nullptr;
}