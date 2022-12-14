package learn.cyburbia.data;

import learn.cyburbia.data.mappers.DeveloperMapper;
import learn.cyburbia.data.mappers.ProjectMapper;
import learn.cyburbia.models.Developer;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.stereotype.Repository;

import java.security.Key;
import java.sql.PreparedStatement;
import java.sql.Statement;
import java.util.List;

@Repository
public class DeveloperJdbcTemplateRepository implements DeveloperRepository{

    private final JdbcTemplate jdbcTemplate;

    public DeveloperJdbcTemplateRepository(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    @Override
    public List<Developer> findAll() {
        final String sql = "select developer_id, `name`, email, location_id " +
                "from developer;";
        return jdbcTemplate.query(sql, new DeveloperMapper());
    }

    @Override
    public Developer findById(int developerId) {
        final String sql = "select developer_id, `name`, email, location_id " +
                "from developer " +
                "where developer_id = ?;";
        Developer developer = jdbcTemplate.query(sql, new DeveloperMapper(), developerId).stream()
                .findFirst().orElse(null);

        if (developer != null) {
            addProjects(developer);
        }
        return developer;
    }

    @Override
    public Developer add(Developer developer) {
        final String sql = "insert into developer (`name`, email, location_id) " +
                " values (?,?,?);";

        KeyHolder keyHolder = new GeneratedKeyHolder();
        int rowsAffected = jdbcTemplate.update(connection -> {
            PreparedStatement ps = connection.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS);
            ps.setString(1, developer.getName());
            ps.setString(2, developer.getEmail());
            ps.setInt(3, developer.getLocationId());
            return ps;
        }, keyHolder);
        if (rowsAffected <= 0) {
            return null;
        }

        developer.setDeveloperId(keyHolder.getKey().intValue());
        return developer;
    }

    @Override
    public boolean update(Developer developer) {
        final String sql = "update developer set "
                + "`name` = ?, "
                + "email = ?, "
                + "location_id = ? "
                + "where developer_id = ?;";

        return jdbcTemplate.update(sql,
                developer.getName(),
                developer.getEmail(),
                developer.getLocationId(),
                developer.getDeveloperId()) > 0;
    }

    private void addProjects(Developer developer){
        final String sql = "select project.project_id, project.location_id, project.agency_id, project.developer_id, project.sq_ft, project.type, project.status, project.description, project.budget "
                +"from project "
                +"inner join developer on developer.developer_id = project.developer_id "
                +"where project.developer_id = ?;";
        var projects = jdbcTemplate.query(sql, new ProjectMapper(), developer.getDeveloperId());
        developer.setProjects(projects);
    }
}
