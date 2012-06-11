package com.kinezik.services;
/**
 * L'interface pour être averti à un service.
 * @author johan
 *
 */
public interface ServiceListener {
	/**
	 * Appelée lorsque le service a effectué sa tâche (on peut maintenant interagir avec lui)
	 */
	public void onServiceReady();
}
