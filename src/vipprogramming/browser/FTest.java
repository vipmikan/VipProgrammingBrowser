package vipprogramming.browser;

import java.awt.*;
import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.Map;
import java.util.Map.Entry;

import javax.swing.tree.DefaultTreeModel;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.TreeSelectionModel;


/* 土台。
 * メニューバーとパネルを載せてある。
 * ツリーに板一覧を2ch.netから登録するアプリ。
 */

public class FTest extends JFrame {
	private static final long serialVersionUID = 1L;
	private JPanel contentPane;
	private JScrollPane scrollPane;
	private JMenuBar menuBar = new JMenuBar();
	private JMenu menu_board_list = new JMenu("板一覧");

	//板一覧を表示するツリー
	private JTree tree = new JTree();

	public static void main(String[] args) {
		EventQueue.invokeLater(new Runnable() {
			public void run() {
				try {
					FTest frame = new FTest();
					frame.setVisible(true);
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		});
	}

	public FTest() {
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		setBounds(100, 100, 900, 600);

		//メニューの項目をセット
		initializeMenu();

		//Frameの上にとりあえずのせるパネル
		contentPane = new JPanel();

		contentPane.setBorder(new EmptyBorder(5, 5, 5, 5));
		contentPane.setLayout(new BorderLayout(0, 0));
		setContentPane(contentPane);

		//とりあえずパネルにスクロールパネルをのせる
		scrollPane = new JScrollPane();
		contentPane.add(scrollPane, BorderLayout.CENTER);

		//スクロールパネルに左右分割するパネルをのせる
		JSplitPane splitPane = new JSplitPane();
		scrollPane.setViewportView(splitPane);

		//左側にスクロールパネルのせる。＆推奨サイズ(幅)を指定
		JScrollPane scrollPane_left = new JScrollPane();
		scrollPane_left.setPreferredSize(new Dimension(180,120));//120(高さ)は意味がない？
		splitPane.setLeftComponent(scrollPane_left);

		//右側にもスクロールパネルのせる。ここにスレ一覧を表示させたい。
		JScrollPane scrollPane_right = new JScrollPane();
		splitPane.setRightComponent(scrollPane_right);

		//板一覧の複数同時選択を不可能にする
		tree.getSelectionModel().setSelectionMode(TreeSelectionModel.SINGLE_TREE_SELECTION);

		//板一覧ファイルがあれば読み込む。なければルート(板一覧)だけ追加。
		Map<String, String> readbl = PrefFileIO.ReadBoardList();
		if(readbl.size()>0){
			addBoardListToTree(tree,readbl);
		}else{
			tree.setModel(new DefaultTreeModel(new DefaultMutableTreeNode("板一覧")));
		}
		scrollPane_left.setViewportView(tree);

		JList list = new JList();
		scrollPane_right.setViewportView(list);
	}

	//メニューバーの項目設定
	private void initializeMenu() {
		setJMenuBar(menuBar);
		menuBar.add(menu_board_list);

		//「板一覧の取得」クリックで板一覧取得のメソッドを呼び出す
		JMenuItem mntmNewMenuItem_1 = new JMenuItem("板一覧の取得");
		mntmNewMenuItem_1.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				addBoardList();
			}

		});
		//メニューバーにアイテムを追加
		menu_board_list.add(mntmNewMenuItem_1);

	}

	private void addBoardList(){
		//BoardListコンストラクタで2chから板一覧データを取得。
		BoardList blc = new BoardList();
		//blに板一覧データを代入
		Map<String, String> bl = blc.getBoard_list();
		//ツリーに追加
		addBoardListToTree(tree,bl);
		//板一覧データをファイルに書き込んでおく
		PrefFileIO.writeBoardList(bl);
	}

	//カテゴリは(key="カテゴリ名",value="c")、板は(key="板名",value="http://~")
	//となったMAPからツリーを作る。
	private void addBoardListToTree(JTree t,Map<String, String> bl) {
		DefaultMutableTreeNode root = new DefaultMutableTreeNode("板一覧");
		t.setModel(new DefaultTreeModel(root));
		for(Entry<String, String> entry : bl.entrySet()){
			if(entry.getValue().equals("c")){
				DefaultMutableTreeNode dmn = new DefaultMutableTreeNode(entry.getKey());
				int c = root.getChildCount();
				if(c<0)c=0;
				((DefaultTreeModel)t.getModel()).insertNodeInto(dmn, root, c);
			}else{
				DefaultMutableTreeNode dmn = new DefaultMutableTreeNode(entry.getKey());
				DefaultMutableTreeNode chi;
				chi = (DefaultMutableTreeNode)((DefaultTreeModel)t.getModel()).getChild(root, root.getChildCount()-1);
				int c = chi.getChildCount();
				if(c<0)c=0;
				((DefaultTreeModel)t.getModel()).insertNodeInto(dmn, chi, c);
			}
		}

	}

}
