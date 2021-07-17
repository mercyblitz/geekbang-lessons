/*
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements.  See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.salesmanager.rest.service;

import com.salesmanager.shop.model.catalog.product.ReadableProductList;

import javax.ws.rs.*;

/**
 * Product Items REST Service
 *
 * @author <a href="mailto:mercyblitz@gmail.com">Mercy</a>
 * @since 1.0.0
 */
@Path("/services")
public interface ProductItemsRestService {

    @GET
    @Path("/public/{store}/products/group/{code}")
    ReadableProductList getProductItemsByGroup(@PathParam("store") String store, @PathParam("code") String code);


    @POST
    @Path("/public/product")
    @Consumes("application/json")// Accept : application/json
    String addProducts(@BeanParam ReadableProductList productList);
}
