public class HuffmanTreeNode {
   
   public int frequency;
   public Object value;
   
   public HuffmanTreeNode leftChild;
   
   public HuffmanTreeNode rightChild;
   
   public HuffmanTreeNode(int f, Object v) {
      frequency = f;
      value = v;
   }
   
   public void setChildren(HuffmanTreeNode left, HuffmanTreeNode right) {
      leftChild = left;
      rightChild = right;
   }

   public void addFrequency(int i) {
      frequency += i;
   }
   
   public int getFrequency() {
      return frequency;
   }
   
   public Object getValue() {
      return value;
   }
   
   public HuffmanTreeNode getLeftChild() {
      return leftChild;
   }
   
   public HuffmanTreeNode getRightChild() {
      return rightChild;
   }
   
   public int getChildrenCount() {
      return (leftChild == null ? 0 : 1) + (rightChild == null ? 0 : 1);
   }
}
