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

public class BlogEnterService implements Action {
//블로그 들어가는 서비스
//파라미터로 이동할 블로그(blogid)를 받는다.
	@Override
	public ActionForward execute(HttpServletRequest request, HttpServletResponse response) {
		ActionForward forward = new ActionForward();
		
		try {
			BlogDao dao = new BlogDao();
			String blogid = request.getParameter("blogid");
			List<Blog_Board> list = dao.getBoardListById(blogid);
			
			request.setAttribute("id", blogid);
			request.setAttribute("blogboardlist", list);
			
			forward.setPath("블로그.jsp");
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
