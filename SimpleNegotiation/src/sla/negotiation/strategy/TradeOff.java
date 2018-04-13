package sla.negotiation.strategy;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import sla.negotiation.model.QOS;
import sla.negotiation.util.UtilityFunction;

public class TradeOff implements Comparator<QOS>, NegotiationStrategy{
	
	private int attributeNumber;
	private int attributeIndexMin; //l_min
	private int attributeIndexMax; //l_max
	private int k;//k is the current negotiation round
	
	private double utility_min;
	private double utility_max;
	
	private List<QOS> QOSList; //compute the proposal qos value
	private List<QOS> bakQOSList = new ArrayList<QOS>();
	private List<QOS> bottomLineQOS;  
	
	public TradeOff(List<QOS> QOSList, List<QOS> reservedQOS, int currentNegotiationRound) {
		this.attributeNumber = QOSList.size();
		this.attributeIndexMin = 0;
		this.attributeIndexMax = QOSList.size() - 1;
		this.QOSList = QOSList;
		for(int i = 0; i<QOSList.size();i++){
			this.bakQOSList.add(QOSList.get(i).clone());
		}
		this.bottomLineQOS = reservedQOS;
		this.utility_min = 0;
		this.utility_max = 0;
		this.k = currentNegotiationRound;
		
	}
	
	
	
	public List<QOS> getUpdatedQOS() {
		return QOSList;
	}


	/**
	 * Compute values of QOS attributes 
	 * @return
	 * true: if at least one attribute is updated.
	 */
	public boolean performStrategy(){
		
		if(QOSList.size() <= 1){
			return false;
		}
		
		double landa2 = 0.06;
		
		List<QOS> temp = new ArrayList<QOS>(QOSList);
	
		Collections.sort(temp, this);
		
		do{
			
			//locate the qos attribute whose weight is smallest
			QOS attribute_min = temp.get(this.attributeIndexMin);
			this.utility_min = calculateAttributeUtility(attribute_min);
			QOS attribute_max = temp.get(this.attributeIndexMax);
			this.utility_max = calculateAttributeUtility(attribute_max);
			
			double reservedUtility_min = 0;
			for(int i = 0; i < bottomLineQOS.size(); i++){
				if(bottomLineQOS.get(i).getName().equals(attribute_min.getName())){
					reservedUtility_min = calculateAttributeUtility(bottomLineQOS.get(i));
				}
			}
		
			//update the utility value
			double uSum = utility_min + utility_max;
			utility_min = utility_min - (landa2 * k) * utility_min;
			//check if the utility value is less than reserved utility value
			if(utility_min < reservedUtility_min){
				utility_min = reservedUtility_min;
			}
			utility_max = uSum - utility_min;
			
			if(utility_max > 1){
				utility_min += utility_max - 1;
				utility_max = 1;
			}
			
			if(this.utility_min < 0 || this.utility_max < 0){
				return false;
			}else{
				attribute_min.setValue(restoreValue(this.utility_min,attribute_min.getWeight(), attribute_min.getPositive()));
				attribute_max.setValue(restoreValue(this.utility_max,attribute_max.getWeight(), attribute_max.getPositive()));
			}
			
			this.attributeIndexMin += 1;
			this.attributeIndexMax = this.attributeNumber - this.attributeIndexMin - 1;
			
		}while(this.attributeIndexMin < (this.attributeNumber / 2 + 1));
		
		//convert back to the original order
		for(int i = 0; i < QOSList.size(); i++){
			for(int j = 0; j < temp.size(); j++){
				if(QOSList.get(i).getName().equals(temp.get(j).getName())){
					QOSList.get(i).setValue(temp.get(j).getValue());
				}
			}
		}
	
		//check if it has a conflict with bottom line
		int conflictCount = 0;
		for(int i = 0; i < QOSList.size(); i++){
			
			if((QOSList.get(i).getValue() < bottomLineQOS.get(i).getValue()) && bottomLineQOS.get(i).getPositive()){
				System.out.println("Computed tradeoff value is violated to bottom line: " + QOSList.get(i).getValue());
				System.out.println("Replace from : " + QOSList.get(i).getValue() + "---->" + bottomLineQOS.get(i).getValue());
				QOSList.get(i).setValue(bottomLineQOS.get(i).getValue());
				
			}
			if((QOSList.get(i).getValue() > bottomLineQOS.get(i).getValue()) && (!bottomLineQOS.get(i).getPositive())){
				QOSList.get(i).setValue(bottomLineQOS.get(i).getValue());
			}
			
		}
		
		for(int i = 0; i < QOSList.size(); i++){
			if(bakQOSList.get(i).getValue() == QOSList.get(i).getValue()){
				conflictCount++;
			}
		}
	
		if(conflictCount == QOSList.size() ){
			return false;
		}
		return true;
	}

	@Override
	public int compare(QOS qos1, QOS qos2) {
		double weight1 = qos1.getWeight();
		double weight2 = qos2.getWeight();
		
		if(weight1 > weight2){
			return 1;
		}else if(weight1 < weight2){
			return -1;
		}else{
			return 0;
		}
	}

	
	public double calculateAttributeUtility(QOS attribute) {

		UtilityFunction u = new UtilityFunction();
		return u.calculateAttributeUtility(attribute.getValue(), attribute.getWeight(), attribute.getPositive());
	}
	
	public double restoreValue(double utility, double weight, boolean positive) {
		// TODO Auto-generated method stub
		UtilityFunction u = new UtilityFunction();
		return u.restoreAttributeValue(utility, weight, positive);
	}

	public double calculateOverallUtility(List<QOS> qosList) {
		UtilityFunction u = new UtilityFunction(qosList);
		return u.computeUtilityFunction();
	}

	
	

}
