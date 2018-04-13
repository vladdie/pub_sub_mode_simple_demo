package sla.negotiation.util;

import java.util.List;

import sla.negotiation.model.QOS;

/**
 * Measure the level of satisfaction that a user receives from the service provider
 * 
 * @author Fan
 *
 */
public class UtilityFunction {
	
	/**
	 * constants and scaling factor of attribute's utility function
	 */
	private double a;
	private double b;
	private double k;
	
	private List<QOS> QOSList;  
	
	public UtilityFunction(){
		
	}
	
	/**
	 * 
	 * @param QOSList
	 */
	public UtilityFunction(List<QOS> QOSList){
		this.a = 0;
		this.b = 0;
		this.k = 0;
		this.QOSList = QOSList;
	}

	public List<QOS> getQOS() {
		return QOSList;
	}

	public void setQOS(List<QOS> QOSList) {
		this.QOSList = QOSList;
	}
	
	/**
	 * Compute total utility value of QOS
	 * @return
	 */
	public double computeUtilityFunction(){
		
		final double a1 = -1;
		final double b1 = 1 / Math.E;
		final double k1 = Math.E / (Math.E - 1);
		
		final double a2 = 1;
		final double b2 = 1;
		final double k2 = 1 / (Math.E - 1);
	
		
		double utilityValue = 0;
		for(int i = 0; i < QOSList.size(); i++){
			
			if(QOSList.get(i).getPositive()){
				a = a2;
				b = b2;
				k = k2;
			}else{
				a = a1;
				b = b1;
				k = k1;
			}
			
			double weight = QOSList.get(i).getWeight();
			double qosValue = QOSList.get(i).getValue();
			utilityValue += weight * (k * (Math.exp(a * qosValue) - b));
		}
		//System.out.println("Computed overall utility value is: " + utilityValue);
		return utilityValue;
	}

	/**
	 * compute utility value of a certain QOS attribute
	 * @param qosValue
	 * @param weight
	 * @param positive
	 * @return
	 */
	public double calculateAttributeUtility(double qosValue, double weight, boolean positive) {

		final double a1 = -1;
		final double b1 = 1 / Math.E;
		final double k1 = Math.E / (Math.E - 1);
		
		final double a2 = 1;
		final double b2 = 1;
		final double k2 = 1 / (Math.E - 1);
		
		if(positive){
			a = a2;
			b = b2;
			k = k2;
		}else{
			a = a1;
			b = b1;
			k = k1;
		}
		
		return k * (Math.exp(a * qosValue) - b);
	}

	/**
	 * compute value of a QOS attribute from utility value
	 * @param utilityValue
	 * @param weight
	 * @param positive
	 * @return
	 */
	public double restoreAttributeValue(double utilityValue, double weight, boolean positive) {

		final double a1 = -1;
		final double b1 = 1 / Math.E;
		final double k1 = Math.E / (Math.E - 1);
		
		final double a2 = 1;
		final double b2 = 1;
		final double k2 = 1 / (Math.E - 1);
		
		if(positive){
			a = a2;
			b = b2;
			k = k2;
		}else{
			a = a1;
			b = b1;
			k = k1;
		}
		
		return Math.log(utilityValue/k + b)/a;
	}
	
}
