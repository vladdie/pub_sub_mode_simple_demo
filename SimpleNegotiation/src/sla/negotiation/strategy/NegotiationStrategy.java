package sla.negotiation.strategy;

import java.util.List;

import sla.negotiation.model.QOS;

public interface NegotiationStrategy {

	public boolean performStrategy();
	
	//public double calculateAttributeUtility(double value, double weight, boolean positive);
	
	//public double calculateOverallUtility(List<QOS> qosList);
	
	//public double restoreValue(double utility, double weight, boolean positive);
	
	public List<QOS> getUpdatedQOS();

}
