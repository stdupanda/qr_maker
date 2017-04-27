package cn.xz.qrmaker.view;

import java.awt.Color;
import java.awt.Container;
import java.awt.Cursor;
import java.awt.EventQueue;
import java.awt.Font;
import java.awt.Image;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.sql.SQLException;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.UIManager;
import javax.swing.border.EmptyBorder;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.zxing.WriterException;

import cn.xz.qrmaker.config.Resources;
import cn.xz.qrmaker.dao.SQLIteUtil;
import cn.xz.qrmaker.entity.UrlLog;
import cn.xz.qrmaker.util.MyZXingUtil;

/**
 * 主窗口，swing 入口
 * 
 * @author gsx
 *
 */
public class MainWindow extends JFrame {

	private static final long serialVersionUID = -5070447543337995239L;

	private static Logger logger = LoggerFactory.getLogger(MainWindow.class);

	private JPanel contentPane;
	private MyImagePanel imagePanel;
	private JTextArea textArea;
	private JLabel label_url;

	/**
	 * 二维码宽度 {@value}
	 */
	private static final int qrWidth = 240;
	/**
	 * 二维码高度 {@value}
	 */
	private static final int qrheight = 240;

	private static String dbName = Resources.DB_NAME;

	JComboBox<UrlLog> comboBox;

	String oldUrl = "";
	/**
	 * 标记是否已完成界面初始化
	 */
	boolean ifInit = false;
	/**
	 * 标记是否刚从下拉框中选择了一个
	 */
	boolean justSelected = false;

	/**
	 * Launch the application.
	 */
	public static void main(String[] args) {
		EventQueue.invokeLater(new Runnable() {
			public void run() {
				try {
					MainWindow frame = new MainWindow();
					// UIManager.setLookAndFeel(UIManager.getCrossPlatformLookAndFeelClassName());
					UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
					frame.setVisible(true);
					frame.setLocationRelativeTo(null);// make center of screen

					SQLIteUtil.initDB(dbName);
					// System.out.println(SQLIteUtil.getCount());
					frame.resetCombo();
					frame.firstInitImagePanel();

					frame.ifInit = true;
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		});
	}

	/**
	 * Create the frame.
	 * 
	 * @throws WriterException
	 */
	public MainWindow() throws WriterException {
		setIconImage(new ImageIcon(getClass().getResource("/icon_red.png")).getImage());
		setTitle(Resources.FRM_TITLE);
		setResizable(false);
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		setBounds(100, 100, 581, 436);
		contentPane = new JPanel();
		contentPane.setBorder(new EmptyBorder(5, 5, 5, 5));
		setContentPane(contentPane);
		contentPane.setLayout(null);

		JLabel lblurl = new JLabel("请输入URL：");
		lblurl.setBounds(10, 14, 94, 15);
		contentPane.add(lblurl);

		JLabel label = new JLabel("选择生成记录：");
		label.setBounds(10, 90, 94, 15);
		contentPane.add(label);

		textArea = new JTextArea();
		textArea.setWrapStyleWord(true);// 换行不断字
		textArea.setLineWrap(true);// 换行
		// textArea.setBounds(136, 10, 349, 60);
		// contentPane.add(textArea);
		textArea.getDocument().addDocumentListener(new DocumentListener() {

			public void removeUpdate(DocumentEvent e) {
				justSelected = false;
			}

			public void insertUpdate(DocumentEvent e) {
				justSelected = false;
			}

			public void changedUpdate(DocumentEvent e) {
				justSelected = false;
			}
		});

		JScrollPane scroll = new JScrollPane(textArea);
		scroll.setBounds(114, 14, 349, 60);
		scroll.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
		scroll.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED);
		contentPane.add(scroll);

		JButton button = new JButton("生成");
		button.setFocusPainted(false);// do not show black line
		button.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseEntered(MouseEvent e) {
				setCursor(new Cursor(Cursor.HAND_CURSOR));
			}

			@Override
			public void mouseExited(MouseEvent e) {
				setCursor(new Cursor(Cursor.DEFAULT_CURSOR));
			}

			@Override
			public void mouseClicked(MouseEvent e) {

				String url = textArea.getText();
				if (null == url || "".equals(url.trim())) {
					logger.debug("内容为空");
					return;
				}
				if (!url.startsWith("http")) {// 不是以 http 开头的网址格式
					url = "http://" + url;
					textArea.setText(url);
				}
				try {
					if (justSelected) {// 刚从下拉框中选择了一个新的，需要重新生成二维码
						setImage(url);
						// resetCombo();
						justSelected = false;
						return;
					}
					if (oldUrl.equals(url)) {// url 没变
						logger.info("url没有变化，不写库");
						return;
					} else {
						if (!SQLIteUtil.check(url)) {// db 表中不存在
							SQLIteUtil.insertLog(url);
						} else {// db 表已存在
							logger.info("url 在 db 中已存在");
							return;
						}
					}
					setImage(url);
					resetCombo();
				} catch (SQLException e1) {
					JOptionPane.showMessageDialog(getParent(), "插入历史记录失败！" + e1.getMessage());
					e1.printStackTrace();
				}
			}
		});
		button.setFont(new Font("SimSun", Font.PLAIN, 16));
		button.setBounds(469, 10, 93, 64);
		contentPane.add(button);

		comboBox = new JComboBox<UrlLog>();
		comboBox.addItemListener(new ItemListener() {
			public void itemStateChanged(ItemEvent e) {
				logger.debug("{}", e.getStateChange());
				if (e.getStateChange() == ItemEvent.SELECTED) {
					if (ifInit) {
						String item = ((UrlLog) comboBox.getSelectedItem()).getUrl();
						logger.info("当前选择的是：{}", item);
						if ("暂无".equals(item)) {
							logger.info("不再设置输入框了");
							return;
						}
						textArea.setText(item);
						oldUrl = item;
						justSelected = true;
					}
				}
			}
		});
		comboBox.setBounds(114, 87, 349, 21);
		contentPane.add(comboBox);

		// Image image = ZXingUtil.getImage("http://www.baidu.com", qrWidth,
		// qrheight, 0);
		// imagePanel = new MyImagePanel(image);
		// imagePanel = new MyImagePanel(null);
		// imagePanel.setBounds(10, 118, qrWidth, qrheight);
		// contentPane.add(imagePanel);

		JLabel label_clear_log = new JLabel("<html><u>清除记录</u></html>");
		label_clear_log.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseEntered(MouseEvent e) {
				setCursor(new Cursor(Cursor.HAND_CURSOR));
			}

			@Override
			public void mouseExited(MouseEvent e) {
				setCursor(new Cursor(Cursor.DEFAULT_CURSOR));
			}

			@Override
			public void mouseClicked(MouseEvent e) {
				int ret = JOptionPane.showConfirmDialog(getParent(), "这将清空所有记录！确定继续吗？",
						"请确认", JOptionPane.OK_CANCEL_OPTION);
				if(ret != JOptionPane.OK_OPTION){
					return;
				}
				try {
					SQLIteUtil.clearDB();
					toast(getParent(), "清除成功！");
					resetCombo();
				} catch (SQLException e1) {
					JOptionPane.showMessageDialog(getParent(), "清除记录失败！" + e1.getMessage());
					logger.error(e1.getMessage());
					e1.printStackTrace();
				}
			}
		});
		label_clear_log.setForeground(Color.BLUE);
		label_clear_log.setToolTipText("点击清除记录");
		label_clear_log.setBounds(479, 90, 54, 15);
		contentPane.add(label_clear_log);

		label_url = new JLabel("");
		label_url.setToolTipText("此处显示网址");
		label_url.setBorder(new MyBorder(1, Color.BLUE));
		label_url.setBounds(10, 368, 552, 29);
		contentPane.add(label_url);
		
		JLabel label_about = new JLabel("关于");
		label_about.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseEntered(MouseEvent e) {
				setCursor(new Cursor(Cursor.HAND_CURSOR));
			}

			@Override
			public void mouseExited(MouseEvent e) {
				setCursor(new Cursor(Cursor.DEFAULT_CURSOR));
			}

			@Override
			public void mouseClicked(MouseEvent e) {
				toast(getParent(), "A simple swing tool for generating qrcode.");
			}
		});
		label_about.setForeground(Color.BLUE);
		label_about.setToolTipText("关于");
		label_about.setBounds(479, 115, 54, 15);
		contentPane.add(label_about);
	}

	/**
	 * 显示二维码
	 * 
	 * @param url
	 * @throws WriterException
	 */
	private void setImage(final String url) {// jdk1.8 下可以不显式声明final
		new Thread(new Runnable() {

			@Override
			public void run() {
				Image image = null;
				try {
					image = MyZXingUtil.getImage(url, qrWidth, qrheight, 0);
				} catch (WriterException e) {
					toast(getParent(), "生成二维码异常！" + e.getMessage());
					e.printStackTrace();
				}
				if (null != imagePanel) {
					contentPane.remove(imagePanel);
				}
				imagePanel = new MyImagePanel(image);
				imagePanel.setBounds(10, 118, qrWidth, qrheight);
				imagePanel.setToolTipText(url);
				contentPane.add(imagePanel);
				contentPane.repaint();
				label_url.setText("  " + url);
				label_url.setToolTipText(url);
				textArea.setText(url);
			}
		}).start();
	}

	private void resetCombo() throws SQLException {
		comboBox.removeAllItems();

		List<UrlLog> ret = SQLIteUtil.listAll();
		if (1 > ret.size()) {
			// comboBox.addItem(new UrlLog(-1, "暂无"));
			comboBox.setSelectedIndex(-1);
		} else {
			for (UrlLog urlLog : ret) {
				comboBox.addItem(urlLog);
			}
		}
	}

	/**
	 * 显示 toast 消息，1秒后自动消失
	 * 
	 * @param msg
	 */
	private void toast(Container container, String msg) {
		JOptionPane op = new JOptionPane(msg, JOptionPane.INFORMATION_MESSAGE);
		
		final JDialog dialog = op.createDialog(container, "提示");
		dialog.setDefaultCloseOperation(JDialog.DISPOSE_ON_CLOSE);
		dialog.setLocationRelativeTo(container);
		dialog.setAlwaysOnTop(true);
		dialog.setModal(false);
		dialog.setVisible(true);
		new Timer().schedule(new TimerTask() {

			@Override
			public void run() {
				dialog.setVisible(false);
				dialog.dispose();
			}
		}, 1000);
	}

	/**
	 * window 初始化后默认加载第一个下拉框内容
	 */
	private void firstInitImagePanel() {
		UrlLog item = comboBox.getItemAt(0);
		if (null != item && !"".equals(item.getUrl()) && !"暂无".equals(item.getUrl())) {
			setImage(item.getUrl());
		}
	}
}
