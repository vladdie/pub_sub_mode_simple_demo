package sla.negotiation.model;

import java.util.List;

//import sla.negotiation.model.Agreement.AgreementState;
import sla.negotiation.model.Offer.OfferState;



public interface Negotiation {
	
	//public boolean startNegotiation();
	
	public boolean offerReceived();
	
	public boolean performNegotiation();//
	
	public double computeTotalUtilityValue(List<QOS> QOSList);//
	
	public boolean createOffer(String initiatorName, String responderName, OfferState offerState);//
	
	public boolean sendOffer(Offer offer);//socket
	public boolean sendOfferToBroker(Offer offerString);//mqtt
	
	public boolean offerValidation(Offer offer);//
	
	public boolean offerAccept(Offer offer);//socket
	public boolean receiveOfferFromBroker();//mqtt
	
	//public boolean createAgreement(String initiatorName, String responderName, Offer offer, AgreementState agreementState);
	
	//public boolean sendAgreement(Agreement agreement);
	
	public boolean negotiationTerminated();//
	
	//public boolean buildConnection() throws Exception;

}
