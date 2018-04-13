package sla.negotiation.model;

import java.io.IOException;
import java.io.Serializable;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

public class Offer implements Serializable{
	
	public enum OfferState{
		//ADVISORY state usually contain elements that are currently not specified, need further negotiation
		ADVISORY,
		//SOLICITED requires that counter offers are either in the ACCEPTABLE or the REJECTED state
		SOLICITED,
		ACCEPTABLE,
		REJECTED,
		DEAL
	}
	
	private static final long serialVersionUID = 1L;
	
	private String initiatorName;
	private String responderName;
	private int id; //id indicates that this offer is generated in which negotiation round
	private List<QOS> qosList;
	private Date date;
	private OfferState offerState;
	
	
	public Offer() {
	}


	/**
	 * 
	 * @param initiatorName
	 * @param responderName
	 * @param id
	 * @param qosList
	 * @param date
	 * @param offerState
	 */
	public Offer(String initiatorName, String responderName, int id, List<QOS> qosList, Date date, OfferState offerState) {
		this.initiatorName = initiatorName;
		this.responderName = responderName;
		this.id = id;//id is the current round
		this.qosList = qosList;
		this.date = date;
		this.offerState = offerState;
	}
	
	public String toJson() throws JsonProcessingException{
		ObjectMapper mapper = new ObjectMapper();
		DateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm a z");
		mapper.setDateFormat(df);
		//String jsonInString = mapper.writerWithDefaultPrettyPrinter().writeValueAsString(this);
		String jsonInString = mapper.writeValueAsString(this);
		//System.out.println("offer.json: \n" + jsonInString);
		return jsonInString;
	}
	
	public Offer parseJson(String offerString) {
		ObjectMapper mapper = new ObjectMapper();
		DateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm a z");
		mapper.setDateFormat(df);
		try {
			Offer parsedOffer = new Offer();
			 parsedOffer = mapper.readValue(offerString, Offer.class);
			return parsedOffer;
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return null;
		}
	}
	
	public String getInitiatorName() {
		return initiatorName;
	}
	public void setInitiatorName(String initiatorName) {
		this.initiatorName = initiatorName;
	}
	public String getResponderName() {
		return responderName;
	}
	public void setResponderName(String responderName) {
		this.responderName = responderName;
	}
	public int getId() {
		return id;
	}
	public void setId(int id) {
		this.id = id;
	}
	public List<QOS> getQosList() {
		return qosList;
	}
	public void setQosList(List<QOS> qosList) {
		this.qosList = qosList;
	}
	public Date getDate() {
		return date;
	}
	public void setDate(Date date) {
		this.date = date;
	}


	public OfferState getOfferState() {
		return offerState;
	}


	public void setOfferState(OfferState offerState) {
		this.offerState = offerState;
	}
	
}
