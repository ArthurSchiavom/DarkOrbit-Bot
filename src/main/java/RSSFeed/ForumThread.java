package RSSFeed;

public class ForumThread {
	String title = "";
	int amountOfComments = 0;
	
	/** 
	 * @param title the thread title
	 * @param amountOfComments the amount of comments on that thread (= number of posts-1)
	 * */
	ForumThread(String title, int amountOfComments) {
		this.title = title;
		this.amountOfComments = amountOfComments;
	}
}
