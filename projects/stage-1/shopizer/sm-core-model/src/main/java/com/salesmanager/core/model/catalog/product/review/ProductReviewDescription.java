/*
 * Licensed to csti consulting 
 * You may obtain a copy of the License at
 *
 * http://www.csticonsulting.com
 * Copyright (c) 2006-Aug 24, 2010 Consultation CS-TI inc. 
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */
package com.salesmanager.core.model.catalog.product.review;

import javax.persistence.Entity;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import javax.persistence.TableGenerator;
import javax.persistence.UniqueConstraint;

import com.salesmanager.core.constants.SchemaConstant;
import com.salesmanager.core.model.common.description.Description;
import com.salesmanager.core.model.reference.language.Language;

@Entity
@Table(name = "PRODUCT_REVIEW_DESCRIPTION", uniqueConstraints={
	@UniqueConstraint(columnNames={
		"PRODUCT_REVIEW_ID",
		"LANGUAGE_ID"
	})
})

@TableGenerator(name = "description_gen", table = "SM_SEQUENCER", pkColumnName = "SEQ_NAME", valueColumnName = "SEQ_COUNT", pkColumnValue = "product_review_description_seq", allocationSize = SchemaConstant.DESCRIPTION_ID_ALLOCATION_SIZE, initialValue = SchemaConstant.DESCRIPTION_ID_START_VALUE)
public class ProductReviewDescription extends Description {
	private static final long serialVersionUID = 1L;

	@ManyToOne(targetEntity = ProductReview.class)
	@JoinColumn(name="PRODUCT_REVIEW_ID")
	private ProductReview productReview;

	public ProductReviewDescription() {
	}

	public ProductReviewDescription(Language language, String name) {
		this.setLanguage(language);
		this.setName(name);
	}

	public ProductReview getProductReview() {
		return productReview;
	}

	public void setProductReview(ProductReview productReview) {
		this.productReview = productReview;
	}
}
