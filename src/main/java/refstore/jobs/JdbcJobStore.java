package refstore.jobs;

import java.io.IOException;
import java.io.StringReader;
import java.io.StringWriter;
import java.lang.reflect.InvocationTargetException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.sql.DataSource;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import refstore.database.migrations.MiniMigrations;

public class JdbcJobStore implements JobStore {

	private final Logger log = LoggerFactory.getLogger(getClass());
	private final DataSource ds;

	public JdbcJobStore(DataSource ds) {
		this.ds = ds;
	}

	public void applyMigrations() throws IOException, SQLException {
		MiniMigrations.apply(ds, getClass().getResource("/sql/postgresql/job_store").getFile());
	}

	@Override
	public List<Job> listJobs(JobDefinition jobDefinition) throws JobStoreException {
		if (!jobDefinition.isPersisted()) {
			throw new JobStoreException("Trying to list jobs from volatile job definition");
		}

		List<Job> result = new ArrayList<>();
		try (Connection connection = ds.getConnection()) {
			PreparedStatement select = connection.prepareStatement("select * from job_instances where job_definition_id = ?");
			select.setInt(1, jobDefinition.getId());
			ResultSet rs = select.executeQuery();
			while (rs.next()) {
				try {
					result.add(createJob(rs, jobDefinition.getType()));
				} catch (Exception e) {
					log.error("Error creating job instance", e);
				}
			}
			rs.close();
			select.close();
		} catch (SQLException e) {
			throw new JobStoreException("Error listing jobs from job definition", e);
		}
		return result;
	}

	private <T extends Job> T createJob(ResultSet rs, Class<T> type) throws SQLException, JobException, IOException {
		try {
			T job = type.getConstructor(JobStore.class).newInstance(this);
			job.setId(rs.getInt("id"));
			job.setCreatedAt(rs.getDate("created_at"));
			job.readData(new StringReader(rs.getString("data")));
			return job;
		} catch (InstantiationException e) {
			throw new RuntimeException(String.format("Error instantiating job %s", type.getCanonicalName()));
		} catch (IllegalAccessException e) {
			throw new RuntimeException(String.format("Access denied for instantiating job %s", type.getCanonicalName()));
		} catch (IllegalArgumentException e) {
			throw new RuntimeException(String.format("Illegal argument when instantiating job %s", type.getCanonicalName()));
		} catch (InvocationTargetException e) {
			throw new JobException("Error creating job", e.getCause());
		} catch (NoSuchMethodException e) {
			throw new RuntimeException(String.format("Job class %s has no appropriate constructor", type.getCanonicalName()));
		} catch (SecurityException e) {
			throw new RuntimeException("Error creating job. Check security manager", e);
		}
	}

	@Override
	public List<JobDefinition> listJobDefinitions() throws JobStoreException {
		List<JobDefinition> result = new ArrayList<>();

		String typeName = null;
		String name     = null;

		try (Connection connection = ds.getConnection()) {
			Statement select = connection.createStatement();
			ResultSet rs = select.executeQuery("select * from job_definitions");
			while (rs.next()) {
				name     = rs.getString("name");
				typeName = rs.getString("class");

				@SuppressWarnings("unchecked")
				Class<Job> type = (Class<Job>) Class.forName(typeName);

				JobDefinition jobDefinition = new JobDefinition(name, type, this);
				jobDefinition.setId(rs.getInt("id"));
				jobDefinition.setCreatedAt(rs.getDate("created_at"));

				result.add(jobDefinition);
			}
			rs.close();
			select.close();
		} catch (SQLException e) {
			throw new JobStoreException("Error getting job definitions", e);
		} catch (ClassNotFoundException e) {
			log.error("Unable to get job class for job definition \"{}\" ({}) : \"{}\"", name, typeName, e.getMessage());
		}
		return result;
	}

	@Override
	public void saveJobDefinition(JobDefinition jobDefinition) throws JobStoreException {
		try (Connection connection = ds.getConnection()) {
			boolean performUpdate = false;
			if (jobDefinition.isPersisted()) {
				PreparedStatement select = connection.prepareStatement("select 1 from job_definitions where id = ?");
				select.setInt(1, jobDefinition.getId());
				if (select.executeQuery().next()) {
					performUpdate = true;
				}
				select.close();
			}

			if (performUpdate) {
				PreparedStatement update = connection.prepareStatement("update job_definitions set name = ?, class = ? where id = ?");
				update.setString(1, jobDefinition.getName());
				update.setString(2, jobDefinition.getType().getCanonicalName());
				update.setInt(3, jobDefinition.getId());
				if (update.executeUpdate() == 0) {
					throw new JobStoreException("Updating job definition into database did not affect any rows");
				}
				update.close();
			} else {
				PreparedStatement insert = connection.prepareStatement("insert into job_definitions (name, class, created_at) values (?, ?, ?)", Statement.RETURN_GENERATED_KEYS);
				insert.setString(1, jobDefinition.getName());
				insert.setString(2, jobDefinition.getClass().getCanonicalName());
				insert.setDate(3, new java.sql.Date(new Date().getTime()));
				if (insert.executeUpdate() == 0) {
					throw new JobStoreException("Inserting job definition into database did not affect any rows");
				} else {
					ResultSet keys = insert.getGeneratedKeys();
					if (keys.next()) {
						jobDefinition.setId(keys.getInt(1));
					} else {
						throw new JobStoreException("No keys were generated when inserting new job definition into database");
					}

				}
				insert.close();
			}
		} catch (SQLException e) {
			throw new JobStoreException("Error saving job definition", e);
		}
	}

	@Override
	public JobDefinition getJobDefinition(int id) throws JobStoreException {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public JobDefinition createJobDefinition(String name, Class<? extends Job> type) {
		return new JobDefinition(name, type, this);
	}

	@Override
	public void deleteJobDefinition(int id) throws JobStoreException {
		try (Connection connection = ds.getConnection()) {
			PreparedStatement delete = connection.prepareStatement("delete from job_definitions where id = ?");
			delete.setInt(1, id);
			if (delete.executeUpdate() == 0) {
				throw new JobStoreException("Deleting job definition did not affect any rows");
			}
			delete.close();
		} catch (SQLException e) {
			throw new JobStoreException("Error deleting job definition", e);
		}
	}

	@Override
	public void saveJob(Job job) throws JobStoreException {
		try (Connection connection = ds.getConnection()) {
			boolean performUpdate = false;
			if (job.isPersisted()) {
				PreparedStatement select = connection.prepareStatement("select 1 from job_instances where id = ?");
				select.setInt(1, job.getId());
				if (select.executeQuery().next()) {
					performUpdate = true;
				}
			}

			if (performUpdate) {
				PreparedStatement update = connection.prepareStatement("update job_instances set status = ?, data = ? where id = ?");
				update.setString(1, job.getStatus().toString());
				StringWriter writer = new StringWriter();
				job.writeData(writer);
				update.setString(2, writer.toString());
				update.setInt(3, job.getId());
			} else {

			}
		} catch (SQLException e) {
			throw new JobStoreException("Error saving job", e);
		} catch (JobException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	@Override
	public Job getJob(int id) throws JobStoreException {
		Job result = null;
		try (Connection connection = ds.getConnection()) {
			PreparedStatement select = connection.prepareStatement(
					"select * from job_definitions jd inner join job_instances ji on (jd.id = ji.job_definition_id) "
							+ "where ji.id = ?");
			select.setInt(1, id);

			ResultSet rs = select.executeQuery();
			if (rs.next()) {

			}
		} catch (SQLException e) {
			throw new JobStoreException("Error getting job", e);
		}
		return result;
	}

}
