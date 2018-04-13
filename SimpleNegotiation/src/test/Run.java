package test;

public class Run {

	
	public static void main(String[] args) {
		// TODO Auto-generated method stub
		long totalTime = 0;
		int successCounter = 0;
		int totalNegotiationRounds = 0;
		double totalUserUtilityValue = 0;
		double totalProviderUtilityValue = 0;
		double totalUserUtilDiff = 0;
		double totalProviderUtilDiff = 0;
		for(int i = 0; i < 100; i++){
			Main main = new Main();
			main.run();
			if(main.success){
				totalTime = totalTime + main.responseTime;
				totalNegotiationRounds = totalNegotiationRounds + main.negotiationRound;
				totalUserUtilityValue = totalUserUtilityValue + main.consumerUtilityValue;
				totalProviderUtilityValue = totalProviderUtilityValue + main.ProviderUtilityValue;
				totalUserUtilDiff = totalUserUtilDiff + main.consumerUtilityDiff;
				totalProviderUtilDiff = totalProviderUtilDiff + main.ProviderUtilityDiff;
				successCounter++;
			}
			try {
				Thread.sleep(1000);
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		double avgTime = (double)totalTime/(double)successCounter;
		double avgRound = (double)totalNegotiationRounds/(double)successCounter;
		double avgUserUtil = (double)totalUserUtilityValue/(double)successCounter;
		double avgProviderUtil = (double)totalProviderUtilityValue/(double)successCounter;
		double avgUserUtilDiff = (double)totalUserUtilDiff/(double)successCounter;
		double avgProviderUtilDiff = (double)totalProviderUtilDiff/(double)successCounter;
		System.out.println("Simulation finished!");
		System.out.println("Success time is: "+ successCounter);
		System.out.println("Average negotiation round is: "+ avgRound);
		//System.out.println("Total negotiation round is: "+ totalNegotiationRounds);
		System.out.println("Average response time is: "+ avgTime);
		System.out.println("Average user utility value is: "+ avgUserUtil);
		System.out.println("Average provider utility value is: "+ avgProviderUtil);
		System.out.println("Average user utility difference is: "+ avgUserUtilDiff);
		System.out.println("Average provider utility difference is: "+ avgProviderUtilDiff);
	}

}
