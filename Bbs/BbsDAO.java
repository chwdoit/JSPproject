package bbs;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;

public class BbsDAO {
    private Connection conn;
    private ResultSet rs;

    public BbsDAO() {
        try {
            String dbURL = "jdbc:oracle:thin:@localhost:1521:xe";
            String dbID = "WEBMASTER";
            String dbPassword = "webmaster";
            Class.forName("oracle.jdbc.driver.OracleDriver");
            conn = DriverManager.getConnection(dbURL, dbID, dbPassword);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public String getDate() {
        String SQL = "SELECT TO_CHAR(SYSDATE, 'YYYY-MM-DD HH24:MI:SS') FROM DUAL";
        try {
            PreparedStatement pstmt = conn.prepareStatement(SQL);
            rs = pstmt.executeQuery();
            if (rs.next()) {
                return rs.getString(1);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return "";
    }

    public int getNext() {
        String SQL = "SELECT bbsID FROM BBS ORDER BY bbsID DESC";
        try {
            PreparedStatement pstmt = conn.prepareStatement(SQL);
            rs = pstmt.executeQuery();
            if (rs.next()) {
                return rs.getInt(1) + 1;
            }
            return 1; // 첫번째 게시물인 경우
        } catch (Exception e) {
            e.printStackTrace();
        }
        return -1;
    }

    public int write(String bbsTitle, String userID, String bbsContent) {
        String SQL = "INSERT INTO BBS (bbsID, bbsTitle, userID, bbsDate, bbsContent, bbsAvailable) VALUES (?, ?, ?, TO_DATE(?, 'YYYY-MM-DD HH24:MI:SS'), ?, ?)";
        try {
            PreparedStatement pstmt = conn.prepareStatement(SQL);
            pstmt.setInt(1, getNext());
            pstmt.setString(2, bbsTitle);
            pstmt.setString(3, userID);
            pstmt.setString(4, getDate());
            pstmt.setString(5, bbsContent);
            pstmt.setInt(6, 1);
            int result = pstmt.executeUpdate();
            System.out.println("SQL: " + pstmt.toString()); // SQL 구문 로그 출력
            return result;
        } catch (Exception e) {
            e.printStackTrace();
            System.out.println("Error during write operation: " + e.getMessage()); // 에러 메시지 출력
        }
        return -1;
    }

    public ArrayList<Bbs> getList(int pageNumber) {
        String SQL = "SELECT * FROM (SELECT bbsID, bbsTitle, userID, bbsDate, bbsAvailable, ROWNUM AS rn FROM (SELECT * FROM BBS WHERE bbsAvailable = 1 ORDER BY bbsID DESC)) WHERE rn BETWEEN ? AND ?";
        ArrayList<Bbs> list = new ArrayList<Bbs>();
        try {
            PreparedStatement pstmt = conn.prepareStatement(SQL);
            pstmt.setInt(1, (pageNumber - 1) * 10 + 1);
            pstmt.setInt(2, pageNumber * 10);
            rs = pstmt.executeQuery();
            while (rs.next()) {
                Bbs bbs = new Bbs();
                bbs.setBbsID(rs.getInt("bbsID"));
                bbs.setBbsTitle(rs.getString("bbsTitle"));
                bbs.setUserID(rs.getString("userID"));
                bbs.setBbsDate(rs.getString("bbsDate"));
                bbs.setBbsAvailable(rs.getInt("bbsAvailable"));
                list.add(bbs);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return list;
    }

    public boolean nextPage(int pageNumber) {
        String SQL = "SELECT COUNT(*) FROM BBS WHERE bbsID < ? AND bbsAvailable = 1";
        try {
            PreparedStatement pstmt = conn.prepareStatement(SQL);
            pstmt.setInt(1, getNext() - (pageNumber - 1) * 10);
            rs = pstmt.executeQuery();
            if (rs.next()) {
                return rs.getInt(1) > 0;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }

    public Bbs getBbs(int bbsID) {
        String SQL = "SELECT * FROM BBS WHERE bbsID = ?";
        try {
            PreparedStatement pstmt = conn.prepareStatement(SQL);
            pstmt.setInt(1, bbsID);
            rs = pstmt.executeQuery();
            if (rs.next()) {
                Bbs bbs = new Bbs();
                bbs.setBbsID(rs.getInt(1));
                bbs.setBbsTitle(rs.getString(2));
                bbs.setUserID(rs.getString(3));
                bbs.setBbsDate(rs.getString(4));
                bbs.setBbsContent(rs.getString(5));
                bbs.setBbsAvailable(rs.getInt(6));
                return bbs;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    public int update(int bbsID, String bbsTitle, String bbsContent) {
        String SQL = "UPDATE BBS SET bbsTitle = ?, bbsContent = ? WHERE bbsID = ?";
        try {
            PreparedStatement pstmt = conn.prepareStatement(SQL);
            pstmt.setString(1, bbsTitle);
            pstmt.setString(2, bbsContent);
            pstmt.setInt(3, bbsID);
            return pstmt.executeUpdate();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return -1; // 데이터베이스 오류
    }

    public int delete(int bbsID) {
        String SQL = "UPDATE BBS SET bbsAvailable = 0 WHERE bbsID = ?";
        try {
            PreparedStatement pstmt = conn.prepareStatement(SQL);
            pstmt.setInt(1, bbsID);
            int result = pstmt.executeUpdate();
            reorderBbsIDs(); // 번호 재정렬 메서드 호출
            return result;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return -1;
    }

    public ArrayList<Bbs> getNumberedList(int pageNumber) {
        String SQL = "SELECT ROWNUM, bbsID, bbsTitle, userID, bbsDate FROM (SELECT * FROM BBS WHERE bbsAvailable = 1 ORDER BY bbsID DESC) WHERE ROWNUM <= ?";
        ArrayList<Bbs> list = new ArrayList<Bbs>();
        try {
            PreparedStatement pstmt = conn.prepareStatement(SQL);
            pstmt.setInt(1, getNext() - (pageNumber - 1) * 10);
            rs = pstmt.executeQuery();
            while (rs.next()) {
                Bbs bbs = new Bbs();
                bbs.setRowNum(rs.getInt(1));
                bbs.setBbsID(rs.getInt(2));
                bbs.setBbsTitle(rs.getString(3));
                bbs.setUserID(rs.getString(4));
                bbs.setBbsDate(rs.getString(5));
                list.add(bbs);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return list;
    }

    public void reorderBbsIDs() {
        String SQL = "SELECT bbsID FROM BBS WHERE bbsAvailable = 1 ORDER BY bbsID ASC";
        ArrayList<Integer> ids = new ArrayList<>();
        try {
            PreparedStatement pstmt = conn.prepareStatement(SQL);
            rs = pstmt.executeQuery();
            while (rs.next()) {
                ids.add(rs.getInt("bbsID"));
            }
            pstmt.close();

            for (int i = 0; i < ids.size(); i++) {
                SQL = "UPDATE BBS SET bbsID = ? WHERE bbsID = ?";
                pstmt = conn.prepareStatement(SQL);
                pstmt.setInt(1, i + 1);
                pstmt.setInt(2, ids.get(i));
                pstmt.executeUpdate();
                pstmt.close();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
