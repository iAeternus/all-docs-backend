package org.ricky.core.common.domain.idtree;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMap;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AccessLevel;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.ricky.core.common.domain.idtree.exception.IdNodeLevelOverflowException;
import org.ricky.core.common.domain.idtree.exception.IdNodeNotFoundException;
import org.ricky.core.common.domain.idtree.exception.NodeIdFormatException;
import org.ricky.core.common.domain.idtree.validation.NodeId;
import org.ricky.core.common.domain.marker.ValueObject;
import org.ricky.core.common.validation.collection.NoNullElement;

import java.util.*;

import static java.util.Objects.requireNonNull;
import static org.ricky.core.common.util.ValidationUtil.requireNotBlank;

/**
 * @author Ricky
 * @version 1.0
 * @date 2024/9/12
 * @className IdTree
 * @desc N叉树，用于表示层次结构
 */
@EqualsAndHashCode
@Getter(value = AccessLevel.PACKAGE)
@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class IdTree implements ValueObject {

    /**
     * 节点ID分隔符
     */
    public static final String NODE_ID_SEPARATOR = "/";

    /**
     * 根节点的孩子
     */
    @Valid
    @NotNull
    @NoNullElement
    @Size(max = 1000)
    private List<IdNode> nodes;

    /**
     * 利用初始节点ID构造
     *
     * @param initNodeId 初始节点ID
     */
    public IdTree(String initNodeId) {
        if (StringUtils.isBlank(initNodeId)) {
            nodes = new ArrayList<>(0);
        } else {
            nodes = new ArrayList<>(List.of(new IdNode(initNodeId)));
        }
    }

    /**
     * 利用节点集合直接构造
     *
     * @param nodes 节点集合
     */
    public IdTree(List<IdNode> nodes) {
        requireNonNull(nodes, "Node must not be null");

        this.nodes = nodes;
    }

    /**
     * 构建层次结构
     *
     * @param maxAllowedLevel 最大树高
     * @return ID树层次结构
     */
    public IdTreeHierarchy buildHierarchy(int maxAllowedLevel) {
        return new IdTreeHierarchy(
                nodes.stream()
                        .map(idNode -> idNode.buildHierarchy(maxAllowedLevel))
                        .flatMap(map -> map.entrySet().stream())
                        .collect(ImmutableMap.toImmutableMap(Map.Entry::getKey, Map.Entry::getValue))
        );
    }

    /**
     * 添加节点
     *
     * @param parentNodeId 父节点ID
     * @param nodeId       要添加的节点ID
     */
    public void addNode(String parentNodeId, String nodeId) {
        requireNotBlank(nodeId, "Node ID must not be blank.");

        if (StringUtils.isBlank(parentNodeId)) {
            nodes.add(0, new IdNode(nodeId));
            return;
        }

        IdNode parent = nodeById(parentNodeId);
        if (parent == null) {
            throw new IdNodeNotFoundException("ID node [" + parentNodeId + "] not found.");
        }

        parent.addChild(nodeId);
    }

    /**
     * 根据ID查询节点
     *
     * @param nodeId 节点ID
     * @return 节点，若没找到返回null
     */
    IdNode nodeById(String nodeId) {
        requireNotBlank(nodeId, "Node ID must not be blank.");

        for (IdNode node : nodes) {
            IdNode idNode = node.nodeById(nodeId);
            if (idNode != null) {
                return idNode;
            }
        }

        return null;
    }

    /**
     * 根据ID删除节点
     *
     * @param nodeId 节点ID
     * @return true=成功删除 false=节点不存在
     */
    public boolean removeNode(String nodeId) {
        requireNotBlank(nodeId, "Node ID must not be blank.");

        boolean result = nodes.removeIf(idNode -> Objects.equals(nodeId, idNode.id));
        if (result) {
            return true;
        }

        for (IdNode node : nodes) {
            boolean childResult = node.removeChild(nodeId);
            if (childResult) {
                return true;
            }
        }

        return false;
    }

    /**
     * 合并两棵树
     *
     * @param another 另一棵树
     */
    public void merge(IdTree another) {
        Set<String> selfIds = new HashSet<>(this.buildHierarchy(10).allIds());
        Set<String> anotherIds = another.buildHierarchy(10).allIds();
        selfIds.retainAll(anotherIds);
        selfIds.forEach(this::removeNode);
        this.nodes.addAll(0, another.nodes);
    }

    /**
     * 将一个现有的索引树中的节点ID根据提供的 idMap进行更新
     *
     * @param idMap ID map
     * @return 索引树
     */
    public IdTree map(Map<String, String> idMap) {
        if (!Objects.equals(this.buildHierarchy(10).allIds(), idMap.keySet())) {
            throw new RuntimeException("Mapped ID must contain all existing tree ids.");
        }

        ImmutableList<IdNode> mappedNodes = this.nodes.stream()
                .map(idNode -> idNode.map(idMap))
                .collect(ImmutableList.toImmutableList());
        return new IdTree(mappedNodes);
    }

    /**
     * 节点
     */
    @Getter
    @EqualsAndHashCode
    @NoArgsConstructor(access = AccessLevel.PRIVATE)
    public static class IdNode {

        /**
         * 节点id
         */
        @NodeId
        @NotBlank
        @Size(max = 50)
        private String id;

        /**
         * 子节点集合
         */
        @Valid
        @NotNull
        @NoNullElement
        @Size(max = 100)
        private List<IdNode> children;

        private IdNode(String id) {
            if (id.contains(NODE_ID_SEPARATOR)) {
                throw new NodeIdFormatException("Node ID must not contain " + NODE_ID_SEPARATOR + ".");
            }

            this.id = id;
            this.children = new ArrayList<>(0);
        }

        /**
         * 建立层次结构
         *
         * @param maxAllowedLevel 最大树高
         * @return 节点在层级结构中的位置<br>
         * 键-节点本身的ID
         * 值-从根节点到该节点的完整路径，以NODE_ID_SEPARATOR分隔
         */
        private Map<String, String> buildHierarchy(int maxAllowedLevel) {
            Map<String, String> result = new HashMap<>();
            result.put(id, id);

            if (CollectionUtils.isEmpty(children)) {
                return result;
            }

            children.forEach(child -> {
                Map<String, String> hierarchy = child.buildHierarchy(maxAllowedLevel);
                for (Map.Entry<String, String> entry : hierarchy.entrySet()) {
                    String value = id + NODE_ID_SEPARATOR + entry.getValue();
                    int level = StringUtils.countMatches(value, NODE_ID_SEPARATOR) + 1; // 当前树高
                    if (level > maxAllowedLevel) {
                        throw new IdNodeLevelOverflowException("Max allowed level is " + maxAllowedLevel + " but actual is " + level + ".");
                    }
                    result.put(entry.getKey(), value);
                }
            });

            return Map.copyOf(result);
        }

        /**
         * 根据节点ID查询节点
         *
         * @param id 节点ID
         * @return 目标节点
         */
        private IdNode nodeById(String id) {
            if (Objects.equals(this.id, id)) {
                return this;
            }

            for (IdNode child : children) {
                IdNode idNode = child.nodeById(id);
                if (idNode != null) {
                    return idNode;
                }
            }

            return null;
        }

        /**
         * 给当前节点添加子节点<br>
         * 子节点会被插入到子节点列表的开头<br>
         *
         * @param nodeId 子节点ID
         */
        private void addChild(String nodeId) {
            children.add(0, new IdNode(nodeId));
        }

        /**
         * 删除当前节点的给定子节点<br>
         *
         * @param nodeId 子节点ID
         * @return true=成功删除 false=节点不存在
         */
        private boolean removeChild(String nodeId) {
            boolean result = children.removeIf(idNode -> Objects.equals(nodeId, idNode.id));
            if (result) {
                return true;
            }

            for (IdNode child : children) {
                boolean childResult = child.removeChild(nodeId);
                if (childResult) {
                    return true;
                }
            }

            return false;
        }

        /**
         * 将Map转换为层次结构
         *
         * @param idMap 节点本身的ID-该节点子节点的ID
         * @return 树的根节点
         */
        private IdNode map(Map<String, String> idMap) {
            IdNode mappedNode = new IdNode(idMap.get(id));
            if (CollectionUtils.isEmpty(children)) {
                return mappedNode;
            }

            children.forEach(child -> mappedNode.children.add(child.map(idMap)));
            return mappedNode;
        }

    }

}
