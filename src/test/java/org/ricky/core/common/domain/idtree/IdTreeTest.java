package org.ricky.core.common.domain.idtree;

import org.junit.jupiter.api.Test;
import org.ricky.core.common.domain.idtree.exception.IdNodeLevelOverflowException;
import org.ricky.core.common.domain.idtree.exception.IdNodeNotFoundException;
import org.ricky.core.common.domain.idtree.exception.NodeIdFormatException;

import java.util.ArrayList;
import java.util.Map;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;

/**
 * @author Ricky
 * @version 1.0
 * @date 2024/9/23
 * @className IdTreeTest
 * @desc
 */
public class IdTreeTest {

    @Test
    public void should_create_tree() {
        // Given
        new IdTree(new ArrayList<>(0));
        IdTree idTree = new IdTree("111");

        // Then
        assertEquals(0, idTree.nodeById("111").getChildren().size());
    }

    @Test
    public void should_fail_create_tree_if_id_contain_separator() {
        assertThrows(NodeIdFormatException.class, () -> new IdTree("1/11"));
    }

    @Test
    public void should_add_node() {
        // Given
        IdTree idTree = new IdTree("111");

        // When
        idTree.addNode("111", "222");
        idTree.addNode("111", "333");
        idTree.addNode("222", "444");

        // Then
        assertEquals(2, idTree.nodeById("111").getChildren().size());
        assertEquals(1, idTree.nodeById("222").getChildren().size());
        assertEquals(0, idTree.nodeById("333").getChildren().size());
        assertEquals(0, idTree.nodeById("444").getChildren().size());
    }

    @Test
    public void should_equal() {
        // Given
        IdTree idTree = new IdTree("111");
        idTree.addNode("111", "222");
        idTree.addNode("111", "333");
        idTree.addNode("222", "444");

        IdTree idTree2 = new IdTree("111");
        idTree2.addNode("111", "222");
        idTree2.addNode("111", "333");
        idTree2.addNode("222", "444");

        // Then
        assertEquals(idTree, idTree2);
    }

    @Test
    public void should_fail_add_node_if_id_contains_separator() {
        assertThrows(NodeIdFormatException.class, () -> {
            IdTree idTree = new IdTree("111");
            idTree.addNode("111", "2/22");
        });
    }

    @Test
    public void should_fail_add_node_if_parent_node_not_found() {
        assertThrows(IdNodeNotFoundException.class, () -> {
            IdTree idTree = new IdTree("111");
            idTree.addNode("aaa", "222");
        });
    }

    @Test
    public void should_build_schema() {
        // Given
        IdTree idTree = new IdTree("111");
        idTree.addNode("111", "222");
        idTree.addNode("111", "333");
        idTree.addNode("222", "444");
        idTree.addNode(null, "555");

        // When
        IdTreeHierarchy hierarchy = idTree.buildHierarchy(3);

        // Then
        assertEquals(5, hierarchy.allIds().size());
        assertEquals("111", hierarchy.schemaOf("111"));
        assertEquals("111/222", hierarchy.schemaOf("222"));
        assertEquals("111/333", hierarchy.schemaOf("333"));
        assertEquals("111/222/444", hierarchy.schemaOf("444"));
        assertEquals("555", hierarchy.schemaOf("555"));
    }

    @Test
    public void should_fail_build_schema_if_max_allowed_level_reached() {
        assertThrows(IdNodeLevelOverflowException.class, () -> {
            // Given
            IdTree idTree = new IdTree("111");
            idTree.addNode("111", "222");
            idTree.addNode("111", "333");
            idTree.addNode("222", "444");
            idTree.addNode(null, "555");

            // When
            idTree.buildHierarchy(2);
        });
    }

    @Test
    public void should_remove_node() {
        // Given
        IdTree idTree = new IdTree("111");
        idTree.addNode("111", "222");
        idTree.addNode("111", "333");
        idTree.addNode("222", "444");
        idTree.addNode(null, "555");

        // When
        idTree.removeNode("555");

        // Then
        assertFalse(idTree.buildHierarchy(5).containsId("555"));

        // When
        boolean result = idTree.removeNode("222");

        // Then
        assertTrue(result);
        assertFalse(idTree.buildHierarchy(5).containsId("222"));
        assertFalse(idTree.buildHierarchy(5).containsId("444"));
        assertTrue(idTree.buildHierarchy(5).containsId("111"));
        assertTrue(idTree.buildHierarchy(5).containsId("333"));
        assertEquals(2, idTree.buildHierarchy(5).allIds().size());
    }

    @Test
    public void should_silently_remove_none_exist_node() {
        // Given
        IdTree idTree = new IdTree("111");
        idTree.addNode("111", "222");
        idTree.addNode("111", "333");
        idTree.addNode("222", "444");
        idTree.addNode(null, "555");

        IdTree idTree2 = new IdTree("111");
        idTree2.addNode("111", "222");
        idTree2.addNode("111", "333");
        idTree2.addNode("222", "444");
        idTree2.addNode(null, "555");

        // When
        idTree.removeNode("whatever");

        // Then
        assertEquals(idTree, idTree2);
    }

    @Test
    public void should_merge() {
        // Given
        IdTree idTree = new IdTree("111");
        idTree.addNode("111", "222");

        IdTree idTree2 = new IdTree("aaa");
        idTree2.addNode("aaa", "bbb");

        // When
        idTree.merge(idTree2);

        // Then
        IdTreeHierarchy hierarchy = idTree.buildHierarchy(5);

        assertEquals("111", hierarchy.schemaOf("111"));
        assertEquals("111/222", hierarchy.schemaOf("222"));
        assertEquals("aaa", hierarchy.schemaOf("aaa"));
        assertEquals("aaa/bbb", hierarchy.schemaOf("bbb"));
    }

    @Test
    public void should_merge_and_self_as_victim_if_id_duplicates() {
        // Given
        IdTree idTree = new IdTree("111");
        idTree.addNode("111", "222");

        IdTree idTree2 = new IdTree("aaa");
        idTree2.addNode("aaa", "bbb");
        idTree2.addNode("bbb", "222");

        // When
        idTree.merge(idTree2);

        // Then
        IdTreeHierarchy hierarchy = idTree.buildHierarchy(5);

        assertEquals("111", hierarchy.schemaOf("111"));
        assertEquals("aaa/bbb/222", hierarchy.schemaOf("222"));
        assertEquals("aaa", hierarchy.schemaOf("aaa"));
        assertEquals("aaa/bbb", hierarchy.schemaOf("bbb"));

        assertEquals("aaa", idTree.getNodes().get(0).getId()); // 保证merge时另一棵树的节点放在最前面
    }

    @Test
    public void should_map_node() {
        // Given
        IdTree idTree = new IdTree("111");
        idTree.addNode("111", "222");
        idTree.addNode("111", "333");
        idTree.addNode(null, "444");

        Map<String, String> idMap = Map.of("111", "aaa", "222", "bbb", "333", "ccc", "444", "ddd");

        // When
        IdTree mappedIdTree = idTree.map(idMap);

        // Then
        IdTreeHierarchy hierarchy = mappedIdTree.buildHierarchy(5);
        Set<String> allIds = hierarchy.allIds();

        assertTrue(allIds.containsAll(Set.of("aaa", "bbb", "ccc", "ddd")));
        assertEquals(4, allIds.size());
        assertEquals("aaa", hierarchy.schemaOf("aaa"));
        assertEquals("aaa/bbb", hierarchy.schemaOf("bbb"));
        assertEquals("aaa/ccc", hierarchy.schemaOf("ccc"));
        assertEquals("ddd", hierarchy.schemaOf("ddd"));
    }

    @Test
    public void should_fail_map_node_if_id_map_not_complete() {
        // Given
        IdTree idTree = new IdTree("111");
        idTree.addNode("111", "222");
        idTree.addNode("111", "333");
        idTree.addNode(null, "444");

        Map<String, String> idMap = Map.of("111", "aaa", "222", "bbb", "333", "ccc");

        // When
        assertThrows(RuntimeException.class, () -> idTree.map(idMap));
    }

}
