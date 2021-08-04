package com.salesmanager.core.business.modules.cms.product;

import com.salesmanager.core.business.exception.ServiceException;
import com.salesmanager.core.business.modules.cms.common.ImageRemove;
import com.salesmanager.core.model.catalog.product.Product;
import com.salesmanager.core.model.catalog.product.image.ProductImage;


public interface ProductImageRemove extends ImageRemove {

  void removeProductImage(ProductImage productImage) throws ServiceException;

  void removeProductImages(Product product) throws ServiceException;

}
