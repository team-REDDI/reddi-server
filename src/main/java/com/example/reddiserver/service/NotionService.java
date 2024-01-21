package com.example.reddiserver.service;

import com.example.reddiserver.entity.Brand;
import com.example.reddiserver.entity.BrandTag;
import com.example.reddiserver.entity.enums.BrandTagType;
import com.example.reddiserver.repository.BrandRepository;
import com.example.reddiserver.repository.BrandTagRepository;
import com.fasterxml.jackson.databind.node.ObjectNode;
import jakarta.persistence.EntityManager;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.reactive.function.BodyInserters;
import org.springframework.web.reactive.function.client.WebClient;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.JsonNode;

import java.util.*;

@Service
@Transactional(readOnly = true)
public class NotionService {

	@Value("${notion.brandDB.id}")
	private String BRAND_DB_ID;

	private final WebClient webClient;
	private final ObjectMapper objectMapper;
	private final BrandRepository brandRepository;
	private final BrandTagRepository brandTagRepository;
	private final EntityManager em;

	public NotionService(WebClient.Builder webClientBuilder, @Value("${notion.api.key}") String NOTION_API_KEY, ObjectMapper objectMapper, BrandRepository brandRepository, BrandTagRepository brandTagRepository, EntityManager em) {
		this.webClient = webClientBuilder.
				baseUrl("https://api.notion.com/v1")
				.defaultHeader(HttpHeaders.AUTHORIZATION, "Bearer " + NOTION_API_KEY)
				.defaultHeader(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
				.defaultHeader("Notion-Version", "2022-06-28")
				.build();

		this.objectMapper = objectMapper;
		this.brandRepository = brandRepository;
		this.brandTagRepository = brandTagRepository;
		this.em = em;
	}

	// 브랜드 database 순회하면서 브랜드 페이지 Id 추출
	public List<String> getBrandPageIds() {
		JsonNode response = webClient.post().uri("/databases/"+BRAND_DB_ID+"/query").body(BodyInserters.empty())
				.retrieve()
				.bodyToMono(JsonNode.class)
				.block();

		if (response != null && response.has("results") && response.get("results").isArray()) {
			List<String> ids = new ArrayList<>();

			for (JsonNode resultNode : response.get("results")) {

				JsonNode isPublishedNode = resultNode.get("properties").get("게시여부").get("select");

				// 게시여부가 Published인 경우에만 브랜드 페이지 Id 추출
				if (!isPublishedNode.isNull() && isPublishedNode.get("name").asText().equals("Published")){
					ids.add(resultNode.get("id").asText());
				}
			}

			return ids;
		} else {
			return Collections.emptyList();
		}
	}

	// 브랜드 페이지 순회하면서 브랜드 페이지 내용 추출 후 DB 저장
	@Transactional
	public void getBrandPageContents(List<String> brandPageIds) {


		for (String brandPageId : brandPageIds) {
			// Fetch metadata for the brand page
			JsonNode pageMetadata = fetchPageMetadata(brandPageId);

			// Extract information from the pageMetadata JsonNode as needed
			String pageId = pageMetadata.get("id").asText();
			String createdTime = pageMetadata.get("created_time").asText();
			String lastEditedTime = pageMetadata.get("last_edited_time").asText();
			String title = pageMetadata.get("properties").get("이름").get("title").get(0).get("plain_text").asText();
			String notionUrl = pageMetadata.get("url").asText();

			Brand brand = new Brand();

			brand.setNotion_page_id(pageId);
			brand.setNotion_page_created_time(createdTime);
			brand.setNotion_page_last_edited_time(lastEditedTime);
			brand.setName(title);
			brand.setNotion_page_url(notionUrl);


			// Extract properties information
			JsonNode propertiesNode = pageMetadata.get("properties");


			// Extract key
			// Check if propertiesNode is not null before proceeding
			if (propertiesNode != null && propertiesNode.isObject()) {
				// Iterate over the field names and print them
				propertiesNode.fieldNames().forEachRemaining(fieldName -> {

					if (fieldName.equals("브랜드_분위기") || fieldName.equals("브랜드_색감") || fieldName.equals("MKT_종류") || fieldName.equals("MKT_타겟층") || fieldName.equals("산업군")) {
						JsonNode multiSelect = propertiesNode.get(fieldName).get("multi_select");

						if (multiSelect != null && multiSelect.isArray()) {
							for (JsonNode select : multiSelect) {
								String tag = select.get("name").asText();

								// Brand Tag 에 저장
								BrandTag brandTag = BrandTag.builder()
										.brand(brand)
										.brandTagType(BrandTagType.valueOf(fieldName))
										.tag(tag)
										.build();


								brandTagRepository.save(brandTag);
								// 또는
								// brand.getBrandTags().add(brandTag);

							}
						}
					}
				});
			}

			else {
				System.out.println("propertiesNode is null or not an object");
			}


			// fetch page contents

			JsonNode pageContents = fetchPageContents(brandPageId).get("results");

			List<ObjectNode> contentList = new ArrayList<>();

			for (JsonNode block : pageContents) {
				if (block.isObject()) {
					// Convert the JsonNode to ObjectNode for easy removal of keys
					ObjectNode blockObject = (ObjectNode) block;

					// Remove specific keys
					blockObject.remove("object");
					blockObject.remove("id");
					blockObject.remove("parent");
					blockObject.remove("created_time");
					blockObject.remove("last_edited_time");
					blockObject.remove("created_by");
					blockObject.remove("last_edited_by");
					blockObject.remove("has_children");
					blockObject.remove("archived");
					blockObject.remove("is_toggleable");


					contentList.add(blockObject);
				}
			}

			brand.setContent(contentList.toString());
			brandRepository.save(brand);


			// Fetch and process additional metadata for each property
			// Example: fetchPropertyMetadata(pageId, "BvYH");
		}
	}


	public JsonNode fetchPageMetadata(String pageId) {
		String pageUrl = "/pages/" + pageId;
		return webClient.get().uri(pageUrl)
				.retrieve()
				.bodyToMono(JsonNode.class)
				.block();
	}

	public JsonNode fetchPageContents(String pageId) {
		String pageContentsUrl = "/blocks/" + pageId + "/children";
		return webClient.get().uri(pageContentsUrl)
				.retrieve()
				.bodyToMono(JsonNode.class)
				.block();
	}

	// brand, brand_tags 테이블 초기화
	@Transactional
	public void deleteAll() {
		brandTagRepository.deleteAll();
		brandRepository.deleteAll();

		em.createNativeQuery("ALTER TABLE brands AUTO_INCREMENT = 1").executeUpdate();
		em.createNativeQuery("ALTER TABLE brand_tags AUTO_INCREMENT = 1").executeUpdate();
	}

}