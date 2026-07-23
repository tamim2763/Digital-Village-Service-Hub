package com.digitalvillage.model;

import lombok.Builder;
import lombok.Value;

/**
 * Immutable view model for the public service highlight cards.
 * Uses message keys instead of literal text so Thymeleaf can resolve
 * them via #{...} expressions for i18n support.
 */
@Value
@Builder
public class ServiceCard {

	String iconClass;
	String titleKey;
	String descriptionKey;
}
