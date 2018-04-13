package sla.negotiation.mqtt;

import org.eclipse.paho.client.mqttv3.MqttClient;
import org.eclipse.paho.client.mqttv3.MqttConnectOptions;
import org.eclipse.paho.client.mqttv3.MqttException;
import org.eclipse.paho.client.mqttv3.MqttMessage;
//import org.eclipse.paho.client.mqttv3.persist.MqttDefaultFilePersistence;

public class Publisher {
	
	 public static final String BROKER_URL = "tcp://localhost:1883";
	 private String myTopic = "/offerTest/";
	 private int qos = 0;
	 private String clientId = "Initiator_";
     private MqttClient client_pub;
     private boolean publishFinished;

     public Publisher(String cliendID){
    		//MqttDefaultFilePersistence dataStore_pub = new MqttDefaultFilePersistence(); 
 		try {
 			clientId = clientId + cliendID;
			client_pub = new MqttClient(BROKER_URL, clientId);
			MqttConnectOptions connOpts = new MqttConnectOptions();
	 		connOpts.setCleanSession(true);
	 		client_pub.connect();
	 		//System.out.println("Publisher connected to broker: "+BROKER_URL);
	 		
		} catch (MqttException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
     }
     
     public boolean publishMessage(String msg, String topic) throws MqttException{
    	setPublishFinished(false);
		MqttMessage message = new MqttMessage();
		message.setPayload(msg.getBytes());
		message.setQos(qos);
		client_pub.publish(myTopic + topic, message);
		//System.out.println("Message published");
		setPublishFinished(true);
		//client_pub.disconnect();
		return true;
     }

	
	public void closeConnection(){
		
		try {
			client_pub.disconnect();
			System.out.println(clientId + ": Close connection to MQTT broker " );
		} catch (MqttException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
     }

	public boolean isPublishFinished() {
		return publishFinished;
	}

	public void setPublishFinished(boolean publishFinished) {
		this.publishFinished = publishFinished;
	}
}
