package com.fujixerox.aus.repository.util;

import com.fujixerox.aus.repository.AbstractComponentTest;
import org.junit.Test;
import org.junit.experimental.categories.Category;

import static org.junit.Assert.assertEquals;

public class RepositoryPropertiesComponentTest implements AbstractComponentTest {
	
	@Test
    @Category(AbstractComponentTest.class)
    public void shouldReadProperties() {
		assertEquals("/Vouchers", RepositoryProperties.repository_image_location);
		assertEquals("/", RepositoryProperties.repository_image_path_sep);
		assertEquals("fxa_voucher_acl", RepositoryProperties.doc_acl_name);
		assertEquals("dm_dbo", RepositoryProperties.doc_acl_domain);
		assertEquals("fxa_voucher_folder_acl", RepositoryProperties.folder_acl_name);
		assertEquals("dm_dbo", RepositoryProperties.folder_acl_domain);
		assertEquals("NAB", RepositoryProperties.repository_name);
		assertEquals("fxa_voucher", RepositoryProperties.doc_object_type);
		assertEquals("fxa_listing", RepositoryProperties.doc_listing_type);
		assertEquals("fxa_voucher_transfer", RepositoryProperties.doc_voucher_transfer_type);
	}
}
