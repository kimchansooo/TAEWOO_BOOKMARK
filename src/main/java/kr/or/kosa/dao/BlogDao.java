package kr.or.kosa.dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Date;
import java.util.ArrayList;
import java.util.List;

import javax.naming.Context;
import javax.naming.InitialContext;
import javax.naming.NamingException;
import javax.servlet.http.HttpServletRequest;
import javax.sql.DataSource;

import kr.or.kosa.dto.Blog_Board;
import kr.or.kosa.dto.Blog_Reply;

public class BlogDao implements BookMarkDao{

	//autoCommit 어떻게 해야되는지?
	
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
	
	//게시글 조회수 증가
	public boolean upHits(int blog_no) {
		boolean result = false;
		
		Connection conn = null;
		PreparedStatement pstmt = null;
		
		try {
			conn = ds.getConnection();
			String sql = "update blog_board set hits = hits + 1"
					+ " where blog_no = ?";
			pstmt = conn.prepareStatement(sql);
			pstmt.setInt(1, blog_no);
			
			int resultrow = pstmt.executeUpdate();
			if(resultrow > 0) {
				result = true;
			}
			
		} catch (Exception e) {
			System.out.println("upHits 예외 : " + e.getMessage());
		}finally {
			try {
				pstmt.close();
				conn.close();
			} catch (Exception e2) {
				
			}
		}
		
		return result;
	}
	
	//글 작성
	public int writeok(Blog_Board board) {
		Connection conn = null;
		PreparedStatement pstmt = null;
		int row = 0;
		
		try {
			conn = ds.getConnection();
			String boardsql = "insert into"
					+ " blog_board(blog_no, id, blog_title, blog_content, hits)"
					+ " values(?,?,?,?,?)";
			pstmt = conn.prepareStatement(boardsql);
			pstmt.setInt(1, board.getBlog_no());
			pstmt.setString(2, board.getId());
			pstmt.setString(3, board.getBlog_title());
			pstmt.setString(4, board.getBlog_content());
			pstmt.setInt(5, board.getHits());
			row = pstmt.executeUpdate();
			//파일이 있다면 /////////////////////////
			if(board.getBlog_filename() != null) {
				pstmt.close();
				String filesql = "insert into blogfile(blog_no, file_name) values(?,?)";
				pstmt = conn.prepareStatement(filesql);
				pstmt.setInt(1, board.getBlog_no());
				pstmt.setString(2, board.getBlog_filename());
				pstmt.execute();
			}
		} catch (Exception e) {
			System.out.println("writeok 예외 : " + e.getMessage());
		}finally {
			try {
				pstmt.close();
				conn.close();
			} catch (Exception e2) {
				// TODO: handle exception
			}
		}
		
		return row;
	}
	
	//글 수정
	public int blogEdit(HttpServletRequest board) {
		int row = 0;
		
		String blog_no = board.getParameter("blog_no");
		//String id = board.getParameter("id");
		String blog_title = board.getParameter("blog_title");
		String blog_content = board.getParameter("blog_content");
		
		Connection conn = null;
		PreparedStatement pstmt = null;
		
		try {
			conn = ds.getConnection();
			String sql = "update blog_board set blog_title=?, blog_content=? "
					+ "where blog_no=?";
			pstmt = conn.prepareStatement(sql);
			pstmt.setString(1, blog_title);
			pstmt.setString(2, blog_content);
			pstmt.setInt(3, Integer.parseInt(blog_no));
			
			row = pstmt.executeUpdate();
		} catch (Exception e) {
			System.out.println("blogEdit 예외 : " + e.getMessage());
		}finally {
			try {
				pstmt.close();
				conn.close();
			} catch (Exception e2) {
				// TODO: handle exception
			}
		}
		
		return row;
	}
	
	//글 삭제
	public int deleteOk(int blog_no) {
		int row = 0;
		
		Connection conn = null;
		PreparedStatement pstmt = null;
		
		try {
			conn = ds.getConnection();
			String sql = "delete from blog_board where blog_no = ?";
			pstmt.setInt(1, blog_no);
			row = pstmt.executeUpdate();
		} catch (Exception e) {
			System.out.println("deleteOk 예외 : " + e.getMessage());
		}
		
		return row;
	}
	
	//특정 글 댓글 불러오기
	public List<Blog_Reply> getReply(int blog_no) {
		Connection conn = null;
		PreparedStatement pstmt = null;
		ResultSet rs = null;
		ArrayList<Blog_Reply> replylist = null;
		
		try {
			conn = ds.getConnection();
			String sql = "select blog_reply_no, id, refer, depth, step, reply_date, reply_content, del "
					+ "from blog_reply where blog_no = ?";
			
			pstmt = conn.prepareStatement(sql);
			pstmt.setInt(1, blog_no);
			//왜 blogDto에선 blog_no가 int인데 여기선 String이지 .. ??
			//실수였대~int로 바꿨삼 221118 19:04
			
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
	public int replyWrite(int blog_reply_no, int blog_no, String id, String content){
		Connection conn = null;
		PreparedStatement pstmt = null;
		int resultrow = 0;
		int maxrefer = getMaxRefer();
		int refer = maxrefer + 1;
		//blog_reply_no를 지금은 받아주고 나중엔 안받아줘도 된다 ? ?
		//원댓글 쓸 땐 blog_reply_no = refer
		try {
			conn = ds.getConnection();
			String sql = "insert into blog_reply(blog_reply_no, blog_no, id, refer, reply_content, del)"
					+ " values(?,?,?,?,?)";
			pstmt = conn.prepareStatement(sql);
			
			pstmt.setInt(1, blog_reply_no);
			pstmt.setInt(2, blog_no);
			pstmt.setString(3, id);
			pstmt.setInt(4, maxrefer);
			pstmt.setString(5, content);
			pstmt.setInt(6, 0); //del
			
			resultrow = pstmt.executeUpdate();
			
		} catch (Exception e) {
			System.out.println("replyWrite 예외 : " + e.getMessage());
		}finally {
			try {
				pstmt.close();
				conn.close();
			} catch (Exception e2) {
				// TODO: handle exception
			}
		}
		
		return resultrow;
	}
	
	//댓글 refer 값 생성하기 ?
	private int getMaxRefer() {
		Connection conn = null;
		PreparedStatement pstmt = null;
		ResultSet rs = null;
		int refer_max = 0;
		
		try {
			conn = ds.getConnection();
			String sql = "select nvl(max(refer), 0) from blog_reply";
			pstmt = conn.prepareStatement(sql);
			rs = pstmt.executeQuery();
			if(rs.next()) {
				refer_max = rs.getInt(1);
			}
		} catch (Exception e) {
			System.out.println("getMaxRefer 예외 : " + e.getMessage());
		}finally {
			try {
				pstmt.close();
				rs.close();
				conn.close();
			}catch (Exception e) {
				// TODO: handle exception
			}
		}
		
		return refer_max;
	}
	//계층형 게시판
	//refer(참조값) , step , depth
	//1. 원본글 : refer 생성?  , step(0) default , depth(0) default
	//2. 답변글 : refer 생성?  , step +1 , depth +1, 현재 읽은 글에 depth + 1
	
	//대댓글 작성
	public int replyRewrite(Blog_Reply reply) {
		int result = 0;
		Connection conn = null;
		PreparedStatement pstmt = null;
		
		
		return result;
	}
	
	
	
	//댓글 수정
	public int replyEdit(HttpServletRequest reply) {
		int row = 0;
		String blog_reply_no = reply.getParameter("blog_reply_no");
		String reply_content = reply.getParameter("reply_content");
		
		Connection conn = null;
		PreparedStatement pstmt = null;
		
		try {
			conn = ds.getConnection();
			String sql = "update blog_reply set reply_content=? where blog_reply_no=?";
			pstmt = conn.prepareStatement(sql);
			pstmt.setString(1, reply_content);
			pstmt.setInt(2, Integer.parseInt(blog_reply_no));
			
			row = pstmt.executeUpdate();
		} catch (Exception e) {
			System.out.println("replyEdit 예외 : " + e.getMessage());
		} finally {
			try {
				pstmt.close();
				conn.close();
			} catch (Exception e2) {
				// TODO: handle exception
			}
		}
		
		return row;
	}
	
	//댓글 삭제
	public int replyDelete(int blog_reply_no) {
		int result = 0;
		
		Connection conn = null;
		PreparedStatement pstmt = null;

		try {
			conn = ds.getConnection();
			//String sql = "delete from blog_reply where blog_reply_no = ?";
			String sql = "update blog_reply set del = 1 where blog_reply_no = ?";
			pstmt.setInt(1, blog_reply_no);
			result = pstmt.executeUpdate();
			
		} catch (Exception e) {
			System.out.println("replyDelete 예외 : " + e.getMessage());
		}finally {
			try {
				pstmt.close();
				conn.close();
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		
		return result;
	}
	
}
