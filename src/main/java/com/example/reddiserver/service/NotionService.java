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
import io.swagger.v3.core.util.Json;
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

	@Value("${notion.homeDB.id}")
	private String HOME_DB_ID;


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
//	@Transactional
//	public void deleteAll() {
//		postTagRepository.deleteAll();
//		postRepository.deleteAll();
//		brandTagRepository.deleteAll();
//		brandRepository.deleteAll();
//
//		em.createNativeQuery("ALTER TABLE post_tags AUTO_INCREMENT = 1").executeUpdate();
//		em.createNativeQuery("ALTER TABLE posts AUTO_INCREMENT = 1").executeUpdate();
//		em.createNativeQuery("ALTER TABLE brand_tags AUTO_INCREMENT = 1").executeUpdate();
//		em.createNativeQuery("ALTER TABLE brands AUTO_INCREMENT = 1").executeUpdate();
//
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

		// 브랜드 삭제

		// 기존 DB에 저장된 브랜드 데이터 가져오기
		List<Brand> existingBrands = brandRepository.findAll();

		for (Brand existingBrand : existingBrands) {
			// 기존 DB에 있는 브랜드가 현재 Notion에서 가져온 데이터에도 존재하는지 확인
			if (!brandPageIds.contains(existingBrand.getNotion_page_id())) {
				// Notion에서 해당 브랜드 데이터가 없다면 삭제
				brandRepository.delete(existingBrand);
				// brand_tags 테이블에서 해당 브랜드와 관련된 데이터 삭제 (cascade 옵션)
			}
		}

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

				// notion pageId 를 이용해서 기존 DB에 해당 브랜드가 있는지 확인
				Brand brand = brandRepository.findBrandByNotion_page_id(pageId);

				// 새 브랜드 생성
				if (brand == null) {
					brand = new Brand();
					brand.setNotion_page_id(pageId);
				}

				// 브랜드 정보 업데이트
				brand.setNotion_page_created_time(createdTime);
				brand.setNotion_page_last_edited_time(lastEditedTime);
				brand.setName(title);
				brand.setCover_url(coverUrl);
				brand.setNotion_page_url(notionUrl);

				brandRepository.save(brand);

				// Extract properties information
				JsonNode propertiesNode = pageMetadata.get("properties");


				// Extract key
				// Check if propertiesNode is not null before proceeding
				if (propertiesNode != null && propertiesNode.isObject()) {
					// Iterate over the field names and print them
					Brand finalBrand = brand;
					List<BrandTag> newBrandTags = new ArrayList<>(); // notion에서 새롭게 가져온 브랜드 태그들
					propertiesNode.fieldNames().forEachRemaining(fieldName -> {

						if (fieldName.equals("브랜드_분위기") || fieldName.equals("브랜드_색감") || fieldName.equals("MKT_종류") || fieldName.equals("MKT_타겟층") || fieldName.equals("산업군")) {
							JsonNode multiSelect = propertiesNode.get(fieldName).get("multi_select");

							if (multiSelect != null && multiSelect.isArray()) {
								for (JsonNode select : multiSelect) {
									String tag = select.get("name").asText();

									// 해당 finalBrand, fieldName, tag 가 DB에 이미 존재하는지 확인
									BrandTag brandTag = brandTagRepository.findBrandTagByBrandAndBrandTagTypeAndTag(finalBrand, BrandTagType.valueOf(fieldName), tag);


									// brandTag 가 DB에 존재하지 않는 경우는 새로 생성
									if (brandTag == null) {
										brandTag = BrandTag.builder()
												.brand(finalBrand)
												.brandTagType(BrandTagType.valueOf(fieldName))
												.tag(tag)
												.build();
										brandTagRepository.save(brandTag);
									}

									newBrandTags.add(brandTag);

									// 또는
									// brand.getBrandTags().add(brandTag);

								}
							}
						}
					});

					// newBrandTags 에 없는 기존 DB brandTag 삭제

					List<BrandTag> existingBrandTags = brandTagRepository.findAllByBrand(finalBrand); // 기존 DB brandTag

					existingBrandTags.removeAll(newBrandTags);
					brandTagRepository.deleteAll(existingBrandTags);
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

	// 마케팅 database 순회하면서 마케팅 페이지 Id 추출
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

	// 마케팅 페이지 순회하면서 브랜드 페이지 내용 추출 후 DB 저장
	@Transactional
	public void getMarketingPageContents(List<String> marketingPageIds) {

		// 마케팅 포스트 삭제

		// 기존 DB에 저장된 포스트 데이터 가져오기
		List<Post> existingPosts = postRepository.findAll();

		for (Post existingPost : existingPosts) {
			// 기존 DB에 있는 포스트가 현재 Notion에서 가져온 데이터에도 존재하는지 확인
			if (!marketingPageIds.contains(existingPost.getNotion_page_id())) {
				// Notion에서 해당 브랜드 데이터가 없다면 삭제
				postRepository.delete(existingPost);
				// post_tags 테이블에서 해당 Post와 관련된 데이터도 삭제 (cascade 옵션)
			}
		}


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

				// DB에 저장된 마케팅 포스트 데이터 가져오기
				Post post = postRepository.findPostByNotion_page_id(pageId);

				// 새 포스트 생성
				if (post == null) {
					post = new Post();
					post.setNotion_page_id(pageId);
				}

				// 포스트 정보 업데이트
				post.setNotion_page_created_time(createdTime);
				post.setNotion_page_last_edited_time(lastEditedTime);
				post.setTitle(title);
				post.setSubtitle(subtitle);
				post.setDescription(description);
				post.setCover_url(coverUrl);
				post.setNotion_page_url(notionUrl);

				// 포스트와 연관된 브랜드 매핑
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

				postRepository.save(post);

				// Extract properties information
				JsonNode propertiesNode = pageMetadata.get("properties");


				// 태그 추출 후 저장
				if (propertiesNode != null && propertiesNode.isObject()) {
					// Iterate over the field names and print them
					Post finalPost = post;

					List<PostTag> newPostTags = new ArrayList<>(); // notion에서 새롭게 가져온 포스트 태그들

					propertiesNode.fieldNames().forEachRemaining(fieldName -> {

						if (fieldName.equals("기업") || fieldName.equals("마케팅_종류") || fieldName.equals("산업") || fieldName.equals("타겟층")) {
							JsonNode multiSelect = propertiesNode.get(fieldName).get("multi_select");

							if (multiSelect != null && multiSelect.isArray()) {
								for (JsonNode select : multiSelect) {
									String tag = select.get("name").asText();

									// 해당 finalPost, fieldName, tag 가 DB에 이미 존재하는지 확인
									PostTag postTag = postTagRepository.findPostTagByPostAndPostTagTypeAndTag(finalPost, PostTagType.valueOf(fieldName), tag);


									// postTag 가 DB에 존재하지 않는 경우는 새로 생성
									if (postTag == null) {
										postTag = PostTag.builder()
												.post(finalPost)
												.postTagType(PostTagType.valueOf(fieldName))
												.tag(tag)
												.build();
										postTagRepository.save(postTag);
									}

									newPostTags.add(postTag);

									// 또는
									// brand.getBrandTags().add(brandTag);

								}
							}
						}
					});


					// newPostTags 에 없는 기존 DB postTag 삭제

					List<PostTag> existingPostTags = postTagRepository.findAllByPost(finalPost); // 기존 DB brandTag

					existingPostTags.removeAll(newPostTags);
					postTagRepository.deleteAll(existingPostTags);

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

	public List<String> getHomeCuratingPageIds() {
		JsonNode response = webClient.post().uri("/databases/"+HOME_DB_ID+"/query").body(BodyInserters.empty())
				.retrieve()
				.bodyToMono(JsonNode.class)
				.block();

		if (response != null && response.has("results") && response.get("results").isArray()) {
			List<String> ids = new ArrayList<>();

			for (JsonNode resultNode : response.get("results")) {
				ids.add(resultNode.get("id").asText());
			}

			return ids;
		} else {
			return Collections.emptyList();
		}
	}

	public List<Map<String, Object>> getHomeCuratingPageContents(List<String> HomeCuratingPageIds){

		List<Map<String, Object>> dataList = new ArrayList<>();

		for (String HomeCuratingPageId : HomeCuratingPageIds) {
			try {
				// fetch page metadata
				JsonNode pageMetadata = fetchPageMetadata(HomeCuratingPageId);

				// Extract properties information
				JsonNode propertiesNode = pageMetadata.get("properties");

				// 태그 추출 후 저장
				if (propertiesNode != null && propertiesNode.isObject()) {

					List<Post> relatedPosts = new ArrayList<>();

					propertiesNode.fieldNames().forEachRemaining(fieldName -> {
						if (fieldName.equals("마케팅")) {
							JsonNode relation = propertiesNode.get(fieldName).get("relation");

							if (relation != null && relation.isArray()) {
								for (JsonNode r_item : relation) {
									String post_id = r_item.get("id").asText();

									// 해당 post_id로 post 가져오기
									Post post = postRepository.findPostByNotion_page_id(post_id);
									relatedPosts.add(post);
								}
							}
						}

					});

					// 큐레이팅 타이틀 추출
					String title = propertiesNode.get("이름").get("title").get(0).get("text").get("content").asText();

					Map<String, Object> data = new HashMap<>();
					data.put("title", title);
					data.put("relatedPosts", relatedPosts);
					dataList.add(data);

				} else {
					log.error("propertiesNode is null or not an object");
				}

			} catch (Exception e) {
				log.error("Error while fetching page metadata for pageId: {}", HomeCuratingPageId, e); // 스택트레이스도 같이 출력
				throw e;
			}
		}

		return dataList;

	}


}