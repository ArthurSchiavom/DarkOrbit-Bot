package RSSFeed;

import java.util.ArrayList;
import java.util.List;

public class FeedChannels {
	private List<String> feedsUrls = new ArrayList<String>();
	private List<String> feedsUrlsNoRepeat = new ArrayList<String>();
	private List<Long> discordChannels = new ArrayList<Long>();
	
	public void add(String feedUrl, Long channelID) {
		feedsUrls.add(feedUrl);
		if (!feedsUrlsNoRepeat.contains(feedUrl))
			feedsUrlsNoRepeat.add(feedUrl);
		discordChannels.add(channelID);
	}
	
	/**
	 * 
	 * @param feedUrl
	 * @return a list with all the Discord channel IDs associated with the given feedUrl 
	 */
	public List<Long> getChannels(String feedUrl) {
		List<Long> results = new ArrayList<Long>();
		int index = 0;
		for (String url : feedsUrls) {
			if (url.equals(feedUrl))
				results.add(discordChannels.get(index));
			index++;
		}
		return results;
	}
	
	public List<String> getFeedsUrls() {
		return feedsUrlsNoRepeat;
	}
}
