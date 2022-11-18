package kr.or.kosa.dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.List;

import javax.naming.Context;
import javax.naming.InitialContext;
import javax.naming.NamingException;
import javax.sql.DataSource;

import kr.or.kosa.dto.Blog_Board;
import kr.or.kosa.dto.Blog_Reply;

public class BlogDao implements BookMarkDao{

	//Blog_Board Board = new Blog_Board();
	//Blog_Reply Reply = new Blog_Reply();
	
	DataSource ds = null;
	
	public BlogDao() throws NamingException {
		Context context = new InitialContext();
		ds = (DataSource)context.lookup("java:comp/env/jdbc/oracle");
	}
	
	//블로그 글 전체 불러오기
	public List<Blog_Board> AllBoard(int cpage , int pagesize){
		List<Blog_Board> boardList = null;
		Connection conn = null;
		PreparedStatement pstmt = null;
		ResultSet rs = null;
		try {
			conn = ds.getConnection();
			String sql= "";
			pstmt = conn.prepareStatement(sql);
			//공식같은 로직
			int start = cpage * pagesize - (pagesize -1); //1 * 5 - (5 - 1) >> 1
			int end = cpage * pagesize; // 1 * 5 >> 5
			
			pstmt.setInt(1, end);
			pstmt.setInt(2, start);
			
			rs = pstmt.executeQuery();
			boardList = new ArrayList<>();
			
			while(rs.next()) {
				Blog_Board board = new Blog_Board();
				board.setBlog_no(rs.getInt("blog_no"));
				board.setBlog_content(rs.getString("blog_content"));
				board.setBlog_date(rs.getDate("blog_date")); //날짜
				board.setBlog_filename(rs.getString("blog_filename"));
				board.setBlog_title(rs.getString("blog_title"));
				board.setHits(rs.getInt("blog_hit"));
				board.setId(rs.getString("id"));
				
				boardList.add(board);
			}
			
		}catch (Exception e) {
			System.out.println("AllBoard 오류 : " + e.getMessage());
		}finally {
			try {
				pstmt.close();
				rs.close();
				conn.close();
			} catch (Exception e2) {
				System.out.println(e2.getMessage());
			}
		}
		
		return boardList;
	}
	
	//게시물 총 건수 구하기
	public int totalBoardCount() {
		Connection conn = null;
		PreparedStatement pstmt = null;
		ResultSet rs = null;
		int totalcount = 0;
		try {
			conn = ds.getConnection(); //연결객체
			String sql = "select count(*) as cnt from blog_board";
			pstmt = conn.prepareStatement(sql);
			rs = pstmt.executeQuery();
			if(rs.next()) {
				totalcount = rs.getInt("cnt");
			}
		} catch (Exception e) {
			System.out.println("totalBoardCount 예외 : " + e.getMessage());
		}
		return totalcount;
	}
	
	
	//블로그 특정 글 조회
	//파일이 있을 경우 조인해서...
	public Blog_Board getContent(int blog_no) {
		Connection conn = null;
		PreparedStatement pstmt = null;
		ResultSet rs = null;
		Blog_Board board = null;
		
		try {
			conn = ds.getConnection();
			String sql = "select a.blog_no, a.id, a.blog_title, a.blog_content, a.hits, a.blog_date, b.file_name "
					+ "from blog_board a left join blogfile b "
					+ "on a.blog_no = b.blog_no "
					+ "where a.blog_no = ?";
			pstmt = conn.prepareStatement(sql);
			pstmt.setInt(1, blog_no);
			
			rs = pstmt.executeQuery();
			
			if(rs.next()) {
				String id = rs.getString("id");
				String blog_title = rs.getString("blog_title");
				String blog_content = rs.getString("blog_content");
				int hits = rs.getInt("hits");
				java.sql.Date blog_date = rs.getDate("blog_date");
				String file_name = rs.getString("file_name");
				
				board = new Blog_Board(blog_no, id, blog_title, blog_content, hits, blog_date, file_name);
			}	
		} catch (Exception e) {
			System.out.println("getContent 예외 : " + e.getMessage());
		}finally {
			try {
				pstmt.close();
				rs.close();
				conn.close(); //반환하기
			} catch (Exception e2) {
				// TODO: handle exception
			}
		}
		return board;
	}
	
	//글 작성
	
	//글 수정
	
	//글 삭제
	
	//특정 글 댓글 불러오기
	public List<Blog_Reply> getReply(String blog_no) {
		Connection conn = null;
		PreparedStatement pstmt = null;
		ResultSet rs = null;
		ArrayList<Blog_Reply> replylist = null;
		
		try {
			conn = ds.getConnection();
			String sql = "select blog_reply_no, id, refer, depth, step, reply_date, reply_content, del "
					+ "from blog_reply where blog_no = ?";
			
			pstmt = conn.prepareStatement(sql);
			pstmt.setString(1, blog_no);
			//왜 blogDto에선 blog_no가 int인데 여기선 String이지 .. ??
			
			rs = pstmt.executeQuery();
			replylist = new ArrayList<>();
			
			while(rs.next()) {
				int blog_reply_no = rs.getInt("blog_reply_no");
				String id = rs.getString("id");
				int refer = rs.getInt("refer");
				int depth = rs.getInt("depth");
				int step = rs.getInt("step");
				java.sql.Date reply_date = rs.getDate("reply_date");
				String reply_content = rs.getString("reply_content");
				int del = rs.getInt("del");
				
				Blog_Reply reply = new Blog_Reply(blog_reply_no, blog_no, id, reply_date, reply_content, refer, depth, step, del);
				replylist.add(reply);
			}
			
		} catch (Exception e) {
			System.out.println("getReply 예외 : " + e.getMessage());
		}finally {
			try {
				pstmt.close();
				rs.close();
				conn.close();
			} catch (Exception e2) {
				
			}
		}
		
		return replylist;
	}
	
	//댓글 작성
	
	
	//댓글 수정
	
	//댓글 삭제
	
}
