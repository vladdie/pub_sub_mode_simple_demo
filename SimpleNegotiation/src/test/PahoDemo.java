package test;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.eclipse.paho.client.mqttv3.MqttClient;
import org.eclipse.paho.client.mqttv3.MqttException;
import org.eclipse.paho.client.mqttv3.MqttMessage;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import sla.negotiation.entity.ServiceCustomer;
import sla.negotiation.model.Offer;
import sla.negotiation.model.Participant;
import sla.negotiation.model.QOS;
import sla.negotiation.model.Offer.OfferState;
import sla.negotiation.mqtt.Publisher;
import sla.negotiation.mqtt.Subscriber;

public class PahoDemo {

	MqttClient client;
	
	public PahoDemo() {}

    public static void main(String[] args) throws MqttException, IOException {
      //new PahoDemo().doDemo();
    	
    	List<QOS> SCReservedQoSList = new ArrayList<QOS>();
		SCReservedQoSList.add(new QOS("availability", 0.80, true, 0.1));
		SCReservedQoSList.add(new QOS("reliability", 0.80, true, 0.2));
		SCReservedQoSList.add(new QOS("responsiveness", 0.50, true, 0.1)); //min requirement
		SCReservedQoSList.add(new QOS("security", 0.75, true, 0.3));
		SCReservedQoSList.add(new QOS("elasticity", 0.85, true, 0.3));
		List<QOS> SCPreferredQoSList = new ArrayList<QOS>();
		SCPreferredQoSList.add(new QOS("availability", 0.95, true, 0.1));
		SCPreferredQoSList.add(new QOS("reliability", 0.95, true, 0.2));
		SCPreferredQoSList.add(new QOS("responsiveness", 0.90, true, 0.1));
		SCPreferredQoSList.add(new QOS("security", 0.85, true, 0.3));
		SCPreferredQoSList.add(new QOS("elasticity", 0.95, true, 0.3));
		Participant sc = new ServiceCustomer("Consumer", "Negotiation Initiator", "localhost", 20, SCReservedQoSList, SCPreferredQoSList);
		
		//int negotiationRound = 0;
		sc.setCurrentNegotiationRound(1);
		sc.setProposal(new Offer("SC", "SP", sc.getCurrentNegotiationRound() , sc.getPreferredQoSList() , new Date(), OfferState.ADVISORY));
		
		//String offerJsonString = sc.getProposal().toJson();
		
		if(sc.sendOfferToBroker(sc.getProposal())){
			System.out.println("SC: Initial Offer_" + sc.getCurrentNegotiationRound() + " is sent to MQTT Broker.");
		}else{
			System.out.println("Fail to send the message to MQTT Broker!");
		}
		/*Publisher pb = new Publisher(sc.getName());
		Subscriber sb = new Subscriber("toTest", sc.getName());
    	pb.publishMessage(offerJsonString, "toTest");*/
    	
    	/*try {
			Thread.sleep(3000);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}*/
    	
    	
    	sc.getSubscriber().closeConnection();
    	sc.getPublisher().closeConnection();
    	
    	
    	System.out.println("Finished");
    }

    
	private void doDemo() throws MqttException, JsonProcessingException {
		
		List<QOS> SCReservedQoSList = new ArrayList<QOS>();
		SCReservedQoSList.add(new QOS("availability", 0.80, true, 0.1));
		SCReservedQoSList.add(new QOS("reliability", 0.80, true, 0.2));
		SCReservedQoSList.add(new QOS("responsiveness", 0.50, true, 0.1)); //min requirement
		SCReservedQoSList.add(new QOS("security", 0.75, true, 0.3));
		SCReservedQoSList.add(new QOS("elasticity", 0.85, true, 0.3));
		List<QOS> SCPreferredQoSList = new ArrayList<QOS>();
		SCPreferredQoSList.add(new QOS("availability", 0.95, true, 0.1));
		SCPreferredQoSList.add(new QOS("reliability", 0.95, true, 0.2));
		SCPreferredQoSList.add(new QOS("responsiveness", 0.90, true, 0.1));
		SCPreferredQoSList.add(new QOS("security", 0.85, true, 0.3));
		SCPreferredQoSList.add(new QOS("elasticity", 0.95, true, 0.3));
		Participant sc = new ServiceCustomer("Consumer", "Negotiation Initiator", "localhost", 20, SCReservedQoSList, SCPreferredQoSList);
		
		//int negotiationRound = 0;
		sc.setCurrentNegotiationRound(1);
		sc.setProposal(new Offer("SC", "SP", sc.getCurrentNegotiationRound() , sc.getPreferredQoSList() , new Date(), OfferState.ADVISORY));
		
		String offerJsonString = sc.getProposal().toJson();
		
		// TODO Auto-generated method stub
		client = new MqttClient("tcp://localhost:1883", "pahomqttpublish1");
		client.connect();
		MqttMessage message = new MqttMessage();
		message.setPayload(offerJsonString.getBytes());
		client.publish("/test", message);
		client.disconnect();
		System.out.println("Finished");
	}
	
}
