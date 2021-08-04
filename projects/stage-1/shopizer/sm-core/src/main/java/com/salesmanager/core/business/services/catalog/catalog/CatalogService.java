package com.salesmanager.core.business.services.catalog.catalog;

import org.springframework.data.domain.Page;

import com.salesmanager.core.business.exception.ServiceException;
import com.salesmanager.core.business.services.common.generic.SalesManagerEntityService;
import com.salesmanager.core.model.catalog.catalog.Catalog;
import com.salesmanager.core.model.merchant.MerchantStore;
import com.salesmanager.core.model.reference.language.Language;

import java.util.Optional;

public interface CatalogService extends SalesManagerEntityService<Long, Catalog> {
	
	
	/**
	 * Creates a new Catalog
	 * @param store
	 * @return Catalog
	 * @throws ServiceException
	 */
	Catalog saveOrUpdate(Catalog catalog, MerchantStore store);

	Optional<Catalog> getById(Long catalogId, MerchantStore store);

	Optional<Catalog> getByCode(String code, MerchantStore store);
	
	/**
	 * Get a list of Catalog associated with a MarketPlace
	 * @param marketPlace
	 * @return List<Catalog>
	 * @throws ServiceException
	 */
	Page<Catalog> getCatalogs(MerchantStore store, Language language, String name, int page, int count);
	
	/**
	 * Delete a Catalog and related objects
	 */
	void delete(Catalog catalog) throws ServiceException;
	
	boolean existByCode(String code, MerchantStore store);

}
