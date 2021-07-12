package com.salesmanager.core.business.services.tax;

import java.util.List;

import javax.inject.Inject;

import org.jsoup.helper.Validate;
import org.springframework.stereotype.Service;

import com.salesmanager.core.business.exception.ServiceException;
import com.salesmanager.core.business.repositories.tax.TaxClassRepository;
import com.salesmanager.core.business.services.common.generic.SalesManagerEntityServiceImpl;
import com.salesmanager.core.model.merchant.MerchantStore;
import com.salesmanager.core.model.tax.taxclass.TaxClass;

@Service("taxClassService")
public class TaxClassServiceImpl extends SalesManagerEntityServiceImpl<Long, TaxClass>
		implements TaxClassService {

	private TaxClassRepository taxClassRepository;
	
	@Inject
	public TaxClassServiceImpl(TaxClassRepository taxClassRepository) {
		super(taxClassRepository);
		
		this.taxClassRepository = taxClassRepository;
	}
	
	@Override
	public List<TaxClass> listByStore(MerchantStore store) throws ServiceException {	
		return taxClassRepository.findByStore(store.getId());
	}
	
	@Override
	public TaxClass getByCode(String code) throws ServiceException {
		return taxClassRepository.findByCode(code);
	}
	
	@Override
	public TaxClass getByCode(String code, MerchantStore store) throws ServiceException {
		return taxClassRepository.findByStoreAndCode(store.getId(), code);
	}
	
	@Override
	public void delete(TaxClass taxClass) throws ServiceException {
		
		TaxClass t = getById(taxClass.getId());
		super.delete(t);
		
	}
	
	@Override
	public TaxClass getById(Long id) {
		return taxClassRepository.getOne(id);
	}

	@Override
	public boolean exists(String code, MerchantStore store) throws ServiceException {
		Validate.notNull(code, "TaxClass code cannot be empty");
		Validate.notNull(store, "MerchantStore cannot be null");
		
		return taxClassRepository.findByStoreAndCode(store.getId(), code) != null;

	}
	
	@Override
	public TaxClass saveOrUpdate(TaxClass taxClass) throws ServiceException {
		if(taxClass.getId()!=null && taxClass.getId() > 0) {
			this.update(taxClass);
		} else {
			taxClass = super.saveAndFlush(taxClass);
		}
		return taxClass;
	}

	

}
