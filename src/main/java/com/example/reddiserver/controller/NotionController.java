package com.example.reddiserver.controller;

import com.example.reddiserver.common.ApiResponse;
import com.example.reddiserver.service.NotionService;
import io.swagger.v3.oas.annotations.Operation;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/notion")
public class NotionController {

	private final NotionService notionService;

	@Operation(summary = "[테스트용] 노션 api 호출해서 자체 DB 갱신")
	@GetMapping("/update")
	public ApiResponse<List<String>> getNotionData() {

		List<String> brandPageIds = notionService.getBrandPageIds();
		notionService.getBrandPageContents(brandPageIds);

		// 임시로 하나만 조회
//		notionService.getBrandPageContents(Collections.singletonList(brandPageIds.get(0)));


		return ApiResponse.successResponse(notionService.getBrandPageIds());
	}

}