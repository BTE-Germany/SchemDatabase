package de.leander.schemdatabase.util;

import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.sql.*;
import java.util.UUID;

public class MySQL {
    public static HikariDataSource dataSource;
    public static Connection con;

    public static final String ANSI_RESET = "\u001B[0m";
    public static final String ANSI_BLACK = "\u001B[30m";
    public static final String ANSI_RED = "\u001B[31m";
    public static final String ANSI_GREEN = "\u001B[32m";
    public static final String ANSI_YELLOW = "\u001B[33m";
    public static final String ANSI_BLUE = "\u001B[34m";
    public static final String ANSI_PURPLE = "\u001B[35m";
    public static final String ANSI_CYAN = "\u001B[36m";
    public static final String ANSI_WHITE = "\u001B[37m";

    public static void connect() {
        FileBuilder fb = new FileBuilder("plugins/SchemDatabase", "mysql.yml");
        String host = fb.getString("mysql.host");
        String port = fb.getString("mysql.port");
        String database = fb.getString("mysql.database");
        String user = fb.getString("mysql.user");
        String password = fb.getString("mysql.password");
        if (!isConnected()) {
            HikariConfig config = new HikariConfig();
            config.setJdbcUrl("jdbc:mysql://" + host + ":" + port + "/" + database);
            config.setUsername(user);
            config.setPassword(password);
            config.addDataSourceProperty("cachePrepStmts", "true");
            config.addDataSourceProperty("prepStmtCacheSize", "250");
            config.addDataSourceProperty("prepStmtCacheSqlLimit", "2048");

            dataSource = new HikariDataSource(config);
            try {
                con = dataSource.getConnection();
            } catch (SQLException throwables) {
                throwables.printStackTrace();
            }

            System.out.println("[SchemDatabase]" + ANSI_GREEN + " MySQL connection ok!" + ANSI_RESET);
        }
    }

    public static void disconnect() {
        if (isConnected()) {
            dataSource.close();
            System.out.println("[SchemDatabase] MySQL connection closed");
        }

    }

    public static boolean isConnected() {
        return (con != null && (!dataSource.isClosed()));
    }


    public static Connection getConnection() {
        if (!isConnected())
            connect();
        return con;
    }

    public static ResultSet getAllSchematics() throws SQLException {
        PreparedStatement ps = getConnection().prepareStatement("SELECT * FROM `schematics`");
        ResultSet resultSet = ps.executeQuery();
        return resultSet;
    }

    public static int getAllSchematicsCount() throws SQLException {
        PreparedStatement ps = getConnection().prepareStatement("SELECT COUNT(*) FROM `schematics`");
        ResultSet rs = ps.executeQuery();
        rs.next();
        int count = rs.getInt("COUNT(*)");
        return count;
    }

    public static ResultSet getAllSchematicsByCategory(String category) throws SQLException {
        PreparedStatement ps = getConnection().prepareStatement("SELECT * FROM `schematics` WHERE category=?");
        ps.setString(1, category);
        ResultSet resultSet = ps.executeQuery();
        return resultSet;
    }
    public static ResultSet getPageSchematicsByCategory(String category, int page, int itemsOnPage) throws SQLException {
        int offset = page*itemsOnPage;
        PreparedStatement ps = getConnection().prepareStatement("SELECT * FROM `schematics` WHERE category=? LIMIT ?,?");
        ps.setString(1, category);
        ps.setInt(2, offset);
        ps.setInt(3, itemsOnPage);
        ResultSet resultSet = ps.executeQuery();
        return resultSet;
    }

    public static int getSchematicsCountInCategory(String category) throws SQLException {
        PreparedStatement ps = getConnection().prepareStatement("SELECT COUNT(*) FROM schematics WHERE category=?;");
        ps.setString(1, category);
        ResultSet rs = ps.executeQuery();
        rs.next();
        int count = rs.getInt("COUNT(*)");
        return count;
    }

    public static ResultSet getPageSchematicsByName(String name, int page, int itemsOnPage) throws SQLException {
        int offset = page*itemsOnPage;
        PreparedStatement ps = getConnection().prepareStatement("SELECT * FROM `schematics` WHERE MATCH (name) AGAINST(?) LIMIT ?,?");
        ps.setString(1, name);
        ps.setInt(2, offset);
        ps.setInt(3, itemsOnPage);
        ResultSet resultSet = ps.executeQuery();
        return resultSet;
    }

    public static int getSchematicsCountInName(String name) throws SQLException {
        PreparedStatement ps = getConnection().prepareStatement("SELECT COUNT(*) FROM schematics WHERE MATCH (name) AGAINST(?);");
        ps.setString(1, name);
        ResultSet rs = ps.executeQuery();
        rs.next();
        int count = rs.getInt("COUNT(*)");
        return count;
    }

    public static String downloadSchematic(String name) throws SQLException, IOException {
        PreparedStatement ps = getConnection().prepareStatement("SELECT schematicData FROM schematics WHERE name=?;");
        ps.setString(1, name.replace("§9", ""));
        ResultSet rs = ps.executeQuery();
        rs.next();
        if(rs.getBinaryStream("schematicData") == null) {
            return null;
        }
        UUID uuid = UUID.randomUUID();
        File file = new File(uuid.toString() + ".schematic");
        file.createNewFile();
        FileOutputStream output = new FileOutputStream(file);
        InputStream input = rs.getBinaryStream("schematicData");
        byte[] buffer = new byte[1024];
        while (input.read(buffer) > 0) {
            output.write(buffer);
        }
        input.close();
        output.close();

        return uuid.toString();
    }



}

