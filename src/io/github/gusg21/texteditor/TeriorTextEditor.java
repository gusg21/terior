package io.github.gusg21.texteditor;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Cursor;
import java.awt.EventQueue;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;
import java.awt.event.InputEvent;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.font.TextAttribute;
import java.awt.image.BufferedImage;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.Map;

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
			UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
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
		/*
		 * Setup the Application Frame.
		 */
		frame = new JFrame();
		frame.setResizable(false);
		frame.setBounds(0, 0, 800, 644);
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		frame.setLocationRelativeTo(null);
		frame.setTitle(Config.TITLE);

		/*
		 * Load and apply the icon. (assets/icon.png)
		 */
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

		/*
		 * Initialize a FileChooser for later use.
		 */
		final JFileChooser fc = new JFileChooser();
		frame.getContentPane().setLayout(new BorderLayout(0, 0));

		/*
		 * Create the tabs and parent panels
		 */
		JTabbedPane tabs = new JTabbedPane(JTabbedPane.TOP);
		frame.getContentPane().add(tabs);

		JPanel editorPanel = new JPanel();
		tabs.addTab("Editor", null, editorPanel, null);

		JPanel settingsPanel = new JPanel();
		tabs.addTab("Settings", null, settingsPanel, null);
		settingsPanel.setLayout(null);

		/****************
		 * EDITOR PANEL *
		 ****************/

		/*
		 * Create the Editor with a theme
		 */
		Theme theme = null;
		try
		{
			theme = Theme.load(getClass().getResourceAsStream(
					"/org/fife/ui/rsyntaxtextarea/themes/dark.xml"));
		} catch (IOException e2)
		{
			e2.printStackTrace();
		}
		editorPanel.setLayout(new BorderLayout(0, 0));

		/*
		 * Create the File Label (File: blah/blah/moo.txt)
		 */
		JLabel lblFile = new JLabel("File: New");
		lblFile.setFont(new Font("Tahoma", Font.BOLD, 11));
		lblFile.setHorizontalAlignment(SwingConstants.CENTER);
		editorPanel.add(lblFile, BorderLayout.NORTH);

		/*
		 * Create the footer with Pos and Language
		 */
		JPanel footerPanel = new JPanel();
		editorPanel.add(footerPanel, BorderLayout.SOUTH);
		footerPanel.setLayout(new FlowLayout(FlowLayout.CENTER, 5, 5));

		/*
		 * Pos Label
		 */
		JLabel lblPos = new JLabel("Pos: 0, 0");
		footerPanel.add(lblPos);

		RSyntaxTextArea textArea = new RSyntaxTextArea(21, 59);
		textArea.addMouseListener(new MouseAdapter()
		{
			@Override
			// Update Pos Label
			public void mouseClicked(MouseEvent e)
			{
				lblPos.setText("Pos: "
						+ String.valueOf(textArea.getCaretPosition() + 1)
						+ ", " + String.valueOf(textArea.getCaretLineNumber()));
			}
		});
		textArea.addKeyListener(new KeyAdapter()
		{
			@Override
			public void keyTyped(KeyEvent e)
			{
				lblPos.setText("Pos: "
						+ String.valueOf(textArea.getCaretPosition() + 1)
						+ ", " + String.valueOf(textArea.getCaretLineNumber()));
			}
		});

		textArea.setSyntaxEditingStyle(SyntaxConstants.SYNTAX_STYLE_PYTHON);
		textArea.setCodeFoldingEnabled(true);
		theme.apply(textArea);

		textArea.setFont(new Font(Config.FONT, Font.PLAIN, 18));

		/*
		 * Language Label
		 */
		JLabel lblLanguage = new JLabel("");
		lblLanguage.setText("Language: "
				+ textArea.getSyntaxEditingStyle().substring(5, 6)
						.toUpperCase()
				+ textArea.getSyntaxEditingStyle().substring(6));
		footerPanel.add(lblLanguage);

		/*
		 * Add the ScrollPane for the Editor
		 */
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
		scrollPane.getGutter().setLineNumberFont(
				new Font(Config.FONT, Font.PLAIN, 15));
		editorPanel.add(scrollPane, BorderLayout.CENTER);

		/*
		 * Create the menu bar
		 */
		JMenuBar menuBar = new JMenuBar();
		frame.getContentPane().add(menuBar, BorderLayout.NORTH);

		/*
		 * Add the file button
		 */
		JMenu mnFile = new JMenu("File");
		menuBar.add(mnFile);

		/*
		 * Add the "New" command
		 */
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

		/*
		 * Make the "Open" item
		 */
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

		/*
		 * Make the "Save" item
		 */
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

		/*
		 * Make the "Change Language" item
		 */
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

					lblLanguage.setText("Language: "
							+ textArea.getSyntaxEditingStyle().substring(5, 6)
									.toUpperCase()
							+ textArea.getSyntaxEditingStyle().substring(6));
				}
			}
		});
		mnFile.add(mntmChangeLang);

		mnFile.addSeparator();

		/*
		 * Make the "Spawn New Editor" item
		 */
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

		/*
		 * Make the "Quit" Item
		 */
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
		mnFile.add(mntmQuit);

		/*
		 * Make "Edit" menu
		 */
		JMenu mnEdit = new JMenu("Edit");
		menuBar.add(mnEdit);

		/*
		 * Make "Find and Replace" Item
		 */
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

		mnEdit.addSeparator();

		/*
		 * Add "Tabs to Spaces" item
		 */
		JMenuItem mntmTabsToSpaces = new JMenuItem("Tabs to Spaces");
		mntmTabsToSpaces.addActionListener(new ActionListener()
		{
			public void actionPerformed(ActionEvent e)
			{
				textArea.convertTabsToSpaces();
			}
		});
		mntmTabsToSpaces.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_T,
				InputEvent.CTRL_MASK | InputEvent.SHIFT_MASK));
		mnEdit.add(mntmTabsToSpaces);

		/*
		 * Add "Spaces to Tabs" item
		 */
		JMenuItem mntmSpacesToTabs = new JMenuItem("Spaces to Tabs");
		mntmSpacesToTabs.addActionListener(new ActionListener()
		{
			public void actionPerformed(ActionEvent e)
			{
				textArea.convertSpacesToTabs();
			}
		});
		mntmSpacesToTabs.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_S,
				InputEvent.CTRL_MASK | InputEvent.SHIFT_MASK));
		mnEdit.add(mntmSpacesToTabs);

		/******************
		 * SETTINGS PANEL *
		 ******************/

		/*
		 * Make the label to help the user with the dropdown
		 */
		JLabel lblTheme = new JLabel("Theme");
		lblTheme.setBounds(10, 11, 46, 14);
		settingsPanel.add(lblTheme);

		/*
		 * Make the theme dropdown
		 */
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

				textArea.setFont(new Font(Config.FONT, Font.PLAIN, 18));
			}
		});
		themeChooser.setModel(new DefaultComboBoxModel<Object>(
				new String[]
				{ "dark", "default-alt", "default", "eclipse", "idea",
						"monokai", "vs" }));
		themeChooser.setBounds(10, 30, 200, 20);
		settingsPanel.add(themeChooser);

		/*
		 * Code Folding Checkbox
		 */
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

		/*
		 * Resizable Window Checkbox
		 */
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

		/*
		 * EOL Markers Checkbox
		 */
		JCheckBox chckbxShowEolMarkers = new JCheckBox("Show EOL Markers");
		chckbxShowEolMarkers.addActionListener(new ActionListener()
		{
			public void actionPerformed(ActionEvent e)
			{
				textArea.setEOLMarkersVisible(chckbxShowEolMarkers.isSelected());
			}
		});
		chckbxShowEolMarkers.setBounds(10, 102, 115, 23);
		settingsPanel.add(chckbxShowEolMarkers);

		/*
		 * Show Bracket Animation Checkbox
		 */
		JCheckBox chckbxShowBracketAnimations = new JCheckBox(
				"Show Bracket Animations");
		chckbxShowBracketAnimations.setSelected(true);
		chckbxShowBracketAnimations.addActionListener(new ActionListener()
		{
			public void actionPerformed(ActionEvent e)
			{
				textArea.setAnimateBracketMatching(chckbxShowBracketAnimations
						.isSelected());
			}
		});
		chckbxShowBracketAnimations.setBounds(127, 102, 145, 23);
		settingsPanel.add(chckbxShowBracketAnimations);

		/*
		 * Add the label to help with the Editor sizing inputs
		 */
		JLabel lblEditorSizing = new JLabel("Editor Sizing");
		lblEditorSizing.setBounds(10, 157, 97, 14);
		settingsPanel.add(lblEditorSizing);

		/*
		 * Add the Rows input
		 */
		JTextField textFieldRows = new JTextField();
		textFieldRows.setText("21");
		textFieldRows.setBounds(10, 182, 86, 20);
		settingsPanel.add(textFieldRows);
		textFieldRows.setColumns(10);

		/*
		 * In between label
		 */
		JLabel lblRowsBy = new JLabel("rows by");
		lblRowsBy.setBounds(106, 185, 46, 14);
		settingsPanel.add(lblRowsBy);

		/*
		 * Add the Columns input
		 */
		JTextField textFieldColumns = new JTextField();
		textFieldColumns.setText("59");
		textFieldColumns.setBounds(153, 182, 86, 20);
		settingsPanel.add(textFieldColumns);
		textFieldColumns.setColumns(10);

		/*
		 * In between label
		 */
		JLabel lblColumns = new JLabel("columns");
		lblColumns.setBounds(249, 185, 46, 14);
		settingsPanel.add(lblColumns);

		/*
		 * Button to submit the rows and columns sizing
		 */
		JButton btnSubmitSizing = new JButton("Submit Sizing");
		btnSubmitSizing.addActionListener(new ActionListener()
		{
			public void actionPerformed(ActionEvent e)
			{
				textArea.setColumns(Integer.parseInt(textFieldColumns.getText()));
				textArea.setRows(Integer.parseInt(textFieldRows.getText()));
			}
		});
		btnSubmitSizing.setBounds(298, 181, 97, 23);
		settingsPanel.add(btnSubmitSizing);

		/*
		 * Font Slider Help Label
		 */
		JLabel lblFontSize = new JLabel("Font Size");
		lblFontSize.setBounds(10, 213, 46, 14);
		settingsPanel.add(lblFontSize);

		/*
		 * Current font size for clarity
		 */
		JLabel lblFontSizeCurrent = new JLabel("Font Size: 18");
		lblFontSizeCurrent.setBounds(10, 258, 86, 14);
		settingsPanel.add(lblFontSizeCurrent);

		/*
		 * Font Size Slider
		 */
		JSlider sliderFontSize = new JSlider();
		sliderFontSize.setValue(18);
		sliderFontSize.addChangeListener(new ChangeListener()
		{
			public void stateChanged(ChangeEvent e)
			{
				textArea.setFont(new Font("Ubuntu", Font.PLAIN, sliderFontSize
						.getValue()));
				scrollPane.getGutter().setLineNumberFont(
						new Font(Config.FONT, Font.PLAIN, sliderFontSize
								.getValue() - 3));
				lblFontSizeCurrent.setText("Font Size: "
						+ String.valueOf(sliderFontSize.getValue()));
			}
		});
		sliderFontSize.setBounds(10, 231, 200, 26);
		settingsPanel.add(sliderFontSize);

		/*
		 * Add GitHub link
		 */
		JLabel lblGithubLink = new JLabel("http://github.com/gusg21/terior/");
		lblGithubLink.addMouseListener(new MouseAdapter()
		{
			@Override
			public void mouseClicked(MouseEvent e)
			{
				try
				{
					java.awt.Desktop.getDesktop().browse(
							new URI(lblGithubLink.getText()));
				} catch (IOException | URISyntaxException e1)
				{
					e1.printStackTrace();
				}
			}
		});
		lblGithubLink.setForeground(Color.BLUE);
		Font font = lblGithubLink.getFont();
		@SuppressWarnings("unchecked")
		Map<TextAttribute, Integer> attributes = (Map<TextAttribute, Integer>) font
				.getAttributes();
		attributes.put(TextAttribute.UNDERLINE, TextAttribute.UNDERLINE_ON);
		lblGithubLink.setFont(font.deriveFont(attributes));
		lblGithubLink.setBounds(10, 541, 163, 14);
		lblGithubLink.setCursor(new Cursor(Cursor.HAND_CURSOR));
		settingsPanel.add(lblGithubLink);
	}

	public static void infoBox(String infoMessage)
	{
		JOptionPane.showMessageDialog(null, infoMessage, "Info",
				JOptionPane.INFORMATION_MESSAGE);
	}
}
