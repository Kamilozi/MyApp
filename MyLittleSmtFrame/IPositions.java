package MyLittleSmtFrame;

import java.awt.BorderLayout;
import java.awt.FlowLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTabbedPane;
import javax.swing.JTable;
import javax.swing.event.TableModelEvent;
import javax.swing.event.TableModelListener;
import javax.swing.table.DefaultTableModel;

import MyLittleSmt.AutoAccounting;
import MyLittleSmt.DataImport;
import MyLittleSmt.FlatFile;
import MyLittleSmt.FrameTemplate;
import MyLittleSmt.FrameTemplateButtons;
import MyLittleSmt.FrameTemplateImageIcon;
import MyLittleSmt.FrameTemplateWindow;
import MyLittleSmt.KeyAdder;
import MyLittleSmt.PythonBase;
import MyLittleSmt.PythonOther;
import MyLittleSmt.StoredProcedures;
import MyLittleSmt.Tesseract;
import MyLittleSmt.txt;

public class IPositions extends FrameTemplateWindow implements ActionListener, TableModelListener, MouseListener {
/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
private FrameTemplate posFrame;
private HashMap<String, ArrayList<String>> posSysAll;
private HashMap<String, String> posSysBase, posSys;
private DefaultTableModel posModel;
private ArrayList<Integer> posKey;
private JPanel rOption, rExport, rImport;
private String firm;
private JTable posTable;
private JButton bPos, bSave, bBook, bUnBook, bTese, bFast;
private JMenuItem bCMDel;
private String oType;
private HashMap<String, Integer> columnNumber;
private boolean autoAdd =true;
private int newRow;


 	public IPositions(int x, int y, String title, String oType) {
		super(x, y, title);
		this.oType=oType;
		simpleOpenFrame();		
	}
 	
  
 	
 	private void simpleOpenFrame()
 	{
		posFrame = new FrameTemplate();
		
		JPanel upPanel = new JPanel(new BorderLayout());
		JPanel downPanel = new JPanel(new BorderLayout());
			upPanel.add(posFrame.GetUpMenu(false), BorderLayout.PAGE_START);
				JTabbedPane jtpUp = new JTabbedPane();
					rOption = new JPanel(new FlowLayout(FlowLayout.LEFT));
					rExport = new JPanel(new FlowLayout(FlowLayout.LEFT));
					rImport = new JPanel(new FlowLayout(FlowLayout.LEFT));
					setPosRibbon();
				jtpUp.add("Opcje", rOption);
				jtpUp.add("Eksport", rExport);
				jtpUp.add("Import", rImport);
			upPanel.add(jtpUp, BorderLayout.PAGE_END);
		
		
		posFrame.JTableHelperSys("IPositions_sys.yml");
		posSys = posFrame.getMapSys();
		posSysBase = posFrame.getMapSysBase();
		posSysAll = posFrame.getMapSysAll();
		this.firm = posFrame.getBFirm().getText();
		posModel = getPosModel(firm);
		posKey = new ArrayList<Integer>(Arrays.asList(0,1));
		
		posTable =  posFrame.getDefaultTable(posModel, posSysAll);
		getDict(posSysAll, posTable);
		
		downPanel.add(new JScrollPane(posTable), BorderLayout.CENTER);
		add(upPanel,BorderLayout.PAGE_START);
		add(downPanel, BorderLayout.CENTER);
		posModel.addTableModelListener(this);
		addPosListeners();
		pack();
 	}
 	private void addPosListeners()
 	{
 		posFrame.addFrameTempListener(posTable, posModel, posSysAll, posSysBase, posSys, posKey);
 		posFrame.addListenerJTable(posTable, posModel);
 		posFrame.addListenerRibbon();
 		posFrame.addContextMenu(posTable);
 		posFrame.setGgenKey(0,3,0);
 		if (oType.equals("COSINV"))
 		{
 			posFrame.setMenuRun(7);
 			posTable.addMouseListener(this);
 		}
 		bCMDel = posFrame.getdeleteM();
 		posFrame.remListmDelete();
 		bCMDel.addActionListener(this);
 		//posTable.removeColumn(posTable.getColumnModel().getColumn(7));
 
 		
 	}
 	
 	private void setPosRibbon()
 	{
 		rOption.add(posFrame.DefaultRibbonSim());
 			rOption.add(posFrame.startConterparty());
 		rOption.add(posFrame.DefaultRibbonPosting());
 		rExport.add(posFrame.DefaultRibbonExp());
 			bBook = new FrameTemplateButtons().RibbonJButton("Zaksiêguj", FrameTemplateImageIcon.iconJButton_SavePostring());
 			bBook.addActionListener(this);
 			bUnBook=new FrameTemplateButtons().RibbonJButton("Odksiêguj", FrameTemplateImageIcon.iconJButton_UnPostring());
 			bUnBook.addActionListener(this);
 			bTese = new FrameTemplateButtons().RibbonJButton("Tesseract", FrameTemplateImageIcon.iconJButton_Tesseract());
 			bTese.addActionListener(this);
 			bFast = new FrameTemplateButtons().RibbonJButton("Szybkie", FrameTemplateImageIcon.iconJButton_FastPost());
 			bFast.addActionListener(this);
 		rOption.add(bBook);
 		rOption.add(bUnBook);
 		rOption.add(bTese);
 		rOption.add(bFast);
 		bPos = posFrame.getBPos();
 		bPos.addActionListener(this);
 		bSave = posFrame.getbSave();
 		posFrame.remListbSave();
		bSave.addActionListener(this);
 	}
 	
 	private DefaultTableModel getPosModel(String firma)
 	{
		StoredProcedures sp = new StoredProcedures();
		return sp.genModelPositions(firma, oType, 6, posSys, posSysAll);
 	}
 	
	private void getDict (HashMap<String, ArrayList<String>> mapsysall, JTable table) 
	{
		
		try {
			PythonBase readers = new PythonBase();
			ArrayList<ArrayList<String>> templist;
			templist = readers.FromBase(false, "IPositions_dict.yml");
			txt txtlist = new txt();
			Object[][] dictdata = Arrays.copyOfRange(txtlist.ArrayListToObject(templist), 1, templist.size());
			posFrame.CommboBoxDefault(dictdata, mapsysall, table);	
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}

	@Override
	public void actionPerformed(ActionEvent e) {
		// TODO Auto-generated method stub
	 	if (e.getSource()==bPos||e.getSource()==bSave||e.getSource()==bBook||e.getSource()==bUnBook||e.getSource()==bCMDel)
	 	{
	 		columnNumber();
	 		if (posTable.getSelectedRow()>-1&&posTable.getSelectedColumn()>-1)
	 		{
		 		for (int row=posTable.getSelectedRow();row<(posTable.getSelectedRow()+posTable.getSelectedRowCount());row++)
		 		{	
		 			String tabID = posTable.getValueAt(row, columnNumber.get("ID")).toString();
		 			String tabFirm = posTable.getValueAt(row, columnNumber.get("Firma")).toString();
		 			String tabDocD= posTable.getValueAt(row, columnNumber.get("Data dokumentu")).toString();
		 			String tabDocA = posTable.getValueAt(row, columnNumber.get("Data alternatywna")).toString();
		 			String tabDocS = posTable.getValueAt(row, columnNumber.get("Data rozliczenia")).toString();
		 			String tabTXT = posTable.getValueAt(row, columnNumber.get("Opis")).toString();
		 			String tabCur = posTable.getValueAt(row, columnNumber.get("Waluta")).toString();
		 			String tabCon = posTable.getValueAt(row, columnNumber.get("Kontrahent")).toString();
		 			boolean tabBook = (boolean) posTable.getValueAt(row, columnNumber.get("Zaksiêgowane"));
		 			String tabOType  = posTable.getValueAt(row, columnNumber.get("OTYPE")).toString();
		 			
			 		if (tabID.length()==10&&
			 			tabFirm.length()==4&&
			 			tabDocD.length()==10&&
			 			tabDocA.length()==10&&
			 			tabDocS.length()==10&&
			 			tabCur.length()==3&&!tabCur.equals("   "))
			 		{
				 		if (e.getSource()==bPos)
				 		{
				 			if (oType=="MANPOS")
				 			{
				 			JFrame postings = new IPostings(500, 1000, "Ksiêgowania:" + tabID, 
				 									tabID, tabFirm, tabDocD, tabDocA, tabDocS, tabTXT,tabCur, tabOType, tabBook);
				 			postings.setVisible(true);
				 			}
				 			else if (oType=="COSINV")
				 			{
					 		JFrame postings = new IPostings(500, 1000, "Ksiêgowania:" + tabID, 
	 								tabID, tabFirm, tabDocD, tabDocA, tabDocS, tabTXT,tabCur, tabCon, tabOType, tabBook);
					 		postings.setVisible(true);	
				 			}
				 		}else if (e.getSource()==bSave)
				 		{
				 			JFrame postings = new IPostings(500, 1000, "Ksiêgowania:" + tabID, 
								tabID, tabFirm, tabDocD,tabCur, tabOType, tabBook, addHashMapToPostings(row));
				 			((IPostings) postings).saveButtons();
				 			JOptionPane.showMessageDialog(null, "Zapisano zmiany ID: " + tabID + " Firma: " + tabFirm, "B³¹d", JOptionPane.INFORMATION_MESSAGE);
				 		}else if (e.getSource()==bBook)
				 		{
					 		posTable.setValueAt(true, row, columnNumber.get("Zaksiêgowane"));
					 		IPostings postings = new IPostings(500, 1000, "Ksiêgowania:" + tabID, 
										tabID, tabFirm, tabDocD,tabCur, tabOType, tabBook, addHashMapToPostings(row));
					 		if (postings.getSumCurrency()==0&&postings.getSumAmount()==0&&postings.getSumQTY()==0)
					 		{
				 			postings.saveButtons();
				 			posFrame.Button_Save();
					 		}else
					 		{
					 			posTable.setValueAt(false, row, columnNumber.get("Zaksiêgowane"));
					 			JOptionPane.showMessageDialog(null, "Ksiêgowania dla ID: " + tabID + " firma: " + tabFirm + " nie bilansuj¹ siê","B³¹d", JOptionPane.ERROR_MESSAGE);
					 		}
					 		JOptionPane.showMessageDialog(null, "Zapisano zmiany ID: " + tabID + " Firma: " + tabFirm, "B³¹d", JOptionPane.INFORMATION_MESSAGE);
				 		}else if (e.getSource()==bUnBook)
				 		{
					 		
					 		posTable.setValueAt(false, row, columnNumber.get("Zaksiêgowane"));
					 		IPostings postings = new IPostings(500, 1000, "Ksiêgowania:" + tabID, 
										tabID, tabFirm, tabDocD,tabCur, tabOType, tabBook, addHashMapToPostings(row));
				 			postings.saveButtons();
				 			
				 			posFrame.Button_Save();
				 			
				 		}else if (bCMDel==e.getSource())
				 			{	HashMap<String, Integer> columnMap = posFrame.getColumnNumbers(posTable, posSysAll);
				 				if (posTable.getValueAt(row, columnMap.get("BOOK")).equals(true))
				 					{
				 						JOptionPane.showMessageDialog(null,"Nie mo¿na usun¹æ zaksiêgowanego rekordu ID:" + tabID + " Firma: " + tabFirm,"B³¹d", JOptionPane.ERROR_MESSAGE);
				 					}else if (posTable.getValueAt(posTable.getSelectedRow(), columnMap.get("BOOK")).equals(false))
				 					{
				 						var pos = new IPostings(0, 0, "usuwanie", 
				 								tabID, tabFirm, tabOType, true);
				 						pos.dispose();
				 						posFrame.removeRowInModel(posModel, posTable, row);
				 						posFrame.Button_Save();
				 						pos.dispose();
				 						
		 				}
		 			}
				 		
		 			}else
		 			{
		 				JOptionPane.showMessageDialog(null, (tabID.length()>0&&tabFirm.length()>0) ? "Brak wymaganych danych. ID: " + tabID + " Firma: " + tabFirm : "Brak wymaganych danych w wierszu " + row,"B³¹d", JOptionPane.ERROR_MESSAGE);
		 			}
		 		}
	 		}
	 	}else if (bTese==e.getSource())
	 	{
	 		Tesseract tesseract = new Tesseract();
	 		tesseract.mainInvoice();
	 		ITesseract iTess = new ITesseract(tesseract.getTableList(), tesseract.getDetailList());
	 		addFromTesseract(iTess.getPosition() ,iTess.getPostingLed(),iTess.getpostingQTY());
	 	}else if (bFast==e.getSource())
	 	{
	 		ITesseract iTess = new ITesseract(new ArrayList<ArrayList<String>>(Arrays.asList(new ArrayList<String>(Arrays.asList("ID", "Lp", "Descriptions", "Qunatity", "Unit", "NetUnitPrice", "NetAmount", "VAT", "VATAmount","GrossAmount","DIMENSION",  "DIMENSION_2", "DIMENSION_3")))), 
	 																						 new ArrayList<ArrayList<String>>(Arrays.asList(new ArrayList<String>(Arrays.asList("ID", "FIRM", "DIMENSION_4", "DOCUMENTDATE", "ALTDOCUMENTDATE", "SETTLEMENTDATE", "TXT", "CURRENCYCODE", "OTYPE","ACCOUNTNUM","SUBACCOUNT"))))
	 																						, oType);
	 		addFromTesseract(iTess.getPosition() ,iTess.getPostingLed(),iTess.getpostingQTY());
	 	}
 
	}
	
	private void addFromTesseract(HashMap<String, Object[]> position, HashMap<String, ArrayList<Object[]>> postingLed,HashMap<String, ArrayList<Object[]>> postingQTY)
	{
		 
		for (String key:position.keySet())
		{
		//	posModel.addRow(position.get(key));
			autoAdd = false;
			try {
				
				String posKey = new KeyAdder().PostingID(String.valueOf(position.get(key)[2]), String.valueOf(position.get(key)[1]));
				//position.get(key)[0] = posKey;
				posModel.addRow(position.get(key));
				posFrame.Button_Save();
				position.get(key)[0] = posModel.getValueAt(newRow, 0);
				posFrame.Button_Save();
				AutoAccounting autoPosting = new AutoAccounting(String.valueOf(position.get(key)[1]), String.valueOf(position.get(key)[0]),String.valueOf(position.get(key)[9]));
				if (postingLed.containsKey(key))
				{
					autoPosting.addAutoRowsToModel(postingLed.get(key), false, false);
				}
				if (postingQTY.containsKey(key))
				{
					autoPosting.addAutoRowsToModel(postingQTY.get(key), true, false);
				}
				autoAdd = true;
				System.out.println(position.get(key));
				
			} catch (ParseException e) {
				autoAdd = true;
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			
			
		}
	 
	}

	
	private HashMap<String, String> addHashMapToPostings(int row)
	{
		var newMap = new HashMap<String, String>();
		for (int i=0; i<posTable.getColumnCount(); i++)
		{
			//System.out.println(oType + ":" + posTable.getColumnName(i));
			if (oType.equals("COSINV") && posTable.getColumnName(i).equals("Kontrahent"))//Zareaguje tylko dla COSINV, aby uzupe³ni³ siê warunek MANPOS powinien byæ bez zmian 
			{
			newMap.put("DIMENSION_4", posTable.getValueAt(row, i).toString());
			}else
			{
				newMap.put(posSysAll.get(posTable.getColumnName(i)).get(0), posTable.getValueAt(row, i).toString());
			}
		}
		
		return newMap;
		
	}
	
	private void columnNumber()
	{
		columnNumber = new HashMap<String, Integer>();
		for (int i = 0; i<posTable.getColumnCount();i++)
		{
			if (posTable.getColumnName(i)!="ID"&&posTable.getColumnName(i)!="FIRM"&&posTable.getColumnName(i)!="OTYPE")
			columnNumber.put(posTable.getColumnName(i), i);
		}
 
	}
	

	@Override
	public void tableChanged(TableModelEvent e) {
		// TODO Auto-generated method stub
		if (e.getType()==1)
		{
		newRow = e.getFirstRow();
		}
		if (e.getType()==1&&autoAdd==true)
		{
			addRow(e.getFirstRow());
		}
	}
	
	private void addRow(int firstRow)
	{
		posModel.setValueAt(posFrame.getBFirm().getText(), firstRow, 1);
			SimpleDateFormat formatter= new SimpleDateFormat("yyyy-MM-dd");
			Date date = new Date(System.currentTimeMillis());
			posModel.setValueAt(formatter.format(date), firstRow, 2);
			posModel.setValueAt(formatter.format(date), firstRow, 3);
			posModel.setValueAt(formatter.format(date), firstRow, 4);
			posModel.setValueAt(oType, firstRow, 9);
	}



	@Override//tylko COSINV
	public void mouseClicked(MouseEvent e) {
		// TODO Auto-generated method stub
		if (e.getSource()==posTable)
		{
			if (e.getClickCount()==2)
			{
				if (posTable.getColumnName(posTable.getSelectedColumn()).equals("Kontrahent"))
				{ 
					ArrayList<String> parameters = new ArrayList<String>();
					posFrame.getSelectionRunWithParameters(posTable, "getConterparty",parameters, "Dict_Conterparty.yml", "Projekty", 0);
				}
			}
		}
	}



	@Override
	public void mousePressed(MouseEvent e) {
		// TODO Auto-generated method stub
		
	}



	@Override
	public void mouseReleased(MouseEvent e) {
		// TODO Auto-generated method stub
		
	}



	@Override
	public void mouseEntered(MouseEvent e) {
		// TODO Auto-generated method stub
		
	}



	@Override
	public void mouseExited(MouseEvent e) {
		// TODO Auto-generated method stub
		
	}

}
