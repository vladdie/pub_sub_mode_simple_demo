package sla.negotiation.strategy;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import sla.negotiation.model.QOS;

public class Mixed implements NegotiationStrategy{
	
	
	
	private int k_concession;//k1: round number to perform concession
	private int k_tradeoff;//k2: round number to perform tradeoff
	
	
	private List<QOS> fullQOSList = new ArrayList<QOS>();  
	private List<QOS> fullReservedQOS;
	//private List<QOS> bakPrefQOS;
	private double tradeoff_prob;
	
	private boolean tradeoff;
	/**
	 * 
	 * @param QOSList
	 * @param reservedQOS
	 */
	public Mixed(List<QOS> fullQOSList,List<QOS> fullReservedQOS, int k_tradeoff, int k_concession, double tradeoff_prob) {
		this.k_concession = k_concession;
		this.fullQOSList = fullQOSList;
	
		this.fullReservedQOS = fullReservedQOS;
		this.k_tradeoff = k_tradeoff;
		//this.bakPrefQOS = bakPrefQOS;
		this.tradeoff_prob = tradeoff_prob;
	}
	

	/**
	 * 
	 * @param QOSList
	 * @param reservedQOS
	 */
	public Mixed(List<QOS> fullQOSList, List<QOS> fullReservedQOS, double tradeoff_prob) {
	
		this.fullQOSList = fullQOSList;
		this.fullReservedQOS = fullReservedQOS;
		
		this.k_concession = 0;
		this.k_tradeoff = 0;
		this.tradeoff_prob = tradeoff_prob;
		
	}
	
	

	public int getK_concession() {
		return k_concession;
	}


	public void setK_concession(int k_concession) {
		this.k_concession = k_concession;
	}


	public int getK_tradeoff() {
		return k_tradeoff;
	}


	public void setK_tradeoff(int k_tradeoff) {
		this.k_tradeoff = k_tradeoff;
	}


	public List<QOS> getUpdatedQOS() {
	
		return fullQOSList;
	}



	@Override
	public boolean performStrategy() {
		
		tradeoff_prob = 0.80;
		
		double r = Math.random();
		
		if(r < (1 - tradeoff_prob)){
			k_concession++;

			Concession concession = new Concession(fullQOSList, fullReservedQOS, k_concession);
			if(concession.performStrategy()){
				fullQOSList = concession.getUpdatedQOS();
				return true;
			}else{
				System.out.println("Unable to perform concession!");
				k_concession -= 1;
				return false;
			}
		}else{
			k_tradeoff++;
			TradeOff tradeoff = new TradeOff(fullQOSList, fullReservedQOS, k_tradeoff);
			if(tradeoff.performStrategy()){
				fullQOSList = tradeoff.getUpdatedQOS();
				return true;
			}else{
				System.out.println("Unable to perform tradeoff! ");
				k_tradeoff -= 1;
				return false;
			}
		}
	}

	

}
