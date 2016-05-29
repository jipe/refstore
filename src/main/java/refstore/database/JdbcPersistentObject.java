package refstore.database;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;

import javax.sql.DataSource;

public class JdbcPersistentObject {

	private final String tableName;
	private final DataSource ds;

	private Integer id;

	protected JdbcPersistentObject(DataSource ds, String tableName) {
		this.ds = ds;
		this.tableName = tableName;
	}

	public Integer getId() {
		return id;
	}

	protected void setId(Integer id) {
		this.id = id;
	}

	public boolean isPersisted() {
		return id == null;
	}

	public int delete() throws SQLException {
		if (isPersisted()) {
			try (Connection connection = ds.getConnection()) {
				PreparedStatement delete = connection.prepareStatement(String.format("delete from %s where id = ?", tableName));
				delete.setInt(1, id);
				return delete.executeUpdate();
			}
		} else {
			return 0;
		}
	}

}
