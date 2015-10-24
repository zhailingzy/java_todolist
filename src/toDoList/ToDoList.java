package toDoList;

import java.awt.Component;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ContainerEvent;
import java.awt.event.ContainerListener;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.IOException;
import java.sql.SQLException;
import java.util.Properties;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.ScrollPaneConstants;

import shareTools.IniCtrl;

import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JCheckBox;

public class ToDoList extends JFrame {
	/**
	 * 
	 */
	private static String programVersion = "1.0";//
	private static final long serialVersionUID = -2897052723602429346L;
	private ToDoListContainer mycontainer;

	public static void main(String[] args) throws IOException, ClassNotFoundException, SQLException {
		Properties defpro = new Properties();
		defpro.setProperty("database", "defdb.db");
		IniCtrl myini = new IniCtrl(".\\Setting.ini", defpro, true);
		String mydb = myini.getIniSetting("database");
		new ToDoList("ToDo List V".concat(programVersion), mydb);
	}

	@Override
	public void pack() {
		// TODO Auto-generated method stub
		if (mycontainer.getCellCount() < 10)
			super.pack();
	}

	public ToDoList(String title, String db) throws ClassNotFoundException, SQLException {
		super(title);
		// TODO Auto-generated constructor stub
		mycontainer = new ToDoListContainer(db);
		mycontainer.addContainerListener(new ContainerListener() {
			@Override
			public void componentRemoved(ContainerEvent e) {
				// TODO Auto-generated method stub
				validate();
				pack();
			}

			@Override
			public void componentAdded(ContainerEvent e) {
				// TODO Auto-generated method stub
				validate();
				pack();
			}
		});
		addWindowListener(new WindowAdapter() {

			@Override
			public void windowClosing(WindowEvent e) {
				// TODO Auto-generated method stub
				mycontainer.bfClose();
				super.windowClosing(e);
			}

		});
		getContentPane().setLayout(new BoxLayout(getContentPane(), BoxLayout.Y_AXIS));
		JPanel headerHost = new JPanel();
		headerHost.setLayout(new BoxLayout(headerHost, BoxLayout.X_AXIS));
		JButton btn_new = new JButton("NEW");
		btn_new.setAlignmentX(Component.CENTER_ALIGNMENT);
		btn_new.setActionCommand("NEW");
		btn_new.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				// TODO Auto-generated method stub
				switch (e.getActionCommand()) {
				case "NEW":
					try {
						mycontainer.addNewCell();
					} catch (ClassNotFoundException | SQLException e1) {
						// TODO Auto-generated catch block
						e1.printStackTrace();
					}
					break;
				default:
					break;
				}
			}
		});
		headerHost.add(btn_new);
		JCheckBox chckbxIncludeDone = new JCheckBox("Include Done");
		chckbxIncludeDone.setAlignmentX(Component.CENTER_ALIGNMENT);
		chckbxIncludeDone.addItemListener(new ItemListener() {
			@Override
			public void itemStateChanged(ItemEvent e) {
				// TODO Auto-generated method stub
				switch (e.getStateChange()) {
				case ItemEvent.SELECTED:
					try {
						mycontainer.addDoneCell();
					} catch (ClassNotFoundException | SQLException e1) {
						// TODO Auto-generated catch block
						e1.printStackTrace();
					}
					break;
				case ItemEvent.DESELECTED:
					mycontainer.removeDoneCell();
					break;
				default:
					break;
				}
			}
		});
		headerHost.add(chckbxIncludeDone);
		add(headerHost);
		JScrollPane scrollPane = new JScrollPane(mycontainer);
		scrollPane.setHorizontalScrollBarPolicy(ScrollPaneConstants.HORIZONTAL_SCROLLBAR_AS_NEEDED);
		scrollPane.setVerticalScrollBarPolicy(ScrollPaneConstants.VERTICAL_SCROLLBAR_ALWAYS);
		add(scrollPane);
		getContentPane().add(scrollPane);
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		pack();
		setVisible(true);
		mycontainer.addWaitingCell();
	}
}
