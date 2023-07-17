package com.paypal.infrastructure.itemlinks.model;

/**
 * Represents a reference for an item in an external system. The item is identified by its
 * Id and its Type.
 *
 * @param <T> Enum with all possible values for item types.
 */
public interface ItemLinkLocator<T> {

	/**
	 * Returns the id of the item.
	 * @return an Id.
	 */
	String getId();

	/**
	 * Returns the type of the item.
	 * @return an enum value with the type.
	 */
	T getType();

	/**
	 * Returns the system where the item is stored.
	 * @return an enum value with the system.
	 */
	ItemLinkExternalSystem getSystem();

}
