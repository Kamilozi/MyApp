package MyLittleSmtFrame;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;

import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.event.TableModelEvent;
import javax.swing.event.TableModelListener;
import javax.swing.table.DefaultTableModel;

import MyLittleSmt.FrameTemplate;
import MyLittleSmt.FrameTemplateImageIcon;
import MyLittleSmt.FrameTemplateWindow;
import MyLittleSmt.MainEntryDataWarehouse;
import MyLittleSmt.StoredProcedures;


public class ITesseract {
	private FrameTemplate deFrame, taFrame;
	private HashMap<String, String> taSys, taSysBase, deSys, deSysBase;
	private HashMap<String, ArrayList<String>> taSysAll, deSysAll;
	private ArrayList<ArrayList<String>> table, detail;
	private HashMap<String, ArrayList<Object[]>> postingLed, postingQTY;
	private HashMap<String, Object[]> position;
	private window window;
	private String OTYPEPos;
	public ITesseract(ArrayList<ArrayList<String>> table,ArrayList<ArrayList<String>> detail)
	{
		postingLed = new HashMap<String, ArrayList<Object[]>>();
		postingQTY= new HashMap<String, ArrayList<Object[]>>();
		position= new HashMap<String, Object[]>();
		this.table = table;
		this.detail = detail;
		deFrame = new FrameTemplate();
		taFrame = new FrameTemplate();
		window = new window( 1000,500, "PDF");
		window.setVisible(true);
		this.OTYPEPos = "COSINV";
				
	}

	public ITesseract(ArrayList<ArrayList<String>> table,ArrayList<ArrayList<String>> detail, String OTYPEPos)
	{
		postingLed = new HashMap<String, ArrayList<Object[]>>();
		postingQTY= new HashMap<String, ArrayList<Object[]>>();
		position= new HashMap<String, Object[]>();
		this.table = table;
		this.detail = detail;
		deFrame = new FrameTemplate();
		taFrame = new FrameTemplate();
		window = new window( 1000,500, "PDF");
		window.setVisible(true);
		this.OTYPEPos = OTYPEPos;
				
	}	
	
	public HashMap<String, ArrayList<Object[]>> getPostingLed()
	{
		return postingLed;
	}
	
	public HashMap<String, ArrayList<Object[]>> getpostingQTY()
	{
		return postingQTY;
	}
	
	public HashMap<String, Object[]> getPosition()
	{
		return position;
	}
	
	class window extends JDialog implements ActionListener, MouseListener, TableModelListener
	{
		/**
		 * 
		 */
		private static final long serialVersionUID = 1L;
		private HashMap<String, Integer> deColM, taColM, deColT;
		private DefaultTableModel taModel, deModel;
		private JTable taTable, deTable;
		private JTextField sumNetto, sumGross, sumVat, selectID;
		private ArrayList<ArrayList<String>> counterparty, firms;
		private HashMap<String, ArrayList<String>> firmsMap;
		private HashMap<String, HashMap<String, String>> accountMap;
		private JButton okBut;
		private JCheckBox cbSum, cbQuantity, cbZero;
		private ArrayList<ArrayList<String>> ListFromBase;
		private JMenuItem jmiSpread;
		//private JPanel rOption;
		
		public window(int x, int y, String title) {
			setModal(true);
			setTitle(title);
			setSize(x,y);   
			setDefaultCloseOperation(HIDE_ON_CLOSE);
			setIconImage(FrameTemplateImageIcon.iconSys().getImage());
			ListFromBase = new ArrayList<ArrayList<String>>();
			firmsMap = MainEntryDataWarehouse.getFirmsMap();
			getAccountMap ();
			getCounterparty();
			deFrame.JTableHelperSys("ITesseract_detail_sys.yml");			
			deSys = deFrame.getMapSys();
			deSysBase = deFrame.getMapSysBase();
			deSysAll = deFrame.getMapSysAll();
			deModel = getModel(detail, 22, deSysAll, deSys);
			deTable = deFrame.getDefaultTable(deModel, deSysAll);
			
			taFrame.JTableHelperSys("ITesseract_table_sys.yml");
			taSys = taFrame.getMapSys();
			taSysBase = taFrame.getMapSysBase();
			taSysAll = taFrame.getMapSysAll();
			taModel = getModel(table, 23 ,taSysAll, taSys);
			taTable = taFrame.getDefaultTable(taModel, taSysAll);
			
			deColM = deFrame.getColumnNumbers(deModel, deSysAll);
			taColM = taFrame.getColumnNumbers(taModel, taSysAll);
			
			cbSum = new JCheckBox("Wartoœciowe w sumie", true); 
			cbQuantity = new JCheckBox("Generuj iloœciowe", true);  
			cbZero = new JCheckBox("Generuj z zerowymi", false); 
			
			jmiSpread = new JMenuItem("Rozpropaguj");
			jmiSpread.addActionListener(this);
			taFrame.getPopup().add(jmiSpread);
			
			okBut = new JButton("Ok");
			okBut.setPreferredSize(new Dimension(125,35));
			okBut.addActionListener(this);
			selectID = new JTextField();
			selectID.setEnabled(false);
			sumNetto = new JTextField();
			sumNetto.setEnabled(false);
			sumGross = new JTextField(); 
			sumGross.setEnabled(false);
			sumVat = new JTextField();
			sumVat.setEnabled(false);
			sumNetto.setPreferredSize(new Dimension(125,20));
			sumGross.setPreferredSize(new Dimension(125,20));
			sumVat.setPreferredSize(new Dimension(125,20));
			selectID.setPreferredSize(new Dimension(125,20));
			JPanel upMenu = new JPanel(new BorderLayout());
				upMenu.add(taFrame.GetUpMenu(false), BorderLayout.PAGE_START);
				//JTabbedPane jtp = new JTabbedPane();
				//	rOption = new JPanel(new FlowLayout(FlowLayout.LEFT));
				//jtp.add("Opcje", rOption);
				//upMenu.add(jtp);
			add(upMenu, BorderLayout.PAGE_START);
			JPanel downMenu = new JPanel(new GridLayout(2,1));
				JPanel downUp = new JPanel(new BorderLayout());
						JPanel small = new JPanel(new FlowLayout(FlowLayout.LEFT));
						small.add(deFrame.getSmallButtons());
						small.add(cbSum);
						small.add(cbQuantity);
					downUp.add(small, BorderLayout.PAGE_START);
					downUp.add(new JScrollPane(deTable), BorderLayout.CENTER);
					JPanel downUpDown = new JPanel(new FlowLayout(FlowLayout.LEFT));
						downUpDown.add(new JLabel("ID:"));
						downUpDown.add(selectID);
						downUpDown.add(new JLabel("Netto:"));
						downUpDown.add(sumNetto);
						downUpDown.add(new JLabel("Vat:"));
						downUpDown.add(sumVat);
						downUpDown.add(new JLabel("Brutto:"));
						downUpDown.add(sumGross);
					downUp.add(downUpDown, BorderLayout.PAGE_END);
				JPanel downDown = new JPanel(new BorderLayout());
				  	JPanel downSmall = new JPanel(new FlowLayout(FlowLayout.LEFT));
				  		downSmall.add(taFrame.getSmallButtons());
				  		downSmall.add(cbZero);
					downDown.add(downSmall, BorderLayout.PAGE_START);
					downDown.add(new JScrollPane(taTable), BorderLayout.CENTER);
					JPanel downDownDown = new JPanel(new FlowLayout(FlowLayout.RIGHT));
						downDownDown.add(okBut);
					downDown.add(downDownDown, BorderLayout.PAGE_END);
			downMenu.add(downUp);
			downMenu.add(downDown);
			add(downMenu);
			listeners();			
			 
			
		}

		private void listeners()
		{
			deTable.addMouseListener(this);
			taTable.addMouseListener(this);
			deModel.addTableModelListener(this);
			taModel.addTableModelListener(this);
			deFrame.setMenuRun(2);
			deFrame.setMenuRun(9);
			deFrame.setMenuRun(10);
			deFrame.getDict(deSysAll, deTable, "ITesseract_detail_dict.yml");
			taFrame.getDict(taSysAll, taTable, "ITesseract_table_dict.yml");
			taFrame.setMenuRun(10);
			taFrame.setMenuRun(11);
			taFrame.setMenuRun(12);
			deFrame.getsmallButtonSave().setEnabled(false);
			taFrame.getsmallButtonSave().setEnabled(false);
			deFrame.addFrameTempListener(deTable, deModel, deSysAll, deSysBase, deSys, new ArrayList<Integer>(Arrays.asList(0)));
			deFrame.addListenerJTable(deTable, deModel);
			deFrame.addListenerSmallButtons();
			deFrame.addListenerContextMenu();
			taFrame.addFrameTempListener(taTable, taModel, taSysAll, taSysBase, taSys, new ArrayList<Integer>(Arrays.asList(0,1)));
			taFrame.addListenerJTable(taTable, taModel);
			taFrame.addListenerContextMenu();
			taFrame.addListenerSmallButtons();
			//deAdd = deFrame.getsmallButtonAdd();
			//deFrame.removeListenerSmallButtonAdd();
			//deAdd.addActionListener(this);
		}
		
		private DefaultTableModel getModel(ArrayList<ArrayList<String>> list, int enable,HashMap<String, ArrayList<String>> sysAll, HashMap<String, String> sys)
		{
			return new StoredProcedures().genModel(list, enable, sys, sysAll);
		}
		
		private void getAccountMap()
		{
			ArrayList<ArrayList<String>> planAccountCodes = new StoredProcedures().genUniversalArray("getPlanAccountCodes", new ArrayList<String>());
			accountMap= new HashMap<String, HashMap<String, String>>();
			for (int i=1;i<planAccountCodes.size();i++)
			{
				if (!accountMap.containsKey(planAccountCodes.get(i).get(0)))
				{
					accountMap.put(planAccountCodes.get(i).get(0), new HashMap<String, String>());
				}
				accountMap.get(planAccountCodes.get(i).get(0)).put(planAccountCodes.get(i).get(1), planAccountCodes.get(i).get(2));
			}
			
			
		}
		
		private void getCounterparty()
		{
			firms= new StoredProcedures().genUniversalArray("getCompany", new ArrayList<String>());
			for (int i=1;i<detail.size();i++)
			{
				for (int j=1;j<firms.size();j++)
				{
					if (firms.get(j).get(8).equals(detail.get(i).get(1)))
					{
						detail.get(i).set(1, firms.get(j).get(0));
						String planAccointGroup= firms.get(j).get(0); 
							if (firmsMap.containsKey(planAccointGroup)&&accountMap.containsKey(firmsMap.get(planAccointGroup).get(1))&&accountMap.get(firmsMap.get(planAccointGroup).get(1)).containsKey("SIMLIA"))
							{
								detail.get(i).set(9, accountMap.get(firmsMap.get(planAccointGroup).get(1)).get("SIMLIA"));
							}
						continue;
					}
				}
			}			
			
			counterparty = new StoredProcedures().genUniversalArray("getConterparty", new ArrayList<String>());
			for (int i=1;i<detail.size();i++)
			{
			///przy okazji dodaje konto
				//deTable.getValueAt(deTable.getSelectedRow(), deColT.get("FIRM"))
				for (int j=1;j<counterparty.size();j++)
				{
					if (counterparty.get(j).get(4).equals(detail.get(i).get(2)))
					{
						detail.get(i).set(2, counterparty.get(j).get(1));
						
						continue;
					}
				}
			}
			
			
			
		}
		
 
		@Override
		public void actionPerformed(ActionEvent e) {
			// TODO Auto-generated method stub
			if (e.getSource()==okBut)
			{
				//1 czy wszystkie dane deTable s¹?
				if (checkData()==true)
				{
					///Stworzenie position
					addPosition();
					
				}
				
			}else if (e.getSource()==jmiSpread)
			{
				if (taTable.getSelectedColumn()>-1&&taTable.getSelectedRow()>-1)
				{
					HashMap<String, Integer> taColT =taFrame.getColumnNumbers(taTable, taSysAll);
					if (taTable.getSelectedColumn()==taColT.get("DIMENSION")
							||taTable.getSelectedColumn()==taColT.get("DIMENSION_2")
							||taTable.getSelectedColumn()==taColT.get("DIMENSION_3"))
						
					{
						String value = String.valueOf(taTable.getValueAt(taTable.getSelectedRow(), taTable.getSelectedColumn()));
						String posID = String.valueOf(taTable.getValueAt(taTable.getSelectedRow(), taColT.get("ID")));
						for (int i=0;i<taTable.getRowCount();i++)
						{
							if (taTable.getValueAt(i,taColT.get("ID")).equals(posID))
							{
								taTable.setValueAt(value, i, taTable.getSelectedColumn());
							}
						}
					}
				}
			}
		}
		private void addPosition()
		{
			position = new HashMap<String,Object[]>();

			
			for (int i=0;i<deModel.getRowCount();i++)
			{
				Object[] newRow = {"", String.valueOf(deModel.getValueAt(i, deColM.get("FIRM"))),
						String.valueOf(deModel.getValueAt(i, deColM.get("DOCUMENTDATE"))),
						String.valueOf(deModel.getValueAt(i, deColM.get("ALTDOCUMENTDATE"))),
						String.valueOf(deModel.getValueAt(i, deColM.get("SETTLEMENTDATE"))),
						String.valueOf(deModel.getValueAt(i, deColM.get("TXT"))),
						String.valueOf(deModel.getValueAt(i, deColM.get("CURRENCYCODE"))),
						findConterId (String.valueOf(deModel.getValueAt(i, deColM.get("DIMENSION_4")))),
						false,
						"COSINV"};
				position.put(String.valueOf(deModel.getValueAt(i, deColM.get("ID"))), newRow);
				ArrayList<ArrayList<String>> postingDetail = new ArrayList<ArrayList<String>>();
				ArrayList<ArrayList<String>> postingQTYDetail = new ArrayList<ArrayList<String>>();
				String firmID = String.valueOf(deModel.getValueAt(i, deColM.get("FIRM")));
				String subaccount = String.valueOf(deModel.getValueAt(i, deColM.get("SUBACCOUNT")));
				String simcos = "4" + String.valueOf(accountMap.get(firmsMap.get(firmID).get(1)).get("SIMLIA").substring(1, accountMap.get(firmsMap.get(firmID).get(1)).get("SIMLIA").length()));
				if (String.valueOf(deModel.getValueAt(i, deColM.get("OTYPE"))).equals("COSSIM")||(String.valueOf(deModel.getValueAt(i, deColM.get("OTYPE"))).equals("COSEND")&&subaccount.trim().length()<=0))
				{
					postingDetail.add(new ArrayList<String>(Arrays.asList(accountMap.get(firmsMap.get(firmID).get(1)).get("SIMLIA"), "G", "1")));
					postingDetail.add(new ArrayList<String>(Arrays.asList(accountMap.get(firmsMap.get(firmID).get(1)).get("COSTAX"), "V", "0")));
					postingDetail.add(new ArrayList<String>(Arrays.asList(simcos, "N", "0")));
					postingQTYDetail.add(new ArrayList<String>(Arrays.asList(accountMap.get(firmsMap.get(firmID).get(1)).get("goodsForPurchase").trim(), "T", "1")));
					postingQTYDetail.add(new ArrayList<String>(Arrays.asList(accountMap.get(firmsMap.get(firmID).get(1)).get("warehouse").trim(), "T", "0")));
				
				}else if (String.valueOf(deModel.getValueAt(i, deColM.get("OTYPE"))).equals("COSFOR"))
				{
					postingDetail.add(new ArrayList<String>(Arrays.asList(accountMap.get(firmsMap.get(firmID).get(1)).get("SIMLIA"), "G", "1")));
					postingDetail.add(new ArrayList<String>(Arrays.asList(accountMap.get(firmsMap.get(firmID).get(1)).get("FORTAX"), "V", "0")));
					postingDetail.add(new ArrayList<String>(Arrays.asList(accountMap.get(firmsMap.get(firmID).get(1)).get("FORCOS"), "N", "0")));					
				}else if (String.valueOf(deModel.getValueAt(i, deColM.get("OTYPE"))).equals("COSPRE")&&subaccount.trim().length()<=0)
				{
					postingDetail.add(new ArrayList<String>(Arrays.asList(accountMap.get(firmsMap.get(firmID).get(1)).get("SIMLIA"), "G", "1")));
					postingDetail.add(new ArrayList<String>(Arrays.asList(accountMap.get(firmsMap.get(firmID).get(1)).get("COSTAX"), "V", "0")));
					postingDetail.add(new ArrayList<String>(Arrays.asList(accountMap.get(firmsMap.get(firmID).get(1)).get("PRECOS"), "N", "0")));	
					postingQTYDetail.add(new ArrayList<String>(Arrays.asList(accountMap.get(firmsMap.get(firmID).get(1)).get("goodsForPurchase").trim(), "T", "1")));
					postingQTYDetail.add(new ArrayList<String>(Arrays.asList(accountMap.get(firmsMap.get(firmID).get(1)).get("PREPUR").trim(), "T", "0")));
					
					
				}else if (String.valueOf(deModel.getValueAt(i, deColM.get("OTYPE"))).equals("COSPRE")&&subaccount.trim().length()>0)
				{
					postingDetail.add(new ArrayList<String>(Arrays.asList(accountMap.get(firmsMap.get(firmID).get(1)).get("PRECOS"), "N", "0")));
					postingDetail.add(new ArrayList<String>(Arrays.asList(accountMap.get(firmsMap.get(firmID).get(1)).get("FORCOS"), "N", "1")));
					postingDetail.add(new ArrayList<String>(Arrays.asList(accountMap.get(firmsMap.get(firmID).get(1)).get("FORTAX"), "V", "1")));
					postingDetail.add(new ArrayList<String>(Arrays.asList(accountMap.get(firmsMap.get(firmID).get(1)).get("COSTAX"), "V", "0")));
					postingQTYDetail.add(new ArrayList<String>(Arrays.asList(accountMap.get(firmsMap.get(firmID).get(1)).get("goodsForPurchase").trim(), "T", "1")));
					postingQTYDetail.add(new ArrayList<String>(Arrays.asList(accountMap.get(firmsMap.get(firmID).get(1)).get("PREPUR").trim(), "T", "0")));
					
				}else if (String.valueOf(deModel.getValueAt(i, deColM.get("OTYPE"))).equals("COSEND")&&subaccount.trim().length()>0)
				{
					postingDetail.add(new ArrayList<String>(Arrays.asList(accountMap.get(firmsMap.get(firmID).get(1)).get("PRECOS"), "P", "0")));
					postingDetail.add(new ArrayList<String>(Arrays.asList(simcos, "P", "1")));
					postingDetail.add(new ArrayList<String>(Arrays.asList(accountMap.get(firmsMap.get(firmID).get(1)).get("COSTAX"), "V", "1")));
					postingDetail.add(new ArrayList<String>(Arrays.asList(accountMap.get(firmsMap.get(firmID).get(1)).get("SIMLIA"), "B", "0")));
					
					postingQTYDetail.add(new ArrayList<String>(Arrays.asList(accountMap.get(firmsMap.get(firmID).get(1)).get("PREPUR").trim(), "T", "1")));
					postingQTYDetail.add(new ArrayList<String>(Arrays.asList(accountMap.get(firmsMap.get(firmID).get(1)).get("warehouse").trim(), "T", "0")));
					postingQTYDetail.add(new ArrayList<String>(Arrays.asList(accountMap.get(firmsMap.get(firmID).get(1)).get("goodsForPurchase").trim(), "T", "1")));	
					
				}
				
				
				
				///cbSum, cbQuantity, cbZero
				ArrayList<Object[]> techPosting = new ArrayList<Object[]>();
				double netto = 0.00;
				double gross = 0.00;
				double tax = 0.00;
				double cost = 0.00;
				////odnajdywanie kwoty cost
				if (String.valueOf(deModel.getValueAt(i, deColM.get("SUBACCOUNT"))).trim().length()>0&&deModel.getValueAt(i, deColM.get("OTYPE")).toString().equals("COSEND"))
				{
					for (int k=1;k<ListFromBase.size();k++)
					{
						if (ListFromBase.get(k).get(0).equals(String.valueOf(deModel.getValueAt(i, deColM.get("FIRM"))))&&
								ListFromBase.get(k).get(2).equals(String.valueOf(deModel.getValueAt(i, deColM.get("SUBACCOUNT")))))
						{
							cost += Double.valueOf(String.valueOf(ListFromBase.get(k).get(4)).replace(",", ","));
						}
					}
				}
				
				int lp = 0;
				int lpQ = 0;
				postingLed = new HashMap<String, ArrayList<Object[]>>();
				postingQTY = new HashMap<String, ArrayList<Object[]>>();
				
		 
				HashMap<String, ArrayList<ArrayList<Object>>> techMap = new HashMap<String, ArrayList<ArrayList<Object>>>();
				
				for (int j=0;j<taModel.getRowCount();j++)
				{
					if (taModel.getValueAt(j, taColM.get("ID")).toString().equals(deModel.getValueAt(i, deColM.get("ID")).toString()))
					{
						netto = FrameTemplate.round(Double.valueOf(taModel.getValueAt(j, taColM.get("NetAmount")).toString().replace(",", ".")),2);
						gross = FrameTemplate.round(Double.valueOf(taModel.getValueAt(j, taColM.get("GrossAmount")).toString().replace(",", ".")),2);
						tax = FrameTemplate.round(Double.valueOf(taModel.getValueAt(j, taColM.get("VATAmount")).toString().replace(",", ".")),2);
						
						
							for (int q=0;q<postingDetail.size();q++)
							{
								 double value=0.00;
								 if (postingDetail.get(q).get(1).equals("N"))
								 {value = netto+cost;}
								 else if (postingDetail.get(q).get(1).equals("V"))
								 {value = tax;}
								 else if (postingDetail.get(q).get(1).equals("G"))
								 {value = gross;}
								 else if (postingDetail.get(q).get(1).equals("P"))
								 {value = cost;}
								 
									if (value!=0||cbZero.isSelected()==true)
									{
										 if (cbSum.isSelected()==false)
										 {
										 Object[] newLedRow = {"", deModel.getValueAt(i, deColM.get("FIRM")),
												 ++lp,
												 deModel.getValueAt(i, deColM.get("DOCUMENTDATE")),
												 deModel.getValueAt(i, deColM.get("ALTDOCUMENTDATE")),
												 deModel.getValueAt(i, deColM.get("SETTLEMENTDATE")),
												 deModel.getValueAt(i, deColM.get("TXT")),
												 postingDetail.get(q).get(0),
												 String.format("%.2f",Double.valueOf(FrameTemplate.round(value, 2))),
												 postingDetail.get(q).get(2).equals("0") ? false : true, 
												 deModel.getValueAt(i, deColM.get("CURRENCYCODE")),
												 "0,00", "0,00",
												 taModel.getValueAt(j, taColM.get("DIMENSION")),
												taModel.getValueAt(j, taColM.get("DIMENSION_2")),
												taModel.getValueAt(j, taColM.get("DIMENSION_3")),
												findConterId (String.valueOf(deModel.getValueAt(i, deColM.get("DIMENSION_4")))),
												taModel.getValueAt(j, taColM.get("Descriptions")).toString().length()>=255 ? taModel.getValueAt(j, taColM.get("Descriptions")).toString().substring(0, 254):taModel.getValueAt(j, taColM.get("Descriptions")).toString(),
												deModel.getValueAt(i, deColM.get("SUBACCOUNT")),
												OTYPEPos, false
												 };
										 if (postingLed.containsKey(taModel.getValueAt(j, taColM.get("ID")).toString()))
										 {
											 postingLed.get(taModel.getValueAt(j, taColM.get("ID")).toString()).add(newLedRow);
										 }else 
										 {
											 postingLed.put(taModel.getValueAt(j, taColM.get("ID")).toString(), new ArrayList<Object[]>());
											 postingLed.get(taModel.getValueAt(j, taColM.get("ID")).toString()).add(newLedRow);
										 }
										 }else 
										 {
											 String tesstype = postingDetail.get(q).get(1).toString();
											 String dim1 =  String.valueOf(taModel.getValueAt(j, taColM.get("DIMENSION"))).trim().length()<=0? "":String.valueOf(taModel.getValueAt(j, taColM.get("DIMENSION")));
											 String dim2 =  String.valueOf(taModel.getValueAt(j, taColM.get("DIMENSION_2"))).trim().length()<=0? "":String.valueOf(taModel.getValueAt(j, taColM.get("DIMENSION_2")));
											 String dim3 =  String.valueOf(taModel.getValueAt(j, taColM.get("DIMENSION_3"))).trim().length()<=0? "":String.valueOf(taModel.getValueAt(j, taColM.get("DIMENSION_3")));
											 
											 if (techMap.containsKey(tesstype))
											 {
												 boolean isFind=false;
												 for (int e = 0; e<techMap.get(tesstype).size();e++)
												 { 
												
													 String dim1T =  String.valueOf(techMap.get(tesstype).get(e).get(0)).trim().length()<=0? "": String.valueOf(techMap.get(tesstype).get(e).get(0));
													 String dim2T =  String.valueOf(techMap.get(tesstype).get(e).get(1)).trim().length()<=0? "": String.valueOf(techMap.get(tesstype).get(e).get(1));
													 String dim3T =  String.valueOf(techMap.get(tesstype).get(e).get(2)).trim().length()<=0? "": String.valueOf(techMap.get(tesstype).get(e).get(2));
													 
													 
													 if (	 dim1T.equals(dim1)&&
															 dim2T.equals(dim2)&&
															 dim3T.equals(dim3)	)
													 {
														 double newValue =FrameTemplate.round(value +Double.valueOf(techMap.get(tesstype).get(e).get(3).toString()),2);
														 techMap.get(tesstype).get(e).set(3, newValue);
														 isFind=true;
														 break;
													 }
												 }
												 if (isFind==false)
												 {
													 techMap.get(tesstype).add(new ArrayList<Object>(Arrays.asList(dim1, dim2, dim3, value)));  //
												 }
											 }else
											 {
												 techMap.put(tesstype, new ArrayList<ArrayList<Object>>(Arrays.asList(new ArrayList<Object>(Arrays.asList(dim1, dim2, dim3, value)))));
	
											 }
										 }
									} 
									
							}
						
					}
					if (cbQuantity.isSelected()==true)
					{
						
						if (deModel.getValueAt(i, deColM.get("ID")).toString().equals(taModel.getValueAt(j, taColM.get("ID")).toString()))
						{
							double quantity = Double.valueOf(taModel.getValueAt(j, taColM.get("Qunatity")).toString().replace(",","."));
							double NetUnitPrice = Double.valueOf(taModel.getValueAt(j, taColM.get("NetUnitPrice")).toString().replace(",","."));
							for (int q=0;q<postingQTYDetail.size();q++)
							{
						 
								
								Object[] newRowQTY = {"", deModel.getValueAt(i, deColM.get("FIRM")),
													 ++lpQ,
													 deModel.getValueAt(i, deColM.get("DOCUMENTDATE")),
													 deModel.getValueAt(i, deColM.get("ALTDOCUMENTDATE")),
													 deModel.getValueAt(i, deColM.get("SETTLEMENTDATE")),
													 String.valueOf(deModel.getValueAt(i, deColM.get("TXT"))),
													 postingQTYDetail.get(q).get(0),
													 String.format("%.2f",Double.valueOf(FrameTemplate.round(quantity, 2))),
													 postingQTYDetail.get(q).get(2).equals("0") ? false : true, 
													 taModel.getValueAt(j, taColM.get("Unit")),
													 String.format("%.2f",Double.valueOf(FrameTemplate.round(NetUnitPrice, 2))),
													taModel.getValueAt(j, taColM.get("DIMENSION")),
													taModel.getValueAt(j, taColM.get("DIMENSION_2")),
													taModel.getValueAt(j, taColM.get("DIMENSION_3")),
													String.valueOf(findConterId (String.valueOf(deModel.getValueAt(i, deColM.get("DIMENSION_4"))))),
													deModel.getValueAt(i, deColM.get("SUBACCOUNT")),
													OTYPEPos, false,
													taModel.getValueAt(j, taColM.get("Descriptions")).toString().length()>=255 ? taModel.getValueAt(j, taColM.get("Descriptions")).toString().substring(0, 254):taModel.getValueAt(j, taColM.get("Descriptions")).toString()
													 };
												 if (postingQTY.containsKey(deModel.getValueAt(i, deColM.get("ID")).toString()))
												 {
													 postingQTY.get(deModel.getValueAt(i, deColM.get("ID")).toString()).add(newRowQTY);
												 }else 
												 {
													 postingQTY.put(deModel.getValueAt(i, deColM.get("ID")).toString(), new ArrayList<Object[]>());
													 postingQTY.get(deModel.getValueAt(i, deColM.get("ID")).toString()).add(newRowQTY);
												 }
								
								
								//postingQTY
							}
						}
						
					}
					
				}
				
				
				 if (cbSum.isSelected()==true)
				 {
					 for (int q=0;q<postingDetail.size();q++)
					 {
						 if (techMap.containsKey(postingDetail.get(q).get(1)))
						 {
							 for (int k=0;k<techMap.get(postingDetail.get(q).get(1)).size();k++)
							 {
								 Object[] newLedRow = {"", deModel.getValueAt(i, deColM.get("FIRM")),
										 ++lp,
										 deModel.getValueAt(i, deColM.get("DOCUMENTDATE")),
										 deModel.getValueAt(i, deColM.get("ALTDOCUMENTDATE")),
										 deModel.getValueAt(i, deColM.get("SETTLEMENTDATE")),
										 deModel.getValueAt(i, deColM.get("TXT")),
										 postingDetail.get(q).get(0),
										 String.format("%.2f",Double.valueOf(FrameTemplate.round(Double.valueOf((double)techMap.get(postingDetail.get(q).get(1)).get(k).get(3)), 2))),
										 postingDetail.get(q).get(2).equals("0") ? false : true, 
										 deModel.getValueAt(i, deColM.get("CURRENCYCODE")),
										 "0,00", "0,00",
										 techMap.get(postingDetail.get(q).get(1)).get(k).get(0),
										 techMap.get(postingDetail.get(q).get(1)).get(k).get(1),
										 techMap.get(postingDetail.get(q).get(1)).get(k).get(2),
										 findConterId (String.valueOf(deModel.getValueAt(i, deColM.get("DIMENSION_4")))),
										"",
										deModel.getValueAt(i, deColM.get("SUBACCOUNT")),
										OTYPEPos, false
										 };
								 if (postingLed.containsKey(deModel.getValueAt(i, deColM.get("ID")).toString()))
								 {
									 postingLed.get(deModel.getValueAt(i, deColM.get("ID")).toString()).add(newLedRow);
								 }else 
								 {
									 postingLed.put(deModel.getValueAt(i, deColM.get("ID")).toString(), new ArrayList<Object[]>());
									 postingLed.get(deModel.getValueAt(i, deColM.get("ID")).toString()).add(newLedRow);
								 }
							 }
						 }
				 }
				 }
				
				
		//System.out.print("test");
			}
			
			window.setVisible(false);
		}
		
	
		private boolean  checkData()
		{
			boolean isOK = true;
			ArrayList<String> logs = new ArrayList<String>();
			for (int i=0;i<deModel.getRowCount();i++)
			{
				String lp =String.valueOf(deModel.getValueAt(i, deColM.get("FIRM")));
				///Czy firma istnieje? 
				if (!firmsMap.containsKey(String.valueOf(deModel.getValueAt(i, deColM.get("FIRM")))))
				{
					isOK= false;
					logs.add(lp + ": firma nie istnieje w systemie.");
				}
				//czy wybrany kontrachent istnieje (dopuszcalny pusty)
				if (String.valueOf(deModel.getValueAt(i, deColM.get("DIMENSION_4"))).trim().length()>0)
				{
					boolean findConter = false;
					for (int q=1;q<counterparty.size();q++)
					{
						if (counterparty.get(q).get(1).equals(String.valueOf(deModel.getValueAt(i, deColM.get("DIMENSION_4"))).trim()))
						{
							findConter = true;
							break;
						}
					}
					if (findConter==false)
					{
						isOK= false;
						logs.add(lp + ": brak kontrahenta w systemie.");
					}
				}
				//Sprawdzanie dat
				if (deDateCheck(String.valueOf(deModel.getValueAt(i, deColM.get("DOCUMENTDATE"))))!=true||
						deDateCheck(String.valueOf(deModel.getValueAt(i, deColM.get("ALTDOCUMENTDATE"))))!=true||
						deDateCheck(String.valueOf(deModel.getValueAt(i, deColM.get("SETTLEMENTDATE"))))!=true)
						{
							isOK= false;
							logs.add(lp + ": b³êdny format dat.");
						}
				//waluty
				if (String.valueOf(deModel.getValueAt(i, deColM.get("CURRENCYCODE"))).length()!=3)
				{
					isOK= false;
					logs.add(lp + ": b³êdna waluta.");
				}
				if (String.valueOf(deModel.getValueAt(i, deColM.get("OTYPE"))).length()!=6)
				{
					isOK= false;
					logs.add(lp + ": brak kodu.");
				}
				if (String.valueOf(deModel.getValueAt(i, deColM.get("ACCOUNTNUM"))).trim().length()<=0)
				{
					isOK= false;
					logs.add(lp + ": brak konta.");
				}
				///czy jest inikalna
				ArrayList<ArrayList<String>> unique = new StoredProcedures().genUniversalArray("isUniqueInvoice", new ArrayList<String>(Arrays.asList(
																														String.valueOf(deModel.getValueAt(i, deColM.get("FIRM"))),
																														findConterId(String.valueOf(deModel.getValueAt(i, deColM.get("DIMENSION_4")))),
																														" '" + String.valueOf(deModel.getValueAt(i, deColM.get("TXT"))) +"'"
																																)));
				if (unique.size()>1&&!unique.get(1).get(0).equals("BRAK"))
				{
					int reply = JOptionPane.showConfirmDialog(null, "Istneje ju¿ faktura nr: " +String.valueOf(deModel.getValueAt(i, deColM.get("TXT"))) +". Czy na pewno chcesz wprowadziæ kolejn¹", unique.get(1).get(0), JOptionPane.YES_NO_OPTION);
					if (reply == JOptionPane.NO_OPTION)
					{
						isOK= false;
						logs.add(lp + ": faktura ju¿ wprowadzona.");						
					}
				}
			}
			for (int q=0;q<logs.size();q++)
			{
				System.out.println(logs.get(q));
			}
			
			return isOK;
		}
		
		
		private boolean deDateCheck(String inDate)
		{
			try {	Date date;
					date = new SimpleDateFormat("yyyy-MM-dd").parse(inDate);
			} catch (ParseException e) {
				return false;
			}
			return true;
		}
		@Override
		public void mouseClicked(MouseEvent e) {
			// TODO Auto-generated method stub
			
		}

		@Override
		public void mousePressed(MouseEvent e) {
			// TODO Auto-generated method stub

		}

		@Override
		public void mouseReleased(MouseEvent e) {
			// TODO Auto-generated method stub
			if (e.getSource()==deTable)
			{
				if (deTable.getSelectedColumn()>-1&&deTable.getSelectedRow()>-1)
				{
					HashMap<String, Integer> deColumT = deFrame.getColumnNumbers(deTable, deSysAll);
					sumValue(deTable.getValueAt(deTable.getSelectedRow(), deColumT.get("ID")).toString());
					if (e.getClickCount()==2&&deTable.getSelectedColumn()== deColumT.get("DIMENSION_4"))
					{
						deFrame.getSelectionRunWithParameters(deTable, "getConterparty",new ArrayList<String>() , "Dict_Conterparty.yml", deTable.getColumnName(deTable.getSelectedColumn()),1);
					}else if (e.getClickCount()==2&&deTable.getSelectedColumn()== deColumT.get("ACCOUNTNUM"))
					{
						deColT =  deFrame.getColumnNumbers(deTable, deSysAll);
						System.out.print(deTable.getValueAt(deTable.getSelectedRow(), deColT.get("FIRM")));
						if (firmsMap.containsKey(deTable.getValueAt(deTable.getSelectedRow(), deColT.get("FIRM")).toString()))
						{
							ArrayList<String> parameters = new ArrayList<String>(Arrays.asList("1",firmsMap.get(deTable.getValueAt(deTable.getSelectedRow(), deColT.get("FIRM")).toString()).get(1)));
							deFrame.getSelectionRunWithParameters(deTable,"getAccountToPostings",parameters, "Dict_PlanAccount_sys.yml", "Konto", 0);
						}else
						{
							JOptionPane.showMessageDialog(null, "Nierozpoznanan firma" , "Informacja", JOptionPane.INFORMATION_MESSAGE);
						}
					}else if (e.getClickCount()==2&&deTable.getSelectedColumn()== deColumT.get("SUBACCOUNT"))
					{
						deColT =  deFrame.getColumnNumbers(deTable, deSysAll);
				
						String conterID = findConterId (String.valueOf(deTable.getValueAt(deTable.getSelectedRow(), deColT.get("DIMENSION_4"))));
						String curr = String.valueOf(deTable.getValueAt(deTable.getSelectedRow(), deColT.get("CURRENCYCODE")));
						
						if (firmsMap.containsKey(deTable.getValueAt(deTable.getSelectedRow(), deColT.get("FIRM")).toString())&&
								conterID!=null&&
								accountMap.containsKey(deTable.getValueAt(deTable.getSelectedRow(), deColT.get("FIRM")).toString())
								&&accountMap.get(deTable.getValueAt(deTable.getSelectedRow(), deColT.get("FIRM")).toString()).containsKey("SIMCOS")
								&&accountMap.get(deTable.getValueAt(deTable.getSelectedRow(), deColT.get("FIRM")).toString()).containsKey("FORCOS")
								&&accountMap.get(deTable.getValueAt(deTable.getSelectedRow(), deColT.get("FIRM")).toString()).containsKey("PRECOS")
								&&curr.length()==3)
						{
							String firmID = deTable.getValueAt(deTable.getSelectedRow(), deColT.get("FIRM")).toString();
							String account = ""; 
							if (String.valueOf(deTable.getValueAt(deTable.getSelectedRow(), deColT.get("OTYPE"))).equals("COSSIM"))
							{
								account = accountMap.get(firmsMap.get(deTable.getValueAt(deTable.getSelectedRow(), deColT.get("FIRM")).toString()).get(1)).get("SIMCOS");
							}else if (String.valueOf(deTable.getValueAt(deTable.getSelectedRow(), deColT.get("OTYPE"))).equals("COSFOR")
									||String.valueOf(deTable.getValueAt(deTable.getSelectedRow(), deColT.get("OTYPE"))).equals("COSPRE"))
							{
								account = accountMap.get(firmsMap.get(deTable.getValueAt(deTable.getSelectedRow(), deColT.get("FIRM")).toString()).get(1)).get("FORCOS");
							}
							else if (String.valueOf(deTable.getValueAt(deTable.getSelectedRow(), deColT.get("OTYPE"))).equals("COSEND"))
							{
								account = accountMap.get(firmsMap.get(deTable.getValueAt(deTable.getSelectedRow(), deColT.get("FIRM")).toString()).get(1)).get("PRECOS");
							}
							ArrayList<String> parameters = new ArrayList<String>(Arrays.asList(firmID,conterID,"'" + account +"'", curr));
							deFrame.getSelectionRunWithParameters(deTable,"getAccBalanceBySubaccount",parameters, "ITesseractProForm_List_sys.yml", "Konto", 2);
							ListFromBase.addAll(deFrame.getListFromBase());
		
							
							
						}
					}
				}
			}else if (e.getSource()==taTable&&e.getClickCount()==2)
			{
				HashMap<String, Integer> taColumT = taFrame.getColumnNumbers(taTable, taSysAll);
				if (taTable.getSelectedColumn()== taColumT.get("DIMENSION"))
				{
					if (!findFirmId(taTable.getValueAt(taTable.getSelectedRow(), taColumT.get("ID")).toString()).equals(null))
					{
					ArrayList<String> parameters = new ArrayList<String>(Arrays.asList(findFirmId(taTable.getValueAt(taTable.getSelectedRow(), taColumT.get("ID")).toString())));
					taFrame.getSelectionRunWithParameters(taTable, " getInstrumentToPosting ",parameters, "Dict_Instruments_sys.yml", "Projekty", 0);
					}
				}else if (taTable.getSelectedColumn()== taColumT.get("DIMENSION_2"))
				{
					ArrayList<String> parameters = new ArrayList<String>(Arrays.asList("'Dimension_2.Type'"));
					taFrame.getSelectionRunWithParameters(taTable, " getDomainValue ",parameters, "Dict_DomainValue.yml", "Typ", 0);
				}else if (taTable.getSelectedColumn()== taColumT.get("DIMENSION_3"))
				{
					ArrayList<String> parameters = new ArrayList<String>(Arrays.asList("'Dimension_3.InternalContractor'"));
					taFrame.getSelectionRunWithParameters(taTable, " getDomainValue ",parameters, "Dict_DomainValue.yml", "Kontrahent wewnêtrzny", 0);
				} 
					
			}
		}

		private String findConterId (String name)
		{
			 
			for (int i = 1; i< counterparty.size();i++)
			{
				if (counterparty.get(i).get(1).equals(name))
				{
					return counterparty.get(i).get(0);
				}
			}
			return null;
		}
		
		private String findFirmId(String id)
		{
			for (int i = 0; i<deModel.getRowCount();i++)
			{
				if (deModel.getValueAt(i, deColM.get("ID")).toString().equals(id)&&firmsMap.containsKey(deModel.getValueAt(i, deColM.get("FIRM")).toString()))
				{
					return deModel.getValueAt(i, deColM.get("FIRM")).toString();
				}
			}
			return null;
		}
		
		@Override
		public void mouseEntered(MouseEvent e) {
			// TODO Auto-generated method stub
			
		}

		@Override
		public void mouseExited(MouseEvent e) {
			// TODO Auto-generated method stub
			
		}
		private void sumValue(String lp)
		{
			selectID.setText(lp);
			double netto = 0.00;
			double vat = 0.00;
			double gross = 0.00;
			
			for (int i = 0; i< taModel.getRowCount(); i++)
			{
				if (taModel.getValueAt(i,  taColM.get("ID")).toString().equals(lp))
				{
				
					netto += Double.valueOf(taModel.getValueAt(i, taColM.get("NetAmount")).toString().replace(",", "."));
					gross += Double.valueOf(taModel.getValueAt(i, taColM.get("GrossAmount")).toString().replace(",", "."));
					vat += Double.valueOf(taModel.getValueAt(i, taColM.get("VATAmount")).toString().replace(",", "."));
				}
			}
			sumVat.setText(String.valueOf(FrameTemplate.round(vat, 2)).replace(".", ","));
			sumNetto.setText(String.valueOf(FrameTemplate.round(netto, 2)).replace(".", ","));
			sumGross.setText(String.valueOf(FrameTemplate.round(gross, 2)).replace(".", ","));
		}

		@Override
		public void tableChanged(TableModelEvent e) {
			// TODO Auto-generated method stub
			if (e.getSource()==taModel&&taModel.getValueAt(e.getFirstRow(), taColM.get("ID")).toString().equals(selectID.getText()) 
					&&(e.getColumn()==taColM.get("GrossAmount")||e.getColumn()==taColM.get("VATAmount")||e.getColumn()==taColM.get("NetAmount")))
			{
				sumValue(taModel.getValueAt(e.getFirstRow(), taColM.get("ID")).toString());
			}
			if (e.getSource()==taModel&&e.getType()==0)
			{ 
				
				if ((e.getColumn()==taColM.get("NetUnitPrice")
						||e.getColumn()==taColM.get("Qunatity")))	
				{
					taModel.setValueAt(teTableNetto(e.getFirstRow()), e.getFirstRow(), taColM.get("NetAmount"));
					taModel.setValueAt(teTableVat(e.getFirstRow()), e.getFirstRow(), taColM.get("VATAmount"));
					taModel.setValueAt(teTableGross(e.getFirstRow()), e.getFirstRow(), taColM.get("GrossAmount"));
				}else if (e.getColumn()==taColM.get("VAT"))//e.getColumn()==taColM.get("NetAmount")||
				{
					taModel.setValueAt(teTableVat(e.getFirstRow()), e.getFirstRow(), taColM.get("VATAmount"));
					taModel.setValueAt(teTableGross(e.getFirstRow()), e.getFirstRow(), taColM.get("GrossAmount"));
				}
			}
			if (e.getSource()==deModel&&e.getType()==1)
			{
				deModel.setValueAt(deModel.getRowCount(), e.getFirstRow(), 0);
			}else if (e.getSource()==deModel&&e.getType()==0)
			{
				String frimID = deModel.getValueAt(e.getFirstRow(), deColM.get("FIRM")).toString();
				if (e.getColumn()== deColM.get("FIRM")
						&&firmsMap.containsKey(frimID)
						&&accountMap.containsKey(firmsMap.get(frimID).get(1))
						&&accountMap.get(firmsMap.get(frimID).get(1)).containsKey("SIMLIA"))
				{
					deModel.setValueAt(firmsMap.get(frimID).get(0),e.getFirstRow(), deColM.get("CURRENCYCODE"));
					deModel.setValueAt("COSSIM",e.getFirstRow(), deColM.get("OTYPE"));
					deModel.setValueAt(accountMap.get(firmsMap.get(frimID).get(1)).get("SIMLIA"),e.getFirstRow(), deColM.get("ACCOUNTNUM"));
				}
					 
			}else if (e.getSource()==taModel&&e.getType()==1)
			{
				if (selectID.getText().length()>0)
				{
				taModel.setValueAt(selectID.getText(), e.getFirstRow(), taColM.get("ID"));
				taModel.setValueAt(getNewLp(Integer.parseInt(selectID.getText())), e.getFirstRow(), taColM.get("Lp"));
				}
			}
			
		}
		
		private int getNewLp(int lp)
		{
			int max = 0;
			for (int i=0; i<taModel.getRowCount();i++)
			{ 
				String clp= taModel.getValueAt(i, taColM.get("ID")).toString().trim().length()>0 ? (String) taModel.getValueAt(i, taColM.get("ID")) : "0";
				
				if (Integer.parseInt(clp)==lp)
				{
					String clps= String.valueOf(taModel.getValueAt(i, taColM.get("Lp"))).trim().length()>0 ?  String.valueOf(taModel.getValueAt(i, taColM.get("Lp"))) : "0";
					if (max< (Integer.parseInt(clps)))
					{
						max =Integer.parseInt(clps);
					}
				}
			}
			return ++max;
		}
		
		private String teTableVat(int row)
		{
			double vat = 0.00;
			double tax =Double.valueOf(taModel.getValueAt(row, taColM.get("VAT")).toString().replace(",", "."));
			double NetAmount =Double.valueOf(taModel.getValueAt(row, taColM.get("NetAmount")).toString().replace(",", "."));
			vat = FrameTemplate.round((tax*NetAmount)/100,2);
			
			return String.format("%.2f",vat);
		}
		private String teTableNetto(int row)
		{
		   double netto = 0.00;
		   double quantity = Double.valueOf(taModel.getValueAt(row, taColM.get("Qunatity")).toString().replace(",", "."));
		   double NetUnitPrice = Double.valueOf(taModel.getValueAt(row, taColM.get("NetUnitPrice")).toString().replace(",", "."));
		   netto = FrameTemplate.round((quantity*NetUnitPrice),2);
		   return String.format("%.2f",netto);
		}

		private String teTableGross(int row)
		{
			double tax =Double.valueOf(taModel.getValueAt(row, taColM.get("VATAmount")).toString().replace(",", "."));
			double NetAmount =Double.valueOf(taModel.getValueAt(row, taColM.get("NetAmount")).toString().replace(",", "."));
			double gross = 0.00;
			gross = FrameTemplate.round(tax+NetAmount,2);
			return String.format("%.2f",gross);
		}
	 

		
		
	}

}
