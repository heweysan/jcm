package pentomino.flow;

import java.util.EventListener;

public interface MyEventListener extends EventListener {
	  public void myEventOccurred(MyEvent evt);
	}