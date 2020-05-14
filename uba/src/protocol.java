import java.util.Arrays;

import pentomino.common.jcmOperation;

public class protocol extends kermit{
	
	public int jcmId = -1;	
	
	public byte[] jcmMessage = new byte[15];
	public int bill;
	
	byte[] version 	= new byte[50];	
	byte[] recyclerVersion 	= new byte[50];	
	boolean accept	= false;
	boolean rturn 	= false;
	
	//Las denominaciones que recicla cada JCM
	public String recyclerOneA = "";
	public String recyclerOneB = "";
		
	public String recyclerContadores = "";
	
	public jcmOperation currentOpertion = jcmOperation.None;
	
	public JcmContadores contadores = new JcmContadores();
	
	
	/* VARIABLES PARA DISPENSADO */
	int jcmCass1 = 0;
	int jcmCass2 = 0;
	
	
	public byte lastMsg = 0x0;
	//private byte lastProtocol = 0x0;
	public static final byte SYNC 						= (byte) 0xFC;
	
	/* ---------------------------------- */
	/* VALORES JCM CONTROLLER -> ACCEPTOR */
	/* ---------------------------------- */
	
	public static final byte STATUS_REQUEST 			= (byte) 0x11;
	public static final byte EXT_STATUS_REQUEST 		= (byte) 0x1A;  //F0h + 1AH
	public static final byte ACK 						= (byte) 0x50;
	//Operation command
	public static final byte OC_RESET 					= (byte) 0x40;
	public static final byte OC_STACK_1 				= (byte) 0x41;
	public static final byte OC_STACK_2 				= (byte) 0x42;
	public static final byte OC_RETURN 					= (byte) 0x43;
	public static final byte OC_HOLD 					= (byte) 0x44;
	public static final byte OC_WAIT 					= (byte) 0x45;
	public static final byte OC_STACK_3					= (byte) 0x49;
	public static final byte OC_E_PAY_OUT				= (byte) 0x4A; //F0
	public static final byte OC_E_COLLECT				= (byte) 0x4B; //F0 + DATA
	public static final byte OC_E_CLEAR					= (byte) 0x4C; //F0
	public static final byte OC_E_EMERGENCY_STOP		= (byte) 0x4D; //F0
	
	//Setting command
	public static final byte SC_ENABLE_DISABLE_DENOM	= (byte) 0xC0;	// +DATA
	public static final byte SC_SECURITY				= (byte) 0xC1;	// +DATA
	public static final byte SC_COMMUNICATION_MODE		= (byte) 0xC2;	// +DATA
	public static final byte SC_INHIBIT					= (byte) 0xC3;	// +DATA
	public static final byte SC_DIRECTION				= (byte) 0xC4;	// +DATA
	public static final byte SC_OPTIONAL_FUNCTION		= (byte) 0xC5;	// +DATA	
	public static final byte SC_E_RECYCLE_CURRENCY		= (byte) 0xD0;	//F0 +DATA
	public static final byte SC_E_RECYCLE_KEY 			= (byte) 0xD1;	//F0 +DATA
	public static final byte SC_E_RECYCLE_COUNT 		= (byte) 0xD2;	//F0 +DATA
	public static final byte SC_E_RECYCLE_REFILL_MODE	= (byte) 0xD4;	//F0 +DATA
	public static final byte SC_E_CURRENT_COUNT 		= (byte) 0xE2;	//F0 +DATA
	
	//Get unit Information Request
	public static final byte UNIT_INFORMATION_REQUEST	= (byte) 0x92;	// +DATA
		
	//Setting status request
	public static final byte SSR_ENABLE_DISABLE_DENOM	= (byte) 0x80;	
	public static final byte SSR_SECURITY				= (byte) 0x81;
	public static final byte SSR_COMMUNICATION_MODE		= (byte) 0x82;
	public static final byte SSR_INHIBIT				= (byte) 0x83;
	public static final byte SSR_DIRECTION				= (byte) 0x84;
	public static final byte SSR_OPTIONAL_FUNCTION		= (byte) 0x85;
	public static final byte SSR_VERSION				= (byte) 0x88;
	public static final byte SSR_BOOT_VERSION			= (byte) 0x89;
	public static final byte SSR_CURRENCY_ASSIGNS		= (byte) 0x8A;
	
	public static final byte SSR_E_RECYCLE_CURRENCY		= (byte) 0x90;	//F0
	public static final byte SSR_E_RECYCLE_KEY 			= (byte) 0x91;	//F0
	public static final byte SSR_E_RECYCLE_COUNT 		= (byte) 0x92;	//F0
	public static final byte SSR_E_SOFTWARE_VERSION		= (byte) 0x93;	//F0
	public static final byte SSR_E_RECYCLE_REFILL_MODE	= (byte) 0x94;	//F0
	public static final byte SSR_E_TOTAL_COUNT	 		= (byte) 0xA0;	//F0
	public static final byte SSR_E_TOTAL_COUNT_CLEAR 	= (byte) 0xA1;	//F0
	public static final byte SSR_E_CURRENT_COUNT 		= (byte) 0xA2;	//F0
	
	
	
	/* ---------------------------------- */
	/* VALORES JCM ACCEPTOR -> CONTROLLER */
	/* ---------------------------------- */
	//STATUS
	public static final byte SR_IDLING					= (byte) 0x11;
	public static final byte SR_ACCEPTING				= (byte) 0x12;	
	public static final byte SR_ESCROW					= (byte) 0x13;	// +DATA
	public static final byte SR_STACKING				= (byte) 0x14;	// +DATA  	
	public static final byte SR_VEND_VALID				= (byte) 0x15;	
	public static final byte SR_STACKED					= (byte) 0x16;	// +DATA
	public static final byte SR_REJECTING				= (byte) 0x17;	// +DATA
	public static final byte SR_RETURNING				= (byte) 0x18;
	public static final byte SR_HOLDING					= (byte) 0x19;
	public static final byte SR_INHIBIT					= (byte) 0x1A;
	public static final byte SR_INITIALIZE				= (byte) 0x1B;	
	public static final byte SR_PAYING					= (byte) 0x20;
	public static final byte SR_COLLECTING				= (byte) 0x21;
	public static final byte SR_COLLECTED				= (byte) 0x22;	// +DATA
	public static final byte SR_PAY_VALID				= (byte) 0x23;
	public static final byte SR_PAY_STAY				= (byte) 0x24;
	public static final byte SR_RETURN_TO_BOX			= (byte) 0x25;
	public static final byte SR_RETURN_PAY_OUT_NOTE		= (byte) 0x26;	// +DATA
	public static final byte SR_RETURN_ERROR			= (byte) 0x2F;	
	public static final byte SR_E_UNCONNECTED			= (byte) 0x00;
	public static final byte SR_E_NORMAL				= (byte) 0x10;	// +DATA
	public static final byte SR_E_EMPTY					= (byte) 0x11;
	public static final byte SR_E_FULL					= (byte) 0x12;
	public static final byte SR_E_BUSY					= (byte) 0x1F;
	
	//power up status
	public static final byte SR_POWER_UP							= (byte) 0x40;
	public static final byte SR_POWER_UP_WITH_BILL_IN_ACCEPTOR		= (byte) 0x41;
	public static final byte SR_POWER_UP_WITH_BILL_IN_STACKER		= (byte) 0x42;	// +DATA
	
	//error status	
	public static final byte SR_ERR_STACKER_FULL			= (byte) 0x43;
	public static final byte SR_ERR_STACKER_OPEN			= (byte) 0x44;
	public static final byte SR_ERR_JAM_IN_ACCEPTOR			= (byte) 0x45;
	public static final byte SR_ERR_JAM_IN_STACKER			= (byte) 0x46;
	public static final byte SR_ERR_PAUSE					= (byte) 0x47;
	public static final byte SR_ERR_CHEATED					= (byte) 0x48;
	public static final byte SR_ERR_FAILURE					= (byte) 0x49;	// +DATA
	public static final byte SR_ERR_COMMUNICATION_ERROR		= (byte) 0x4A;
	public static final byte SR_ERR_RECYCLER_ERROR			= (byte) 0x4C;	
	public static final byte SR_E_ERR_RECYCLER_JAM			= (byte) 0x40;
	public static final byte SR_E_ERR_DOOR_OPEN				= (byte) 0x41;
	public static final byte SR_E_ERR_MOTOR_ERROR 			= (byte) 0x42;
	public static final byte SR_E_ERR_EEPROM_ERROR			= (byte) 0x43;
	public static final byte SR_E_ERR_PAY_OUT_NOTE_ERROR	= (byte) 0x44;
	public static final byte SR_E_ERR_RECYCLE_BOX_OPEN		= (byte) 0x45;
	public static final byte SR_E_ERR_HARDWARE_ERROR		= (byte) 0x4A;
		
	//poll request
	public static final byte ENQ							= (byte) 0x05;
	
	//response to operation command	
	public static final byte OCR_INVALID_COMMAND			= (byte) 0x48;
	
	//response to setting command
	public static final byte SCR_ENABLE_DENOM				= (byte) 0xC0;	// +DATA
	public static final byte SCR_SECURITY_DENOM				= (byte) 0xC1;	// +DATA
	public static final byte SCR_COMMUNICATION_MODE			= (byte) 0xC2;	// +DATA
	public static final byte SCR_INHIBIT 					= (byte) 0xC3;	// +DATA
	public static final byte SCR_DIRECTION 					= (byte) 0xC4;	// +DATA
	public static final byte SCR_OPTIONAL_FUNCTION 			= (byte) 0xC5;	// +DATA
	public static final byte SCR_E_RECYCLE_CURRENCY 		= (byte) 0xD0;	//F0 +DATA
	public static final byte SCR_E_RECYCLE_KEY				= (byte) 0xD1;	//F0 +DATA
	public static final byte SCR_E_RECYCLE_COUNT 			= (byte) 0xD2;	//F0 +DATA
	public static final byte SCR_E_RECYCLE_REFILL_MODE 		= (byte) 0xD4;	//F0 +DATA
	public static final byte SCR_E_CURRENT_COUNT 			= (byte) 0xE2;	//F0 +DATA
	
	//response to unit information
	public static final byte UNIT_INFORMATION_RESPONSE 		= (byte) 0x92;	// +DATA
		
	//response to setting status
	public static final byte SSRR_ENABLE_DENOM					= (byte) 0x80;	// +DATA
	public static final byte SSRR_SECURITY	 					= (byte) 0x81;	// +DATA
	public static final byte SSRR_COMMUNICATION_MODE 			= (byte) 0x82;	// +DATA
	public static final byte SSRR_INHIBIT			 			= (byte) 0x83;	// +DATA
	public static final byte SSRR_DIRECTION		 				= (byte) 0x84;	// +DATA
	public static final byte SSRR_OPTIONAL_FUNCTION 			= (byte) 0x85;	// +DATA
	public static final byte SSRR_VERSION_INFORMATION			= (byte) 0x88;	// +DATA
	public static final byte SSRR_BOOT_VERSION_INFO 			= (byte) 0x89;	// +DATA
	public static final byte SSRR_DENOMINATION_DATA 			= (byte) 0x8A;	// +DATA		
	public static final byte SSRR_E_RECYCLE_CURRENCY			= (byte) 0x90;	//F0 +DATA
	public static final byte SSRR_E_RECYCLE_KEY_SETTING 		= (byte) 0x91;	//F0 +DATA
	public static final byte SSRR_E_RECYCLE_COUNT		 		= (byte) 0x92;	//F0 +DATA
	public static final byte SSRR_E_RECYCLE_SOFTWARE_VERSION	= (byte) 0x93;	//F0 +DATA
	public static final byte SSRR_E_RECYCLE_REFILL_MODE			= (byte) 0x44;	//F0 +DATA
	public static final byte SSRR_E_TOTAL_COUNT 				= (byte) 0xA0;	//F0 +DATA
	public static final byte SSRR_E_TOTAL_COUNT_CLEAR 			= (byte) 0xA1;	//F0 +DATA
	public static final byte SSRR_E_CURRENT_COUNT 				= (byte) 0xA2;	//F0 +DATA
	
	public static final byte INVALID_COMMAND					= (byte) 0x4B;	
	
	
	protocol (){
		jcmMessage[0]= (byte)0xFC;  //SYNC siempre FC
		jcmMessage[1]= (byte)0x05;  //LNG Data length (total number of bytes from syn through CRC
		jcmMessage[2]= (byte)0x11;  //CMD Command,status
		jcmMessage[3]= (byte)0x27;  //0-250byte: Data required for a command (may be ommited depending on the CMD)
		jcmMessage[4]= (byte)0x56;  //CRC 2 byte Check code of CRC method
	}
	public void receiving(byte[] bty){
		
		byte[] byteArray = Arrays.copyOf(bty, bty[1]);

		if(this.compareCRC(byteArray)){
			//System.out.println("Yes");
			processing(bty);
		}else {
			System.out.println("CRC No");
			return;
		}
	}
	
	void id003_format(byte LNG, byte CMD, byte[] DATA, boolean zeroData ){
		
				
		DATA[0] = SYNC;
		DATA[1] = LNG;
		DATA[2] = CMD;
		
		if(zeroData) {
			DATA[3] = 0;
			DATA[4] = 0;
			DATA[5] = 0;
			DATA[6] = 0;
			DATA[7] = 0;
			DATA[8] = 0;
			DATA[9] = 0;
		}
		int chkres = this.crc_kermit(DATA, (byte)(LNG - 2));
		
		DATA[LNG-2] = (byte) (chkres & 0xFF);
		DATA[LNG-1] = (byte) ((chkres >> 8) & 0xFF);
	}	
	
	void id003_format_ext(byte LNG, byte EXT_CMD, byte UNIT, byte CMD, byte DATA1, byte DATA2,  byte[] DATA){
		
		System.out.println("[" + jcmId + "] protocol id003_format_ext LNG [" + Integer.toHexString(LNG) + "] CMD [" + Integer.toHexString(CMD) + "] " + baitsToString("",DATA));
		
		DATA[0] = SYNC;
		DATA[1] = LNG;
		DATA[2] = EXT_CMD;
		DATA[3] = UNIT;
		DATA[4] = CMD;
		DATA[5] = DATA1;
		DATA[6] = DATA2;
		/*
		if(DATA[7] == 0x0)
			DATA[7] = 0;
		DATA[8] = 0;
		DATA[9] = 0;
		*/
		int chkres = this.crc_kermit(DATA, (byte)(LNG - 2));
		
		DATA[LNG-2] = (byte) (chkres & 0xFF);
		DATA[LNG-1] = (byte) ((chkres >> 8) & 0xFF);
	}
	
	int bill_value(byte _bill_)
	{
		int bll = 0;
        switch(_bill_)
        {
            case 0x61: 
            	bll = 10; // 10 Pesos
            	break;
            case 0x62:
            	bll = 20; // 20 Pesos
            	break;
            case 0x63: 
            	bll = 50; // 50 Pesos
            	break;
            case 0x64:
            	bll = 100; // 100 Pesos
            	break;
            case 0x65: 
            	bll = 200; // 200 Pesos
            	break;
            case 0x66:
            	bll = 500; // 500 Pesos
            	break;
            default:
            	
            	break;
        }
        return bll;
	}
	
    public void processing(byte[] jcmResponse){
    	
    	boolean mostrar = false;
    	//Si es disitnto al ultimo si despliego la info
    	if(lastMsg != jcmResponse[2]) {
    		lastMsg = jcmResponse[2];
    		mostrar = true;
    	}
        switch(jcmResponse[2]){
        
	        case INVALID_COMMAND: // 0x4b Invalid Command
	        	if(mostrar) System.out.println(baitsToString("[" + jcmId + "] protocol processing INVALID COMMAND", jcmResponse));
	        	break;
            case ACK: // 0x50 ACK
            	if(mostrar) System.out.println(baitsToString("[" + jcmId + "] protocol processing ACK", jcmResponse));
            	id003_format((byte)5, STATUS_REQUEST, jcmMessage,true); //STATUS_REQUEST
            	break;
            case SR_IDLING: //0x11 IDLING
            	//System.out.println(baitsToString("[" + jcmId + "] protocol processing IDLING", jcmResponse));            	
            	//id003_format((byte)5, (byte) STATUS_REQUEST, jcmMessage); //STATUS_REQUEST
            	break;
            case SR_ACCEPTING: //0x12 ACCEPTING
            	if(mostrar) System.out.println(baitsToString("[" + jcmId + "] protocol processing ACCEPTING", jcmResponse));
            	id003_format((byte)5, (byte) 0x11, jcmMessage,true); //STATUS_REQUEST
            	break;
            case SR_ESCROW: // 0x13 ESCROW
            	if(mostrar) System.out.println(baitsToString("[" + jcmId + "] protocol processing ESCROW", jcmResponse));
            	bill = bill_value(jcmResponse[3]); //bill = b[3];
            	
            	EventListenerClass.fireMyEvent(new MyEvent("bill"+jcmId));
            	accept = false;
            	rturn = false;
            	id003_format((byte)5, (byte) 0x44, jcmMessage,true); //HOLD
            	//id003_format((byte)5, (byte) 0x41, jcmMessage); //STACK1
            	break;
            case SR_STACKING: //0x14 STACKING +DATA
            	if(mostrar) System.out.println(baitsToString("[" + jcmId + "] protocol processing STACKING", jcmResponse));
            	
            	//Revisamos el status del stacking
            	if(jcmMessage[3] == 0x00)
            		System.out.println("Stacking cash box");
            	if(jcmMessage[3] == 0x01)
            		System.out.println("Stacking RecycleBox 1");
            	if(jcmMessage[3] == 0x02)
            		System.out.println("Stacking RecycleBox 2");
            	
            	id003_format((byte)5, (byte) 0x11, jcmMessage,true); //STATUS_REQUEST
            	break;
            case SR_VEND_VALID: //0x15 VEND_VALID
            	if(mostrar) System.out.println(baitsToString("[" + jcmId + "] protocol processing VEND_VALID", jcmResponse));
            	id003_format((byte)5, ACK, jcmMessage,true); //ACK
            	break;
            case SR_STACKED: // 0x16 STACKED  +DATA
            	if(mostrar) System.out.println(baitsToString("[" + jcmId + "] protocol processing STACKED", jcmResponse));
            	//Actualizacion de contadores de reciclaje
            	id003_format_ext((byte) 0x07, (byte) 0xf0, (byte) 0x20, (byte) 0xA2, (byte) 0x00, (byte) 0x0,jcmMessage);
            	
            	//id003_format((byte)5, (byte) 0x11, jcmMessage,true); //STATUS_REQUEST
            	EventListenerClass.fireMyEvent(new MyEvent("clearbill"+jcmId));
            	break;
            case SR_REJECTING: // 0x17 REJECTING
            	if(mostrar) System.out.println(baitsToString("[" + jcmId + "] protocol processing REJECTING", jcmResponse));
            	
	            	switch(jcmResponse[3]) {
	            	case 0x71:
	            		System.out.println("REJECTING insertion error");
	            		break;
	            	case 0x72:
	            		System.out.println("REJECTING mug error");
	            		break;
	            	case 0x73:
	            		System.out.println("REJECTING Return action due to residual bills at the head part of ACCEPTOR");
	            		break;
	            	default:
	            		System.out.println("REJECTING Unknown error");
	            			break;
	            	}
            	
            	break;
            	
            case SR_RETURNING: //0x18 RETURNING
            	if(mostrar) System.out.println(baitsToString("[" + jcmId + "] protocol processing RETURNING", jcmResponse));
            	id003_format((byte)5, (byte) 0x11, jcmMessage,true); //STATUS_REQUEST
            	EventListenerClass.fireMyEvent(new MyEvent("clearbill"+jcmId));
            	break;
            	
            case SR_HOLDING: // 0x19 HOLDING            	
            	if(mostrar) System.out.println(baitsToString("[" + jcmId + "] protocol processing HOLDING", jcmResponse));
            	//guardamos el billete
            	id003_format((byte)5, (byte) 0x41, jcmMessage,true); //STACK1
            	
            	/*
            	if( (protocol.accept == false) && (protocol.rturn == false) ){
            		id003_format((byte)5, (byte) 0x44, jcmMessage,true); //HOLD
            	}else if(protocol.rturn == true){
            		id003_format((byte)5, (byte) 0x43, jcmMessage,true); //RETURN
            	}else if(protocol.accept == true){
            		id003_format((byte)5, (byte) 0x41, jcmMessage,true); //STACK1
            	}
            	*/
            	accept = false;
            	rturn = false;
            	break;
            	
            case SR_INHIBIT: //0x1A DISABLE (INHIBIT)
            	if(mostrar) System.out.println(baitsToString("[" + jcmId + "] protocol processing DISABLE (INHIBIT)", jcmResponse));
            	
            	if(currentOpertion == jcmOperation.Reset) {
            		
            		//Pedimos que billetes recicla el JCM
            		id003_format_ext((byte) 0x07, (byte) 0xf0, (byte) 0x20, (byte) 0x90, (byte) 0x40, (byte) 0x0, jcmMessage);
            		
            		/*
            		protocol.currentOpertion = jcmOperation.None;
            		            		
            		System.out.println("RE INHIBIT");
    				protocol.jcmMessage[3] = 0x00;
    				id003_format((byte) 0x6, (byte) 0xC3, protocol.jcmMessage, false);
    				*/
            	}
            	if(currentOpertion == jcmOperation.Dispense) {
            		//Validamos si hay que dispensar mas o no            		
            		
            		
            		if(jcmCass2 > 0) {
            			int cuantos2 = jcmCass2;
        				id003_format_ext((byte) 0x9, (byte) 0xf0, (byte) 0x20, (byte) 0x4a, (byte) cuantos2, (byte) 0x2,jcmMessage);
        				jcmCass2 = 0;
            		}
            		else {
            		            		
	            		currentOpertion = jcmOperation.None;
	            		
	            		//Rehabilitamos el aceptador
	            		jcmMessage[3] = 0x00;
	    				id003_format((byte) 0x6, (byte) 0xC3, jcmMessage, false);
            		}
            		
            		
            	}
            	
            	break;
            	
            case SR_INITIALIZE: // 0x1B INITIALIZE
            	if(mostrar) System.out.println(baitsToString("[" + jcmId + "] protocol processing INITIALIZE", jcmResponse));
            	id003_format((byte)5, (byte) 0x88, jcmMessage,true); //SSR_VERSION_REQUEST
            	break;
            	
            case SR_PAYING: // 0x20 PAYING
            	if(mostrar) System.out.println(baitsToString("[" + jcmId + "] protocol processing PAYING", jcmResponse));            	
            	break;
            	
            case SR_COLLECTING: // 0x21 COLLECTING
            	if(mostrar) System.out.println(baitsToString("[" + jcmId + "] protocol processing COLLETING", jcmResponse));                        	
            	break;
            	
            case SR_COLLECTED: // 0x22 COLLECTED + DATA
            	if(mostrar) System.out.println(baitsToString("[" + jcmId + "] protocol processing COLLECTED", jcmResponse));
            	
            	//Revisamos el status del Collected
            	if(jcmMessage[3] == 0x01)
            		System.out.println("Stacking RecycleBox 1");
            	if(jcmMessage[3] == 0x02)
            		System.out.println("Stacking RecycleBox 2");
            	
            	break;
            	
            case SR_PAY_VALID: //0x23 PAY VALID
            	if(mostrar) System.out.println(baitsToString("[" + jcmId + "] protocol processing PAY VALID", jcmResponse)); 
            	id003_format((byte)5, ACK, jcmMessage,true); // ACK
            	break;
            	
            case SR_PAY_STAY: //0x24 PAY STAY
            	if(mostrar) System.out.println(baitsToString("[" + jcmId + "] protocol processing PAY STAY", jcmResponse)); 
            	id003_format((byte)5, STATUS_REQUEST, jcmMessage,true); // STATUS_REQUEST
            	break;
            	
            case SR_RETURN_TO_BOX: //0x25 RETURN TO BOX
            	if(mostrar) System.out.println(baitsToString("[" + jcmId + "] protocol processing RETURN TO BOX", jcmResponse)); 
            	id003_format((byte)5, ACK, jcmMessage,true); // ACK
            	break;
            	
            case SR_RETURN_PAY_OUT_NOTE: //0x26 RETURN PAY OUT NOTE +DATA
             	if(mostrar) System.out.println(baitsToString("[" + jcmId + "] protocol processing RETURN PAY OUT NOTE", jcmResponse)); 
            	id003_format((byte)5, ACK, jcmMessage,true); // ACK
            	break;
            	
            case SR_RETURN_ERROR: //0x2F RETURN ERROR
            	if(mostrar) System.out.println(baitsToString("[" + jcmId + "] protocol processing RETURN TO BOX", jcmResponse)); 
            	id003_format((byte)5, ACK, jcmMessage,true); // ACK
            	break;
            
            case SR_POWER_UP_WITH_BILL_IN_STACKER: //0x42 POWER_UP_WITH_BILL_IN_STACKER / MOTOR_ERROR            	
            	if(mostrar) System.out.println(baitsToString("[" + jcmId + "] protocol processing SR_POWER_UP_WITH_BILL_IN_STACKER", jcmResponse));
            	
            	//TODO: REVISAR SI ES NORMAL O EXTENDED
            	
            	break;
            	
            case SR_ERR_RECYCLER_ERROR: //0x4C  RECYCLER ERROR
            	if(mostrar) System.out.println(baitsToString("[" + jcmId + "] protocol processing SR_ERR_RECYCLER_ERROR", jcmResponse));            	
            	
            	//TODO: REVISAR EL DATA
            	
            	//MAndamos el clear
            	id003_format_ext((byte) 0x9, (byte) 0xf0, (byte) 0x20, (byte) 0x4C, (byte) 0x1, (byte) 0x2,
						jcmMessage);
            	break;
           
            	
            case SR_E_UNCONNECTED: //0x00  UNCONNECTED
            	if(mostrar) System.out.println(baitsToString("[" + jcmId + "] protocol processing SR_E_UNCONNECTED", jcmResponse));            	
            	break;	
            	
            case SR_E_NORMAL: //0x10  SR_E_NORMAL + DATA
            	if(mostrar) System.out.println(baitsToString("[" + jcmId + "] protocol processing SR_E_NORMAL", jcmResponse));            	
            	break;	
            /*  TODO: VALIDAR SI ES EXTENSION	
            case SR_E_EMPTY: //0x11  SR_E_EMPTY
            	if(mostrar) System.out.println(baitsToString("[" + jcmId + "] protocol processing SR_E_EMPTY", jcmResponse));            	
            	break;	
            */	
            case SR_ERR_STACKER_FULL: //0x43 STACKER_FULL
            	if(mostrar) System.out.println(baitsToString("[" + jcmId + "] protocol processing STACKER FULL", jcmResponse));            	
            	break;
            case SR_ERR_STACKER_OPEN: //0x44 SR_ERR_STACKER_OPEN
            	if(mostrar) System.out.println(baitsToString("[" + jcmId + "] protocol processing SR_ERR_STACKER_OPEN", jcmResponse));            	
            	break;
            case SR_ERR_JAM_IN_ACCEPTOR: //0x45 JAM_IN_ACCEPTOR
            	if(mostrar) System.out.println(baitsToString("[" + jcmId + "] protocol processing SR_ERR_JAM_IN_ACCEPTOR", jcmResponse));            	
            	break;
            case SR_ERR_JAM_IN_STACKER: //0x46 SR_ERR_JAM_IN_STACKER
            	if(mostrar) System.out.println(baitsToString("[" + jcmId + "] protocol processing SR_ERR_JAM_IN_STACKER", jcmResponse));            	
            	break;
            	
            case SR_ERR_PAUSE: //0x47 SR_ERR_PAUSE
            	if(mostrar) System.out.println(baitsToString("[" + jcmId + "] protocol processing SR_ERR_PAUSE", jcmResponse));            	
            	break;
            	
            case SR_ERR_CHEATED: //0x48 SR_ERR_CHEATED
            	if(mostrar) System.out.println(baitsToString("[" + jcmId + "] protocol processing SR_ERR_CHEATED", jcmResponse));            	
            	break;	
            	
            case SR_ERR_FAILURE: //0x49 SR_ERR_FAILURE + DATA
            	if(mostrar) System.out.println(baitsToString("[" + jcmId + "] protocol processing SR_ERR_FAILURE", jcmResponse));            	
            	//TODO: Checar el DATA
            	break;	
            	
            case SR_ERR_COMMUNICATION_ERROR: //0x4A SR_ERR_COMMUNICATION_ERROR
            	if(mostrar) System.out.println(baitsToString("[" + jcmId + "] protocol processing SR_ERR_COMMUNICATION_ERROR", jcmResponse));            	
            	//TODO: Checar el DATA
            	break;	           	
            	
            case SCR_ENABLE_DENOM: // 0xC0 SCR_ENABLE_DENOM
            	if(mostrar) System.out.println(baitsToString("[" + jcmId + "] protocol processing SCR_ENABLE_DENOM", jcmResponse));
            	//TODO: Checar el DATA            		
            	id003_format((byte)7, (byte) 0xC1, jcmMessage,true); // CMD_SECURITY_DENOMI
            	break;
            case SCR_SECURITY_DENOM: //0xC1 SCR_SECURITY_DENOM
            	if(mostrar) System.out.println(baitsToString("[" + jcmId + "] protocol processing SCR_SECURITY_DENOM", jcmResponse));
            	//TODO: Checar el DATA            		
            	id003_format((byte)6, (byte) 0xC4, jcmMessage,true); // CMD_DIRECTION
            	break;
            case SCR_COMMUNICATION_MODE: //0xC2 CMD_INHIBIT_ACCEPTOR
            	if(mostrar) System.out.println(baitsToString("[" + jcmId + "] protocol processing CMD INHIBIT ACCEPTOR", jcmResponse));
            	//TODO: Checar el DATA            		
            	id003_format((byte)5, (byte) 0x83, jcmMessage,true); // SSR_INHIBIT_ACCEPTOR
            	break;
            case SCR_INHIBIT: //0xC3 SCR_INHIBIT
            	if(mostrar) System.out.println(baitsToString("[" + jcmId + "] protocol processing CMD SCR_INHIBIT ACCEPTOR", jcmResponse));
            	//TODO: Checar el DATA            		
            	id003_format((byte)5, (byte) 0x83, jcmMessage,true); // SSR_INHIBIT_ACCEPTOR
            	break;
            case SCR_DIRECTION: //0xC4 CMD_DIRECTION
            	if(mostrar) System.out.println(baitsToString("[" + jcmId + "] protocol processing CMD_DIRECTION", jcmResponse));
            	//TODO: Checar el DATA            		
            	id003_format_ext((byte) 0x07, (byte) 0xf0, (byte) 0x20, SSR_E_SOFTWARE_VERSION, (byte) 0x00, (byte) 0x0,
						jcmMessage);
            	//id003_format((byte)5, (byte) 0x50, jcmMessage,true); // ACK
            	break;
            case SCR_OPTIONAL_FUNCTION: //0xC5 CMD_DIRECTION
            	//TODO: Checar el DATA       
            	if(mostrar) System.out.println(baitsToString("[" + jcmId + "] protocol processing SCR_OPTIONAL_FUNCTION", jcmResponse));            	
            	break;            	
            case SSRR_ENABLE_DENOM: //0x80 SSR_EN_DIS_DENOMI
            	//TODO: Checar el DATA
            	id003_format((byte)5, (byte) SSRR_SECURITY, jcmMessage,true); // SSRR_ENABLE_DENOM
            	break;
            case SSRR_SECURITY: // 0x81 SSRR_SECURITY
            	if(mostrar) System.out.println(baitsToString("[" + jcmId + "] protocol processing SSRR_SECURITY", jcmResponse));
            	//TODO: Checar el DATA
            	id003_format((byte)5, (byte) 0x85, jcmMessage,true); // SSR_OPTIONAL_FUNCTION
            	break;         
            case SSRR_COMMUNICATION_MODE: // 0x82 SSRR_COMMUNICATION_MODE
            	//TODO: Checar el DATA            	
            	break;         
            case (byte)SSRR_INHIBIT: //0x83 SSR_INHIBIT_ACCEPTOR
            	//TODO: Checar el DATA
            	if(mostrar) System.out.println(baitsToString("[" + jcmId + "] protocol processing SSR INHIBIT ACCEPTOR", jcmResponse));            	
            	
            	//Checamos que tipo de operacion estamos haciendo.
            	switch(currentOpertion) {
            	
            	case None:
            		id003_format((byte)5, (byte) 0x11, jcmMessage,true); // STATUS_REQUEST
            		break;
            		
            	case Dispense:
            		System.out.println("Procesando Dispense...");      
            		
            		if(jcmCass1 > 0) {
            			int cuantos = jcmCass1;            		
            			id003_format_ext((byte) 0x9, (byte) 0xf0, (byte) 0x20, (byte) 0x4a, (byte) cuantos, (byte) 0x1,jcmMessage);
            			jcmCass1 = 0;
            		}
            		else {
            			if(jcmCass2 > 0) {
            				int cuantos2 = jcmCass2;
            				id003_format_ext((byte) 0x9, (byte) 0xf0, (byte) 0x20, (byte) 0x4a, (byte) cuantos2, (byte) 0x2,jcmMessage);
            				jcmCass2 = 0;
            			}
            		}
            		break;
            	case Reset:
            		System.out.println("Procesando Reset");
            		id003_format((byte)5, (byte) 0x11, jcmMessage,true); // STATUS_REQUEST
            		break;
            	default:
            		id003_format((byte)5, (byte) 0x11, jcmMessage,true); // STATUS_REQUEST
            		break;            	
            	}
            	
            	
            	break;
            case SSRR_DIRECTION: // 0x84 SSRR_DIRECTION
            	if(mostrar) System.out.println(baitsToString("[" + jcmId + "] protocol processing SSRR_DIRECTION", jcmResponse));
            	//TODO: Checar el DATA
            	id003_format((byte)6, (byte) 0xC3, jcmMessage,true); // SSRR_DIRECTION
            	break;
            case SSRR_OPTIONAL_FUNCTION: //0x85 SSRR_OPTIONAL_FUNCTION
            	//TODO: Checar el DATA
            	id003_format((byte)5, (byte) 0x84, jcmMessage,true); // SSR_DIRECTION
            	break;
            case SSRR_VERSION_INFORMATION: //0x88 SSRR_VERSION_INFORMATION  (FIRMWARE)
            	if(mostrar) System.out.println(baitsToString("[" + jcmId + "] protocol processing FIRMWARE", jcmResponse));
            	id003_format((byte)5, (byte) 0x89, jcmMessage,true); // SSR_BOOT_VERSION_REQUEST
            	System.arraycopy(jcmResponse, 3, version, 0,jcmResponse[1]-5);
            	EventListenerClass.fireMyEvent(new MyEvent("version"+jcmId));
            	break;   
            case SSRR_BOOT_VERSION_INFO: // 0x89 SSRR_BOOT_VERSION_INFO
            	//TODO: Checar el DATA   
            	id003_format((byte)5, (byte) 0x8A, jcmMessage,true); // SSR_BILL_TABLE
            	break;
            case SSRR_DENOMINATION_DATA: // 0x8A SSRR_DENOMINATION_DATA
            	//TODO: Checar el DATA   
            	id003_format((byte)7, (byte) 0xC0, jcmMessage,true); // CMD_EN_DIS_DENOMI
            	break;
            	
            case 0x40: // POWER_UP            	
            	if(mostrar) System.out.println(baitsToString("[" + jcmId + "] protocol processing POWER UP", jcmResponse));
            	 id003_format((byte)5, (byte) 0x40, jcmMessage,true); // RESET_      // SAN TODO AQUI: 
            	break;
            case 0x41:  // POWER_UP_WITH_BILL_IN_ACCEPTOR * POWER_UP_WITH_BILL_IN_STACKER            	
            	if(mostrar) System.out.println(baitsToString("[" + jcmId + "] protocol processing POWER_UP_WITH_BILL_IN_ACCEPTOR", jcmResponse));
            	break;
            
            case (byte)0xF0: // ALGUN EXTENDED
            	
            	
            	switch(jcmResponse[4]) {
	            	case (byte)0xA0:
	            		if(mostrar) System.out.println(baitsToString("[" + jcmId + "] protocol processing TOTAL COUNT REQUEST", jcmResponse));
	            	
	            		System.out.println("Total number of the stacked notes in Recycle      [" + jcmResponse[5] + "]");
	            		System.out.println("Total number of the dispensed notes from Recycler [" + jcmResponse[8] + "]");
	            		System.out.println("Total number of the collected notes from Recycler [" + jcmResponse[11] + "]");
	            		id003_format((byte)5, (byte) 0x11, jcmMessage,true); //STATUS_REQUEST
	            	
	            		break;
	            	case (byte)0xA1:
	            		if(mostrar) System.out.println(baitsToString("[" + jcmId + "] protocol processing TOTAL COUNT CLEAR ", jcmResponse));
	            		switch(jcmResponse[5]) {
	            		case 0x0:
	            			System.out.println("Normal End");
	            			break;
	            		case 0x01:
	            			System.out.println("Abormal End");
	            			break;
	            		default:
	            			System.out.println("NA");
	            			break;
	            				
	            		}
	            			
	            		id003_format((byte)5, (byte) 0x11, jcmMessage,true); //STATUS_REQUEST
	            		break;
	            	case (byte)0xA2:
	            		if(mostrar) System.out.println(baitsToString("[" + jcmId + "] protocol processing CURRENT COUNT REQUEST", jcmResponse));
	            		
		            	System.out.println("Rec Count 1 [" + jcmResponse[5] + "]");
	            		System.out.println("Rec Count 2 [" + jcmResponse[7] + "]");
	            		
	            		recyclerContadores = "Rec1 [" + jcmResponse[5] + "] / Rec2 [" + jcmResponse[7] + "]";
	            		contadores.Cass1Available = Byte.toUnsignedInt(jcmResponse[5]);
	            		contadores.Cass2Available = Byte.toUnsignedInt(jcmResponse[7]);
	            	
		            	if(currentOpertion == jcmOperation.Reset) {
		            			            		
		            		currentOpertion = jcmOperation.None;
		            		            		
		            		System.out.println("RE INHIBIT");
		    				jcmMessage[3] = 0x00;
		    				id003_format((byte) 0x6, (byte) 0xC3, jcmMessage, false);
		    				
		            	}
		            	else {
		            		id003_format((byte)5, (byte) 0x11, jcmMessage,true); //STATUS_REQUEST
		            	}	
	            		
	            		EventListenerClass.fireMyEvent(new MyEvent("recyclerContadores"+jcmId));
	            		

	            	break;
	            	case (byte)0x93:
	            		if(mostrar) System.out.println(baitsToString("[" + jcmId + "] protocol processing Recycler Software Version Request", jcmResponse));
            			System.arraycopy(jcmResponse, 5, recyclerVersion, 0,jcmResponse[1]-7);
            			
            			EventListenerClass.fireMyEvent(new MyEvent("recyclerVersion"+jcmId));
            			id003_format((byte)5, (byte) 0x11, jcmMessage,true); //
	            		break;
	            	case (byte)0x90:
	            		if(mostrar) System.out.println(baitsToString("[" + jcmId + "] protocol processing RECYCLE CURRENCY REQUEST", jcmResponse));
	            		
		            	if(currentOpertion == jcmOperation.Reset) {
		            		
		            		id003_format_ext((byte) 0x07, (byte) 0xf0, (byte) 0x20, (byte) 0xA2, (byte) 0x00, (byte) 0x0,jcmMessage);
		            		
		            		
		            		/*
		            		currentOpertion = jcmOperation.None;
		            		            		
		            		System.out.println("RE INHIBIT");
		    				jcmMessage[3] = 0x00;
		    				id003_format((byte) 0x6, (byte) 0xC3, jcmMessage, false);
		    				
		    				*/
		    				
		            	}
		            	else {
		            		
		            		id003_format((byte)5, (byte) 0x11, jcmMessage,true); //STATUS_REQUEST
		            	}
	            	
	            		//0x02:20 0x04:50 0x08:100 0x10:200 0x20:500;
	            		//sacamos que billetes esta reciclando:
	            	
	            		recyclerOneA = hexToDenom(jcmResponse[5]);
	            		contadores.Cass1Denom = Integer.parseInt(recyclerOneA);
	            	
	            		recyclerOneB = hexToDenom(jcmResponse[7]);
	            		contadores.Cass2Denom = Integer.parseInt(recyclerOneB);
	            		
	            		recyclerOneA = "$" + recyclerOneA;
	            		recyclerOneB = "$" + recyclerOneB;
	            		
	            		
	            		EventListenerClass.fireMyEvent(new MyEvent("recyclerBillsA"+jcmId));
	            		break;
	            		
	            	case (byte)0x92:
	            		if(mostrar) System.out.println(baitsToString("[" + jcmId + "] protocol processin Recycle Count Req (+92h)", jcmResponse));
	            		System.out.println("Recycle Box No. 1 [" + jcmResponse[5] + "]");
            			System.out.println("Recycle Box No. 2 [" + jcmResponse[6] + "]");
            			id003_format((byte)5, (byte) 0x11, jcmMessage,true); //STATUS_REQUEST
	            		break;
	            	default:
	            		if(mostrar) System.out.println(baitsToString("[" + jcmId + "] protocol processing Algun Extended", jcmResponse));
	            		id003_format((byte)5, (byte) 0x11, jcmMessage,true); //STATUS_REQUEST
	            		break;
            	}
            	
            	break;	
            case (byte)0x92: // UNIT INFORMATION RESPONSE
            	if(mostrar) System.out.println(baitsToString("[" + jcmId + "] protocol processing UNIT INFORMATION RESPONSE", jcmResponse));
            	//TODO: Checar el DATA   
            	
            	break;	
            	
            	
            default:
            	id003_format((byte)5, (byte) 0x11, jcmMessage,true); // STATUS_REQUEST
            	break;
        }

    }
    
    private String hexToDenom(byte Data) {
    	
    	switch(Data) {
		case (byte)0x02:
			System.out.println("\t Reciclador [20]");
			return "20";			
		case (byte)0x04:
			System.out.println("\t Reciclador [50]");
			return "50";
			
		case (byte)0x08:
			System.out.println("\t Reciclador [100]");
			return "100";
			
		case (byte)0x10:
			System.out.println("\t Reciclador [200]");
			return "200";
			
		case (byte)0x20:
			System.out.println("\t Reciclador [500]");
			return "500";
		
		default:
			return "NA";
    	}
    	
    	
    }
    
    public String baitsToString(String texto, byte[] baits) {
    	String result = texto;
    	
    	int total = baits[1] & 0xFF;
    	int count = 0;
    	System.out.println("Numerito [" + total + "]" );
    	
    	for (byte theByte : baits){
    		result += " [" + Integer.toHexString(theByte) + "] ";
    		count++;
    		if(count >= total)
    			break;
        }
    	return result;
    }
    
	
}
