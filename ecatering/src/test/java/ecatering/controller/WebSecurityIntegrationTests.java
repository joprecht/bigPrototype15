package ecatering.controller;

import static org.hamcrest.CoreMatchers.*;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import org.junit.Test;

import ecatering.AbstractWebIntegrationTests;

public class WebSecurityIntegrationTests extends AbstractWebIntegrationTests{


		@Test
		public void redirectsToLoginPageForSecuredResource() throws Exception {

			mvc.perform(get("/kitchen")).//
					andExpect(status().isFound()).//
					andExpect(header().string("Location", endsWith("/login")));
		}


		@Test
		public void returnsModelAndViewForSecuredUriAfterAuthentication() throws Exception {

			mvc.perform(get("/kitchen").with(user("koch").roles("KITCHEN"))).//
					andExpect(status().isOk()).//
					andExpect(view().name("kitchen"));//.
					//andExpect(model().attributeExists("ordersCompleted"));
		}
	}

	

