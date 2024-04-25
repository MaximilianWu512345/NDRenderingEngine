import java.lang.reflect.Field;

public abstract class DataField<T> {
   
   public T owner;
   
   public DataField(T owner) {
      this.owner = owner;
   }
   
   public T getOwner() {
      return owner;
   }
   
   public abstract void setValue(T value);
   
   public abstract String getValue();
   
   public abstract String getName();
}