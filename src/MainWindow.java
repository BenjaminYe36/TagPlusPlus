import org.eclipse.swt.graphics.*;
import org.eclipse.swt.widgets.*;

import java.awt.Desktop;
import java.io.*;
import java.util.*;
import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.TableEditor;
import org.eclipse.wb.swt.SWTResourceManager;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;

public class MainWindow {

	protected Shell shell;
	protected Display display;
	private Table table;
	private File file;
	private Text textBox;
	private Text query;
	private Table table_1;
	private Table table_2;
	private Table table_3;

	/**
	 * Launch the application.
	 * 
	 * @param args
	 */
	public static void main(String[] args) {
		try {
			MainWindow window = new MainWindow();
			window.open();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	/**
	 * Open the window.
	 */
	public void open() {
		createContents();
		shell.open();
		shell.layout();
		while (!shell.isDisposed()) {
			if (!display.readAndDispatch()) {
				display.sleep();
			}
		}
	}

	public void removeAndFillTable(Table table, Set<String> result) {
		table.removeAll();
		for (Control c : table.getChildren()) {
			c.dispose();
		}
		for (String dir : result) {
			Link link = new Link(table, SWT.NONE);
			link.addSelectionListener(new SelectionAdapter() {
				public void widgetSelected(SelectionEvent e) {
					try {
						Desktop.getDesktop().open(new File(dir));
					} catch (IOException e1) {
						e1.printStackTrace();
					}
				}
			});
			link.setText("<a>open file</a>");
			link.pack();

			TableItem tableItem = new TableItem(table, SWT.NONE);
			tableItem.setText(dir);
			TableEditor editor = new TableEditor(table);

			editor.minimumWidth = link.getSize().x;
			editor.horizontalAlignment = SWT.CENTER;
			editor.setEditor(link, tableItem, 1);
		}
	}

	/**
	 * Create contents of the window.
	 */
	protected void createContents() {
		display = new Display();
		shell = new Shell(display);
		shell.setSize(1200, 800);
		shell.setText("Tag++ : A New way to manage your tag system");
		shell.setLayout(null);

		file = null;

		TagManagement.init();

		// Sets window in the center
		Monitor primary = display.getPrimaryMonitor();
		Rectangle bounds = primary.getBounds();
		Rectangle rect = shell.getBounds();
		int x = bounds.x + (bounds.width - rect.width) / 2;
		int y = bounds.y + (bounds.height - rect.height) / 2;
		shell.setLocation(x, y);

		TabFolder tabFolder = new TabFolder(shell, SWT.NONE);
		tabFolder.setBounds(0, 0, 1182, 753);

		TabItem tbtmManageTags = new TabItem(tabFolder, SWT.NONE);
		tbtmManageTags.setText("Manage tags for a file");

		Composite composite = new Composite(tabFolder, SWT.NONE);
		tbtmManageTags.setControl(composite);

		Label lblFilePath = new Label(composite, SWT.NONE);
		lblFilePath.setFont(SWTResourceManager.getFont("Microsoft YaHei UI", 15, SWT.NORMAL));
		lblFilePath.setBounds(33, 22, 822, 57);
		lblFilePath.setText("File Path:");

		Label lblTagNum = new Label(composite, SWT.NONE);
		lblTagNum.setFont(SWTResourceManager.getFont("Microsoft YaHei UI", 15, SWT.NORMAL));
		lblTagNum.setBounds(21, 119, 400, 40);

		Button btnBrowseAFile = new Button(composite, SWT.NONE);
		btnBrowseAFile.addListener(SWT.Selection, new Listener() {
			public void handleEvent(Event event) {
				table.removeAll();
				FileDialog fileDialog = new FileDialog(shell, SWT.OPEN);
				fileDialog.setFilterExtensions(new String[] { "*.*" });
				fileDialog.setFilterNames(new String[] { "Any" });
				String firstFile = fileDialog.open();
				if (firstFile != null) {
					lblFilePath.setText("File Path: " + fileDialog.getFilterPath() + "\\" + fileDialog.getFileName());
					lblFilePath.pack();
					file = new File(fileDialog.getFilterPath() + "\\" + fileDialog.getFileName());
					Set<String> tagSet = TagManagement.loadTags(file);
					if (tagSet.isEmpty()) {
						lblTagNum.setText("No tags for this file yet");
					} else {
						lblTagNum.setText("This file has " + tagSet.size() + " tag(s)");
						for (String tag : tagSet) {
							new TableItem(table, SWT.NONE).setText(tag);
						}
					}
				} else {
					lblFilePath.setText("File Path:");
					lblTagNum.setText("");
					file = null;
				}
			}
		});
		btnBrowseAFile.setFont(SWTResourceManager.getFont("Microsoft YaHei UI", 15, SWT.NORMAL));
		btnBrowseAFile.setBounds(978, 24, 186, 51);
		btnBrowseAFile.setText("Browse A File");

		Label label = new Label(composite, SWT.SEPARATOR | SWT.HORIZONTAL);
		label.setBounds(21, 100, 1143, 2);

		table = new Table(composite, SWT.BORDER | SWT.FULL_SELECTION);
		table.setBounds(21, 192, 400, 481);
		table.setHeaderVisible(true);
		table.setLinesVisible(true);

		TableColumn tblclmnTags = new TableColumn(table, SWT.NONE);
		tblclmnTags.setWidth(370);
		tblclmnTags.setText("Tags");

		textBox = new Text(composite, SWT.BORDER);
		textBox.setToolTipText("Type your tag name here");
		textBox.setFont(SWTResourceManager.getFont("Microsoft YaHei UI", 15, SWT.NORMAL));
		textBox.setBounds(457, 130, 408, 45);

		Button btnAdd = new Button(composite, SWT.NONE);
		btnAdd.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				if (file != null) {
					String[] tags = textBox.getText().split(",");
					for (String tag : tags) {
						tag = tag.strip();
						if (!tag.equals("") && !TagManagement.getSet().contains(tag)) {
							TagManagement.add(tag, file);
							new TableItem(table, SWT.NONE).setText(tag);
						}
					}
					textBox.setText("");
					lblTagNum.setText("This file has " + TagManagement.getSet().size() + " tag(s)");
				}
			}
		});
		btnAdd.setFont(SWTResourceManager.getFont("Microsoft YaHei UI", 15, SWT.NORMAL));
		btnAdd.setBounds(909, 132, 186, 43);
		btnAdd.setText("Add");

		Button btnDelete = new Button(composite, SWT.NONE);
		btnDelete.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				if (file != null) {
					try {
						String tag = table.getItem(table.getSelectionIndex()).getText();
						TagManagement.remove(tag, file);
						table.remove(table.getSelectionIndex());
						lblTagNum.setText("This file has " + TagManagement.getSet().size() + " tag(s)");
					} catch (Exception e2) {
						e2.printStackTrace();
					}
				}
			}
		});
		btnDelete.setFont(SWTResourceManager.getFont("Microsoft YaHei UI", 15, SWT.NORMAL));
		btnDelete.setBounds(455, 206, 127, 467);
		btnDelete.setText("Delete");

		TabItem tbtmSearchTags = new TabItem(tabFolder, SWT.NONE);
		tbtmSearchTags.setText("Search tags");

		Composite composite_1 = new Composite(tabFolder, SWT.NONE);
		tbtmSearchTags.setControl(composite_1);

		query = new Text(composite_1, SWT.BORDER);
		query.setFont(SWTResourceManager.getFont("Microsoft YaHei UI", 15, SWT.NORMAL));
		query.setToolTipText("Type tags seprated with \",\" to search");
		query.setBounds(44, 23, 806, 49);

		Button btnSearch = new Button(composite_1, SWT.NONE);
		btnSearch.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				if (!query.getText().strip().equals("")) {
					String[] tags = query.getText().split(",");
					Set<String> result = new HashSet<>();
					result.addAll(TagManagement.get(tags[0].strip()));
					for (int i = 1; i < tags.length; i++) {
						if (result.isEmpty()) {
							break;
						}
						result.retainAll(TagManagement.get(tags[i].strip()));
					}
					removeAndFillTable(table_1, result);
				}
			}
		});
		btnSearch.setFont(SWTResourceManager.getFont("Microsoft YaHei UI", 15, SWT.NORMAL));
		btnSearch.setBounds(908, 23, 227, 49);
		btnSearch.setText("Search");

		table_1 = new Table(composite_1, SWT.BORDER | SWT.FULL_SELECTION);
		table_1.setBounds(44, 105, 1091, 538);
		table_1.setHeaderVisible(true);
		table_1.setLinesVisible(true);

		TableColumn tblclmnFullFilePath = new TableColumn(table_1, SWT.NONE);
		tblclmnFullFilePath.setWidth(800);
		tblclmnFullFilePath.setText("Full File Path");

		TableColumn tblclmnHyperlinkToFile = new TableColumn(table_1, SWT.NONE);
		tblclmnHyperlinkToFile.setWidth(200);
		tblclmnHyperlinkToFile.setText("Hyperlink to File");

		TabItem tbtmAllTagsView = new TabItem(tabFolder, SWT.NONE);
		tbtmAllTagsView.setText("All Tags View");

		Composite composite_2 = new Composite(tabFolder, SWT.NONE);
		tbtmAllTagsView.setControl(composite_2);

		table_2 = new Table(composite_2, SWT.BORDER | SWT.FULL_SELECTION);
		table_2.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				try {
					String tag = table_2.getItem(table_2.getSelectionIndex()).getText(0);
					removeAndFillTable(table_3, TagManagement.getMap().get(tag));
				} catch (Exception e2) {
					e2.printStackTrace();
				}
			}
		});
		table_2.setBounds(35, 48, 316, 636);
		table_2.setHeaderVisible(true);
		table_2.setLinesVisible(true);

		TableColumn tblclmnTagName = new TableColumn(table_2, SWT.NONE);
		tblclmnTagName.setWidth(200);
		tblclmnTagName.setText("Tag Name");

		TableColumn tblclmnSize = new TableColumn(table_2, SWT.NONE);
		tblclmnSize.setWidth(100);
		tblclmnSize.setText("Size");

		table_3 = new Table(composite_2, SWT.BORDER | SWT.FULL_SELECTION);
		table_3.setBounds(437, 48, 696, 636);
		table_3.setHeaderVisible(true);
		table_3.setLinesVisible(true);
		
		TableColumn tblclmnFullFilePath_1 = new TableColumn(table_3, SWT.NONE);
		tblclmnFullFilePath_1.setWidth(500);
		tblclmnFullFilePath_1.setText("Full File Path");
		
		TableColumn tblclmnHyperLink = new TableColumn(table_3, SWT.NONE);
		tblclmnHyperLink.setWidth(160);
		tblclmnHyperLink.setText("Hyperlink to File");

		Button btnLoadAllTags = new Button(composite_2, SWT.NONE);
		btnLoadAllTags.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				table_2.removeAll();
				for (String tag : TagManagement.getMap().keySet()) {
					TableItem ti = new TableItem(table_2, SWT.NONE);
					ti.setText(new String[] {tag, ""+TagManagement.getMap().get(tag).size()});
				}
			}
		});
		btnLoadAllTags.setBounds(35, 10, 131, 30);
		btnLoadAllTags.setText("Load All Tags");

	}
}
