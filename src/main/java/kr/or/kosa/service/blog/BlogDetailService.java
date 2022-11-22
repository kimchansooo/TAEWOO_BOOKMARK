package kr.or.kosa.service.blog;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import kr.or.kosa.action.Action;
import kr.or.kosa.action.ActionForward;
import kr.or.kosa.dao.BlogDao;
import kr.or.kosa.dto.Blog_Board;

public class BlogDetailService implements Action {
//블로그 게시글 상세보기
//파라미터로 블로그게시글번호(blogno)를 받는다.
	@Override
	public ActionForward execute(HttpServletRequest request, HttpServletResponse response) {
		ActionForward forward = new ActionForward();
		Blog_Board blog = null;
		try {
			BlogDao dao = new BlogDao();
			int blogno = Integer.parseInt(request.getParameter("blogno"));
			
			blog = dao.getContent(blogno);
			
			if(blog != null) {
				dao.upHits(blogno);
				request.setAttribute("content", blog);
			}
			forward.setPath("블로그상세보기.jsp");
			forward.setRedirect(false);
		} catch (Exception e) {
			String msg  = "서버 오류 발생";
			String path = "main.do";
		} 
		return forward;
	}

}
