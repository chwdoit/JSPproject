package bbs;

public class Bbs {
    private int bbsID;          // 게시물의 고유 번호
    private String bbsTitle;    // 게시물의 제목
    private String userID;      // 게시물을 작성한 사용자의 ID
    private String bbsDate;     // 게시물 작성일시 (형식: 'YYYY-MM-DD HH:MM:SS')
    private String bbsContent;  // 게시물 내용
    private int bbsAvailable;   // 게시물의 삭제 여부 (1: 삭제되지 않음, 0: 삭제됨)
    private int rowNum;         // 테이블에서의 순번
    
	public int getBbsID() {
		return bbsID;
	}
	public void setBbsID(int bbsID) {
		this.bbsID = bbsID;
	}
	public String getBbsTitle() {
		return bbsTitle;
	}
	public void setBbsTitle(String bbsTitle) {
		this.bbsTitle = bbsTitle;
	}
	public String getUserID() {
		return userID;
	}
	public void setUserID(String userID) {
		this.userID = userID;
	}
	public String getBbsDate() {
		return bbsDate;
	}
	public void setBbsDate(String bbsDate) {
		this.bbsDate = bbsDate;
	}
	public String getBbsContent() {
		return bbsContent;
	}
	public void setBbsContent(String bbsContent) {
		this.bbsContent = bbsContent;
	}
	public int getBbsAvailable() {
		return bbsAvailable;
	}
	public void setBbsAvailable(int bbsAvailable) {
		this.bbsAvailable = bbsAvailable;
	}
	public int getRowNum() {
		return rowNum;
	}
	public void setRowNum(int rowNum) {
		this.rowNum = rowNum;
	}

}
