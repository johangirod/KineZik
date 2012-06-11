package com.kinezik;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.util.UUID;

import android.content.Context;
import android.util.Log;


public class AndroidUUID {
	private static String sID = null;
    private static final String INSTALLATION = "INSTALLATION";
    
   
    public synchronized static String id(Context context) {
    	 if (sID == null) {  
             File installation = new File(context.getFilesDir(), INSTALLATION);
            
             try {
                 if (!installation.exists()) {
                	 Log.d("DEBUG" , "l'installation n'existe pas") ;       	 
                     writeInstallationFile(installation);}
                 sID = readInstallationFile(installation);
             } catch (Exception e) {
                 throw new RuntimeException(e);
             }

         }
    	 Log.d("DEBUG" , "je retourne l'id "+ sID) ;

         return sID;
        
     }

   
    private static void writeInstallationFile(File installation) throws IOException {
    
        FileOutputStream out = new FileOutputStream(installation);
        String id = UUID.randomUUID().toString();
        out.write(id.getBytes());
        out.close();
    }
    private static String readInstallationFile(File installation) throws IOException {
        RandomAccessFile f = new RandomAccessFile(installation, "r");
        byte[] bytes = new byte[(int) f.length()];
        f.readFully(bytes);
        f.close();
        return new String(bytes);
    }
    
   
}
