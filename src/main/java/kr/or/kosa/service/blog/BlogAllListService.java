package kr.or.kosa.service.blog;

import java.util.ArrayList;
import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import kr.or.kosa.action.Action;
import kr.or.kosa.action.ActionForward;
import kr.or.kosa.dao.BlogDao;
import kr.or.kosa.dao.BookMarkDao;
import kr.or.kosa.dto.Blog_Board;
import kr.or.kosa.utils.DaoFactory;

public class BlogAllListService implements Action {
//블로그 전체 조회 담고 관리자 페이지로
	@Override
	public ActionForward execute(HttpServletRequest request, HttpServletResponse response) {
		ActionForward forward = new ActionForward();
		
		try {
			BlogDao dao = new BlogDao();
			List<Blog_Board> list = dao.AllBoard();
			
			request.setAttribute("blogboardlist", list);
			
			forward.setPath("관리자블로그게시글조회.do");
			forward.setRedirect(false);
		} catch (Exception e) {
			e.printStackTrace();
			request.setAttribute("msg", "에러가 발생했습니다.");
			request.setAttribute("url", "main.do");
			forward.setPath("에러페이지");
			forward.setRedirect(true);
		}
		return forward;
	}

}
