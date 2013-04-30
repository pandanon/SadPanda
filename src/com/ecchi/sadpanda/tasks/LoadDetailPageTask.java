package com.ecchi.sadpanda.tasks;

import java.util.ArrayList;
import java.util.List;

import com.ecchi.sadpanda.util.ImageSetThumb;
import com.ecchi.sadpanda.util.PagedScrollAdapter;

public class LoadDetailPageTask extends LoadPageTask<ImageSetThumb> {

	public LoadDetailPageTask(PagedScrollAdapter<ImageSetThumb> pageAdapter) {
		super(pageAdapter);
	}

	@Override
	protected List<ImageSetThumb> parseHtmlContent(String content) {
		
		String token = "class=\"gdtm";
		String strippedContent = content.substring(content.indexOf(token)+token.length());
		String[] thumbsContent = strippedContent.split(token);
		
		List<ImageSetThumb> thumbUrls = new ArrayList<ImageSetThumb>();
		
		for(String thumb: thumbsContent)
		{
			int startIdx = thumb.indexOf("url(") + 4;
			int endIdx = thumb.indexOf(")", startIdx);
			String url = thumb.substring(startIdx, endIdx);
			
			// TODO deal with retarded two-way thumb display (background vs img)
			
			//TODO calculate matrix
			
			thumbUrls.add(new ImageSetThumb(url, null));	
		}
		
		return thumbUrls;
	}

}
