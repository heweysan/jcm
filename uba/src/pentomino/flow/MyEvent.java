package pentomino.flow;

import java.util.EventObject;

@SuppressWarnings("serial")
public class MyEvent extends EventObject {
  public MyEvent(Object source) {
    super(source);
  }
}