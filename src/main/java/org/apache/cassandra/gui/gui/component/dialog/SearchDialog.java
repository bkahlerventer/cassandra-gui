package org.apache.cassandra.gui.gui.component.dialog;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.InputMethodEvent;
import java.awt.event.InputMethodListener;

import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JTextField;
import javax.swing.JTree;
import javax.swing.SwingUtilities;
import javax.swing.text.Position;
import javax.swing.tree.TreePath;

import org.apache.commons.lang.StringUtils;

import com.cloudgarden.layout.AnchorConstraint;
import com.cloudgarden.layout.AnchorLayout;


/**
* This code was edited or generated using CloudGarden's Jigloo
* SWT/Swing GUI Builder, which is free for non-commercial
* use. If Jigloo is being used commercially (ie, by a corporation,
* company or business for any purpose whatever) then you
* should purchase a license for each developer using Jigloo.
* Please visit www.cloudgarden.com for details.
* Use of Jigloo implies acceptance of these licensing terms.
* A COMMERCIAL LICENSE HAS NOT BEEN PURCHASED FOR
* THIS MACHINE, SO JIGLOO OR THIS CODE CANNOT BE USED
* LEGALLY FOR ANY CORPORATE OR COMMERCIAL PURPOSE.
*/
public class SearchDialog extends javax.swing.JDialog {
	private JButton findButton;
	private JButton cancalButton;
	private JLabel searchLabel;
	private JTextField searchTextField;
	private JTree jtree;
	
	public void setJtree(JTree jtree) {
		this.jtree = jtree;
	}

	private int startRow=0;
	/**
	* Auto-generated main method to display this JDialog
	*/
	public static void main(String[] args) {
		SwingUtilities.invokeLater(new Runnable() {
			public void run() {
				JFrame frame = new JFrame();
				SearchDialog inst = new SearchDialog(frame);
				inst.setVisible(true);
			}
		});
	}
	
	public SearchDialog(JTree jtree) {		
		initGUI();
		this.jtree = jtree;
	}

	
	public SearchDialog(JFrame frame) {
		super(frame);
		initGUI();
	}
	
	
	
	private void initGUI() {
		try {
			AnchorLayout thisLayout = new AnchorLayout();
			getContentPane().setLayout(thisLayout);
			this.setTitle("Search");
			{
				cancalButton = new JButton();
				getContentPane().add(cancalButton, new AnchorConstraint(586, 842, 790, 606, AnchorConstraint.ANCHOR_REL, AnchorConstraint.ANCHOR_REL, AnchorConstraint.ANCHOR_REL, AnchorConstraint.ANCHOR_REL));
				cancalButton.setText("Cancel");
				cancalButton.setPreferredSize(new java.awt.Dimension(83, 25));
				cancalButton.addActionListener(new ActionListener() {
					
					@Override
					public void actionPerformed(ActionEvent e) {
						setVisible(false);
						
					}
				});
			}
			{
				searchLabel = new JLabel();
				getContentPane().add(searchLabel, new AnchorConstraint(182, 327, 351, 121, AnchorConstraint.ANCHOR_REL, AnchorConstraint.ANCHOR_REL, AnchorConstraint.ANCHOR_REL, AnchorConstraint.ANCHOR_REL));
				searchLabel.setText("Search ");
				searchLabel.setPreferredSize(new java.awt.Dimension(72, 20));
			}
			{
				searchTextField = new JTextField();
				getContentPane().add(searchTextField, new AnchorConstraint(184, 842, 372, 276, AnchorConstraint.ANCHOR_REL, AnchorConstraint.ANCHOR_REL, AnchorConstraint.ANCHOR_REL, AnchorConstraint.ANCHOR_REL));
				searchTextField.setPreferredSize(new java.awt.Dimension(199, 23));
				searchTextField.addInputMethodListener(new InputMethodListener() {
					
					@Override
					public void inputMethodTextChanged(InputMethodEvent event) {
						System.out.println("input: " + event.getText());
						
					}
					
					@Override
					public void caretPositionChanged(InputMethodEvent event) {
						// TODO Auto-generated method stub
						
					}
				});
			}
			{
				findButton = new JButton();
				getContentPane().add(findButton, new AnchorConstraint(586, 544, 790, 348, AnchorConstraint.ANCHOR_REL, AnchorConstraint.ANCHOR_REL, AnchorConstraint.ANCHOR_REL, AnchorConstraint.ANCHOR_REL));
				findButton.setText("Find");
				findButton.setPreferredSize(new java.awt.Dimension(69, 25));
				findButton.addActionListener(new ActionListener() {
					
					@Override
					public void actionPerformed(ActionEvent e) {
						findAction(e);
						
					}
				});
			}
			
			setLocationRelativeTo(null);
	        
			
			this.setSize(360, 150);
			setDefaultCloseOperation(JDialog.DISPOSE_ON_CLOSE);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	public void setFindActionListner(ActionListener actionListener) {
		findButton.addActionListener(actionListener);
	}
	
	public void findAction(ActionEvent e) {
		String searchText = searchTextField.getText();

		TreePath path = getNextMatch(jtree, searchText, startRow, Position.Bias.Forward);
		
						
		jtree.setSelectionPath(path);
		jtree.scrollPathToVisible(path);
		startRow = jtree.getLeadSelectionRow() + 1;

		
	}
	
	public TreePath getNextMatch(JTree jtree, String searchStr, int startingRow,
			Position.Bias bias) {

		int max = jtree.getRowCount();
		if (searchStr == null) {
			throw new IllegalArgumentException();
		}
		if (startingRow < 0 || startingRow >= max) {
			throw new IllegalArgumentException();
		}
//		matchword = matchword.toUpperCase();

		// start search from the next/previous element froom the
		// selected element
		int increment = (bias == Position.Bias.Forward) ? 1 : -1;
		int row = startingRow;
		do {
			TreePath path = jtree.getPathForRow(row);
			String text = jtree.convertValueToText(path.getLastPathComponent(),
					jtree.isRowSelected(row), jtree.isExpanded(row), true, row,
					false);
			
			
			if (StringUtils.contains(text, searchStr)) {
//			if (text.toUpperCase().startsWith(matchword)) {
				return path;
			}
			row = (row + increment + max) % max;
		} while (row != startingRow);
		return null;
	}

}
