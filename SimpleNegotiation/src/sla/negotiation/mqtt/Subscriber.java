package sla.negotiation.mqtt;


import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.Queue;

import org.eclipse.paho.client.mqttv3.IMqttDeliveryToken;
import org.eclipse.paho.client.mqttv3.MqttCallback;
import org.eclipse.paho.client.mqttv3.MqttClient;
import org.eclipse.paho.client.mqttv3.MqttConnectOptions;
import org.eclipse.paho.client.mqttv3.MqttException;
import org.eclipse.paho.client.mqttv3.MqttMessage;
import org.eclipse.paho.client.mqttv3.MqttSecurityException;

import sla.negotiation.model.Offer;


//import org.eclipse.paho.client.mqttv3.persist.MqttDefaultFilePersistence;

public class Subscriber implements MqttCallback {

	public static final String BROKER_URL = "tcp://localhost:1883";
	private String clientId = "Responder_";
	private String myTopic = "/offerTest/";
	private int qos = 0;
	private MqttClient client_sub;
	//private String receivedMsg;
	private boolean processFinished;
	//private boolean dataArrived;


	private Queue<String> queue;
	
	public Subscriber(String topic, String clientID){
		//MqttDefaultFilePersistence dataStore_sub = new MqttDefaultFilePersistence(); 
        try {
        	clientId = clientId + clientID;
			client_sub = new MqttClient(BROKER_URL, clientId);
			MqttConnectOptions conOpt = new MqttConnectOptions();
	    	conOpt.setCleanSession(true);
	    	client_sub.connect();
			System.out.println("Subscriber onnected to broker: "+BROKER_URL);
			client_sub.subscribe(myTopic + topic, qos);
			client_sub.setCallback(this);
			setProcessFinished(true);
			queue = new LinkedList<String>();
			//setDataArrived(false);
		} catch (MqttException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
    	
	}
    
    
/*	public boolean isDataArrived() {
		return dataArrived;
	}


	public void setDataArrived(boolean dataArrived) {
		this.dataArrived = dataArrived;
	}*/
	
	


	@Override
	public void connectionLost(Throwable cause) {
		// TODO Auto-generated method stub
		System.out.println("Subscriber: Connection lost!");
	}

	@Override
	public void messageArrived(String topic, MqttMessage message) throws Exception {
		//System.out.println("Subscriber: data arrived!");
		String receivedMsg;
		receivedMsg = new String(message.getPayload());
		//System.out.println(receivedMsg);
		queue.add(receivedMsg);
		//System.out.println("Subscriber: data added to the queue!");	
	}

	@Override
	public void deliveryComplete(IMqttDeliveryToken token) {
		// TODO Auto-generated method stub
		 System.out.println("deliveryComplete:" + token.getMessageId());
	}
	
	public void closeConnection(){
		/*if(!processFinished){
			try {
				wait();
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}*/
	
		try {
			System.out.println(clientId + ": Close connection to MQTT broker " );
			client_sub.disconnect();
		} catch (MqttException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}



	public boolean isProcessFinished() {
		return processFinished;
	}


	public void setProcessFinished(boolean processFinished) {
		this.processFinished = processFinished;
	}
	
	public Queue<String> getQueue() {
		return queue;
	}


	public void setQueue(Queue<String> queue) {
		this.queue = queue;
	}

	

}
