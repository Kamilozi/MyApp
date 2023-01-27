package MyLittleSmt;

import java.io.File;
import java.io.IOException;
import java.time.Instant;
import java.util.ArrayList;

public class PythonOther {
	private ArrayList<ArrayList<Object>> ListLog= new ArrayList<ArrayList<Object>>();
	private String txtFilePath = "C:\\Logika\\temporary";
	
	/**
	 * Modyfikator dost�pu do log�w clasy 
	 * @return - logi
	 */
	public ArrayList<ArrayList<Object>> getListLog()
	{
		return ListLog;
	}
	
	/**
	 * Import pliku kurs�w �rednich NBP ze strony w postaci pliku XML
	 * @param FileName - nazwa zapisanego pliku xml
	 * @param FilePath - �cie�ka zapisanego pliku
	 * @param URL - adress url kurs�w �rednich na dzie�
	 */
	public void importXMLFromNBP( String FileName, String FilePath, String URL)
	{
		
		ILogAddToLog AddLog = new ILogAddToLog();
		AddLog.AddToLog("Import plku z adresu: " + URL, "(I)");
		
		
		
		String Path = "C:\\MyLittleSmt\\Import XML.py";
		String command = "py \"" + Path + "\" \"" + FileName + "\" \"" + FilePath + "\" \"" + URL + "\"";
		
		try 
		{
			Process p = Runtime.getRuntime().exec(command);
			File file = new File(FilePath + "\\" + FileName);
			
			if (WaitForFile(file)==true)
			{
				AddLog.AddToLog("Import zako�czony. Plik zapisany jako: " + file.toString(), "(I)");
			}else
			{
				AddLog.AddToLog("B��d 9. Import pliku ze strony nie powi�d� si�.", "(I)");
			}
		} catch (IOException e1) {
			AddLog.AddToLog("B��d 9. Import pliku ze strony nie powi�d� si�.", "(E)");
			
		}
		ListLog.addAll(AddLog.getListLog());
	}
	
	public boolean runTesseract(String fileName, String filePath, String commandPy)
	{
		String Path = "C:\\MyLittleSmt\\" + commandPy; //tesseract_ver_Prod.py";
		String command = "py \"" + Path + "\" \"" + fileName + "\" \"" + filePath + "\"";

		try 
		{
			String newFileName = fileName.substring(0, fileName.length()-4) + ".txt";
			Process p = Runtime.getRuntime().exec(command);
			File file = new File(filePath + "\\" + fileName+ ".txt");
			return WaitForFile(file);
		} catch (IOException e1) {
			return false;
			//AddLog.AddToLog("B��d 9. Import pliku ze strony nie powi�d� si�.", "(E)");
		}
	}
	
	public boolean WaitForFile (File pathfile)
	{
		boolean waitforfile = false; 
		Instant later = Instant.now().plusSeconds(10) ;
			do{
				if (pathfile.exists()||later.isBefore(Instant.now()))
				{
					waitforfile=true; 
				}
			}while ((waitforfile==false)); 
			
			if (pathfile.exists()==false)
			{
				waitforfile=false;	
			}

		return waitforfile;
	}
	
	public void pdfGenerator(String html, String pdf)
	{
		
		String Path = "C:\\MyLittleSmt\\invoiceGenerator.py";
		String command = "py \"" + Path + "\" \"" + html + "\" \"" + pdf + "\"";
		try {
			Process p = Runtime.getRuntime().exec(command);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
}
