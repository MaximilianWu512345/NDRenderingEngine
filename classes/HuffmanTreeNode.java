public class HuffmanTreeNode {
   
   public int frequency;
   public Object value;
   
   public HuffmanTreeNode(int f, Object v) {
      frequency = f;
      value = v;
   }

   public int getFrequency() {
      return frequency;
   }
   
   public Object getValue() {
      return value;
   }
}
