package com.kh.miniservlet.dao;

import com.kh.miniservlet.common.Common;
import com.kh.miniservlet.vo.BoardVO;
import com.kh.miniservlet.vo.MemberVO;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;


public class BoardDAO {

    private Connection conn = null;
    private Statement stmt = null;
    private ResultSet rs = null;
    private PreparedStatement pstmt = null;

    /*
     *   List<MemberVO>
     */

    public List<BoardVO> boardSelect(String reqDocNum) {

        List<BoardVO> list = new ArrayList<>();
        try {
            conn = Common.getConnection();
            stmt = conn.createStatement();
            String sql = null; // sql문을 생성하고 들어오는 인자값 (reqId)에 따라
            if(reqDocNum.equals("ALL")) sql = "SELECT * FROM BOARD ORDER BY DOC_NUM DESC";
            else if(reqDocNum.equals("자유게시판")) sql = "SELECT * FROM BOARD WHERE CATEGORY = 0 ORDER BY DOC_NUM DESC" ;
            else if (reqDocNum.equals("후기게시판")) sql = "SELECT * FROM BOARD WHERE CATEGORY = 1 ORDER BY DOC_NUM DESC" ;
            else sql = "SELECT * FROM BOARD WHERE DOC_NUM = " + reqDocNum;
            rs = stmt.executeQuery(sql);

            while(rs.next()) {

                Integer docNum = rs.getInt("DOC_NUM");
                Integer category = rs.getInt("CATEGORY");
                String title = rs.getString("TITLE");
                String content = rs.getString("CONTENT");
                String id = rs.getString("ID");
                Date writeDate = rs.getDate("WRITE_DATE");

                BoardVO vo = new BoardVO();

                vo.setBoardNum(docNum);
                vo.setCategory(category);
                vo.setTitle(title);
                vo.setBoardContent(content);
                vo.setId(id);
                vo.setBoardDate(writeDate);
                list.add(vo);
            }
            Common.close(rs);
            Common.close(stmt);
            Common.close(conn);
        } catch (Exception e) {
            e.printStackTrace();
        }

        return list;
    }

    // 현재 가장 큰 글 고유넘버 가져오기
    public Integer biggestBoardNum() {
        System.out.println("biggestBoardNum 실행");


        Integer docNum = null;
        try {
            conn = Common.getConnection();
            stmt = conn.createStatement();

            // 내가 보낼 sql문
            String sql = "SELECT DOC_NUM  FROM (SELECT * FROM BOARD ORDER BY DOC_NUM DESC) WHERE ROWNUM = 1";
            rs = stmt.executeQuery(sql);

            while (rs.next()) {
                docNum = rs.getInt("DOC_NUM");
            }

            Common.close(rs);
            Common.close(stmt);
            Common.close(conn);

        } catch (Exception e) {
            e.printStackTrace();
        }

        return docNum;
    }


// 글쓰기 등록 기능
// 글쓰기 성공시 1리턴, 실패시 0리

    public boolean boardRegister( String category, String title, String content, String id) {
        int result = 0;
        String sql = "INSERT INTO BOARD VALUES(?, ?, ?, ?, ?,SYSDATE)";
        try {


            // 현재의 가장 큰 boardNum을 가져온다
            BoardDAO dao = new BoardDAO();
            Integer nowBiggestBoardNum = dao.biggestBoardNum();
            // String 으로 받아온 값을 Int형으로 변경해준다
            int newCategory = Integer.parseInt(category);

            conn = Common.getConnection();
            pstmt = conn.prepareStatement(sql);
            pstmt.setInt(1, nowBiggestBoardNum + 1);
            pstmt.setInt(2, newCategory);
            pstmt.setString(3, title);
            pstmt.setString(4, content);
            pstmt.setString(5, id);
            result = pstmt.executeUpdate();
            System.out.println("글쓰기 DB 결과 확인 : " + result);

        } catch (Exception e) {
            e.printStackTrace();
        }
        Common.close(rs);
        Common.close(pstmt);
        Common.close(conn);

        if(result == 1) return true;
        else return false;
    }

    // 작성자가 누군지 확인 (boardNum 입력 -> id 출력)
    public String whoWriteBoard(String docNum) {
        String id = null;
        try {
            conn = Common.getConnection();
            stmt = conn.createStatement();

            // 내가 보낼 sql문
            String sql = "SELECT ID FROM BOARD WHERE DOC_NUM = " +docNum;
            rs = stmt.executeQuery(sql);

            while (rs.next()) {
                id = rs.getString("ID");
            }

            Common.close(rs);
            Common.close(stmt);
            Common.close(conn);

        } catch (Exception e) {
            e.printStackTrace();
        }

        return id;
    }


    // 글 삭제
// 삭제 성공시 1, 실패시 0 반환
    public boolean boardDelete(Integer docNum) {
        int result = 0;
        String sql = "DELETE FROM BOARD WHERE DOC_NUM = ? ";


        try {
            conn = Common.getConnection();
            pstmt = conn.prepareStatement(sql);
            pstmt.setInt(1, docNum);
            result = pstmt.executeUpdate();
        } catch(Exception e) {
            e.printStackTrace();
        }
        Common.close(pstmt);
        Common.close(conn);
        if(result == 1) return true;
        else return false;
    }
}
