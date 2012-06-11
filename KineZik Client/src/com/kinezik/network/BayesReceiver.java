package com.kinezik.network;


/**
 * Interface for the Thread that receives the Bayesian Table from the server
 * @author root
 *
 */
public interface BayesReceiver {
	
	public void onBayesReceive(String JSONBayesTable);

}
