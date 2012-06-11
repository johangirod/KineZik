
package modele;

/**
 * Installation represente une instance unique d'installation de l'application KineZik sur un device
 * @author johan
 *
 */
public class Installation {
	private String UUID;
	
	public Installation(String UUID){
		this.UUID = UUID;
	}
	
	public String getUUID() {
		return UUID;
	}
	
	
	/**
	 * Verifie que l'UUID passé en argument sous forme de chaîne a une syntaxe correcte
	 * @param UUID : un UUID passé sous forme de chaîne
	 * @return true si l'UUID est une chaine de caractère hexadécimaux représentant un entier de 128bit. 
	 */
	public static boolean isCorrect(String UUID){

		String hexa = "[a-f0-9]"; 
		if (UUID == null){
			return false;
		}
			return UUID.matches(hexa+"{8}-(" + hexa + "{4}-){3}"+ hexa + "{12}") && UUID.length()==36;
	}
}
	