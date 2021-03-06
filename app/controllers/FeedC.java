package controllers;

import java.io.IOError;
import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

import models.BlogPost;
import models.KeyValueData;
import play.Logger;
import play.mvc.Controller;
import viecili.jrss.generator.RSSFeedGenerator;
import viecili.jrss.generator.RSSFeedGeneratorFactory;
import viecili.jrss.generator.elem.Channel;
import viecili.jrss.generator.elem.Item;
import viecili.jrss.generator.elem.RSS;

public class FeedC extends Controller {
	
	public static final org.apache.log4j.Logger cLogger = 
											Logger.log4j.getLogger(FeedC.class);
	
	public static final String BLOG_TITLE = "blog_title";
	public static final String BLOG_URL = "blog_url";
	public static final String BLOG_DESCRIPTION = "blog_description";
	public static final String BLOG_COPYRIGHT = "blog_copyright";
	public static final String BLOG_FEED_URL = "feed_url";
	public static final String TWITTER_URL = "twitter_url";
	public static final String FACEBOOK_URL = "facebook_url";

	//TODO: We need to incorporate chicklets to add the feed to Yahoo, Google Reader, etc
	//TODO: Find out how to enable feed detection which wil redirect to the Feedburner feed
	public static void blog() {				
		RSSFeedGenerator feedGen = RSSFeedGeneratorFactory.getDefault();
		
		try {			
			RSS rss = new RSS();
			rss.addChannel(getChannel());
			String feed = feedGen.generateAsString(rss);
			renderXml(feed);
		} catch(IOException ioe) {
			//TODO: How do we specify to the blog reader that an error has occurred 
			cLogger.error("Error while genarating the blog feed", ioe);
		}
	}

	private static Channel getChannel() {
		List<BlogPost> blogPosts = BlogPost.findLatest(50);
		//TODO: The information below should come from the database

		Channel channel = new Channel(KeyValueData.findValue(BLOG_TITLE, "no title"), 
									  KeyValueData.findValue(BLOG_URL, request.domain + ":" + request.port + "/" + request.url), 
									  KeyValueData.findValue(BLOG_DESCRIPTION, "no description"));
		//TODO: Should we provide a url?
		channel.setCopyright(KeyValueData.findValue(BLOG_COPYRIGHT, ""));
		
		try {
			//TODO: This should be a daily date
			channel.setPubDate(getToday());
		} catch(ParseException pe) {
			cLogger.error("Could not get today as Date", pe);
		}
		
		//TODO: This should be a proper date which reflects the last build date
		if(blogPosts != null && blogPosts.size() > 0) {
			BlogPost latest = blogPosts.get(0);
			channel.setLastBuildDate(latest.lastUpdatedAt);
		}
		
		channel.setTtl(60); //TODO: Can this be longer?
		//channel.setImage(null);
		//channel.setRating(rating);
		
		for(BlogPost blogPost : blogPosts) {
			channel.addItem(getItem(blogPost));
		}
		return channel;
	}

	private static Item getItem(BlogPost blogPost) {
		//TODO: Also add a link to the blog post
		Item item = new Item(blogPost.title, blogPost.content);
		item.setAuthor(blogPost.author.screenname);
		item.setPubDate(blogPost.lastUpdatedAt);
		//TODO: Set comments
		//item.setComments("");
		
		return item;
	}
	
	private static Date getToday() throws ParseException {
		Date today = new Date();
		SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
		String todayAsYearMonthDate = dateFormat.format(today);
		return dateFormat.parse(todayAsYearMonthDate);
	}
}
