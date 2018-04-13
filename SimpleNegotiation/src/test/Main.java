package test;

import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Random;

import org.apache.log4j.Logger;

import sla.negotiation.entity.ServiceCustomer;
import sla.negotiation.entity.ServiceProvider;
import sla.negotiation.model.Offer;
import sla.negotiation.model.Participant;
import sla.negotiation.model.QOS;
import sla.negotiation.util.UtilityFunction;
import sla.negotiation.model.Offer.OfferState;

public class Main4 {
	
	//final static Logger loggerTime = Logger.getLogger(Participant.class);
	static final Logger loggerTime = Logger.getLogger("debugLogger");
	static final Logger resultLog = Logger.getLogger("reportsLogger");
	
	public long responseTime;
	public boolean success = false;
	public int negotiationRound;
	public double consumerUtilityValue = 0;
	public double ProviderUtilityValue = 0;
	public double consumerUtilityDiff = 0;
	public double ProviderUtilityDiff = 0;
	
	public void run() {
		// TODO Auto-generated method stub
		
		Random generator = new Random();
		
		List<QOS> SPReservedQoSList = new ArrayList<QOS>();
		SPReservedQoSList.add(new QOS("availability", 0.99, false, 0.2));
		SPReservedQoSList.add(new QOS("reliability", 0.90, false, 0.3));
		SPReservedQoSList.add(new QOS("responsiveness", 0.70, false, 0.3));//best effort: 0.70
		SPReservedQoSList.add(new QOS("security", 0.90, false, 0.1));
		SPReservedQoSList.add(new QOS("elasticity", 0.99, false, 0.1));
		for(int i = 0; i < SPReservedQoSList.size(); i++){
			System.out.println("SP reserved values--->"+SPReservedQoSList.get(i).getName() + ": " + SPReservedQoSList.get(i).getValue() );
		}
		List<QOS> SPPreferredQoSList = new ArrayList<QOS>();
		SPPreferredQoSList.add(new QOS("availability", 0.84, false, 0.2));
		SPPreferredQoSList.add(new QOS("reliability", 0.75, false, 0.3));
		SPPreferredQoSList.add(new QOS("responsiveness", 0.30, false, 0.3));
		SPPreferredQoSList.add(new QOS("security", 0.80, false, 0.1));
		SPPreferredQoSList.add(new QOS("elasticity", 0.89, false, 0.1));
		for(int i = 0; i < SPPreferredQoSList.size(); i++){
			System.out.println("SP preferred values--->"+SPPreferredQoSList.get(i).getName() + ": " + SPPreferredQoSList.get(i).getValue() );
		}
		Participant sp = new ServiceProvider("Provider_A", "Negotiation Responder", "localhost_5844", 20, SPReservedQoSList, SPPreferredQoSList);
		UtilityFunction ut = new UtilityFunction(SPReservedQoSList);
		double SPReservedUtilityValue = ut.computeUtilityFunction();
		ut = new UtilityFunction(SPPreferredQoSList);
		double SPpreferredUtilityValue = ut.computeUtilityFunction();
	
		loggerTime.info(sp.getName()+" utility value range is: " + SPReservedUtilityValue+"~"+SPpreferredUtilityValue);
		//loggerTime.info(sp.getName()+" reserved utility value is: " + reservedUtilityValue);
		//loggerTime.info(sp.getName()+" preferred utility value is: " + preferredUtilityValue);
		//double middleSPOrg = preferredUtilityValue - reservedUtilityValue;
		//loggerTime.info(sp.getName()+" middle original utility value is: " + middleSPOrg);
		
		List<QOS> SCReservedQoSList = new ArrayList<QOS>();
		SCReservedQoSList.add(new QOS("availability", 0.80, true, 0.1));
		SCReservedQoSList.add(new QOS("reliability", 0.80, true, 0.2));
		SCReservedQoSList.add(new QOS("responsiveness", 0.50, true, 0.1)); //min requirement
		SCReservedQoSList.add(new QOS("security", 0.75, true, 0.3));
		SCReservedQoSList.add(new QOS("elasticity", 0.85, true, 0.3));
		/*for(int i = 0; i < SCReservedQoSList.size(); i++){
			System.out.println("SC reserved values--->"+SCReservedQoSList.get(i).getName() + ": " + SCReservedQoSList.get(i).getValue() );
		}*/
		List<QOS> SCPreferredQoSList = new ArrayList<QOS>();
		SCPreferredQoSList.add(new QOS("availability", 0.95, true, 0.1));
		SCPreferredQoSList.add(new QOS("reliability", 0.95, true, 0.2));
		SCPreferredQoSList.add(new QOS("responsiveness", 0.90, true, 0.1));
		SCPreferredQoSList.add(new QOS("security", 0.85, true, 0.3));
		SCPreferredQoSList.add(new QOS("elasticity", 0.95, true, 0.3));
	
		Participant sc = new ServiceCustomer("Consumer", "Negotiation Initiator", "localhost", 20, SCReservedQoSList, SCPreferredQoSList);
		ut = new UtilityFunction(SCReservedQoSList);
		double SCreservedUtilityValue = ut.computeUtilityFunction();
		sc.setReservedUtilityValue(SCreservedUtilityValue);
		ut = new UtilityFunction(SCPreferredQoSList);
		double SCpreferredUtilityValue = ut.computeUtilityFunction();
		sc.setPreferredUtilityValue(SCpreferredUtilityValue);
		loggerTime.info(sc.getName()+" utility value range is: " + SCreservedUtilityValue+"~"+SCpreferredUtilityValue);
		//double middleSCOrg = preferredUtilityValue - reservedUtilityValue;
		//loggerTime.info(sc.getName()+" reserved utility value is: " + reservedUtilityValue);
		//loggerTime.info(sc.getName()+" preferred utility value is: " + preferredUtilityValue);
		//loggerTime.info(sc.getName()+" middle original utility value is: " + middleSCOrg);
		
		
		long startTime, finishTime;
		//int negotiationRound = 0;
		sc.setCurrentNegotiationRound(1);
		sc.setProposal(new Offer("SC", "SP", sc.getCurrentNegotiationRound() , sc.getPreferredQoSList() , new Date(), OfferState.ADVISORY));
		if(sc.sendOfferToBroker(sc.getProposal())){
			startTime = System.currentTimeMillis();
			System.out.println("SC: Initial Offer_" + sc.getCurrentNegotiationRound() + " is sent to MQTT Broker.");	
		}else{
			startTime = 0;
			System.out.println("Fail to send the message to MQTT Broker!");
			System.exit(0);
		}
		System.out.println();
		System.out.println("&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&");
		System.out.println("SC: Initial proposed value are:");
		for(int i = 0; i < sc.getProposal().getQosList().size(); i++){
			QOS temp = sc.getProposal().getQosList().get(i);
			System.out.println(temp.getName() + ": " + temp.getValue() + " positive "+ temp.getPositive() );
		}
		System.out.println("&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&& \n");
				
		//while(!sc.negotiationTerminated() || !sp.negotiationTerminated()){
		while(true){
			
			if(sc.negotiationTerminated() || sp.negotiationTerminated()){
				
				finishTime = System.currentTimeMillis();
				if(sc.success || sp.success){
					this.success = true;
					if(sc.getCurrentNegotiationRound() <= sp.getCurrentNegotiationRound()){
						this.negotiationRound = sc.getCurrentNegotiationRound();
					}else{
						this.negotiationRound = sp.getCurrentNegotiationRound();
					}
					ut = new UtilityFunction(sc.getPreferredQoSList());
					consumerUtilityValue = ut.computeUtilityFunction();
					ut = new UtilityFunction(sp.getPreferredQoSList());
					ProviderUtilityValue = ut.computeUtilityFunction();
					consumerUtilityDiff = SCpreferredUtilityValue-consumerUtilityValue;
					ProviderUtilityDiff = SPpreferredUtilityValue-ProviderUtilityValue;
					
					logTime(startTime, finishTime);
					loggerTime.info(sc.getName()+" result_util: " + consumerUtilityValue);
					loggerTime.info(sp.getName()+" result_util: " + ProviderUtilityValue);
					loggerTime.info("#");
					resultLog.info("#");
					long responseT = finishTime-startTime;
					resultLog.info("Response Time: " + responseT + " milliseconds");
					resultLog.info("sc negotiation round: "+sc.getCurrentNegotiationRound());
					resultLog.info("sc k_concession is: " + sc.k_concession);
					resultLog.info("sc k_tradeoff is: " + sc.k_tradeoff);
					resultLog.info("sp negotiation round: "+sp.getCurrentNegotiationRound());
					resultLog.info("sp k_concession is: " + sp.k_concession);
					resultLog.info("sp k_tradeoff is: " + sp.k_tradeoff);
					resultLog.info("distance to sc preference: "+consumerUtilityDiff);
					resultLog.info("distance to sP preference: "+ProviderUtilityDiff);
					resultLog.info("#");
				}
				
				for(int i = 0; i < SCReservedQoSList.size(); i++){
					System.out.println("SC reserved values--->"+SCReservedQoSList.get(i).getName() + ": " + SCReservedQoSList.get(i).getValue() );
				}
				for(int i = 0; i < SPReservedQoSList.size(); i++){
					System.out.println("SP reserved values--->"+SPReservedQoSList.get(i).getName() + ": " + SPReservedQoSList.get(i).getValue() );
				}
				for(int i = 0; i < sc.getPreferredQoSList().size(); i++){
					System.out.println("SC negotiate result--->"+sc.getPreferredQoSList().get(i).getName() + ": " + sc.getPreferredQoSList().get(i).getValue() );
				}
				for(int i = 0; i < sp.getPreferredQoSList().size(); i++){
					System.out.println("SP negotiate result--->"+sp.getPreferredQoSList().get(i).getName() + ": " + sp.getPreferredQoSList().get(i).getValue() );
				}
				
				

				System.out.println("Negotiation finished. wait to disconnect mqtt.");
				try {
					Thread.sleep(100);
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
				sp.getSubscriber().closeConnection();
				sp.getPublisher().closeConnection();
				sc.getSubscriber().closeConnection();
				sc.getPublisher().closeConnection();
		    
				break;
		    	//System.exit(0);
				
			}
			
			System.out.println("Waiting for SP to provide counter offer... \n");
			
			System.out.println("Here----->wait for sp negotiation ");
			try {
				Thread.sleep(10);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
			boolean success1;
			if((success1 = sp.performNegotiation())){
				System.out.println("**************************" );
				System.out.println("server negotiation round " + sp.getCurrentNegotiationRound() + " : " + success1);
				System.out.println("**************************" );
				
				for(int i = 0; i < sp.getProposal().getQosList().size(); i++){
					QOS temp = sp.getProposal().getQosList().get(i);
					System.out.println(temp.getName() + ": " + temp.getValue() + " positive "+ temp.getPositive());
				}
				System.out.println();
			}else{
				continue;
			}
			//boolean success1 = sp.performNegotiation();
			
			
			if(sc.negotiationTerminated() || sp.negotiationTerminated()){
				//System.out.println("before continue");
				continue;				
			}
			System.out.println("Here----->wait for sc negotiation ");
			try {
				Thread.sleep(10);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
			//boolean success2 = sc.performNegotiation();
			if((success1 = sc.performNegotiation())){
				System.out.println("**************************" );
				System.out.println("client negotiation round " + sc.getCurrentNegotiationRound() + " : " + success1);
				System.out.println("**************************" );
				
				for(int i = 0; i < sc.getProposal().getQosList().size(); i++){
					QOS temp = sc.getProposal().getQosList().get(i);
					System.out.println(temp.getName() + ": " + temp.getValue() + " positive "+ temp.getPositive());
				}
				System.out.println();
			}else{
				continue;		
			}
			
			//System.out.println("Here-------------------------------------------->in the loop");
		}
		System.out.println("Finished");
	}
	
	public static void restoreInitValues(Participant sp, Participant sc){
		//int negotiationRound = 0;
		sp.setCurrentNegotiationRound(0);
		sc.setCurrentNegotiationRound(1);
		
		for(QOS scIndex : sc.getPreferredQoSList()){
			if(scIndex.getName().equals("availability")){
				scIndex.setValue(0.95);
			}
			if(scIndex.getName().equals("reliability")){
				scIndex.setValue(0.95);
			}
			if(scIndex.getName().equals("responsiveness")){
				scIndex.setValue(0.90);
			}
			if(scIndex.getName().equals("security")){
				scIndex.setValue(0.85);
			}
			if(scIndex.getName().equals("elasticity")){
				scIndex.setValue(0.95);
			}
		}
		
		for(QOS spIndex : sp.getPreferredQoSList()){
			if(spIndex.getName().equals("availability")){
				spIndex.setValue(0.84);
			}
			if(spIndex.getName().equals("reliability")){
				spIndex.setValue(0.75);
			}
			if(spIndex.getName().equals("responsiveness")){
				spIndex.setValue(0.37);
			}
			if(spIndex.getName().equals("security")){
				spIndex.setValue(0.80);
			}
			if(spIndex.getName().equals("elasticity")){
				spIndex.setValue(0.89);
			}
			
		}
		
		sc.setProposal(new Offer("SC", "SP", sc.getCurrentNegotiationRound() , sc.getPreferredQoSList() , new Date(), OfferState.ADVISORY));
		if(sc.sendOfferToBroker(sc.getProposal())){
			System.out.println("SC: Initial Offer_" + sc.getCurrentNegotiationRound() + " is sent to MQTT Broker.");
		}else{
			System.out.println("Fail to send the message to MQTT Broker!");
		}
		
		System.out.println();
		System.out.println("&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&");
		System.out.println("SC: Initial proposed value are:");
		for(int i = 0; i < sc.getProposal().getQosList().size(); i++){
			QOS temp = sc.getProposal().getQosList().get(i);
			System.out.println(temp.getName() + ": " + temp.getValue() + " positive "+ temp.getPositive() );
		}
		System.out.println("&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&& \n");
	}
	
	
	public void logTime(long start, long finish){
		responseTime = finish-start;
		loggerTime.info("Response Time: " + responseTime + " milliseconds");
		//loggerTime.info("#");
	}

}
