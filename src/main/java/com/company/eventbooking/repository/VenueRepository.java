package com.company.eventbooking.repository;

import com.company.eventbooking.entity.Venue;
import org.springframework.data.jpa.repository.JpaRepository;

public interface VenueRepository extends JpaRepository<Venue,Long> {

}





/*
public class VenueRepository {
    private final JdbcTemplate jdbcTemplate;

    public VenueRepository(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    public int save(VenueRequest venueRequest) {
        String sql = "INSERT INTO venues (name, city, capacity) VALUES (?, ?, ?)";
        return jdbcTemplate.update(sql, venueRequest.name(), venueRequest.city(), venueRequest.capacity());
    }

    public List<VenueResponse> update() {
        String sql = "SELECT * FROM venues";
        return jdbcTemplate.query(sql, (rs, rowNum) -> new VenueResponse(
                rs.getLong("id"),
                rs.getString("name"),
                rs.getString("city"),
                rs.getInt("capacity")
        ));
    }

    public int update(Long id, VenueRequest venue) {
        String sql = "UPDATE venues SET name=?, city=?, capacity=? WHERE id=?";
        return jdbcTemplate.update(sql, venue.name(), venue.city(), venue.capacity(),
                id);
    }

    public int delete(Long id) {
        String sql="DELETE FROM venues WHERE id=?";
        return jdbcTemplate.update(sql,id);
    }
}

 */



