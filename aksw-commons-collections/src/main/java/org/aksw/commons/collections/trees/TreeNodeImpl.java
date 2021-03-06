package org.aksw.commons.collections.trees;

import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Data structure that pairs a tree with one of its nodes.
 * Useful to keep references to specific nodes in a tree while still allowing traversal.
 *
 * @author raven
 *
 * @param <T>
 */
public class TreeNodeImpl<T>
    implements TreeNode<T>
{
    protected Tree<T> tree;
    protected T node;

    public TreeNodeImpl(Tree<T> tree, T node) {
        super();
        this.tree = tree;
        this.node = node;
    }

    public Tree<T> getTree() {
        return tree;
    }

    public T getNode() {
        return node;
    }

    @Override
    public TreeNode<T> getParent() {
        T tmp = tree.getParent(node);
        TreeNode<T> result = new TreeNodeImpl<>(tree, tmp);
        return result;
    }

    @Override
    public Collection<TreeNode<T>> getChildren() {
        Collection<T> tmp = tree.getChildren(node);
        List<TreeNode<T>> result = tmp.stream().map(child -> new TreeNodeImpl<>(tree, child)).collect(Collectors.toList());
        return result;
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((node == null) ? 0 : node.hashCode());
        result = prime * result + ((tree == null) ? 0 : tree.hashCode());
        return result;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj)
            return true;
        if (obj == null)
            return false;
        if (getClass() != obj.getClass())
            return false;
        TreeNodeImpl<?> other = (TreeNodeImpl<?>) obj;
        if (node == null) {
            if (other.node != null)
                return false;
        } else if (!node.equals(other.node))
            return false;
        if (tree == null) {
            if (other.tree != null)
                return false;
        } else if (!tree.equals(other.tree))
            return false;
        return true;
    }

    @Override
    public String toString() {
        return "TreeNodeImpl [tree=" + tree + ", node=" + node + "]";
    }
}
