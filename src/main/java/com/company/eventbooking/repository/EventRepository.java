package com.company.eventbooking.repository;

import com.company.eventbooking.dto.EventRequest;
import com.company.eventbooking.dto.EventResponse;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public class EventRepository {

    private final JdbcTemplate jdbcTemplate;

    public EventRepository(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    public int create(EventRequest eventRequest) {
        String sql= """
                 INSERT INTO events(event_code,name,event_date, venue_id) VALUES(?,?,?,?)
                 """;
        return jdbcTemplate.update(sql, eventRequest.eventCode(), eventRequest.name(), eventRequest.eventDate(), eventRequest.venueId());
    }

    public List<EventResponse> findAll() {
        String sql="SELECT id,event_code,name,event_date,venue_id FROM events";
        return jdbcTemplate.query(sql,(rs,rowNum) ->
                new EventResponse(
                        rs.getLong("id"),
                        rs.getString("event_code"),
                        rs.getString("name"),
                        rs.getTimestamp("event_date").toLocalDateTime(),
                        rs.getLong("venue_id")
                )
        );

    }

    public List<EventResponse> findByVenueId(Long venueId) {
        String sql = """
                SELECT id,event_code,name,event_date,venue_id FROM events WHERE event_venue_id = ?;
                """;
        return jdbcTemplate.query(sql, (rs, rowNum) ->
                        new EventResponse(
                                rs.getLong("id"),
                                rs.getString("event_code"),
                                rs.getString("name"),
                                rs.getTimestamp("event_date").toLocalDateTime(),
                                rs.getLong("venue_id")
                        ),
                venueId);
    }

    public int update(Long id, EventRequest request) {
        String sql= """
                UPDATE events SET event_codes=?, name=?,event_date=?, venue_id=? WHERE id=?;
                """;
        return jdbcTemplate.update(sql, request.eventCode(), request.name(), request.eventDate(), request.venueId(), id);
    }

    public int delete(Long id) {
        String sql="DELETE FROM events WHERE id=?;";
        return jdbcTemplate.update(sql, id);
    }

}