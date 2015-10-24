package toDoList;

import java.awt.Point;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.function.Consumer;
import java.util.function.Predicate;
import java.util.stream.Collectors;
import javax.swing.BoxLayout;
import javax.swing.JPanel;

import shareTools.SqlOperator;
import shareTools.SqlOperator.dbtype;

public class ToDoListContainer extends JPanel {
	/**
	 * 
	 */
	private static final long serialVersionUID = -636628239685080837L;
	private ArrayList<ToDoListCell> myCellList;
	private String sqlitedb;

	private void addCell(ToDoListCell add) {
		myCellList.add(add);
		if (add.getMyDone() == 1) {
			add(add);
		} else {
			add(add, 0);
		}
	}

	private void removeCell(ToDoListCell remove) {
		remove(remove);
	}

	private void addDbCell(Integer done) throws ClassNotFoundException, SQLException {
		initdb(dbtype.sqlite, sqlitedb);
		SqlOperator sql = new SqlOperator(dbtype.sqlite, sqlitedb, true);
		ResultSet rs = sql.executeQuery(
				"select xf_id,xf_done,xf_order,xf_describe,xf_startdate,xf_enddate from xf_todolist where xf_done = "
						.concat(done.toString()).concat(" order by xf_done asc,xf_order asc"));
		while (rs.next()) {
			addCell(new ToDoListCell(this, rs.getInt("xf_id"), rs.getInt("xf_done"), rs.getInt("xf_order"),
					rs.getString("xf_describe"), rs.getTimestamp("xf_startdate"), rs.getTimestamp("xf_enddate")));
		}
		sql.disConnect();
	}

	private void initdb(dbtype type, String db) throws ClassNotFoundException, SQLException {
		String createtable = "CREATE TABLE xf_todolist " + "(xf_id			INT PRIMARY KEY     NOT NULL,"
				+ " xf_done		INT    NOT NULL, " + " xf_order		INT     NOT NULL, " + " xf_describe	text, "
				+ " xf_startdate	datetime NOT NULL, " + " xf_enddate	datetime	)";
		if (!Files.exists(Paths.get(db))) {
			SqlOperator sql = new SqlOperator(type, db, true);
			sql.executeUpdate(createtable);
			sql.disConnect();
		}
	}

	private Long getNewID() throws ClassNotFoundException, SQLException {
		SqlOperator sql = new SqlOperator(dbtype.sqlite, sqlitedb, true);
		String sqlstr = "select max(xf_id) from xf_todolist";
		Long newID = null;
		ResultSet rs = sql.executeQuery(sqlstr);
		if (rs.next()) {
			newID = rs.getLong(1) + 1;
		} else {
			newID = (long) 1;
		}
		sql.disConnect();
		return newID;
	}

	private Long getNewOrder() throws ClassNotFoundException, SQLException {
		SqlOperator sql = new SqlOperator(dbtype.sqlite, sqlitedb, true);
		String sqlstr = "select max(xf_order) from xf_todolist";
		Long newOrder = null;
		ResultSet rs = sql.executeQuery(sqlstr);
		if (rs.next()) {
			newOrder = rs.getLong(1) + 10;
		} else {
			newOrder = (long) 10;
		}
		sql.disConnect();
		return newOrder;
	}

	private void switchCell(ToDoListCell cell1, ToDoListCell cell2, Boolean reorder)
			throws ClassNotFoundException, SQLException {
		Point Point1 = cell1.getLocation();
		Integer Order1 = cell1.getMyOrder();
		Point Point2 = cell2.getLocation();
		Integer Order2 = cell2.getMyOrder();
		if (reorder) {
			cell1.setMyOrder(Order2);
			cell2.setMyOrder(Order1);
		}
		cell1.setLocation(Point2);
		cell2.setLocation(Point1);
	}

	public ToDoListContainer(String sqlitedb) throws ClassNotFoundException, SQLException {
		this.sqlitedb = sqlitedb;
		setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));
		myCellList = new ArrayList<ToDoListCell>();
	}

	public void addNewCell() throws ClassNotFoundException, SQLException {
		int id = getNewID().intValue();
		int order = getNewOrder().intValue();
		addCell(new ToDoListCell(this, id, order));
	}

	public void addWaitingCell() throws ClassNotFoundException, SQLException {
		addDbCell(0);
	}

	public void addDoneCell() throws ClassNotFoundException, SQLException {
		addDbCell(1);
	}

	public void removeDoneCell() {
		ArrayList<ToDoListCell> delTemp = (ArrayList<ToDoListCell>) myCellList.stream()
				.filter(new Predicate<ToDoListCell>() {
					@Override
					public boolean test(ToDoListCell t) {
						// TODO Auto-generated method stub
						return t == null ? false : t.getMyDone() == 1;
					}
				}).collect(Collectors.toList());
		myCellList.removeAll(delTemp);
		delTemp.forEach(new Consumer<ToDoListCell>() {
			@Override
			public void accept(ToDoListCell t) {
				// TODO Auto-generated method stub
				removeCell(t);
			}
		});
	}

	public Integer getCellCount() {
		return myCellList.size();
	}

	public void updateCell(String sqlstr) throws ClassNotFoundException, SQLException {
		SqlOperator sql = new SqlOperator(dbtype.sqlite, sqlitedb, true);
		sql.executeUpdate(sqlstr);
		sql.disConnect();
	}

	public void cellUp(ToDoListCell lowerCell) throws ClassNotFoundException, SQLException {
		Point lowerPoint = new Point(lowerCell.getLocation());
		lowerPoint.translate(0, -1 * lowerCell.getHeight() / 2);
		Object upper = findComponentAt(lowerPoint);
		ToDoListCell upperCell = null;
		if (upper instanceof ToDoListCell) {
			upperCell = (ToDoListCell) upper;
			switchCell(lowerCell, upperCell, true);
		}
	}

	public void cellDown(ToDoListCell upperCell) throws ClassNotFoundException, SQLException {
		Point upperPoint = new Point(upperCell.getLocation());
		upperPoint.translate(0, upperCell.getHeight() * 3 / 2);
		Object lower = findComponentAt(upperPoint);
		ToDoListCell lowerCell = null;
		if (lower instanceof ToDoListCell) {
			lowerCell = (ToDoListCell) lower;
			switchCell(upperCell, lowerCell, true);
		}
	}

	public void bfClose() {
		myCellList.forEach(new Consumer<ToDoListCell>() {

			@Override
			public void accept(ToDoListCell t) {
				// TODO Auto-generated method stub
				try {
					t.updateBfClose(true);
				} catch (Exception e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
		});
	}
}
