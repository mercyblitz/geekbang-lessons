package com.salesmanager.shop.mapper.catalog;

import org.apache.commons.lang3.Validate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.salesmanager.core.model.catalog.catalog.Catalog;
import com.salesmanager.core.model.catalog.catalog.CatalogCategoryEntry;
import com.salesmanager.core.model.catalog.category.Category;
import com.salesmanager.core.model.merchant.MerchantStore;
import com.salesmanager.core.model.reference.language.Language;
import com.salesmanager.shop.mapper.Mapper;
import com.salesmanager.shop.model.catalog.catalog.PersistableCatalogCategoryEntry;
import com.salesmanager.shop.store.api.exception.ConversionRuntimeException;
import com.salesmanager.shop.store.controller.catalog.facade.CatalogFacade;
import com.salesmanager.shop.store.controller.category.facade.CategoryFacade;

@Component
public class PersistableCatalogCategoryEntryMapper implements Mapper<PersistableCatalogCategoryEntry, CatalogCategoryEntry> {

	
	@Autowired
	private CategoryFacade categoryFacade;
	
	@Autowired
	private CatalogFacade catalogFacade;
	
	
	@Override
	public CatalogCategoryEntry convert(PersistableCatalogCategoryEntry source, MerchantStore store, Language language) {
		CatalogCategoryEntry destination = new CatalogCategoryEntry();
		return this.merge(source, destination, store, language);
	}

	@Override
	public CatalogCategoryEntry merge(PersistableCatalogCategoryEntry source, CatalogCategoryEntry destination, MerchantStore store,
									  Language language) {
		Validate.notNull(source, "CatalogEntry must not be null");
		Validate.notNull(store, "MerchantStore must not be null");
		Validate.notNull(source.getProductCode(), "ProductCode must not be null");
		Validate.notNull(source.getCategoryCode(), "CategoryCode must not be null");
		Validate.notNull(source.getCatalog(), "Catalog must not be null");
		
		
		
		if(destination == null) {
			destination = new CatalogCategoryEntry();
			
		}
		destination.setId(source.getId());
		destination.setVisible(source.isVisible());

		
		try {
			
			String catalog = source.getCatalog();
			
			Catalog catalogModel = catalogFacade.getCatalog(catalog, store);
			if(catalogModel == null) {
				throw new ConversionRuntimeException("Error while converting CatalogEntry product [" + source.getCatalog() + "] not found");
			}
			
			destination.setCatalog(catalogModel);

/*			Product productModel = productFacade.getProduct(source.getProductCode(), store);
			if(productModel == null) {
				throw new ConversionRuntimeException("Error while converting CatalogEntry product [" + source.getProductCode() + "] not found");
			}*/

			//destination.setProduct(productModel);
			
			Category categoryModel = categoryFacade.getByCode(source.getCategoryCode(), store);
			if(categoryModel == null) {
				throw new ConversionRuntimeException("Error while converting CatalogEntry category [" + source.getCategoryCode() + "] not found");
			}
			
			destination.setCategory(categoryModel);
			
		} catch (Exception e) {
			throw new ConversionRuntimeException("Error while converting CatalogEntry", e);
		}
		
		return destination;
	}

}
