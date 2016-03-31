
import org.jfree.data.time.Day;
import org.jfree.data.time.TimeSeries;

public class TimeSeriesTest {

	public static void main(String[] args) {
		final TimeSeries series = new TimeSeries("abc");
		final Day day1 = new Day(1,1,1970);
		final Day day2 = new Day(1,1,1971);
		series.add(day1, 3.0);
		series.add(day2, 4.0);
		}
}
