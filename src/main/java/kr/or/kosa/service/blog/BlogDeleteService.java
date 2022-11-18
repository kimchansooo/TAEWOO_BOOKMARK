package kr.or.kosa.service.blog;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import kr.or.kosa.action.Action;
import kr.or.kosa.action.ActionForward;

public class BlogDeleteService implements Action {

	@Override
	public ActionForward execute(HttpServletRequest request, HttpServletResponse response) {
		ActionForward forward = new ActionForward();
		
		try {
			
		} catch (Exception e) {
			
		} 
		//팝업 보여주고 다른 페이지로
		request.setAttribute("msg","");
		request.setAttribute("url", "");
		return forward;
	}

}
