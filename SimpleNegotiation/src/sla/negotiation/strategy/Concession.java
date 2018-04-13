package sla.negotiation.strategy;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import sla.negotiation.model.QOS;
import sla.negotiation.util.UtilityFunction;

public class Concession implements NegotiationStrategy{

	private int attributeNumber;
	private int attributeIndexMin; //l_min
	private int attributeIndexMax; //l_max
	private int k;//k is the current negotiation round
	
	private double utility_min;
	private double utility_max;

	private List<QOS> QOSList = new ArrayList<QOS>(); //compute the proposal qos value
	private List<QOS> bottomLineQOS;  

	public Concession() {
	}

	/**
	 * 
	 * @param QOSList
	 * @param reservedQOS
	 * @param maxNegotiationRounds
	 * @param currentNegotiationRound
	 */
	public Concession(List<QOS> QOSList, List<QOS> reservedQOS, int currentNegotiationRound) {
		this.attributeNumber = QOSList.size();
		this.attributeIndexMin = 0;
		this.attributeIndexMax = QOSList.size() - 1;
		
		this.QOSList = QOSList;
		this.bottomLineQOS = reservedQOS;
		
		this.k = currentNegotiationRound;
		this.utility_min = 0;
		this.utility_max = 0;
	
	}
	
	
	public boolean performStrategy(){
		
		double landa1 = 0.06;
		double landa2 = 0.06;
		
	
		do{
			QOS attribute_min = QOSList.get(this.attributeIndexMin);
			this.utility_min = calculateAttributeUtility(attribute_min.getValue(),attribute_min.getWeight(), attribute_min.getPositive());
			QOS attribute_max = QOSList.get(this.attributeIndexMax);
			this.utility_max = calculateAttributeUtility(attribute_max.getValue(),attribute_max.getWeight(), attribute_max.getPositive());
			
			//update the utility value
			this.utility_min = this.utility_min - k * landa1 * this.utility_min;
			this.utility_max = this.utility_max - k * landa2 * this.utility_max;
			
			if(this.utility_min < 0 || this.utility_max < 0){
				return false;
			}else{
				double min = restoreValue(this.utility_min,attribute_min.getWeight(), attribute_min.getPositive());
				double max = restoreValue(this.utility_max,attribute_max.getWeight(), attribute_max.getPositive());
				System.out.println("Concession strategy from "+ attribute_min.getValue()+"--->"+min);
				System.out.println("Concession strategy from "+ attribute_max.getValue()+"--->"+max);
				attribute_min.setValue(min);
				attribute_max.setValue(max);
			}
			
			this.attributeIndexMin += 1;
			this.attributeIndexMax = this.attributeNumber - this.attributeIndexMin - 1;
			
		}while(this.attributeIndexMin < (this.attributeNumber / 2 + 1));
		
		//check if it has a conflict with bottom line
		int conflictCount = 0;
		for(int i = 0; i < QOSList.size(); i++){
			
		
			if((QOSList.get(i).getValue() < bottomLineQOS.get(i).getValue()) && bottomLineQOS.get(i).getPositive()){
				QOSList.get(i).setValue(bottomLineQOS.get(i).getValue());
				conflictCount++;
			}
			if((QOSList.get(i).getValue() > bottomLineQOS.get(i).getValue()) && (!bottomLineQOS.get(i).getPositive())){
				QOSList.get(i).setValue(bottomLineQOS.get(i).getValue());
				conflictCount++;
			}
		
		}
		
		if(conflictCount == QOSList.size()){
		
			return false;
		}
		return true;
	}

	public int getNegotiationRound() {
		return this.k;
	}

	public void setNegotiationRound(int k) {
		this.k = k;
	}

	public List<QOS> getUpdatedQOS() {
		return QOSList;
	}

	public double restoreValue(double utility, double weight, boolean positive) {
		// TODO Auto-generated method stub
		UtilityFunction u = new UtilityFunction();
		return u.restoreAttributeValue(utility, weight, positive);
	}

	public int getAttributeNumber() {
		return attributeNumber;
	}

	public void setAttributeNumber(int attributeNumber) {
		this.attributeNumber = attributeNumber;
	}

	public int getAttributeIndexMin() {
		return attributeIndexMin;
	}

	public void setAttributeIndexMin(int attributeIndexMin) {
		this.attributeIndexMin = attributeIndexMin;
	}

	public int getAttributeIndexMax() {
		return attributeIndexMax;
	}

	public void setAttributeIndexMax(int attributeIndexMax) {
		this.attributeIndexMax = attributeIndexMax;
	}
	
	public double calculateAttributeUtility(double value, double weight, boolean positive) {

		UtilityFunction u = new UtilityFunction();
		return u.calculateAttributeUtility(value, weight, positive);
	}
	
	public double calculateOverallUtility(List<QOS> qosList) {

		UtilityFunction u = new UtilityFunction(qosList);
		return u.computeUtilityFunction();
	}
	
}
