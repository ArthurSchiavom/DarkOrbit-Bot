package RSSFeed;

/**
 * This class will check for new threads or comments, according to the rss feeds urls provided at config.txt, every 30 seconds
 */
public class Informer implements Runnable {

	RSSFeedReader feedReader = new RSSFeedReader();
	@Override
	public void run() {
		feedReader.loadThreads();
		while (true) {
			try {
				Thread.sleep(60000);
			} catch (Exception e) {
				main.ErrorMessenger.inform("Failed to set Informer to sleep");
				e.printStackTrace();
			}
			try {
				feedReader.checkForNewPosts();
			} catch (Exception e) {
				main.ErrorMessenger.inform("Failed to check for new forum posts (Informer class)");
				e.printStackTrace();
			}
		}

	}

}
