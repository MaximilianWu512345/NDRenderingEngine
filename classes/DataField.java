import java.lang.reflect.Field;

public class DataField<T> {
   
   public T owner;
   
   public String value;
   
   public DataField(T owner) {
      this.owner = owner;
      updateValue();
   }
   
   public T getOwner() {
      return owner;
   }
   
   public void setValue() { };
   
   public void updateValue() { };
   
   public void setValue(String value) { this.value = value; }
   
   public String getValue() { return value; }
   
   public String getName() { return ""; }
   
   public boolean canEdit() { return true; }
}