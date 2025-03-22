package org.ricky.common.sensitiveword.domain.algorithm.ac;

import lombok.EqualsAndHashCode;

import javax.annotation.concurrent.NotThreadSafe;
import java.util.*;

/**
 * @author Ricky
 * @version 1.0
 * @date 2024/8/21
 * @className ACTrie
 * @desc aho-corasick算法（又称AC自动机算法）
 */
@NotThreadSafe
@EqualsAndHashCode
public class ACTrie {

    /**
     * 根节点
     */
    private final ACTrieNode root;

    public ACTrie(List<String> words) {
        words = words.stream().distinct().toList();
        root = new ACTrieNode();
        for (String word : words) {
            addWord(word);
        }
        initFailover();
    }

    /**
     * 添加敏感词
     *
     * @param word 敏感词
     */
    public void addWord(String word) {
        ACTrieNode walkNode = root;
        char[] chars = word.toCharArray();
        for (int i = 0; i < word.length(); ++i) {
            walkNode.addChildIfAbsent(chars[i]);
            walkNode = walkNode.childOf(chars[i]);
            walkNode.setDepth(i + 1);
        }
        walkNode.setLeaf(true);
    }

    /**
     * 初始化节点的fail指针
     */
    private void initFailover() {
        Queue<ACTrieNode> queue = new LinkedList<>();
        Map<Character, ACTrieNode> children = root.getChildren();
        for (ACTrieNode node : children.values()) {
            // 第一层的fail指针指向root
            node.setFailover(root);
            queue.offer(node);
        }
        // 利用层序遍历构建剩余层数节点的fail指针
        while (!queue.isEmpty()) {
            ACTrieNode parentNode = queue.poll();
            // 遍历子节点
            for (Map.Entry<Character, ACTrieNode> entry : parentNode.getChildren().entrySet()) {
                ACTrieNode childNode = handleFailover(entry, parentNode);
                queue.offer(childNode);
            }
        }
    }

    /**
     * 处理fail指针
     *
     * @param entry      子节点entry
     * @param parentNode 父节点
     * @return 返回子节点
     */
    private ACTrieNode handleFailover(Map.Entry<Character, ACTrieNode> entry, ACTrieNode parentNode) {
        ACTrieNode childNode = entry.getValue();
        ACTrieNode failover = parentNode.getFailover();
        // 在树中找到以childNode为结尾的字符串的最长前缀匹配，failover指向了这个最长前缀匹配的父节点
        while (failover != null && !failover.hasChild(entry.getKey())) {
            failover = failover.getFailover();
        }
        if (failover == null) {
            // 回溯到了root节点，fail指针指向root
            childNode.setFailover(root);
        } else {
            // 更新fail指针
            childNode.setFailover(failover.childOf(entry.getKey()));
        }
        return childNode;
    }

    /**
     * 查询句子中包含的敏感词的起始位置和结束位置
     *
     * @param text 文本
     * @return 匹配结果列表
     */
    public List<MatchResult> matches(String text) {
        List<MatchResult> result = new ArrayList<>();
        ACTrieNode walkNode = root;
        for (int i = 0; i < text.length(); ++i) {
            char c = text.charAt(i);
            while (!walkNode.hasChild(c) && walkNode.getFailover() != null) {
                walkNode = walkNode.getFailover();
            }
            // 如果因为当前节点的孩子节点有这个字符，则将walkNode替换为下面的孩子节点
            if (walkNode.hasChild(c)) {
                walkNode = walkNode.childOf(c);
                // 检索到了敏感词
                if (walkNode.isLeaf()) {
                    result.add(new MatchResult(i - walkNode.getDepth() + 1, i + 1));
                    // 模式串回退到最长可匹配前缀位置并开启新一轮的匹配
                    // 这种回退方式将一个不漏的匹配到所有的敏感词，匹配结果的区间可能会有重叠的部分
                    walkNode = walkNode.getFailover();
                }
            }
        }
        return result;
    }

}
