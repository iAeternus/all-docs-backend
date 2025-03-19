package org.ricky.core.common.domain.idtree;

import com.google.common.collect.ImmutableMap;
import com.google.common.collect.ImmutableSet;
import com.google.common.collect.Maps;
import lombok.AccessLevel;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import org.apache.commons.lang3.StringUtils;
import org.ricky.core.common.domain.idtree.exception.IdNodeNotFoundException;
import org.ricky.core.common.domain.marker.ValueObject;

import java.util.Arrays;
import java.util.Map;
import java.util.Objects;
import java.util.Set;

import static java.util.Objects.requireNonNull;
import static java.util.stream.Collectors.joining;
import static org.apache.commons.lang3.StringUtils.isBlank;
import static org.ricky.core.common.domain.idtree.IdTree.NODE_ID_SEPARATOR;
import static org.ricky.core.common.util.ValidationUtil.requireNotBlank;

/**
 * @author Ricky
 * @version 1.0
 * @date 2024/9/12
 * @className IdTreeHierarchy
 * @desc ID树层次结构
 */
@EqualsAndHashCode
@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class IdTreeHierarchy implements ValueObject {

    /**
     * 映射集合
     * 键-节点本身的ID
     * 值-从根节点到该节点的完整路径，以NODE_ID_SEPARATOR分隔
     */
    private Map<String, String> schemas;

    public IdTreeHierarchy(Map<String, String> schemas) {
        requireNonNull(schemas, "Hierarchy must not be null.");
        this.schemas = schemas;
    }

    /**
     * 根据ID获取从根节点到该节点的完整路径
     *
     * @param id 节点ID
     * @return 从根节点到该节点的完整路径
     */
    public String schemaOf(String id) {
        requireNotBlank(id, "Node ID must not be null.");

        String schema = schemas.get(id);
        if (StringUtils.isBlank(schema)) {
            throw new IdNodeNotFoundException("No ID node found for [" + id + "].");
        }
        return schema;
    }

    /**
     * 计算ID对应的节点的高度
     *
     * @param id 节点ID
     * @return 树高
     */
    public int levelOf(String id) {
        requireNotBlank(id, "Node ID must not be null.");

        return StringUtils.countMatches(schemaOf(id), NODE_ID_SEPARATOR) + 1;
    }

    /**
     * 获取树中所有ID
     *
     * @return 节点ID集合
     */
    public Set<String> allIds() {
        return schemas.keySet();
    }

    /**
     * 判断节点ID对应的节点是否存在
     *
     * @param id 节点ID
     * @return true=存在 false=不存在
     */
    public boolean containsId(String id) {
        return allIds().contains(id);
    }

    /**
     * 获取给定父节点ID之下一层的所有子节点ID
     *
     * @param parentId 父节点ID
     * @return 子节点ID集合
     */
    public Set<String> directChildIdsUnder(String parentId) {
        if (StringUtils.isBlank(parentId)) {
            return allRootIds();
        }

        return schemas.values().stream()
                .filter(value -> value.contains(parentId + NODE_ID_SEPARATOR))
                .map(value -> {
                    String[] split = value.split(parentId + NODE_ID_SEPARATOR);
                    return split[1].split(NODE_ID_SEPARATOR)[0];
                })
                .collect(ImmutableSet.toImmutableSet());
    }

    /**
     * 获取根节点下一层所有子节点ID
     *
     * @return 子节点ID集合
     */
    public Set<String> allRootIds() {
        return schemas.values().stream()
                .filter(value -> !value.contains(NODE_ID_SEPARATOR))
                .collect(ImmutableSet.toImmutableSet());
    }

    /**
     * 获取给定节点的所有兄弟节点ID
     *
     * @param id 节点ID
     * @return 兄弟节点ID集合
     */
    public Set<String> siblingIdsOf(String id) {
        requireNotBlank(id, "Node ID must not be null.");

        if (isRoot(id)) {
            return allRootIds().stream()
                    .filter(aId -> !Objects.equals(aId, id))
                    .collect(ImmutableSet.toImmutableSet());
        }

        return directChildIdsUnder(parentIdOf(id)).stream()
                .filter(aId -> !Objects.equals(aId, id))
                .collect(ImmutableSet.toImmutableSet());
    }

    /**
     * 判断给定节点ID所对应节点是否为根节点
     *
     * @param id 节点ID
     * @return true=是 false=否
     */
    public boolean isRoot(String id) {
        requireNotBlank(id, "Node ID must not be null.");

        return !schemaOf(id).contains(NODE_ID_SEPARATOR);
    }

    /**
     * 获取给定节点ID对应节点的父节点ID
     *
     * @param id 节点ID
     * @return 父节点ID，若该节点没有父节点则返回null
     */
    private String parentIdOf(String id) {
        String[] ids = schemaOf(id).split(NODE_ID_SEPARATOR);
        if (ids.length <= 1) {
            return null;
        }
        return ids[ids.length - 2];
    }

    /**
     * 获取给定节点ID对应节点的所有子节点ID，包括该节点
     *
     * @param id 节点ID
     * @return 子节点ID集合
     */
    public Set<String> withAllChildIdsOf(String id) {
        requireNotBlank(id, "Node ID must not be null.");

        return schemas.entrySet().stream()
                .filter(entry -> entry.getValue().contains(id))
                .map(Map.Entry::getKey)
                .collect(ImmutableSet.toImmutableSet());
    }

    /**
     * 获取给定节点ID对应节点的所有子节点ID，不包括该节点
     *
     * @param id 节点ID
     * @return 子节点ID集合
     */
    public Set<String> allChildIdsOf(String id) {
        requireNotBlank(id, "Node ID must not be null.");

        return schemas.entrySet().stream()
                .filter(entry -> entry.getValue().contains(id) && !entry.getKey().equals(id))
                .map(Map.Entry::getKey)
                .collect(ImmutableSet.toImmutableSet());
    }

    /**
     * 获取给定节点ID对应节点的所有父节点ID，包括该节点
     *
     * @param id 节点ID
     * @return 父节点ID集合
     */
    public Set<String> withAllParentIdsOf(String id) {
        requireNotBlank(id, "Node ID must not be null.");

        return Set.of(schemaOf(id).split(NODE_ID_SEPARATOR));
    }

    /**
     * 获取给定节点ID对应节点的所有父节点ID，不包括该节点
     *
     * @param id 节点ID
     * @return 父节点ID集合
     */
    public Set<String> allParentIdsOf(String id) {
        requireNotBlank(id, "Node ID must not be null.");

        return Arrays.stream(schemaOf(id).split(NODE_ID_SEPARATOR))
                .filter(aId -> !Objects.equals(aId, id))
                .collect(ImmutableSet.toImmutableSet());
    }

    /**
     * 获取所有节点全名，并组织成映射<br>
     * 将映射集合中的值（从根节点到该节点的完整路径）中的每个节点ID转换为节点名
     *
     * @param allNames 节点ID-节点名 映射
     * @return 节点全名映射，节点ID-从根节点到该节点的完整路径，由节点名构成
     */
    public Map<String, String> fullNames(Map<String, String> allNames) {
        requireNonNull(allNames, "Provided names must not be null.");

        return this.schemas.entrySet().stream()
                .map(entry -> {
                    String names = Arrays.stream(entry.getValue().split(NODE_ID_SEPARATOR))
                            .map(groupId -> {
                                String name = allNames.get(groupId);
                                if (isBlank(name)) {
                                    throw new RuntimeException("No name found for id[" + groupId + "].");
                                }
                                return name;
                            })
                            .collect(joining(NODE_ID_SEPARATOR));
                    return Maps.immutableEntry(entry.getKey(), names);
                })
                .collect(ImmutableMap.toImmutableMap(Map.Entry::getKey, Map.Entry::getValue));
    }

}
