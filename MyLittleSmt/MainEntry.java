package MyLittleSmt;

import java.awt.BorderLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.time.LocalDate;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JPanel;

import MyLittleSmtFrame.IBank;
import MyLittleSmtFrame.IFirms;
import MyLittleSmtFrame.IInstruments;
import MyLittleSmtFrame.IOrders;
import MyLittleSmtFrame.IPlanAccount;
import MyLittleSmtFrame.IPlanAccountN;
import MyLittleSmtFrame.IPositions;
import MyLittleSmtFrame.IProjectInvoice;
import MyLittleSmtFrame.IWarehouse;

class MainEntry extends JFrame implements ActionListener 
{
/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
public String GlobUser = System.getProperty("user.name")+ "aaa";
public LocalDate GlobDate = LocalDate.now();

JMenuBar menubar;
JMenu menuBA, Config, Project, Accounting, Cost, Income;
JMenuItem Cfirm, CPlanAccount, CPCalendar, CPCurrency, CConterParty, menuJPKvsBA, menuCost, projectList, ordesList, ledgerPositions, incomeProjectPositions, BankAccount, Warehouse;
JButton upButt;
FrameTemplate frametemplate;
JFrame calendar, currency, conter, instrument, orders, positions, firms, projectIncome, planAccount, mainBank;
//ICalendar calendar;
//ICurrency currency;
//ICounterparties conter;
//IInstruments instrument;
//IOrders orders;

private JPanel upmenuglob; 
	public MainEntry() 
	{

		frametemplate = new FrameTemplate();
		upmenuglob = frametemplate.GetUpMenu(true);
		
		setIconImage(FrameTemplateImageIcon.iconSys().getImage());
		setSize(1300,650);
		setTitle("MainEntry");
		setLayout(new BorderLayout(0,0));
		
		menubar = new JMenuBar();
		setJMenuBar(menubar);
		
		Config = new JMenu("Konfiguracja");
		Cfirm = new JMenuItem("Firmy");
		Config.add(Cfirm);
		Cfirm.addActionListener(this);
		CPlanAccount = new JMenuItem("Plan kont");
		Config.add(CPlanAccount);
		CPlanAccount.addActionListener(this);
		CConterParty = new JMenuItem("Kontrahenci");
		Config.add(CConterParty);
		CConterParty.addActionListener(this);
		CPCalendar = new JMenuItem("Kalendarz");
		Config.add(CPCalendar);
		CPCalendar.addActionListener(this);
		CPCurrency = new JMenuItem("Waluta");
		Config.add(CPCurrency);
		CPCurrency.addActionListener(this);
		
		Project = new JMenu("Projekty");
		projectList = new JMenuItem("Lista projekt�w");
		Project.add(projectList);
		projectList.addActionListener(this);
		ordesList = new JMenuItem("Pozycje");
		Project.add(ordesList);
		ordesList.addActionListener(this);
		Warehouse = new JMenuItem("Magazyn");
		Project.add(Warehouse);
		Warehouse.addActionListener(this);
		
		Accounting = new JMenu("Ksi�gowo��");
		ledgerPositions = new JMenuItem("Ksi�gowania r�czne");
		ledgerPositions.addActionListener(this);
		Accounting.add(ledgerPositions);
		Accounting.addSeparator();
		Cost = new JMenu("Koszty");
		Income = new JMenu("Przychody");
			
			menuCost =new JMenuItem("Faktury kosztowe");
			menuCost.addActionListener(this);
			Cost.add(menuCost);
			
			incomeProjectPositions = new JMenuItem("Faktury projektowe");
			incomeProjectPositions.addActionListener(this);
			Income.add(incomeProjectPositions);
		Accounting.add(Cost);
		//Accounting.addSeparator();
		Accounting.add(Income);
		
		
		
		//Expan = new JMenu("Koszty");
		//menuInvoice = new JMenuItem("Faktury");
		//Expan.add(menuInvoice);
		//menuInvoice.addActionListener(this);
		
		menuBA = new JMenu("Bank");
		
		BankAccount = new JMenuItem("Wyci�gi bankowe");
		menuBA.add(BankAccount);
		menuJPKvsBA = new JMenuItem("JPK vs WB");
		menuBA.add(menuJPKvsBA);
		
		menubar.add(Config);
		menubar.add(Project);
		menubar.add(Accounting);
		//menubar.add(Expan);
		menubar.add(menuBA);
		
		
		menuJPKvsBA.addActionListener(this);
		BankAccount.addActionListener(this);
		
		upButt= frametemplate.getBFirm();
		upButt.addActionListener(this);
		
		//TheHandlerGlob handler = new TheHandlerGlob();
		add(upmenuglob, BorderLayout.PAGE_START);
		
		//import from base 
		//
		refreshCalendar();
		refreshCurrency();
		refreshFirms();
		
	}
	public void refreshCalendar()
	{
		calendar = new ICalendar(500,500, "Kalendarz");
	}
	public void refreshCurrency()
	{
		currency = new ICurrency(1000,700,"Waluta");
	}
	public void refreshFirms()
	{
		firms = new IFirms(0,0,"Firmy");
	}
	 @Override
	public void actionPerformed(ActionEvent e) 
	 {
		Object source = e.getSource();
		//System.out.print(source);
		if (source==menuJPKvsBA)
		{
			new JpkVsBsFrame().setVisible(true);	
		}
	//	if (source ==menuInvoice)
	//	{
			
	//	}
		if (source==Cfirm)
		{
			firms = new IFirms(500,500, "Firmy");
			firms.setVisible(true);
			firms.setExtendedState(JFrame.MAXIMIZED_BOTH);
			
			//new ICompany().setVisible(true);
			//firms = new IPositions(500,900,"Firmy");
			//firms.setVisible(true);
			
		}
		else if (source==CPlanAccount)
		{
			 	//new IPlanAccount().setVisible(true);
				planAccount = new IPlanAccountN(800,700,"Plan kont");
				planAccount.setVisible(true);
				//planAccount.setExtendedState(JFrame.MAXIMIZED_BOTH);
			 	
		}else if (source==CConterParty)
		{		conter = new ICounterparties(500,500, "Kontrahenci");
				conter.setVisible(true);
			//	new IConterparty().setVisible(true);
		}else if (source==CPCalendar)
		{
			calendar = new ICalendar(500,500, "Kalendarz");
			calendar.setVisible(true);
			//calendar.setExtendedState(JFrame.MAXIMIZED_BOTH); 
		}else if (source==CPCurrency)
		{
			refreshCalendar();
			currency.setVisible(true);
		}else if (source==projectList)
		{
			instrument = new IInstruments(500, 900, "Instrumenty");
			instrument.setVisible(true);
			instrument.setExtendedState(JFrame.MAXIMIZED_BOTH);
		}else if (source==ordesList)
		{
			orders = new IOrders(500, 900, "Pozycje zm�wienia","", "");
			orders.setVisible(true);
			orders.setExtendedState(JFrame.MAXIMIZED_BOTH);
		}else if (source==ledgerPositions)
		{
			positions = new IPositions(500,900,"Ksi�gowania r�czne", "MANPOS");
			positions.setVisible(true);
			positions.setExtendedState(JFrame.MAXIMIZED_BOTH);
		}else if (source==menuCost)
		{
			positions = new IPositions(500,900,"Ksi�gowania r�czne", "COSINV");
			positions.setVisible(true);
			positions.setExtendedState(JFrame.MAXIMIZED_BOTH);
		}else if (source==incomeProjectPositions)
		{
			projectIncome= new IProjectInvoice(500, 900, "Faktury projektowe", frametemplate.getBFirm().getText().toString());
			projectIncome.setVisible(true);
			projectIncome.setExtendedState(JFrame.MAXIMIZED_BOTH);
		}else if (source==BankAccount)
		{
			mainBank = new IBank(700,850, "Wyci�gi bankowe", frametemplate.getBFirm().getText().toString());
			mainBank.setVisible(true);
		}else if (source==Warehouse)
		{
			IWarehouse ware = new IWarehouse(frametemplate.getBFirm().getText().toString());
		}
		
		
	}
	public static void main(String [] args)
	 {
		
		 MainEntry okienko = new MainEntry();
		 okienko.setVisible(true);
		 okienko.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
	 }	
	 
	 
	
}
