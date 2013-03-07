package util;

import java.util.Date;

/**
 * Container class for all of sadPanda's image sets. 
 * @author Alex
 *
 */
public class ImageSetDescription {

	private ImageContent setContent;
	private String setName;
	private String setThumbUrl;
	private Date setPublished;
	private int setScore;
	private String setUploader;
	private String setTorrentUrl;

	public ImageSetDescription(ImageContent inSetContent, String inSetName,
			String inSetThumbUrl, Date inSetPublished, int inSetScore,
			String inSetUploader, String inSetTorrentUrl) {
		this.setContent = inSetContent;
		this.setName = inSetName;
		this.setThumbUrl = inSetThumbUrl;
		this.setPublished = inSetPublished;
		this.setScore = inSetScore;
		this.setUploader = inSetUploader;
		this.setTorrentUrl = inSetTorrentUrl;
	}

	public ImageContent getSetContent() {
		return setContent;
	}
	
	public String getSetName() {
		return setName;
	}
	
	public String getSetThumbUrl() {
		return setThumbUrl;
	}
	
	public Date getSetPublished() {
		return setPublished;
	}
	
	public int getSetScore() {
		return setScore;
	}
	
	public String getSetUploader() {
		return setUploader;
	}
	
	public String getSetTorrentUrl() {
		return setTorrentUrl;
	}
	
	public static enum ImageContent {
		ImageSet, Doujinshi, Western, Manga, NonH, Cosplay, Misc, AsianPorn, GameCG, ArtistCG;
		
		public static ImageContent parseContent(String content)
		{
			if(content.equals("imageset"))
				return ImageSet;
			else if(content.equals("doujinshi"))
				return Doujinshi;
			else if(content.equals("manga"))
				return Manga;
			else if(content.equals("artistcg"))
				return ArtistCG;
			else if(content.equals("gamecg"))
				return GameCG;
			else if(content.equals("western"))
				return Western;
			else if(content.equals("non-h"))
				return NonH;
			else if(content.equals("cosplay"))
				return Cosplay;
			else if(content.equals("asianporn"))
				return AsianPorn;
			else
				return Misc;
		}
		
		public static int getColor(ImageContent content)
		{
			int out;
			
			switch (content) {
			case NonH:
				out = 0xFF80E6FF;
				break;
			case Doujinshi:
				out = 0xFFFF3333;
				break;
			case GameCG:
				out = 0xFF009933;
				break;
			case Manga:
				out = 0xFFFF9933;
				break;
			case Western:
				out = 0xFF66FF66;
				break;
			case ImageSet:
				out = 0xFF0066FF;
				break;
			case ArtistCG:
				out = 0xFFFFFF00;
				break;
			case Cosplay:
				out = 0xFF993399;
				break;
			case AsianPorn:
				out = 0xFFFF99FF;
				break;
			default:
				out = 0xFFE2E2E2;
				break;
			}
			
			return out;
		}
	}
}
