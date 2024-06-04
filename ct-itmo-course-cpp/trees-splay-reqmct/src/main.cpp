#include <algorithm>
#include <iostream>
#include <map>

#include "tree/Tree.hpp"

Splay make_tree(std::vector<int> v) {
    Splay tree;
    for (int i = 0; i < 7; i++) {
        tree.insert(v[i]);
    }
    return tree;
}

void out(std::vector<int> v) {
    for (int i = 0; i < v.size(); i++) {
        std::cout << v[i] << " ";
    }
    std::cout << "\n";
}

void rez(int n) {
    std::vector<int> v;
    for (int i = 0; i < n; i++) {
        v.push_back(i);
    }
    do {
        Splay tree                           = make_tree(v);
        std::vector<std::pair<int, int>> all = tree.vec();
        for (int i = all.size() - 1; i >= 0; i--) {
            Splay new_tree = make_tree(v);
            new_tree.contains(all[i].first);
            if (all[i].second >= tree.h() - 2 && new_tree.h() >= n) {
                std::cout << "------------" << new_tree.h() << " " << all[i].first << " " << all[i].second
                          << "----------------------\n";
                new_tree.out();
                std::cout << "\n";
                out(v);
            }
        }
    } while (std::next_permutation(v.begin(), v.end()));
}

int main() {
    std::map<int, int> mp;
    for (int i = 0; i < 4; i++) {
        mp[i] = i;
    }
}