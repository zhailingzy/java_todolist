package toDoList;

import javax.swing.JPanel;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JButton;
import javax.swing.JTextField;
import javax.swing.JCheckBox;
import java.awt.GridBagLayout;
import java.awt.GridBagConstraints;
import java.awt.Insets;
import java.util.Date;
import javax.swing.SwingConstants;
import javax.swing.UIManager;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.text.DateFormatter;

import shareTools.MessageBox;

import java.sql.SQLException;
import java.sql.Timestamp;
import java.awt.event.ItemListener;
import java.awt.event.ItemEvent;
import java.awt.event.ActionListener;
import java.awt.event.ActionEvent;

public class ToDoListCell extends JPanel {
	/**
	 * 
	 */
	private static final long serialVersionUID = -7122358422009592782L;
	private String s_Done = "done";
	private String s_id = "id";
	private String s_Describe = "describe";
	private String s_order = "order";
	private String s_startDate = "startdate";
	private String s_endDate = "enddate";
	private boolean bl_textChanged;
	private Integer i_myDone;
	private Integer i_myID;
	private String s_myDescribe;
	private Integer i_myOrder;
	private Date d_myStartDate, d_myEndDate;
	private JCheckBox chckbxDone;
	private JTextField textFieldDescribe;
	private JLabel lblEndDate;
	private JButton btnDown;
	private JButton btnUp;
	private ToDoListContainer myParent;
	private JLabel lblStartDate;
	private ToDoListCell mySelf;

	private void newCell() throws ClassNotFoundException, SQLException {
		String sqlstr = "insert into xf_todolist (xf_id,xf_done,xf_order,xf_startdate) values( "
				.concat(i_myID.toString()).concat(" , ").concat(i_myDone.toString()).concat(" , ")
				.concat(i_myOrder.toString()).concat(" , ").concat(String.valueOf(d_myStartDate.getTime()))
				.concat(" )");
		myParent.updateCell(sqlstr);
	}

	private void updateCell(String property, Object value) throws ClassNotFoundException, SQLException {
		String sqlstr = "update xf_todolist set xf_".concat(property).concat(" =")
				.concat(value instanceof String ? "'" : "")
				.concat(value instanceof Date ? String.valueOf(((Date) value).getTime()) : value.toString())
				.concat(value instanceof String ? "'" : "").concat(" where xf_id =").concat(i_myID.toString());
		myParent.updateCell(sqlstr);
	}

	private void AddComponent() {
		GridBagLayout gridBagLayout = new GridBagLayout();
		gridBagLayout.columnWidths = new int[] { 9, 150, 150, 5, 0 };
		gridBagLayout.rowHeights = new int[] { 5, 6, 0 };
		gridBagLayout.columnWeights = new double[] { 0.0, 0.0, 0.0, 0.0, Double.MIN_VALUE };
		gridBagLayout.rowWeights = new double[] { 0.0, 0.0, Double.MIN_VALUE };
		setLayout(gridBagLayout);
		textFieldDescribe = new JTextField(s_myDescribe);
		textFieldDescribe.setColumns(10);
		textFieldDescribe.getDocument().addDocumentListener(new DocumentListener() {
			@Override
			public void removeUpdate(DocumentEvent e) {
				// TODO Auto-generated method stub
				s_myDescribe = textFieldDescribe.getText();
				bl_textChanged = true;
			}

			@Override
			public void insertUpdate(DocumentEvent e) {
				// TODO Auto-generated method stub
				s_myDescribe = textFieldDescribe.getText();
				bl_textChanged = true;
			}

			@Override
			public void changedUpdate(DocumentEvent e) {
				// TODO Auto-generated method stub
				s_myDescribe = textFieldDescribe.getText();
				bl_textChanged = true;
			}
		});
		GridBagConstraints gbc_textFieldDescribe = new GridBagConstraints();
		gbc_textFieldDescribe.gridwidth = 2;
		gbc_textFieldDescribe.fill = GridBagConstraints.BOTH;
		gbc_textFieldDescribe.insets = new Insets(0, 0, 5, 5);
		gbc_textFieldDescribe.gridx = 1;
		gbc_textFieldDescribe.gridy = 0;
		add(textFieldDescribe, gbc_textFieldDescribe);
		btnUp = new JButton("UP");
		btnUp.setActionCommand("UP");
		btnUp.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				switch (e.getActionCommand()) {
				case "UP":
					try {
						myParent.cellUp(mySelf);
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
		GridBagConstraints gbc_btnUp = new GridBagConstraints();
		gbc_btnUp.insets = new Insets(0, 0, 5, 0);
		gbc_btnUp.fill = GridBagConstraints.BOTH;
		gbc_btnUp.gridx = 3;
		gbc_btnUp.gridy = 0;
		add(btnUp, gbc_btnUp);
		btnDown = new JButton("DOWN");
		btnDown.setActionCommand("DOWN");
		btnDown.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				switch (e.getActionCommand()) {
				case "DOWN":
					if (i_myOrder >= 20)
						try {
							myParent.cellDown(mySelf);
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
		GridBagConstraints gbc_btnDown = new GridBagConstraints();
		gbc_btnDown.gridx = 3;
		gbc_btnDown.gridy = 1;
		add(btnDown, gbc_btnDown);
		chckbxDone = new JCheckBox("");
		chckbxDone.setSelected(i_myDone == 1);
		chckbxDone.setEnabled(i_myDone == 0);
		textFieldDescribe.setEditable(i_myDone == 0);
		btnUp.setEnabled(i_myDone == 0);
		btnDown.setEnabled(i_myDone == 0);
		chckbxDone.addItemListener(new ItemListener() {
			@Override
			public void itemStateChanged(ItemEvent e) {
				switch (e.getStateChange()) {
				case ItemEvent.SELECTED:
					i_myDone = 1;
					try {
						updateCell(s_Done, i_myDone);
					} catch (ClassNotFoundException | SQLException e1) {
						// TODO Auto-generated catch block
						e1.printStackTrace();
						chckbxDone.setSelected(false);
					}
					d_myEndDate = new Date();
					try {
						updateCell(s_endDate, d_myEndDate);
					} catch (ClassNotFoundException | SQLException e1) {
						// TODO Auto-generated catch block
						e1.printStackTrace();
						chckbxDone.setSelected(false);
					}
					myParent.removeDoneCell();
					break;
				case ItemEvent.DESELECTED:
					i_myDone = 0;
					break;
				default:
					break;
				}
				chckbxDone.setEnabled(i_myDone == 0);
				textFieldDescribe.setEditable(i_myDone == 0);
				btnUp.setEnabled(i_myDone == 0);
				btnDown.setEnabled(i_myDone == 0);
				lblEndDate.setText(d_myEndDate == null ? "" : new DateFormatter().getFormat().format(d_myEndDate));
			}
		});
		chckbxDone.setHorizontalAlignment(SwingConstants.CENTER);
		GridBagConstraints gbc_chckbxDone = new GridBagConstraints();
		gbc_chckbxDone.fill = GridBagConstraints.BOTH;
		gbc_chckbxDone.insets = new Insets(0, 0, 5, 5);
		gbc_chckbxDone.gridx = 0;
		gbc_chckbxDone.gridy = 0;
		add(chckbxDone, gbc_chckbxDone);
		lblStartDate = new JLabel(d_myStartDate == null ? "" : d_myStartDate.toString());
		GridBagConstraints gbc_lblStartDate = new GridBagConstraints();
		gbc_lblStartDate.insets = new Insets(0, 0, 0, 5);
		gbc_lblStartDate.gridx = 1;
		gbc_lblStartDate.gridy = 1;
		add(lblStartDate, gbc_lblStartDate);
		lblEndDate = new JLabel(d_myEndDate == null ? "" : d_myEndDate.toString());
		GridBagConstraints gbc_lblEndDate = new GridBagConstraints();
		gbc_lblEndDate.insets = new Insets(0, 0, 0, 5);
		gbc_lblEndDate.gridx = 2;
		gbc_lblEndDate.gridy = 1;
		add(lblEndDate, gbc_lblEndDate);
	}

	public Integer getMyID() {
		return i_myID;
	}

	public Integer getMyDone() {
		return i_myDone;
	}

	public Integer getMyOrder() {
		return i_myOrder;
	}

	public void updateBfClose(boolean showError) throws Exception {
		String s_updateErrorTitle = "Error During Updating";
		MessageBox temp = new MessageBox();
		if (bl_textChanged)
			try {
				updateCell(s_Describe, s_myDescribe);
			} catch (ClassNotFoundException | SQLException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
				if (showError)
					temp.showMessage(s_updateErrorTitle,
							e.getMessage().concat(System.lineSeparator()).concat(s_myDescribe),
							JOptionPane.ERROR_MESSAGE);
				throw e;
			}
	}

	public void setMyOrder(Integer myOrder) throws ClassNotFoundException, SQLException {
		updateCell(s_order, this.i_myOrder);
		this.i_myOrder = myOrder;
	}

	/**
	 * Create the panel.
	 * 
	 * @param ID
	 * @param done
	 * @param order
	 * @param describe
	 * @param startDate
	 * @param endDate
	 * @throws SQLException
	 * @throws ClassNotFoundException
	 */
	public ToDoListCell(ToDoListContainer parent) throws ClassNotFoundException, SQLException {
		// TODO Auto-generated constructor stub
		this(parent, 0, 0, 0, "", null, null);
	}

	public ToDoListCell(ToDoListContainer parent, Integer ID, Integer order)
			throws ClassNotFoundException, SQLException {
		// TODO Auto-generated constructor stub
		this(parent, ID, 0, order, "", null, null);
	}

	public ToDoListCell(ToDoListContainer parent, Integer ID, Integer order, String describe)
			throws ClassNotFoundException, SQLException {
		// TODO Auto-generated constructor stub
		this(parent, ID, 0, order, describe, null, null);
	}

	/**
	 * @throws SQLException
	 * @throws ClassNotFoundException
	 * @wbp.parser.constructor
	 */
	public ToDoListCell(ToDoListContainer parent, Integer ID, Integer done, Integer order, String describe,
			Date startDate, Date endDate) throws ClassNotFoundException, SQLException {
		bl_textChanged = false;
		mySelf = this;
		i_myID = ID;
		myParent = parent;
		i_myDone = done;
		if (i_myDone == null)
			i_myDone = 0;
		i_myOrder = order;
		s_myDescribe = describe;
		d_myStartDate = startDate;
		if (d_myStartDate == null) {
			d_myStartDate = new Timestamp(new Date().getTime());
			newCell();
		}
		d_myEndDate = endDate;
		setBorder(UIManager.getBorder("Button.border"));
		AddComponent();
	}
}
