package com.digitalvillage.model;

import lombok.Builder;
import lombok.Value;

/**
 * Immutable view model for the public service highlight cards.
 */
@Value
@Builder
public class ServiceCard {

	String iconClass;
	String title;
	String description;
}
