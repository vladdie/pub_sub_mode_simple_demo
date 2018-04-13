package sla.negotiation.entity;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.util.List;

import org.eclipse.paho.client.mqttv3.MqttException;

import com.fasterxml.jackson.core.JsonProcessingException;

import sla.negotiation.model.Offer;
import sla.negotiation.model.Participant;
import sla.negotiation.model.QOS;
import sla.negotiation.model.Offer.OfferState;
import sla.negotiation.mqtt.Publisher;
import sla.negotiation.mqtt.Subscriber;

public class ServiceCustomer extends Participant{

	public static Socket s;
	public Publisher publisher;
	public Subscriber subscriber;
	
	public ServiceCustomer() {
		super();
		// TODO Auto-generated constructor stub
	}

	public ServiceCustomer(String name, String role, String location, int maxNegotiationRound,
			List<QOS> ReservedQoSList, List<QOS> PreferredQoSList) {
		super(name, role, location, maxNegotiationRound, ReservedQoSList, PreferredQoSList);
		this.setPublisher(new Publisher(name));
		this.setSubscriber(new Subscriber("toCustomer", this.getName()));
		// TODO Auto-generated constructor stub
	}

	/**
	 * Communication using socket
	 */
	@Override
	public boolean offerReceived() {
		try {
			ObjectInputStream inStream = new ObjectInputStream(s.getInputStream());
			//Offer negotiationOffer = (Offer) inStream.readObject();
			this.setCounterOffer((Offer) inStream.readObject());
			if(this.getCounterOffer() == null){
				System.out.println("Object NULL received! ");
				return false;
			}else{
				//System.out.println("Received counter offer id is: " + this.getCounterOffer().getId());
				return true;
			}
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return false;
	}

	/**
	 * Communication using socket
	 */
	@Override
	public boolean sendOffer(Offer offer) {
			
		if(!this.negotiationTerminated()){
			
			ObjectOutputStream outputStream;
			try {
				//System.out.println("SC sending offer to SP, round " + String.valueOf(this.getCurrentNegotiationRound()) );
				//System.out.println();
				outputStream = new ObjectOutputStream(s.getOutputStream());
				outputStream.writeObject(offer);
				return true;
			} catch (IOException e) {
				e.printStackTrace();
			}
			
		}
		return false;
	}

	@Override
	public boolean sendOfferToBroker(Offer offer) {
		if(this.getPublisher() == null){
			System.out.println(this.getName() + ": publisher is null pointer!");
			this.setPublisher(new Publisher(this.getName()));
		}
		if(!this.negotiationTerminated()){
			try {
				String offerJsonString = offer.toJson();
				this.getPublisher().publishMessage(offerJsonString, "toProvider");
				System.out.println(this.getName() + " sent the offer to MQTT broker!");
				return true;
			} catch (JsonProcessingException | MqttException e) {
				e.printStackTrace();
			}
		}
		return false;
	}

	@Override
	public  boolean receiveOfferFromBroker() {
		
		if(this.getSubscriber() == null){
			System.out.println(this.getName() + ": subscriber is null pointer!");
			this.setSubscriber(new Subscriber("toCustomer", this.getName()));
		}
		
		while(this.getSubscriber().getQueue().isEmpty()){
			//System.out.println("Waiting for customer retrieving counteroffer...");
			try {
				Thread.sleep(5);
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		String offerString = this.getSubscriber().getQueue().element();
		Offer offer = new Offer();
		offer = offer.parseJson(offerString);
		this.getSubscriber().getQueue().remove();
		this.setCounterOffer(offer);
		return true;

	}
	
	
	/*
	 * (non-Javadoc)
	 * @see sla.negotiation.model.Negotiation#performNegotiation()
	 * 
	 * generate the counter offer based on the negotiation strategy
	 * 
	 * Communication with MQTT
	 */
	@Override
	public boolean performNegotiation(){
		
		if(this.receiveOfferFromBroker()){
			//situation 1: deal verification, no increment on the round number
			if(this.getCounterOffer().getOfferState().equals(OfferState.DEAL)){
				if(!this.offerAccept(this.getCounterOffer())){//make sure the offer is not modified by counterpart
					return false;
				}
				System.out.println(this.getName() + ": deal verification received from counterpart " + this.getCounterOffer().getInitiatorName()); 
				/*if(this.createAgreement(this.getName(), this.counterOffer.getInitiatorName(), this.counterOffer, AgreementState.OBSERVED)){
					this.sendAgreement(this.agreement);
					System.out.println(this.getName() + ": Created SLA is sent to " + this.counterOffer.getInitiatorName());
					return true;
				}*/
				this.setStopNegotiation(true);
				return true;
			}
			
			//situation 2: offer accepted by counterpart, no increment on the round number
			if(this.getCounterOffer().getOfferState().equals(OfferState.ACCEPTABLE)){
				if(!this.offerAccept(this.getCounterOffer())){//make sure the offer is not modified by counterpart
					this.setStopNegotiation(true);
					return false;
				}
				System.out.println(this.getName() + ": Proposed offer is accepted by counterpart " + this.getCounterOffer().getInitiatorName() 
				+ " in round: "+ this.getCounterOffer().getId()); 
				if(this.createOffer(this.getName(), this.getCounterOffer().getInitiatorName(), OfferState.DEAL)){
					this.sendOfferToBroker(this.getProposal());
					System.out.println(this.getName() + ": Deal verification is sent to " + this.getCounterOffer().getInitiatorName());
					this.setStopNegotiation(true);
					return true;
				}
			}
			
			//situation 3: offer rejected by counterpart, reset current round number
			if(this.getCounterOffer().getOfferState().equals(OfferState.REJECTED)){
				System.out.println(this.getName() + ": Proposed offer is rejected by counterpart" + this.getCounterOffer().getInitiatorName() 
				+ " in round: "+ this.getCounterOffer().getId()); 
				this.setStopNegotiation(true);
				//this.setCurrentNegotiationRound(0);
				return false;
			}
			
			//situation 4: solicited, increase round number
			if(this.getCounterOffer().getOfferState().equals(OfferState.SOLICITED)){
				System.out.println("\n !!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!");
				System.out.println("This is the last proposal from " + this.getCounterOffer().getInitiatorName());
				int round = this.getCurrentNegotiationRound() + 1;
				this.setCurrentNegotiationRound(round);
				//check if the last offer violated my bottomline?
				if(this.offerAccept(this.getCounterOffer())){
					if(this.createOffer(this.getName(), this.getCounterOffer().getInitiatorName(), OfferState.ACCEPTABLE)){
						this.sendOfferToBroker(this.getProposal());
						System.out.println(this.getName() + ": Offer from " + this.getCounterOffer().getInitiatorName() + " is acceptable, waiting for deal verification...");
						return true;
					}
				}else{
					if(this.createOffer(this.getName(), this.getCounterOffer().getInitiatorName(), OfferState.REJECTED)){
						this.sendOfferToBroker(this.getProposal());
						System.out.println(this.getName() + ": Offer from " + this.getCounterOffer().getInitiatorName() + " is refused, negotiation terminated.");
						this.setStopNegotiation(true);
						this.setCurrentNegotiationRound(0);
						return true;
					}
				}
				System.out.println("!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!! \n");
			}
			
			//situation 5: advisory
			if(this.getCounterOffer().getOfferState().equals(OfferState.ADVISORY)){
				System.out.println(this.getName() + ": receive advisory offer from " + this.getCounterOffer().getInitiatorName() 
				+ " in round: "+ this.getCounterOffer().getId()); 
				
				if(this.getCurrentNegotiationRound() >= (this.getMaxNegotiationRound() - 1)){
					//this is the last chance for me. give you my bottom line as preferred value
					if(this.offerAccept(this.getCounterOffer())){
						//all the value satisfied my requirement, accept the offer
						if(this.createOffer(this.getName(), this.getCounterOffer().getInitiatorName(), OfferState.ACCEPTABLE)){
							this.sendOfferToBroker(this.getProposal());
							System.out.println(this.getName() + ": Offer from " + this.getCounterOffer().getInitiatorName() + " is acceptable, waiting for deal verification...");
							return true;
						}
					}else{
						int round = this.getCurrentNegotiationRound() + 1;
						this.setCurrentNegotiationRound(round);
						//create solicited offer
						if(this.createOffer(this.getName(), this.getCounterOffer().getInitiatorName(), OfferState.SOLICITED)){
							this.sendOfferToBroker(this.getProposal());
							System.out.println(this.getName() + ": Solicited offer is sent to " + this.getCounterOffer().getInitiatorName()
							+ " in round " + this.getCurrentNegotiationRound());
							return true;
						}
					}
					
				}else{
					//max round not reached. create advisory offer
					//find the values satisfied my requirement
					if(this.offerAccept(this.getCounterOffer())){
						//all the value satisfied my requirement, accept the offer
						if(this.createOffer(this.getName(), this.getCounterOffer().getInitiatorName(), OfferState.ACCEPTABLE)){
							this.sendOfferToBroker(this.getProposal());
							System.out.println(this.getName() + ": Offer from " + this.getCounterOffer().getInitiatorName() + " is acceptable, waiting for deal verification...");
							return true;
						}
					}else{
						int round = this.getCurrentNegotiationRound() + 1;
						this.setCurrentNegotiationRound(round);
						if(this.createOffer(this.getName(), this.getCounterOffer().getInitiatorName(), OfferState.ADVISORY)){
							this.sendOfferToBroker(this.getProposal());
							System.out.println(this.getName() + ":"+this.getProposal().getOfferState()+" offer is sent to " + this.getCounterOffer().getInitiatorName()
							+ " in round " + this.getCurrentNegotiationRound());
							return true;
						}
					}
				}
			}
		}else{
			System.out.println("No msg received from broker! ");
		}
		
		
		System.out.println("Negotiation of round " + getCurrentNegotiationRound() + ". Unknown Reason.");
		return false;
	}
	


}
