package com.example.reddiserver.service.notion;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.BodyInserters;
import org.springframework.web.reactive.function.client.WebClient;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.JsonNode;

import java.util.*;

@Service
public class NotionService {

	@Value("${notion.brandDB.id}")
	private String BRAND_DB_ID;

	private final WebClient webClient;
	private final ObjectMapper objectMapper;

	public NotionService(WebClient.Builder webClientBuilder, @Value("${notion.api.key}") String NOTION_API_KEY, ObjectMapper objectMapper) {
		this.webClient = webClientBuilder.
				baseUrl("https://api.notion.com/v1")
				.defaultHeader(HttpHeaders.AUTHORIZATION, "Bearer " + NOTION_API_KEY)
				.defaultHeader(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
				.defaultHeader("Notion-Version", "2022-06-28")
				.build();

		this.objectMapper = objectMapper;
	}

	//	public String fetchDataFromNotion() {
//
//		// Perform the actual request
//		return webClient.post().uri("/").body(BodyInserters.empty()).retrieve().bodyToMono(String.class).block();
//	}

	// 브랜드 database 순회하면서 브랜드 페이지 Id 추출
	public List<String> getBrandPageIds() {
		JsonNode response = webClient.post().uri("/databases/"+BRAND_DB_ID+"/query").body(BodyInserters.empty())
				.retrieve()
				.bodyToMono(JsonNode.class)
				.block();

		if (response != null && response.has("results") && response.get("results").isArray()) {
			List<String> ids = new ArrayList<>();

			for (JsonNode resultNode : response.get("results")) {
				if (resultNode.has("id") && resultNode.get("id").isTextual()) {
					ids.add(resultNode.get("id").asText());
				}
			}

			return ids;
		} else {
			return Collections.emptyList();
		}
	}

	// 브랜드 페이지 순회하면서 브랜드 페이지 내용 추출 후 DB 저장
	public void getBrandPageContents(List<String> brandPageIds) {


		for (String brandPageId : brandPageIds) {
			// Fetch metadata for the brand page
			JsonNode pageMetadata = fetchPageMetadata(brandPageId);

			// Extract information from the pageMetadata JsonNode as needed
			String pageId = pageMetadata.get("id").asText();
			String createdTime = pageMetadata.get("created_time").asText();
			String lastEditedTime = pageMetadata.get("last_edited_time").asText();

			System.out.println("Page ID: " + pageId);
			System.out.println("Created Time: " + createdTime);
			System.out.println("Last Edited Time: " + lastEditedTime);



			// Extract properties information
			JsonNode propertiesNode = pageMetadata.get("properties");

			System.out.println("propertiesNode: " + propertiesNode);
			System.out.println();


			// Fetch and process additional metadata for each property
			// Example: fetchPropertyMetadata(pageId, "BvYH");
		}
	}

	public JsonNode fetchPageMetadata(String pageId) {
		String pageUrl = "https://api.notion.com/v1/pages/" + pageId;
		return webClient.get().uri(pageUrl)
				.retrieve()
				.bodyToMono(JsonNode.class)
				.block();
	}

	public JsonNode fetchPropertyMetadata(String pageId, String propertyId) {
		String propertyUrl = "https://api.notion.com/v1/pages/" + pageId + "/properties/" + propertyId;
		return webClient.get().uri(propertyUrl)
				.retrieve()
				.bodyToMono(JsonNode.class)
				.block();
	}

}