package com.salesmanager.shop.store.api.v1.content;

import java.io.IOException;
import java.util.List;
import java.util.Optional;

import javax.inject.Inject;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.validation.Valid;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import com.salesmanager.core.model.content.ContentType;
import com.salesmanager.core.model.merchant.MerchantStore;
import com.salesmanager.core.model.reference.language.Language;
import com.salesmanager.shop.model.content.ContentFile;
import com.salesmanager.shop.model.content.ContentFolder;
import com.salesmanager.shop.model.content.ContentName;
import com.salesmanager.shop.model.content.PersistableContentEntity;
import com.salesmanager.shop.model.content.ReadableContentEntity;
import com.salesmanager.shop.model.content.ReadableContentFull;
import com.salesmanager.shop.model.content.box.PersistableContentBox;
import com.salesmanager.shop.model.content.box.ReadableContentBox;
import com.salesmanager.shop.model.content.page.PersistableContentPage;
import com.salesmanager.shop.model.content.page.ReadableContentPage;
import com.salesmanager.shop.model.entity.Entity;
import com.salesmanager.shop.model.entity.EntityExists;
import com.salesmanager.shop.model.entity.ReadableEntityList;
import com.salesmanager.shop.store.api.exception.ServiceRuntimeException;
import com.salesmanager.shop.store.controller.content.facade.ContentFacade;
import com.salesmanager.shop.utils.ImageFilePath;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.SwaggerDefinition;
import io.swagger.annotations.Tag;
import springfox.documentation.annotations.ApiIgnore;

@RestController
@RequestMapping(value = "/api/v1")
@Api(tags = { "Content management resource (Content Management Api)" })
@SwaggerDefinition(tags = {
		@Tag(name = "Content management resource", description = "Add pages, content boxes, manage images and files") })
public class ContentApi {

	private static final Logger LOGGER = LoggerFactory.getLogger(ContentApi.class);

	private static final String DEFAULT_PATH = "/";
	
	private final static String BOX = "BOX";
	private final static String PAGE = "PAGE";

	@Inject
	private ContentFacade contentFacade;

	@Inject
	@Qualifier("img")
	private ImageFilePath imageUtils;

	/**
	 * List content pages
	 * @param merchantStore
	 * @param language
	 * @param page
	 * @param count
	 * @return
	 */
	@GetMapping(value = {"/private/content/pages", "/content/pages"}, produces = MediaType.APPLICATION_JSON_VALUE)
	@ApiOperation(httpMethod = "GET", value = "Get page names created for a given MerchantStore", notes = "", produces = "application/json", response = List.class)
	@ApiImplicitParams({ @ApiImplicitParam(name = "store", dataType = "String", defaultValue = "DEFAULT"),
			@ApiImplicitParam(name = "lang", dataType = "String", defaultValue = "en") })
	public ReadableEntityList<ReadableContentPage> pages(
			@ApiIgnore MerchantStore merchantStore,
			@ApiIgnore Language language,
			int page,
			int count) {
		return contentFacade
				.getContentPages(merchantStore, language, page, count);
	}

	@Deprecated
	@GetMapping(value = "/content/summary", produces = MediaType.APPLICATION_JSON_VALUE)
	@ApiOperation(httpMethod = "GET", value = "Get pages summary created for a given MerchantStore. Content summary is a content bux having code summary.", notes = "", produces = "application/json", response = List.class)
	@ApiImplicitParams({ @ApiImplicitParam(name = "store", dataType = "String", defaultValue = "DEFAULT"),
			@ApiImplicitParam(name = "lang", dataType = "String", defaultValue = "en") })
	public List<ReadableContentBox> pagesSummary(
			@ApiIgnore MerchantStore merchantStore, 
			@ApiIgnore Language language) {
		//return contentFacade.getContentBoxes(ContentType.BOX, "summary_", merchantStore, language);
		return null;
	}

	/**
	 * List all boxes
	 * 
	 * @param merchantStore
	 * @param language
	 * @return
	 */
	@GetMapping(value = {"/content/boxes","/private/content/boxes"}, produces = MediaType.APPLICATION_JSON_VALUE)
	@ApiOperation(httpMethod = "GET", value = "Get boxes for a given MerchantStore", notes = "", produces = "application/json", response = List.class)
	@ApiImplicitParams({ 
		@ApiImplicitParam(name = "store", dataType = "String", defaultValue = "DEFAULT"),
		@ApiImplicitParam(name = "lang", dataType = "String", defaultValue = "en") })
	public ReadableEntityList<ReadableContentBox> boxes(
			@ApiIgnore MerchantStore merchantStore,
			@ApiIgnore Language language,
			int page,
			int count
			) {
		return contentFacade.getContentBoxes(ContentType.BOX, merchantStore, language, page, count);
	}

	/**
	 * List specific content box
	 * @param code
	 * @param merchantStore
	 * @param language
	 * @return
	 */
	@GetMapping(value = "/content/pages/{code}", produces = MediaType.APPLICATION_JSON_VALUE)
	@ApiOperation(httpMethod = "GET", value = "Get page content by code for a given MerchantStore", notes = "", produces = "application/json", response = ReadableContentPage.class)
	@ApiImplicitParams({ @ApiImplicitParam(name = "store", dataType = "String", defaultValue = "DEFAULT"),
			@ApiImplicitParam(name = "lang", dataType = "String", defaultValue = "en") })
	public ReadableContentPage page(@PathVariable("code") String code, @ApiIgnore MerchantStore merchantStore,
			@ApiIgnore Language language) {

		return contentFacade.getContentPage(code, merchantStore, language);

	}

	/**
	 * Get content page by name
	 * @param name
	 * @param merchantStore
	 * @param language
	 * @return
	 */
	@GetMapping(value = "/content/pages/name/{name}", produces = MediaType.APPLICATION_JSON_VALUE)
	@ApiOperation(httpMethod = "GET", value = "Get page content by code for a given MerchantStore", notes = "", produces = "application/json", response = ReadableContentPage.class)
	@ApiImplicitParams({ @ApiImplicitParam(name = "store", dataType = "String", defaultValue = "DEFAULT"),
			@ApiImplicitParam(name = "lang", dataType = "String", defaultValue = "en") })
	public ReadableContentPage pageByName(@PathVariable("name") String name, @ApiIgnore MerchantStore merchantStore,
			@ApiIgnore Language language) {

		return contentFacade.getContentPageByName(name, merchantStore, language);

	}
	
	/**
	 * Create content box
	 * 
	 * @param page
	 * @param merchantStore
	 * @param language
	 * @param pageCode
	 */
	@PostMapping(value = "/private/content/box")
	@ResponseStatus(HttpStatus.CREATED)
	@ApiOperation(httpMethod = "POST", value = "Create content box", notes = "", response = Entity.class)
	@ApiImplicitParams({ 
		@ApiImplicitParam(name = "store", dataType = "String", defaultValue = "DEFAULT"),
		@ApiImplicitParam(name = "lang", dataType = "String", defaultValue = "en") })
	public Entity createBox(
			@RequestBody @Valid PersistableContentBox box, 
			@ApiIgnore MerchantStore merchantStore,
			@ApiIgnore Language language) {

		Long id = contentFacade.saveContentBox(box, merchantStore, language);
		Entity entity = new Entity();
		entity.setId(id);
		return entity;
	}
	
	@GetMapping(value = "/private/content/box/{code}/exists")
	@ResponseStatus(HttpStatus.OK)
	@ApiOperation(httpMethod = "GET", value = "Check unique content box", notes = "", response = EntityExists.class)
	@ApiImplicitParams({ 
		@ApiImplicitParam(name = "store", dataType = "String", defaultValue = "DEFAULT"),
		@ApiImplicitParam(name = "lang", dataType = "String", defaultValue = "en") })
	public EntityExists boxExists(
			@PathVariable String code, 
			@ApiIgnore MerchantStore merchantStore,
			@ApiIgnore Language language) {

		boolean exists = contentFacade.codeExist(code, BOX, merchantStore);
		EntityExists entity = new EntityExists(exists);
		return entity;
	}
	
	@GetMapping(value = "/private/content/page/{code}/exists")
	@ResponseStatus(HttpStatus.OK)
	@ApiOperation(httpMethod = "GET", value = "Check unique content page", notes = "", response = EntityExists.class)
	@ApiImplicitParams({ 
		@ApiImplicitParam(name = "store", dataType = "String", defaultValue = "DEFAULT"),
		@ApiImplicitParam(name = "lang", dataType = "String", defaultValue = "en") })
	public EntityExists pageExists(
			@PathVariable String code, 
			@ApiIgnore MerchantStore merchantStore,
			@ApiIgnore Language language) {

		boolean exists = contentFacade.codeExist(code, PAGE, merchantStore);
		EntityExists entity = new EntityExists(exists);
		return entity;
	}
	
	/**
	 * Create content page
	 * @param page
	 * @param merchantStore
	 * @param language
	 */
	@PostMapping(value = "/private/content/page")
	@ResponseStatus(HttpStatus.CREATED)
	@ApiOperation(httpMethod = "POST", value = "Create content page", notes = "", response = Entity.class)
	@ApiImplicitParams({ 
		@ApiImplicitParam(name = "store", dataType = "String", defaultValue = "DEFAULT"),
		@ApiImplicitParam(name = "lang", dataType = "String", defaultValue = "en") })
	public Entity createPage(
			@RequestBody @Valid PersistableContentPage page, 
			@ApiIgnore MerchantStore merchantStore,
			@ApiIgnore Language language) {

		Long id = contentFacade.saveContentPage(page, merchantStore, language);
		Entity entity = new Entity();
		entity.setId(id);
		return entity;
	}
	
	
	/**
	 * Delete content page
	 * @param id
	 * @param merchantStore
	 * @param language
	 */
	@DeleteMapping(value = "/private/content/page/{id}")
	@ResponseStatus(HttpStatus.OK)
	@ApiOperation(httpMethod = "DELETE", value = "Delete content page", notes = "", response = Void.class)
	@ApiImplicitParams({ 
		@ApiImplicitParam(name = "store", dataType = "String", defaultValue = "DEFAULT"),
		@ApiImplicitParam(name = "lang", dataType = "String", defaultValue = "en") })
	public void deletePage(
			@PathVariable Long id,
			@ApiIgnore MerchantStore merchantStore,
			@ApiIgnore Language language) {

		contentFacade.delete(merchantStore, id);

	}
	
	/**
	 * Delete content box
	 * @param id
	 * @param merchantStore
	 * @param language
	 */
	@DeleteMapping(value = "/private/content/box/{id}")
	@ResponseStatus(HttpStatus.OK)
	@ApiOperation(httpMethod = "DELETE", value = "Delete content box", notes = "", response = Void.class)
	@ApiImplicitParams({ 
		@ApiImplicitParam(name = "store", dataType = "String", defaultValue = "DEFAULT"),
		@ApiImplicitParam(name = "lang", dataType = "String", defaultValue = "en") })
	public void deleteBox(
			@PathVariable Long id,
			@ApiIgnore MerchantStore merchantStore,
			@ApiIgnore Language language) {

		contentFacade.delete(merchantStore, id);

	}
	
	@PutMapping(value = "/private/content/page/{id}")
	@ResponseStatus(HttpStatus.OK)
	@ApiOperation(httpMethod = "PUT", value = "Update content page", notes = "", response = Void.class)
	@ApiImplicitParams({ 
		@ApiImplicitParam(name = "store", dataType = "String", defaultValue = "DEFAULT"),
		@ApiImplicitParam(name = "lang", dataType = "String", defaultValue = "en") })
	public void updatePage(
			@RequestBody @Valid PersistableContentPage page,
			@PathVariable Long id,
			@ApiIgnore MerchantStore merchantStore,
			@ApiIgnore Language language) {

		contentFacade.updateContentPage(id, page, merchantStore, language);
	}
	
	@PutMapping(value = "/private/content/box/{id}")
	@ResponseStatus(HttpStatus.OK)
	@ApiOperation(httpMethod = "PUT", value = "Update content box", notes = "", response = Void.class)
	@ApiImplicitParams({ 
		@ApiImplicitParam(name = "store", dataType = "String", defaultValue = "DEFAULT"),
		@ApiImplicitParam(name = "lang", dataType = "String", defaultValue = "en") })
	public void updateBox(
			@RequestBody @Valid PersistableContentBox box,
			@PathVariable Long id,
			@ApiIgnore MerchantStore merchantStore,
			@ApiIgnore Language language) {

		contentFacade.updateContentBox(id, box, merchantStore, language);
	}

	@Deprecated
	@GetMapping(value = "/private/content/any/{code}", produces = MediaType.APPLICATION_JSON_VALUE)
	@ApiOperation(httpMethod = "GET", value = "Get page content by code for a given MerchantStore", notes = "", produces = "application/json", response = ReadableContentPage.class)
	@ApiImplicitParams({ @ApiImplicitParam(name = "store", dataType = "String", defaultValue = "DEFAULT"),
			@ApiImplicitParam(name = "lang", dataType = "String", defaultValue = "en") })
	public ReadableContentFull content(@PathVariable("code") String code, @ApiIgnore MerchantStore merchantStore,
			@ApiIgnore Language language) {

		return contentFacade.getContent(code, merchantStore, language);

	}

	@Deprecated
	@GetMapping(value = "/private/contents/any", produces = MediaType.APPLICATION_JSON_VALUE)
	@ApiOperation(httpMethod = "GET", value = "Get contents (page and box) for a given MerchantStore", notes = "", produces = "application/json", response = ReadableContentPage.class)
	@ApiImplicitParams({ @ApiImplicitParam(name = "store", dataType = "String", defaultValue = "DEFAULT"),
			@ApiImplicitParam(name = "lang", dataType = "String", defaultValue = "en") })
	public List<ReadableContentEntity> contents(@ApiIgnore MerchantStore merchantStore, @ApiIgnore Language language) {

		Optional<String> op = Optional.empty();
		return contentFacade.getContents(op, merchantStore, language);

	}
	
	
	@GetMapping(value = "/private/content/boxes/{code}", produces = MediaType.APPLICATION_JSON_VALUE)
	@ApiOperation(httpMethod = "GET", value = "Manage box content by code for a code and a given MerchantStore", notes = "", produces = "application/json", response = List.class)
	@ApiImplicitParams({ @ApiImplicitParam(name = "store", dataType = "String", defaultValue = "DEFAULT"),
			@ApiImplicitParam(name = "lang", dataType = "String", defaultValue = "en") })
	public ReadableContentBox manageBoxByCode(@PathVariable("code") String code, @ApiIgnore MerchantStore merchantStore,
			@ApiIgnore Language language) {
		return contentFacade.getContentBox(code, merchantStore, language);
	}

	@GetMapping(value = "/content/boxes/{code}", produces = MediaType.APPLICATION_JSON_VALUE)
	@ApiOperation(httpMethod = "GET", value = "Get box content by code for a code and a given MerchantStore", notes = "", produces = "application/json", response = List.class)
	@ApiImplicitParams({ @ApiImplicitParam(name = "store", dataType = "String", defaultValue = "DEFAULT"),
			@ApiImplicitParam(name = "lang", dataType = "String", defaultValue = "en") })
	public ReadableContentBox getBoxByCode(@PathVariable("code") String code, @ApiIgnore MerchantStore merchantStore,
			@ApiIgnore Language language) {
		return contentFacade.getContentBox(code, merchantStore, language);
	}





	/**
	 * 
	 * @param parent
	 * @param folder
	 * @param merchantStore
	 * @param language
	 */
	@DeleteMapping(value = "/content/folder", produces = MediaType.APPLICATION_JSON_VALUE)
	@ResponseStatus(HttpStatus.CREATED)
	@ApiImplicitParams({ @ApiImplicitParam(name = "store", dataType = "String", defaultValue = "DEFAULT"),
			@ApiImplicitParam(name = "lang", dataType = "String", defaultValue = "en") })
	public void addFolder(@RequestParam String parent, @RequestParam String folder,
			@ApiIgnore MerchantStore merchantStore, @ApiIgnore Language language) {

	}

	/**
	 * @param code
	 * @param path
	 * @param request
	 * @param response
	 * @return
	 * @throws Exception
	 */
	@GetMapping(value = "/content/images", produces = MediaType.APPLICATION_JSON_VALUE)
	@ApiOperation(httpMethod = "GET", value = "Get store content images", notes = "", response = ContentFolder.class)
	@ApiImplicitParams({ @ApiImplicitParam(name = "store", dataType = "String", defaultValue = "DEFAULT"),
			@ApiImplicitParam(name = "lang", dataType = "String", defaultValue = "en") })
	public ContentFolder images(@ApiIgnore MerchantStore merchantStore, @ApiIgnore Language language,
			@RequestParam(value = "path", required = false) String path, HttpServletRequest request,
			HttpServletResponse response) throws Exception {

		//String decodedPath = decodeContentPath(path);
		ContentFolder folder = contentFacade.getContentFolder(path, merchantStore);
		return folder;
	}



	/**
	 * Need type, name and entity
	 *
	 * @param file
	 */
	@PostMapping(value = "/private/file")
	@ResponseStatus(HttpStatus.CREATED)
	@ApiImplicitParams({ @ApiImplicitParam(name = "store", dataType = "String", defaultValue = "DEFAULT"),
			@ApiImplicitParam(name = "lang", dataType = "String", defaultValue = "en") })
	public void upload(@RequestParam("file") MultipartFile file, @ApiIgnore MerchantStore merchantStore,
			@ApiIgnore Language language) {

		ContentFile f = new ContentFile();
		f.setContentType(file.getContentType());
		f.setName(file.getOriginalFilename());
		try {
			f.setFile(file.getBytes());
		} catch (IOException e) {
			throw new ServiceRuntimeException("Error while getting file bytes");
		}

		contentFacade.addContentFile(f, merchantStore.getCode());

	}

	@PostMapping(value = "/private/files", consumes = { MediaType.MULTIPART_FORM_DATA_VALUE })
	@ResponseStatus(HttpStatus.CREATED)
	@ApiImplicitParams({
			// @ApiImplicitParam(name = "file[]", value = "File stream object",
			// required = true,dataType = "MultipartFile",allowMultiple = true),
			@ApiImplicitParam(name = "store", dataType = "String", defaultValue = "DEFAULT"),
			@ApiImplicitParam(name = "lang", dataType = "String", defaultValue = "en") })
	public void uploadMultipleFiles(@RequestParam(value = "file[]", required = true) MultipartFile[] files,
			@ApiIgnore MerchantStore merchantStore, @ApiIgnore Language language) {

		for (MultipartFile f : files) {
			ContentFile cf = new ContentFile();
			cf.setContentType(f.getContentType());
			cf.setName(f.getName());
			try {
				cf.setFile(f.getBytes());
				contentFacade.addContentFile(cf, merchantStore.getCode());
			} catch (IOException e) {
				throw new ServiceRuntimeException("Error while getting file bytes");
			}
		}

	}

	
	@Deprecated
	@PutMapping(value = "/private/content/{id}")
	@ResponseStatus(HttpStatus.OK)
	@ApiOperation(httpMethod = "PUT", value = "Update content page", notes = "Updates a content page",

			response = Void.class)
	@ApiImplicitParams({ @ApiImplicitParam(name = "store", dataType = "String", defaultValue = "DEFAULT"),
			@ApiImplicitParam(name = "lang", dataType = "String", defaultValue = "en") })

	public void updatePage(@PathVariable Long id, @RequestBody @Valid PersistableContentEntity page,
			@ApiIgnore MerchantStore merchantStore, @ApiIgnore Language language) {
		page.setId(id);
		//contentFacade.saveContentPage(page, merchantStore, language);
	}

	/**
	 * Deletes a content from CMS
	 *
	 * @param name
	 */
	@Deprecated
	@DeleteMapping(value = "/private/content/{id}")
	@ApiOperation(httpMethod = "DELETE", value = "Deletes a content from CMS", notes = "Delete a content box or page", response = Void.class)
	@ApiImplicitParams({ @ApiImplicitParam(name = "store", dataType = "String", defaultValue = "DEFAULT") })
	public void deleteContent(Long id, @ApiIgnore MerchantStore merchantStore) {
		contentFacade.delete(merchantStore, id);
	}

	/*  *//**
			 * Deletes a content from CMS
			 *
			 * @param name
			 *//*
			 * @DeleteMapping(value = "/private/content/page/{id}")
			 * 
			 * @ApiOperation(httpMethod = "DELETE", value =
			 * "Deletes a file from CMS", notes = "Delete a file from server",
			 * response = Void.class)
			 * 
			 * @ApiImplicitParams({
			 * 
			 * @ApiImplicitParam(name = "store", dataType = "String",
			 * defaultValue = "DEFAULT")}) public void deleteFile( Long id,
			 * 
			 * @ApiIgnore MerchantStore merchantStore) {
			 * contentFacade.deletePage(merchantStore, id); }
			 */

	/**
	 * Deletes a file from CMS
	 *
	 * @param name
	 */
	@DeleteMapping(value = "/private/content/")
	@ApiOperation(httpMethod = "DELETE", value = "Deletes a file from CMS", notes = "Delete a file from server", response = Void.class)
	@ApiImplicitParams({ @ApiImplicitParam(name = "store", dataType = "String", defaultValue = "DEFAULT"),
			@ApiImplicitParam(name = "lang", dataType = "String", defaultValue = "en") })
	public void deleteFile(@Valid ContentName name, @ApiIgnore MerchantStore merchantStore,
			@ApiIgnore Language language) {
		contentFacade.delete(merchantStore, name.getName(), name.getContentType());
	}


}
