import javax.swing.JTextField;
import java.lang.reflect.Field;
import java.awt.event.ActionListener;
import java.awt.event.ActionEvent;
import java.awt.Color;

public class DataField<FieldType> extends Component implements ActionListener {
   
   protected Text text;
   
   protected JTextField textField;
   
   protected Object owner;
   
   protected Field field;
   
   public DataField(int x, int y, int width, int height, Object owner, Field field) {
      super(x, y, width, height);
      initialize(owner, field);
   }
   
   public void initialize(Object owner, Field field) {
      this.owner = owner;
      this.field = field;
      try {
         text = new Text(0, 0, width / 2, height, Color.black, field.getName());
         textField = new JTextField(field.get(owner).toString(), 1);
         textField.addActionListener(this);
      }
      catch (Exception e) {
         System.out.println(e);
      }
   }
   
   public void actionPerformed(ActionEvent actionEvent) {
      try {
         Object value = textField.getText();
         if (FieldType.class.equals(int.class)) {
            value = Integer.parseInt((String)value);
         }
         else {
            value = (FieldType)value;
         }
         field.set(owner, value);
      }
      catch (Exception e) {
         System.out.println(e);
      }
   }
}