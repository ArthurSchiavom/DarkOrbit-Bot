package RSSFeed;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URL;
import java.net.URLConnection;
import java.util.HashMap;
import java.util.Map;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.select.Elements;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

import main.Ref;
import net.dv8tion.jda.core.EmbedBuilder;
import net.dv8tion.jda.core.JDA;
import net.dv8tion.jda.core.entities.MessageEmbed;


public class RSSFeedReader {
	// ~_-_-_-_-_-_-_~     RL = Recently Loaded     ~_-_-_-_-_-_-_~
	
	
	/** 
	 * @String the thread url
	 * @ForumThread object containing the post title and amount of comments
	 *  */
	private Map<String, ForumThread> threadsList = new HashMap<String, ForumThread>();
	
	/** 
	 * @param url the thread base url (first page)
	 * @Return true if the thread was previously loaded (= if it's not a new thread) 
	 * */
	private boolean threadIsListed(String url) {
		return threadsList.containsKey(url);
	}

	// ~_-_-_-_-_-_-_~     RL = Recently Loaded     ~_-_-_-_-_-_-_~
	/** 
	 * @param RLUrl the thread url
	 * @param RLThreadAmountOfComments the current amount of comments on the thread
	 * @return true if the amount of comments of the thread with the url given has changed 
	 * */
	private boolean threadAmountOfCommentsChanged(String RLUrl, int RLThreadAmountOfComments) {
		if(threadsList.get(RLUrl).amountOfComments == RLThreadAmountOfComments)
			return false;
		else
			return true;
	}
	
	/**
	 * Sets the amountOfComments of a given thread
	 * @url the thread's url
	 * @newValue the amount of comments
	 *  */
	private void setListedThreadAmountOfComments(String url, int newValue) {
		threadsList.get(url).amountOfComments = newValue;
	}
	
	/**
	 * @param url the thread's url
	 * @return the saved amountOfComments present in the thread of the given url
	 * */
	private int getListedThreadAmountOfComments(String url) {
		return threadsList.get(url).amountOfComments;
	}
	
	/**Lists(=register) a new thread with the given url and ForumThread object
	 * @url the url of the thread
	 * @thread the ForumThread object representing the thread information
	 * */
	private void listThread(String url, ForumThread thread) {
		threadsList.put(url, thread);
	}
	
	
	
	// ~_-_-_-_-_-_-_~     RL = Recently Loaded     ~_-_-_-_-_-_-_~
	/** Checks if there are new threads and/or comments and informs users by sending a message to the discord channel set on config.txt */
	void checkForNewPosts() {
		org.w3c.dom.Document parsedXml = null;
		for (String RSSFeedUrl : Ref.RSSFeeds.getFeedsUrls()) {
			parsedXml = getParsedWebpageXml(RSSFeedUrl);
			if (parsedXml != null) {
				// url , nComments+title
				HashMap<String, ForumThread> RLForumThreads = getForumThreads(parsedXml);
				for (String RLThreadUrl : RLForumThreads.keySet()) {
					ForumThread RLThread = RLForumThreads.get(RLThreadUrl);
					// System.out.println("1");
					if (threadIsListed(RLThreadUrl)) {
						if (threadAmountOfCommentsChanged(RLThreadUrl, RLThread.amountOfComments)) {
							// If a comment was added
							if (getListedThreadAmountOfComments(RLThreadUrl) < RLThread.amountOfComments) {
								// +2 to count with the initial post and to ignore the last listed post; +1 to
								// count with the initial post
								for (int postNumber = getListedThreadAmountOfComments(RLThreadUrl) + 2; postNumber <= RLThread.amountOfComments + 1; postNumber++) {
									try {
										informUsersOfNewPost(RSSFeedUrl, getForumPost(RLThreadUrl, RLThread.title, postNumber), false);
									} catch (Exception e) {
										main.ErrorMessenger.inform(
												"Error informing users of new post, thread of url: <" + RLThreadUrl + ">");
										e.printStackTrace();
									}
	
								}
							}
	
							// Update it independently of whether a comment was added or deleted (after the verification using the old value is done)
							setListedThreadAmountOfComments(RLThreadUrl, RLThread.amountOfComments);
						}
	
					} else {
						listThread(RLThreadUrl, RLThread);
						informUsersOfNewPost(RSSFeedUrl, getForumPost(RLThreadUrl, RLThread.title, 1), true);
						// informUsersOfNewPost(getForumPost(RLThreadUrl, RLThread.title, 1), true);
						if (RLThread.amountOfComments != 0)
							for (int postNumber = 2; postNumber <= RLThread.amountOfComments + 1; postNumber++)
								informUsersOfNewPost(RSSFeedUrl, getForumPost(RLThreadUrl, RLThread.title, postNumber), false);
						// informUsersOfNewPost(getForumPost(RLThreadUrl, RLThread.title, postNumber),
						// false);
					}
				}
			}
		}
	}
	
	/**
	 * Informs users that there is a new comment or thread
	 * @param forumPost the ForumPost object containing the post information
	 * @param isNewThread if true, will display "New thread" instead of "New comment"*/
	public void informUsersOfNewPost(String RSSFeedUrl, ForumPost forumPost, boolean isNewThread) {
		EmbedBuilder eb = new EmbedBuilder();

		eb.setAuthor(forumPost.authorName + " (" + forumPost.authorTitle + ")", 
				forumPost.url, forumPost.authorAvatarUrl);
		eb.setThumbnail(getThumbnailUrl(RSSFeedUrl));
		
		if (isNewThread)
			eb.setTitle(forumPost.threadTitle + " - New thread", forumPost.url);
		else
			eb.setTitle(forumPost.threadTitle + " - New comment", forumPost.url);
		
		String message = forumPost.message;
		if (message.length() > 1800) {
			message = message.substring(0, 1800).concat("[...]\n\n[Check the [page](" + forumPost.url + ") to read all]");
		}
		eb.appendDescription(message);
			
		eb.setFooter(getGameName(RSSFeedUrl) + " forum", Ref.feedEmbedFooterImageUrl);
		eb.setColor(Ref.feedEmbedColor);
		JDA api = main.Main.botAPI;
		MessageEmbed embed = eb.build();
		for (long ID : Ref.RSSFeeds.getChannels(RSSFeedUrl)) {
			api.getTextChannelById(ID).sendMessage(embed).queue();
		}
	}
	
	/**
	 * Gets the thumbnail of a game according to the game, specified via a url to a feed of the game's forum
	 * @param feedUrl the url of a rss feed of the game forum
	 * @return the thumbnail of the game whose forum has the specified feed
	 */
	private String getThumbnailUrl(String feedUrl) {
		if (isDarkOrbitRSSFeedUrl(feedUrl))
			return Ref.darkOrbitLogoUrl;
		else if (isSeaFightRSSFeedUrl(feedUrl))
			return Ref.seafightLogoUrl;
		else if (isDrakensangRSSFeedUrl(feedUrl))
			return Ref.drakensangLogoUrl;
		else if (isBattlestarRSSFeedUrl(feedUrl))
			return Ref.battlestarLogoUrl;
		else if (isPirateStormRSSFeedUrl(feedUrl))
			return Ref.pirateStormLogoUrl;
		else if (isLeagueOfAngelsRSSFeedUrl(feedUrl))
			return Ref.leagueOfAngelsLogoUrl;
		else
			return Ref.darkOrbitLogoUrl;
	}
	
	/**
	 * Gets the name of a game whose forum has the specified feed
	 * @param feedUrl the url of a rss feed of the game forum
	 * @return the name of the game whose forum has the specified feed
	 */
	private String getGameName(String feedUrl) {
		if (isDarkOrbitRSSFeedUrl(feedUrl))
			return "Dark Orbit Reloaded";
		else if (isSeaFightRSSFeedUrl(feedUrl))
			return "Seafight";
		else if (isDrakensangRSSFeedUrl(feedUrl))
			return "Drakensang Online";
		else if (isBattlestarRSSFeedUrl(feedUrl))
			return "Battlestar Galactica Online";
		else if (isPirateStormRSSFeedUrl(feedUrl))
			return "Pirate Storm";
		else if (isLeagueOfAngelsRSSFeedUrl(feedUrl))
			return "League of Angels II";
		else
			return "???";
	}

	private boolean isDarkOrbitRSSFeedUrl(String url) {
		return url.startsWith("http://www.bigpoint.com/darkorbit");
	}
	private boolean isSeaFightRSSFeedUrl(String url) {
		return url.startsWith("http://www.bigpoint.com/seafight");
	}
	private boolean isDrakensangRSSFeedUrl(String url) {
		return url.startsWith("http://www.bigpoint.com/drasaonline");
	}
	private boolean isBattlestarRSSFeedUrl(String url) {
		return url.startsWith("http://www.bigpoint.com/bsgo");
	}
	private boolean isPirateStormRSSFeedUrl(String url) {
		return url.startsWith("http://www.bigpoint.com/piratestorm");
	}
	private boolean isLeagueOfAngelsRSSFeedUrl(String url) {
		return url.startsWith("http://www.bigpoint.com/leagueofangels2");
	}
	
	/**
	 * Provides a DOM document with the parsed xml from the specified link
	 * @return a DOM document with the parsed xml from the specified linkodes
	 * @param rSSFeedUrl the url to extract the xml code from
	 * */
	private org.w3c.dom.Document getParsedWebpageXml(String RSSFeedUrl) {
		org.w3c.dom.Document parsedXml = null;
		DocumentBuilder db = null;
		try {
			db = DocumentBuilderFactory.newInstance().newDocumentBuilder();
		} catch (ParserConfigurationException e1) {
			main.ErrorMessenger.inform("Error generating a DocumentBuilder to parse xml");
			e1.printStackTrace();
		}
		
		try {
			try {
				parsedXml = db.parse(new URL(RSSFeedUrl).openStream());
			} catch (IOException e) {
				System.out.println("Error parsing xml from the url: " + RSSFeedUrl
						+ "\nThis error is in silent mode because the bot was sometimes unable to access the webpage."
						+ "\nIf new posts happened while the bot couldn't access the feed, they will take while longer to be updated (till the bot is able to access the feed again)");
				e.printStackTrace();
			}
		} catch (SAXException e) {
			main.ErrorMessenger.inform("Was unable to parse the xml code from: " + RSSFeedUrl);
			e.printStackTrace();
		}

		return parsedXml;
	}

	/**
	 * @return a HashMap object with the thread urls as keys and the respective ForumThread object as object
	 * @parsedXml the Document containing the parsed xml code
	 * */
	public HashMap<String, ForumThread> getForumThreads(org.w3c.dom.Document parsedXml) {
		HashMap<String, ForumThread> threads = new HashMap<String, ForumThread>();
		NodeList nodes = parsedXml.getElementsByTagName("item");
		Element element;
		String threadUrl;
		String threadTitle;
		int threadAmountOfComments;
		ForumThread thread;
		for (int nodeNumber = 0; nodeNumber < nodes.getLength(); nodeNumber++) {
			element = (Element) nodes.item(nodeNumber);
			
			Node commentsNode = element.getElementsByTagName("slash:comments").item(0);
			if (commentsNode != null) {
				threadAmountOfComments = Integer.parseInt(commentsNode.getTextContent());

				// Test line
//				System.out.println("\nThread url: " + threadUrl + "\nThread title: " + threadTitle + "\nThread amount ofcomments: " + threadAmountOfComments);
			}
			else
				threadAmountOfComments = 0;
			threadUrl = element.getElementsByTagName("link").item(0).getTextContent();
			threadTitle = element.getElementsByTagName("title").item(0).getTextContent();
			thread = new ForumThread(threadTitle, threadAmountOfComments);
			threads.put(threadUrl, thread);
		}
		return threads;
	}
	
	/**
	 * @param url the url of the webpage to have it's html code parsed
	 * @return the parsed webpage html
	 * */
	private Document getParsedWebpageHtml(String url) {
		StringBuilder htmlCode = new StringBuilder("");
		try {
			URLConnection conn = new URL(url).openConnection();
			BufferedReader bf = new BufferedReader(new InputStreamReader(conn.getInputStream(), "UTF-8"));
			String htmlLine;
			while ((htmlLine = bf.readLine()) != null)
				htmlCode = htmlCode.append(htmlLine);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		return Jsoup.parse(htmlCode.toString());
	}
	
	/** A code, extremely unlikely to be present in the html code, to then be replaced by a line break, 
	 * because jsoup does not convert br and p tags into line breaks */
	private final String NEW_LINE_MARKER = "93eg90b2jmkz45";
	/** 
	 * Compiles information about the post into a ForumPost object
	 * @param postUrl the base url for the thread (no page number indication)
	 * @param postNumber the position of the post in the thread. 1 = initial post, 2 = first comment,...
	 * @param postTitle the title of the thread where the post is inserted
	 * @return the ForumPost object representing the forum post based on the information given
	 * */
	public ForumPost getForumPost(String postUrl, String postTitle, int postNumber) {
		if (postNumber < 1)
			return null;

		String postMessage = "Error retrieving the post message.";
		String postAuthorName = "Error";
		String postAuthorTitle = "Error";
		String postAuthorAvatar = null;
		try {
			float floatPositionInAllPages = (float) postNumber / 20;
			/** The number of the page in which the post is located */
			int realPositionInAllPages = (int) floatPositionInAllPages;
			if (floatPositionInAllPages > realPositionInAllPages)
				realPositionInAllPages++;

			postUrl = postUrl + "page-" + realPositionInAllPages;

			Document htmlDoc = getParsedWebpageHtml(postUrl);
			htmlDoc.select("br").append(NEW_LINE_MARKER);
			htmlDoc.select("p").prepend(NEW_LINE_MARKER.concat(NEW_LINE_MARKER));

			Elements posts = htmlDoc.select("div.messageContent");
			posts.select("img").prepend("[Image]");
			posts.select("aside").remove();

			// postNumber-1 because Elements indexes start at 0 | 20 posts per page
			int postNumberInPage = postNumber - 1 - (realPositionInAllPages - 1) * 20;
			postMessage = posts.get(postNumberInPage).text().replaceAll(NEW_LINE_MARKER, "\n").replaceAll("\n" + " +",
					"\n");

			Elements userInfo = htmlDoc.select("div.messageUserInfo");
			postAuthorName = userInfo.get(postNumberInPage).select("a.username").text();
			postAuthorTitle = userInfo.get(postNumberInPage).select("em.userTitle").text();
			postAuthorAvatar = userInfo.get(postNumberInPage).select("img").get(0).absUrl("src");
		} catch (Exception e) {
			main.ErrorMessenger
					.inform("Failed to apply getForumPost(" + postUrl + ", " + postTitle + ", " + postNumber + ")");
			e.printStackTrace();
		}

		return new ForumPost(postAuthorName, postAuthorTitle, postAuthorAvatar, postTitle, postUrl, postMessage);
	}
	
	/** Loads the threads present on the xml codes, that are in the webpages of urls provided by config.txt, into threadsList */
	public void loadThreads() {
		org.w3c.dom.Document parsedXml = null;
		for (String RSSFeedUrl : Ref.RSSFeeds.getFeedsUrls()) {
			parsedXml = getParsedWebpageXml(RSSFeedUrl);
			//      url   , nComments+title
			HashMap<String, ForumThread> RLForumThreads = getForumThreads(parsedXml);
			for(String RLUrl : RLForumThreads.keySet())
				threadsList.put(RLUrl, new ForumThread(RLForumThreads.get(RLUrl).title, RLForumThreads.get(RLUrl).amountOfComments));
		}
	}
}
