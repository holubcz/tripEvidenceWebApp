package cz.holub.myTrips.serviceTools;


public class Status {
	public static final int STATUS_SUCCESFULL= 0;
	public static final int STATUS_EROR= -1;
	private static final String NEW_LINE="\r\n";
	
	int code= STATUS_SUCCESFULL;
	String message;
	String tripId;
	
	
	public Status(int code, String message, String tripId) {
		super();
		this.code = code;
		this.message = message;
		this.tripId = tripId;
	}
	
	public Status() {
		code= STATUS_SUCCESFULL;
		message= new String();
	}
	
	
	public int getCode() {
		return code;
	}
	public void setCode(int code) {
		this.code = code;
	}
	public String getMessage() {
		return message;
	}
	public void setMessage(String message) {
		this.message = message;
	}
	public String getTripId() {
		return tripId;
	}
	public void setTripId(String tripId) {
		this.tripId = tripId;
	}
	
	public void addMessage(String newMessage, boolean newLine) {
		if (this.message == null) {
			this.message= new String(newMessage);
		} else {
			this.message+= (newLine ? NEW_LINE : "") + newMessage;
		}
	}
	public void addMessageToStart(String newMessage, boolean newLine) {
		if (this.message == null) {
			this.message= new String(newMessage);
		} else {
			this.message= newMessage + (newLine ? NEW_LINE : "") + this.message;
		}
	}
}
