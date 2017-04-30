package io.github.gusg21.texteditor;

import java.awt.BorderLayout;
import java.awt.EventQueue;
import java.awt.Font;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;
import java.awt.event.InputEvent;
import java.awt.event.KeyEvent;
import java.awt.image.BufferedImage;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.PrintWriter;

import javax.imageio.ImageIO;
import javax.swing.DefaultComboBoxModel;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JComboBox;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JSlider;
import javax.swing.JTabbedPane;
import javax.swing.JTextField;
import javax.swing.KeyStroke;
import javax.swing.SwingConstants;
import javax.swing.UIManager;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

import org.fife.ui.rsyntaxtextarea.RSyntaxTextArea;
import org.fife.ui.rsyntaxtextarea.SyntaxConstants;
import org.fife.ui.rsyntaxtextarea.Theme;
import org.fife.ui.rtextarea.RTextScrollPane;

public class TeriorTextEditor
{

	private JFrame frame;

	/**
	 * Launch the application.
	 */
	public static void main(String[] args)
	{
		try
		{
			UIManager
					.setLookAndFeel("com.sun.java.swing.plaf.windows.WindowsLookAndFeel");
		} catch (Throwable e)
		{
			e.printStackTrace();
		}
		EventQueue.invokeLater(new Runnable()
		{
			public void run()
			{
				try
				{
					TeriorTextEditor window = new TeriorTextEditor();
					window.frame.setVisible(true);
				} catch (Exception e)
				{
					e.printStackTrace();
				}
			}
		});
	}

	/**
	 * Create the application.
	 */
	public TeriorTextEditor()
	{
		initialize();
	}

	/**
	 * Initialize the contents of the frame.
	 */
	private void initialize()
	{
		frame = new JFrame();
		frame.setResizable(false);
		frame.setBounds(0, 0, 800, 644);
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		frame.setLocationRelativeTo(null);
		frame.setTitle(Config.TITLE);

		String imagePath = "assets/icon.png";
		InputStream imgStream = TeriorTextEditor.class
				.getResourceAsStream(imagePath);
		BufferedImage icon = null;
		try
		{
			icon = ImageIO.read(imgStream);
		} catch (IOException e2)
		{
			e2.printStackTrace();
		}

		frame.setIconImage(icon);

		final JFileChooser fc = new JFileChooser();
		frame.getContentPane().setLayout(new BorderLayout(0, 0));

		JTabbedPane tabs = new JTabbedPane(JTabbedPane.TOP);
		frame.getContentPane().add(tabs);

		JPanel editorPanel = new JPanel();
		tabs.addTab("Editor", null, editorPanel, null);

		JPanel settingsPanel = new JPanel();
		tabs.addTab("Settings", null, settingsPanel, null);
		settingsPanel.setLayout(null);

		Theme theme = null;
		try
		{
			theme = Theme.load(getClass().getResourceAsStream(
					"/org/fife/ui/rsyntaxtextarea/themes/dark.xml"));
		} catch (IOException e2)
		{
			e2.printStackTrace();
		}

		RSyntaxTextArea textArea = new RSyntaxTextArea(21, 59);

		textArea.setSyntaxEditingStyle(SyntaxConstants.SYNTAX_STYLE_PYTHON);
		textArea.setCodeFoldingEnabled(true);
		theme.apply(textArea);

		textArea.setFont(new Font("Ubuntu Mono", Font.PLAIN, 18));
		editorPanel.setLayout(new BorderLayout(0, 0));

		JLabel lblFile = new JLabel("File: New");
		lblFile.setFont(new Font("Tahoma", Font.BOLD, 11));
		lblFile.setHorizontalAlignment(SwingConstants.CENTER);
		editorPanel.add(lblFile, BorderLayout.NORTH);

		JMenuBar menuBar = new JMenuBar();
		frame.getContentPane().add(menuBar, BorderLayout.NORTH);

		JMenu mnFile = new JMenu("File");
		menuBar.add(mnFile);

		JMenuItem mntmNew = new JMenuItem("New");
		mntmNew.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_N,
				InputEvent.CTRL_MASK));
		mntmNew.addActionListener(new ActionListener()
		{
			public void actionPerformed(ActionEvent e)
			{
				textArea.setText("");
				lblFile.setText("File: New");
			}
		});
		mnFile.add(mntmNew);

		JMenuItem mntmOpen = new JMenuItem("Open");
		mntmOpen.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_O,
				InputEvent.CTRL_MASK));
		mntmOpen.addActionListener(new ActionListener()
		{
			public void actionPerformed(ActionEvent e)
			{
				int returnVal = fc.showOpenDialog(frame);

				if (returnVal == JFileChooser.APPROVE_OPTION)
				{
					File file = fc.getSelectedFile();

					InputStream stream = null;
					try
					{
						stream = new FileInputStream(file.getAbsolutePath());
					} catch (FileNotFoundException e1)
					{
						e1.printStackTrace();
					}

					BufferedReader reader = new BufferedReader(
							new InputStreamReader(stream));

					String text = "";
					String line = "";
					try
					{
						while ((line = reader.readLine()) != null)
						{
							text += line + "\n";
						}
					} catch (IOException e1)
					{
						e1.printStackTrace();
					}

					textArea.setText(text);
					lblFile.setText("File: " + file.getAbsolutePath());
				}
			}
		});
		mnFile.add(mntmOpen);

		JMenuItem mntmSave = new JMenuItem("Save");
		mntmSave.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_S,
				InputEvent.CTRL_MASK));
		mntmSave.addActionListener(new ActionListener()
		{
			public void actionPerformed(ActionEvent e)
			{
				if (lblFile.getText() == "File: New")
				{
					int returnVal = fc.showOpenDialog(frame);

					if (returnVal == JFileChooser.APPROVE_OPTION)
					{
						try
						{
							PrintWriter writer = new PrintWriter(fc
									.getSelectedFile().getAbsolutePath(),
									"UTF-8");
							for (String line : textArea.getText().split(
									"\\r?\\n"))
							{
								writer.println(line);
							}
							writer.close();
						} catch (IOException e1)
						{
							e1.printStackTrace();
						}

						lblFile.setText("File: "
								+ fc.getSelectedFile().getAbsolutePath());
					}
				} else
				{
					try
					{
						PrintWriter writer = new PrintWriter(lblFile.getText()
								.substring(6), "UTF-8");
						for (String line : textArea.getText().split("\\r?\\n"))
						{
							writer.println(line);
						}
						writer.close();
					} catch (IOException e1)
					{
						e1.printStackTrace();
					}
				}
			}
		});
		mnFile.add(mntmSave);

		mnFile.addSeparator();

		JMenuItem mntmChangeLang = new JMenuItem("Change Language");
		mntmChangeLang.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_L,
				InputEvent.CTRL_MASK));
		mntmChangeLang.addActionListener(new ActionListener()
		{
			public void actionPerformed(ActionEvent e)
			{
				String langName = JOptionPane.showInputDialog(frame,
						"Enter language name:", null);
				if (langName != null)
				{
					switch (langName.toLowerCase().replace(" ", ""))
					{
					case "python":
						textArea.setSyntaxEditingStyle(SyntaxConstants.SYNTAX_STYLE_PYTHON);
						break;
					case "java":
						textArea.setSyntaxEditingStyle(SyntaxConstants.SYNTAX_STYLE_JAVA);
						break;
					case "html":
						textArea.setSyntaxEditingStyle(SyntaxConstants.SYNTAX_STYLE_HTML);
						break;
					case "css":
						textArea.setSyntaxEditingStyle(SyntaxConstants.SYNTAX_STYLE_CSS);
						break;
					case "c++":
					case "cplusplus":
						textArea.setSyntaxEditingStyle(SyntaxConstants.SYNTAX_STYLE_CPLUSPLUS);
						break;
					case "c#":
					case "csharp":
						textArea.setSyntaxEditingStyle(SyntaxConstants.SYNTAX_STYLE_CSHARP);
						break;
					case "js":
					case "javascript":
						textArea.setSyntaxEditingStyle(SyntaxConstants.SYNTAX_STYLE_JAVASCRIPT);
						break;
					case "ruby":
						textArea.setSyntaxEditingStyle(SyntaxConstants.SYNTAX_STYLE_RUBY);
						break;
					case "json":
						textArea.setSyntaxEditingStyle(SyntaxConstants.SYNTAX_STYLE_JSON);
						break;
					case "actionscript":
						textArea.setSyntaxEditingStyle(SyntaxConstants.SYNTAX_STYLE_ACTIONSCRIPT);
						break;
					case "assembly":
						textArea.setSyntaxEditingStyle(SyntaxConstants.SYNTAX_STYLE_ASSEMBLER_X86);
						break;
					case "bbcode":
					case "bb":
						textArea.setSyntaxEditingStyle(SyntaxConstants.SYNTAX_STYLE_BBCODE);
						break;
					case "c":
						textArea.setSyntaxEditingStyle(SyntaxConstants.SYNTAX_STYLE_C);
						break;
					case "clojure":
						textArea.setSyntaxEditingStyle(SyntaxConstants.SYNTAX_STYLE_CLOJURE);
						break;
					case "d":
						textArea.setSyntaxEditingStyle(SyntaxConstants.SYNTAX_STYLE_D);
						break;
					case "dart":
						textArea.setSyntaxEditingStyle(SyntaxConstants.SYNTAX_STYLE_DART);
						break;
					case "delphi":
						textArea.setSyntaxEditingStyle(SyntaxConstants.SYNTAX_STYLE_DELPHI);
						break;
					case "docker":
					case "dockerfile":
						textArea.setSyntaxEditingStyle(SyntaxConstants.SYNTAX_STYLE_DOCKERFILE);
						break;
					case "dtd":
						textArea.setSyntaxEditingStyle(SyntaxConstants.SYNTAX_STYLE_DTD);
						break;
					case "fortran":
						textArea.setSyntaxEditingStyle(SyntaxConstants.SYNTAX_STYLE_FORTRAN);
						break;
					case "groovy":
						textArea.setSyntaxEditingStyle(SyntaxConstants.SYNTAX_STYLE_GROOVY);
						break;
					case "hosts":
						textArea.setSyntaxEditingStyle(SyntaxConstants.SYNTAX_STYLE_HOSTS);
						break;
					case "htaccess":
						textArea.setSyntaxEditingStyle(SyntaxConstants.SYNTAX_STYLE_HTACCESS);
						break;
					case "jsp":
						textArea.setSyntaxEditingStyle(SyntaxConstants.SYNTAX_STYLE_JSP);
						break;
					case "latex":
						textArea.setSyntaxEditingStyle(SyntaxConstants.SYNTAX_STYLE_LATEX);
						break;
					case "less":
						textArea.setSyntaxEditingStyle(SyntaxConstants.SYNTAX_STYLE_LESS);
						break;
					case "lisp":
						textArea.setSyntaxEditingStyle(SyntaxConstants.SYNTAX_STYLE_LISP);
						break;
					case "lua":
						textArea.setSyntaxEditingStyle(SyntaxConstants.SYNTAX_STYLE_LUA);
						break;
					case "make":
					case "makefile":
						textArea.setSyntaxEditingStyle(SyntaxConstants.SYNTAX_STYLE_MAKEFILE);
						break;
					case "mxml":
						textArea.setSyntaxEditingStyle(SyntaxConstants.SYNTAX_STYLE_MXML);
						break;
					case "plaintext":
					case "text":
					case "plain":
					case "none":
						textArea.setSyntaxEditingStyle(SyntaxConstants.SYNTAX_STYLE_NONE);
						break;
					case "nsis":
						textArea.setSyntaxEditingStyle(SyntaxConstants.SYNTAX_STYLE_NSIS);
						break;
					case "perl":
						textArea.setSyntaxEditingStyle(SyntaxConstants.SYNTAX_STYLE_PERL);
						break;
					case "php":
						textArea.setSyntaxEditingStyle(SyntaxConstants.SYNTAX_STYLE_PHP);
						break;
					case "properties":
					case "props":
					case "propertiesfile":
						textArea.setSyntaxEditingStyle(SyntaxConstants.SYNTAX_STYLE_PROPERTIES_FILE);
						break;
					case "scala":
						textArea.setSyntaxEditingStyle(SyntaxConstants.SYNTAX_STYLE_SCALA);
						break;
					case "sql":
						textArea.setSyntaxEditingStyle(SyntaxConstants.SYNTAX_STYLE_SQL);
						break;
					case "tcl":
						textArea.setSyntaxEditingStyle(SyntaxConstants.SYNTAX_STYLE_TCL);
						break;
					case "typescript":
						textArea.setSyntaxEditingStyle(SyntaxConstants.SYNTAX_STYLE_TYPESCRIPT);
						break;
					case "sh":
					case "unixshell":
					case "bash":
						textArea.setSyntaxEditingStyle(SyntaxConstants.SYNTAX_STYLE_UNIX_SHELL);
						break;
					case "vb":
					case "visualbasic":
						textArea.setSyntaxEditingStyle(SyntaxConstants.SYNTAX_STYLE_VISUAL_BASIC);
						break;
					case "batch":
					case "winbatch":
						textArea.setSyntaxEditingStyle(SyntaxConstants.SYNTAX_STYLE_WINDOWS_BATCH);
						break;
					case "xml":
						textArea.setSyntaxEditingStyle(SyntaxConstants.SYNTAX_STYLE_XML);
						break;
					case "chucktesta":
						infoBox("Nope, just Chuck Testa with another realistic animal recreation.");
						break;
					default:
						infoBox("Unknown Language Name: "
								+ langName.toLowerCase()
								+ ". Language not changed.");
					}
				}
			}
		});
		mnFile.add(mntmChangeLang);

		mnFile.addSeparator();

		JMenuItem mntmQuit = new JMenuItem("Quit");
		mntmQuit.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_Q,
				InputEvent.CTRL_MASK));
		mntmQuit.addActionListener(new ActionListener()
		{
			public void actionPerformed(ActionEvent e)
			{
				System.exit(0);
			}
		});

		JMenuItem mntmSpawnNewEditor = new JMenuItem("Spawn New Editor");
		mntmSpawnNewEditor.addActionListener(new ActionListener()
		{
			public void actionPerformed(ActionEvent e)
			{
				String[] args =
				{};
				TeriorTextEditor.main(args);
			}
		});
		mntmSpawnNewEditor.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_N,
				InputEvent.CTRL_MASK | InputEvent.SHIFT_MASK));
		mnFile.add(mntmSpawnNewEditor);
		mnFile.add(mntmQuit);

		JMenu mnEdit = new JMenu("Edit");
		menuBar.add(mnEdit);

		JMenuItem mntmFindReplace = new JMenuItem("Find and Replace");
		mntmFindReplace.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_H,
				InputEvent.CTRL_MASK));
		mntmFindReplace.addActionListener(new ActionListener()
		{
			public void actionPerformed(ActionEvent e)
			{
				String find = JOptionPane.showInputDialog(frame,
						"String to find:", null);
				String replace = JOptionPane.showInputDialog(frame,
						"Replace \"" + find + "\" with:", null);

				textArea.setText(textArea.getText().replace(find, replace));
			}
		});
		mnEdit.add(mntmFindReplace);

		RTextScrollPane scrollPane = new RTextScrollPane(textArea);
		frame.addComponentListener(new ComponentAdapter()
		{
			@Override
			public void componentResized(ComponentEvent e)
			{
				scrollPane.setBounds(scrollPane.getBounds().x,
						scrollPane.getBounds().y, frame.getWidth() - 10,
						frame.getHeight() - 10);
			}
		});
		editorPanel.add(scrollPane);

		JComboBox<Object> themeChooser = new JComboBox<Object>();
		themeChooser.addActionListener(new ActionListener()
		{
			public void actionPerformed(ActionEvent e)
			{
				Theme theme = null;
				try
				{
					theme = Theme.load(getClass().getResourceAsStream(
							"/org/fife/ui/rsyntaxtextarea/themes/"
									+ themeChooser.getSelectedItem().toString()
									+ ".xml"));

				} catch (IOException e2)
				{
					e2.printStackTrace();
				}

				theme.apply(textArea);

				textArea.setFont(new Font("Ubuntu Mono", Font.PLAIN, 18));
			}
		});
		themeChooser.setModel(new DefaultComboBoxModel<Object>(
				new String[]
				{ "dark", "default-alt", "default", "eclipse", "idea",
						"monokai", "vs" }));
		themeChooser.setBounds(10, 30, 200, 20);
		settingsPanel.add(themeChooser);

		JLabel lblTheme = new JLabel("Theme");
		lblTheme.setBounds(10, 11, 46, 14);
		settingsPanel.add(lblTheme);

		JCheckBox chckbxCodeFolding = new JCheckBox("Code Folding");
		chckbxCodeFolding.addActionListener(new ActionListener()
		{
			public void actionPerformed(ActionEvent e)
			{
				textArea.setCodeFoldingEnabled(chckbxCodeFolding.isSelected());
			}
		});
		chckbxCodeFolding.setSelected(true);
		chckbxCodeFolding.setBounds(10, 76, 87, 23);
		settingsPanel.add(chckbxCodeFolding);

		JTextField textFieldRows = new JTextField();
		textFieldRows.setText("21");
		textFieldRows.setBounds(10, 143, 86, 20);
		settingsPanel.add(textFieldRows);
		textFieldRows.setColumns(10);

		JLabel lblRowsBy = new JLabel("rows by");
		lblRowsBy.setBounds(106, 146, 46, 14);
		settingsPanel.add(lblRowsBy);

		JTextField textFieldColumns = new JTextField();
		textFieldColumns.setText("59");
		textFieldColumns.setBounds(153, 143, 86, 20);
		settingsPanel.add(textFieldColumns);
		textFieldColumns.setColumns(10);

		JLabel lblColumns = new JLabel("columns");
		lblColumns.setBounds(249, 146, 46, 14);
		settingsPanel.add(lblColumns);

		JLabel lblEditorSizing = new JLabel("Editor Sizing");
		lblEditorSizing.setBounds(10, 118, 97, 14);
		settingsPanel.add(lblEditorSizing);

		JLabel lblFontSize = new JLabel("Font Size");
		lblFontSize.setBounds(10, 174, 46, 14);
		settingsPanel.add(lblFontSize);

		JLabel lblFontSizeCurrent = new JLabel("Font Size: 18");
		lblFontSizeCurrent.setBounds(10, 219, 86, 14);
		settingsPanel.add(lblFontSizeCurrent);

		JSlider sliderFontSize = new JSlider();
		sliderFontSize.setValue(18);
		sliderFontSize.addChangeListener(new ChangeListener()
		{
			public void stateChanged(ChangeEvent e)
			{
				textArea.setFont(new Font("Ubuntu", Font.PLAIN, sliderFontSize
						.getValue()));
				lblFontSizeCurrent.setText("Font Size: "
						+ String.valueOf(sliderFontSize.getValue()));
			}
		});
		sliderFontSize.setBounds(10, 192, 200, 26);
		settingsPanel.add(sliderFontSize);

		JCheckBox chckbxResizableWindow = new JCheckBox("Resizable Window");
		chckbxResizableWindow.addActionListener(new ActionListener()
		{
			public void actionPerformed(ActionEvent e)
			{
				frame.setResizable(chckbxResizableWindow.isSelected());
			}
		});
		chckbxResizableWindow.setBounds(99, 76, 111, 23);
		settingsPanel.add(chckbxResizableWindow);

		JButton btnSubmitSizing = new JButton("Submit Sizing");
		btnSubmitSizing.addActionListener(new ActionListener()
		{
			public void actionPerformed(ActionEvent e)
			{
				textArea.setColumns(Integer.parseInt(textFieldColumns.getText()));
				textArea.setRows(Integer.parseInt(textFieldRows.getText()));
			}
		});
		btnSubmitSizing.setBounds(298, 142, 97, 23);
		settingsPanel.add(btnSubmitSizing);
	}

	public static void infoBox(String infoMessage)
	{
		JOptionPane.showMessageDialog(null, infoMessage, "Info",
				JOptionPane.INFORMATION_MESSAGE);
	}
}
