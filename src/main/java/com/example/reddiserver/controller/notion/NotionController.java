package com.example.reddiserver.controller.notion;

import com.example.reddiserver.common.ApiResponse;
import com.example.reddiserver.service.notion.NotionService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/notion")
public class NotionController {

	private final NotionService notionService;

	@Autowired
	public NotionController(NotionService notionService) {
		this.notionService = notionService;
	}

	@GetMapping("/data")
	public ApiResponse<List<String>> getNotionData() {

		List<String> brandPageIds = notionService.getBrandPageIds();
		notionService.getBrandPageContents(brandPageIds);


		return ApiResponse.successResponse(notionService.getBrandPageIds());
	}

}