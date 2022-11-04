package com.kh.miniservlet.servlet.board;

import java.io.IOException;
import java.io.PrintWriter;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.List;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.kh.miniservlet.common.Common;
import com.kh.miniservlet.dao.BoardDAO;
import com.kh.miniservlet.vo.BoardVO;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;


/**
 * Servlet implementation class boardListServlet
 */
@RestController
@CrossOrigin(origins = "http://localhost:3000")
public class BoardListServlet extends HttpServlet {
    @PostMapping("/BoardListServlet")
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {

        request.setCharacterEncoding("utf-8");
        response.setCharacterEncoding("utf-8");

        StringBuffer sb = Common.reqStringBuff(request);
        JSONObject jsonObj = Common.getJsonObj(sb);

        String reqCmd = (String)jsonObj.get("cmd");
        System.out.println("cmd : " + reqCmd);
        PrintWriter out = response.getWriter();

        // 잘못 받아왔을 경우
        if(!reqCmd.equals("BoardInfo")) {
            JSONObject resJson = new JSONObject();
            resJson.put("result", "NOK");
            out.print(resJson);
            return;
        }


        BoardDAO dao = new BoardDAO();
        List<BoardVO> list = dao.boardSelect();

        // 제이슨 형식 어레이 생성
        JSONArray boardArray = new JSONArray();
        for (BoardVO e : list) {
            // 자바 객체 생성
            JSONObject boardInfo = new JSONObject();
            boardInfo.put("boardNum", e.getBoardNum());
            boardInfo.put("category", e.getCategory());
            boardInfo.put("title", e.getTitle());
            boardInfo.put("boardContent", e.getBoardContent());
            boardInfo.put("id", e.getId());
            DateFormat dateFormat = new SimpleDateFormat("YYYY/dd/MM HH:mm:ss");
            String dateToStr = dateFormat.format(e.getBoardDate());
            boardInfo.put("boardDate", dateToStr);
            boardArray.add(boardInfo);
        }
        System.out.println(boardArray);
        out.print(boardArray);

    }


}