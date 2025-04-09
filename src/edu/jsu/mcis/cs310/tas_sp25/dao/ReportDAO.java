package edu.jsu.mcis.cs310.tas_sp25.dao;

import com.github.cliftonlabs.json_simple.JsonArray;
import com.github.cliftonlabs.json_simple.JsonObject;
import edu.jsu.mcis.cs310.tas_sp25.*;
import java.sql.*;


public class ReportDAO {

        
    /** DAO factory for managing database connections and cross-DAO access. */
    private final DAOFactory daoFactory;
    

    /**
     * Constructs a {@code BadgeDAO} using the provided factory.
     *
     * @param daoFactory Factory to get database connection
     */
    ReportDAO(DAOFactory daoFactory) {

        this.daoFactory = daoFactory;

    }
    public String getBadgeSummary(Integer departmentId) {
    Connection conn = daoFactory.getConnection();
    JsonArray json = new JsonArray();

    String query = "SELECT b.id AS badgeid, b.description AS name, d.description AS department, e.employeetypeid AS type " +
                   "FROM employee e " +
                   "JOIN badge b ON e.badgeid = b.id " +
                   "JOIN department d ON e.departmentid = d.id " +
                   "WHERE e.inactive IS NULL";

    if (departmentId != null) {
        query += " AND d.id = ?";
    }

    query += " ORDER BY b.description";

    try (PreparedStatement ps = conn.prepareStatement(query)) {

        if (departmentId != null) {
            ps.setInt(1, departmentId);
        }

        ResultSet rs = ps.executeQuery();

        while (rs.next()) {
            JsonObject obj = new JsonObject();
            obj.put("badgeid", rs.getString("badgeid"));
            obj.put("name", rs.getString("name"));
            obj.put("department", rs.getString("department"));

            String typeCode = rs.getString("type");
            String type = typeCode.equals("0") ? "Temporary Employee" : "Full-Time Employee";
            obj.put("type", type);

            json.add(obj);
        }

    } catch (Exception e) {
        e.printStackTrace();
    }

    return json.toJson();
    }
}