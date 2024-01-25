package com.example.reddiserver.controller;

import com.example.reddiserver.common.ApiResponse;
import com.example.reddiserver.service.NotionService;
import io.swagger.v3.oas.annotations.Hidden;
import io.swagger.v3.oas.annotations.Operation;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/notion")
@Slf4j
public class NotionController {

	private final NotionService notionService;

	@Operation(summary = "[수동갱신] 노션 api 호출해서 DB 갱신")
	@GetMapping("/update")
	public ApiResponse<?> getNotionData() {


		List<String> brandPageIds = notionService.getBrandPageIds();
		notionService.getBrandPageContents(brandPageIds);

		// 임시로 하나만 조회
//		notionService.getBrandPageContents(Collections.singletonList(brandPageIds.get(0)));

		List<String> marketingPageIds = notionService.getMarketingPageIds();
		notionService.getMarketingPageContents(marketingPageIds);


		return ApiResponse.successWithNoContent();
	}

}