import java.util.ArrayList;
import java.util.Comparator;

public class HuffmanTree<anyType> {

   public HuffmanTreeNode root;

   public String encodedString;
   
   public HuffmanTree(ArrayList<HuffmanTreeNode> nodes, anyType[] o) {
      sort(nodes);
      initialize(nodes);
      encode(o);
   }
   
   public HuffmanTree(ArrayList<HuffmanTreeNode> nodes, ArrayList<anyType> o) {
      sort(nodes);
      initialize(nodes);
      encode(o);
   }
   
   public void sort(ArrayList<HuffmanTreeNode> nodes) {
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
   }
   
   public void initialize(ArrayList<HuffmanTreeNode> nodes) {
      if (nodes == null || nodes.size() == 0)
         return;
      while (nodes.size() > 0) {
         HuffmanTreeNode one = root != null ? root : nodes.remove(nodes.size() - 1);
         HuffmanTreeNode two = nodes.remove(nodes.size() - 1);
         if (one.getFrequency() > two.getFrequency()) {
            HuffmanTreeNode temp = two;
            two = one;
            one = temp;
         }
         HuffmanTreeNode internalNode = new HuffmanTreeNode(one.getFrequency() + two.getFrequency(), null);
         internalNode.setChildren(one, two);
         root = internalNode;
      }
   }
   
   public Object[] GetNodeAndChildren(HuffmanTreeNode node, anyType match, String code) {
      if (node == null || node.getLeftChild() == null)
         return null;
      if (Match((anyType)node.getLeftChild().getValue(), match))
         return new Object[] { node.getLeftChild(), code + "0" };
      if (node.getRightChild() == null)
         return null;
      if (Match((anyType)node.getRightChild().getValue(), match))
         return new Object[] { node.getRightChild(), code + "1" };
      Object[] left = GetNodeAndChildren(node.getLeftChild(), match, code + "0");
      if (left != null)
         return left;
      Object[] right = GetNodeAndChildren(node.getRightChild(), match, code + "1");
      if (right != null)
         return right;
      return null;
   }
   
   public boolean Match(anyType one, anyType two) {
      return one != null && two != null && (one == two || one.equals(two));
   }
   
   public String getEncode(anyType x) {
      Object[] match = GetNodeAndChildren(root, x, "");
      if (match != null) {
         return (String)match[1];
      }
      return "";
   }
   
   public void encode(ArrayList<anyType> list) {
      encodedString = "";
      for (int i = 0; i < list.size(); i++) {
         encodedString += getEncode(list.get(i));
         if (i != list.size() - 1)
            encodedString += " ";
      }
   }
   
   public void encode(anyType[] list) {
      encodedString = "";
      for (int i = 0; i < list.length; i++) {
         encodedString += getEncode(list[i]);
         if (i != list.length - 1)
            encodedString += " ";
      }
   }
   
   public String decode(String s) {
      String temp = "";
      String[] letters = s.split(" ");
      for (int i = 0; i < letters.length; i++) {
         HuffmanTreeNode current = root;
         for (int x = 0; x < letters[i].length(); x++) {
            String letter = letters[i].substring(x, x + 1);
            if (letter.equalsIgnoreCase("0")) {
               current = current.getLeftChild();
            }
            else if (letter.equalsIgnoreCase("1")) {
               current = current.getRightChild();
            }
         }
         temp += current.getValue();
         if (i != letters.length - 1)
            temp += " ";
      }
      return temp;
   }
   
   public String getEncodedString() {
      return encodedString;
   }
   
   public String getDecodedString() {
      return decode(encodedString);
   }
}