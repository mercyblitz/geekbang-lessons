package com.salesmanager.shop.store.controller.product.facade;

import org.springframework.web.multipart.MultipartFile;

import com.salesmanager.core.model.merchant.MerchantStore;
import com.salesmanager.core.model.reference.language.Language;
import com.salesmanager.shop.model.catalog.product.attribute.PersistableProductAttribute;
import com.salesmanager.shop.model.catalog.product.attribute.api.PersistableProductOptionEntity;
import com.salesmanager.shop.model.catalog.product.attribute.api.PersistableProductOptionValueEntity;
import com.salesmanager.shop.model.catalog.product.attribute.api.ReadableProductAttributeEntity;
import com.salesmanager.shop.model.catalog.product.attribute.api.ReadableProductAttributeList;
import com.salesmanager.shop.model.catalog.product.attribute.api.ReadableProductOptionEntity;
import com.salesmanager.shop.model.catalog.product.attribute.api.ReadableProductOptionList;
import com.salesmanager.shop.model.catalog.product.attribute.api.ReadableProductOptionValueEntity;
import com.salesmanager.shop.model.catalog.product.attribute.api.ReadableProductOptionValueList;


/*
 * Attributes, Options and Options values management independently from Product
 */
public interface ProductOptionFacade {
  
  ReadableProductOptionEntity getOption(Long optionId, MerchantStore store, Language language);

  ReadableProductOptionValueEntity getOptionValue(Long optionValueId, MerchantStore store, Language language);

  ReadableProductOptionEntity saveOption(PersistableProductOptionEntity option, MerchantStore store, Language language);
  
  ReadableProductOptionValueEntity saveOptionValue(PersistableProductOptionValueEntity optionValue, MerchantStore store, Language language);

  void addOptionValueImage(MultipartFile image, Long optionValueId, MerchantStore store, Language language);
  
  void removeOptionValueImage(Long optionValueId, MerchantStore store, Language language);
  
  boolean optionExists(String code, MerchantStore store);
  
  boolean optionValueExists(String code, MerchantStore store);
  
  void deleteOption(Long optionId, MerchantStore store);
  
  void deleteOptionValue(Long optionValueId, MerchantStore store);
  
  ReadableProductOptionList options(MerchantStore store, Language language, String name, int page, int count);
  
  ReadableProductOptionValueList optionValues(MerchantStore store, Language language, String name, int page, int count);

  ReadableProductAttributeEntity saveAttribute(Long productId, PersistableProductAttribute attribute, MerchantStore store, Language language);
 
  ReadableProductAttributeEntity getAttribute(Long productId, Long attributeId, MerchantStore store, Language language);
  
  ReadableProductAttributeList getAttributesList(Long productId, MerchantStore store, Language language);
  
  void deleteAttribute(Long productId, Long attributeId, MerchantStore store);
  
}