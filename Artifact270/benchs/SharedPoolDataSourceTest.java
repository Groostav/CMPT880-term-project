
import java.util.ConcurrentModificationException;

import javax.sql.ConnectionPoolDataSource;

import org.apache.commons.dbcp.datasources.SharedPoolDataSource;

public class SharedPoolDataSourceTest {

	public static void main(String[] args) {
		final SharedPoolDataSource spds = new SharedPoolDataSource();
		final ConnectionPoolDataSource cpds = spds.getConnectionPoolDataSource();
		System.out.println("Cpds is " + cpds);
		spds.setConnectionPoolDataSource(cpds);
		spds.setDataSourceName("aaa");
                try {
		  spds.close();
                } catch (Exception e) {}
		
	}
	
}
