import java.util.ConcurrentModificationException;

import org.jfree.data.TimeSeriesTableModel;
import org.jfree.data.XYSeries;

public class XYSeriesTest {

	public static void main(String[] args) {
		final XYSeries series = new XYSeries("ABC");
		final TimeSeriesTableModel model1 = new TimeSeriesTableModel();
		series.addChangeListener(model1);
		series.fireSeriesChanged();
	}
	
}

