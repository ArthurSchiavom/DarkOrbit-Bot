package database;

import java.util.List;

public class FeedTable implements Table {

	@Override
	public void createTable() {
		
	}
	
	public void addThread(String url, String title, int amountOfComments) {
		
	}
	
	public List<RSSFeed.ForumThread> getThreads() {
		
		return null;
	}
	
	/**
	 * Changes the amount of comments of the row whose url is the specified
	 * @param url the url of the thread whose amount of comments changed
	 * @param newAmountOfComments the current amount of comments in that thread
	 */
	public void modifyThread(String url, String newAmountOfComments) {
		
	}
	
	// Used to delete, from the database, a thread that has been removed from the forum.
	/**
	 * Deletes the thread of the given url from the database
	 * @param url
	 */
	public void deleteThread(String url) {
		
	}
}
