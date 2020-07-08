package RSSFeed;

/** 
 * Contains the following information about posts:<br>
 * author name<br>
 * author title<br>
 * author avatar url<br>
 * post title<br>
 * post page url<br>
 * post message
 * */
public class ForumPost {
	/** The post's author */
	String authorName;
	/** The author's title */
	String authorTitle;
	/** The author's avatar */
	String authorAvatarUrl;
	/** The thread's title */
	String threadTitle;
	/** Url of the thread's page where the comment is */
	String url;
	/** Post message */
	String message;
	
	/**
	 * @param authorName the post's author
	 * @param authorTitle the author's title
	 * @param authorAvatarUrl the author's avatar - HTTP or HTTPS format
	 * @param threadTitle the thread's title
	 * @param url url of the thread's page where the comment is - HTTP or HTTPS format
	 * @param message post message
	 */
	ForumPost(String authorName, String authorTitle, String authorAvatarUrl, String threadTitle, String url, String message) {
		this.authorName = authorName;
		this.authorTitle = authorTitle;
		this.authorAvatarUrl = authorAvatarUrl;
		this.threadTitle = threadTitle;
		this.url = url;
		this.message = message;
	}
}
