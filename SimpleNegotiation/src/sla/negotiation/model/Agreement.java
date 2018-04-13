package sla.negotiation.model;


/**
 * 
 * @author Fan
 *
 */
public class Agreement {
	
	public enum AgreementState{
		
		//PENDING,
		OBSERVED,
		COMPLETE,
		REJECTED,
		//PENDINGANDTERMINATING,
		OBSERVEDANDTERMINATING,
		TERMINATED
	}

	private static final long serialVersionUID = 1L;
	
	private String signature;

	public String getSignature() {
		return signature;
	}

	public void setSignature(String signature) {
		this.signature = signature;
	}

	public static long getSerialversionuid() {
		return serialVersionUID;
	}
	
	public Agreement() {
	}


}
