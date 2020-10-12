package pentomino.core.devices;

import javax.print.event.PrintJobEvent;
import javax.print.event.PrintJobListener;

public class MyPrintJobListener  implements PrintJobListener{

@Override
public void printDataTransferCompleted(PrintJobEvent pje) {
    System.out.println("Transferecia de datos completada");

}

@Override
public void printJobCanceled(PrintJobEvent pje) {
	System.out.println("Impresion cancelada");

}

@Override
public void printJobCompleted(PrintJobEvent pje) {
	System.out.println("Impresion completada");
}

@Override
public void printJobFailed(PrintJobEvent pje) {
	System.out.println( "No se pudo imprimir");
}

@Override
public void printJobNoMoreEvents(PrintJobEvent pje) {
	System.out.println( "NoMore Events");

}

@Override
public void printJobRequiresAttention(PrintJobEvent pje) {
	System.out.println( "Requiere atencion");

}


}
