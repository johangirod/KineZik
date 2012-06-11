package com.kinezik.network;


/**
 * Thread that receive Bayesian Table from the server, and make it a String
 * @author johan
 *
 */
public class BayesReceiveThread extends Thread {
	private BayesReceiver receiver;

	/**
	 * The receiver
	 * @param receiver
	 */
	public BayesReceiveThread(BayesReceiver receiver){
		this.receiver = receiver;	
	}

	public void run(){
		Message mess = new Message();
		mess.add("action", "getBayesTable");
		String response = mess.send();
		receiver.onBayesReceive(response);
	}


}
