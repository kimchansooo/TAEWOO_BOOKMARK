package kr.or.kosa.dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;

import javax.naming.spi.DirStateFactory.Result;

import kr.or.kosa.utils.ConnectionHelper;

public class CalendarDao implements BookMarkDao{

	private Connection conn;
	private PreparedStatement pstmt;
	private ResultSet rs;
	private String sql;
	 
	public CalendarDao() {
		conn = ConnectionHelper.getConnection("oracle");
		pstmt = null;
		rs = null;
		sql = "";
		
	}
}
