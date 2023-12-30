//package searchengine.services.parser;
//
//import java.util.ArrayList;
//import java.util.List;
//
//public class TreeNode {
//
//    private final List<TreeNode> children;
//    private TreeNode parent;
//    private final String url;
//
//    public TreeNode(String url) {
//        this.url = url;
//        this.children = new ArrayList<>();
//    }
//
//    public List<TreeNode> getChildren() {
//        return children;
//    }
//
//    public String getUrl() {
//        return url;
//    }
//
//    public TreeNode getParent() {
//        return parent;
//    }
//
//    public TreeNode getRoot() {
//        if (parent == null) {
//            return this;
//        }
//        return parent.getRoot();
//    }
//
//
//    public TreeNode addChild(TreeNode child) {
//        children.add(child);
//        child.parent = this;
//        return this;
//    }
//
//    public int level() {
//        if (parent == null) {
//            return 0;
//        }
//        return parent.level() + 1;
//    }
//}
