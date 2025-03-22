package org.ricky.common.sensitiveword.domain.algorithm.ac;

import lombok.Data;
import lombok.EqualsAndHashCode;

import java.util.HashMap;
import java.util.Map;

/**
 * @author Ricky
 * @version 1.0
 * @date 2024/8/21
 * @className ACTrieNode
 * @desc AC字典树节点
 */
@Data
@EqualsAndHashCode
public class ACTrieNode {

    /**
     * 子节点集合
     */
    private final Map<Character, ACTrieNode> children = new HashMap<>();

    /**
     * fail指针<br>
     * 匹配过程中，如果模式串不匹配，模式串指针会回退到failover继续进行匹配
     */
    private ACTrieNode failover = null;

    /**
     * 节点深度
     */
    private int depth;

    /**
     * 是否为叶结点<br>
     * trie树的叶结点代表敏感词的结尾
     */
    private boolean isLeaf = false;

    /**
     * 添加子节点<br>
     * 如果已存在，则什么都不做
     *
     * @param c 字符
     */
    public void addChildIfAbsent(char c) {
        children.computeIfAbsent(c, key -> new ACTrieNode());
    }

    /**
     * 获取子节点
     *
     * @param c 子节点存储的字符
     * @return 如果存在返回子节点，否则返回null
     */
    public ACTrieNode childOf(char c) {
        return children.get(c);
    }

    /**
     * 判断子节点是否存在
     *
     * @param c 子节点存储的字符
     * @return true=存在 false=不存在
     */
    public boolean hasChild(char c) {
        return children.containsKey(c);
    }

    public boolean isLeaf() {
        return isLeaf;
    }

    public void setLeaf(boolean leaf) {
        isLeaf = leaf;
    }

    @Override
    public String toString() {
        return "ACTrieNode{" +
                ", failover=" + failover +
                ", depth=" + depth +
                ", isLeaf=" + isLeaf +
                '}';
    }
}
