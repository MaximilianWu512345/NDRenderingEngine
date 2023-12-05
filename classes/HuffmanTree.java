import java.util.ArrayList;
import java.util.Comparator;

public class HuffmanTree {

   public HuffmanTreeNode[] nodeTree;

   public String encodedString;
   
   public HuffmanTree(ArrayList<HuffmanTreeNode> nodes) {
      nodes.sort(
         new Comparator<HuffmanTreeNode>() {
            public int compare(HuffmanTreeNode one, HuffmanTreeNode two) {
               if (one.getFrequency() > two.getFrequency())
                  return -1;
               else if (one.getFrequency() < two.getFrequency())
                  return 1;
               return 0;
            }
         });
      initialize(nodes);
   }
   
   public void initialize(ArrayList<HuffmanTreeNode> nodes) {
      nodeTree = new HuffmanTreeNode[nodes.size()];
      for (int i = 0; i < nodes.size(); i++) {
         addNode(nodes.get(i));
      }
   }
   
   public void addNode(HuffmanTreeNode node) {
      int index = 1;
      while (getChildrenCount(index) == 2) {
         index *= 2;
      }
      if (getChildrenCount(index) == 0) {
         
      }
      else if (getChildrenCount(index) == 1) {
         
      }
      else {
      
      }
   }
   
   public int getChildrenCount(int index) {
      int children = 0;
      if (index * 2 < nodeTree.length && nodeTree[index * 2] != null) {
         children++;
      }
      if (index * 2 + 1 < nodeTree.length && nodeTree[index * 2 + 1] != null) {
         children++;
      }
      return children;
   }
   
   public String getString() {
      return encodedString;
   }
   
}