/**
 * (c) 2015 dmulloy2
 */
package net.dmulloy2.kitpvp.type;

import lombok.AllArgsConstructor;
import lombok.Getter;
import net.dmulloy2.types.IPermission;

/**
 * @author dmulloy2
 */

@Getter
@AllArgsConstructor
public enum Permission implements IPermission {
	CREATE_KIT("create.kit"),
	SET_LOBBY("set.lobby"),
	RELOAD("reload"),
	;

	private final String node;
}
