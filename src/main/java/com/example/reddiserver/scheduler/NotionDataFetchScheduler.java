package com.example.reddiserver.scheduler;

import com.example.reddiserver.common.ApiResponse;
import com.example.reddiserver.service.NotionService;
import lombok.RequiredArgsConstructor;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
@RequiredArgsConstructor
public class NotionDataFetchScheduler {

	private final NotionService notionService;

	@Scheduled(cron = "0 */30 * * * *")  // 30분마다 실행
	public void fetchNotionData() {
		// DB init
		notionService.deleteAll();

		List<String> brandPageIds = notionService.getBrandPageIds();
		notionService.getBrandPageContents(brandPageIds);

		List<String> marketingPageIds = notionService.getMarketingPageIds();
		notionService.getMarketingPageContents(marketingPageIds);
	}
}
