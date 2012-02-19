package vipprogramming.browser;

import javax.swing.JFrame;
import javax.swing.JMenuBar;
import javax.swing.JMenu;
import javax.swing.JMenuItem;
import java.awt.event.ActionListener;
import java.awt.event.ActionEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;

import javax.swing.JScrollPane;
import java.awt.BorderLayout;
import java.awt.EventQueue;
import java.net.MalformedURLException;
import java.net.URL;
import java.text.NumberFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Map;
import java.util.Map.Entry;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.swing.JSplitPane;
import javax.swing.border.EmptyBorder;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import javax.swing.table.DefaultTableModel;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.DefaultTreeModel;
import javax.swing.tree.TreePath;
import javax.swing.tree.TreeSelectionModel;

import org.apache.commons.lang3.SystemUtils;

import javax.swing.JEditorPane;
import javax.swing.JTree;
import javax.swing.JTextArea;
import javax.swing.JTextPane;
import javax.swing.JButton;
import javax.swing.JPanel;

import java.awt.GridLayout;

/* 土台。
 * メニューバーとパネルを載せてある。
 * ツリーに板一覧を2ch.netから登録するアプリ。
 */

@SuppressWarnings("serial")
public class MainFrame extends JFrame {
	// munuBar
	private JMenuBar menuBar = new JMenuBar();
	private JMenu menu = new JMenu("板一覧");
	private JMenuItem menu_item_get_board_list = new JMenuItem("板一覧の取得");

	// MainFrameにのせるペイン
	private JScrollPane scrollPane_frame = new JScrollPane();
	private JSplitPane splitPane_frame = new JSplitPane();

	// MainFrameはメニューバーと左右のペインの３つ
	// 親御さんペイン。左ペイン
	private JScrollPane scrollPane_left = new JScrollPane();

	// 親御さんペイン。右ペイン
	private JScrollPane scrollPane_right = new JScrollPane();

	// 左ペインを上下に分割。
	private JSplitPane splitPane_left = new JSplitPane();
	private JScrollPane scrollPane = new JScrollPane();
	// 左上ペインに板一覧取得ボタンと板一覧ツリーをのせる
	private final JButton btnBoardListRefresh = new JButton("板一覧取得");
	private final JTree tree = new JTree();

	// 左下ペインはコンソール
	private JScrollPane scrollPane_1 = new JScrollPane();
	private final JTextArea txtrConsole = new JTextArea();

	// 右ペインも上下に分割
	private JSplitPane splitPane_right = new JSplitPane();
	private JScrollPane scrollPane_2 = new JScrollPane();
	// 右上ペインにスレ一覧テーブルをのせる
	private ThreadTable thread_table = new ThreadTable();

	// 右下ペインはスレ表示
	private JScrollPane scrollPane_3 = new JScrollPane();
	private final JTextPane textPane = new JTextPane();
	private final JEditorPane htmlPane = new JEditorPane();
	private final JPanel panel = new JPanel();

	// ローカルにある板一覧データを保持するマップ
	private Map<String, String> readbl;
	// 板一覧、スレ一覧を取ってきては保持するクラス
	private Get2chData g2d = new Get2chData();

	//
	private String thread_url = null;

	public MainFrame() {
		// 閉じるで終了
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		// 四隅の位置を指定
		setBounds(100, 100, 900, 600);

		setJMenuBar(menuBar);
		menuBar.add(menu);
		menu.add(menu_item_get_board_list);

		// ペインをセット

		getContentPane().add(scrollPane_frame, BorderLayout.CENTER);

		scrollPane_frame.setViewportView(splitPane_frame);
		scrollPane_frame.setBorder(new EmptyBorder(0, 0, 0, 0));
		splitPane_frame.setOneTouchExpandable(true);
		splitPane_frame.setDividerLocation(180);
		splitPane_frame.setDividerSize(2);
		splitPane_frame.setBorder(new EmptyBorder(0, 0, 0, 0));
		splitPane_frame.setLeftComponent(scrollPane_left);
		splitPane_frame.setRightComponent(scrollPane_right);

		scrollPane_left.setViewportView(splitPane_left);
		scrollPane_left.setBorder(new EmptyBorder(0, 0, 0, 0));

		splitPane_left.setOrientation(JSplitPane.VERTICAL_SPLIT);
		splitPane_left.setLeftComponent(scrollPane);
		splitPane_left.setRightComponent(scrollPane_1);
		splitPane_left.setOneTouchExpandable(true);
		splitPane_left.setDividerLocation(450);
		splitPane_left.setDividerSize(2);
		splitPane_left.setBorder(new EmptyBorder(0, 0, 0, 0));

		scrollPane_right.setViewportView(splitPane_right);
		scrollPane_right.setBorder(new EmptyBorder(0, 0, 0, 0));

		splitPane_right.setOrientation(JSplitPane.VERTICAL_SPLIT);
		splitPane_right.setLeftComponent(scrollPane_2);
		splitPane_right.setRightComponent(scrollPane_3);
		splitPane_right.setOneTouchExpandable(true);
		splitPane_right.setDividerLocation(180);
		splitPane_right.setDividerSize(2);
		splitPane_right.setBorder(new EmptyBorder(0, 0, 0, 0));

		scrollPane.setColumnHeaderView(panel);
		scrollPane.setViewportView(tree);
		scrollPane_1.setViewportView(txtrConsole);
		scrollPane_2.setViewportView(thread_table);
		scrollPane_3.setViewportView(htmlPane);

		panel.add(btnBoardListRefresh);
		panel.setLayout(new GridLayout(0, 1, 0, 0));

		// ボタンにイベント追加
		btnBoardListRefresh.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				addBoardList();
			}
		});

		// 板一覧の複数同時選択を不可能にする
		tree.getSelectionModel().setSelectionMode(TreeSelectionModel.SINGLE_TREE_SELECTION);
		// 板一覧データがローカルにあるかチェック
		readbl = PrefFileIO.ReadBoardList();
		if (readbl.size() > 0) {
			addBoardListToTree(tree, readbl);
			tree.expandRow(0);
		} else {
			tree.setModel(new DefaultTreeModel(new DefaultMutableTreeNode(new BoardEntry("板一覧", ""))));
		}

		txtrConsole.setText("console");
		txtrConsole.setRows(5);

		thread_table.getSelectionModel().addListSelectionListener(new ListSelectionListener() {
			@Override
			public void valueChanged(ListSelectionEvent e) {
				if (!e.getValueIsAdjusting()) {
					txtrConsole.setText("スレッド取得中");
					int index = thread_table.convertRowIndexToModel(thread_table.getSelectedRow());
					String dat_file_name = thread_url + "/dat/"
							+ (String) ((DefaultTableModel) thread_table.getModel()).getValueAt(index, 5) + ".dat";
					// textPane.setText(dat_file_name);
					htmlPane.setText(dat_file_name);
					try {
						getThread(new URL(dat_file_name));
					} catch (MalformedURLException e1) {
						e1.printStackTrace();
					}
				}
			}
		});

		htmlPane.setEditable(false);
		htmlPane.setContentType("text/html");
		htmlPane.setText("<b>thread view</b>");

		// scrollPane_3.setViewportView(textPane);

		menu_item_get_board_list.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				addBoardList();
			}
		});
		MouseListener ml = new MouseAdapter() {
			public void mousePressed(MouseEvent e) {
				int selRow = tree.getRowForLocation(e.getX(), e.getY());
				TreePath selPath = tree.getPathForLocation(e.getX(), e.getY());
				if (selRow != -1) {
					if (e.getClickCount() == 1) {
						mySingleClick(selRow, selPath);
					} else if (e.getClickCount() == 2) {
						// myDoubleClick(selRow, selPath);
					}
				}
			}

			private void mySingleClick(int selRow, TreePath selPath) {
				DefaultMutableTreeNode be = (DefaultMutableTreeNode) selPath.getLastPathComponent();
				BoardEntry selected_board_entry = (BoardEntry) be.getUserObject();
				if (!selected_board_entry.getBoard_url().equals("")) {
					thread_url = selected_board_entry.getBoard_url();
					String thread_list_url = thread_url + "subject.txt";
					txtrConsole.setText(thread_list_url);
					try {
						getThreadList(new URL(thread_list_url));
					} catch (MalformedURLException e) {
						e.printStackTrace();
					}
				}
			}

		};
		tree.addMouseListener(ml);
	}

	private void addBoardList() {
		// BoardListコンストラクタで2chから板一覧データを取得。
		txtrConsole.setText("板一覧取得中");

		g2d.getMenu();
		// blに板一覧データを代入
		Map<String, String> bl = g2d.getBoardMap();
		// ツリーに追加
		addBoardListToTree(tree, bl);
		// 板一覧データをファイルに書き込んでおく
		PrefFileIO.writeBoardList(bl);
		txtrConsole.setText("板一覧取得済み");
	}

	private void getThreadList(URL thread_list_url) {
		txtrConsole.setText("スレッド一覧取得中");
		g2d.getThreadList(thread_list_url);
		// blに板一覧データを代入
		ArrayList<String> tl = g2d.getThreadArray();
		// テーブルに追加
		addThreadListArrayToTable(tl);
		// 板一覧データをファイルに書き込んでおく
		PrefFileIO.writeThreadList(tl);
		txtrConsole.setText("スレッド一覧取得済み");
	}

	private void getThread(URL thread_url) {
		txtrConsole.setText("スレッド取得中");
		g2d.getThread(thread_url);
		ArrayList<String> r_al = g2d.getResArray();
		// テーブルに追加
		addThread(r_al);
		txtrConsole.setText("スレッド一覧取得済み");
	}

	private void addThread(ArrayList<String> r_al) {
		int thread_cnt = 1;
		String name;
		String mail;
		String date;
		String content;
		String title;
		StringBuffer sBuffer = new StringBuffer();
		for (String r : r_al) {
			Pattern thread_pattern = Pattern.compile("^(.*)<>(.*)<>(.*)<>(.*)<>(.*)$");
			Matcher thread_matcher = thread_pattern.matcher(r);
			if (thread_matcher.find()) {
				name = thread_matcher.group(1);
				mail = thread_matcher.group(2);
				date = thread_matcher.group(3);
				content = thread_matcher.group(4);
				title = thread_matcher.group(5);
				sBuffer.append(title);
				sBuffer.append("<br>");
				sBuffer.append("<font color=blue>");
				sBuffer.append(thread_cnt);
				sBuffer.append("</font>");
				sBuffer.append("<font color=green>");
				sBuffer.append(name);
				sBuffer.append("</font>");
				sBuffer.append("[");
				sBuffer.append(mail);
				sBuffer.append("]");
				sBuffer.append(date);
				sBuffer.append("<br>");
				sBuffer.append(content);
				sBuffer.append("<br><br>");
			}
			thread_cnt++;
		}
		htmlPane.setText(sBuffer.toString());
	}

	private void addThreadListArrayToTable(ArrayList<String> tl) {
		DefaultTableModel model = ((DefaultTableModel) thread_table.getModel());
		model.setRowCount(0);
		//
		int i_cnt = 1;
		for (String str : tl) {
			Pattern thread_list_pattern = Pattern.compile("(\\d+)\\.dat<>(.*)\\s\\((\\d+)\\)$");
			Matcher thread_list_matcher = thread_list_pattern.matcher(str);
			if (thread_list_matcher.find()) {

				long epoc_ms = Long.parseLong(thread_list_matcher.group(1)) * 1000 + 9 * 60 * 60;
				long now_ms = System.currentTimeMillis();
				Date date = new Date(epoc_ms);
				SimpleDateFormat sdf = new SimpleDateFormat("yyyy/MM/dd/ HH:mm");
				String order = String.valueOf(i_cnt);
				String title = thread_list_matcher.group(2);
				String response_count = thread_list_matcher.group(3);
				String formated_date = sdf.format(date);
				double speed = Double.parseDouble(response_count) * 1000.0 * 24.0 * 3600.0
						/ (double) ((double) (now_ms - epoc_ms));
				NumberFormat speed_f = NumberFormat.getInstance();
				speed_f.setMaximumFractionDigits(2);
				String speed_str = speed_f.format(speed);
				String[] strAry = { order, title, response_count, formated_date, speed_str,
						thread_list_matcher.group(1) };
				((DefaultTableModel) thread_table.getModel()).addRow(strAry);
			}
			i_cnt++;
		}

	}

	// カテゴリは(key="カテゴリ名",value="c")、板は(key="板名",value="http://~")
	// としてMAPからツリーを作る。
	private void addBoardListToTree(JTree t, Map<String, String> bl) {
		DefaultMutableTreeNode root = new DefaultMutableTreeNode(new BoardEntry("板一覧", ""));
		t.setModel(new DefaultTreeModel(root));
		for (Entry<String, String> entry : bl.entrySet()) {
			if (entry.getValue().equals("c")) {
				DefaultMutableTreeNode dmn = new DefaultMutableTreeNode(new BoardEntry(entry.getKey(), ""));
				int c_cnt = root.getChildCount();
				if (c_cnt < 0)
					c_cnt = 0;
				((DefaultTreeModel) t.getModel()).insertNodeInto(dmn, root, c_cnt);
			} else {
				DefaultMutableTreeNode dmn = new DefaultMutableTreeNode(
						new BoardEntry(entry.getKey(), entry.getValue()));
				DefaultMutableTreeNode chi;
				chi = (DefaultMutableTreeNode) ((DefaultTreeModel) t.getModel()).getChild(root,
						root.getChildCount() - 1);
				int c_cnt = chi.getChildCount();
				if (c_cnt < 0)
					c_cnt = 0;
				((DefaultTreeModel) t.getModel()).insertNodeInto(dmn, chi, c_cnt);
			}
		}

	}

	public static void main(String[] args) {

		EventQueue.invokeLater(new Runnable() {
			public void run() {
				try {
					if (SystemUtils.IS_OS_MAC) {
						System.setProperty("apple.laf.useScreenMenuBar", "true");
						System.setProperty("com.apple.mrj.application.apple.menu.about.name", "アプリケーション名");
					}
					MainFrame frame = new MainFrame();
					frame.setVisible(true);
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		});
	}
}
