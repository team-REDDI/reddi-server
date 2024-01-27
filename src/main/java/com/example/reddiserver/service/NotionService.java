package com.example.reddiserver.service;

import com.example.reddiserver.entity.Brand;
import com.example.reddiserver.entity.BrandTag;
import com.example.reddiserver.entity.Post;
import com.example.reddiserver.entity.PostTag;
import com.example.reddiserver.entity.enums.BrandTagType;
import com.example.reddiserver.entity.enums.PostTagType;
import com.example.reddiserver.repository.BrandRepository;
import com.example.reddiserver.repository.BrandTagRepository;
import com.example.reddiserver.repository.PostRepository;
import com.example.reddiserver.repository.PostTagRepository;
import com.fasterxml.jackson.databind.node.ObjectNode;
import jakarta.persistence.EntityManager;
import lombok.extern.slf4j.Slf4j;
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
@Slf4j
@Transactional(readOnly = true)
public class NotionService {

	@Value("${notion.brandDB.id}")
	private String BRAND_DB_ID;

	@Value("${notion.marketingDB.id}")
	private String MARKETING_DB_ID;

	private final WebClient webClient;
	private final BrandRepository brandRepository;
	private final BrandTagRepository brandTagRepository;
	private final PostRepository postRepository;
	private final PostTagRepository postTagRepository;
	private final EntityManager em;

	public NotionService(WebClient.Builder webClientBuilder, @Value("${notion.api.key}") String NOTION_API_KEY, BrandRepository brandRepository, BrandTagRepository brandTagRepository, PostRepository postRepository, PostTagRepository postTagRepository, EntityManager em) {
		this.webClient = webClientBuilder.
				baseUrl("https://api.notion.com/v1")
				.defaultHeader(HttpHeaders.AUTHORIZATION, "Bearer " + NOTION_API_KEY)
				.defaultHeader(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
				.defaultHeader("Notion-Version", "2022-06-28")
				.build();

		this.brandRepository = brandRepository;
		this.brandTagRepository = brandTagRepository;
		this.postRepository = postRepository;
		this.postTagRepository = postTagRepository;
		this.em = em;
	}

	// brand, brand_tags 테이블 초기화
	@Transactional
	public void deleteAll() {
		postTagRepository.deleteAll();
		postRepository.deleteAll();
		brandTagRepository.deleteAll();
		brandRepository.deleteAll();

		em.createNativeQuery("ALTER TABLE brands AUTO_INCREMENT = 1").executeUpdate();
		em.createNativeQuery("ALTER TABLE brand_tags AUTO_INCREMENT = 1").executeUpdate();
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

			try {
				// Fetch metadata for the brand page
				JsonNode pageMetadata = fetchPageMetadata(brandPageId);

				// Extract information from the pageMetadata JsonNode as needed
				String pageId = pageMetadata.get("id").asText();
				String createdTime = pageMetadata.get("created_time").asText();
				String lastEditedTime = pageMetadata.get("last_edited_time").asText();
				String title = pageMetadata.get("properties").get("이름").get("title").get(0).get("plain_text").asText();

				JsonNode coverNode = pageMetadata.get("cover");
				String coverUrl = null;
				if (!coverNode.isNull()) {
					if (coverNode.get("type").asText().equals("external")) {
						coverUrl = coverNode.get("external").get("url").asText();
					}
					else if (coverNode.get("type").asText().equals("file")) {
						coverUrl = coverNode.get("file").get("url").asText();
					}
				}

				String notionUrl = pageMetadata.get("url").asText();

				Brand brand = new Brand();

				brand.setNotion_page_id(pageId);
				brand.setNotion_page_created_time(createdTime);
				brand.setNotion_page_last_edited_time(lastEditedTime);
				brand.setName(title);
				brand.setCover_url(coverUrl);
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
					log.error("propertiesNode is null or not an object");
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

			}

			catch (Exception e) {
				log.error("Error while fetching page metadata for pageId: {}", brandPageId, e);
				throw e;
			}

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

	// 브랜드 database 순회하면서 브랜드 페이지 Id 추출
	public List<String> getMarketingPageIds() {
		JsonNode response = webClient.post().uri("/databases/"+MARKETING_DB_ID+"/query").body(BodyInserters.empty())
				.retrieve()
				.bodyToMono(JsonNode.class)
				.block();

		if (response != null && response.has("results") && response.get("results").isArray()) {
			List<String> ids = new ArrayList<>();

			for (JsonNode resultNode : response.get("results")) {

//				log.info("resultNode : {}", resultNode.toPrettyString());

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
	public void getMarketingPageContents(List<String> marketingPageIds) {


		for (String marketingPageId : marketingPageIds) {

			try {
				// Fetch metadata for the brand page
				JsonNode pageMetadata = fetchPageMetadata(marketingPageId);

				// Extract information from the pageMetadata JsonNode as needed
				String pageId = pageMetadata.get("id").asText();
				String createdTime = pageMetadata.get("created_time").asText();
				String lastEditedTime = pageMetadata.get("last_edited_time").asText();
				String title = pageMetadata.get("properties").get("이름").get("title").get(0).get("plain_text").asText();

				JsonNode subtitleNode = pageMetadata.get("properties").get("소제목").get("rich_text");
				String subtitle = null;
				if (!subtitleNode.isEmpty()) {
					subtitle = subtitleNode.get(0).get("plain_text").asText();
				}

				JsonNode descriptionNode = pageMetadata.get("properties").get("설명").get("rich_text");
				String description = null;
				if (!descriptionNode.isEmpty()) {
					description = descriptionNode.get(0).get("plain_text").asText();
				}

				JsonNode coverNode = pageMetadata.get("cover");
				String coverUrl = null;
				if (!coverNode.isNull()) {
					if (coverNode.get("type").asText().equals("external")) {
						coverUrl = coverNode.get("external").get("url").asText();
					}
					else if (coverNode.get("type").asText().equals("file")) {
						coverUrl = coverNode.get("file").get("url").asText();
					}
				}

				String notionUrl = pageMetadata.get("url").asText();

				Post post = new Post();

				post.setNotion_page_id(pageId);
				post.setNotion_page_created_time(createdTime);
				post.setNotion_page_last_edited_time(lastEditedTime);
				post.setTitle(title);
				post.setSubtitle(subtitle);
				post.setDescription(description);
				post.setCover_url(coverUrl);
				post.setNotion_page_url(notionUrl);

				JsonNode BrandNode = pageMetadata.get("properties").get("브랜드").get("relation");
				String brandId = null;
				if (!BrandNode.isEmpty()) {
					brandId = BrandNode.get(0).get("id").asText();

					// brandId로 브랜드 엔티티 가져오기
					Brand brand = brandRepository.findBrandByNotion_page_id(brandId);

					if (brand != null) {
						post.setBrand(brand);
					}
				}

				// Extract properties information
				JsonNode propertiesNode = pageMetadata.get("properties");


				// 태그 추출 후 저장
				if (propertiesNode != null && propertiesNode.isObject()) {
					// Iterate over the field names and print them
					propertiesNode.fieldNames().forEachRemaining(fieldName -> {

						if (fieldName.equals("기업") || fieldName.equals("마케팅_종류") || fieldName.equals("산업") || fieldName.equals("타겟층")) {
							JsonNode multiSelect = propertiesNode.get(fieldName).get("multi_select");

							if (multiSelect != null && multiSelect.isArray()) {
								for (JsonNode select : multiSelect) {
									String tag = select.get("name").asText();

									// Brand Tag 에 저장
									PostTag postTag = PostTag.builder()
											.post(post)
											.postTagType(PostTagType.valueOf(fieldName))
											.tag(tag)
											.build();


									postTagRepository.save(postTag);
									// 또는
									// brand.getBrandTags().add(brandTag);

								}
							}
						}
					});
				} else {
					log.error("propertiesNode is null or not an object");
				}


				// fetch page contents

				JsonNode pageContents = fetchPageContents(marketingPageId).get("results");

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

				post.setContent(contentList.toString());
				postRepository.save(post);

			}


			catch (Exception e) {
				log.error("Error while fetching page metadata for pageId: {}", marketingPageId, e); // 스택트레이스도 같이 출력
				throw e;
			}



		}
	}
}