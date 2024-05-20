import java.lang.reflect.Field;

public class DataField<A, B> {

   public A parent;
   
   public B owner;
   
   public String value;
   
   public DataField(A parent, B owner) {
      this.parent = parent;
      this.owner = owner;
      updateValue();
   }
   
   public A getParent() {
      return parent;
   }
   
   public B getOwner() {
      return owner;
   }
   
   public void setValue() { };
   
   public void updateValue() { };
   
   public void setValue(String value) { this.value = value; }
   
   public String getValue() { return value; }
   
   public String getName() { return ""; }
   
   public boolean canEdit() { return true; }
}