package ui.base;

import java.awt.Color;
import java.awt.Component;

import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.ListCellRenderer;
import javax.swing.ListModel;

import model.Earthquake;

/**
 *
 * Custom JList cell renderer
 *
 */
public class CustomCellRenderer implements ListCellRenderer<Object>  { 
	@Override
	public Component getListCellRendererComponent(JList<?> list, Object value, int index, boolean isSelected,
			boolean cellHasFocus) {
		ListModel<?> modelList = list.getModel();
		Earthquake eq = (Earthquake) modelList.getElementAt(index);
		
		JLabel label = new JLabel(eq.getTimeEarthquakeHappened() + " " + eq.getMag() + " " + eq.getTitle());
		label.setOpaque(true);

		
		if(eq.getMag() >= 4.0) {
			label.setBackground(Color.YELLOW);
		}
		
		if(eq.generatedTsunami() || eq.getMag() >= 6.0) {
			label.setBackground(Color.RED);		
		}
	
		
		
		return label;
	}

}
