package sla.negotiation.model;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.log4j.Logger;

import sla.negotiation.model.Offer.OfferState;
import sla.negotiation.mqtt.Publisher;
import sla.negotiation.mqtt.Subscriber;
import sla.negotiation.strategy.Concession;
import sla.negotiation.strategy.Mixed;
import sla.negotiation.strategy.NegotiationStrategy;
import sla.negotiation.strategy.TradeOff;
import sla.negotiation.util.UtilityFunction;



/**
 * abstract class of negotiation entity
 * 
 * @author Fan
 *
 */
public abstract class Participant implements Negotiation{

	private String role;
	private String name;
	private String location;
	
	private Offer proposal;//offer ready to be send to counterparts
	private Offer counterOffer;//counter offer received from counterparts
	
	private Agreement agreement;
	
	private int currentNegotiationRound;
	private int maxNegotiationRound;

	private  boolean stopNegotiation;
	
	private List<QOS> ReservedQoSList;//bottom line
	private List<QOS> PreferredQoSList;
	
	private double reservedUtilityValue;
	private double preferredUtilityValue;
	

	public boolean success = false;
	
	private Mixed mixed;
	
	private Publisher publisher;
	private Subscriber subscriber;
	
	public int k_concession;
	public int k_tradeoff; 
	
	//final static Logger logger = Logger.getLogger(Participant.class);
	static final Logger logger = Logger.getLogger("debugLogger");
	static final Logger resultLog = Logger.getLogger("reportsLogger");
	
	public Participant() {
		
	}
	
	/**
	 * 
	 * @param name
	 * @param role
	 * @param location
	 */
	public Participant(String name, String role, String location, int maxNegotiationRound, List<QOS> ReservedQoSList, List<QOS> PreferredQoSList) {
	      //System.out.println("Constructing an participant...");
	      this.name = name;
	      this.role = role;
	      this.location = location;
	      this.stopNegotiation = false;
	      this.currentNegotiationRound = 0;
	      this.maxNegotiationRound = 20;
	      this.proposal = null;
	      this.counterOffer = null;
		  this.PreferredQoSList = PreferredQoSList;
	      //this.PreferredQoSList = new ArrayList<QOS>(PreferredQoSList);
		  this.ReservedQoSList = ReservedQoSList;
	    
	}
	
	
	public Publisher getPublisher() {
		return publisher;
	}

	public void setPublisher(Publisher publisher) {
		this.publisher = publisher;
	}

	public Subscriber getSubscriber() {
		return subscriber;
	}

	public void setSubscriber(Subscriber subscriber) {
		this.subscriber = subscriber;
	}

	public String getLocation() {
		return location;
	}

	public void setLocation(String location) {
		this.location = location;
	}

	public String getRole() {
		return role;
	}
	public void setRole(String role) {
		this.role = role;
	}
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public List<QOS> getReservedQoSList() {
		return ReservedQoSList;
	}
	public void setReservedQoSList(List<QOS> reservedQoSList) {
		ReservedQoSList = reservedQoSList;
	}
	public List<QOS> getPreferredQoSList() {
		return PreferredQoSList;
	}
	public void setPreferredQoSList(List<QOS> preferredQoSList) {
		PreferredQoSList = preferredQoSList;
	}
	public double getReservedUtilityValue() {
		return reservedUtilityValue;
	}
	public void setReservedUtilityValue(double reservedUtilityValue) {
		this.reservedUtilityValue = reservedUtilityValue;
	}
	public double getPreferredUtilityValue() {
		return preferredUtilityValue;
	}
	public void setPreferredUtilityValue(double preferredUtilityValue) {
		this.preferredUtilityValue = preferredUtilityValue;
	}
	
	public Offer getProposal() {
		return proposal;
	}


	public void setProposal(Offer proposal) {
		this.proposal = proposal;
	}


	public Offer getCounterOffer() {
		return counterOffer;
	}


	public void setCounterOffer(Offer counterOffer) {
		this.counterOffer = counterOffer;
	}


	public Agreement getAgreement() {
		return agreement;
	}


	public void setAgreement(Agreement agreement) {
		this.agreement = agreement;
	}


	public int getCurrentNegotiationRound() {
		return currentNegotiationRound;
	}


	public void setCurrentNegotiationRound(int currentNegotiationRound) {
		this.currentNegotiationRound = currentNegotiationRound;
	}

	public int getMaxNegotiationRound() {
		return maxNegotiationRound;
	}

	public void setMaxNegotiationRound(int maxNegotiationRound) {
		this.maxNegotiationRound = maxNegotiationRound;
	}

	public boolean isStopNegotiation() {
		return stopNegotiation;
	}

	public void setStopNegotiation(boolean stopNegotiation) {
		this.stopNegotiation = stopNegotiation;
	}

	
	/*
	 * (non-Javadoc)
	 * @see sla.negotiation.model.Negotiation#computeTotalUtilityValue(java.util.List)
	 */
	@Override
	public double computeTotalUtilityValue(List<QOS> QOSList) {

		UtilityFunction u = new UtilityFunction(QOSList);
		return u.computeUtilityFunction();
	}
	
	/*
	 * (non-Javadoc)
	 * @see sla.negotiation.model.Negotiation#offerValidation(sla.negotiation.model.Offer)
	 * 
	 * Offer validation for the last round, bottom line violation?
	 * true: bottom line satisfied
	 */
	@Override
	public boolean offerValidation(Offer receivedOffer) {
		//satisfiedQOSMap.clear();
		Map<QOS, Double> violatedQOSMap = new HashMap<QOS, Double>();
		List<QOS> receivedQOSList = receivedOffer.getQosList();
		for(int i = 0; i < receivedQOSList.size(); i++){
			double receivedValue = receivedQOSList.get(i).getValue();
			double reservedValue = ReservedQoSList.get(i).getValue();
			
			if((receivedValue > reservedValue) && (!ReservedQoSList.get(i).getPositive())){
				violatedQOSMap.put(ReservedQoSList.get(i),receivedQOSList.get(i).getValue());
			}
			
			if((receivedValue < reservedValue) && ReservedQoSList.get(i).getPositive()){
				violatedQOSMap.put(ReservedQoSList.get(i),receivedQOSList.get(i).getValue());
			}
		}
		/*tolerance = tolerance / violatedQOSMap.size();
		if(tolerance >= 0.01){
			tolerance = 0.01;
		}*/
		double tolerance = 0.01;
		//System.out.println("Tolerance is: "+ tolerance);
		int counterVio = 0;
		for (Map.Entry<QOS, Double> entry : violatedQOSMap.entrySet()){
			double reservedVal = entry.getKey().getValue()+tolerance;
			double receivedVal = entry.getValue();
			if((receivedVal > reservedVal) && (!entry.getKey().getPositive())){
				violatedQOSMap.put(entry.getKey(),receivedVal);
				counterVio++;
			}
			if((receivedVal < reservedVal) && entry.getKey().getPositive()){
				violatedQOSMap.put(entry.getKey(),receivedVal);
				counterVio++;
			}
		}
		
		if(counterVio != 0){
			return false;
		}
		
		return true;
	}

	public double computeTolerance(){
		double tolerance = 0;
		
		return tolerance;
	}
	
	
	/*
	 * (non-Javadoc)
	 * @see sla.negotiation.model.Negotiation#offerAccept(java.util.List)
	 * 
	 * Check if the received offer satisfied my preferred value
	 * and receive all the qos in that offer that satisfied my requirement
	 * 
	 * true: requirement satisfied
	 */
	@Override
	public boolean offerAccept(Offer receivedOffer) {
		int counter = 0;
		List<QOS> receivedQOSList = new ArrayList<QOS>();
		for(QOS temp:receivedOffer.getQosList()){
			QOS newTemp = temp.clone();
			newTemp.setPositive(ReservedQoSList.get(0).getPositive());
			receivedQOSList.add(newTemp);
		}
		
		UtilityFunction ut = new UtilityFunction(receivedQOSList);
		double receivedUtil = ut.computeUtilityFunction();
		ut = new UtilityFunction(this.PreferredQoSList);
		double lastUtil = ut.computeUtilityFunction();
		
		if(receivedUtil>lastUtil){
			return true;
		}
		
		for(int i = 0; i < receivedQOSList.size(); i++){
			System.out.println("received value: " + receivedQOSList.get(i).getValue());
			System.out.println("expected value: " + PreferredQoSList.get(i).getValue());
			if((receivedQOSList.get(i).getValue() >= PreferredQoSList.get(i).getValue()) && ReservedQoSList.get(i).getPositive()){
				System.out.println(receivedQOSList.get(i).getName() + " satisfied!");
			}
			if((receivedQOSList.get(i).getValue() > PreferredQoSList.get(i).getValue()) && (!ReservedQoSList.get(i).getPositive())){
				System.out.println(receivedQOSList.get(i).getName() + " violated!");
				counter++;
			}
			if((receivedQOSList.get(i).getValue() <= PreferredQoSList.get(i).getValue()) && (!ReservedQoSList.get(i).getPositive())){
				System.out.println(receivedQOSList.get(i).getName() + " satisfied!");
			}
			if((receivedQOSList.get(i).getValue() < PreferredQoSList.get(i).getValue()) && ReservedQoSList.get(i).getPositive()){
				System.out.println(receivedQOSList.get(i).getName() + " violated!");
				counter++;
			}
		}
		System.out.println();

		if(counter != 0){
			return false;
		}
		return true;
	}

	/*
	 * (non-Javadoc)
	 * @see sla.negotiation.model.Negotiation#createOffer(java.lang.String, java.lang.String, sla.negotiation.model.Offer.OfferState)
	 *
	 * Compute qos values to create the offer, the offer state is the expected state of offer to be generated
	 */
	@Override
	public boolean createOffer(String initiatorName, String responderName, OfferState offerState){
		
		//repeat the preferred value 
		if(offerState.equals(OfferState.DEAL)){
			this.proposal = new Offer(initiatorName, responderName, this.currentNegotiationRound, this.PreferredQoSList , new Date(), OfferState.DEAL);
			logDeal(this.proposal);
			return true;
		}
		
		//accept offer. update preferred value to counterpart's proposal
		if(offerState.equals(OfferState.ACCEPTABLE)){
			for(int i = 0; i < PreferredQoSList.size(); i++){
				this.PreferredQoSList.get(i).setValue(this.counterOffer.getQosList().get(i).getValue());
			}
			this.proposal = new Offer(initiatorName, responderName, this.currentNegotiationRound, this.PreferredQoSList , new Date(), OfferState.ACCEPTABLE);
			return true;
		}
		
		//refuse offer, don't update preferred value, copy the counterpart's prolog.info("Hello this is an info message");posal to generate the offer
		if(offerState.equals(OfferState.REJECTED)){
			this.proposal = new Offer(initiatorName, responderName, this.currentNegotiationRound, this.counterOffer.getQosList(), new Date(), OfferState.REJECTED);
			logRejected(this.currentNegotiationRound);
			return true;
		}
		
		//to create advisory offer, compute preferred value with negotiation strategy and counterpart's proposal
		if(offerState.equals(OfferState.ADVISORY) || offerState.equals(OfferState.SOLICITED)){
			//find out the values that is not satisfied my requirement with my preferred value
			Map<String, Double> inputQOSMap = new HashMap<String, Double>();
			for(QOS temp:PreferredQoSList){
				inputQOSMap.put(temp.getName(), temp.getValue());
			}
			
			double tradeoffProb;
			if(this.getName().contains("Consumer")){
				tradeoffProb = 0.2;
			}else{
				tradeoffProb = Math.random(); // -> [0, 1)
				tradeoffProb = tradeoffProb * 0.6;              // scale     -> [0, 0.6)
				tradeoffProb = tradeoffProb + 0.2;              // translate -> [0.2, 0.8)
			}
			
			if(mixed == null){
				System.out.println("This is creating mixed object from ------------>" + this.getName());
				mixed = new Mixed(PreferredQoSList,ReservedQoSList, tradeoffProb);
				k_concession = mixed.getK_concession();
				k_tradeoff = mixed.getK_tradeoff();
				System.out.println("In round " + currentNegotiationRound + " k_concession is " + k_concession + ", k_tradeoff is " + k_tradeoff + " from--------->" + this.getName());
			
			}else{
				k_concession = mixed.getK_concession();
				k_tradeoff = mixed.getK_tradeoff();
				System.out.println("In round " + currentNegotiationRound + " k_concession is " + k_concession + ", k_tradeoff is " + k_tradeoff + " from--------->" + this.getName());
				mixed = new Mixed(PreferredQoSList,ReservedQoSList,k_tradeoff, k_concession, tradeoffProb);
				
			}
			if(mixed.performStrategy()){
				PreferredQoSList = mixed.getUpdatedQOS();
		
				k_concession = mixed.getK_concession();
				k_tradeoff = mixed.getK_tradeoff();
				this.proposal = new Offer(initiatorName, responderName, this.currentNegotiationRound, PreferredQoSList , new Date(), offerState);
			}else{
				this.proposal = new Offer(initiatorName, responderName, this.currentNegotiationRound, PreferredQoSList , new Date(), OfferState.REJECTED);
			}
			
			return true;
		}
		
		
		System.out.println("can't create offer! Unknown offer state!");
		return false;
	}
	
	public void logDeal(Offer offer){
		resultLog.info("Success");
		success = true;
		List<QOS> temp = offer.getQosList();
		for(QOS currentItem: temp){
			logger.info(currentItem.getName() + "=" + currentItem.getValue());
		}
		//logger.info(" negotiated utility value is: " + ut.computeUtilityFunction());
		logger.info("Round Number: " + offer.getId());
		//logger.info("#");
	}
	public void logRejected(int round){
		logger.info("Fail");
		logger.info("Round Number: " + round);
		//logger.info("#");
	}
	
	
	@Override
	public boolean negotiationTerminated(){
		return stopNegotiation;
	}
	
}
